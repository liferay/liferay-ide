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
 * @author Cindy Li
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class CreateEntryTemplateModel extends CreateWebClassTemplateModel {

	public CreateEntryTemplateModel(IDataModel dataModel) {
		super(dataModel);
	}

	@Override
	public String getClassName() {
		return dataModel.getStringProperty(INewPortletClassDataModelProperties.ENTRY_CLASS_NAME);
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

		collectionList.add("com.liferay.portlet.BaseControlPanelEntry");
		collectionList.add("com.liferay.portal.model.Portlet");
		collectionList.add("com.liferay.portal.security.permission.PermissionChecker");

		return collectionList;
	}

	protected boolean generateGenericInclude = false;

}