/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.core;

import com.liferay.ide.eclipse.project.core.AbstractProjectDefinition;
import com.liferay.ide.eclipse.project.core.IPortletFramework;
import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * @author Greg Amerson
 */
public class LiferayPortletDefinition extends AbstractProjectDefinition implements IPluginProjectDataModelProperties {

	protected IDataModel nestedModel;

	public LiferayPortletDefinition() {
		super();
	}

	public void setupNewProject(IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject) {
		ProjectUtil.setGenerateDD(dataModel, true);

		FacetDataModelMap map = (FacetDataModelMap) dataModel.getProperty(FACET_DM_MAP);
		IDataModel webFacetModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());

		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.PORTLET_PLUGIN_SDK_CONFIG_FOLDER);
		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.SOURCE_FOLDER, IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);
		IDataModel javaFacetModel = map.getFacetDataModel(JavaFacet.FACET.getId());
		javaFacetModel.setStringProperty(
			IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
			IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);
		javaFacetModel.setStringProperty(
			IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
			IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);

		if (dataModel.isNestedModel(PLUGIN_FRAGMENT_DM)) {
			dataModel.removeNestedModel(PLUGIN_FRAGMENT_DM);
		}

		// need to allow portlet framework to do any additional configuration

		IPortletFramework portletFramework = (IPortletFramework) dataModel.getProperty(PORTLET_FRAMEWORK);

		portletFramework.setupNewProject(dataModel, facetedProject);
	}
}
