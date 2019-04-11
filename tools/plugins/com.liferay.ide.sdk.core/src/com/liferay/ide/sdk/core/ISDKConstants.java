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
import com.liferay.ide.core.util.StringPool;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public interface ISDKConstants {

	public static final IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE.getNode(SDKCorePlugin.PLUGIN_ID);

	public static final String[] ANT_LIBRARIES = defaultPrefs.get(
		"ant.libraries", StringPool.EMPTY
	).split(
		StringPool.COMMA
	);

	public static final String[] BINARY_PLUGIN_EXTENSIONS = {"*.war"};

	public static final String[] BINARY_PLUGIN_FILTERS = {"war"};

	public static final String[] BINARY_PLUGIN_PROJECT_WILDCARDS = {
		StringPool.ASTERISK + "-hook*.war", StringPool.ASTERISK + "-theme*.war", StringPool.ASTERISK + "-portlet*.war",
		StringPool.ASTERISK + "-layouttpl*.war", StringPool.ASTERISK + "-web*.war"
	};

	public static final String BUILD_PROPERTIES = "build.properties";

	public static final String CREATE_BAT = "create.bat";

	public static final String CREATE_SH = "create.sh";

	public static final String DEFAULT_DOCROOT_FOLDER = "docroot";

	public static final String EXT_PLUGIN_ANT_BUILD = defaultPrefs.get("ext.plugin.ant.build", null);

	public static final String EXT_PLUGIN_PROJECT_FOLDER = "ext";

	public static final String EXT_PLUGIN_PROJECT_SUFFIX = "-ext";

	public static final String HOOK_PLUGIN_ANT_BUILD = defaultPrefs.get("hook.plugin.ant.build", null);

	public static final String HOOK_PLUGIN_PROJECT_FOLDER = "hooks";

	public static final String HOOK_PLUGIN_PROJECT_SUFFIX = "-hook";

	public static final String IVY_SETTINGS_XML_FILE = "ivy-settings.xml";

	public static final String IVY_XML_FILE = "ivy.xml";

	public static final String LAYOUT_TEMPLATE_PLUGIN_ANT_BUILD = defaultPrefs.get("layouttpl.plugin.ant.build", null);

	public static final String LAYOUTTPL_PLUGIN_ANT_BUILD = defaultPrefs.get("layouttpl.plugin.ant.build", null);

	public static final String LAYOUTTPL_PLUGIN_PROJECT_FOLDER = "layouttpl";

	public static final String LAYOUTTPL_PLUGIN_PROJECT_SUFFIX = "-layouttpl";

	public static final Version LEAST_SUPPORTED_SDK_VERSION = ILiferayConstants.V601;

	public static final String PORTLET_PLUGIN_ANT_BUILD = defaultPrefs.get("portlet.plugin.ant.build", null);

	public static final String PORTLET_PLUGIN_PROJECT_FOLDER = "portlets";

	public static final String PORTLET_PLUGIN_PROJECT_SUFFIX = "-portlet";

	public static final String[] PORTLET_PLUGIN_ZIP_IGNORE_FILES = defaultPrefs.get(
		"portlet.plugin.zip.ignore.files", StringPool.EMPTY
	).split(
		StringPool.COMMA
	);

	public static final String PORTLET_PLUGIN_ZIP_PATH = defaultPrefs.get("portlet.plugin.zip.path", null);

	public static final String PROJECT_BUILD_XML = defaultPrefs.get("project.build.xml", null);

	public static final String PROPERTY_APP_SERVER_DEPLOY_DIR = "app.server{0}deploy.dir";

	public static final String PROPERTY_APP_SERVER_DIR = "app.server{0}dir";

	public static final String PROPERTY_APP_SERVER_LIB_GLOBAL_DIR = "app.server{0}lib.global.dir";

	public static final String PROPERTY_APP_SERVER_PARENT_DIR = "app.server.parent.dir";

	public static final String PROPERTY_APP_SERVER_PORTAL_DIR = "app.server{0}portal.dir";

	public static final String PROPERTY_APP_SERVER_TYPE = "app.server.type";

	public static final String PROPERTY_APP_ZIP_NAME = "app.server.zip.name";

	public static final String PROPERTY_AUTO_DEPLOY_CUSTOM_PORTLET_XML = "auto.deploy.custom.portlet.xml";

	public static final String PROPERTY_AUTO_DEPLOY_UNPACK_WAR = "auto.deploy.unpack.war";

	public static final String PROPERTY_EXT_DISPLAY_NAME = "ext.display.name";

	public static final String PROPERTY_EXT_NAME = "ext.name";

	public static final String PROPERTY_EXT_PARENT_DIR = "ext.parent.dir";

	public static final String PROPERTY_EXT_WORK_DIR = "ext.work.dir";

	public static final String PROPERTY_HOOK_DISPLAY_NAME = "hook.display.name";

	public static final String PROPERTY_HOOK_NAME = "hook.name";

	public static final String PROPERTY_HOOK_PARENT_DIR = "hook.parent.dir";

	public static final String PROPERTY_LANG_DIR = "lang.dir";

	public static final String PROPERTY_LANG_FILE = "lang.file";

	public static final String PROPERTY_LAYOUTTPL_DISPLAY_NAME = "layouttpl.display.name";

	public static final String PROPERTY_LAYOUTTPL_NAME = "layouttpl.name";

	public static final String PROPERTY_LAYOUTTPL_PARENT_DIR = "layouttpl.parent.dir";

	public static final String PROPERTY_LAYOUTTPL_TEMPLATE_NAME = "layouttpl.template.name";

	public static final String PROPERTY_LP_VERSION = "lp.version";

	public static final String PROPERTY_LP_VERSION_SUFFIX = "lp.version.file.suffix";

	public static final String PROPERTY_NAME = "sdk-name";

	public static final String PROPERTY_PLUGIN_FILE = "plugin.file";

	public static final String PROPERTY_PLUGIN_FILE_DEFAULT = "plugin.file.default";

	public static final String PROPERTY_PORTLET_DISPLAY_NAME = "portlet.display.name";

	public static final String PROPERTY_PORTLET_FRAMEWORK = "portlet.framework";

	public static final String PROPERTY_PORTLET_NAME = "portlet.name";

	public static final String PROPERTY_PORTLET_PARENT_DIR = "portlet.parent.dir";

	public static final String PROPERTY_SERVICE_FILE = "service.file";

	public static final String PROPERTY_SERVICE_INPUT_FILE = "service.input.file";

	public static final String PROPERTY_THEME_DISPLAY_NAME = "theme.display.name";

	public static final String PROPERTY_THEME_NAME = "theme.name";

	public static final String PROPERTY_THEME_PARENT_DIR = "theme.parent.dir";

	public static final String PROPERTY_WEB_DISPLAY_NAME = "web.display.name";

	public static final String PROPERTY_WEB_NAME = "web.name";

	public static final String PROPERTY_WEB_PARENT_DIR = "web.parent.dir";

	public static final String TARGET_ALL = "all";

	public static final String TARGET_BUILD_CLIENT = "build-client";

	public static final String TARGET_BUILD_CSS = "build-css";

	public static final String TARGET_BUILD_DB = "build-db";

	public static final String TARGET_BUILD_LANG = "build-lang";

	public static final String TARGET_BUILD_LANG_CMD = "build-lang-cmd";

	public static final String TARGET_BUILD_SERVICE = "build-service";

	public static final String TARGET_BUILD_WSDD = "build-wsdd";

	public static final String TARGET_CLEAN = "clean";

	public static final String TARGET_CLEAN_APP_SERVER = "clean-app-server";

	public static final String TARGET_COMPILE = "compile";

	public static final String TARGET_COMPILE_TEST = "compile-test";

	public static final String TARGET_CREATE = "create";

	public static final String TARGET_DEPLOY = "deploy";

	public static final String TARGET_DIRECT_DEPLOY = "direct-deploy";

	public static final String TARGET_FORMAT_SOURCE = "format-source";

	public static final String TARGET_MERGE = "merge";

	public static final String TARGET_TEST = "test";

	public static final String TARGET_WAR = "war";

	public static final String THEME_PLUGIN_ANT_BUILD = defaultPrefs.get("theme.plugin.ant.build", null);

	public static final String THEME_PLUGIN_PROJECT_FOLDER = "themes";

	public static final String THEME_PLUGIN_PROJECT_SUFFIX = "-theme";

	public static final String VAR_NAME_LIFERAY_SDK_DIR = "liferay_sdk_dir";

	public static final String WEB_PLUGIN_ANT_BUILD = defaultPrefs.get("web.plugin.ant.build", null);

	public static final String WEB_PLUGIN_PROJECT_FOLDER = "webs";

	public static final String WEB_PLUGIN_PROJECT_SUFFIX = "-web";

}