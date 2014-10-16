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

package com.liferay.ide.portlet.core;

import com.liferay.ide.core.util.CoreUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration.PropertiesWriter;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "rawtypes" )
public class PluginPropertiesConfigurationLayout extends PropertiesConfigurationLayout
{

    public static class PluginPropertiesWriter extends PropertiesWriter
    {

        /** Constant for the escaping character. */
        private static final String ESCAPE = "\\"; //$NON-NLS-1$

        /** The list of possible key/value separators */
        private static final char[] SEPARATORS = new char[] { '=', ':' };

        /** The white space characters used as key/value separators. */
        private static final char[] WHITE_SPACE = new char[] { ' ', '\t', '\f' };

        private char delimiter;

        public PluginPropertiesWriter( Writer writer, char delimiter )
        {
            super( writer, delimiter );

            this.delimiter = delimiter;
        }

        public void writeProperty( String key, Object value, boolean forceSingleLine, boolean wrappedProperty )
            throws IOException
        {

            String v;

            if( value instanceof List )
            {
                List values = (List) value;

                if( forceSingleLine )
                {
                    v = makeSingleLineValue( values );
                }
                else
                {
                    writeProperty( key, values );
                    return;
                }
            }
            else if( wrappedProperty )
            {
                String[] values = value.toString().split( "," ); //$NON-NLS-1$

                StringBuffer buf = new StringBuffer();

                for( String val : values )
                {
                    if( key.equals( IPluginPackageModel.PROPERTY_DEPLOY_EXCLUDE ) &&
                        !val.startsWith( "**/WEB-INF/lib/" ) )
                    {
                        val = "**/WEB-INF/lib/" + val;
                    }

                    if( CoreUtil.isNullOrEmpty( buf.toString() ) )
                    {
                        buf.append( "\\\n" ); //$NON-NLS-1$
                        buf.append( "    " + escapeValue( val ) ); //$NON-NLS-1$
                    }
                    else
                    {
                        buf.append( ",\\\n    " + escapeValue( val ) ); //$NON-NLS-1$
                    }
                }
                v = buf.toString();
            }
            else
            {
                v = escapeValue( value );
            }

            write( escapeKey( key ) );
            write( "=" ); //$NON-NLS-1$
            write( v );

            writeln( null );
        }

        private String escapeKey( String key )
        {
            StringBuffer newkey = new StringBuffer();

            for( int i = 0; i < key.length(); i++ )
            {
                char c = key.charAt( i );

                if( ArrayUtils.contains( SEPARATORS, c ) || ArrayUtils.contains( WHITE_SPACE, c ) )
                {
                    // escape the separator
                    newkey.append( '\\' );
                    newkey.append( c );
                }
                else
                {
                    newkey.append( c );
                }
            }

            return newkey.toString();
        }

        private String escapeValue( Object value )
        {
            String escapedValue = StringEscapeUtils.escapeJava( String.valueOf( value ) );

            if( delimiter != 0 )
            {
                escapedValue = StringUtils.replace( escapedValue, String.valueOf( delimiter ), ESCAPE + delimiter );
            }

            return escapedValue;
        }

        private String makeSingleLineValue( List values )
        {
            if( !values.isEmpty() )
            {
                Iterator it = values.iterator();

                String lastValue = escapeValue( it.next() );

                StringBuffer buf = new StringBuffer( lastValue );

                while( it.hasNext() )
                {
                    // if the last value ended with an escape character, it has
                    // to be escaped itself; otherwise the list delimiter will
                    // be escaped
                    if( lastValue.endsWith( ESCAPE ) )
                    {
                        buf.append( ESCAPE ).append( ESCAPE );
                    }

                    buf.append( delimiter );

                    lastValue = escapeValue( it.next() );

                    buf.append( lastValue );
                }

                return buf.toString();
            }
            else
            {
                return null;
            }
        }

    }

    public static final String[] sortedKeys = new String[] 
    {
        "name",  //$NON-NLS-1$
        "module-group-id",  //$NON-NLS-1$
        "module-incremental-version", //$NON-NLS-1$
        "tags",  //$NON-NLS-1$
        "short-description",  //$NON-NLS-1$
        "long-description",  //$NON-NLS-1$
        "change-log",  //$NON-NLS-1$
        "page-url",  //$NON-NLS-1$
        "author",  //$NON-NLS-1$
        "licenses",  //$NON-NLS-1$
        "liferay-versions",  //$NON-NLS-1$
        "portal-dependency-jars", //$NON-NLS-1$
        "deploy-excludes", //$NON-NLS-1$
        "portal-dependency-tlds",  //$NON-NLS-1$
        "speed-filters-enabled"  //$NON-NLS-1$
    };

    public PluginPropertiesConfigurationLayout( PropertiesConfiguration config )
    {
        super( config );

        this.setForceSingleLine( true );
        this.setSingleLine( IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_JARS, false );
        this.setSingleLine( IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_TLDS, false );
        this.setSingleLine( IPluginPackageModel.PROPERTY_DEPLOY_EXCLUDE, false );
        this.setSingleLine( IPluginPackageModel.PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS, false );
    }

    public boolean isWrappedProperty( String key )
    {
        return key.equals( IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_JARS ) ||
            key.equals( IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_TLDS ) ||
            key.equals( IPluginPackageModel.PROPERTY_DEPLOY_EXCLUDE ) ||
            key.equals( IPluginPackageModel.PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS );
    }

    public void save( Writer out ) throws ConfigurationException
    {
        try
        {
            char delimiter =
                getConfiguration().isDelimiterParsingDisabled() ? 0 : getConfiguration().getListDelimiter();

            PluginPropertiesWriter writer = new PluginPropertiesWriter( out, delimiter );

            if( getHeaderComment() != null )
            {
                writer.writeln( getCanonicalHeaderComment( true ) );
                writer.writeln( null );
            }

            List<Object> keyList = Arrays.asList( getKeys().toArray() );

            Collections.sort( keyList, new Comparator<Object>()
            {
                public int compare( Object o1, Object o2 )
                {
                    int index1 = Integer.MAX_VALUE;
                    int index2 = Integer.MAX_VALUE;

                    for( int i = 0; i < sortedKeys.length; i++ )
                    {
                        if( sortedKeys[i].equals( o1 ) )
                        {
                            index1 = i;
                        }

                        if( sortedKeys[i].equals( o2 ) )
                        {
                            index2 = i;
                        }
                    }

                    if( index1 < index2 )
                    {
                        return -1;
                    }
                    else if( index1 > index2 )
                    {
                        return 1;
                    }

                    return 0;
                }

            } );

            for( Iterator it = keyList.iterator(); it.hasNext(); )
            {
                String key = (String) it.next();

                if( getConfiguration().containsKey( key ) )
                {

                    // Output blank lines before property
                    for( int i = 0; i < getBlancLinesBefore( key ); i++ )
                    {
                        writer.writeln( null );
                    }

                    // Output the comment
                    if( getComment( key ) != null )
                    {
                        writer.writeln( getCanonicalComment( key, true ) );
                    }

                    // Output the property and its value
                    boolean singleLine =
                        ( isForceSingleLine() || isSingleLine( key ) ) &&
                            !getConfiguration().isDelimiterParsingDisabled();

                    boolean wrappedProperty = isWrappedProperty( key );

                    writer.writeProperty( key, getConfiguration().getProperty( key ), singleLine, wrappedProperty );
                }
            }

            writer.flush();
            writer.close();
        }
        catch( IOException ioex )
        {
            throw new ConfigurationException( ioex );
        }
    }
}
