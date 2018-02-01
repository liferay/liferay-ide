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

import org.eclipse.core.runtime.IPath;

import org.osgi.framework.dto.BundleDTO;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 * @author Terry Jia
 */
public class BundleSupervisor
{

    private GogoBundleDeployer helper;

    public BundleSupervisor() {
        helper = new GogoBundleDeployer();
    }

    public BundleSupervisor( String host, int port ) {
        helper = new GogoBundleDeployer(host, port);
    }

    public BundleDTO deploy( final String bsn, final File bundleFile, final String bundleUrl ) throws Exception
    {
        BundleDTO retval = null;

        boolean isFragment = false;
        String fragmentHostName = null;

        if( !bundleUrl.contains( "webbundle:" ) )
        {
            fragmentHostName = ServerUtil.getFragemtHostName( bundleFile );

            isFragment = ( fragmentHostName != null );
        }

        long bundleId = helper.getBundleId( bsn );

        if( bundleId > 0 )
        {
            if( !isFragment )
            {
                helper.stop( bundleId );
            }

            if( bundleUrl.contains( "webbundle:" ) )
            {
                helper.update( bundleId, bundleUrl );
            }
            else
            {
                helper.update( bundleId, bundleFile );
            }

            if( !isFragment )
            {
                String startStatus = helper.start( bundleId );

                if( startStatus != null )
                {
                    retval = new BundleDTO();

                    retval.id = bundleId;

                    retval = new BundleDTOWithStatus( retval, startStatus );
                }
            }

            if( retval == null )
            {
                retval = new BundleDTO();

                retval.id = bundleId;
            }
        }
        else
        {
            if( bundleUrl.contains( "webbundle:" ) )
            {
                retval = helper.install( bundleUrl );
            }
            else
            {
                retval = helper.install( bundleFile );
            }

            if( !isFragment )
            {
                String startStatus = helper.start( retval.id );

                if( startStatus != null )
                {
                    retval = new BundleDTOWithStatus( retval, startStatus );
                }
            }
            else
            {
                helper.refresh( fragmentHostName );
            }
        }

        return retval;
    }

    public String uninstall( IBundleProject bundleProject, IPath outputJar ) throws Exception
    {
        if (FileUtil.notExists(outputJar))
        {
            return null;
        }

        String retVal = null;

        String fragmentHostName = ServerUtil.getFragemtHostName( outputJar.toFile() );

        boolean isFragment = ( fragmentHostName != null );

        final String symbolicName = bundleProject.getSymbolicName();

        if( symbolicName != null )
        {
            long bundleId = helper.getBundleId( symbolicName );

            if( bundleId > 0 )
            {
                retVal = helper.uninstall( bundleId );

                if( isFragment )
                {
                    	helper.refresh( fragmentHostName );
                }
            }
        }

        return retVal;
    }

}
