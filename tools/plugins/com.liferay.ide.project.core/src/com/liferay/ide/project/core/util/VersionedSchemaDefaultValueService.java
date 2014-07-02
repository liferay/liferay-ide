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
package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.StringPool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * @author Gregory Amerson
 */
public class VersionedSchemaDefaultValueService extends DefaultValueService
{
    private final Pattern namespacePattern;
    private final String defaultVersion;

    public VersionedSchemaDefaultValueService( Pattern namespacePattern, String defaultVersion )
    {
        this.namespacePattern = namespacePattern;
        this.defaultVersion = defaultVersion;
    }

    @Override
    protected String compute()
    {
        String version = defaultVersion;

        final RootXmlResource resource = context( Element.class ).resource().adapt( RootXmlResource.class );

        if( resource != null )
        {
            final Document document = resource.getDomDocument();

            if( document != null )
            {
                final Node node = document.getDocumentElement();

                if( node != null )
                {
                    final String namespace = node.getNamespaceURI();
                    final Matcher matcher = this.namespacePattern.matcher( namespace );

                    if( matcher.matches() )
                    {
                        version = matcher.group( 1 );
                    }
                }
            }
        }

        return version.replaceAll( StringPool.UNDERSCORE, "." );
    }

}
