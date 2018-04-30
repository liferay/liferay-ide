/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.project.core.modules.fragment;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Charles Wu
 */
public class NewModuleFragmentFilesOpMethods {

	public static final Status execute(NewModuleFragmentFilesOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Copy files (this process may take several minutes)", 100);

		String projectName = op.getProjectName().content();

		IProject project = CoreUtil.getProject(projectName);

		Status retval = null;

		try {
			String hostBundleName = op.getHostOsgiBundle().content();

			IPath projectCoreLocation = ProjectCore.getDefault().getStateLocation();

			IPath tempJarDir = LiferayCore.GLOBAL_USER_DIR.append(hostBundleName);

			if (FileUtil.notExists(tempJarDir)) {
				IRuntime runtime = ServerUtil.getRuntime(op.getLiferayRuntimeName().content());

				String hostOsgiJar = hostBundleName + ".jar";

				ServerUtil.getModuleFileFrom70Server(runtime, hostOsgiJar, projectCoreLocation);

				IPath tempJarFile = projectCoreLocation.append(hostOsgiJar);

				try {
					ZipUtil.unzip(tempJarFile.toFile(), tempJarDir.toFile());
				}
				catch (IOException ioe) {
					throw new CoreException(ProjectCore.createErrorStatus(ioe));
				}
			}

			ElementList<OverrideFilePath> files = op.getOverrideFiles();

			for (OverrideFilePath file : files) {
				File fragmentFile = tempJarDir.append(file.getValue().content()).toFile();

				if (FileUtil.exists(fragmentFile)) {
					File folder = null;

					if (fragmentFile.getName().equals("portlet.properties")) {
						IPath path = project.getLocation().append("src/main/java");

						folder = path.toFile();

						FileUtil.copyFileToDir(fragmentFile, "portlet-ext.properties", folder);
					}
					else if (fragmentFile.getName().contains("default.xml")) {
						String parent = fragmentFile.getPath();

						parent = parent.replaceAll("\\\\", "/");

						String metaInfResources = "resource-actions";

						parent = parent.substring(parent.indexOf(metaInfResources) + metaInfResources.length());

						IPath resources = project.getLocation().append("src/main/resources/resource-actions");

						folder = resources.toFile();

						folder.mkdirs();

						FileUtil.copyFileToDir(fragmentFile, "default-ext.xml", folder);

						try {
							File ext = new File(
								project.getLocation().append("src/main/resources") + "/portlet-ext.properties");

							ext.createNewFile();

							String extFileContent =
								"resource.actions.configs=resource-actions/default.xml," +
									"resource-actions/default-ext.xml";

							FileUtil.writeFile(ext, extFileContent, null);
						}
						catch (Exception e) {
							throw new CoreException(ProjectCore.createErrorStatus(e));
						}
					}
					else {
						String parent = fragmentFile.getParentFile().getPath();

						parent = parent.replaceAll("\\\\", "/");

						String metaInfResources = "META-INF/resources";

						parent = parent.substring(parent.indexOf(metaInfResources) + metaInfResources.length());

						IPath resources = project.getLocation().append("src/main/resources/META-INF/resources");

						folder = resources.toFile();

						folder.mkdirs();

						if (!parent.equals("resources") && !parent.equals("")) {
							folder = resources.append(parent).toFile();

							folder.mkdirs();
						}

						FileUtil.copyFileToDir(fragmentFile, folder);
					}
				}
			}

			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			retval = Status.createOkStatus();
		}
		catch (Exception e) {
			String msg = "Error copy files.";

			ProjectCore.logError(msg, e);

			return Status.createErrorStatus(msg + " Please see Eclipse error log for more details.", e);
		}

		return retval;
	}

}