package com.liferay.ide.portal.core.structures;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.XMLContentDescriber;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class StructuresContentDescriber extends XMLContentDescriber
{

    @Override
    public int describe( InputStream input, IContentDescription description ) throws IOException
    {
        if( super.describe( input, description ) == INVALID )
        {
            return INVALID;
        }

        input.reset();

        return checkCriteria( new InputSource( input ) );
    }

    private int checkCriteria( InputSource input ) throws IOException
    {
        final StructuresHandler structuresHandler = new StructuresHandler();

        try
        {
            if( ! structuresHandler.parseContents( input ) )
            {
                return INDETERMINATE;
            }
        }
        catch( SAXException e )
        {
            // could be non structures content
            return INDETERMINATE;
        }
        catch( ParserConfigurationException e )
        {
            throw new RuntimeException( "Structures content describer error" ); //$NON-NLS-1$
        }

        // check to see if we matched our critieria
        if( structuresHandler.hasRootRootElement() )
        {
            if( structuresHandler.hasStructuresElement() || structuresHandler.hasDynamicElementElement() )
            {
                return VALID;
            }
        }

        return INDETERMINATE;
    }

    @Override
    public int describe( Reader input, IContentDescription description ) throws IOException
    {
        if( super.describe( input, description ) == INVALID )
        {
            return INVALID;
        }

        input.reset();

        return checkCriteria( new InputSource( input ) );
    }
}
