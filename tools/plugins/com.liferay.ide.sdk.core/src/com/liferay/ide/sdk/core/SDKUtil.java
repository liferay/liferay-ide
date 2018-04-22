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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 * @author Lovett Li
 * @author Simon Jiang
 */
public class SDKUtil {

	public static int countPossibleWorkspaceSDKProjects() {
		int sdkCount = 0;
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isValidSDKLocation(project.getLocation().toOSString())) {
				sdkCount++;
			}
		}

		return sdkCount;
	}

	public static SDK createSDKFromLocation(IPath path) {
		try {
			if (isValidSDKLocation(path.toOSString())) {
				SDK sdk = new SDK(path);

				sdk.setName(path.lastSegment());

				return sdk;
			}
		}
		catch (Exception e) {
		}

		return null;
	}

	public static SDK getSDK(IProject project) {
		SDK retval = null;

		// try to determine SDK based on project location

		IPath projectLocation = project.getRawLocation();

		if (projectLocation == null) {
			projectLocation = project.getLocation();
		}

		if (projectLocation != null) {
			IPath sdkLocation = projectLocation.removeLastSegments(2);

			retval = SDKManager.getInstance().getSDK(sdkLocation);

			if (retval == null) {
				retval = createSDKFromLocation(sdkLocation);

				if (retval != null) {
					SDK newSDK = retval;

					SDKManager.getInstance().addSDK(newSDK);
				}
			}
		}

		if (retval == null) {

			/**
			 *  this means the sdk could not be determined by location (user is
			 *using out-of-sdk style projects)
			 *so we should check to see if the sdk name is persisted to the
			 *project prefs
			 */
			IScopeContext[] context = {new ProjectScope(project)};

			String sdkName = Platform.getPreferencesService().getString(
				SDKCorePlugin.PLUGIN_ID, SDKCorePlugin.PREF_KEY_SDK_NAME, null, context);

			retval = SDKManager.getInstance().getSDK(sdkName);
		}

		return retval;
	}

	public static SDK getSDKFromProjectDir(File projectDir) {
		File sdkDir = projectDir.getParentFile().getParentFile();

		if (sdkDir.exists() && isValidSDKLocation(sdkDir.getPath())) {
			Path sdkLocation = new Path(sdkDir.getPath());

			SDK existingSDK = SDKManager.getInstance().getSDK(sdkLocation);

			if (existingSDK != null) {
				return existingSDK;
			}
			else {
				return createSDKFromLocation(sdkLocation);
			}
		}

		return null;
	}

	public static SDK getWorkspaceSDK() throws CoreException {
		SDK sdk = null;
		IProject workspaceSDKProject = getWorkspaceSDKProject();

		if (workspaceSDKProject != null) {
			sdk = createSDKFromLocation(workspaceSDKProject.getLocation());
		}

		return sdk;
	}

	public static IProject getWorkspaceSDKProject() throws CoreException {
		IProject retval = null;
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isValidSDKLocation(project.getLocation().toOSString())) {
				if (retval != null) {
					throw new CoreException(
						SDKCorePlugin.createErrorStatus(
							new IllegalStateException("Workspace can't have more than one SDK project open")));
				}

				retval = project;
			}
		}

		return retval;
	}

	public static IProject[] getWorkspaceSDKs() {
		List<IProject> sdkProjects = new ArrayList<>();
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isValidSDKLocation(project.getLocation().toOSString())) {
				sdkProjects.add(project);
			}
		}

		return sdkProjects.toArray(new IProject[sdkProjects.size()]);
	}

	public static boolean hasGradleTools(IPath path) {
		IPath gradleToolPath = path.append("tools").append("gradle");

		return gradleToolPath.toFile().exists();
	}

	public static boolean isIvyProject(IProject project) {
		try {
			if (isSDKProject(project) && project.hasNature("org.apache.ivyde.eclipse.ivynature")) {
				return true;
			}

			return false;
		}
		catch (CoreException ce) {
		}

		return false;
	}

	public static boolean isSDKProject(IProject project) {
		if ((project == null) || !project.exists() || !project.isAccessible()) {
			return false;
		}

		if (getSDK(project) != null) {
			return true;
		}

		return false;
	}

	public static boolean isSDKSupported(String location) {
		boolean retval = false;

		try {
			String version = readSDKVersion(location);

			retval = CoreUtil.compareVersions(new Version(version), ISDKConstants.LEAST_SUPPORTED_SDK_VERSION) >= 0;
		}
		catch (Exception e) {

			// best effort we didn't find a valid location

		}

		return retval;
	}

	public static boolean isValidSDKLocation(String loc) {
		boolean retval = false;

		try {
			File sdkDir = new File(loc);

			File buildProperties = new File(sdkDir, ISDKConstants.BUILD_PROPERTIES);
			File portletsBuildXml = new File(sdkDir, ISDKConstants.PORTLET_PLUGIN_ANT_BUILD);
			File hooksBuildXml = new File(sdkDir, ISDKConstants.HOOK_PLUGIN_ANT_BUILD);

			retval = buildProperties.exists() && portletsBuildXml.exists() && hooksBuildXml.exists();
		}
		catch (Exception e) {

			// best effort we didn't find a valid location

		}

		return retval;
	}

	public static boolean isValidSDKVersion(String sdkVersion, Version lowestValidVersion) {
		Version sdkVersionValue = null;

		try {
			sdkVersionValue = new Version(sdkVersion);
		}
		catch (Exception ex) {

			// ignore means we don't have valid version

		}

		if ((sdkVersionValue != null) && (CoreUtil.compareVersions(sdkVersionValue, lowestValidVersion) >= 0)) {
			return true;
		}

		return false;
	}

	public static void openAsProject(SDK sdk) throws CoreException {
		openAsProject(sdk, new NullProgressMonitor());
	}

	public static void openAsProject(SDK sdk, IProgressMonitor monitor) throws CoreException {
		IProject sdkProject = CoreUtil.getProject(sdk.getName());

		if ((sdkProject == null) || !sdkProject.exists()) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			IProjectDescription description = workspace.newProjectDescription(sdk.getLocation().lastSegment());

			File sdkLocation = sdk.getLocation().toFile();

			description.setLocationURI(sdkLocation.toURI());

			sdkProject.create(description, monitor);

			IPath settingFolderPath = sdkProject.getLocation().append(".settings");

			File settingFolder = settingFolderPath.toFile();

			if (!settingFolder.exists()) {
				settingFolder.mkdir();
			}

			File settingFile = settingFolderPath.append("org.eclipse.wst.validation.prefs").toFile();

			if (!settingFile.exists()) {
				try {
					Bundle bundle = SDKCorePlugin.getDefault().getBundle();

					URL url = FileLocator.toFileURL(bundle.getEntry("files/org.eclipse.wst.validation.prefs"));

					File file = new File(url.getFile());

					FileUtil.copyFileToDir(file, settingFolder);
				}
				catch (IOException ioe) {
				}
			}

			sdkProject.open(monitor);
		}
	}

	public static String readSDKVersion(String path) throws FileNotFoundException, IOException {
		Properties properties = new Properties();

		Path sdkPath = new Path(path);

		File propertiesFile = sdkPath.append("build.properties").toFile();

		try (InputStream in = Files.newInputStream(propertiesFile.toPath())) {
			properties.load(in);
		}
		catch (Exception e) {
		}

		return properties.getProperty("lp.version");
	}

	public static void saveSDKNameSetting(IProject project, String sdkName) {
		try {
			IEclipsePreferences prefs = new ProjectScope(project).getNode(SDKCorePlugin.PLUGIN_ID);

			prefs.put(SDKCorePlugin.PREF_KEY_SDK_NAME, sdkName);
			prefs.flush();
		}
		catch (BackingStoreException bse) {
			SDKCorePlugin.logError("Unable to persist sdk name to project " + project, bse);
		}
	}

}