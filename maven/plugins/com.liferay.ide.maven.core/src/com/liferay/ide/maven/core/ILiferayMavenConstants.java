/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.maven.core;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 */
public interface ILiferayMavenConstants
{

    String _LIFERAY_MAVEN_PLUGINS_GROUP_ID = "com.liferay.maven.plugins";  //$NON-NLS-1$

    String _LIFERAY_MOJO_PREFIX = "liferay:";  //$NON-NLS-1$

    String BND_MAVEN_PLUGIN_KEY = "biz.aQute.bnd:bnd-maven-plugin";

    String DEFAULT_PLUGIN_TYPE = "portlet"; //$NON-NLS-1$

    String EXT_PLUGIN_TYPE = "ext"; //$NON-NLS-1$

    String GOAL_BUILD_CSS = "build-css"; //$NON-NLS-1$

    String GOAL_BUILD_EXT = "build-ext"; //$NON-NLS-1$

    String GOAL_BUILD_THUMBNAIL = "build-thumbnail"; //$NON-NLS-1$

    String GOAL_THEME_MERGE = "theme-merge"; //$NON-NLS-1$

    String HOOK_PLUGIN_TYPE = "hook"; //$NON-NLS-1$

    String LAYOUTTPL_PLUGIN_TYPE = "layouttpl"; //$NON-NLS-1$

    String LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID = LiferayMavenCore.PLUGIN_ID + ".configurationProblem";//$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN = "liferay-maven-plugin"; //$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID = "liferay-maven-plugin"; //$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN_KEY = _LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" + LIFERAY_MAVEN_PLUGIN; //$NON-NLS-1$

    String LIFERAY_THEME_BUILDER_PLUGIN_KEY = "com.liferay:com.liferay.portal.tools.theme.builder";

    String M2E_LIFERAY_FOLDER = "m2e-liferay";  //$NON-NLS-1$

    String MAVEN_BUNDLE_PLUGIN_KEY = "org.apache.felix:maven-bundle-plugin";

    String PLUGIN_CONFIG_API_BASE_DIR = "apiBaseDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR = "appServerPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_LIFERAY_VERSION = "liferayVersion"; //$NON-NLS-1$

    String PLUGIN_CONFIG_PARENT_THEME = "parentTheme";

    String PLUGIN_CONFIG_PLUGIN_NAME = "pluginName"; //$NON-NLS-1$

    String PLUGIN_CONFIG_PLUGIN_TYPE = "pluginType"; //$NON-NLS-1$

    String PLUGIN_CONFIG_SASS_DIR_NAMES = "sassDirNames"; //$NON-NLS-1$

    String PLUGIN_CONFIG_THEME_TYPE = "themeType"; //$NON-NLS-1$

    String PLUGIN_CONFIG_WEBAPP_DIR = "webappDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_WEBAPPBASE_DIR = "webappBaseDir"; //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_CSS = _LIFERAY_MOJO_PREFIX + "build-css";  //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_DB = _LIFERAY_MOJO_PREFIX + "build-db"; //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_EXT = _LIFERAY_MOJO_PREFIX + "build-ext"; //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_LANG = _LIFERAY_MOJO_PREFIX + "build-lang"; //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_SERVICE = _LIFERAY_MOJO_PREFIX + "build-service";  //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_THUMBNAIL = _LIFERAY_MOJO_PREFIX + "build-thumbnail"; //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_WSDD = _LIFERAY_MOJO_PREFIX + "build-wsdd"; //$NON-NLS-1$

    String PLUGIN_GOAL_DEPLOY = _LIFERAY_MOJO_PREFIX + "deploy";  //$NON-NLS-1$

    String PLUGIN_GOAL_DIRECT_DEPLOY = _LIFERAY_MOJO_PREFIX + "direct-deploy";  //$NON-NLS-1$

    String PLUGIN_GOAL_THEME_MERGE = _LIFERAY_MOJO_PREFIX + "theme-merge";  //$NON-NLS-1$

    String PORTLET_PLUGIN_TYPE = DEFAULT_PLUGIN_TYPE;

    String SERVICE_BUILDER_PLUGIN_ARTIFACT_ID = "com.liferay.portal.tools.service.builder";

    String SERVICE_BUILDER_PLUGIN_KEY = "com.liferay:" + SERVICE_BUILDER_PLUGIN_ARTIFACT_ID;

    String THEME_PLUGIN_TYPE = "theme"; //$NON-NLS-1$

    String THEME_RESOURCES_FOLDER = "theme-resources"; //$NON-NLS-1$

    String WEB_PLUGIN_TYPE = "web";

}
