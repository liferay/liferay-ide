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
package com.liferay.ide.alloy.core.webresources;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesFileSystemProvider;

/**
 * @author Gregory Amerson
 */
public class PortalResourcesProvider implements IWebResourcesFileSystemProvider
{

    private static final Map<IPath, Collection<File>> fileCache = new WeakHashMap<IPath, Collection<File>>();

    @Override
    public File[] getResources( IWebResourcesContext context )
    {
        File[] retval = null;
        final IFile htmlFile = context.getHtmlFile();
        final ILiferayProject project = LiferayCore.create( htmlFile.getProject() );

        if( htmlFile != null && project != null )
        {
            final ILiferayPortal portal = project.adapt( ILiferayPortal.class );

            if( portal != null && ProjectUtil.isPortletProject( htmlFile.getProject() ) )
            {
                final IPath portalDir = portal.getAppServerPortalDir();

                if( portalDir != null )
                {
                    final IPath cssPath = portalDir.append( "html/themes/_unstyled/css" );

                    if( cssPath.toFile().exists() )
                    {
                        synchronized( fileCache )
                        {
                            final Collection<File> cachedFiles = fileCache.get( cssPath );

                            if( cachedFiles != null )
                            {
                                retval = cachedFiles.toArray( new File[0] );
                            }
                            else
                            {
                                final Collection<File> files =
                                    FileUtils.listFiles( cssPath.toFile(), new String[] { "css", "scss" }, true );

                                final Collection<File> cached = new HashSet<File>();

                                for( File file : files )
                                {
                                    if( file.getName().endsWith( "scss" ) )
                                    {
                                        final File cachedFile =
                                            new File( file.getParent(), ".sass-cache/" +
                                                file.getName().replaceAll( "scss$", "css" ) );

                                        if( cachedFile.exists() )
                                        {
                                            cached.add( file );
                                        }
                                    }
                                }

                                files.removeAll( cached );

                                if( files != null )
                                {
                                    retval = files.toArray( new File[0] );
                                }

                                fileCache.put( cssPath, files );
                            }
                        }
                    }
                }
            }
            else if( portal != null && ProjectUtil.isLayoutTplProject( htmlFile.getProject() ) )
            {
            // return the static css resource for layout template names based on the version

                final String version = portal.getVersion();

                try
                {
                    if( version != null && ( version.startsWith( "6.0" ) || version.startsWith( "6.1" ) ) )
                    {
                        retval = createLayoutHelperFiles( "resources/layouttpl-6.1.css" );
                    }
                    else if( version != null )
                    {
                        retval = createLayoutHelperFiles( "resources/layouttpl-6.2.css" );
                    }
                }
                catch( IOException e )
                {
                    AlloyCore.logError( "Unable to load layout template helper css files", e );
                }
            }
        }

        return retval;
    }

    private File[] createLayoutHelperFiles( String path ) throws IOException
    {
        final URL url = FileLocator.toFileURL( AlloyCore.getDefault().getBundle().getEntry( path ) );

        return new File[] { new File( url.getFile() ) };
    }
}
