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
package com.liferay.ide.xml.search.core.resources;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.resource.ResourceBaseURIResolver;


/**
 * @author Kuo Zhang
 */
public class LanguagePropertiesURIResolver extends ResourceBaseURIResolver
{
    public static final LanguagePropertiesURIResolver INSTANCE = new LanguagePropertiesURIResolver();

    private static final Set<String> EXTENSIONS;

    static
    {
        EXTENSIONS = new HashSet<String>();
        EXTENSIONS.add( "properties" );
    }

    public LanguagePropertiesURIResolver()
    {
        super();
    }

    protected Set<String> getExtensions()
    {
        return EXTENSIONS;
    }

    @Override
    public boolean accept( Object selectedNode, IResource rootContainer, IResource file, String matching, boolean fullMatch )
    {
        final String extension = file.getFileExtension();

        if( ! getExtensions().contains( extension.toLowerCase() ) )
        {
            return false;
        }

        if( matching != null )
        {
            final String uri = resolve( selectedNode, rootContainer, file ).toLowerCase();

            matching = matching.toLowerCase();

            if( matching.contains( "*" ) )
            {
                matching = matching.replace( "*", ".*" );

                return uri.matches( matching );
            }
            else
            {
                return uri.startsWith( matching );
            }
        }

        return false;
    }
}
