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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.core;

import com.liferay.ide.project.core.AbstractProjectDefinition;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;

import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.JavaFacetUtils;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "deprecation" )
public class LiferayHookDefinition extends AbstractProjectDefinition implements IPluginProjectDataModelProperties
{

    public LiferayHookDefinition()
    {
        super();
    }

    public void setupNewProjectDefinition( IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject )
    {
        FacetDataModelMap map = (FacetDataModelMap) dataModel.getProperty( FACET_DM_MAP );
        IDataModel javaFacetModel = map.getFacetDataModel( JavaFacetUtils.JAVA_FACET.getId() );
        IDataModel webFacetModel = map.getFacetDataModel( IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId() );

        webFacetModel.setStringProperty(
            IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.HOOK_PLUGIN_SDK_CONFIG_FOLDER );
        webFacetModel.setStringProperty(
            IWebFacetInstallDataModelProperties.SOURCE_FOLDER, IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER );
        javaFacetModel.setStringProperty(
            IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
            IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER );
        javaFacetModel.setStringProperty(
            IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
            IPluginFacetConstants.HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER );
    }

}
