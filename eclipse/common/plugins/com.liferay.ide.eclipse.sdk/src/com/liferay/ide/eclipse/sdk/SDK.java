/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.sdk;

import com.liferay.ide.eclipse.sdk.util.SDKHelper;
import com.liferay.ide.eclipse.sdk.util.SDKUtil;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;

/**
 * @author Greg Amerson
 */
public class SDK {

	private static final String MSG_MANAGED_BY_LIFERAY_IDE =
		"Managed by Liferay IDE (remove this comment to prevent future updates)";

	public static String createXMLNameValuePair(String name, String value) {
		return name + "=\"" + value + "\" ";
	}

	protected static IPreferenceStore getPrefStore() {
		return SDKPlugin.getDefault().getPreferenceStore();
	}

	protected boolean contributed;

	protected boolean defaultSDK;

	protected IPath location;

	// private String runtime;

	protected String name;

	protected String version;

	public SDK() {
	}

	public SDK(IPath location) {
		this.location = location;
	}

	public IStatus buildLanguage(IProject project, IFile langFile, Map<String, String> overrideProperties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = configureAppServerProperties(project);

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			String langDirLocation = langFile.getParent().getRawLocation().toOSString();

			String langFileName = langFile.getFullPath().removeFileExtension().lastSegment();

			properties.put(ISDKConstants.PROPERTY_LANG_DIR, langDirLocation);
			properties.put(ISDKConstants.PROPERTY_LANG_FILE, langFileName);

			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_BUILD_LANG_CMD,
				properties, true);
		}
		catch (Exception e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus buildService(IProject project, IFile serviceXmlFile, Map<String, String> overrideProperties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = configureAppServerProperties(project);

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			String serviceFile = serviceXmlFile.getRawLocation().toOSString();

			properties.put(ISDKConstants.PROPERTY_SERVICE_FILE, serviceXmlFile.getRawLocation().toOSString());
			properties.put(ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile);

			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_BUILD_SERVICE,
				properties, true);
		}
		catch (Exception e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus buildWSDD(IProject project, IFile serviceXmlFile, Map<String, String> overrideProperties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = configureAppServerProperties(project);

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			String serviceFile = serviceXmlFile.getRawLocation().toOSString();

			properties.put(ISDKConstants.PROPERTY_SERVICE_FILE, serviceXmlFile.getRawLocation().toOSString());
			properties.put(ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile);

			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_BUILD_WSDD,
				properties, true);
		}
		catch (Exception e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus cleanAppServer(IProject project, IPath bundleZipLocation) {
		try {
			Map<String, String> properties = configureAppServerProperties(project);

			String appServerDir = properties.get(ISDKConstants.PROPERTY_APP_SERVER_DIR);
			IPath workPath = new Path(appServerDir).removeLastSegments(2);

			properties.put(ISDKConstants.PROPERTY_APP_ZIP_NAME, bundleZipLocation.toOSString());
			properties.put(ISDKConstants.PROPERTY_EXT_WORK_DIR, workPath.toOSString());

			IStatus status = runTarget(project, properties, "clean-app-server", true);

			if (!status.isOK()) {
				return status;
			}
		}
		catch (Exception ex) {
			return SDKPlugin.createErrorStatus(ex);
		}

		return Status.OK_STATUS;
	}

	public IStatus compileThemePlugin(IProject project, Map<String, String> overrideProperties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = configureAppServerProperties(project);

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_COMPILE,
				properties, true);
		}
		catch (CoreException e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IPath createNewExtProject(String extName, String extDisplayName, ILiferayRuntime appServer) {
		try {
			SDKHelper antHelper = new SDKHelper(this);

			Map<String, String> properties = configureAppServerProperties(appServer);

			properties.put(ISDKConstants.PROPERTY_EXT_NAME, extName);
			properties.put(ISDKConstants.PROPERTY_EXT_DISPLAY_NAME, extDisplayName);

			// create a space for new portlet template to get built
			IPath tempPath =
				SDKPlugin.getDefault().getStateLocation().append(ISDKConstants.TARGET_CREATE).append(
					String.valueOf(System.currentTimeMillis()));
			// if (!newPortletPath.toFile().mkdirs()) {
			// throw new
			// CoreException(SDKPlugin.createErrorStatus("Unable to create directory in state location"));
			// }

			properties.put(ISDKConstants.PROPERTY_EXT_PARENT_DIR, tempPath.toOSString());

			antHelper.runTarget(
				getLocation().append(ISDKConstants.EXT_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties);

			return tempPath;
		}
		catch (CoreException e) {
			SDKPlugin.logError(e);
		}

		return null;
	}

	public IPath createNewHookProject(String hookName, String hookDisplayName) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ISDKConstants.PROPERTY_HOOK_NAME, hookName);
			properties.put(ISDKConstants.PROPERTY_HOOK_DISPLAY_NAME, hookDisplayName);

			// create a space for new portlet template to get built
			IPath newHookPath =
				SDKPlugin.getDefault().getStateLocation().append(ISDKConstants.TARGET_CREATE).append(
					String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_HOOK_PARENT_DIR, newHookPath.toOSString());

			antHelper.runTarget(
				getLocation().append(ISDKConstants.HOOK_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties);

			return newHookPath;
		}
		catch (CoreException e) {
			SDKPlugin.logError(e);
		}

		return null;
	}

	public IPath createNewLayoutTplProject(String layoutTplName, String layoutTplDisplayName, ILiferayRuntime runtime) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = configureAppServerProperties(runtime);

			properties.put(ISDKConstants.PROPERTY_LAYOUTTPL_NAME, layoutTplName);
			properties.put(ISDKConstants.PROPERTY_LAYOUTTPL_DISPLAY_NAME, layoutTplDisplayName);

			// create a space for new layouttpm template to get built
			IPath newLayoutTplPath =
				SDKPlugin.getDefault().getStateLocation().append(ISDKConstants.TARGET_CREATE).append(
					String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_LAYOUTTPL_PARENT_DIR, newLayoutTplPath.toOSString());

			antHelper.runTarget(
				getLocation().append(ISDKConstants.LAYOUTTPL_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties);

			return newLayoutTplPath;
		}
		catch (CoreException e) {
			SDKPlugin.logError(e);
		}

		return null;
	}

	public IPath createNewPortletProject(String portletName, String portletDisplayName, ILiferayRuntime runtime) {
		return createNewPortletProject(portletName, portletDisplayName, null, runtime);
	}

	public IPath createNewPortletProject(
		String portletName, String portletDisplayName, String portletFramework, ILiferayRuntime runtime) {

		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = configureAppServerProperties(runtime);

			properties.put(ISDKConstants.PROPERTY_PORTLET_NAME, portletName);
			properties.put(ISDKConstants.PROPERTY_PORTLET_DISPLAY_NAME, portletDisplayName);
			properties.put(ISDKConstants.PROPERTY_PORTLET_FRAMEWORK, portletFramework);

			// create a space for new portlet template to get built
			IPath newPortletPath =
				SDKPlugin.getDefault().getStateLocation().append(ISDKConstants.TARGET_CREATE).append(
					String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_PORTLET_PARENT_DIR, newPortletPath.toOSString());

			antHelper.runTarget(
				getLocation().append(ISDKConstants.PORTLET_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties);

			return newPortletPath;
		}
		catch (CoreException e) {
			SDKPlugin.logError(e);
		}

		return null;
	}

	public IPath createNewThemeProject(String themeName, String themeDisplayName) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ISDKConstants.PROPERTY_THEME_NAME, themeName);
			properties.put(ISDKConstants.PROPERTY_THEME_DISPLAY_NAME, themeDisplayName);

			// create a space for new portlet template to get built
			IPath tempPath =
				SDKPlugin.getDefault().getStateLocation().append(ISDKConstants.TARGET_CREATE).append(
					String.valueOf(System.currentTimeMillis()));

			properties.put(ISDKConstants.PROPERTY_THEME_PARENT_DIR, tempPath.toOSString());

			antHelper.runTarget(
				getLocation().append(ISDKConstants.THEME_PLUGIN_ANT_BUILD), ISDKConstants.TARGET_CREATE, properties);

			return tempPath;
		}
		catch (CoreException e) {
			SDKPlugin.logError(e);
		}

		return null;
	}

	public IStatus directDeploy(IProject project, Map<String, String> overrideProperties, boolean separateJRE) {
		try {
			SDKHelper antHelper = new SDKHelper(this);

			Map<String, String> properties = configureAppServerProperties(project);

			if (overrideProperties != null) {
				properties.putAll(overrideProperties);
			}

			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_DIRECT_DEPLOY,
				properties, separateJRE);
		}
		catch (CoreException e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;

	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SDK && getName() != null && getName().equals(((SDK) obj).getName()) &&
			getLocation() != null && getLocation().equals(((SDK) obj).getLocation());
	}

	public IPath[] getAntLibraries() {
		List<IPath> antLibs = new ArrayList<IPath>();

		for (String antLib : ISDKConstants.ANT_LIBRARIES) {
			antLibs.add(getLocation().append(antLib));
		}

		return antLibs.toArray(new IPath[0]);
	}

	public IPath getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public IPath getPortletTemplatePath() {
		return getLocation().append(ISDKConstants.PORTLET_PLUGIN_ZIP_PATH);
	}

	public String getVersion() {
		return version;
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

		if (!SDKUtil.isValidSDKLocation(sdkLocation.toOSString())) {
			return false;
		}

		return true;
	}

	public void loadFromMemento(IMemento sdkElement) {
		setName(sdkElement.getString("name"));
		setLocation(Path.fromPortableString(sdkElement.getString("location")));
		setVersion(sdkElement.getString("version"));
		// setRuntime(sdkElement.getString("runtime"));
	}

	public void saveToMemento(IMemento child) {
		child.putString("name", getName());
		child.putString("location", getLocation().toPortableString());
		child.putString("version", getVersion());
		// child.putString("runtime", getRuntime() != null ? getRuntime() : "");
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
		return this.getName();
	}

	public String toXmlString() {
		StringBuilder builder = new StringBuilder();

		builder.append("<sdk ");
		builder.append(createXMLNameValuePair("name", getName()));
		builder.append(createXMLNameValuePair("location", getLocation().toPortableString()));
		// builder.append(createXMLNameValuePair("runtime", getRuntime() != null
		// ? getRuntime() : ""));
		builder.append(createXMLNameValuePair("version", getVersion()));
		builder.append("/>");

		return builder.toString();
	}

	public IStatus validate() {
		boolean validLocation = SDKUtil.isValidSDKLocation(getLocation().toOSString());

		boolean buildXmlExists = getLocation().append("build.xml").toFile().exists();

		if (!validLocation) {
			return SDKPlugin.createErrorStatus("SDK location is invalid.");
		}

		if (!buildXmlExists) {
			return SDKPlugin.createErrorStatus("build.xml file does not exist.");
		}

		return Status.OK_STATUS;
	}

	private boolean promptForOverwrite(final File userBuildFile) {
		final boolean[] retval = new boolean[1];

		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				String message =
					MessageFormat.format(
						"The user build.properties file in \"{0}\" SDK has not been updated with the latest app server build properties.  Should these properties be written to the file \"{1}\"?\n\nAnswering no will pass these properties directly to Ant and disregard any settings in the user build.properties file.",
						new Object[] { getName(), userBuildFile.getName() });

				MessageDialogWithToggle dialog =
					MessageDialogWithToggle.openYesNoQuestion(
						Display.getDefault().getActiveShell(), "Plugins SDK", message,
						"Remember this answer for future SDK operations.", false, getPrefStore(),
						SDKPlugin.PREF_KEY_OVERWRITE_USER_BUILD_FILE);

				retval[0] = dialog.getReturnCode() == IDialogConstants.YES_ID;
			}
		});

		return retval[0];
	}

	protected Map<String, String> configureAppServerProperties(ILiferayRuntime appServer) {
		Map<String, String> properties = new HashMap<String, String>();

		String type = appServer.getAppServerType();

		String dir = appServer.getAppServerDir().toOSString();

		String deployDir = appServer.getDeployDir().toOSString();

		String libGlobalDir = appServer.getLibGlobalDir().toOSString();

		String portalDir = appServer.getPortalDir().toOSString();

		properties.put(ISDKConstants.PROPERTY_APP_SERVER_TYPE, type);
		properties.put(ISDKConstants.PROPERTY_APP_SERVER_DIR, dir);
		properties.put(ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, deployDir);
		properties.put(ISDKConstants.PROPERTY_APP_SERVER_LIB_GLOBAL_DIR, libGlobalDir);
		properties.put(ISDKConstants.PROPERTY_APP_SERVER_PORTAL_DIR, portalDir);

		try {
			persistAppServerProperties(properties);
		}
		catch (Exception e) {
			SDKPlugin.logError(e);
		}

		return properties;
	}

	protected Map<String, String> configureAppServerProperties(IProject project)
		throws CoreException {

		ILiferayRuntime runtime = null;

		try {
			runtime = ServerUtil.getLiferayRuntime(project);
		}
		catch (CoreException e1) {
			throw new CoreException(SDKPlugin.createErrorStatus(e1));
		}

		return configureAppServerProperties(runtime);
	}

	protected void persistAppServerProperties(Map<String, String> properties)
		throws FileNotFoundException, IOException, ConfigurationException {

		IPath loc = getLocation();

		// check for build.<username>.properties

		String userName = System.getProperty("user.name");

		File userBuildFile = loc.append("build." + userName + ".properties").toFile();

		if (userBuildFile.exists()) {
			/*
			 * the build file exists so we need to check the following conditions 1. if the header in the comment
			 * contains the text written by a previous SDK operation then we can write it again 2. if the file was not
			 * previously written by us we will need to prompt the user with permission yes/no to update the
			 * build.<username>.properties file
			 */

			PropertiesConfiguration propsConfig = new PropertiesConfiguration(userBuildFile);

			String header = propsConfig.getHeader();

			boolean shouldUpdateBuildFile = false;

			if (header != null && header.contains(MSG_MANAGED_BY_LIFERAY_IDE)) {
				shouldUpdateBuildFile = true;
			}
			else {
				String overwrite = getPrefStore().getString(SDKPlugin.PREF_KEY_OVERWRITE_USER_BUILD_FILE);

				if (MessageDialogWithToggle.ALWAYS.equals(overwrite)) {
					shouldUpdateBuildFile = true;
				}
				else if (MessageDialogWithToggle.NEVER.equals(overwrite)) {
					shouldUpdateBuildFile = false;
				}
				else {
					shouldUpdateBuildFile = promptForOverwrite(userBuildFile);
				}
			}

			if (shouldUpdateBuildFile) {
				for (String key : properties.keySet()) {
					propsConfig.setProperty(key, properties.get(key));
				}

				propsConfig.setHeader(MSG_MANAGED_BY_LIFERAY_IDE);
				propsConfig.save(userBuildFile);
			}

		}
		else {
			Properties props = new Properties();

			props.putAll(properties);

			props.store(new FileOutputStream(userBuildFile), MSG_MANAGED_BY_LIFERAY_IDE);
		}

	}

	protected IStatus runTarget(IProject project, Map<String, String> properties, String target, boolean separateJRE) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), target, properties, separateJRE);
		}
		catch (CoreException e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

}
