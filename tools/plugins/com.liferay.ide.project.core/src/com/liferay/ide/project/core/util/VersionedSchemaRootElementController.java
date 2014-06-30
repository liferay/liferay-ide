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


import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.StandardRootElementController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author Gregory Amerson
 */
public class VersionedSchemaRootElementController extends StandardRootElementController
{
    private final String xmlBindingPath;
    private final Pattern namespacePattern;
    private final Pattern schemaPattern;
    private final String namespaceTemplate;
    private final String schemaTemplate;
    private final String defaultVersion;

    private RootElementInfo rootElementInfo;

    public VersionedSchemaRootElementController( final String xmlBindingPath,
                                                 final Pattern namespacePattern,
                                                 final Pattern schemaPattern,
                                                 final String namespaceTemplate,
                                                 final String schemaTemplate,
                                                 final String defaultVersion )
    {
        this.xmlBindingPath = xmlBindingPath;
        this.namespacePattern = namespacePattern;
        this.schemaPattern = schemaPattern;
        this.namespaceTemplate = namespaceTemplate;
        this.schemaTemplate = schemaTemplate;
        this.defaultVersion = defaultVersion;
    }

    private Document getDocument()
    {
        return this.resource().root().adapt( RootXmlResource.class ).getDomDocument();
    }

    @Override
    protected RootElementInfo getRootElementInfo()
    {
        if( this.rootElementInfo == null )
        {
            final Map<String, String> schemas = new HashMap<String, String>();
            final Document doc = getDocument();

            String namespace = null;
            String uri = null;
            String location = null;

            if( doc != null )
            {
                final Element documentElement = doc.getDocumentElement();

                if( documentElement != null )
                {
                    uri = documentElement.getNamespaceURI();

                    if( uri != null && namespacePattern.matcher( uri ).matches() )
                    {
                        namespace = uri;

                        final String schema = documentElement.getAttribute( "xsi:schemaLocation" );

                        final Matcher matcher = schemaPattern.matcher( schema );

                        if( schema != null && matcher.matches() )
                        {
                            location = matcher.group( 1 );
                        }
                    }
                }
                else
                {
                    String version = defaultVersion;

                    // no documentElement lets get default values
                    final IProject project = resource().adapt( IProject.class );

                    if( project != null )
                    {
                        version = LiferayDescriptorHelper.getDescriptorVersion( project, defaultVersion );
                    }

                    namespace = MessageFormat.format( this.namespaceTemplate, version );
                    uri = namespace;
                    location = MessageFormat.format( this.schemaTemplate, version.replaceAll( "\\.", "_" ) );
                }
            }

            schemas.put( uri, location );
            this.rootElementInfo = new RootElementInfo( namespace, "", this.xmlBindingPath, schemas );
        }

        return this.rootElementInfo;
    }
}
