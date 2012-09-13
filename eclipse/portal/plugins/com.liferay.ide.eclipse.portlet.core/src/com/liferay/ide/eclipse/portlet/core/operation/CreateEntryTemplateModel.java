/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.core.operation;

import java.util.Collection;

import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Cindy Li
 */
@SuppressWarnings("restriction")
public class CreateEntryTemplateModel extends CreateWebClassTemplateModel {

    protected boolean generateGenericInclude = false;

    public CreateEntryTemplateModel(IDataModel dataModel) {
        super(dataModel);
    }

    @Override
    public String getClassName() {
        return dataModel.getStringProperty(INewPortletClassDataModelProperties.ENTRY_CLASS_NAME);
    }

    @Override
    public Collection<String> getImports() {
        Collection<String> collection = super.getImports();
        collection.add( "com.liferay.portlet.BaseControlPanelEntry" );
        collection.add( "com.liferay.portal.model.Portlet");
        collection.add( "com.liferay.portal.security.permission.PermissionChecker" );

        return collection;
    }
}
