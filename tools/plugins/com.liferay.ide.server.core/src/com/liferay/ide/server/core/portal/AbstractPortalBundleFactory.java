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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public abstract class AbstractPortalBundleFactory implements PortalBundleFactory
{
    private String bundleFactoryType;

    @Override
    public IPath canCreateFromPath( Map<String, Object> appServerProperties )
    {
        IPath retval = null;

        final String appServerPath = (String) ( appServerProperties.get( "app.server.dir" ) );
        final String appServerParentPath = (String) ( appServerProperties.get( "app.server.parent.dir" ) );
        final String appServerDeployPath = (String) ( appServerProperties.get( "app.server.deploy.dir" ) );
        final String appServerGlobalLibPath = (String) ( appServerProperties.get( "app.server.lib.global.dir" ) );
        final String appServerPortalPath = (String) ( appServerProperties.get( "app.server.portal.dir" ) );

        if( !ServerUtil.verifyPath( appServerPath ) ||
            !ServerUtil.verifyPath( appServerParentPath ) ||
            !ServerUtil.verifyPath( appServerDeployPath ) ||
            !ServerUtil.verifyPath( appServerPortalPath ) ||
            !ServerUtil.verifyPath( appServerGlobalLibPath ) )
        {
            return retval;
        }

        final IPath appServerLocation = new Path( appServerPath );
        final IPath liferayHomelocation = new Path( appServerParentPath );

        if( detectBundleDir( appServerLocation )  )
        {
            retval = appServerLocation;
        }
        else if( detectLiferayHome( liferayHomelocation ) )
        {
            final File[] directories = FileUtil.getDirectories( liferayHomelocation.toFile() );

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
    public IPath canCreateFromPath( IPath location )
    {
        IPath retval = null;

        if( detectBundleDir( location ) && detectLiferayHome( location.append( ".." ) ) )
        {
            retval = location;
        }
        else if( detectLiferayHome( location ) )
        {
            final File[] directories = FileUtil.getDirectories( location.toFile() );

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

    protected abstract boolean detectBundleDir( IPath path );

    @Override
    public String getType()
    {
        return this.bundleFactoryType;
    }

    public void setBundleFactoryType( String type )
    {
        this.bundleFactoryType = type;
    }
}
