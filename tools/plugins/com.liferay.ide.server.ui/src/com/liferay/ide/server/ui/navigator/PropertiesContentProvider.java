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
package com.liferay.ide.server.ui.navigator;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IServer;


/**
 * @author Gregory Amerson
 */
public class PropertiesContentProvider extends AbstractNavigatorContentProvider
{
    private final Map<String, PropertiesFile[]> propertiesFilesMap = new HashMap<String, PropertiesFile[]>();

    public PropertiesContentProvider()
    {
        super();
    }

    public void dispose()
    {
        this.propertiesFilesMap.clear();
    }

    public Object[] getChildren( Object parentElement )
    {
        return null;
    }

    private File[] getExtPropertiesFiles( ILiferayRuntime liferayRuntime )
    {
        File[] retVal = new File[0];

        final IPath liferayHome = liferayRuntime.getLiferayHome();

        if( liferayHome != null )
        {
            final File liferayHomeDir = liferayHome.toFile();

            final File[] files = liferayHomeDir.listFiles( new FilenameFilter()
            {

                public boolean accept( File dir, String name )
                {
                    return dir.equals( liferayHomeDir ) && name.endsWith( "-ext.properties" );
                }
            } );

            retVal = files;
        }

        return retVal;
    }

    @Override
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public void getPipelinedChildren( Object parent, Set currentChildren )
    {
        if( shouldAddChildren( parent ) )
        {
            final IServer server = (IServer) parent;

            PropertiesFile[] propertiesFiles = this.propertiesFilesMap.get( server.getId() );

            if( ListUtil.isEmpty(propertiesFiles) )
            {
                final ILiferayRuntime runtime = ServerUtil.getLiferayRuntime( server );

                if( runtime != null )
                {
                    File[] files = getExtPropertiesFiles(runtime);

                    final List<PropertiesFile> newFiles = new ArrayList<PropertiesFile>();

                    for( File file : files )
                    {
                        newFiles.add( new PropertiesFile( file ) );
                    }

                    propertiesFiles = newFiles.toArray( new PropertiesFile[0] );
                    this.propertiesFilesMap.put( server.getId() , propertiesFiles );
                }
            }

            if( ListUtil.isNotEmpty(propertiesFiles) )
            {
                for( PropertiesFile propertiesFile : propertiesFiles )
                {
                    currentChildren.add( propertiesFile );
                }
            }
        }
    }

    @Override
    public boolean hasChildren( Object element )
    {
        boolean retVal = false;

        if( element instanceof IServer )
        {
            final IServer server = (IServer) element;

            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( server );

            if( liferayRuntime != null )
            {
                File[] files = getExtPropertiesFiles( liferayRuntime );

                return ListUtil.isNotEmpty(files);
            }
        }

        return retVal;
    }

    @Override
    public boolean hasPipelinedChildren( Object element, boolean currentHasChildren )
    {
        return hasChildren( element );
    }

    private boolean shouldAddChildren( Object parent )
    {
        if( parent instanceof IServer )
        {
            final IServer server = (IServer) parent;

            final ILiferayServer liferayServer = (ILiferayServer) server.loadAdapter( ILiferayServer.class, null );

            if( ! ( liferayServer instanceof IRemoteServer ) )
            {
                return true;
            }
        }

        return false;
    }

}
