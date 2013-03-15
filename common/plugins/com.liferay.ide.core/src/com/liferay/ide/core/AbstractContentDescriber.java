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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.XMLContentDescriber;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Greg Amerson
 */
public abstract class AbstractContentDescriber extends XMLContentDescriber implements IExecutableExtension
{

    public AbstractContentDescriber()
    {
        super();
    }

    /*
     * (Intentionally not included in javadoc)
     * @see IContentDescriber#describe(InputStream, IContentDescription)
     */
    public int describe( InputStream contents, IContentDescription description ) throws IOException
    {
        // call the basic XML describer to do basic recognition
        if( super.describe( contents, description ) == INVALID )
        {
            return INVALID;
        }

        // super.describe will have consumed some chars, need to rewind
        contents.reset();

        // Check to see if we matched our criteria.
        return checkCriteria( new InputSource( contents ) );
    }

    /*
     * (Intentionally not included in javadoc)
     * @see IContentDescriber#describe(Reader, IContentDescription)
     */
    public int describe( Reader contents, IContentDescription description ) throws IOException
    {
        // call the basic XML describer to do basic recognition
        if( super.describe( contents, description ) == INVALID )
        {
            return INVALID;
        }

        // super.describe will have consumed some chars, need to rewind
        contents.reset();

        // Check to see if we matched our criteria.
        return checkCriteria( new InputSource( contents ) );
    }

    public void setInitializationData( IConfigurationElement config, String propertyName, Object data )
        throws CoreException
    {
    }

    private int checkCriteria( InputSource contents ) throws IOException
    {
        AbstractDefaultHandler contentHandler = createDefaultHandler();

        try
        {
            if( !contentHandler.parseContents( contents ) )
            {
                return INDETERMINATE;
            }
        }
        catch( SAXException e )
        {
            // we may be handed any kind of contents... it is normal we fail to
            // parse
            return INDETERMINATE;
        }
        catch( ParserConfigurationException e )
        {
            // some bad thing happened - force this describer to be disabled
            String message =
                "Internal Error: XML parser configuration error during content description for Service Builder files"; //$NON-NLS-1$

            throw new RuntimeException( message );
        }

        // Check to see if we matched our criteria.
        if( contentHandler.hasDTD() )
        {
            if( contentHandler.hasTopLevelElement() )
            {
                return VALID;
            }

            // only a top level project element...maybe an Ant buildfile
            return INDETERMINATE;
        }

        return INVALID;
    }

    protected abstract AbstractDefaultHandler createDefaultHandler();

}
