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

import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.server.remote.IServerManagerConnection;
import com.liferay.ide.server.remote.ServerManagerConnection;
import com.liferay.ide.server.util.SocketUtil;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.IServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class ServerManagerTests extends ServerCoreBase
{

    private static final String BUNDLE_SHUTDOWN_PORT = "8005";
    private static final String BUNDLE_AJP_PORT = "8009";
    private static final String BUNDLE_START_PORT = "8080";
    private final static String portalSetupWizardFileName = "portal-setup-wizard.properties";
    private final static String remoteIDEConnectorLPKGFileName = "Remote IDE Connector CE.lpkg";
    private static IServerManagerConnection service;
    private final static String testApplicationPartialModificationWarFileName = "test-portlet-partial-modification.war";
    private final static String testApplicationPartialDeletionWarFileName = "test-portlet-partial-deletion.war";
    private final static String testApplicationWarFileName = "test-portlet.war";

    private ILaunchConfigurationWorkingCopy getLaunchConfig( IPath workingDir, String execFileName, String command )
        throws CoreException
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        ILaunchConfigurationType configType =
            launchManager.getLaunchConfigurationType( "org.eclipse.ui.externaltools.ProgramLaunchConfigurationType" );
        ILaunchConfigurationWorkingCopy config =
            configType.newInstance( null, launchManager.generateLaunchConfigurationName( "tomcat-server" ) );

        config.setAttribute( "org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", true );
        config.setAttribute( "org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", true );
        config.setAttribute( "org.eclipse.debug.ui.ATTR_PRIVATE", true );

        String execPath = workingDir.append( execFileName ).toOSString();

        new File( execPath ).setExecutable( true );

        config.setAttribute( "org.eclipse.ui.externaltools.ATTR_LOCATION", execPath );
        config.setAttribute( "org.eclipse.ui.externaltools.ATTR_WORKING_DIRECTORY", workingDir.toOSString() );
        config.setAttribute( "org.eclipse.ui.externaltools.ATTR_TOOL_ARGUMENTS", command );

        return config;
    }

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
        if( shouldSkipServerTests() ) return;

        final IServer server = getServer();

        assertEquals(
            "Expected the port " + liferayServerStartPort + " is available", true,
            SocketUtil.isPortAvailable( liferayServerStartPort ) );

        assertEquals(
            "Expected the port " + liferayServerAjpPort + " is available", true,
            SocketUtil.isPortAvailable( liferayServerAjpPort ) );

        changeServerXmlPort( BUNDLE_START_PORT, liferayServerStartPort );

        changeServerXmlPort( BUNDLE_AJP_PORT, liferayServerAjpPort );

        changeServerXmlPort( BUNDLE_SHUTDOWN_PORT, liferayServerShutdownPort );

        copyFileToServer( server, "deploy", "files", remoteIDEConnectorLPKGFileName );

        copyFileToServer( server, "", "files", portalSetupWizardFileName );

        final String exceFileName = Platform.getOS().contains( "win" ) ? "catalina.bat" : "catalina.sh";

        final LaunchHelper launchHelper = new LaunchHelper();

        launchHelper.setLaunchSync( false );

        final IPath serverLocation = server.getRuntime().getLocation().append( "bin" );

        launchHelper.launch(
            getLaunchConfig( serverLocation, exceFileName, "run" ), ILaunchManager.RUN_MODE, null );

        boolean stop = false;

        int i = 0;

        int statusCode = 0;

        while( !stop )
        {
            try
            {
                if( i > 1500 )
                {
                    stop = true;
                }

                URL pingUrl = new URL( "http://localhost:" + liferayServerStartPort );
                URLConnection conn = pingUrl.openConnection();
                ( (HttpURLConnection) conn ).setInstanceFollowRedirects( false );
                statusCode = ( (HttpURLConnection) conn ).getResponseCode();

                if( !stop )
                {
                    Thread.sleep( 200 );
                }

                stop = true;
            }
            catch( Exception e )
            {
                i++;

                Thread.sleep( 200 );
            }
        }

        service = new ServerManagerConnection();

        service.setHost( "localhost" );
        service.setHttpPort( Integer.parseInt( liferayServerStartPort ) );
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

        assertEquals( 200, statusCode );
    }

    @After
    public void stopServer() throws Exception
    {
        if( shouldSkipServerTests() ) return;

        IServer server = getServer();

        final String exceFileName = Platform.getOS().contains( "win" ) ? "shutdown.bat" : "shutdown.sh";

        final LaunchHelper launchHelper = new LaunchHelper();

        launchHelper.setLaunchSync( false );

        final IPath serverLocation = server.getRuntime().getLocation().append( "bin" );

        launchHelper.launch(
            getLaunchConfig( serverLocation, exceFileName, "run" ), ILaunchManager.RUN_MODE, null );

        boolean stop = false;

        int i = 0;

        while( !stop )
        {
            try
            {
                if( i > 15 )
                {
                    stop = true;
                }

                URL pingUrl = new URL( "http://localhost:" + liferayServerStartPort );
                URLConnection conn = pingUrl.openConnection();
                ( (HttpURLConnection) conn ).setInstanceFollowRedirects( false );
                ( (HttpURLConnection) conn ).getResponseCode();

                if( !stop )
                {
                    Thread.sleep( 200 );
                }

                i++;
            }
            catch( Exception e )
            {
                stop = true;
            }
        }

        changeServerXmlPort( liferayServerShutdownPort, BUNDLE_SHUTDOWN_PORT );
        changeServerXmlPort( liferayServerStartPort, BUNDLE_START_PORT );
        changeServerXmlPort( liferayServerAjpPort, BUNDLE_AJP_PORT );
    }

    @Test
    @Ignore
    public void testInstallUpdateUninstallApplication() throws Exception
    {
        if( shouldSkipServerTests() ) return;

        final NullProgressMonitor npm = new NullProgressMonitor();

        assertEquals( "Expected the remote connection's status should be alive", true, service.isAlive() );

        assertEquals( "Expected the server state is started", "STARTED", service.getServerState() );

        Object result = service.installApplication( getTestApplicationWar().getAbsolutePath(), "test-application", npm );

        File testApplicationFolder = getLiferayRuntimeDir().append( "webapps" ).append( "test-application" ).toFile();

        assertEquals( "Expected the Test Application has been installed", null, result );

        assertEquals(
            "Expected the Test Application Folder to exist:" + testApplicationFolder.getAbsolutePath(), true,
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

        assertEquals( "Expected the view jsp file to exist:" + testJspFile.getAbsolutePath(), true, testJspFile.exists() );

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
