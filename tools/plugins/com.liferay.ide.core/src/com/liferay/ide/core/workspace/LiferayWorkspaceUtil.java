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

package com.liferay.ide.core.workspace;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.IWorkspaceProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.osgi.framework.Version;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andy Wu
 * @author Charles Wu
 * @author Simon Jiang
 */
public class LiferayWorkspaceUtil {

	public static String hasLiferayWorkspaceMsg =
		"A Liferay Workspace project already exists in this Eclipse instance.";
	public static String multiWorkspaceErrorMsg = "More than one Liferay workspace build in current Eclipse workspace.";

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
		if (Objects.isNull(dir)) {
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

	public static IPath getBundleHomePath(IProject project) {
		if (Objects.nonNull(project)) {
			IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, project);

			if (Objects.isNull(workspaceProject)) {
				return null;
			}

			String bundleHome = workspaceProject.getLiferayHome();

			IPath bundleHomePath = Path.fromOSString(bundleHome);

			if (!bundleHomePath.isAbsolute()) {
				IPath projectLocation = project.getLocation();

				bundleHomePath = projectLocation.append(bundleHome);
			}

			return bundleHomePath;
		}

		return null;
	}

	public static String getExtDir(IProject project) {
		String retval = null;

		if (Objects.nonNull(project)) {
			IPath projectLocation = project.getLocation();

			if (Objects.nonNull(projectLocation)) {
				retval = getGradleProperty(
					projectLocation.toPortableString(), WorkspaceConstants.EXT_DIR_PROPERTY,
					WorkspaceConstants.DEFAULT_EXT_DIR);
			}
		}

		if (CoreUtil.empty(retval)) {
			return WorkspaceConstants.DEFAULT_EXT_DIR;
		}

		return retval;
	}

	public static IPath getExtDirLocation(IProject workspaceProject) {
		String extDir = getExtDir(workspaceProject);

		IPath projectLocation = workspaceProject.getLocation();

		return projectLocation.append(extDir);
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

	public static IWorkspaceProject getGradleWorkspaceProject() {
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isValidGradleWorkspaceLocation(project.getLocation())) {
				return LiferayCore.create(IWorkspaceProject.class, project);
			}
		}

		return null;
	}

	public static String getHomeDir(String location) {
		String result = null;

		if (CoreUtil.isNullOrEmpty(location)) {
			return result;
		}

		if (isValidGradleWorkspaceLocation(location)) {
			result = getGradleProperty(
				location, WorkspaceConstants.HOME_DIR_PROPERTY, WorkspaceConstants.DEFAULT_HOME_DIR);
		}
		else if (isValidMavenWorkspaceLocation(location)) {
			result = getMavenProperty(
				location, WorkspaceConstants.LIFERAY_HOME_PROPERTY, WorkspaceConstants.DEFAULT_HOME_DIR);
		}

		return result;
	}

	public static boolean getIndexSource(IProject project) {
		String result = "false";

		if (FileUtil.exists(project)) {
			IPath location = project.getLocation();

			if (isValidGradleWorkspaceLocation(location)) {
				result = getGradleProperty(
					location.toOSString(), WorkspaceConstants.TARGET_PLATFORM_INDEX_SOURCES_PROPERTY, "false");
			}
		}

		return Boolean.parseBoolean(result);
	}

	public static IWorkspaceProject getLiferayWorkspaceProject() {
		IProject workspaceProject = getWorkspaceProject();

		if (Objects.nonNull(workspaceProject)) {
			return LiferayCore.create(IWorkspaceProject.class, workspaceProject);
		}

		return null;
	}

	public static String getLiferayWorkspaceProjectVersion() {
		IWorkspaceProject liferayWorkspaceProject = getLiferayWorkspaceProject();

		if (Objects.isNull(liferayWorkspaceProject)) {
			return null;
		}

		String targetPlatformVersion = liferayWorkspaceProject.getTargetPlatformVersion();

		if (CoreUtil.isNotNullOrEmpty(targetPlatformVersion)) {
			try {
				Version liferayVersion = new Version(targetPlatformVersion);

				return new String(liferayVersion.getMajor() + "." + liferayVersion.getMinor());
			}
			catch (IllegalArgumentException iae) {
				LiferayCore.logError("Failed to get liferay workspace project version.", iae);
			}
		}

		return null;
	}

	public static String[] getLiferayWorkspaceProjectWarsDirs(String workspaceLocation) {
		String[] retval = null;

		if (workspaceLocation != null) {
			String val = getGradleProperty(workspaceLocation, WorkspaceConstants.WARS_DIR_PROPERTY, "wars");

			retval = val.split(",");
		}

		return retval;
	}

	public static String getMavenProperty(String location, String propertyName, String defaultValue) {
		File pomFile = new File(location, "pom.xml");

		if (FileUtil.exists(pomFile)) {
			try {
				Document doc = FileUtil.readXMLFile(pomFile);

				NodeList propertyNodeList = doc.getElementsByTagName("properties");

				for (int i = 0; i < propertyNodeList.getLength(); i++) {
					Node propertyNode = propertyNodeList.item(i);

					if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
						Element propertyElement = (Element)propertyNode;

						NodeList propertyTagValueNodes = propertyElement.getElementsByTagName(propertyName);

						int nodeLength = propertyTagValueNodes.getLength();

						if (nodeLength > 0) {
							Node node = propertyTagValueNodes.item(0);

							return node.getTextContent();
						}
					}
				}
			}
			catch (Exception e) {
			}
		}

		return defaultValue;
	}

	public static String getModulesDir(IProject project) {
		if (Objects.nonNull(getModulesDirArray(project))) {
			return getModulesDirArray(project)[0];
		}

		return null;
	}

	public static String[] getModulesDirArray(IProject project) {
		String[] retval = new String[0];

		if (Objects.nonNull(project)) {
			IPath projectLocation = project.getLocation();

			if (Objects.nonNull(projectLocation)) {
				String val = getGradleProperty(
					projectLocation.toPortableString(), WorkspaceConstants.MODULES_DIR_PROPERTY, "modules");

				if (CoreUtil.empty(val)) {
					val = "modules";
				}

				retval = val.split(",");
			}
		}

		return retval;
	}

	public static String getPluginsSDKDir(String location) {
		String result = getGradleProperty(location, WorkspaceConstants.PLUGINS_SDK_DIR_PROPERTY, "plugins-sdk");

		if (CoreUtil.empty(result)) {
			return "bundles";
		}

		return result;
	}

	public static String getThemesDir(IProject project) {
		String retval = null;

		if (Objects.nonNull(project)) {
			IPath projectLocation = project.getLocation();

			if (Objects.nonNull(projectLocation)) {
				retval = getGradleProperty(
					projectLocation.toPortableString(), WorkspaceConstants.THEMES_DIR_PROPERTY, "themes");
			}
		}

		if (CoreUtil.empty(retval)) {
			return "themes";
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

	public static IWorkspaceProjectBuilder getWorkspaceProjectBuilder(IProject project) throws CoreException {
		final ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

		if (Objects.isNull(liferayProject)) {
			throw new CoreException(LiferayCore.createErrorStatus("Can not find Liferay workspace project."));
		}

		final IWorkspaceProjectBuilder builder = liferayProject.adapt(IWorkspaceProjectBuilder.class);

		if (Objects.isNull(builder)) {
			throw new CoreException(LiferayCore.createErrorStatus("Can not find Liferay Gradle project builder."));
		}

		return builder;
	}

	public static File getWorkspaceProjectFile() {
		IProject workspaceProject = getWorkspaceProject();

		if (Objects.nonNull(workspaceProject)) {
			IPath location = workspaceProject.getLocation();

			return location.toFile();
		}

		return null;
	}

	public static String getWorkspaceType(String location) {
		if (isValidWorkspaceLocation(location)) {
			File pomFile = new File(location, "pom.xml");

			if (FileUtil.exists(pomFile)) {
				return "maven-liferay-workspace";
			}

			return "gradle-liferay-workspace";
		}

		return null;
	}

	public static String guessLiferayVersion(IProject project) {
		if (Objects.isNull(project)) {
			return "";
		}

		IPath location = project.getLocation();

		String bundleUrl = getGradleProperty(location.toOSString(), WorkspaceConstants.BUNDLE_URL_PROPERTY, "");

		if (bundleUrl.contains("7.0")) {
			return "7.0";
		}
		else if (bundleUrl.contains("7.1")) {
			return "7.1";
		}
		else if (bundleUrl.contains("7.2")) {
			return "7.2";
		}
		else {
			String targetPlatformVersion = getGradleProperty(
				location.toOSString(), WorkspaceConstants.TARGET_PLATFORM_VERSION_PROPERTY, "");

			if (targetPlatformVersion.startsWith("7.0")) {
				return "7.0";
			}
			else if (targetPlatformVersion.startsWith("7.1")) {
				return "7.1";
			}
			else if (targetPlatformVersion.startsWith("7.2")) {
				return "7.2";
			}
		}

		return "";
	}

	public static boolean hasBundlesDir(String location) {
		String bundleHomeDir = getHomeDir(location);

		IPath bundleHomePath = Path.fromOSString(bundleHomeDir);

		if (bundleHomePath.isAbsolute()) {
			if (FileUtil.isDir(bundleHomePath.toFile())) {
				return true;
			}
		}
		else {
			File bundles = new File(location, bundleHomeDir);

			if (FileUtil.isDir(bundles)) {
				return true;
			}
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
			throw new CoreException(LiferayCore.createErrorStatus(multiWorkspaceErrorMsg));
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
			throw new CoreException(LiferayCore.createErrorStatus(multiWorkspaceErrorMsg));
		}

		return false;
	}

	public static boolean hasWorkspace() {
		IProject[] projects = CoreUtil.getAllProjects();

		int count = 0;

		for (IProject project : projects) {
			if (isValidWorkspace(project)) {
				++count;
			}
		}

		if (count > 0) {
			return true;
		}

		return false;
	}

	public static boolean inLiferayWorkspace(IPath location) {
		if (FileUtil.notExists(getWorkspaceProject())) {
			return false;
		}

		IPath workspaceLocation = getWorkspaceProject().getLocation();

		if (workspaceLocation.isPrefixOf(location)) {
			return true;
		}

		return false;
	}

	public static boolean inLiferayWorkspace(IProject project) {
		return inLiferayWorkspace(project.getLocation());
	}

	public static boolean isValidGradleWorkspaceLocation(IPath location) {
		if (FileUtil.notExists(location)) {
			return false;
		}

		return isValidGradleWorkspaceLocation(location.toOSString());
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

		if (Objects.nonNull(settingsContent)) {
			Matcher matcher = _workspacePluginPattern.matcher(settingsContent);

			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

	public static boolean isValidGradleWorkspaceProject(IProject project) {
		if (Objects.isNull(project)) {
			return false;
		}

		return isValidGradleWorkspaceLocation(project.getLocation());
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
		if (Objects.nonNull(project) && Objects.nonNull(project.getLocation()) &&
			isValidWorkspaceLocation(project.getLocation())) {

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
		if (Objects.nonNull(project) && Objects.nonNull(project.getLocation()) &&
			isValidGradleWorkspaceLocation(FileUtil.toOSString(project.getLocation()))) {

			return true;
		}

		return false;
	}

	private static boolean _isValidMavenWorkspace(IProject project) {
		if (Objects.nonNull(project) && Objects.nonNull(project.getLocation()) &&
			isValidMavenWorkspaceLocation(FileUtil.toOSString(project.getLocation()))) {

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