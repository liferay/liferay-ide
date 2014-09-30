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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesContext;
import org.eclipse.wst.html.webresources.core.providers.IWebResourcesFileSystemProvider;



/**
 * @author Gregory Amerson
 */
public class PortalResourcesProvider implements IWebResourcesFileSystemProvider
{

    @Override
    public File[] getResources( IWebResourcesContext context )
    {
        File[] retval = null;
        final IFile htmlFile = context.getHtmlFile();

        if( htmlFile != null && ProjectUtil.isPortletProject( htmlFile.getProject() ) )
        {
            final ILiferayProject project = LiferayCore.create( htmlFile.getProject() );

            if( project != null )
            {
                final IPath portalDir = project.getAppServerPortalDir();

                final IPath cssPath = portalDir.append( "html/themes/_unstyled/css" );

                if( cssPath.toFile().exists() )
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
                }
            }
        }

        return retval;
    }
}
