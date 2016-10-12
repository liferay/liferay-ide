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

package com.liferay.ide.project.core.modules.templates.portlet;

import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentPortletOperation extends AbstractLiferayComponentTemplate
{
	private static final String TEMPLATE_FILE = "portlet/portlet.ftl";
    private final static String SUPER_CLASS = "GenericPortlet";
    private final static String EXTENSION_CLASS = "Portlet.class";

    private final static String[] PROPERTIES_LIST =
        new String[] {
            "com.liferay.portlet.display-category=category.sample", 
            "com.liferay.portlet.instanceable=true",
            "javax.portlet.security-role-ref=power-user,user" 
        };
    
    public NewLiferayComponentPortletOperation()
    {
        super();
    }

    @Override
    protected String getTemplateFile()
    {
        return TEMPLATE_FILE;
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.add( "javax.portlet.Portlet" );
        imports.add( "java.io.IOException" );
        imports.add( "java.io.PrintWriter" );
        imports.add( "javax.portlet.GenericPortlet" );
        imports.add( "javax.portlet.PortletException" );
        imports.add( "javax.portlet.RenderRequest" );
        imports.add( "javax.portlet.RenderResponse" );

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

        properties.add( "javax.portlet.display-name=" + componentNameWithoutTemplateName + " Portlet" );
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
    protected List<String[]> getComponentDependency() throws CoreException
    {
        List<String[]> componentDependency = super.getComponentDependency();
        componentDependency.add( new String[]{ "javax.portlet", "portlet-api", "2.0"} );
        return componentDependency;
    }
}
