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

package com.liferay.ide.portlet.vaadin.core.operation;

import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.portlet.core.operation.AddPortletOperation;
import com.liferay.ide.portlet.vaadin.core.dd.VaadinPortletDescriptorHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaEEArtifactClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara
 */
@SuppressWarnings( "restriction" )
public class AddVaadinApplicationOperation extends AddPortletOperation
    implements INewVaadinPortletClassDataModelProperties
{

    public AddVaadinApplicationOperation( IDataModel dataModel )
    {
        super( dataModel );
    }

    protected NewJavaEEArtifactClassOperation getNewClassOperation()
    {
        return new NewVaadinApplicationClassOperation( getDataModel() );
    }

    @Override
    protected PortletDescriptorHelper createPortletDescriptorHelper( IProject targetProject )
    {
        // also adds a dependency to vaadin.jar in
        // liferay-plugin-package.properties
        return new VaadinPortletDescriptorHelper( targetProject );
    }

}
