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

    String BUILD_CSS = "build-css"; //$NON-NLS-1$

    String BUILD_EXT = "build-ext"; //$NON-NLS-1$

    String BUILD_THUMBNAIL = "build-thumbnail"; //$NON-NLS-1$

    String DEFAULT_PLUGIN_TYPE = "portlet"; //$NON-NLS-1$

    String EXT_PLUGIN_TYPE = "ext"; //$NON-NLS-1$

    String HOOK_PLUGIN_TYPE = "hook"; //$NON-NLS-1$

    String LAYOUTTPL_PLUGIN_TYPE = "layouttpl"; //$NON-NLS-1$

    String LIFERAY_MAVEN_MARKER_CONFIGURATION_ERROR_ID = LiferayMavenCore.PLUGIN_ID + ".configurationProblem";//$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN = "liferay-maven-plugin"; //$NON-NLS-1$

    String LIFERAY_MAVEN_PLUGIN_KEY = "com.liferay.maven.plugins:liferay-maven-plugin"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR = "appServerDeployDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR = "appServerLibGlobalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR = "appServerPortalDir"; //$NON-NLS-1$

    String PLUGIN_CONFIG_LIFERAY_VERSION = "liferayVersion"; //$NON-NLS-1$

    String PLUGIN_CONFIG_PLUGIN_TYPE = "pluginType"; //$NON-NLS-1$

    String PORTLET_PLUGIN_TYPE = DEFAULT_PLUGIN_TYPE;

    String THEME_PLUGIN_TYPE = "theme"; //$NON-NLS-1$

}
