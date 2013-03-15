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

import com.liferay.ide.core.util.StringPool;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Greg Amerson
 */
public abstract class AbstractDefaultHandler extends DefaultHandler
{
    /**
     * An exception indicating that the parsing should stop.
     * 
     * @since 3.1
     */
    protected class StopParsingException extends SAXException
    {
        /**
         * All serializable objects should have a stable serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constructs an instance of <code>StopParsingException</code> with a <code>null</code> detail message.
         */
        public StopParsingException()
        {
            super( (String) null );
        }
    }

    protected boolean dtd = false;
    protected SAXParserFactory fFactory;
    protected int fLevel = -1;
    protected String fTopElementFound;
    protected String publicIdPrefix;
    protected String publicIdSuffix;
    protected String rootElement;
    protected String systemIdPrefix;
    protected String systemIdSuffix;
    protected boolean topLevelElement = false;

    public AbstractDefaultHandler(
        String publicIdPrefix, String publicIdSuffix, String systemIdPrefix, String systemIdSuffix, String rootElement )
    {
        this.publicIdPrefix = publicIdPrefix;
        this.publicIdSuffix = publicIdSuffix;
        this.systemIdPrefix = systemIdPrefix;
        this.systemIdSuffix = systemIdSuffix;
        this.rootElement = rootElement;
    }

    public boolean hasDTD()
    {
        return this.dtd;
    }

    public boolean hasTopLevelElement()
    {
        return this.topLevelElement;
    }

    @Override
    public InputSource resolveEntity( String publicId, String systemId ) throws IOException, SAXException
    {
        if( systemId != null && systemId.startsWith( systemIdPrefix ) && systemId.endsWith( systemIdSuffix ) &&
            publicId != null && publicId.startsWith( publicIdPrefix ) && publicId.endsWith( publicIdSuffix ) )
        {

            this.dtd = true;
        }

        return new InputSource( new StringReader( StringPool.EMPTY ) );
    }

    @Override
    public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException
    {
        fLevel++;

        if( fTopElementFound == null )
        {
            fTopElementFound = localName;

            this.topLevelElement = hasRootProjectElement();

            throw new StopParsingException();
        }

    }

    protected final SAXParser createParser( SAXParserFactory parserFactory ) throws ParserConfigurationException,
        SAXException, SAXNotRecognizedException, SAXNotSupportedException
    {
        // Initialize the parser.
        final SAXParser parser = parserFactory.newSAXParser();

        // final XMLReader reader = parser.getXMLReader();
        // disable DTD validation (bug 63625)
        // try {
        // be sure validation is "off" or the feature to ignore DTD's will not
        // apply
        //            reader.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
        //            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
        // } catch (SAXNotRecognizedException e) {
        // not a big deal if the parser does not recognize the features
        // } catch (SAXNotSupportedException e) {
        // not a big deal if the parser does not support the features
        // }

        return parser;
    }

    protected SAXParserFactory getFactory()
    {
        synchronized( this )
        {
            if( fFactory != null )
            {
                return fFactory;
            }

            fFactory = SAXParserFactory.newInstance();

            fFactory.setNamespaceAware( true );
        }

        return fFactory;
    }

    protected boolean hasRootProjectElement()
    {
        return rootElement != null && rootElement.equals( fTopElementFound );
    }

    protected boolean parseContents( InputSource contents ) throws IOException, ParserConfigurationException,
        SAXException
    {
        // Parse the file into we have what we need (or an error occurs).
        try
        {
            fFactory = getFactory();

            if( fFactory == null )
            {
                return false;
            }

            final SAXParser parser = createParser( fFactory );
            parser.parse( contents, this );
        }
        catch( StopParsingException e )
        {
            // Abort the parsing normally. Fall through...
        }

        return true;
    }
}
