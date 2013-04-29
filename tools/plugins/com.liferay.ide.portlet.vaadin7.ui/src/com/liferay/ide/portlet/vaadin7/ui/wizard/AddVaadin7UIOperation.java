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

package com.liferay.ide.portlet.vaadin7.ui.wizard;

import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.portlet.ui.wizard.AddPortletOperation;
import com.liferay.ide.portlet.vaadin7.core.dd.Vaadin7PortletDescriptorHelper;
import com.liferay.ide.portlet.vaadin7.core.operation.INewVaadin7PortletClassDataModelProperties;
import com.liferay.ide.portlet.vaadin7.core.operation.NewVaadin7UIClassOperation;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaEEArtifactClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara
 */
@SuppressWarnings( "restriction" )
public class AddVaadin7UIOperation extends AddPortletOperation
    implements INewVaadin7PortletClassDataModelProperties
{

    public AddVaadin7UIOperation( IDataModel dataModel, TemplateStore type, TemplateContextType store )
    {
        super( dataModel, type, store );
    }

    protected NewJavaEEArtifactClassOperation getNewClassOperation()
    {
        return new NewVaadin7UIClassOperation( getDataModel() );
    }

    @Override
    protected PortletDescriptorHelper createPortletDescriptorHelper( IProject targetProject )
    {
        // also adds a dependency to jsoup.jar, vaadin-server.jar, vaadin-shared.jar, and vaadin-shared-deps.jar in
        // liferay-plugin-package.properties
        return new Vaadin7PortletDescriptorHelper( targetProject );
    }

}
