/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved./
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

package com.liferay.ide.server.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.portal.LiferayServerPort;
import com.liferay.ide.server.core.portal.PortalBundleConfiguration;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

/**
 * @author Simon Jiang
 */
public class PortalConfigurationTests extends ServerCoreBase
{
    private String tomcatRuntimId;
    private String wildflyRuntimeId;

    protected IPath getLiferayTomcatUnzipRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga4-tomcat" );
    }
    
    protected IPath getLiferayTomcatRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga4-tomcat/liferay-ce-portal-7.0-ga4" );
    }

    protected IPath getLiferayTomcatRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-ce-portal-tomcat-7.0-ga4-20170613175008905.zip" );
    }

    public String getTomcatRuntimeName()
    {
        return "Liferay CE GA4 Tomcat";
    }

    protected IPath getLiferayWildflyUnzipRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga4-wildfly" );
    }
    
    protected IPath getLiferayWildflyRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga4-wildfly/liferay-ce-portal-7.0-ga4" );
    }

    protected IPath getLiferayWildflyRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-ce-portal-wildfly-7.0-ga4-20170613175008905.zip" );
    }

    protected String getLiferayWildflyZipDir()
    {
        return "liferay-ce-portal-7.0-ga4-wildfly";
    }
    
    public String getWildflyRuntimeName()
    {
        return "Liferay CE GA4 Wildfly";
    }

    public void extractLiferayRuntime( IPath zip , IPath dir ) throws Exception
    {
        final File liferayRuntimeDirFile = dir.toFile();

        if( !liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = zip.toFile();

            assertEquals(
                "Expected file to exist: " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, dir.toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );
    }

    private String setupRuntime( IPath runtimeZipPath, IPath runitmeDirPath, String runtimeName, IPath runtimUnzipDir ) throws Exception
    {
        assertEquals( "Expected liferayBundlesPath to exist: " + runtimeZipPath.toOSString(), true,
            runtimeZipPath.toFile().exists() );

        extractLiferayRuntime( runtimeZipPath, runtimUnzipDir );

        final NullProgressMonitor npm = new NullProgressMonitor();

        IRuntime findRuntime = ServerCore.findRuntime( runtimeName );

        if( findRuntime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( runitmeDirPath );

            findRuntime = runtimeWC.save( true, npm );
        }

        assertNotNull( findRuntime );
        final PortalRuntime liferayRuntime =
            (PortalRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( PortalRuntime.class, npm );

        assertNotNull( liferayRuntime );
        
        return findRuntime.getId();
    }
    
    @Before
    public void setupRuntime() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        tomcatRuntimId = setupRuntime( getLiferayTomcatRuntimeZip(), getLiferayTomcatRuntimeDir(), getTomcatRuntimeName(), getLiferayTomcatUnzipRuntimeDir() );
        wildflyRuntimeId = setupRuntime( getLiferayWildflyRuntimeZip(), getLiferayWildflyRuntimeDir(), getWildflyRuntimeName(), getLiferayWildflyUnzipRuntimeDir() );

        importExistedServers( getClass().getResourceAsStream( "files/servers.xml" ) );
    }
    
    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.portal.runtime";
    }

    protected void checkPortValue( PortalServerDelegate wc, LiferayServerPort newPort ) throws Exception
    {
        List<LiferayServerPort> liferayServerPortsBeforeChanged = wc.getLiferayServerPorts();

        int retVal = getPortValue( liferayServerPortsBeforeChanged, newPort.getId() );
        assertNotEquals( retVal, newPort.getPort() );

        PortalBundleConfiguration initBundleConfiguration = wc.initBundleConfiguration();
        initBundleConfiguration.modifyServerPort( newPort.getId(), newPort.getPort() );
        wc.applyChange( newPort, new NullProgressMonitor() );
        wc.saveConfiguration( new NullProgressMonitor() );
        initBundleConfiguration.load( new NullProgressMonitor() );

        List<LiferayServerPort> liferayServerPortsAfterChanged = wc.getLiferayServerPorts();

        retVal = getPortValue( liferayServerPortsAfterChanged, newPort.getId() );
        assertEquals( retVal, newPort.getPort() );
    }

    protected int getPortValue( List<LiferayServerPort> ports, String portId )
    {
        int retVal = -1;
        for( LiferayServerPort port : ports )
        {
            if( port.getId().toLowerCase().equals( portId.toLowerCase() ) )
            {
                retVal = port.getPort();
                break;
            }
        }

        return retVal;
    }

    @Test
    public void testGetPortalServerConfigurationForNewTomcatServer() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        IRuntime tomcatRuntime = ServerCore.findRuntime( tomcatRuntimId );
        final IServerWorkingCopy serverWC = createServerForRuntime( "testNewTomcatServerConfiguration", tomcatRuntime );
        final PortalServerDelegate portalServerDelegate =
            (PortalServerDelegate) serverWC.loadAdapter( PortalServerDelegate.class, new NullProgressMonitor() );

        LiferayServerPort agentPort = new LiferayServerPort(
            ILiferayServer.ATTR_AGENT_PORT, "Bnd Agent", 27998, "TCPIP", LiferayServerPort.defayltStoreInServer );
        LiferayServerPort httpPort =
            new LiferayServerPort( "HTTP/1.1", "HTTP/1.1", 8982, "HTTP/1.1", LiferayServerPort.defaultStoreInXML );

        checkPortValue( portalServerDelegate, agentPort );
        checkPortValue( portalServerDelegate, httpPort );
    }
    
    @Test
    public void testGetPortalServerConfigurationForExistedTomcatServer() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;
        
        IServer existedServer = ServerCore.findServer( "Liferay CE GA4 Tomcat at localhost" );
        IServerWorkingCopy existedServerWorkingCopy = existedServer.createWorkingCopy();

        final PortalServerDelegate portalServerDelegate =
            (PortalServerDelegate) existedServerWorkingCopy.loadAdapter( PortalServerDelegate.class, new NullProgressMonitor() );

        LiferayServerPort agentPort = new LiferayServerPort(
            ILiferayServer.ATTR_AGENT_PORT, "Bnd Agent", 27997, "TCPIP", LiferayServerPort.defayltStoreInServer );
        LiferayServerPort httpPort =
            new LiferayServerPort( "HTTP/1.1", "HTTP/1.1", 8987, "HTTP/1.1", LiferayServerPort.defaultStoreInXML );

        checkPortValue( portalServerDelegate, agentPort );
        checkPortValue( portalServerDelegate, httpPort );
    }
    
    @Test
    public void testGetPortalServerConfigurationForNewWildfly() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        IRuntime wildflyRuntime = ServerCore.findRuntime( wildflyRuntimeId );
        assertNotNull( wildflyRuntime );

        final IServerWorkingCopy serverWC = createServerForRuntime( "testJBossConfiguration", wildflyRuntime );
        final PortalServerDelegate portalServerDelegate =
            (PortalServerDelegate) serverWC.loadAdapter( PortalServerDelegate.class, new NullProgressMonitor());

        LiferayServerPort agentPort = new LiferayServerPort( ILiferayServer.ATTR_AGENT_PORT, "Bnd Agent", 27996, "TCPIP", LiferayServerPort.defayltStoreInServer );
        LiferayServerPort httpPort = new LiferayServerPort( "HTTP", "HTTP", 8972, "HTTP", LiferayServerPort.defaultStoreInXML );

        checkPortValue( portalServerDelegate, agentPort );
        checkPortValue( portalServerDelegate, httpPort );
    }
    
    @Test
    public void testGetPortalServerConfigurationForExistedWildfly() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        IServer existedServer = ServerCore.findServer( "Liferay CE GA4 Wildfly at localhost" );
        IServerWorkingCopy existedWorkingCopy = existedServer.createWorkingCopy();

        final PortalServerDelegate portalServerDelegate =
            (PortalServerDelegate) existedWorkingCopy.loadAdapter( PortalServerDelegate.class, new NullProgressMonitor());

        LiferayServerPort agentPort = new LiferayServerPort( ILiferayServer.ATTR_AGENT_PORT, "Bnd Agent", 27995, "TCPIP", LiferayServerPort.defayltStoreInServer );
        LiferayServerPort httpPort = new LiferayServerPort( "HTTP", "HTTP", 8978, "HTTP", LiferayServerPort.defaultStoreInXML );

        checkPortValue( portalServerDelegate, agentPort );
        checkPortValue( portalServerDelegate, httpPort );
    }
}
