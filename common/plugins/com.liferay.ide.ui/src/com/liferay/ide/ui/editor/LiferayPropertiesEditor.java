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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.editor.LiferayPropertiesContentAssistProcessor.PropKey;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayPropertiesEditor extends PropertiesFileEditor
{
    public static final String ID = "com.liferay.ide.ui.editor.LiferayPortalPropertiesEditor";

    @Override
    protected void doSetInput( IEditorInput input ) throws CoreException
    {
        super.doSetInput( input );

        PropKey[] propKeys = null;

        // first fine runtime location to get portal.properties definition
        final IPath appServerPortalDir = getAppServerPortalDir( input );

        if( appServerPortalDir != null && appServerPortalDir.toFile().exists() )
        {
            try
            {
                final JarFile jar = new JarFile( appServerPortalDir.append( "WEB-INF/lib/portal-impl.jar" ).toFile() );
                final ZipEntry lang = jar.getEntry( "portal.properties" );

                propKeys = parseKeys( jar.getInputStream( lang ) );

                jar.close();
            }
            catch( Exception e )
            {
                LiferayUIPlugin.logError( "Unable to get portal language properties file", e );
            }
        }

        final Object adapter = input.getAdapter( IFile.class );

        if( adapter instanceof IFile && isHookProject( ( (IFile) adapter ).getProject() ) )
        {
            final ILiferayProject liferayProject = LiferayCore.create( ( (IFile) adapter ).getProject() );
            final Set<String> hookProps = new HashSet<String>();
            Collections.addAll( hookProps, liferayProject.getHookSupportedProperties() );

            final List<PropKey> filtered = new ArrayList<PropKey>();

            for( PropKey pk : propKeys )
            {
                if( hookProps.contains( pk.getKey() ) )
                {
                    filtered.add( pk );
                }
            }

            propKeys = filtered.toArray( new PropKey[0] );
        }

        ( (LiferayPropertiesSourceViewerConfiguration) getSourceViewerConfiguration() ).setPropKeys( propKeys );
    }

    private IPath getAppServerPortalDir( final IEditorInput input )
    {
        IPath retval = null;

        final IFile ifile = (IFile) input.getAdapter( IFile.class );

        if( ifile != null )
        {
            final ILiferayProject project = LiferayCore.create( ifile.getProject() );

            if( project != null )
            {
                retval = project.getAppServerPortalDir();
            }
        }
        else
        {
            File file = (File) input.getAdapter( File.class );

            if( file != null && file.exists() )
            {
                try
                {
                    final IPath propsParentPath = new Path( file.getParentFile().getCanonicalPath() );

                    for( IRuntime runtime : ServerCore.getRuntimes() )
                    {
                        if( propsParentPath.equals( runtime.getLocation() ) ||
                            propsParentPath.isPrefixOf( runtime.getLocation() ) )
                        {
                            final ILiferayRuntime lr = ServerUtil.getLiferayRuntime( runtime );

                            retval = lr.getAppServerPortalDir();

                            break;
                        }
                    }
                }
                catch( Exception e )
                {
                    LiferayUIPlugin.logError( "Unable to get portal language properties file", e );
                }
            }
        }

        return retval;
    }

    @Override
    protected void initializeEditor()
    {
        super.initializeEditor();

        setSourceViewerConfiguration( new LiferayPropertiesSourceViewerConfiguration( this ) );
    }

    private boolean isHookProject( IProject project )
    {
        final ILiferayProject lr = LiferayCore.create( project );

        return lr != null && CoreUtil.getDescriptorFile( project, ILiferayConstants.LIFERAY_HOOK_XML_FILE ) != null;
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
