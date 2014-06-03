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

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IServer;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.remote.ServerManagerConnection;

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
        return getProjectFile( "files", testApplicationWarFileName );
    }

    protected File getTestApplicationPartialModificationWar()
    {
        return getProjectFile( "files", testApplicationPartialModificationWarFileName );
    }

    protected File getTestApplicationPartialDeletionWar()
    {
        return getProjectFile( "files", testApplicationPartialDeletionWarFileName );
    }

    @Before
    public void startServer() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        IServer server = getServer();

        copyFileToServer( server, "deploy", "files", remoteIDEConnectorLPKGFileName );

        copyFileToServer( server, "", "files", portalSetupWizardFileName );

        server.start( ILaunchManager.DEBUG_MODE, npm );

        assertEquals( "Expected server has started", IServer.STATE_STARTED, server.getServerState() );

        service = new ServerManagerConnection();

        service.setHost( "localhost" );
        service.setHttpPort( "8080" );
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

    @Test
    public void testDeployApplication() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        assertEquals( "Expected the server doesn't have debug port", -1, service.getDebugPort() );

        assertEquals( "Expected the server state is started", "STARTED", service.getServerState() );

        Object result = service.installApplication( getTestApplicationWar().getAbsolutePath(), "test-application", npm );

        assertEquals( "Expected the Test Application has been installed", null, result );

        result = service.isAppInstalled( "test-application" );

        assertEquals( "Expected the Test Application has been installed", true, result );

        assertNotNull( service.getLiferayPlugins() );

        result =
            service.updateApplication(
                "test-application", getTestApplicationPartialModificationWar().getAbsolutePath(), npm );

        assertEquals( "Expected uploading the Modified Test Portlet is success", null, result );

        result =
            service.updateApplication(
                "test-application", getTestApplicationPartialDeletionWar().getAbsolutePath(), npm );

        assertEquals( "Expected uploading the Deletion Test Portlet is success", null, result );

        result = service.uninstallApplication( "test-application", npm );

        assertEquals( "Expected uninstall the Test Portlet is success", null, result );
    }

}
