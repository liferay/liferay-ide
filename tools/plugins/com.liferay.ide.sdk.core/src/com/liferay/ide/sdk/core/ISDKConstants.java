/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.StringPool;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public interface ISDKConstants
{

    @SuppressWarnings( "deprecation" )
    public static final IEclipsePreferences __defaultPrefs = new DefaultScope().getNode( SDKCorePlugin.PLUGIN_ID );

    static final String _HOOK_PLUGIN_PROJECT_SUFFIX = "-hook"; //$NON-NLS-1$

    static final String _LAYOUTTPL_PLUGIN_PROJECT_SUFFIX = "-layouttpl"; //$NON-NLS-1$

    static final String _PORTLET_PLUGIN_PROJECT_SUFFIX = "-portlet"; //$NON-NLS-1$

    static final String _THEME_PLUGIN_PROJECT_SUFFIX = "-theme"; //$NON-NLS-1$

    static final String _WEB_PLUGIN_PROJECT_SUFFIX = "-web"; //$NON-NLS-1$

    public static final String[] ANT_LIBRARIES = __defaultPrefs.get( "ant.libraries", StringPool.EMPTY ).split( StringPool.COMMA ); //$NON-NLS-1$

    public static final String[] BINARY_PLUGIN_EXTENSIONS = { "*.war" }; //$NON-NLS-1$

    public static final String[] BINARY_PLUGIN_FILTERS = { "war" }; //$NON-NLS-1$

    public static final String[] BINARY_PLUGIN_PROJECT_WILDCARDS =
    {
        StringPool.ASTERISK + _HOOK_PLUGIN_PROJECT_SUFFIX + "*.war", //$NON-NLS-1$
        StringPool.ASTERISK + _THEME_PLUGIN_PROJECT_SUFFIX + "*.war",  //$NON-NLS-1$
        StringPool.ASTERISK + _PORTLET_PLUGIN_PROJECT_SUFFIX + "*.war", //$NON-NLS-1$
        StringPool.ASTERISK + _LAYOUTTPL_PLUGIN_PROJECT_SUFFIX + "*.war"  //$NON-NLS-1$
    };

    public static final String BUILD_PROPERTIES = "build.properties"; //$NON-NLS-1$

    public static final String DEFAULT_DOCROOT_FOLDER = "docroot";  //$NON-NLS-1$

    public static final String EXT_PLUGIN_ANT_BUILD = __defaultPrefs.get( "ext.plugin.ant.build", null ); //$NON-NLS-1$

    public static final String EXT_PLUGIN_PROJECT_FOLDER = "ext"; //$NON-NLS-1$

    public static final String EXT_PLUGIN_PROJECT_SUFFIX = "-ext"; //$NON-NLS-1$

    public static final String HOOK_PLUGIN_ANT_BUILD = __defaultPrefs.get( "hook.plugin.ant.build", null ); //$NON-NLS-1$

    public static final String HOOK_PLUGIN_PROJECT_FOLDER = "hooks"; //$NON-NLS-1$

    public static final String HOOK_PLUGIN_PROJECT_SUFFIX = _HOOK_PLUGIN_PROJECT_SUFFIX;

    public static final String IVY_SETTINGS_XML_FILE = "ivy-settings.xml";  //$NON-NLS-1$

    public static final String IVY_XML_FILE = "ivy.xml";  //$NON-NLS-1$

    public static final String LAYOUT_TEMPLATE_PLUGIN_ANT_BUILD = __defaultPrefs.get(
        "layouttpl.plugin.ant.build", null ); //$NON-NLS-1$

    public static final String LAYOUTTPL_PLUGIN_ANT_BUILD = __defaultPrefs.get( "layouttpl.plugin.ant.build", null ); //$NON-NLS-1$

    public static final String LAYOUTTPL_PLUGIN_PROJECT_FOLDER = "layouttpl"; //$NON-NLS-1$

    public static final String LAYOUTTPL_PLUGIN_PROJECT_SUFFIX = _LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;

    public static final Version LEAST_SUPPORTED_SDK_VERSION = ILiferayConstants.V601;

    public static final String PORTLET_PLUGIN_ANT_BUILD = __defaultPrefs.get( "portlet.plugin.ant.build", null ); //$NON-NLS-1$

    public static final String PORTLET_PLUGIN_PROJECT_FOLDER = "portlets"; //$NON-NLS-1$

    public static final String PORTLET_PLUGIN_PROJECT_SUFFIX = _PORTLET_PLUGIN_PROJECT_SUFFIX;

    public static final String[] PORTLET_PLUGIN_ZIP_IGNORE_FILES = __defaultPrefs.get(
        "portlet.plugin.zip.ignore.files", StringPool.EMPTY ).split( StringPool.COMMA ); //$NON-NLS-1$

    public static final String PORTLET_PLUGIN_ZIP_PATH = __defaultPrefs.get( "portlet.plugin.zip.path", null ); //$NON-NLS-1$

    public static final String PROJECT_BUILD_XML = __defaultPrefs.get( "project.build.xml", null ); //$NON-NLS-1$

    public static final String PROPERTY_APP_SERVER_DEPLOY_DIR = "app.server{0}deploy.dir"; //$NON-NLS-1$

    public static final String PROPERTY_APP_SERVER_DIR = "app.server{0}dir"; //$NON-NLS-1$

    public static final String PROPERTY_APP_SERVER_LIB_GLOBAL_DIR = "app.server{0}lib.global.dir"; //$NON-NLS-1$

    public static final String PROPERTY_APP_SERVER_PARENT_DIR = "app.server.parent.dir"; //$NON-NLS-1$

    public static final String PROPERTY_APP_SERVER_PORTAL_DIR = "app.server{0}portal.dir"; //$NON-NLS-1$

    public static final String PROPERTY_APP_SERVER_TYPE = "app.server.type"; //$NON-NLS-1$

    public static final String PROPERTY_APP_ZIP_NAME = "app.server.zip.name"; //$NON-NLS-1$

    public static final String PROPERTY_AUTO_DEPLOY_CUSTOM_PORTLET_XML = "auto.deploy.custom.portlet.xml"; //$NON-NLS-1$

    public static final String PROPERTY_AUTO_DEPLOY_UNPACK_WAR = "auto.deploy.unpack.war"; //$NON-NLS-1$

    public static final String PROPERTY_EXT_DISPLAY_NAME = "ext.display.name"; //$NON-NLS-1$

    public static final String PROPERTY_EXT_NAME = "ext.name"; //$NON-NLS-1$

    public static final String PROPERTY_EXT_PARENT_DIR = "ext.parent.dir"; //$NON-NLS-1$

    public static final String PROPERTY_EXT_WORK_DIR = "ext.work.dir"; //$NON-NLS-1$

    public static final String PROPERTY_HOOK_DISPLAY_NAME = "hook.display.name"; //$NON-NLS-1$

    public static final String PROPERTY_HOOK_NAME = "hook.name"; //$NON-NLS-1$

    public static final String PROPERTY_HOOK_PARENT_DIR = "hook.parent.dir"; //$NON-NLS-1$

    public static final String PROPERTY_LANG_DIR = "lang.dir"; //$NON-NLS-1$

    public static final String PROPERTY_LANG_FILE = "lang.file"; //$NON-NLS-1$

    public static final String PROPERTY_LAYOUTTPL_DISPLAY_NAME = "layouttpl.display.name"; //$NON-NLS-1$

    public static final String PROPERTY_LAYOUTTPL_NAME = "layouttpl.name"; //$NON-NLS-1$

    public static final String PROPERTY_LAYOUTTPL_PARENT_DIR = "layouttpl.parent.dir"; //$NON-NLS-1$

    public static final String PROPERTY_LAYOUTTPL_TEMPLATE_NAME = "layouttpl.template.name"; //$NON-NLS-1$

    public static final String PROPERTY_NAME = "sdk-name"; //$NON-NLS-1$

    public static final String PROPERTY_PLUGIN_FILE = "plugin.file"; //$NON-NLS-1$

    public static final String PROPERTY_PLUGIN_FILE_DEFAULT = "plugin.file.default"; //$NON-NLS-1$

    public static final String PROPERTY_PORTLET_DISPLAY_NAME = "portlet.display.name"; //$NON-NLS-1$

    public static final String PROPERTY_PORTLET_FRAMEWORK = "portlet.framework"; //$NON-NLS-1$

    public static final String PROPERTY_PORTLET_NAME = "portlet.name"; //$NON-NLS-1$

    public static final String PROPERTY_PORTLET_PARENT_DIR = "portlet.parent.dir"; //$NON-NLS-1$

    public static final String PROPERTY_SERVICE_FILE = "service.file"; //$NON-NLS-1$

    public static final String PROPERTY_SERVICE_INPUT_FILE = "service.input.file"; //$NON-NLS-1$

    public static final String PROPERTY_THEME_DISPLAY_NAME = "theme.display.name"; //$NON-NLS-1$

    public static final String PROPERTY_THEME_NAME = "theme.name"; //$NON-NLS-1$

    public static final String PROPERTY_THEME_PARENT_DIR = "theme.parent.dir"; //$NON-NLS-1$

    public static final String PROPERTY_WEB_DISPLAY_NAME = "web.display.name"; //$NON-NLS-1$

    public static final String PROPERTY_WEB_NAME = "web.name"; //$NON-NLS-1$

    public static final String PROPERTY_WEB_PARENT_DIR = "web.parent.dir"; //$NON-NLS-1$

    public static final String TARGET_ALL = "all"; //$NON-NLS-1$

    public static final String TARGET_BUILD_CLIENT = "build-client"; //$NON-NLS-1$

    public static final String TARGET_BUILD_CSS = "build-css"; //$NON-NLS-1$

    public static final String TARGET_BUILD_DB = "build-db"; //$NON-NLS-1$

    public static final String TARGET_BUILD_LANG = "build-lang"; //$NON-NLS-1$

    public static final String TARGET_BUILD_LANG_CMD = "build-lang-cmd"; //$NON-NLS-1$

    public static final String TARGET_BUILD_SERVICE = "build-service"; //$NON-NLS-1$

    public static final String TARGET_BUILD_WSDD = "build-wsdd"; //$NON-NLS-1$

    public static final String TARGET_CLEAN = "clean"; //$NON-NLS-1$

    public static final String TARGET_CLEAN_APP_SERVER = "clean-app-server"; //$NON-NLS-1$

    public static final String TARGET_COMPILE = "compile"; //$NON-NLS-1$

    public static final String TARGET_COMPILE_TEST = "compile-test"; //$NON-NLS-1$

    public static final String TARGET_CREATE = "create"; //$NON-NLS-1$

    public static final String TARGET_DEPLOY = "deploy"; //$NON-NLS-1$

    public static final String TARGET_DIRECT_DEPLOY = "direct-deploy"; //$NON-NLS-1$

    public static final String TARGET_FORMAT_SOURCE = "format-source"; //$NON-NLS-1$

    public static final String TARGET_MERGE = "merge"; //$NON-NLS-1$

    public static final String TARGET_TEST = "test"; //$NON-NLS-1$

    public static final String TARGET_WAR = "war"; //$NON-NLS-1$

    public static final String THEME_PLUGIN_ANT_BUILD = __defaultPrefs.get( "theme.plugin.ant.build", null ); //$NON-NLS-1$

    public static final String THEME_PLUGIN_PROJECT_FOLDER = "themes"; //$NON-NLS-1$

    public static final String THEME_PLUGIN_PROJECT_SUFFIX = _THEME_PLUGIN_PROJECT_SUFFIX;

    public static final String VAR_NAME_LIFERAY_SDK_DIR = "liferay_sdk_dir"; //$NON-NLS-1$

    public static final String WEB_PLUGIN_ANT_BUILD = __defaultPrefs.get( "web.plugin.ant.build", null ); //$NON-NLS-1$

    public static final String WEB_PLUGIN_PROJECT_FOLDER = "webs"; //$NON-NLS-1$

    public static final String WEB_PLUGIN_PROJECT_SUFFIX = _WEB_PLUGIN_PROJECT_SUFFIX;

}
