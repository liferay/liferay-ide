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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.sdk.core.ISDKConstants;

import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class IPluginFacetConstants {

	public static final String EXT_PLUGIN_DEFAULT_OUTPUT_FOLDER =
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-web/docroot/WEB-INF/classes";

	public static final String EXT_PLUGIN_SDK_CONFIG_FOLDER =
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-web/" + ISDKConstants.DEFAULT_DOCROOT_FOLDER;

	public static final String[] EXT_PLUGIN_SDK_OUTPUT_FOLDERS = {
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-impl/classes",
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-service/classes",
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-util-bridges/classes",
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-util-java/classes",
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-util-taglib/classes"
	};

	public static final String[] EXT_PLUGIN_SDK_SOURCE_FOLDERS = {
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-impl/src",
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-service/src",
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-util-bridges/src",
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-util-java/src",
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/ext-util-taglib/src"
	};

	public static final String HOOK_PLUGIN_SDK_CONFIG_FOLDER = ISDKConstants.DEFAULT_DOCROOT_FOLDER;

	public static final String HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER =
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/classes";

	public static final String HOOK_PLUGIN_SDK_SOURCE_FOLDER = ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/src";

	public static final String LAYOUTTPL_PLUGIN_SDK_CONFIG_FOLDER = ISDKConstants.DEFAULT_DOCROOT_FOLDER;

	public static final String LAYOUTTPL_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER =
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/classes";

	public static final String LIFERAY_DEFAULT_FACET_TEMPLATE = "liferay.portlet.template";

	public static final String LIFERAY_EXT_FACET_ID = "liferay.ext";

	public static final String LIFERAY_EXT_FACET_TEMPLATE_ID = "liferay.ext.template";

	public static final String LIFERAY_EXT_PRESET = "liferay.ext.preset";

	public static final IProjectFacet LIFERAY_EXT_PROJECT_FACET = ProjectFacetsManager.getProjectFacet(
		LIFERAY_EXT_FACET_ID);

	public static final String LIFERAY_HOOK_FACET_ID = "liferay.hook";

	public static final String LIFERAY_HOOK_FACET_TEMPLATE_ID = "liferay.hook.template";

	public static final String LIFERAY_HOOK_PRESET = "liferay.hook.preset";

	public static final IProjectFacet LIFERAY_HOOK_PROJECT_FACET = ProjectFacetsManager.getProjectFacet(
		LIFERAY_HOOK_FACET_ID);

	public static final String LIFERAY_LAYOUTTPL_FACET_ID = "liferay.layouttpl";

	public static final String LIFERAY_LAYOUTTPL_FACET_TEMPLATE_ID = "liferay.layouttpl.template";

	public static final String LIFERAY_LAYOUTTPL_PRESET = "liferay.layouttpl.preset";

	public static final IProjectFacet LIFERAY_LAYOUTTPL_PROJECT_FACET = ProjectFacetsManager.getProjectFacet(
		LIFERAY_LAYOUTTPL_FACET_ID);

	public static final String LIFERAY_PORTLET_FACET_ID = "liferay.portlet";

	public static final String LIFERAY_PORTLET_FACET_TEMPLATE_ID = "liferay.portlet.template";

	public static final String LIFERAY_PORTLET_PRESET = "liferay.portlet.preset";

	public static final IProjectFacet LIFERAY_PORTLET_PROJECT_FACET = ProjectFacetsManager.getProjectFacet(
		LIFERAY_PORTLET_FACET_ID);

	public static final String LIFERAY_SDK_NAME_DEFAULT_VALUE = "__NO__SDK__";

	public static final String LIFERAY_SDK_NAME_DEFAULT_VALUE_DESCRIPTION = "<None>";

	public static final String LIFERAY_THEME_FACET_ID = "liferay.theme";

	public static final String LIFERAY_THEME_FACET_TEMPLATE_ID = "liferay.theme.template";

	public static final String LIFERAY_THEME_PRESET = "liferay.theme.preset";

	public static final IProjectFacet LIFERAY_THEME_PROJECT_FACET = ProjectFacetsManager.getProjectFacet(
		LIFERAY_THEME_FACET_ID);

	public static final String LIFERAY_WEB_FACET_ID = "liferay.web";

	public static final String LIFERAY_WEB_FACET_TEMPLATE_ID = "liferay.web.template";

	public static final String LIFERAY_WEB_PRESET = "liferay.web.preset";

	public static final IProjectFacet LIFERAY_WEB_PROJECT_FACET = ProjectFacetsManager.getProjectFacet(
		LIFERAY_WEB_FACET_ID);

	public static final String PORTLET_PLUGIN_SDK_CONFIG_FOLDER = ISDKConstants.DEFAULT_DOCROOT_FOLDER;

	public static final String PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER =
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/classes";

	public static final String PORTLET_PLUGIN_SDK_SOURCE_FOLDER = ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/src";

	public static final String THEME_PLUGIN_SDK_CONFIG_FOLDER = ISDKConstants.DEFAULT_DOCROOT_FOLDER;

	public static final String THEME_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER =
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/classes";

	public static final String WEB_PLUGIN_SDK_CONFIG_FOLDER = ISDKConstants.DEFAULT_DOCROOT_FOLDER;

	public static final String WEB_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER =
		ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/classes";

	public static final String WEB_PLUGIN_SDK_SOURCE_FOLDER = ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/src";

}