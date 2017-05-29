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

import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.server.core.jmx.PortalBundleDeployer;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BundleSupervisor extends AgentSupervisor<Supervisor, Agent> implements Supervisor
{

    private String lastOutput;
    private final int jmxPort;
    private PortalBundleDeployer bundleDeployer;

    public BundleSupervisor(int jmxPort)
    {
        super();

        this.jmxPort = jmxPort;
    }

    @Override
    public boolean stderr( String out ) throws Exception
    {
        return true;
    }

    public void connect( String host, int port ) throws Exception
    {
        super.connect( Agent.class, this, host, port, 600 );
        bundleDeployer = new PortalBundleDeployer( jmxPort );
    }

    public void close() throws IOException
    {
        super.close();

        bundleDeployer.close();
    }

    @Override
    public void event( Event e ) throws Exception
    {
    }

    public BundleDTO deploy(
        final String bsn, final File bundleFile, final String bundleUrl) throws Exception
    {
        BundleDTO retval = null;

        boolean isFragment = false;
        String fragmentHostName = null;
        if( !bundleUrl.contains( "webbundle:" ) )
        {
            fragmentHostName = ServerUtil.getFragemtHostName( bundleFile );
            isFragment = ( fragmentHostName != null );
        }

        final Agent agent = getAgent();

        long bundleId  = getBundleId( bsn );

        if( bundleId > 0 )
        {
            if( !isFragment )
            {
                agent.stop( bundleId );
            }

            if( bundleUrl.contains( "webbundle:" ) )
            {
                bundleDeployer.updateBundleFromURL( bundleId, bundleUrl );
            }
            else
            {
                String url = bundleFile.toURI().toURL().toExternalForm();
                bundleDeployer.updateBundleFromURL( bundleId, url );
            }

            if( !isFragment )
            {
                agent.start( bundleId );
            }

            retval = new BundleDTO();

            retval.id = bundleId;
        }
        else
        {
            if( bundleUrl.contains( "webbundle:" ) )
            {
                retval = bundleDeployer.installBundleFromURL( bundleUrl );
            }
            else
            {
                String url = bundleFile.toURI().toURL().toExternalForm();
                retval = bundleDeployer.installBundleFromURL( url );
            }

            if( !isFragment )
            {
                String startStatus = agent.start( retval.id );

                if( startStatus != null )
                {
                    retval = new BundleDTOWithStatus( retval, startStatus );
                }
            }
            else
            {
                refreshHostBundle( fragmentHostName );
            }
        }

        return retval;
    }

    public long getBundleId( String bsn ) throws Exception
    {
        long id = -1;

        try
        {
            String result = getAgent().shell( "lb -s " + bsn );

            String[] lines = result.split( "\n" );

            for( String line : lines )
            {
                if( line.contains( "(" ) && line.contains( ")" ) )
                {
                    String[] bundleAttris = line.split( "\\|" );

                    String bsnTemp = bundleAttris[3].substring( 0, bundleAttris[3].indexOf( "(" ) ).trim();

                    if( bsn.equals( bsnTemp ) )
                    {
                        id = Long.parseLong( bundleAttris[0].trim() );
                        break;
                    }
                }
            }

            return id;
        }
        catch( Exception e )
        {
            return id;
        }
    }

    public String getOutInfo()
    {
        return lastOutput;
    }

    @Override
    public boolean stdout( String out ) throws Exception
    {
        if( !"".equals( out ) && out != null )
        {
            out = out.replaceAll( "^>.*$", "" );

            if( !"".equals( out ) && !out.startsWith( "true" ) )
            {
                lastOutput = out;
            }
        }

        return true;
    }

    public void refreshHostBundle( String fragmentHostName ) throws Exception
    {
        long fragmentHostId = getBundleId( fragmentHostName );

        if( fragmentHostId > 0 )
        {
            Agent agent = getAgent();
            agent.redirect( Agent.COMMAND_SESSION );
            agent.stdin( "refresh " + fragmentHostId );
            agent.redirect( Agent.NONE );
        }
    }

    public String uninstall( IBundleProject bundleProject, IPath outputJar ) throws Exception
    {
        String retVal = null;

        String fragmentHostName = ServerUtil.getFragemtHostName( outputJar.toFile() );

        boolean isFragment = ( fragmentHostName != null );

        final String symbolicName = bundleProject.getSymbolicName();

        if( symbolicName != null )
        {
            long bundleId = getBundleId( symbolicName );

            if( bundleId > 0 )
            {
                retVal = getAgent().uninstall( bundleId );

                if( isFragment )
                {
                    refreshHostBundle( fragmentHostName );
                }
            }
        }

        return retVal;
    }

}
