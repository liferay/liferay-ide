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

package com.liferay.ide.portlet.core.operation;

import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewPortletClassOperation extends NewWebClassOperation
{

    protected static final String TEMPLATE_DIR = "/templates/"; //$NON-NLS-1$

    protected static final String TEMPLATE_FILE = TEMPLATE_DIR + "portlet.javajet"; //$NON-NLS-1$

    public NewPortletClassOperation( IDataModel dataModel )
    {
        super( dataModel );
    }

    @Override
    protected CreateWebClassTemplateModel createTemplateModel()
    {
        return new CreatePortletTemplateModel( getDataModel() );
    }

    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }

    @Override
    protected Object getTemplateImplementation()
    {
        return PortletTemplate.create( null );
    }

    // protected String generateTemplateSource(WTPPlugin plugin,
    // CreateJavaEEArtifactTemplateModel templateModel,
    // String templateFile, Object templateImpl, IProgressMonitor monitor)
    // throws JETException {
    // always use the template file version
    // return generateTemplateSource(plugin, templateModel, templateFile,
    // monitor);
    // }
}
