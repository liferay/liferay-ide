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
import com.liferay.ide.server.util.ServerUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public abstract class ServerCoreBase extends BaseTests
{

    private final static String liferayBundlesDir = System.getProperty( "liferay.bundles.dir" );
    private static IPath liferayBundlesPath;
    protected final static String liferayServerAjpPort = System.getProperty( "liferay.server.ajp.port" );
    protected final static String liferayServerShutdownPort = System.getProperty( "liferay.server.shutdown.port" );
    protected final static String liferayServerStartPort = System.getProperty( "liferay.server.start.port" );
    private static IRuntime runtime;
    private static IServer server;
    private final static String skipBundleTests = System.getProperty( "skipBundleTests" );

    protected void changeServerXmlPort( String currentPort, String targetPort )
    {
        final File serverXml = server.getRuntime().getLocation().append( "conf" ).append( "server.xml" ).toFile();

        assertEquals(
            "Expected the server.xml file to exist:" + serverXml.getAbsolutePath(), true, serverXml.exists() );

        try
        {
            String contents = CoreUtil.readStreamToString( new FileInputStream( serverXml ), true );

            contents = contents.replaceAll( currentPort, targetPort );

            FileWriter fw = new FileWriter(serverXml.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(contents);

            bw.close();
        }
        catch( IOException e )
        {
        }

    }

    public void copyFileToServer( IServer server, String targetFolderLocation, String fileDir, String fileName )
        throws IOException
    {
        InputStream is = getClass().getResourceAsStream( fileDir + "/" + fileName );

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
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
    }

    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    protected String getRuntimeId()
    {
        return "com.liferay.ide.server.62.tomcat.runtime.70";
    }

    public String getRuntimeVersion()
    {
        return "6.2.0";
    }

    public IServer getServer() throws Exception
    {
        if( server == null )
        {
            setupServer();
        }
        return server;
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

        final File liferayRuntimeDirFile = getLiferayRuntimeDir().toFile();

        if( !liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = getLiferayRuntimeZip().toFile();

            assertEquals(
                "Expected file to exist: " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, ProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );

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

        if( runtime == null )
        {
            setupRuntime();
        }

        final IServerWorkingCopy serverWC = ServerUtil.createServerForRuntime( runtime );

        server = serverWC.save( true, npm );

        assertNotNull( server );
    }

    protected boolean shouldSkipBundleTests() { return "true".equals( skipBundleTests ); }

}
