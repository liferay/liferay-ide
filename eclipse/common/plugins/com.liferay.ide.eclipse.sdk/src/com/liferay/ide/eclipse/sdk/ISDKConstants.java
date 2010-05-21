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

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
public interface ISDKConstants {

	public static final IEclipsePreferences _defaultPrefs = new DefaultScope().getNode(SDKPlugin.PLUGIN_ID);
	
	public static final String[] ANT_LIBRARIES = _defaultPrefs.get("ant.libraries", "").split(",");

	public static final String EXT_PLUGIN_ANT_BUILD = _defaultPrefs.get("ext.plugin.ant.build", null);

	public static final String HOOK_PLUGIN_ANT_BUILD = _defaultPrefs.get("hook.plugin.ant.build", null);

	public static final String LAYOUT_TEMPLATE_PLUGIN_ANT_BUILD =
		_defaultPrefs.get("layouttpl.plugin.ant.build", null);

	public static final Version LEAST_SUPPORTED_SDK_VERSION = new Version(6, 0, 1);

	public static final String PORTLET_PLUGIN_ANT_BUILD = _defaultPrefs.get("portlet.plugin.ant.build", null);

	public static final String[] PORTLET_PLUGIN_ZIP_IGNORE_FILES =
		_defaultPrefs.get("portlet.plugin.zip.ignore.files", "").split(",");

	public static final String PORTLET_PLUGIN_ZIP_PATH = _defaultPrefs.get("portlet.plugin.zip.path", null);

	static final String PROJECT_BUILD_XML = _defaultPrefs.get("project.build.xml", null);

	public static final String PROPERTY_APP_SERVER_DEPLOY_DIR = "app.server.deploy.dir";

	public static final String PROPERTY_APP_SERVER_DIR = "app.server.dir";

	public static final String PROPERTY_APP_SERVER_LIB_GLOBAL_DIR = "app.server.lib.global.dir";

	public static final String PROPERTY_APP_SERVER_PORTAL_DIR = "app.server.portal.dir";

	public static final String PROPERTY_APP_SERVER_TYPE = "app.server.type";

	public static final String PROPERTY_EXT_DISPLAY_NAME = "ext.display.name";

	public static final String PROPERTY_EXT_NAME = "ext.name";

	public static final String PROPERTY_EXT_PARENT_DIR = "ext.parent.dir";

	public static final String PROPERTY_HOOK_DISPLAY_NAME = "hook.display.name";

	public static final String PROPERTY_HOOK_NAME = "hook.name";

	public static final String PROPERTY_HOOK_PARENT_DIR = "hook.parent.dir";

	public static final String PROPERTY_LANG_DIR = "lang.dir";

	public static final String PROPERTY_LANG_FILE = "lang.file";

	public static final String PROPERTY_LAYOUTTPL_DISPLAY_NAME = "layouttpl.display.name";

	public static final String PROPERTY_LAYOUTTPL_NAME = "layouttpl.name";

	public static final String PROPERTY_LAYOUTTPL_PARENT_DIR = "layouttpl.parent.dir";

	public static final String PROPERTY_LAYOUTTPL_TEMPLATE_NAME = "layouttpl.template.name";

	public static final String PROPERTY_NAME = "sdk-name";

	public static final String PROPERTY_PORTLET_DISPLAY_NAME = "portlet.display.name";

	public static final String PROPERTY_PORTLET_NAME = "portlet.name";

	public static final String PROPERTY_PORTLET_PARENT_DIR = "portlet.parent.dir";

	public static final String PROPERTY_SERVICE_INPUT_FILE = "service.input.file";

	public static final String PROPERTY_THEME_DISPLAY_NAME = "theme.display.name";

	public static final String PROPERTY_THEME_NAME = "theme.name";

	public static final String PROPERTY_THEME_PARENT_DIR = "theme.parent.dir";

	public static final String TARGET_BUILD_LANG_CMD = "build-lang-cmd";

	public static final String TARGET_BUILD_SERVICE = "build-service";

	public static final String TARGET_CREATE = "create";

	public static final String TARGET_DEPLOY = "deploy";

	public static final String THEME_PLUGIN_ANT_BUILD = _defaultPrefs.get("theme.plugin.ant.build", null);
}
