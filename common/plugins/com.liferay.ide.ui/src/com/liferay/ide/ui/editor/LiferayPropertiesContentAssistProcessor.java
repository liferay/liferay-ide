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
package com.liferay.ide.ui.editor;

import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.LiferayUIPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;


/**
 * @author Gregory Amerson
 */
public class LiferayPropertiesContentAssistProcessor implements IContentAssistProcessor
{
    class PropKey
    {
        private final String key;
        private final String comment;

        PropKey( String key, String comment )
        {
            this.key = key;
            this.comment = comment;
        }

        String getKey()
        {
            return this.key;
        }

        String getComment()
        {
            return this.comment;
        }
    }

    private final char[] AUTO_CHARS = new char[] { '.' };
    private final File propertiesFile;
    private PropKey[] propkeys;


    public LiferayPropertiesContentAssistProcessor( File propertiesFile , String contentType )
    {
        this.propertiesFile = propertiesFile;
    }

    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {
        if( this.propkeys == null )
        {
            try
            {
                final IPath propsParentPath = new Path( this.propertiesFile.getParentFile().getCanonicalPath() );

                for( IRuntime runtime : ServerCore.getRuntimes() )
                {
                    if( propsParentPath.equals( runtime.getLocation() ) ||
                        propsParentPath.isPrefixOf( runtime.getLocation() ) )
                    {
                        final ILiferayRuntime lr = ServerUtil.getLiferayRuntime( runtime );

                        final JarFile jar =
                            new JarFile( lr.getAppServerPortalDir().append( "WEB-INF/lib/portal-impl.jar" ).toFile() );
                        final ZipEntry lang = jar.getEntry( "portal.properties" );

                        propkeys = parseKeys( jar.getInputStream( lang ) );

                        jar.close();

                        break;
                    }
                }
            }
            catch( Exception e )
            {
                LiferayUIPlugin.logError( "Unable to get portal language properties file", e );
            }
        }

        final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

        if( this.propkeys != null )
        {
            for( PropKey key : this.propkeys )
            {
                proposals.add( new PropertyCompletionProposal( key.getKey(), key.getComment() ) );
            }
        }

        return proposals.toArray( new ICompletionProposal[0] );
    }

    public IContextInformation[] computeContextInformation( ITextViewer viewer, int offset )
    {
        return null;
    }

    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return AUTO_CHARS;
    }

    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }

    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }

    public String getErrorMessage()
    {
        return "Unable to get keys from portal.properties";
    }

    private PropKey[] parseKeys( InputStream inputStream ) throws ConfigurationException, IOException
    {
        List<PropKey> parsed = new ArrayList<PropKey>();

        final PortalPropertiesConfiguration config = new PortalPropertiesConfiguration();
        config.load( inputStream );
        inputStream.close();

        final Iterator<?> keys = config.getKeys();
        final PropertiesConfigurationLayout layout = config.getLayout();

        while( keys.hasNext() )
        {
            final String key = keys.next().toString();
            final String comment = layout.getComment( key );

            parsed.add( new PropKey( key, comment == null ? null : comment.replaceAll( "\n", "\n<br/>" ) ) );
        }

        final PropKey[] parsedKeys = parsed.toArray( new PropKey[0] );

        Arrays.sort( parsedKeys, new Comparator<PropKey>()
        {
            public int compare( PropKey o1, PropKey o2 )
            {
                return o1.getKey().compareTo( o2.getKey() );
            }
        });

        return parsedKeys;
    }

}
