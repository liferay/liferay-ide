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

package com.liferay.ide.project.core.modules.templates.modellistener;

import com.liferay.ide.project.core.modules.ServiceContainer;
import com.liferay.ide.project.core.modules.templates.AbstractLiferayComponentTemplate;
import com.liferay.ide.project.core.util.TargetPlatformUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Simon Jiang
 */

public class NewLiferayComponentModelListenerOperation extends AbstractLiferayComponentTemplate
{

    private static final String TEMPLATE_FILE = "modellistener/modellistener.ftl";

    private final static String SUPER_CLASS = "BaseModelListener";
    private final static String EXTENSION_CLASS = "ModelListener.class";

    private final static String[] PROPERTIES_LIST = new String[] {};

    public NewLiferayComponentModelListenerOperation()
    {
        super();
    }

    @Override
    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();
        
        imports.add( "com.liferay.portal.kernel.exception.ModelListenerException" );
        imports.add( "com.liferay.portal.kernel.model.BaseModelListener" );
        imports.add( modelClass );
        imports.add( "com.liferay.portal.kernel.model.ModelListener" );
        
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
        try
        {
            ServiceContainer serviceBundle = TargetPlatformUtil.getServiceBundle( modelClass );

            if ( serviceBundle != null )
            {
                componentDependency.add( new String[]{ serviceBundle.getBundleGroup(), serviceBundle.getBundleName(), serviceBundle.getBundleVersion() } );    
            }
        }
        catch( Exception e )
        {
        }

        return componentDependency;
    }
}
