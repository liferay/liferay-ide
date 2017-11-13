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

package com.liferay.ide.portlet.vaadin.core.operation;

import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;

import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara
 */
@SuppressWarnings("restriction")
public class NewVaadinApplicationClassOperation
	extends NewWebClassOperation implements INewPortletClassDataModelProperties {

	public NewVaadinApplicationClassOperation(IDataModel dataModel) {
		super(dataModel);
	}

	@Override
	protected CreateWebClassTemplateModel createTemplateModel() {
		return new CreateVaadinPortletTemplateModel(getDataModel());
	}

	@Override
	protected String getTemplateFile() {
		return TEMPLATE_FILE;
	}

	@Override
	protected Object getTemplateImplementation() {
		return VaadinPortletTemplate.create(null);
	}

	protected static final String TEMPLATE_DIR = "/templates/";

	protected static final String TEMPLATE_FILE = TEMPLATE_DIR + "application.javajet";

}