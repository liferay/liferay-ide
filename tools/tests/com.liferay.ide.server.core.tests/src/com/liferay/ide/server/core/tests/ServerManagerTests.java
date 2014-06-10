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

import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.remote.ServerManagerConnection;
import com.liferay.ide.server.util.SocketUtil;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class ServerManagerTests extends ServerCoreBase
{

    private final static String portalSetupWizardFileName = "portal-setup-wizard.properties";
    private final static String remoteIDEConnectorLPKGFileName = "Remote IDE Connector CE.lpkg";
    private static IServerManagerConnection service;
    private final static String testApplicationPartialModificationWarFileName = "test-portlet-partial-modification.war";
    private final static String testApplicationPartialDeletionWarFileName = "test-portlet-partial-deletion.war";
    private final static String testApplicationWarFileName = "test-portlet.war";

    protected File getTestApplicationWar()
    {
        return createTempFile( "files", testApplicationWarFileName );
    }

    protected File getTestApplicationPartialModificationWar()
    {
        return createTempFile( "files", testApplicationPartialModificationWarFileName );
    }

    protected File getTestApplicationPartialDeletionWar()
    {
        return createTempFile( "files", testApplicationPartialDeletionWarFileName );
    }

    @Before
    public void startServer() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        final IServer server = getServer();

        assertEquals(
            "Expected the port " + liferayServerPort + " is available", true,
            SocketUtil.isPortAvailable( liferayServerPort ) );

        changeServerXmlPort( "8080", liferayServerPort );

        copyFileToServer( server, "deploy", "files", remoteIDEConnectorLPKGFileName );

        copyFileToServer( server, "", "files", portalSetupWizardFileName );

        if( server.getServerState() == IServer.STATE_STOPPED )
        {
            server.start( ILaunchManager.DEBUG_MODE, npm );

            int i = 0;

            do
            {
                Thread.sleep( 30000 );

                i++;
            }
            while( ( server.getServerState() != IServer.STATE_STARTED ) &&
                ( server.getServerState() == IServer.STATE_STARTING ) && ( i < 20 ) );
        }

        assertEquals( "Expected server has started", IServer.STATE_STARTED, server.getServerState() );

        service = new ServerManagerConnection();

        service.setHost( "localhost" );
        service.setHttpPort( liferayServerPort );
        service.setManagerContextPath( "/server-manager-web" );
        service.setUsername( "test@liferay.com" );
        service.setPassword( "test" );

        // Given the server 10 seconds to deploy remote IDE Connector plugin
        try
        {
            Thread.sleep( 10000 );
        }
        catch( Exception e )
        {
        }

        assertEquals( "Expected the remote connection's status should be alive", true, service.isAlive() );
    }

    @After
    public void stopServer() throws Exception
    {
        IServer server = getServer();

        if( server.getServerState() != IServer.STATE_STOPPED )
        {
            server.stop( true );

            changeServerXmlPort( liferayServerPort, "8080" );
        }
    }

    @Test
    public void testInstallUpdateUninstallApplication() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        assertEquals( "Expected the server state is started", "STARTED", service.getServerState() );

        Object result = service.installApplication( getTestApplicationWar().getAbsolutePath(), "test-application", npm );

        File testApplicationFolder = getLiferayRuntimeDir().append( "webapps" ).append( "test-application" ).toFile();

        assertEquals( "Expected the Test Application has been installed", null, result );

        assertEquals(
            "Expected the Test Application Folder to exist:" + testApplicationFolder.toPath(), true,
            testApplicationFolder.exists() );

        result = service.isAppInstalled( "test-application" );

        assertEquals( "Expected the Test Application has been installed", true, result );

        assertNotNull( service.getLiferayPlugins() );

        result =
            service.updateApplication(
                "test-application", getTestApplicationPartialModificationWar().getAbsolutePath(), npm );

        File testJspFile =
            getLiferayRuntimeDir().append( "webapps" ).append( "test-application" ).append( "view.jsp" ).toFile();

        assertEquals( "Expected uploading the Modified Test Portlet is success", null, result );

        assertEquals( "Expected the view jsp file to exist:" + testJspFile.toPath(), true, testJspFile.exists() );

        result =
            service.updateApplication(
                "test-application", getTestApplicationPartialDeletionWar().getAbsolutePath(), npm );

        assertEquals( "Expected uploading the Deletion Test Portlet is success", null, result );

        File testIconFile =
            getLiferayRuntimeDir().append( "webapps" ).append( "test-application" ).append( "icon.png" ).toFile();

        assertEquals( "Expected the icon png has been deleted", false, testIconFile.exists() );

        result = service.uninstallApplication( "test-application", npm );

        assertEquals( "Expected uninstall the Test Portlet is success", null, result );

        File testApplicationUnistallFolder =
            getLiferayRuntimeDir().append( "webapps" ).append( "test-application" ).toFile();

        assertEquals( "Expected the Test Portlet has been uninstalled", false, testApplicationUnistallFolder.exists() );
    }

}
