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

package com.liferay.ide.portlet.vaadin.core.dd;

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.portlet.vaadin.core.operation.INewVaadinPortletClassDataModelProperties;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * Helper for editing various portlet configuration XML files, to add Vaadin
 * portlet configuration to them. Also supports adding a dependency to Vaadin in
 * liferay-plugin-package.properties (if necessary).
 *
 * @author Henri Sara
 * @author Tao Tao
 * @author Kuo Zhang
 */
public class VaadinPortletDescriptorHelper
	extends PortletDescriptorHelper implements INewVaadinPortletClassDataModelProperties {

	public VaadinPortletDescriptorHelper() {
	}

	public VaadinPortletDescriptorHelper(IProject project) {
		super(project);
	}

	@Override
	public boolean canAddNewPortlet(IDataModel model) {
		return StringUtil.contains(model.getID(), "NewVaadinPortlet");
	}

	@Override
	protected String getPortletClassText(IDataModel model) {
		return model.getStringProperty(VAADIN_PORTLET_CLASS);
	}

}