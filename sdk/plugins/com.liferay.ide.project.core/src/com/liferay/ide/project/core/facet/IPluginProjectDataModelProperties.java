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

package com.liferay.ide.project.core.facet;

import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public interface IPluginProjectDataModelProperties
    extends IJ2EEFacetProjectCreationDataModelProperties, IProjectCreationPropertiesNew
{

    public static final String CONFIGURE_DEPLOYMENT_ASSEMBLY = 
                    "IPluginProjectDataModelProperties.CONFIGURE_DEPLOYMENT_ASSEMBLY"; //$NON-NLS-1$

    public static final String CREATE_PROJECT_OPERATION = "IPluginProjectDataModelProperties.CREATE_PROJECT_OPERATION"; //$NON-NLS-1$

    public static final String DISPLAY_NAME = "IPluginProjectCreationPropertiesNew.DISPLAY_NAME"; //$NON-NLS-1$

    public static final String EXT_NAME = "IPluginProjectDataModelProperties.EXT_NAME"; //$NON-NLS-1$

    public static final String HOOK_NAME = "IPluginProjectDataModelProperties.HOOK_NAME"; //$NON-NLS-1$

    public static final String INSTALL_LIFERAY_PLUGIN_LIBRARY_DELEGATE =
        "IPluginProjectDataModelProperties.INSTALL_LIFERAY_PLUGIN_LIBRARY_DELEGATE"; //$NON-NLS-1$

    public static final String LAYOUTTPL_NAME = "IPluginProjectDataModelProperties.LAYOUTTPL_NAME"; //$NON-NLS-1$

    public static final String LIFERAY_PLUGIN_LIBRARY_DELEGATE =
        "IPluginProjectDataModelProperties.LIFERAY_PLUGIN_LIBRARY_DELEGATE"; //$NON-NLS-1$

    public static final String LIFERAY_SDK_NAME = "IPluginProjectDataModelProperties.LIFERAY_SDK_NAME"; //$NON-NLS-1$

    public static final String LIFERAY_USE_CUSTOM_LOCATION =
        "IPluginProjectDataModelProperties.LIFERAY_USE_CUSTOM_LOCATION"; //$NON-NLS-1$

    public static final String LIFERAY_USE_SDK_LOCATION = "IPluginProjectDataModelProperties.LIFERAY_USE_SDK_LOCATION"; //$NON-NLS-1$

    public static final String PLUGIN_FRAGMENT_DM = "IPluginProjectDataModelProperties.PLUGIN_FRAGMENT_DM"; //$NON-NLS-1$

    public static final String PLUGIN_FRAGMENT_ENABLED = "IPluginProjectDataModelProperties.PLUGIN_FRAGMENT_ENABLED"; //$NON-NLS-1$

    public static final String PLUGIN_TYPE_EXT = "IPluginProjectDataModelProperties.PLUGIN_TYPE_EXT"; //$NON-NLS-1$

    public static final String PLUGIN_TYPE_HOOK = "IPluginProjectDataModelProperties.PLUGIN_TYPE_HOOK"; //$NON-NLS-1$

    public static final String PLUGIN_TYPE_LAYOUTTPL = "IPluginProjectDataModelProperties.PLUGIN_TYPE_LAYOUTTPL"; //$NON-NLS-1$

    public static final String PLUGIN_TYPE_PORTLET = "IPluginProjectDataModelProperties.PLUGIN_TYPE_PORTLET"; //$NON-NLS-1$

    public static final String PLUGIN_TYPE_THEME = "IPluginProjectDataModelProperties.PLUGIN_TYPE_THEME"; //$NON-NLS-1$

    public static final String PORTLET_FRAMEWORK_ID = "IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID"; //$NON-NLS-1$

    public static final String PORTLET_NAME = "IPluginProjectDataModelProperties.PORTLET_NAME"; //$NON-NLS-1$

    public static final String SETUP_DEFAULT_OUTPUT_LOCATION =
        "IPluginProjectDataModelProperties.SETUP_DEFAULT_OUTPUT_LOCATION"; //$NON-NLS-1$

    public static final String THEME_NAME = "IPluginProjectDataModelProperties.THEME_NAME"; //$NON-NLS-1$
}
