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
package com.liferay.ide.server.tomcat.core;

import static com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil.detectCatalinaDir;
import static com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil.detectLiferayHome;

import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalBundleFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PortalTomcatBundleFactory implements PortalBundleFactory
{
    @Override
    public IPath canCreateFromPath( IPath location )
    {
        IPath retval = null;

        // detect tomcat installtion

        if( detectCatalinaDir( location ) && detectLiferayHome( location.append( ".." ) ) )
        {
            retval = location;
        }
        else if( detectLiferayHome( location ) )
        {
            final File[] directories = location.toFile().listFiles
            (
                new FileFilter()
                {
                    @Override
                    public boolean accept( File file )
                    {
                        return file.isDirectory();
                }
                }
            );

            for( File directory : directories )
            {
                final Path dirPath = new Path( directory.getAbsolutePath() );

                if( detectCatalinaDir( dirPath ) )
                {
                    retval = dirPath;
                    break;
                }
            }
        }

        return retval;
    }

    @Override
    public IPath canCreateFromPath( Map<String, String> appServerProperties )
    {
        IPath retval = null;

        final String appServerPath = (String) (appServerProperties.get( "app.server.dir"));

        final IPath location = new Path(appServerPath);

        if( detectCatalinaDir( location )  )
        {
            retval = location;
        }
        else if( detectLiferayHome( location ) )
        {
            final File[] directories = location.toFile().listFiles
            (
                new FileFilter()
                {
                    @Override
                    public boolean accept( File file )
                    {
                        return file.isDirectory();
                    }
                }
            );

            for( File directory : directories )
            {
                final Path dirPath = new Path( directory.getAbsolutePath() );

                if( detectCatalinaDir( dirPath ) )
                {
                    retval = dirPath;
                    break;
                }
            }
        }

        return retval;
    }

    @Override
    public PortalBundle create( IPath location )
    {
        return new PortalTomcatBundle( location );
    }

    @Override
    public PortalBundle create( Map<String, String> appServerProperties )
    {
        return new PortalTomcatBundle( appServerProperties );
    }

}
