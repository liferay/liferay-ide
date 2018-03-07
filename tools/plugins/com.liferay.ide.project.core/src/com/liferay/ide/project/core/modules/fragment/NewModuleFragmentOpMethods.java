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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 * @author Charles Wu
 */
public class NewModuleFragmentOpMethods {

	public static void copyOverrideFiles(NewModuleFragmentOp op) {
		String projectName = op.getProjectName().content();

		IPath location = PathBridge.create(op.getLocation().content());

		ElementList<OverrideFilePath> files = op.getOverrideFiles();

		for (OverrideFilePath file : files) {
			File fragmentFile = new File(_hostBundleDir, file.getValue().content());

			if (FileUtil.notExists(fragmentFile)) {
				continue;
			}

			File folder = null;

			if (fragmentFile.getName().equals("portlet.properties")) {
				IPath path = location.append(projectName).append("src/main/java");

				folder = path.toFile();

				FileUtil.copyFileToDir(fragmentFile, "portlet-ext.properties", folder);
			}
			else if (fragmentFile.getName().contains("default.xml")) {
				String parent = fragmentFile.getParentFile().getPath();

				parent = parent.replaceAll("\\\\", "/");

				String metaInfResources = "resource-actions";

				parent = parent.substring(parent.indexOf(metaInfResources) + metaInfResources.length());

				IPath resources = location.append(projectName).append("src/main/resources/resource-actions");

				folder = resources.toFile();

				folder.mkdirs();

				if (!parent.equals("resource-actions") && !parent.equals("")) {
					folder = resources.append(parent).toFile();

					folder.mkdirs();
				}

				FileUtil.copyFileToDir(fragmentFile, "default-ext.xml", folder);

				try {
					File ext =
						new File(location.append(projectName).append("src/main/resources") + "/portlet-ext.properties");

					ext.createNewFile();

					String extFileContent =
						"resource.actions.configs=resource-actions/default.xml,resource-actions/default-ext.xml";

					FileUtil.writeFile(ext, extFileContent, null);
				}
				catch (Exception e) {}
			}
			else {
				String parent = fragmentFile.getParentFile().getPath();

				parent = parent.replaceAll("\\\\", "/");

				String metaInfResources = "META-INF/resources";

				parent = parent.substring(parent.indexOf(metaInfResources) + metaInfResources.length());

				IPath resources = location.append(projectName).append("src/main/resources/META-INF/resources");

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

	public static final Status execute(NewModuleFragmentOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Creating Liferay module fragment project (this process may take several minutes)", 100);

		Status retval = null;

		try {
			NewLiferayProjectProvider<BaseModuleOp> projectProvider = op.getProjectProvider().content(true);

			IStatus status = projectProvider.createNewProject(op, monitor);

			retval = StatusBridge.create(status);

			if (retval.ok()) {
				_updateBuildPrefs(op);
			}
		}
		catch (Exception e) {
			String msg = "Error creating Liferay module fragment project.";

			ProjectCore.logError(msg, e);

			return Status.createErrorStatus(msg + " Please see Eclipse error log for more details.", e);
		}

		return retval;
	}

	public static String[] getBsnAndVersion(NewModuleFragmentOp op) throws CoreException {
		String hostBundleName = op.getHostOsgiBundle().content();

		IPath tempLocation = ProjectCore.getDefault().getStateLocation();

		File hostBundleJar = tempLocation.append(hostBundleName).toFile();

		if (FileUtil.notExists(hostBundleJar)) {
			IRuntime runtime = ServerUtil.getRuntime(op.getLiferayRuntimeName().content());

			hostBundleJar = ServerUtil.getModuleFileFrom70Server(runtime, hostBundleName, tempLocation);
		}

		String[] bsnAndVersion =
			FileUtil.readMainFestProsFromJar(hostBundleJar, "Bundle-SymbolicName", "Bundle-Version");

		try {
			_hostBundleDir = LiferayCore.GLOBAL_USER_DIR.append(bsnAndVersion[0] + "-" + bsnAndVersion[1]).toFile();
		}
		catch (NullPointerException npe) {
			throw new CoreException(
				ProjectCore.createErrorStatus("'" + hostBundleName + "' is not a valid osgi bundle")
			);
		}

		if (FileUtil.notExists(_hostBundleDir)) {
			try {
				ZipUtil.unzip(hostBundleJar, _hostBundleDir);
			}
			catch (IOException ioe) {
				throw new CoreException(ProjectCore.createErrorStatus(ioe));
			}
		}

		return bsnAndVersion;
	}

	public static String getMavenParentPomGroupId(NewModuleFragmentOp op, String projectName, IPath path) {
		String retval = null;

		File parentProjectDir = path.toFile();

		NewLiferayProjectProvider<BaseModuleOp> provider = op.getProjectProvider().content();

		IStatus locationStatus = provider.validateProjectLocation(projectName, path);

		if (locationStatus.isOK() && FileUtil.hasChildren(parentProjectDir)) {
			List<String> groupIds = provider.getData("parentGroupId", String.class, parentProjectDir);

			if (ListUtil.isNotEmpty(groupIds)) {
				retval = groupIds.get(0);
			}
		}

		return retval;
	}

	public static String getMavenParentPomVersion(NewModuleFragmentOp op, String projectName, IPath path) {
		String retval = null;

		File parentProjectDir = path.toFile();

		NewLiferayProjectProvider<BaseModuleOp> provider = op.getProjectProvider().content();

		IStatus locationStatus = provider.validateProjectLocation(projectName, path);

		if (locationStatus.isOK() && FileUtil.hasChildren(parentProjectDir)) {
			List<String> versions = provider.getData("parentVersion", String.class, parentProjectDir);

			if (ListUtil.isNotEmpty(versions)) {
				retval = versions.get(0);
			}
		}

		return retval;
	}

	private static void _updateBuildPrefs(NewModuleFragmentOp op) {
		try {
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

			prefs.put(
				ProjectCore.PREF_DEFAULT_MODULE_FRAGMENT_PROJECT_BUILD_TYPE_OPTION, op.getProjectProvider().text());

			prefs.flush();
		}
		catch (Exception e) {
			String msg = "Error updating default project build type.";

			ProjectCore.logError(msg, e);
		}
	}

	private static File _hostBundleDir = null;
}