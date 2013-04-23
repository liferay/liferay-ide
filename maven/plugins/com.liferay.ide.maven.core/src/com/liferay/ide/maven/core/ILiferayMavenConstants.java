/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 */
public interface ILiferayMavenConstants
{

    String _LIFERAY_MAVEN_PLUGINS_PREFIX = "com.liferay.maven.plugins";  //$NON-NLS-1$

    String _LIFERAY_MOJO_PREFIX = "liferay:";  //$NON-NLS-1$

    String BUILD_CSS = "build-css"; //$NON-NLS-1$

    String BUILD_EXT = "build-ext"; //$NON-NLS-1$

    String BUILD_THUMBNAIL = "build-thumbnail"; //$NON-NLS-1$

    String DEFAULT_PLUGIN_TYPE = "portlet"; //$NON-NLS-1$

    String EXT_PLUGIN_TYPE = "ext"; //$NON-NLS-1$

    String HOOK_PLUGIN_TYPE = "hook"; //$NON-NLS-1$

    String LAYOUTTPL_PLUGIN_TYPE = "layouttpl"; //$NON-NLS-1$

    String LIFERAY_MAVEN_MARKER_CONFIGURATION_ERROR_ID = LiferayMavenCore.PLUGIN_ID + ".configurationProblem";//$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN = "liferay-maven-plugin"; //$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID = "liferay-maven-plugin"; //$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN_KEY = _LIFERAY_MAVEN_PLUGINS_PREFIX + ":" + LIFERAY_MAVEN_PLUGIN; //$NON-NLS-1$

    String PLUGIN_CONFIG_API_BASE_DIR = "apiBaseDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR = "autoDeployDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_CLASSES_PORTAL_DIR = "appServerClassesPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR = "appServerDeployDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR = "appServerLibGlobalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_LIB_PORTAL_DIR = "appServerLibPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR = "appServerPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_TLD_PORTAL_DIR = "appServerTldPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_LIFERAY_VERSION = "liferayVersion"; //$NON-NLS-1$

    String PLUGIN_CONFIG_PLUGIN_TYPE = "pluginType"; //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_DB = _LIFERAY_MOJO_PREFIX + "build-db"; //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_LANG = _LIFERAY_MOJO_PREFIX + "build-lang"; //$NON-NLS-1$

    String PLUGIN_GOAL_BUILD_SERVICE = _LIFERAY_MOJO_PREFIX + "build-service";  //$NON-NLS-1$

    String PORTLET_PLUGIN_TYPE = DEFAULT_PLUGIN_TYPE;

    String SERVICE_BUILDER_GOAL = _LIFERAY_MOJO_PREFIX + "build-service"; //$NON-NLS-1$

    String THEME_PLUGIN_TYPE = "theme"; //$NON-NLS-1$

}
