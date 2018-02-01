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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class PortalBundleTests extends ServerCoreBase
{

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga5/tomcat-8.0.32" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.portal.runtime";
    }

    @Override
    public void setupRuntime() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        extractRuntime( getLiferayRuntimeZip(), getLiferayRuntimeDir() );
    }

    @Test
    public void testPortalBundleChangeLocation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        IProgressMonitor npm = new NullProgressMonitor();

        final String name = "changeLocationTest";
        final IRuntimeWorkingCopy runtimeWC =
            ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

        assertNotNull( runtimeWC );

        runtimeWC.setName( name );
        runtimeWC.setLocation( getLiferayRuntimeDir().append( "../.." ) );

        PortalRuntime portalRuntime = (PortalRuntime) runtimeWC.loadAdapter( PortalRuntime.class, npm );

        assertNotNull( portalRuntime );

        assertNull( portalRuntime.getPortalBundle() );

        runtimeWC.setLocation( getLiferayRuntimeDir().append( ".." ) );

        assertNotNull( portalRuntime.getPortalBundle() );

        runtimeWC.setLocation( null );

        assertNull( portalRuntime.getPortalBundle() );

        runtimeWC.setLocation( getLiferayRuntimeDir() );

        assertNotNull( portalRuntime.getPortalBundle() );

        // set 7.x server with 6.2 location
//        IPath zip = getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2-ce-ga4-20150416163831865.zip" );
//        IPath dir = ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2-ce-ga4/tomcat-7.0.42" );
//
//        extractRuntime( zip, dir );
//
//        runtimeWC.setLocation( dir );
//
//        assertNull( portalRuntime.getPortalBundle() );
    }

    @Test
    public void testPortalBundleTypeCorrection() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        IProgressMonitor npm = new NullProgressMonitor();

        final String name = "correctionTest";
        final IRuntimeWorkingCopy runtimeWC =
            ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

        assertNotNull( runtimeWC );

        runtimeWC.setName( name );
        runtimeWC.setLocation( getLiferayRuntimeDir().append( ".." ) );

        PortalRuntime portalRuntime = (PortalRuntime) runtimeWC.loadAdapter( PortalRuntime.class, npm );

        assertNotNull( portalRuntime );

        assertNotNull( portalRuntime.getPortalBundle() );

        assertEquals( "tomcat", portalRuntime.getPortalBundle().getType() );

        assertEquals( getLiferayRuntimeDir().append( ".." ),  portalRuntime.getPortalBundle().getLiferayHome() );
    }

    @Test
    public void testPortalBundleTypeDetection() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        IProgressMonitor npm = new NullProgressMonitor();

        final String name = "detectionTest";
        final IRuntimeWorkingCopy runtimeWC =
            ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

        assertNotNull( runtimeWC );

        runtimeWC.setName( name );
        runtimeWC.setLocation( getLiferayRuntimeDir() );

        PortalRuntime portalRuntime = (PortalRuntime) runtimeWC.loadAdapter( PortalRuntime.class, npm );

        assertNotNull( portalRuntime );

        assertNotNull( portalRuntime.getPortalBundle() );

        assertEquals( "tomcat", portalRuntime.getPortalBundle().getType() );

        assertEquals( getLiferayRuntimeDir().append( ".." ),  portalRuntime.getPortalBundle().getLiferayHome() );
    }

    @Test
    public void testPortalBundleTypeNotFound() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        IProgressMonitor npm = new NullProgressMonitor();

        final String name = "notfoundTest";
        final IRuntimeWorkingCopy runtimeWC =
            ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

        assertNotNull( runtimeWC );

        runtimeWC.setName( name );
        runtimeWC.setLocation( getLiferayRuntimeDir().append( "../.." ) );

        PortalRuntime portalRuntime = (PortalRuntime) runtimeWC.loadAdapter( PortalRuntime.class, npm );

        assertNotNull( portalRuntime );

        assertNull( portalRuntime.getPortalBundle() );
    }

    @Test
    public void testPortalBundleReleaseName() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        PortalBundle bundle = LiferayServerCore.newPortalBundle( getLiferayRuntimeDir() );

        assertNotNull( bundle );

        assertEquals( "Liferay Community Edition Portal 7.0.4 GA5", bundle.getServerReleaseInfo() );
    }

    @Test
    @Ignore
    public void testPortalServerDelegateName() throws Exception {
        if( shouldSkipBundleTests() ) return;

        IServerType portalServerType = ServerCore.findServerType( PortalServer.ID );

        assertNotNull( portalServerType );

        IProgressMonitor monitor = new NullProgressMonitor();

        IServerWorkingCopy newServer = portalServerType.createServer( null, null, monitor );

        assertNotNull( newServer );

        assertEquals( "Liferay 7.x at localhost", newServer.getName() );

        newServer.setHost( "127.0.0.1" );

        ServerDelegate delegate = (ServerDelegate) newServer.loadAdapter( ServerDelegate.class, monitor );

        delegate.newServerDetailsChanged( monitor );

        assertEquals( "Liferay 7.x at 127.0.0.1", newServer.getName() );
    }
}

