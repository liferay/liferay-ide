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
package com.liferay.ide.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.core.util.RegistryReader;


/**
 * @author Gregory Amerson
 */
public class LiferayProjectProviderReader extends RegistryReader
{

    private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
    private static final String EXTENSION = "liferayProjectProviders"; //$NON-NLS-1$
    private static final String PROVIDER_ELEMENT = "liferayProjectProvider"; //$NON-NLS-1$

    private boolean hasInitialized = false;
    private Map<String, ILiferayProjectProvider> providerExtensions = new HashMap<String, ILiferayProjectProvider>();

    public LiferayProjectProviderReader()
    {
        super( LiferayCore.PLUGIN_ID, EXTENSION );
    }

    public ILiferayProjectProvider[] getProviders( Class<?> type )
    {
        if( type == null )
        {
            return null;
        }

        if( !this.hasInitialized )
        {
            readRegistry();

            this.hasInitialized = true;
        }

        List<ILiferayProjectProvider> providers = new LinkedList<ILiferayProjectProvider>();

        for( ILiferayProjectProvider provider : this.providerExtensions.values() )
        {
            if( provider.provides( type ) )
            {
                providers.add( provider );
            }
        }

        return providers.toArray( new ILiferayProjectProvider[0] );
    }

    @Override
    public boolean readElement( IConfigurationElement element )
    {
        if ( PROVIDER_ELEMENT.equals( element.getName() ) )
        {
            String id = element.getAttribute( ATTRIBUTE_ID );

            try
            {
                ILiferayProjectProvider provider =
                    (ILiferayProjectProvider) element.createExecutableExtension( ATTRIBUTE_CLASS );

                this.providerExtensions.put( id, provider );
            }
            catch( CoreException e )
            {
                LiferayCore.logError( e );
            }
        }

        return true;
    }

}
