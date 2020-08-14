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
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.modules.BndProperties;
import com.liferay.ide.project.core.modules.BndPropertiesValue;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
 * @author Ethan Sun
 * @author Simon Jiang
 */
public class NewModuleFragmentOpMethods {

	public static void copyOverrideFiles(NewModuleFragmentOp op) {
		String projectName = _getter.get(op.getProjectName());

		IPath location = PathBridge.create(_getter.get(op.getLocation()));

		ElementList<OverrideFilePath> files = op.getOverrideFiles();

		for (OverrideFilePath file : files) {
			File fragmentFile = new File(_hostBundleDir, _getter.get(file.getValue()));

			if (FileUtil.notExists(fragmentFile)) {
				continue;
			}

			File folder = null;

			if (FileUtil.nameEquals(fragmentFile, "portlet.properties")) {
				IPath path = location.append(projectName);

				path = path.append("src/main/java");

				folder = path.toFile();

				FileUtil.copyFileToDir(fragmentFile, "portlet-ext.properties", folder);
			}
			else if (StringUtil.contains(fragmentFile.getName(), "default.xml")) {
				File parentFile = fragmentFile.getParentFile();

				String parent = parentFile.getPath();

				parent = parent.replaceAll("\\\\", "/");

				String metaInfResources = "resource-actions";

				parent = parent.substring(parent.indexOf(metaInfResources) + metaInfResources.length());

				IPath resources = location.append(projectName);

				resources = resources.append("src/main/resources/resource-actions");

				folder = resources.toFile();

				folder.mkdirs();

				if (!parent.equals("resource-actions") && !parent.equals("")) {
					folder = FileUtil.getFile(resources.append(parent));

					folder.mkdirs();
				}

				FileUtil.copyFileToDir(fragmentFile, "default-ext.xml", folder);

				try {
					IPath p = location.append(projectName);

					File ext = new File(p.append("src/main/resources") + "/portlet-ext.properties");

					ext.createNewFile();

					String extFileContent =
						"resource.actions.configs=resource-actions/default.xml,resource-actions/default-ext.xml";

					FileUtil.writeFile(ext, extFileContent, null);
				}
				catch (Exception e) {
				}
			}
			else {
				File parent = fragmentFile.getParentFile();

				String parentPath = parent.getPath();

				parentPath = parentPath.replaceAll("\\\\", "/");

				String metaInfResources = "META-INF/resources";

				parentPath = parentPath.substring(parentPath.indexOf(metaInfResources) + metaInfResources.length());

				IPath resources = location.append(projectName);

				resources = resources.append("src/main/resources/META-INF/resources");

				folder = resources.toFile();

				folder.mkdirs();

				if (!parentPath.equals("resources") && !parentPath.equals("")) {
					folder = FileUtil.getFile(resources.append(parentPath));

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

		Throwable errorStack = null;

		try {
			NewLiferayProjectProvider<BaseModuleOp> projectProvider = _getter.get(op.getProjectProvider());

			IStatus status = projectProvider.createNewProject(op, monitor);

			retval = StatusBridge.create(status);

			if (retval.ok()) {
				_updateBuildPrefs(op);
			}
			else if ((retval.severity() == Status.Severity.ERROR) && (retval.exception() != null)) {
				errorStack = retval.exception();
			}
		}
		catch (Exception e) {
			errorStack = e;
		}

		if (errorStack != null) {
			String readableStack = CoreUtil.getStackTrace(errorStack);

			ProjectCore.logError(readableStack);

			return Status.createErrorStatus(readableStack + "\t Please see Eclipse error log for more details.");
		}

		return retval;
	}

	public static String[] getBsnAndVersion(NewModuleFragmentOp op) throws CoreException {
		String hostBundleName = _getter.get(op.getHostOsgiBundle());

		ProjectCore projectCore = ProjectCore.getDefault();

		IPath tempLocation = projectCore.getStateLocation();

		File hostBundleJar = FileUtil.getFile(tempLocation.append(hostBundleName));

		if (FileUtil.notExists(hostBundleJar)) {
			IRuntime runtime = ServerUtil.getRuntime(_getter.get(op.getLiferayRuntimeName()));

			hostBundleJar = ServerUtil.getModuleFileFrom70Server(runtime, hostBundleName, tempLocation);
		}

		String[] bsnAndVersion = FileUtil.readMainFestProsFromJar(
			hostBundleJar, "Bundle-SymbolicName", "Bundle-Version");

		try {
			_hostBundleDir = FileUtil.getFile(
				LiferayCore.GLOBAL_USER_DIR.append(bsnAndVersion[0] + "-" + bsnAndVersion[1]));
		}
		catch (NullPointerException npe) {
			throw new CoreException(
				ProjectCore.createErrorStatus("'" + hostBundleName + "' is not a valid osgi bundle", npe));
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

		NewLiferayProjectProvider<BaseModuleOp> provider = _getter.get(op.getProjectProvider());

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

		NewLiferayProjectProvider<BaseModuleOp> provider = _getter.get(op.getProjectProvider());

		IStatus locationStatus = provider.validateProjectLocation(projectName, path);

		if (locationStatus.isOK() && FileUtil.hasChildren(parentProjectDir)) {
			List<String> versions = provider.getData("parentVersion", String.class, parentProjectDir);

			if (ListUtil.isNotEmpty(versions)) {
				retval = versions.get(0);
			}
		}

		return retval;
	}

	public static void storeRuntimeInfo(NewModuleFragmentOp op) {
		String projectName = _getter.get(op.getProjectName());

		IPath location = PathBridge.create(_getter.get(op.getLocation()));

		String runtimeName = _getter.get(op.getLiferayRuntimeName());

		IRuntime runtime = ServerUtil.getRuntime(runtimeName);

		if (runtime == null) {
			return;
		}

		PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(
			PortalRuntime.class, new NullProgressMonitor());

		if (portalRuntime == null) {
			return;
		}

		IPath projectLocation = location.append(projectName);

		IPath bndFilePath = projectLocation.append("bnd.bnd");

		File bndFile = bndFilePath.toFile();

		if (bndFile.exists()) {
			BndProperties bndProperty = new BndProperties();

			try {
				bndProperty.load(bndFile);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}

			try (OutputStream out = Files.newOutputStream(bndFile.toPath())) {
				bndProperty.addValue("Portal-Bundle-Version", new BndPropertiesValue(portalRuntime.getPortalVersion()));

				bndProperty.store(out, null);
			}
			catch (Exception e) {
			}
		}
	}

	private static void _updateBuildPrefs(NewModuleFragmentOp op) {
		try {
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

			prefs.put(
				ProjectCore.PREF_DEFAULT_MODULE_FRAGMENT_PROJECT_BUILD_TYPE_OPTION,
				SapphireUtil.getText(op.getProjectProvider()));

			prefs.flush();
		}
		catch (Exception e) {
			String msg = "Error updating default project build type.";

			ProjectCore.logError(msg, e);
		}
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};
	private static File _hostBundleDir = null;

}