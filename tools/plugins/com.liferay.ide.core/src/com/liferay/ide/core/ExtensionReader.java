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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;


/**
 * @author Gregory Amerson
 */
public abstract class ExtensionReader<T> extends RegistryReader
{

    private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$

    private String element;
    private Map<String, T> extensions = new HashMap<String, T>();
    private boolean hasInitialized = false;

    public ExtensionReader( String pluginID, String extension, String element )
    {
        super( pluginID, extension );

        this.element = element;
    }

    public List<T> getExtensions()
    {
        if( !this.hasInitialized )
        {
            readRegistry();

            this.hasInitialized = true;
        }

        List<T> adapters = new LinkedList<T>();

        for( T adapter : this.extensions.values() )
        {
            adapters.add( adapter );
        }

        return adapters;
    }

    protected abstract T initElement( IConfigurationElement configElement, T execExt );

    @SuppressWarnings( "unchecked" )
    @Override
    public boolean readElement( IConfigurationElement element )
    {
        if ( this.element.equals( element.getName() ) )
        {
            String id = element.getAttribute( ATTRIBUTE_ID );

            try
            {
                T execExt = (T) element.createExecutableExtension( ATTRIBUTE_CLASS );

                execExt = initElement( element, execExt );

                if( execExt != null )
                {
                    this.extensions.put( id, execExt );
                }
            }
            catch( CoreException e )
            {
                LiferayCore.logError( e );
            }
        }

        return true;
    }

}
