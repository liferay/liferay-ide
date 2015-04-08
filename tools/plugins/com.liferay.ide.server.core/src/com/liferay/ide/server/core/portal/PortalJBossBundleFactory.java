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

package com.liferay.ide.server.core.portal;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class PortalJBossBundleFactory implements PortalBundleFactory
{

    @Override
    public IPath canCreateFromPath( IPath location )
    {
        IPath retval = null;

        if( detectBundleDir( location ) && detectLiferayHome( location.append( ".." ) ) )
        {
            retval = location;
        }
        else if( detectLiferayHome( location ) )
        {
            final File[] directories = location.toFile().listFiles( new FileFilter()
            {

                @Override
                public boolean accept( File file )
                {
                    return file.isDirectory();
                }
            } );

            for( File directory : directories )
            {
                final Path dirPath = new Path( directory.getAbsolutePath() );

                if( detectBundleDir( dirPath ) )
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
        return new PortalJBossBundle( location );
    }

    private boolean detectLiferayHome( IPath path )
    {
        if( !path.toFile().exists() )
        {
            return false;
        }

        if( path.append( "data" ).toFile().exists() && path.append( "osgi" ).toFile().exists() )
        {
            return true;
        }

        if( path.append( "portal-ext.properties" ).toFile().exists() ||
            path.append( "portal-setup-wizard.properties" ).toFile().exists() )
        {
            return true;
        }

        return false;
    }

    private boolean detectBundleDir( IPath path )
    {
        if( !path.toFile().exists() )
        {
            return false;
        }

        if( path.append( "bundles" ).toFile().exists() && path.append( "modules" ).toFile().exists() &&
            path.append( "standalone" ).toFile().exists() && path.append( "bin" ).toFile().exists() )
        {
            return true;
        }

        return false;
    }
}
