package com.liferay.ide.portal.core.structures;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class StructuresHandler extends DefaultHandler
{

    private class StopParsingException extends SAXException
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

    private SAXParserFactory factory;

    private SAXParserFactory getFactory()
    {
        synchronized( this )
        {
            if( this.factory != null )
            {
                return this.factory;
            }

            this.factory = SAXParserFactory.newInstance();
        }

        return this.factory;
    }

    private int level = -1;
    private String topElementFound;
    private boolean structureElementFound;
    private boolean dynamicElementFound;

    @Override
    public final void startElement( final String uri, final String localName, final String qName,
                                    final Attributes attributes ) throws SAXException
    {
        this.level++;

        if( this.topElementFound == null )
        {
            this.topElementFound = qName;

            if( !hasRootRootElement() )
            {
                throw new StopParsingException();
            }
        }

        if( this.level == 1 && STRUCTURE.equals( qName ) )
        {
            this.structureElementFound = true;
            throw new StopParsingException();
        }
        else if( this.level == 1 && DYNAMIC_ELEMENT.equals( qName ) )
        {
            this.dynamicElementFound = true;
            throw new StopParsingException();
        }

        super.startElement( uri, localName, qName, attributes );
    }

    @Override
    public void endElement( String uri, String localName, String qName ) throws SAXException
    {
        super.endElement( uri, localName, qName );
        this.level--;
    }

    private static final String ROOT = "root"; //$NON-NLS-1$
    private static final String STRUCTURE = "structure"; //$NON-NLS-1$
    private static final String DYNAMIC_ELEMENT = "dynamic-element"; //$NON-NLS-1$

    private final SAXParser createParser( SAXParserFactory parserFactory ) throws ParserConfigurationException,
        SAXException, SAXNotRecognizedException, SAXNotSupportedException
    {
        // Initialize the parser.
        final SAXParser parser = parserFactory.newSAXParser();
        final XMLReader reader = parser.getXMLReader();
        // disable DTD validation
        try
        {
            // be sure validation is "off" or the feature to ignore DTD's will not apply
            reader.setFeature( "http://xml.org/sax/features/validation", false ); //$NON-NLS-1$
            reader.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false ); //$NON-NLS-1$
        }
        catch( SAXNotRecognizedException e )
        {
            // not a big deal if the parser does not recognize the features
        }
        catch( SAXNotSupportedException e )
        {
            // not a big deal if the parser does not support the features
        }

        return parser;
    }


    public boolean parseContents( InputSource input ) throws IOException, ParserConfigurationException, SAXException
    {
        try
        {
            this.factory = getFactory();

            if( this.factory == null )
            {
                return false;
            }

            final SAXParser parser = createParser( this.factory );
            parser.parse( input, this );
        }
        catch ( StopParsingException e )
        {

        }

        return true;
    }

    public boolean hasRootRootElement()
    {
        return ROOT.equals( this.topElementFound );
    }

    public boolean hasStructuresElement()
    {
        return this.structureElementFound;
    }

    public boolean hasDynamicElementElement()
    {
        return this.dynamicElementFound;
    }

}
