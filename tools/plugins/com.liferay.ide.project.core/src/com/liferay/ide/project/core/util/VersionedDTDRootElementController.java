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
package com.liferay.ide.project.core.util;


import com.liferay.ide.core.util.StringPool;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.StandardRootElementController;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;


/**
 * @author Gregory Amerson
 */
public class VersionedDTDRootElementController extends StandardRootElementController
{
    private final Pattern publicIdPattern;
    private final String publicIdTemplate;
    private RootElementInfo rootElementInfo;
    private final Pattern systemIdPattern;
    private final String systemIdTemplate;

    private final String xmlBindingPath;

    public VersionedDTDRootElementController( final String xmlBindingPath,
                                              final String publicIdTemplate,
                                              final String systemIdTemplate,
                                              final Pattern publicIdPattern,
                                              final Pattern systemIdPattern )
    {
        this.xmlBindingPath = xmlBindingPath;
        this.publicIdTemplate = publicIdTemplate;
        this.systemIdTemplate = systemIdTemplate;
        this.publicIdPattern = publicIdPattern;
        this.systemIdPattern = systemIdPattern;
    }

    private boolean checkDocType()
    {
        try
        {
            Document document = getDocument();

            if( document != null )
            {
                DocumentType docType = document.getDoctype();

                if( docType != null)
                {
                    Matcher publicIdMatcher = this.publicIdPattern.matcher( docType.getPublicId() );

                    if( publicIdMatcher.matches() )
                    {
                        String version = publicIdMatcher.group( 1 );

                        if( version != null )
                        {
                            Matcher systemIdMatcher = this.systemIdPattern.matcher( docType.getSystemId() );

                            if( systemIdMatcher.matches() )
                            {
                                String systemIdVersion = systemIdMatcher.group( 1 );

                                if( systemIdVersion != null )
                                {
                                    if( systemIdVersion.replaceAll( StringPool.UNDERSCORE, StringPool.EMPTY )
                                            .equals( version.replaceAll( "\\.", StringPool.EMPTY ) ) ) //$NON-NLS-1$
                                    {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            // fail back to the default
        }

        return false;
    }

    @Override
    public boolean checkRootElement()
    {
        boolean checkRoot = super.checkRootElement();

        if( checkRoot )
        {
            return checkDocType();
        }
        else
        {
            return false;
        }
    }

    @Override
    public void createRootElement()
    {
        super.createRootElement();

        if( ! checkDocType() )
        {
            final IProject project = this.resource().adapt( IProject.class );

            final String defaultVersion = new LiferayDescriptorHelper( project ).getDescriptorVersion();

            DocumentType existingDocType = getDocument().getDoctype();

            if( existingDocType != null )
            {
                getDocument().removeChild( existingDocType );
            }

            final String publicId = MessageFormat.format( this.publicIdTemplate, defaultVersion );
            final String systemId = MessageFormat.format( this.systemIdTemplate, defaultVersion.replaceAll( "\\.", "_" ) ); //$NON-NLS-1$ //$NON-NLS-2$

            final DocumentType newDocType =
                getDocument().getImplementation().createDocumentType( this.xmlBindingPath, publicId, systemId );

            if (newDocType != null)
            {
                getDocument().insertBefore(newDocType, getDocument().getDocumentElement() );
            }
        }
    }

    private Document getDocument()
    {
        RootXmlResource rootXmlResource = (RootXmlResource) this.resource().root();
        return rootXmlResource.getDomDocument();
    }

    @Override
    protected RootElementInfo getRootElementInfo()
    {
        if( this.rootElementInfo == null )
        {
            this.rootElementInfo = new RootElementInfo( null, null, this.xmlBindingPath, null );
        }

        return this.rootElementInfo;
    }
}
