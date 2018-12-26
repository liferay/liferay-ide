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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Dirname;
import org.apache.tools.ant.taskdefs.Property;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.core.XMLMemento;
import org.eclipse.osgi.util.NLS;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class SDK {

	public static final List<String> appServerPropertiesKeys = Collections.unmodifiableList(
		Arrays.asList(
			"app.server.type", "app.server.dir", "app.server.deploy.dir", "app.server.lib.global.dir",
			"app.server.parent.dir", "app.server.portal.dir"));
	public static final Map<String, Map<String, Object>> buildPropertiesCache = new HashMap<>();
	public static final List<String> supportedServerTypes = Collections.unmodifiableList(
		Arrays.asList("tomcat", "jboss", "glassfish", "jetty", "wildfly"));

	public SDK() {
	}

	public SDK(IPath location) {
		this.location = location;
	}

	public void addOrUpdateServerProperties(Map<String, String> appServerPropertiesMap) throws IOException {
		Project project = _getSDKAntProject();

		String[] buildFileNames = {
			"build." + project.getProperty("user.name") + ".properties",
			"build." + project.getProperty("env.COMPUTERNAME") + ".properties",
			project.getProperty("env.HOST") + ".properties", project.getProperty("env.HOSTNAME") + ".properties"
		};

		File buildFile = null;

		for (String name : buildFileNames) {
			IPath buildFilePath = getLocation().append(name);

			if (FileUtil.exists(buildFilePath)) {
				buildFile = buildFilePath.toFile();

				break;
			}
		}

		if (buildFile == null) {
			IPath buildFileLocation = getLocation().append("build." + project.getProperty("user.name") + ".properties");

			buildFile = new File(buildFileLocation.toString());

			buildFile.createNewFile();
		}

		Properties p = new Properties();

		try (InputStream in = Files.newInputStream(buildFile.toPath());
			OutputStream out = Files.newOutputStream(buildFile.toPath())) {

			p.load(in);
			p.put("app.server.parent.dir", appServerPropertiesMap.get("app.server.parent.dir"));

			String bundleType = appServerPropertiesMap.get("app.server.type");

			p.put("app.server.type", bundleType);

			p.put("app.server." + bundleType + ".dir", appServerPropertiesMap.get("app.server.dir"));
			p.put("app.server." + bundleType + ".deploy.dir", appServerPropertiesMap.get("app.server.deploy.dir"));
			p.put(
				"app.server." + bundleType + ".lib.global.dir",
				appServerPropertiesMap.get("app.server.lib.global.dir"));
			p.put("app.server." + bundleType + ".portal.dir", appServerPropertiesMap.get("app.server.portal.dir"));

			p.store(out, "");
		}
	}

	public IStatus buildLanguage(
		IProject project, IFile langFile, Map<String, String> overrideProperties, IProgressMonitor monitor) {

		SDKHelper antHelper = new SDKHelper(this, monitor);

		try {
			Map<String, String> properties = new HashMap<>();

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			properties.put(ISDKConstants.PROPERTY_LANG_DIR, FileUtil.getRawLocationOSString(langFile.getParent()));
			properties.put(ISDKConstants.PROPERTY_LANG_FILE, FileUtil.getLastSegment(langFile.getFullPath(), true));

			IPath buildFile = FileUtil.getRawLocation(project.getFile(ISDKConstants.PROJECT_BUILD_XML));

			String workingDir = _getDefaultWorkingDir(buildFile);

			antHelper.runTarget(buildFile, ISDKConstants.TARGET_BUILD_LANG_CMD, properties, true, workingDir);
		}
		catch (Exception e) {
			return SDKCorePlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus buildService(IProject project, IFile serviceXmlFile, Map<String, String> overrideProperties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = new HashMap<>();

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			String serviceFile = FileUtil.getRawLocationOSString(serviceXmlFile);

			properties.put(ISDKConstants.PROPERTY_SERVICE_FILE, serviceFile);
			properties.put(ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile);

			IPath buildFile = FileUtil.getRawLocation(project.getFile(ISDKConstants.PROJECT_BUILD_XML));

			String workingDir = _getDefaultWorkingDir(buildFile);

			antHelper.runTarget(buildFile, ISDKConstants.TARGET_BUILD_SERVICE, properties, true, workingDir);
		}
		catch (Exception e) {
			return SDKCorePlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus buildWSDD(IProject project, IFile serviceXmlFile, Map<String, String> overrideProperties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = new HashMap<>();

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			String serviceFile = FileUtil.getRawLocationOSString(serviceXmlFile);

			properties.put(ISDKConstants.PROPERTY_SERVICE_FILE, serviceFile);
			properties.put(ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile);

			IPath buildFile = FileUtil.getRawLocation(project.getFile(ISDKConstants.PROJECT_BUILD_XML));

			String workingDir = _getDefaultWorkingDir(buildFile);

			antHelper.runTarget(buildFile, ISDKConstants.TARGET_BUILD_WSDD, properties, true, workingDir);
		}
		catch (Exception e) {
			return SDKCorePlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus cleanAppServer(
		IProject project, String bundleZipLocation, String appServerDir, IProgressMonitor monitor) {

		try {
			Map<String, String> properties = new HashMap<>();

			IPath workPath = new Path(appServerDir).removeLastSegments(2);

			properties.put(ISDKConstants.PROPERTY_APP_ZIP_NAME, bundleZipLocation);
			properties.put(ISDKConstants.PROPERTY_EXT_WORK_DIR, workPath.toOSString());

			IStatus status = runTarget(project, properties, "clean-app-server", true, monitor);

			if (!status.isOK()) {
				return status;
			}
		}
		catch (Exception ex) {
			return SDKCorePlugin.createErrorStatus(ex);
		}

		return Status.OK_STATUS;
	}

	public IStatus compileThemePlugin(IProject project, Map<String, String> overrideProperties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = new HashMap<>();

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			IPath buildFile = FileUtil.getRawLocation(project.getFile(ISDKConstants.PROJECT_BUILD_XML));

			String workingDir = _getDefaultWorkingDir(buildFile);

			antHelper.runTarget(buildFile, ISDKConstants.TARGET_COMPILE, properties, true, workingDir);
		}
		catch (Exception e) {
			return SDKCorePlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public SDK copy() {
		SDK copy = new SDK(getLocation());

		copy.setContributed(isContributed());
		copy.setDefault(isDefault());
		copy.setName(getName());
		copy.setVersion(getVersion());

		return copy;
	}

	public IPath createNewExtProject(
		String extName, String extDisplayName, boolean separateJRE, String workingDir, String baseDir,
		IProgressMonitor monitor) {

		try {
			SDKHelper antHelper = new SDKHelper(this, monitor);

			Map<String, String> properties = new HashMap<>();

			properties.put(ISDKConstants.PROPERTY_EXT_NAME, extName);
			properties.put(ISDKConstants.PROPERTY_EXT_DISPLAY_NAME, extDisplayName);

			// create a space for new portlet template to get built

			IPath tempPath = FileUtil.pathAppend(
				_sdkPluginLocation, ISDKConstants.TARGET_CREATE, String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_EXT_PARENT_DIR, tempPath.toOSString());

			if (baseDir != null) {
				properties.put("plugin.type.dir", baseDir);
			}

			antHelper.runTarget(
				getLocation().append(ISDKConstants.EXT_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties,
				separateJRE, workingDir);

			return tempPath;
		}
		catch (Exception e) {
			SDKCorePlugin.logError(e);
		}

		return null;
	}

	public IPath createNewHookProject(
		String hookName, String hookDisplayName, boolean separateJRE, String workingDir, String baseDir,
		IProgressMonitor monitor) {

		SDKHelper antHelper = new SDKHelper(this, monitor);

		try {
			Map<String, String> properties = new HashMap<>();

			properties.put(ISDKConstants.PROPERTY_HOOK_NAME, hookName);
			properties.put(ISDKConstants.PROPERTY_HOOK_DISPLAY_NAME, hookDisplayName);

			// create a space for new portlet template to get built

			IPath newHookPath = FileUtil.pathAppend(
				_sdkPluginLocation, ISDKConstants.TARGET_CREATE, String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_HOOK_PARENT_DIR, newHookPath.toOSString());

			IPath buildLocation = getLocation().append(ISDKConstants.HOOK_PLUGIN_ANT_BUILD);

			if (baseDir != null) {
				properties.put("plugin.type.dir", baseDir);
			}

			antHelper.runTarget(buildLocation, ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir);

			return newHookPath;
		}
		catch (Exception e) {
			SDKCorePlugin.logError(e);
		}

		return null;
	}

	public IPath createNewLayoutTplProject(
		String layoutTplName, String layoutTplDisplayName, boolean separateJRE, String workingDir, String baseDir,
		IProgressMonitor monitor) {

		SDKHelper antHelper = new SDKHelper(this, monitor);

		try {
			Map<String, String> properties = new HashMap<>();

			properties.put(ISDKConstants.PROPERTY_LAYOUTTPL_NAME, layoutTplName);
			properties.put(ISDKConstants.PROPERTY_LAYOUTTPL_DISPLAY_NAME, layoutTplDisplayName);

			// create a space for new layouttpm template to get built

			IPath newLayoutTplPath = FileUtil.pathAppend(
				_sdkPluginLocation, ISDKConstants.TARGET_CREATE, String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_LAYOUTTPL_PARENT_DIR, newLayoutTplPath.toOSString());

			if (baseDir != null) {
				properties.put("plugin.type.dir", baseDir);
			}

			antHelper.runTarget(
				getLocation().append(ISDKConstants.LAYOUTTPL_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties,
				separateJRE, workingDir);

			return newLayoutTplPath;
		}
		catch (Exception e) {
			SDKCorePlugin.logError(e);
		}

		return null;
	}

	public IPath createNewPortletProject(
		String portletName, String portletDisplayName, String portletFramework, boolean separateJRE, String workingDir,
		String baseDir, IProgressMonitor monitor) {

		SDKHelper antHelper = new SDKHelper(this, monitor);

		try {
			Map<String, String> properties = new HashMap<>();

			properties.put(ISDKConstants.PROPERTY_PORTLET_NAME, portletName);
			properties.put(ISDKConstants.PROPERTY_PORTLET_DISPLAY_NAME, portletDisplayName);
			properties.put(ISDKConstants.PROPERTY_PORTLET_FRAMEWORK, portletFramework);

			// create a space for new portlet template to get built

			IPath newPortletPath = FileUtil.pathAppend(
				_sdkPluginLocation, ISDKConstants.TARGET_CREATE, String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_PORTLET_PARENT_DIR, newPortletPath.toOSString());

			if (baseDir != null) {
				properties.put("plugin.type.dir", baseDir);
			}

			antHelper.runTarget(
				getLocation().append(ISDKConstants.PORTLET_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties,
				separateJRE, workingDir);

			return newPortletPath;
		}
		catch (Exception e) {
			SDKCorePlugin.logError(e);
		}

		return null;
	}

	public IPath createNewProject(
			String projectName, ArrayList<String> arguments, String type, String workingDir, IProgressMonitor monitor)
		throws CoreException {

		CreateHelper createHelper = new CreateHelper(this, monitor);

		IPath pluginFolder = getLocation().append(getPluginFolder(type));

		IPath newPath = pluginFolder.append(projectName + _getPluginSuffix(type));

		String createScript = ISDKConstants.CREATE_BAT;

		if (!CoreUtil.isWindows()) {
			createScript = ISDKConstants.CREATE_SH;
		}

		IPath createFilePath = pluginFolder.append(createScript);

		File createFile = createFilePath.toFile();

		String originalCreateConetent = "";

		if (!CoreUtil.isWindows() && createFile.exists()) {
			originalCreateConetent = FileUtil.readContents(createFile, true);

			if (originalCreateConetent.contains("DisplayName=\\\"$2\\\"")) {
				String createContent = originalCreateConetent.replace("DisplayName=\\\"$2\\\"", "DisplayName=\"$2\"");

				try (InputStream input = new ByteArrayInputStream(createContent.getBytes("UTF-8"))) {
					FileUtil.writeFile(createFile, input, null);
				}
				catch (Exception e) {
					SDKCorePlugin.logError(e);
				}
			}
		}

		createHelper.runTarget(createFilePath, arguments, workingDir);

		if (FileUtil.notExists(newPath)) {
			throw new CoreException(SDKCorePlugin.createErrorStatus("Create script did not complete successfully."));
		}

		if (!CoreUtil.isNullOrEmpty(originalCreateConetent)) {
			try (InputStream input = new ByteArrayInputStream(originalCreateConetent.getBytes("UTF-8"))) {
				FileUtil.writeFile(createFile, input, null);
			}
			catch (Exception e) {
				SDKCorePlugin.logError(e);
			}
		}

		return newPath;
	}

	public IPath createNewThemeProject(
		String themeName, String themeDisplayName, boolean separateJRE, String workingDir, String baseDir,
		IProgressMonitor monitor) {

		SDKHelper antHelper = new SDKHelper(this, monitor);

		try {
			Map<String, String> properties = new HashMap<>();

			properties.put(ISDKConstants.PROPERTY_THEME_NAME, themeName);
			properties.put(ISDKConstants.PROPERTY_THEME_DISPLAY_NAME, themeDisplayName);

			// create a space for new portlet template to get built

			IPath tempPath = FileUtil.pathAppend(
				_sdkPluginLocation, ISDKConstants.TARGET_CREATE, String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_THEME_PARENT_DIR, tempPath.toOSString());

			if (baseDir != null) {
				properties.put("plugin.type.dir", baseDir);
			}

			antHelper.runTarget(
				getLocation().append(ISDKConstants.THEME_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties,
				separateJRE, workingDir);

			return tempPath;
		}
		catch (CoreException ce) {
			SDKCorePlugin.logError(ce);
		}

		return null;
	}

	public IPath createNewWebProject(
		String webName, String webDisplayName, boolean separateJRE, String workingDir, String baseDir,
		IProgressMonitor monitor) {

		SDKHelper antHelper = new SDKHelper(this, monitor);

		try {
			Map<String, String> properties = new HashMap<>();

			properties.put(ISDKConstants.PROPERTY_WEB_NAME, webName);
			properties.put(ISDKConstants.PROPERTY_WEB_DISPLAY_NAME, webDisplayName);

			// create a space for new web template to get built

			IPath newWebPath = FileUtil.pathAppend(
				_sdkPluginLocation, ISDKConstants.TARGET_CREATE, String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_WEB_PARENT_DIR, newWebPath.toOSString());

			IPath buildLocation = getLocation().append(ISDKConstants.WEB_PLUGIN_ANT_BUILD);

			if (baseDir != null) {
				properties.put("plugin.type.dir", baseDir);
			}

			antHelper.runTarget(buildLocation, ISDKConstants.TARGET_CREATE, properties, separateJRE, workingDir);

			return newWebPath;
		}
		catch (Exception e) {
			SDKCorePlugin.logError(e);
		}

		return null;
	}

	public String createXMLNameValuePair(String name, String value) {
		return name + "=\"" + value + "\" ";
	}

	public IStatus directDeploy(
		IProject project, Map<String, String> overrideProperties, boolean separateJRE, IProgressMonitor monitor) {

		try {
			SDKHelper antHelper = new SDKHelper(this, monitor);

			Map<String, String> properties = new HashMap<>();

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			IPath buildFile = FileUtil.getRawLocation(project.getFile(ISDKConstants.PROJECT_BUILD_XML));

			String workingDir = _getDefaultWorkingDir(buildFile);

			antHelper.runTarget(buildFile, ISDKConstants.TARGET_DIRECT_DEPLOY, properties, separateJRE, workingDir);
		}
		catch (Exception e) {
			return SDKCorePlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IPath[] getAntLibraries() {
		List<IPath> antLibs = new ArrayList<>();

		for (String antLib : ISDKConstants.ANT_LIBRARIES) {
			IPath antLibPath = getLocation().append(antLib);

			if (FileUtil.exists(antLibPath)) {
				antLibs.add(antLibPath);
			}
		}

		return antLibs.toArray(new IPath[0]);
	}

	public Map<String, Object> getBuildProperties() throws CoreException {
		return getBuildProperties(false);
	}

	public Map<String, Object> getBuildProperties(boolean reload) throws CoreException {
		Project project = _getSDKAntProject();

		Map<String, Object> sdkProperties = buildPropertiesCache.get(getLocation().toPortableString());

		try {
			if ((sdkProperties == null) || reload) {
				if (sdkProperties != null) {
					sdkProperties.clear();
				}

				_loadPropertiesFile(project, "build." + project.getProperty("user.name") + ".properties");
				_loadPropertiesFile(project, "build." + project.getProperty("env.COMPUTERNAME") + ".properties");
				_loadPropertiesFile(project, "build." + project.getProperty("env.HOST") + ".properties");
				_loadPropertiesFile(project, "build." + project.getProperty("env.HOSTNAME") + ".properties");
				_loadPropertiesFile(project, "build.properties");

				if (project.getProperty("app.server.type") == null) {
					throw new CoreException(
						SDKCorePlugin.createErrorStatus(
							"Missing ${app.server.type} setting in build.properties file."));
				}

				Map<String, String> propertyCopyList = new HashMap<>();

				propertyCopyList.put(
					"app.server." + project.getProperty("app.server.type") +
						".dir",
					"app.server.dir");
				propertyCopyList.put(
					"app.server." + project.getProperty("app.server.type") +
						".deploy.dir",
					"app.server.deploy.dir");
				propertyCopyList.put(
					"app.server." + project.getProperty("app.server.type") +
						".lib.global.dir",
					"app.server.lib.global.dir");
				propertyCopyList.put(
					"app.server." + project.getProperty("app.server.type") +
						".portal.dir",
					"app.server.portal.dir");

				for (String key : propertyCopyList.keySet()) {
					AntPropertyCopy propertyCopyTask = new AntPropertyCopy();

					propertyCopyTask.setOverride(true);
					propertyCopyTask.setProject(project);

					String from = key;

					String to = propertyCopyList.get(from);
					propertyCopyTask.setFrom(from);

					propertyCopyTask.setName(to);
					propertyCopyTask.execute();
				}

				sdkProperties = project.getProperties();

				for (String propertyKey : appServerPropertiesKeys) {
					if (ListUtil.notContains(sdkProperties.keySet(), propertyKey)) {
						throw new CoreException(
							SDKCorePlugin.createErrorStatus(
								"Missing ${" + propertyKey + "} setting in build.properties file."));
					}
				}

				buildPropertiesCache.put(getLocation().toPortableString(), sdkProperties);
			}
		}
		catch (Exception e) {
			throw new CoreException(SDKCorePlugin.createErrorStatus(e.getMessage()));
		}

		return sdkProperties;
	}

	public IPath[] getDependencyJarPaths() {
		List<IPath> retval = new ArrayList<>();

		try {
			IPath sdkLibPath = getLocation().append("dependencies");

			int compareVersions = CoreUtil.compareVersions(Version.parseVersion(getVersion()), ILiferayConstants.V700);

			if (FileUtil.exists(sdkLibPath) && (compareVersions >= 0)) {
				List<File> libFiles = FileListing.getFileListing(new File(sdkLibPath.toOSString()));

				for (File lib : libFiles) {
					if (lib.exists() && StringUtil.endsWith(lib.getName(), ".jar")) {
						retval.add(new Path(lib.getPath()));
					}
				}
			}
		}
		catch (FileNotFoundException fnfe) {
		}

		return retval.toArray(new IPath[0]);
	}

	public IPath getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getPluginFolder(String type) {
		if ("ext".equals(type)) {
			return ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER;
		}
		else if ("portlet".equals(type)) {
			return ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER;
		}
		else if ("hook".equals(type)) {
			return ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER;
		}
		else if ("layouttpl".equals(type)) {
			return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER;
		}
		else if ("theme".equals(type)) {
			return ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER;
		}
		else if ("web".equals(type)) {
			return ISDKConstants.WEB_PLUGIN_PROJECT_FOLDER;
		}
		else {
			return "";
		}
	}

	public IPath getPortletTemplatePath() {
		return getLocation().append(ISDKConstants.PORTLET_PLUGIN_ZIP_PATH);
	}

	public String getVersion() {
		if (version != null) {
			return version;
		}

		IPath sdkLocation = getLocation().makeAbsolute();

		if (!sdkLocation.isEmpty()) {
			try {
				version = SDKUtil.readSDKVersion(sdkLocation.toOSString());

				if (version != null) {
					if (version.equals(ILiferayConstants.V611.toString())) {
						Properties buildProperties = _getProperties(sdkLocation.append("build.properties"));

						if (_hasAppServerSpecificProps(buildProperties)) {
							version = ILiferayConstants.V612.toString();
						}
					}

					if (version.equals(ILiferayConstants.V6120.toString())) {
						Properties buildProperties = _getProperties(sdkLocation.append("build.properties"));

						if (_hasAppServerSpecificProps(buildProperties)) {
							version = ILiferayConstants.V6130.toString();
						}
					}
				}
			}
			catch (Exception e) {
				SDKCorePlugin.logError("Could not detect the sdk version.", e);
			}
		}

		return version;
	}

	public boolean hasProjectFile() {
		if ((location != null) && FileUtil.exists(location.append(".project"))) {
			return true;
		}

		return false;
	}

	public boolean isContributed() {
		return contributed;
	}

	public boolean isDefault() {
		return defaultSDK;
	}

	public boolean isValid() {
		IPath sdkLocation = getLocation();

		if (sdkLocation == null) {
			return false;
		}

		if (!SDKUtil.isSDKSupported(sdkLocation.toOSString())) {
			return false;
		}

		if (!SDKUtil.isValidSDKLocation(sdkLocation)) {
			return false;
		}

		return true;
	}

	public void loadFromMemento(XMLMemento sdkElement) {
		setName(sdkElement.getString("name"));
		setLocation(Path.fromPortableString(sdkElement.getString("location")));
	}

	public IStatus runCommand(
		IProject project, IFile buildXmlFile, String command, Map<String, String> overrideProperties,
		IProgressMonitor monitor) {

		SDKHelper antHelper = new SDKHelper(this, monitor);

		try {
			Map<String, String> properties = new HashMap<>();

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			IPath buildFile = buildXmlFile.getRawLocation();

			String workingDir = _getDefaultWorkingDir(buildFile);

			antHelper.setVMArgs(_getAntHomeVMArg());

			antHelper.runTarget(buildFile, command, properties, true, workingDir);
		}
		catch (Exception e) {
			return SDKCorePlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public void saveToMemento(XMLMemento child) {
		child.putString("name", getName());
		child.putString("location", getLocation().toPortableString());
		child.putString("version", getVersion());
	}

	public void setContributed(boolean contributed) {
		this.contributed = contributed;
	}

	public void setDefault(boolean defaultSDK) {
		this.defaultSDK = defaultSDK;
	}

	public void setLocation(IPath location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return getName() + (isDefault() ? " [default]" : "");
	}

	public String toXmlString() {
		StringBuilder builder = new StringBuilder();

		builder.append("<sdk ");
		builder.append(createXMLNameValuePair("name", getName()));
		builder.append(createXMLNameValuePair("location", getLocation().toPortableString()));
		builder.append(createXMLNameValuePair("version", getVersion()));
		builder.append("/>");

		return builder.toString();
	}

	public IStatus validate() {
		return validate(false);
	}

	public IStatus validate(boolean reload) {
		MultiStatus status = new MultiStatus(SDKCorePlugin.PLUGIN_ID, IStatus.OK, "", null);

		boolean validLocation = SDKUtil.isValidSDKLocation(getLocation());

		IPath buildXmlLocation = getLocation().append("build.xml");

		if (!validLocation) {
			status.add(SDKCorePlugin.createErrorStatus(Msgs.sdkLocationInvalid));

			return status;
		}

		if (FileUtil.notExists(buildXmlLocation)) {
			status.add(SDKCorePlugin.createErrorStatus(Msgs.buildXmlFileNotExist));

			return status;
		}

		Map<String, Object> sdkProperties = null;

		try {
			sdkProperties = getBuildProperties(reload);

			if (sdkProperties == null) {
				status.add(SDKCorePlugin.createErrorStatus("Could not find any sdk settings."));

				return status;
			}
		}
		catch (Exception e) {
			status.add(SDKCorePlugin.createErrorStatus(e.getMessage()));

			return status;
		}

		for (String propertyKey : appServerPropertiesKeys) {
			if (!status.isOK()) {

				// stop after finding the first invalid property key

				break;
			}

			String propertyValue = (String)sdkProperties.get(propertyKey);

			if (propertyValue == null) {
				status.add(SDKCorePlugin.createErrorStatus(propertyKey + " is null."));
			}
			else {
				switch (propertyKey) {
					case "app.server.type":
						if (!supportedServerTypes.contains(propertyValue)) {
							status.add(
								SDKCorePlugin.createErrorStatus(
									"The " + propertyKey + "(" + propertyValue +
										") server is not supported by Liferay IDE."));
						}

						break;

					case "app.server.dir":
					case "app.server.deploy.dir":
					case "app.server.lib.global.dir":
					case "app.server.parent.dir":
					case "app.server.portal.dir":
						IPath propertyPath = new Path(propertyValue);

						if (FileUtil.notExists(propertyPath)) {
							String errorMessage = new String(
								propertyKey + " is invalid. Please reconfigure Plugins SDK setting: " + propertyKey +
									"=" + propertyValue);

							status.add(SDKCorePlugin.createErrorStatus(SDKCorePlugin.PLUGIN_ID, errorMessage));
						}

						break;

					default:
				}
			}
		}

		return status;
	}

	public IStatus war(
		IProject project, Map<String, String> overrideProperties, boolean separateJRE, IProgressMonitor monitor) {

		return war(project, overrideProperties, separateJRE, null, monitor);
	}

	public IStatus war(
		IProject project, Map<String, String> overrideProperties, boolean separateJRE, String[] vmargs,
		IProgressMonitor monitor) {

		try {
			SDKHelper antHelper = new SDKHelper(this, monitor);

			antHelper.setVMArgs(vmargs);

			Map<String, String> properties = new HashMap<>();

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			IPath buildFile = FileUtil.getRawLocation(project.getFile(ISDKConstants.PROJECT_BUILD_XML));

			String workingDir = _getDefaultWorkingDir(buildFile);

			antHelper.runTarget(buildFile, ISDKConstants.TARGET_WAR, properties, separateJRE, workingDir);
		}
		catch (Exception e) {
			return SDKCorePlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	protected IEclipsePreferences getPrefStore() {
		return InstanceScope.INSTANCE.getNode(SDKCorePlugin.PREFERENCE_ID);
	}

	protected IStatus runTarget(
		IProject project, Map<String, String> properties, String target, boolean separateJRE,
		IProgressMonitor monitor) {

		SDKHelper antHelper = new SDKHelper(this, monitor);

		try {
			IFile file = project.getFile(ISDKConstants.PROJECT_BUILD_XML);

			IPath buildFile = file.getRawLocation();

			String workingDir = _getDefaultWorkingDir(buildFile);

			antHelper.runTarget(buildFile, target, properties, separateJRE, workingDir);
		}
		catch (CoreException ce) {
			return SDKCorePlugin.createErrorStatus(ce);
		}

		return Status.OK_STATUS;
	}

	protected boolean contributed;
	protected boolean defaultSDK;
	protected IPath location;
	protected String name;
	protected String version;

	private String[] _getAntHomeVMArg() {
		AntCorePlugin antCorePlugin = AntCorePlugin.getPlugin();

		AntCorePreferences prefs = antCorePlugin.getPreferences();

		String antHome = prefs.getAntHome();

		if (!CoreUtil.isNullOrEmpty(antHome)) {
			return new String[] {"-Dant.home=\"" + antHome + "\""};
		}

		return null;
	}

	private String _getDefaultWorkingDir(IPath buildFile) {
		return FileUtil.toOSString(buildFile.removeLastSegments(1));
	}

	private String _getPluginSuffix(String type) {
		if ("ext".equals(type)) {
			return ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
		}
		else if ("portlet".equals(type)) {
			return ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
		}
		else if ("hook".equals(type)) {
			return ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
		}
		else if ("layouttpl".equals(type)) {
			return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
		}
		else if ("theme".equals(type)) {
			return ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
		}
		else if ("web".equals(type)) {
			return ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX;
		}
		else {
			return "";
		}
	}

	private Properties _getProperties(File file) {
		Properties properties = new Properties();

		try (InputStream propertiesInput = Files.newInputStream(file.toPath())) {
			properties.load(propertiesInput);
		}
		catch (Exception e) {
			SDKCorePlugin.logError(e);
		}

		return properties;
	}

	private Properties _getProperties(IPath location) {
		return _getProperties(location.toFile());
	}

	private Project _getSDKAntProject() {
		Project project = new Project();

		project.setBaseDir(new File(getLocation().toPortableString()));
		project.setSystemProperties();

		Dirname dirname = new Dirname();
		IPath buildFileLocation = getLocation().append("build-common.xml");

		dirname.setProject(project);
		dirname.setFile(new File(buildFileLocation.toPortableString()));
		dirname.setProperty("sdk.dir");
		dirname.execute();

		Property envTask = new Property();

		envTask.setProject(project);
		envTask.setEnvironment("env");
		envTask.execute();

		return project;
	}

	private boolean _hasAppServerSpecificProps(Properties props) {
		Enumeration<?> names = props.propertyNames();

		while (names.hasMoreElements()) {
			Object o = names.nextElement();

			String name = o.toString();

			if (name.matches("app.server.tomcat.*")) {
				return true;
			}
		}

		return false;
	}

	private void _loadPropertiesFile(Project project, String fileName) {
		if ((project != null) && (fileName != null)) {
			IPath propertiesFileLocation = getLocation().append(fileName);

			File propertiesFile = new File(propertiesFileLocation.toPortableString());

			if (propertiesFile.exists()) {
				Property loadPropetiesTask = new Property();

				loadPropetiesTask.setProject(project);
				loadPropetiesTask.setFile(propertiesFile);
				loadPropetiesTask.execute();
			}
		}
	}

	private IPath _sdkPluginLocation = SDKCorePlugin.getDefault().getStateLocation();

	private static class Msgs extends NLS {

		public static String buildXmlFileNotExist;
		public static String sdkLocationInvalid;

		static {
			initializeMessages(SDK.class.getName(), Msgs.class);
		}

	}

}