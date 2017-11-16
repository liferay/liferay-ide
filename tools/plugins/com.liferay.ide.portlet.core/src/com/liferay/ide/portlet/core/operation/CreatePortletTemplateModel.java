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

package com.liferay.ide.portlet.core.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class CreatePortletTemplateModel extends CreateWebClassTemplateModel {

	public CreatePortletTemplateModel(IDataModel dataModel) {
		super(dataModel);
	}

	public String getClassName() {
		return dataModel.getStringProperty(INewPortletClassDataModelProperties.CLASS_NAME);
	}

	@Override
	public Collection<String> getImports() {
		List<String> collectionList = new ArrayList<>();

		for (String importItem : super.getImports()) {
			if (importItem.contains("<") && importItem.contains(">")) {
				continue;
			}

			collectionList.add(importItem);
		}

		if (!isMVCPortletSuperclass()) {
			collectionList.add("java.io.IOException");
			collectionList.add("javax.portlet.PortletException");

			// collection.add("javax.portlet.PortletRequest");

			collectionList.add("javax.portlet.PortletRequestDispatcher");

			// collection.add("javax.portlet.PortletResponse");

			collectionList.add("javax.portlet.RenderRequest");
			collectionList.add("javax.portlet.RenderResponse");
			collectionList.add("com.liferay.portal.kernel.log.Log");
			collectionList.add("com.liferay.portal.kernel.log.LogFactoryUtil");
		}

		if (shouldGenerateOverride(INewPortletClassDataModelProperties.PROCESSACTION_OVERRIDE)) {
			collectionList.add("javax.portlet.ActionRequest");
			collectionList.add("javax.portlet.ActionResponse");
		}

		if (shouldGenerateOverride(INewPortletClassDataModelProperties.SERVERESOURCE_OVERRIDE)) {
			collectionList.add("javax.portlet.ResourceRequest");
			collectionList.add("javax.portlet.ResourceResponse");
		}

		return collectionList;
	}

	public String getInitParameterName() {
		return dataModel.getStringProperty(INewPortletClassDataModelProperties.INIT_PARAMETER_NAME);
	}

	public boolean hasPortletMode(String portletModeProperty) {
		return dataModel.getBooleanProperty(portletModeProperty);
	}

	public boolean isGenericPortletSuperclass() {
		return isGenericPortletSuperclass(false);
	}

	public boolean isGenericPortletSuperclass(boolean checkHierarchy) {
		return PortletSupertypesValidator.isGenericPortletSuperclass(dataModel, checkHierarchy);
	}

	public boolean isLiferayPortletSuperclass() {
		return PortletSupertypesValidator.isLiferayPortletSuperclass(dataModel);
	}

	public boolean isMVCPortletSuperclass() {
		return PortletSupertypesValidator.isMVCPortletSuperclass(dataModel);
	}

	public void setGenerateGenericInclude(boolean include) {
		generateGenericInclude = include;
	}

	public boolean shouldGenerateGenericInclude() {
		return generateGenericInclude;
	}

	public boolean shouldGenerateOverride(String generateProperty) {
		if (isMVCPortletSuperclass()) {
			return false;
		}

		return dataModel.getBooleanProperty(generateProperty);
	}

	protected boolean generateGenericInclude = false;

}