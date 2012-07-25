/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.sdk;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
public interface ISDKConstants
{

    @SuppressWarnings( "deprecation" )
    public static final IEclipsePreferences __defaultPrefs = new DefaultScope().getNode( SDKPlugin.PLUGIN_ID );

    static final String _HOOK_PLUGIN_PROJECT_SUFFIX = "-hook";

    static final String _LAYOUTTPL_PLUGIN_PROJECT_SUFFIX = "-layouttpl";

    static final String _PORTLET_PLUGIN_PROJECT_SUFFIX = "-portlet";

    static final String _THEME_PLUGIN_PROJECT_SUFFIX = "-theme";

    public static final String[] ANT_LIBRARIES = __defaultPrefs.get( "ant.libraries", "" ).split( "," );

    public static final String[] BINARY_PLUGIN_EXTENSIONS = { "*.war" };

    public static final String[] BINARY_PLUGIN_FILTERS = { "war" };

    public static final String[] BINARY_PLUGIN_PROJECT_WILDCARDS = 
    { 
        "*" + _HOOK_PLUGIN_PROJECT_SUFFIX + "*.war",
        "*" + _THEME_PLUGIN_PROJECT_SUFFIX + "*.war", 
        "*" + _PORTLET_PLUGIN_PROJECT_SUFFIX + "*.war",
        "*" + _LAYOUTTPL_PLUGIN_PROJECT_SUFFIX + "*.war" 
    };

    public static final String EXT_PLUGIN_ANT_BUILD = __defaultPrefs.get( "ext.plugin.ant.build", null );

    public static final String EXT_PLUGIN_PROJECT_FOLDER = "ext";

    public static final String EXT_PLUGIN_PROJECT_SUFFIX = "-ext";

    public static final String HOOK_PLUGIN_ANT_BUILD = __defaultPrefs.get( "hook.plugin.ant.build", null );

    public static final String HOOK_PLUGIN_PROJECT_FOLDER = "hooks";

    public static final String HOOK_PLUGIN_PROJECT_SUFFIX = _HOOK_PLUGIN_PROJECT_SUFFIX;

    public static final String LAYOUT_TEMPLATE_PLUGIN_ANT_BUILD = __defaultPrefs.get(
        "layouttpl.plugin.ant.build", null );

    public static final String LAYOUTTPL_PLUGIN_ANT_BUILD = __defaultPrefs.get( "layouttpl.plugin.ant.build", null );

    public static final String LAYOUTTPL_PLUGIN_PROJECT_FOLDER = "layouttpl";

    public static final String LAYOUTTPL_PLUGIN_PROJECT_SUFFIX = _LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;

    public static final Version LEAST_SUPPORTED_SDK_VERSION = new Version( 6, 0, 1 );

    public static final String PORTLET_PLUGIN_ANT_BUILD = __defaultPrefs.get( "portlet.plugin.ant.build", null );

    public static final String PORTLET_PLUGIN_PROJECT_FOLDER = "portlets";

    public static final String PORTLET_PLUGIN_PROJECT_SUFFIX = _PORTLET_PLUGIN_PROJECT_SUFFIX;

    public static final String[] PORTLET_PLUGIN_ZIP_IGNORE_FILES = __defaultPrefs.get(
        "portlet.plugin.zip.ignore.files", "" ).split( "," );

    public static final String PORTLET_PLUGIN_ZIP_PATH = __defaultPrefs.get( "portlet.plugin.zip.path", null );

    public static final String PROJECT_BUILD_XML = __defaultPrefs.get( "project.build.xml", null );

    public static final String PROPERTY_APP_SERVER_DEPLOY_DIR = "app.server.deploy.dir";

    public static final String PROPERTY_APP_SERVER_DIR = "app.server.dir";

    public static final String PROPERTY_APP_SERVER_LIB_GLOBAL_DIR = "app.server.lib.global.dir";

    public static final String PROPERTY_APP_SERVER_PORTAL_DIR = "app.server.portal.dir";

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

    public static final String PROPERTY_NAME = "sdk-name";

    public static final String PROPERTY_PLUGIN_FILE = "plugin.file";

    public static final String PROPERTY_PORTLET_DISPLAY_NAME = "portlet.display.name";

    public static final String PROPERTY_PORTLET_FRAMEWORK = "portlet.framework";

    public static final String PROPERTY_PORTLET_NAME = "portlet.name";

    public static final String PROPERTY_PORTLET_PARENT_DIR = "portlet.parent.dir";

    public static final String PROPERTY_SERVICE_FILE = "service.file";

    public static final String PROPERTY_SERVICE_INPUT_FILE = "service.input.file";

    public static final String PROPERTY_THEME_DISPLAY_NAME = "theme.display.name";

    public static final String PROPERTY_THEME_NAME = "theme.name";

    public static final String PROPERTY_THEME_PARENT_DIR = "theme.parent.dir";

    public static final String TARGET_ALL = "all";

    public static final String TARGET_BUILD_CLIENT = "build-client";

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

    public static final String THEME_PLUGIN_ANT_BUILD = __defaultPrefs.get( "theme.plugin.ant.build", null );

    public static final String THEME_PLUGIN_PROJECT_FOLDER = "themes";

    public static final String THEME_PLUGIN_PROJECT_SUFFIX = _THEME_PLUGIN_PROJECT_SUFFIX;

    public static final String DEFAULT_WEBCONTENT_FOLDER = "docroot";

}
