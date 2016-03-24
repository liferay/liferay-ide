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

import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import org.osgi.framework.dto.BundleDTO;

import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BundleSupervisor extends AgentSupervisor<Supervisor, Agent> implements Supervisor
{

    private String lastOutput;

    @Override
    public boolean stderr( String out ) throws Exception
    {
        return true;
    }

    public void connect( String host, int port ) throws Exception
    {
        super.connect( Agent.class, this, host, port );
    }

    @Override
    public void event( Event e ) throws Exception
    {
    }

    public BundleDTO deploy(
        final String bsn, final File bundleFile, final String bundleUrl, BundleDTO[] existingBundles ) throws Exception
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

        final String sha = addFile( bundleFile );
        long bundleId = -1;

        for( BundleDTO bundle : existingBundles )
        {
            if( bundle.symbolicName.equals( bsn ) )
            {
                bundleId = bundle.id;
                retval = bundle;
                break;
            }
        }

        if( bundleId > 0 )
        {
            if( !isFragment )
            {
                agent.stop( bundleId );
            }

            if( bundleUrl.contains( "webbundle:" ) )
            {
                agent.updateFromURL( bundleId, bundleUrl );
            }
            else
            {
                agent.update( bundleId, sha );
            }

            if( !isFragment )
            {
                agent.start( bundleId );
            }
        }
        else
        {
            if( bundleUrl.contains( "webbundle:" ) )
            {
                retval = agent.installFromURL( bundleUrl, bundleUrl );
            }
            else
            {
                retval = agent.install( bundleUrl, sha );
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
                updateHostBundle( fragmentHostName, existingBundles );
            }
        }

        return retval;
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

    public void updateHostBundle( String fragmentHostName, BundleDTO[] existingBundles ) throws Exception
    {
        long fragmentHostId = -1;

        for( BundleDTO bundle : existingBundles )
        {
            if( bundle.symbolicName.equals( fragmentHostName ) )
            {
                fragmentHostId = bundle.id;
                break;
            }
        }

        if( fragmentHostId > 0 )
        {
            Agent agent = getAgent();
            agent.redirect( Agent.COMMAND_SESSION );
            agent.stdin( "update " + fragmentHostId );
            agent.redirect( Agent.NONE );
        }
    }

}
