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
import com.liferay.ide.server.core.LiferayServerCore;
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

    public BundleSupervisor( int jmxPort )
    {
        super();

        this.jmxPort = jmxPort;
    }

    public void close() throws IOException
    {
        super.close();

        bundleDeployer.close();
    }

    public void connect( String host, int port, int telnetPort ) throws Exception
    {
        GogoTelnetClient gogoShell = new GogoTelnetClient( host, telnetPort );

        makeSureBundlesStarted( gogoShell, "biz.aQute.remote.agent" );
        makeSureBundlesStarted( gogoShell, "org.apache.aries.jmx.api" );
        makeSureBundlesStarted( gogoShell, "org.apache.aries.jmx.core" );
        makeSureBundlesStarted( gogoShell, "org.apache.aries.util" );

        gogoShell.close();

        super.connect( Agent.class, this, host, port, 600 );
        bundleDeployer = new PortalBundleDeployer( host, jmxPort );
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

        final Agent agent = getAgent();

        long bundleId = getBundleId( bsn );

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
                bundleDeployer.updateBundleFromURL( bundleId, bundleFile.toURI().toURL().toExternalForm() );
            }

            if( !isFragment )
            {
                String startStatus = agent.start( bundleId );

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

    @Override
    public void event( Event e ) throws Exception
    {
    }

    private BundleDTOWithStatus getBundleDTOwithStatus( String result, String bsn )
    {
        BundleDTOWithStatus bundleDTOWithStatus = null;

        long id;

        String status = null;

        String[] lines = result.split( "\n" );

        for( String line : lines )
        {
            if( line.contains( "(" ) && line.contains( ")" ) )
            {
                String[] bundleAttris = line.split( "\\|" );

                String bsnTemp = bundleAttris[3].substring( 0, bundleAttris[3].indexOf( "(" ) ).trim();

                if( bsn.equals( bsnTemp ) )
                {
                    status = bundleAttris[1].trim();
                    id = Long.parseLong( bundleAttris[0].trim() );

                    bundleDTOWithStatus = new BundleDTOWithStatus( id, status, bsn );

                    return bundleDTOWithStatus;
                }
            }
        }

        return bundleDTOWithStatus;
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
            LiferayServerCore.logError( "Get result error when executing shell(lb -s " + bsn + ")", e );
            return id;
        }
    }

    public String getOutInfo()
    {
        return lastOutput;
    }

    private void makeSureBundlesStarted( GogoTelnetClient gogoShell, String bsn ) throws Exception
    {
        String result = gogoShell.send( "lb -s " + bsn );

        BundleDTOWithStatus bundleDTO = getBundleDTOwithStatus( result, bsn );

        if( bundleDTO == null )
        {
            throw new Exception( "can't find " + bsn + " in running liferay instance" );
        }

        if( bundleDTO._status.equals( "Active" ) )
        {
            return;
        }

        if( bundleDTO._status.equals( "Resolved" ) )
        {
            gogoShell.send( "start " + bundleDTO.id );

            result = gogoShell.send( "lb -s " + bsn );

            bundleDTO = getBundleDTOwithStatus( result, bsn );

            if( bundleDTO == null )
            {
                throw new Exception( "can't find " + bsn + " in running liferay instance" );
            }

            if( bundleDTO._status.equals( "Active" ) )
            {
                return;
            }
            else
            {
                throw new Exception( "can't start " + bsn + " in running liferay instance" );
            }
        }
        else
        {
            throw new Exception( "unknow status of " + bsn + " in running liferay instance" );
        }
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

    @Override
    public boolean stderr( String out ) throws Exception
    {
        return true;
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
