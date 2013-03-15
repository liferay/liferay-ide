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

package com.liferay.ide.project.core.facet;

import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author Greg Amerson
 */
public class IPluginFacetConstants
{
    public static final String EXT_PLUGIN_DEFAULT_OUTPUT_FOLDER = "docroot/WEB-INF/classes"; //$NON-NLS-1$

    public static final String EXT_PLUGIN_SDK_CONFIG_FOLDER = "docroot/WEB-INF/ext-web/docroot"; //$NON-NLS-1$

    public static final String[] EXT_PLUGIN_SDK_OUTPUT_FOLDERS = new String[] 
    { 
        "docroot/WEB-INF/ext-impl/classes", //$NON-NLS-1$
        "docroot/WEB-INF/ext-service/classes",  //$NON-NLS-1$
        "docroot/WEB-INF/ext-util-bridges/classes", //$NON-NLS-1$
        "docroot/WEB-INF/ext-util-java/classes",  //$NON-NLS-1$
        "docroot/WEB-INF/ext-util-taglib/classes",  //$NON-NLS-1$
    };

    public static final String[] EXT_PLUGIN_SDK_SOURCE_FOLDERS = new String[] 
    { 
        "docroot/WEB-INF/ext-impl/src", //$NON-NLS-1$
        "docroot/WEB-INF/ext-service/src",  //$NON-NLS-1$
        "docroot/WEB-INF/ext-util-bridges/src",  //$NON-NLS-1$
        "docroot/WEB-INF/ext-util-java/src", //$NON-NLS-1$
        "docroot/WEB-INF/ext-util-taglib/src",  //$NON-NLS-1$
    };

    public static final String HOOK_PLUGIN_SDK_CONFIG_FOLDER = "docroot"; //$NON-NLS-1$

    public static final String HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER = "docroot/WEB-INF/classes"; //$NON-NLS-1$

    public static final String HOOK_PLUGIN_SDK_SOURCE_FOLDER = "docroot/WEB-INF/src"; //$NON-NLS-1$

    public static final String LAYOUTTPL_PLUGIN_SDK_CONFIG_FOLDER = "docroot"; //$NON-NLS-1$

    public static final String LAYOUTTPL_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER = "docroot/WEB-INF/classes"; //$NON-NLS-1$

    public static final String LIFERAY_DEFAULT_FACET_TEMPLATE = "liferay.portlet.template"; //$NON-NLS-1$

    public static final String LIFERAY_EXT_FACET_ID = "liferay.ext"; //$NON-NLS-1$

    public static final String LIFERAY_EXT_FACET_TEMPLATE_ID = "liferay.ext.template"; //$NON-NLS-1$

    public static final String LIFERAY_EXT_PRESET = "liferay.ext.preset"; //$NON-NLS-1$

    public static final IProjectFacet LIFERAY_EXT_PROJECT_FACET =
        ProjectFacetsManager.getProjectFacet( LIFERAY_EXT_FACET_ID );

    public static final String LIFERAY_HOOK_FACET_ID = "liferay.hook"; //$NON-NLS-1$

    public static final String LIFERAY_HOOK_FACET_TEMPLATE_ID = "liferay.hook.template"; //$NON-NLS-1$

    public static final String LIFERAY_HOOK_PRESET = "liferay.hook.preset"; //$NON-NLS-1$

    public static final IProjectFacet LIFERAY_HOOK_PROJECT_FACET =
        ProjectFacetsManager.getProjectFacet( LIFERAY_HOOK_FACET_ID );

    public static final String LIFERAY_LAYOUTTPL_FACET_ID = "liferay.layouttpl"; //$NON-NLS-1$

    public static final String LIFERAY_LAYOUTTPL_FACET_TEMPLATE_ID = "liferay.layouttpl.template"; //$NON-NLS-1$

    public static final String LIFERAY_LAYOUTTPL_PRESET = "liferay.layouttpl.preset"; //$NON-NLS-1$

    public static final IProjectFacet LIFERAY_LAYOUTTPL_PROJECT_FACET =
        ProjectFacetsManager.getProjectFacet( LIFERAY_LAYOUTTPL_FACET_ID );

    public static final String LIFERAY_PORTLET_FACET_ID = "liferay.portlet"; //$NON-NLS-1$

    public static final String LIFERAY_PORTLET_FACET_TEMPLATE_ID = "liferay.portlet.template"; //$NON-NLS-1$

    public static final String LIFERAY_PORTLET_PRESET = "liferay.portlet.preset"; //$NON-NLS-1$

    public static final IProjectFacet LIFERAY_PORTLET_PROJECT_FACET =
        ProjectFacetsManager.getProjectFacet( LIFERAY_PORTLET_FACET_ID );

    public static final String LIFERAY_SDK_NAME_DEFAULT_VALUE = "__NO__SDK__"; //$NON-NLS-1$

    public static final String LIFERAY_SDK_NAME_DEFAULT_VALUE_DESCRIPTION = "<None>"; //$NON-NLS-1$

    public static final String LIFERAY_THEME_FACET_ID = "liferay.theme"; //$NON-NLS-1$

    public static final String LIFERAY_THEME_FACET_TEMPLATE_ID = "liferay.theme.template"; //$NON-NLS-1$

    public static final String LIFERAY_THEME_PRESET = "liferay.theme.preset"; //$NON-NLS-1$

    public static final IProjectFacet LIFERAY_THEME_PROJECT_FACET =
        ProjectFacetsManager.getProjectFacet( LIFERAY_THEME_FACET_ID );

    public static final String PORTLET_PLUGIN_SDK_CONFIG_FOLDER = "docroot"; //$NON-NLS-1$

    public static final String PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER = "docroot/WEB-INF/classes"; //$NON-NLS-1$

    public static final String PORTLET_PLUGIN_SDK_SOURCE_FOLDER = "docroot/WEB-INF/src"; //$NON-NLS-1$

    public static final String THEME_PLUGIN_SDK_CONFIG_FOLDER = "docroot"; //$NON-NLS-1$

    public static final String THEME_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER = "docroot/WEB-INF/classes"; //$NON-NLS-1$
}
