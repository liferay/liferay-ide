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
package com.liferay.ide.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;


/**
 * @author Gregory Amerson
 */
public class LiferayProjectProviderReader extends ExtensionReader<ILiferayProjectProvider>
{
    private static final String ATTRIBUTE_DEFAULT = "default"; //$NON-NLS-1$
    private static final String ATTRIBUTE_DISPLAYNAME = "displayName"; //$NON-NLS-1$
    private static final String ATTRIBUTE_PRIORITY = "priority"; //$NON-NLS-1$
    private static final String ATTRIBUTE_SHORTNAME = "shortName"; //$NON-NLS-1$
    private static final String ATTRIBUTE_PROJECTTYPE = "projectType"; //$NON-NLS-1$
    private static final String EXTENSION = "liferayProjectProviders"; //$NON-NLS-1$
    private static final String PROVIDER_ELEMENT = "liferayProjectProvider"; //$NON-NLS-1$

    public LiferayProjectProviderReader()
    {
        super( LiferayCore.PLUGIN_ID, EXTENSION, PROVIDER_ELEMENT );
    }

    public ILiferayProjectProvider[] getProviders()
    {
        return getExtensions().toArray( new ILiferayProjectProvider[0] );
    }

    public ILiferayProjectProvider[] getProviders( Class<?> type )
    {
        final List<ILiferayProjectProvider> providers = new ArrayList<ILiferayProjectProvider>();

        for( ILiferayProjectProvider provider : getExtensions() )
        {
            if( provider.provides( type ) )
            {
                providers.add( provider );
            }
        }

        return providers.toArray( new ILiferayProjectProvider[0] );
    }

    @Override
    protected ILiferayProjectProvider initElement( IConfigurationElement configElement, ILiferayProjectProvider provider )
    {
        final String shortName = configElement.getAttribute( ATTRIBUTE_SHORTNAME );
        final String displayName = configElement.getAttribute( ATTRIBUTE_DISPLAYNAME );
        final String priority = configElement.getAttribute( ATTRIBUTE_PRIORITY );
        final String type = configElement.getAttribute( ATTRIBUTE_PROJECTTYPE );
        final boolean isDefault = Boolean.parseBoolean( configElement.getAttribute( ATTRIBUTE_DEFAULT ) );

        final AbstractLiferayProjectProvider projectProvider = (AbstractLiferayProjectProvider) provider;

        projectProvider.setShortName( shortName );
        projectProvider.setDisplayName( displayName );
        projectProvider.setProjectType( type );

        int priorityValue = 10;

        if( "lowest".equals( priority ) ) //$NON-NLS-1$
        {
            priorityValue = 1;
        }
        else if( "low".equals( priority ) ) //$NON-NLS-1$
        {
            priorityValue = 2;
        }
        else if( "normal".equals( priority ) ) //$NON-NLS-1$
        {
            priorityValue = 3;
        }
        else if( "high".equals( priority ) ) //$NON-NLS-1$
        {
            priorityValue = 4;
        }
        else if( "highest".equals( priority ) ) //$NON-NLS-1$
        {
            priorityValue = 5;
        }

        projectProvider.setPriority( priorityValue );
        projectProvider.setDefault( isDefault );

        return provider;
    }

    public ILiferayProjectProvider[] getProviders( String projectType )
    {
        final List<ILiferayProjectProvider> retval = new ArrayList<>();

        final ILiferayProjectProvider[] providers = getProviders();

        for( ILiferayProjectProvider provider : providers )
        {
            if( provider.getProjectType().equals( projectType ) )
            {
                retval.add( provider );
            }
        }

        return retval.toArray( new ILiferayProjectProvider[0] );
    }

}
