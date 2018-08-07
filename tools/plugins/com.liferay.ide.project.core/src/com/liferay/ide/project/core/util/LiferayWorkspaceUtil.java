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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.Properties;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * @author Andy Wu
 * @author Charles Wu
 */
public class LiferayWorkspaceUtil {

	public static final String LIFERAY_WORKSPACE_BUNDLE_TOKEN_DOWNLOAD = "liferay.workspace.bundle.token.download";

	public static final String LIFERAY_WORKSPACE_BUNDLE_TOKEN_EMAIL_ADDRESS =
		"liferay.workspace.bundle.token.email.address";

	public static final String LIFERAY_WORKSPACE_BUNDLE_TOKEN_FORCE = "liferay.workspace.bundle.token.force";

	public static final String LIFERAY_WORKSPACE_BUNDLE_TOKEN_PASSWORD = "liferay.workspace.bundle.token.password";

	public static final String LIFERAY_WORKSPACE_BUNDLE_URL = "liferay.workspace.bundle.url";

	public static final String LIFERAY_WORKSPACE_DEFAULT_REPOSITORY_ENABLED =
		"liferay.workspace.default.repository.enabled";

	public static final String LIFERAY_WORKSPACE_ENVIRONMENT = "liferay.workspace.environment";

	public static final String LIFERAY_WORKSPACE_HOME_DIR = "liferay.workspace.home.dir";

	public static final String LIFERAY_WORKSPACE_MODULES_DEFAULT_REPOSITORY_ENABLED =
		"liferay.workspace.modules.default.repository.enabled";

	public static final String LIFERAY_WORKSPACE_MODULES_DIR = "liferay.workspace.modules.dir";

	public static final String LIFERAY_WORKSPACE_PLUGINS_SDK_DIR = "liferay.workspace.plugins.sdk.dir";

	public static final String LIFERAY_WORKSPACE_THEMES_DIR = "liferay.workspace.themes.dir";

	public static final String LIFERAY_WORKSPACE_WARS_DIR = "liferay.workspace.wars.dir";

	public static String hasLiferayWorkspaceMsg =
		"A Liferay Workspace project already exists in this Eclipse instance.";
	public static String multiWorkspaceErrorMsg = "More than one Liferay workspace build in current Eclipse workspace.";

	public static IStatus addPortalRuntime() {
		return addPortalRuntime(null);
	}

	public static IStatus addPortalRuntime(String serverName) {
		IProject project = getWorkspaceProject();

		try {
			if (project == null) {
				return ProjectCore.createErrorStatus("Can't get a valid Liferay Workspace project.");
			}

			IPath bundlesLocation = getHomeLocation(project);

			if (FileUtil.exists(bundlesLocation)) {
				PortalBundle bundle = LiferayServerCore.newPortalBundle(bundlesLocation);

				if (bundle == null) {
					return ProjectCore.createErrorStatus("Bundle can't be found in:" + bundlesLocation);
				}

				if (serverName == null) {
					serverName = bundle.getServerReleaseInfo();
				}

				ServerUtil.addPortalRuntimeAndServer(serverName, bundlesLocation, new NullProgressMonitor());

				IProject pluginsSDK = CoreUtil.getProject(getPluginsSDKDir(project.getLocation().toPortableString()));

				if (FileUtil.exists(pluginsSDK)) {
					SDK sdk = SDKUtil.createSDKFromLocation(pluginsSDK.getLocation());

					sdk.addOrUpdateServerProperties(
						ServerUtil.getLiferayRuntime(ServerUtil.getServer(serverName)).getLiferayHome());

					pluginsSDK.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
			}
		}
		catch (Exception e) {
			return ProjectCore.createErrorStatus("Add Liferay server failed", e);
		}

		return Status.OK_STATUS;
	}

	public static void clearWorkspace(String location) {
		File projectFile = new File(location, ".project");

		if (FileUtil.exists(projectFile)) {
			projectFile.delete();
		}

		File classpathFile = new File(location, ".classpath");

		if (FileUtil.exists(classpathFile)) {
			classpathFile.delete();
		}

		File settings = new File(location, ".settings");

		if (FileUtil.isDir(settings)) {
			FileUtil.deleteDir(settings, true);
		}
	}

	public static File findParentFile(File dir, String[] fileNames, boolean checkParents) {
		if (dir == null) {
			return null;
		}

		for (String fileName : fileNames) {
			File file = new File(dir, fileName);

			if (FileUtil.exists(file)) {
				return dir;
			}
		}

		if (checkParents) {
			return findParentFile(dir.getParentFile(), fileNames, checkParents);
		}

		return null;
	}

	public static String getGradleProperty(String projectLocation, String key, String defaultValue) {
		File gradleProperties = new File(projectLocation, "gradle.properties");

		String retVal = null;

		if (FileUtil.exists(gradleProperties)) {
			Properties properties = PropertiesUtil.loadProperties(gradleProperties);

			retVal = properties.getProperty(key, defaultValue);
		}

		return retVal;
	}

	public static String getHomeDir(String location) {
		String result = getGradleProperty(location, LIFERAY_WORKSPACE_HOME_DIR, "bundles");

		if (CoreUtil.empty(result)) {
			return "bundles";
		}

		return result;
	}

	public static IPath getHomeLocation(IProject project) {
		return getHomeLocation(project.getLocation().toOSString());
	}

	public static IPath getHomeLocation(String location) {
		String homeNameOrPath = getHomeDir(location);

		IPath homePath = new Path(location).append(homeNameOrPath);

		if (FileUtil.exists(homePath)) {
			return homePath;
		}

		homePath = new Path(homeNameOrPath);

		if (FileUtil.exists(homePath)) {
			return homePath;
		}

		return null;
	}

	public static String[] getLiferayWorkspaceProjectWarsDirs(String workspaceLocation) {
		String[] retval = null;

		if (workspaceLocation != null) {
			String val = getGradleProperty(workspaceLocation, LIFERAY_WORKSPACE_WARS_DIR, "wars");

			retval = val.split(",");
		}

		return retval;
	}

	public static IWorkspaceProjectBuilder getWorkspaceProjectBuilder(IProject project) throws CoreException {
		final ILiferayProject liferayProject = LiferayCore.create(project);

		if (liferayProject == null) {
			throw new CoreException(ProjectCore.createErrorStatus("Can't find Liferay workspace project."));
		}

		final IWorkspaceProjectBuilder builder = liferayProject.adapt(IWorkspaceProjectBuilder.class);

		if (builder == null) {
			throw new CoreException(ProjectCore.createErrorStatus("Can't find Liferay Gradle project builder."));
		}

		return builder;
	}

	public static String getModulesDir(IProject project) {
		return getModulesDirArray(project)[0];
	}

	public static String[] getModulesDirArray(IProject project) {
		String[] retval = null;

		if (project != null) {
			IPath projectLocation = project.getLocation();

			if (projectLocation != null) {
				String val = getGradleProperty(
					projectLocation.toPortableString(), LIFERAY_WORKSPACE_MODULES_DIR, "modules");

				if (CoreUtil.empty(val)) {
					val = "modules";
				}

				retval = val.split(",");
			}
		}

		return retval;
	}

	public static String getPluginsSDKDir(String location) {
		String result = getGradleProperty(location, LIFERAY_WORKSPACE_PLUGINS_SDK_DIR, "plugins-sdk");

		if (CoreUtil.empty(result)) {
			return "bundles";
		}

		return result;
	}

	public static String getThemesDir(IProject project) {
		String retval = null;

		if (project != null) {
			IPath projectLocation = project.getLocation();

			if (projectLocation != null) {
				retval = getGradleProperty(projectLocation.toPortableString(), LIFERAY_WORKSPACE_THEMES_DIR, "themes");
			}
		}

		if (CoreUtil.empty(retval)) {
			return "themes";
		}

		return retval;
	}

	public static String[] getWarsDirs(IProject project) {
		String[] retval = null;

		if (project != null) {
			IPath projectLocation = project.getLocation();

			if (projectLocation != null) {
				String val = getGradleProperty(projectLocation.toPortableString(), LIFERAY_WORKSPACE_WARS_DIR, "wars");

				if (CoreUtil.empty(val)) {
					val = "wars";
				}

				retval = val.split(",");
			}
		}

		return retval;
	}

	public static File getWorkspaceDir(File dir) {
		return findParentFile(dir, new String[] {_SETTINGS_GRADLE_FILE_NAME, _GRADLE_PROPERTIES_FILE_NAME}, true);
	}

	public static IProject getWorkspaceProject() {
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isValidWorkspace(project)) {
				return project;
			}
		}

		return null;
	}

	public static String getWorkspaceType(String location) {
		if (isValidWorkspaceLocation(location)) {
			File pomFile = new File(location, "pom.xml");

			if (FileUtil.exists(pomFile)) {
				return "maven-liferay-workspace";
			}
			else {
				return "gradle-liferay-workspace";
			}
		}

		return null;
	}

	public static boolean hasBundlesDir(String location) {
		File bundles = new File(location, getHomeDir(location));

		File outsideOfWorkspaceBundles = new File(getHomeDir(location));

		if (FileUtil.isDir(bundles) || FileUtil.isDir(outsideOfWorkspaceBundles)) {
			return true;
		}

		return false;
	}

	public static boolean hasGradleWorkspace() throws CoreException {
		IProject[] projects = CoreUtil.getAllProjects();

		int count = 0;

		for (IProject project : projects) {
			if (_isValidGradleWorkspace(project)) {
				++count;
			}
		}

		if (count == 1) {
			return true;
		}
		else if (count > 1) {
			throw new CoreException(ProjectCore.createErrorStatus(multiWorkspaceErrorMsg));
		}

		return false;
	}

	public static boolean hasMavenWorkspace() throws CoreException {
		IProject[] projects = CoreUtil.getAllProjects();

		int count = 0;

		for (IProject project : projects) {
			if (_isValidMavenWorkspace(project)) {
				++count;
			}
		}

		if (count == 1) {
			return true;
		}
		else if (count > 1) {
			throw new CoreException(ProjectCore.createErrorStatus(multiWorkspaceErrorMsg));
		}

		return false;
	}

	public static boolean hasWorkspace() throws CoreException {
		IProject[] projects = CoreUtil.getAllProjects();

		int count = 0;

		for (IProject project : projects) {
			if (isValidWorkspace(project)) {
				++count;
			}
		}

		if (count == 1) {
			return true;
		}
		else if (count > 1) {
			throw new CoreException(ProjectCore.createErrorStatus(multiWorkspaceErrorMsg));
		}

		return false;
	}

	public static boolean inLiferayWorkspace(IProject project) {
		return inLiferayWorkspace(project.getLocation());
	}

	public static boolean inLiferayWorkspace(IPath location) {
		if (FileUtil.notExists(location) || FileUtil.notExists(getWorkspaceProject())) {
			return false;
		}

		IPath workspaceLocation = getWorkspaceProject().getLocation();

		if (workspaceLocation.isPrefixOf(location) && !workspaceLocation.equals(location)) {
			return true;
		}

		return false;
	}

	public static boolean isValidGradleWorkspaceLocation(String location) {
		File workspaceDir = new File(location);

		File buildGradle = new File(workspaceDir, _BUILD_GRADLE_FILE_NAME);
		File settingsGradle = new File(workspaceDir, _SETTINGS_GRADLE_FILE_NAME);
		File gradleProperties = new File(workspaceDir, _GRADLE_PROPERTIES_FILE_NAME);

		if (FileUtil.notExists(buildGradle) || FileUtil.notExists(settingsGradle) ||
			FileUtil.notExists(gradleProperties)) {

			return false;
		}

		String settingsContent = FileUtil.readContents(settingsGradle, true);

		if ((settingsContent != null) && _workspacePluginPattern.matcher(settingsContent).matches()) {
			return true;
		}

		return false;
	}

	public static boolean isValidMavenWorkspaceLocation(String location) {
		File workspaceDir = new File(location);

		File pomFile = new File(workspaceDir, "pom.xml");

		if (FileUtil.exists(pomFile)) {
			String content = FileUtil.readContents(pomFile);

			if (content.contains("com.liferay.portal.tools.bundle.support")) {
				return true;
			}
		}

		return false;
	}

	public static boolean isValidWorkspace(IProject project) {
		if ((project != null) && (project.getLocation() != null) &&
			isValidWorkspaceLocation(project.getLocation().toOSString())) {

			return true;
		}

		return false;
	}

	public static boolean isValidWorkspaceLocation(IPath path) {
		if (FileUtil.notExists(path)) {
			return false;
		}

		return isValidWorkspaceLocation(path.toOSString());
	}

	public static boolean isValidWorkspaceLocation(String location) {
		if (isValidMavenWorkspaceLocation(location) || isValidGradleWorkspaceLocation(location)) {
			return true;
		}

		return false;
	}

	public static String read(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()));
	}

	private static boolean _isValidGradleWorkspace(IProject project) {
		if ((project != null) && (project.getLocation() != null) &&
			isValidGradleWorkspaceLocation(project.getLocation().toOSString())) {

			return true;
		}

		return false;
	}

	private static boolean _isValidMavenWorkspace(IProject project) {
		if ((project != null) && (project.getLocation() != null) &&
			isValidMavenWorkspaceLocation(project.getLocation().toOSString())) {

			return true;
		}

		return false;
	}

	private static final String _BUILD_GRADLE_FILE_NAME = "build.gradle";

	private static final String _GRADLE_PROPERTIES_FILE_NAME = "gradle.properties";

	private static final String _SETTINGS_GRADLE_FILE_NAME = "settings.gradle";

	private static final Pattern _workspacePluginPattern = Pattern.compile(
		".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.workspace[\'\"].*", Pattern.MULTILINE | Pattern.DOTALL);

}