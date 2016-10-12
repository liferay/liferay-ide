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

package com.liferay.ide.project.core.modules.templates.rest;

import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;
import com.liferay.ide.project.core.modules.templates.BndProperties;
import com.liferay.ide.project.core.modules.templates.BndPropertiesValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentRestOperation extends AbstractLiferayComponentTemplate
{

    private static final String TEMPLATE_FILE = "rest/rest.ftl";

    private final static String SUPER_CLASS = "Application";
    private final static String EXTENSION_CLASS = "Application.class";

    private final static String[] PROPERTIES_LIST = new String[] { "jaxrs.application=true" };

    public NewLiferayComponentRestOperation()
    {
        super();
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.add( "com.liferay.portal.kernel.model.User" );
        imports.add( "com.liferay.portal.kernel.service.UserLocalService" );    
        imports.add( "java.util.Collections" );
        imports.add( "java.util.Set" );
        imports.add( "javax.ws.rs.GET" );
        imports.add( "javax.ws.rs.Path" );
        imports.add( "javax.ws.rs.Produces" );
        imports.add( "javax.ws.rs.core.Application" );
        imports.add( "org.osgi.service.component.annotations.Reference" );
        imports.addAll( super.getImports() );

        return imports;
    }

    @Override
    protected List<String> getProperties()
    {
        List<String> properties = new ArrayList<String>();
        properties.addAll( Arrays.asList( PROPERTIES_LIST ) );

        for( String property : super.getProperties() )
        {
            properties.add( property );
        }
        return properties;
    }

    @Override
    protected String getExtensionClass()
    {
        return EXTENSION_CLASS;
    }

    @Override
    protected String getSuperClass()
    {
        return SUPER_CLASS;
    }

    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }

    @Override
    protected List<String[]> getComponentDependency() throws CoreException
    {
        List<String[]> componentDependency = super.getComponentDependency();
        componentDependency.add( new String[]{ "javax.ws.rs", "javax.ws.rs-api", "2.0.1"} );
        return componentDependency;
    }

    @Override
    protected void setBndProperties( BndProperties bndProperty )
    {
        bndProperty.addValue( "Require-Capability", new BndPropertiesValue( "osgi.contract; filter:=\"(&(osgi.contract=JavaJAXRS)(version=2))\"" ) );
        bndProperty.addValue( "-sources", new BndPropertiesValue( "true" ) );
    }
}
