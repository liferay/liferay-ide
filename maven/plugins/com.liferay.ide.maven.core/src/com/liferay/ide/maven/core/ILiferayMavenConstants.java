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

package com.liferay.ide.maven.core;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Simon Jiang
 */
public interface ILiferayMavenConstants {

	public static String LIFERAY_MAVEN_PLUGINS_GROUP_ID = "com.liferay.maven.plugins";

	public String BND_MAVEN_PLUGIN_KEY = "biz.aQute.bnd:bnd-maven-plugin";

	public String DEFAULT_PLUGIN_TYPE = "portlet";

	public String EXT_PLUGIN_TYPE = "ext";

	public String GOAL_BUILD_CSS = "build-css";

	public String GOAL_BUILD_EXT = "build-ext";

	public String GOAL_BUILD_THUMBNAIL = "build-thumbnail";

	public String GOAL_THEME_MERGE = "theme-merge";

	public String HOOK_PLUGIN_TYPE = "hook";

	public String LAYOUTTPL_PLUGIN_TYPE = "layouttpl";

	public String LIFERAY_CSS_BUILDER_PLUGIN_KEY = "com.liferay:com.liferay.css.builder";

	public String LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID = LiferayMavenCore.PLUGIN_ID + ".configurationProblem";

	public String LIFERAY_MAVEN_PLUGIN = "liferay-maven-plugin";

	public String LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID = "liferay-maven-plugin";

	public String LIFERAY_MAVEN_PLUGIN_KEY = LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" + LIFERAY_MAVEN_PLUGIN;

	public String LIFERAY_MAVEN_PLUGINS_BUNDLE_SUPPORT_KEY = "com.liferay.portal.tools.bundle.support";

	public String LIFERAY_MAVEN_PLUGINS_CSS_BUILDER_KEY = "com.liferay.css.builder";

	public String LIFERAY_MAVEN_PLUGINS_DB_SUPPORT_KEY = "com.liferay.portal.tools.db.support";

	public String LIFERAY_MAVEN_PLUGINS_LANG_BUILDER_KEY = "com.liferay.lang.builder";

	public String LIFERAY_MAVEN_PLUGINS_SERVICE_BUILDER_KEY = "com.liferay.portal.tools.service.builder";

	public String LIFERAY_MAVEN_PLUGINS_WSDD_BUILDER_KEY = "com.liferay.portal.tools.wsdd.builder";

	public String LIFERAY_MOJO_PREFIX = "liferay:";

	public String LIFERAY_THEME_BUILDER_PLUGIN_KEY = "com.liferay:com.liferay.portal.tools.theme.builder";

	public String M2E_LIFERAY_FOLDER = "m2e-liferay";

	public String MAVEN_BUNDLE_PLUGIN_KEY = "org.apache.felix:maven-bundle-plugin";

	public String NEW_LIFERAY_MAVEN_PLUGINS_GROUP_ID = "com.liferay";

	public String PLUGIN_CONFIG_API_BASE_DIR = "apiBaseDir";

	public String PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR = "appServerPortalDir";

	public String PLUGIN_CONFIG_LIFERAY_VERSION = "liferayVersion";

	public String PLUGIN_CONFIG_PARENT_THEME = "parentTheme";

	public String PLUGIN_CONFIG_PLUGIN_NAME = "pluginName";

	public String PLUGIN_CONFIG_PLUGIN_TYPE = "pluginType";

	public String PLUGIN_CONFIG_SASS_DIR_NAMES = "sassDirNames";

	public String PLUGIN_CONFIG_THEME_TYPE = "themeType";

	public String PLUGIN_CONFIG_WEBAPP_DIR = "webappDir";

	public String PLUGIN_CONFIG_WEBAPPBASE_DIR = "webappBaseDir";

	public String PLUGIN_GOAL_BUILD_CSS = LIFERAY_MOJO_PREFIX + "build-css";

	public String PLUGIN_GOAL_BUILD_DB = LIFERAY_MOJO_PREFIX + "build-db";

	public String PLUGIN_GOAL_BUILD_EXT = LIFERAY_MOJO_PREFIX + "build-ext";

	public String PLUGIN_GOAL_BUILD_LANG = LIFERAY_MOJO_PREFIX + "build-lang";

	public String PLUGIN_GOAL_BUILD_SERVICE = LIFERAY_MOJO_PREFIX + "build-service";

	public String PLUGIN_GOAL_BUILD_THUMBNAIL = LIFERAY_MOJO_PREFIX + "build-thumbnail";

	public String PLUGIN_GOAL_BUILD_WSDD = LIFERAY_MOJO_PREFIX + "build-wsdd";

	public String PLUGIN_GOAL_DEPLOY = LIFERAY_MOJO_PREFIX + "deploy";

	public String PLUGIN_GOAL_DIRECT_DEPLOY = LIFERAY_MOJO_PREFIX + "direct-deploy";

	public String PLUGIN_GOAL_INIT_BUNDLE = LIFERAY_MOJO_PREFIX + "init-bundle";

	public String PLUGIN_GOAL_THEME_MERGE = LIFERAY_MOJO_PREFIX + "theme-merge";

	public String PORTLET_PLUGIN_TYPE = DEFAULT_PLUGIN_TYPE;

	public String SERVICE_BUILDER_PLUGIN_ARTIFACT_ID = "com.liferay.portal.tools.service.builder";

	public String SERVICE_BUILDER_PLUGIN_KEY = "com.liferay:" + SERVICE_BUILDER_PLUGIN_ARTIFACT_ID;

	public String THEME_PLUGIN_TYPE = "theme";

	public String THEME_RESOURCES_FOLDER = "theme-resources";

	public String WEB_PLUGIN_TYPE = "web";

}