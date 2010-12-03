/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;

/**
 * @author Greg Amerson
 */
public class SDK {

	public static String createXMLNameValuePair(String name, String value) {
		return name + "=\"" + value + "\" ";
	}

	protected boolean contributed;

	protected boolean defaultSDK;

	protected IPath location;

	protected String name;

	// private String runtime;

	protected String version;

	public SDK() {
	}

	public SDK(IPath location) {
		this.location = location;
	}

	public IStatus buildLanguage(IProject project, IFile langFile, Map<String, String> properties) {
		SDKHelper antHelper = new SDKHelper(this);

		String langDirLocation = langFile.getParent().getRawLocation().toOSString();

		String langFileName = langFile.getFullPath().removeFileExtension().lastSegment();

		properties.put(ISDKConstants.PROPERTY_LANG_DIR, langDirLocation);
		properties.put(ISDKConstants.PROPERTY_LANG_FILE, langFileName);

		try {
			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_BUILD_LANG_CMD,
				properties);
		}
		catch (Exception e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus buildService(IProject project, IFile serviceXmlFile, Map<String, String> properties) {
		SDKHelper antHelper = new SDKHelper(this);

		String serviceFile = serviceXmlFile.getRawLocation().toOSString();

		properties.put(ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile);

		try {
			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_BUILD_SERVICE,
				properties);
		}
		catch (Exception e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus buildWSDD(IProject project, IFile serviceXmlFile, Map<String, String> properties) {
		SDKHelper antHelper = new SDKHelper(this);

		String serviceFile = serviceXmlFile.getRawLocation().toOSString();

		properties.put(ISDKConstants.PROPERTY_SERVICE_INPUT_FILE, serviceFile);

		try {
			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_BUILD_WSDD,
				properties);
		}
		catch (Exception e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus compileThemePlugin(IProject project, Map<String, String> properties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_COMPILE,
				properties);
		}
		catch (CoreException e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IPath createNewExtProject(String extName, String extDisplayName, String appServerDir) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ISDKConstants.PROPERTY_EXT_NAME, extName);
			properties.put(ISDKConstants.PROPERTY_EXT_DISPLAY_NAME, extDisplayName);
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_TYPE, "tomcat");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_DIR, appServerDir);
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, appServerDir + "/webapps");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_LIB_GLOBAL_DIR, appServerDir + "/lib/ext");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_PORTAL_DIR, appServerDir + "/webapps/ROOT");

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

	public IPath createNewHookProject(String hookName, String hookDisplayName, String appServerDir) {
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

	public IPath createNewLayoutTplProject(String layoutTplName, String layoutTplDisplayName, String runtimeLocation) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ISDKConstants.PROPERTY_LAYOUTTPL_NAME, layoutTplName);
			properties.put(ISDKConstants.PROPERTY_LAYOUTTPL_DISPLAY_NAME, layoutTplDisplayName);
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_TYPE, "tomcat");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_DIR, runtimeLocation);
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, runtimeLocation + "/webapps");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_LIB_GLOBAL_DIR, runtimeLocation + "/lib/ext");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_PORTAL_DIR, runtimeLocation + "/webapps/ROOT");

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

	public IPath createNewPortletProject(String portletName, String portletDisplayName, String appServerDir) {
		return createNewPortletProject(portletName, portletDisplayName, null, appServerDir);
	}

	public IPath createNewPortletProject(
		String portletName, String portletDisplayName, String portletFramework, String appServerDir) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			Map<String, String> properties = new HashMap<String, String>();

			properties.put(ISDKConstants.PROPERTY_PORTLET_NAME, portletName);
			properties.put(ISDKConstants.PROPERTY_PORTLET_DISPLAY_NAME, portletDisplayName);
			properties.put(ISDKConstants.PROPERTY_PORTLET_FRAMEWORK, portletFramework);
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_TYPE, "tomcat");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_DIR, appServerDir);
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, appServerDir + "/webapps");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_LIB_GLOBAL_DIR, appServerDir + "/lib/ext");
			properties.put(ISDKConstants.PROPERTY_APP_SERVER_PORTAL_DIR, appServerDir + "/webapps/ROOT");

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

	public IStatus deployExtPlugin(IProject project, Map<String, String> properties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_DEPLOY,
				properties);
		}
		catch (CoreException e) {
			return SDKPlugin.createErrorStatus(e);
		}

		return Status.OK_STATUS;
	}

	public IStatus directDeploy(IProject project, Map<String, String> properties) {
		SDKHelper antHelper = new SDKHelper(this);

		try {
			antHelper.runTarget(
				project.getFile(ISDKConstants.PROJECT_BUILD_XML).getRawLocation(), ISDKConstants.TARGET_DIRECT_DEPLOY,
				properties);
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

}
