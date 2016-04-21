/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.project.core.modules.templates.servicewrapper;

import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentServiceOperation extends AbstractLiferayComponentTemplate
{

    private static final String TEMPLATE_FILE = "servicewrapper/servicewrapper.ftl";
    private String serviceClassName;

    public NewLiferayComponentServiceOperation()
    {
        super();
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();
        imports.add( "com.liferay.portal.kernel.exception.PortalException" );
        imports.add( "com.liferay.portal.kernel.model.User" );
        imports.add( "com.liferay.portal.kernel.service.ServiceWrapper" );
        imports.add( serviceName );
        imports.add( "java.util.Map" );
        imports.addAll( super.getImports() );
        return imports;
    }

    @Override
    protected String getSuperClass()
    {
        if( serviceName != null )
        {
            int servicePos = serviceName.lastIndexOf( "." );

            serviceClassName = serviceName.substring( servicePos + 1 );
        }

        return serviceClassName;
    }

    @Override
    protected String getExtensionClass()
    {
        return "ServiceWrapper.class";
    }

    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }
}
