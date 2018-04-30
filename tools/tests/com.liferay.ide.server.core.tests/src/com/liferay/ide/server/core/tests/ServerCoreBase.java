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

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.server.util.LiferayPublishHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Module;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.junit.AfterClass;
import org.junit.Before;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Li Lu
 */
@SuppressWarnings( "restriction" )
public abstract class ServerCoreBase extends BaseTests
{

    private final static String liferayBundlesDir = System.getProperty( "liferay.bundles.dir" );
    private static IPath liferayBundlesPath;
    protected final static String liferayServerAjpPort = System.getProperty( "liferay.server.ajp.port" );
    protected final static String liferayServerShutdownPort = System.getProperty( "liferay.server.shutdown.port" );
    protected final static String liferayServerStartPort = System.getProperty( "liferay.server.start.port" );
    protected IRuntime runtime;
    protected IServer server;
    private final static String skipBundleTests = System.getProperty( "skipBundleTests" );
    private final static String skipServerTests = System.getProperty( "skipServerTests" );

    public static IServerWorkingCopy createServerForRuntime( String id, IRuntime runtime )
    {
        for( IServerType serverType : ServerCore.getServerTypes() )
        {
            if( serverType.getRuntimeType().equals( runtime.getRuntimeType() ) )
            {
                try
                {
                    return serverType.createServer( id, null, runtime, null );
                }
                catch( CoreException e )
                {
                }
            }
        }

        return null;
    }

    @AfterClass
    public static void deleteServers()
    {
        for( IServer server : ServerCore.getServers() )
        {
            server.stop( true );

            try
            {
                server.delete();
            }
            catch( CoreException e )
            {
            }
        }
    }

    protected static void extractRuntime( IPath zip , IPath dir ) throws Exception
    {
        final File liferayRuntimeDirFile = dir.toFile();

        if( !liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = zip.toFile();

            assertEquals(
                "Expected file to exist: " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, ProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );
    }

    protected void changeServerXmlPort( String currentPort, String targetPort )
    {
        final File serverXml = server.getRuntime().getLocation().append( "conf" ).append( "server.xml" ).toFile();

        assertEquals(
            "Expected the server.xml file to exist:" + serverXml.getAbsolutePath(), true, serverXml.exists() );

        try(OutputStream outputStream = Files.newOutputStream( serverXml.toPath() ))
        {
            String contents = CoreUtil.readStreamToString( Files.newInputStream( serverXml.toPath() ), true );

            contents = contents.replaceAll( currentPort, targetPort );

            CoreUtil.writeStreamFromString( contents, outputStream );
        }
        catch( IOException e )
        {
        }

    }

    public void copyFileToServer( IServer server, String targetFolderLocation, String fileDir, String fileName )
        throws IOException
    {
        InputStream is = ServerCoreBase.class.getResourceAsStream( fileDir + "/" + fileName );

        assertNotNull( is );

        final IRuntime runtime = server.getRuntime();

        IPath portalBundleFolder = runtime.getLocation().removeLastSegments( 1 );

        IPath folderPath = portalBundleFolder.append( targetFolderLocation );

        File folder = folderPath.toFile();

        if( !folder.exists() )
        {
            folder.mkdir();
        }

        assertEquals(
            "Expected the " + targetFolderLocation + " to exist:" + folderPath.toOSString(), true, folder.exists() );

        File file = folderPath.append( fileName ).toFile();

        FileUtil.writeFileFromStream( file, is );

        assertEquals( "Expected the " + file.getName() + " to exist:" + file.getAbsolutePath(), true, file.exists() );
    }

    protected IPath getLiferayBundlesPath()
    {
        if( liferayBundlesPath == null )
        {
            liferayBundlesPath = new Path( liferayBundlesDir );
        }

        return liferayBundlesPath;
    }

    protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2-ce-ga6/tomcat-7.0.62" );
    }

    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2-ce-ga6-20160112152609836.zip" );
    }

    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.62.tomcat.runtime.70";
    }

    public String getRuntimeVersion()
    {
        return "6.2.5";
    }

    public IServer getServer() throws Exception
    {
        if( server == null )
        {
            IServer[] servers = ServerCore.getServers();
            if( servers.length != 0 )
                server = servers[0];
            else
                setupServer();
        }
        if( server.getRuntime() == null )
        {
            server.delete();
            setupServer();
        }

        return server;
    }

    protected void publishToServer( IProject project )
    {
        ServerBehaviourDelegate delegate =
            (ServerBehaviourDelegate) server.loadAdapter( ServerBehaviourDelegate.class, null );

        Module[] moduleTree = { new Module( null, project.getName(), project.getName(), "jst.web", "3.0", project ) };

        LiferayPublishHelper.prePublishModule( delegate, 1, 1, moduleTree, null, null );
    }

    @Before
    public void setupRuntime() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        assertNotNull(
            "Expected System.getProperty(\"liferay.bundles.dir\") to not be null",
            System.getProperty( "liferay.bundles.dir" ) );

        assertNotNull( "Expected liferayBundlesDir to not be null", liferayBundlesDir );

        assertEquals(
            "Expected liferayBundlesPath to exist: " + getLiferayBundlesPath().toOSString(), true,
            getLiferayBundlesPath().toFile().exists() );

        extractRuntime( getLiferayRuntimeZip(), getLiferayRuntimeDir() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final String runtimeName = getRuntimeVersion();

        runtime = ServerCore.findRuntime( runtimeName );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( getLiferayRuntimeDir() );

            runtime = runtimeWC.save( true, npm );
        }

        assertNotNull( runtime );

        final ILiferayTomcatRuntime liferayRuntime =
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( ILiferayTomcatRuntime.class, npm );

        assertNotNull( liferayRuntime );

        final IPath portalBundleFolder = runtime.getLocation().removeLastSegments( 1 );

        final IPath deployPath = portalBundleFolder.append( "deploy" );

        final File deployFolder = deployPath.toFile();

        if( !deployFolder.exists() )
        {
            deployFolder.mkdir();
        }

        assertEquals( "Expected the deploy folder to exist:" + deployPath.toOSString(), true, deployFolder.exists() );
    }

    protected void setupServer() throws Exception
    {
        final NullProgressMonitor npm = new NullProgressMonitor();

        final IServerWorkingCopy serverWC = createServerForRuntime( "6.2.0", runtime );

        ServerDelegate delegate = (ServerDelegate) serverWC.loadAdapter( ServerDelegate.class, null );
        delegate.importRuntimeConfiguration( serverWC.getRuntime(), null );
        server = serverWC.save( true, npm );

        assertNotNull( server );
    }

    protected boolean shouldSkipBundleTests()
    {
        return "true".equals( skipBundleTests );
    }

    protected boolean shouldSkipServerTests()
    {
        return "true".equals( skipServerTests );
    }

    public void startServer() throws Exception
    {
        server = getServer();
        if( server.getServerState() == IServer.STATE_STARTED )
        {
            return;
        }

        copyFileToServer( server, "", "files", "portal-setup-wizard.properties" );

        server.start( "run", new NullProgressMonitor() );
        long timeoutExpiredMs = System.currentTimeMillis() + 120000;
        while( true )
        {
            Thread.sleep( 500 );
            if( server.getServerState() == IServer.STATE_STARTED )
            {
                break;
            }
            if( System.currentTimeMillis() >= timeoutExpiredMs )
            {
                break;
            }
        }
        Thread.sleep( 10000 );
    }
}
