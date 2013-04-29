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

package com.liferay.ide.portlet.vaadin7.core.operation;

import java.util.Collection;

import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara
 */
@SuppressWarnings( "restriction" )
public class CreateVaadin7PortletTemplateModel extends CreateWebClassTemplateModel
{
    protected boolean generateGenericInclude = false;

    public CreateVaadin7PortletTemplateModel( IDataModel dataModel )
    {
        super( dataModel );
    }

    public String getClassName()
    {
        return dataModel.getStringProperty( INewJavaClassDataModelProperties.CLASS_NAME );
    }

    @Override
    public Collection<String> getImports()
    {
        Collection<String> collection = super.getImports();

        collection.add( "com.vaadin.server.VaadinRequest" ); //$NON-NLS-1$
        collection.add( "com.vaadin.ui.Label" ); //$NON-NLS-1$
        collection.add( "com.vaadin.ui.UI" ); //$NON-NLS-1$
        collection.add( "com.vaadin.ui.VerticalLayout" ); //$NON-NLS-1$

        return collection;
    }

}
