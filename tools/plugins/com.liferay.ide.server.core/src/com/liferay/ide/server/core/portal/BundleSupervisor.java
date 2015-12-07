package com.liferay.ide.server.core.portal;

import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarInputStream;

import org.osgi.framework.dto.BundleDTO;

public class BundleSupervisor extends AgentSupervisor<Supervisor, Agent>implements Supervisor
{

    @Override
    public boolean stdout(String out) throws Exception {
        return true;
    }

    @Override
    public boolean stderr(String out) throws Exception {
        return true;
    }

    public void connect(String host, int port) throws Exception {
        super.connect(Agent.class, this, host, port);
    }

    @Override
    public void event(Event e) throws Exception {
    }

    public BundleDTO deploy(
        final String bsn, final File bundleFile, final String bundleUrl, BundleDTO[] existingBundles ) throws Exception
    {
        BundleDTO retval = null;

        boolean isFragment = false;

        if( !bundleUrl.contains( "webbundle:" ) )
        {
            try ( JarInputStream jarStream = new JarInputStream( new FileInputStream( bundleFile ) ) ) {
                isFragment = jarStream.getManifest().getMainAttributes().getValue( "Fragment-Host" ) != null;
            }
            catch( Exception e ) {
            }
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
                agent.start( retval.id );
            }
        }

        return retval;
    }

}
