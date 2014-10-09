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

package com.liferay.ide.xml.search.ui.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.junit.Before;


/**
 * The minimal collection generated from: 
 * {@link XmlBaseTestsBase}, {@link ProjectCoreBase}, {@link ServerCoreBase}, and {@link BaseTests}
 * to remove dependencies on other test plugins.
 * 
 * @author Kuo Zhang
 * 
 */
@SuppressWarnings( "restriction" )
public class XmlSearchTestsBase
{

    private static IPath liferayBundlesPath;
    private static IRuntime runtime;
    private final static String liferayBundlesDir = System.getProperty( "liferay.bundles.dir" );
    private final static String skipBundleTests = System.getProperty( "skipBundleTests" );

    protected static String MESSAGE_TYPE_HIERARCHY_INCORRECT = "Type hierarchy of type \"{0}\" incorrect";

    protected boolean checkNoMarker( IFile descriptorFile, String markerType ) throws Exception
    {
        final IMarker[] markers = descriptorFile.findMarkers( markerType, false, IResource.DEPTH_ZERO );

        for( IMarker marker : markers )
        {
            if( markerType.equals( marker.getType() ) )
            {
                return false;
            }
        }

        return true;
    }

    protected boolean checkMarker( IFile descriptorFile, String markerType, String markerMessage, boolean fullMatch ) throws Exception
    {
        final IMarker[] markers = descriptorFile.findMarkers( markerType, false, IResource.DEPTH_ZERO );

        for( IMarker marker : markers )
        {
            if( markerType.equals( marker.getType() ) )
            {
                if( fullMatch )
                {
                    if( marker.getAttribute( IMarker.MESSAGE ).equals( markerMessage ) )
                    {
                        return true;
                    }
                }
                else
                {
                    if( marker.getAttribute( IMarker.MESSAGE ).toString().startsWith( markerMessage ) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected void failTest( Exception e )
    {
        System.out.println("FooBar");
        StringWriter s = new StringWriter();
        e.printStackTrace(new PrintWriter(s));
        fail(s.toString());
    }

    protected IPath getCustomLocationBase()
    {
        final IPath customLocationBase =
            org.eclipse.core.internal.utils.FileUtil.canonicalPath( new Path( System.getProperty( "java.io.tmpdir" ) ) ).append(
                "custom-project-location-tests" );

        return customLocationBase;
    }

    protected IPath getIvyCacheZip()
    {
        return getLiferayBundlesPath().append( "ivy-cache.zip" );
    }

    protected IPath getLiferayBundlesPath()
    {
        if( liferayBundlesPath == null )
        {
            liferayBundlesPath = new Path( liferayBundlesDir );
        }

        return liferayBundlesPath;
    }

    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
    }

    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2.0/";
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

    protected IProject importProject( String path, String bundleId, String projectName ) throws Exception
    {
        final IPath sdkLocation = SDKManager.getInstance().getDefaultSDK().getLocation();
        final IPath hooksFolder = sdkLocation.append( path );

        final URL hookZipUrl =
            Platform.getBundle( bundleId ).getEntry( "projects/" + projectName + ".zip" );

        final File hookZipFile = new File( FileLocator.toFileURL( hookZipUrl ).getFile() );

        ZipUtil.unzip( hookZipFile, hooksFolder.toFile() );

        final IPath projectFolder = hooksFolder.append( projectName );
        assertEquals( true, projectFolder.toFile().exists() );

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectFolder.toOSString() );
        assertNotNull( projectRecord );

        final IRuntime runtime = ServerCore.findRuntime( getRuntimeVersion() );
        assertNotNull( runtime );

        final IProject project = ProjectUtil.importProject(
            projectRecord, ServerUtil.getFacetRuntime( runtime ), sdkLocation.toOSString(),new NullProgressMonitor() );

        assertNotNull( project );

        assertEquals( "Expected new project to exist.", true, project.exists() );

        return project;
    }

    @Before
    public void setupPluginsSDK() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

        if( ! liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSdkZipFile = getLiferayPluginsSDKZip().toFile();

            assertEquals(
                "Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
                liferayPluginsSdkZipFile.exists() );

            liferayPluginsSdkDirFile.mkdirs();

            final String liferayPluginsSdkZipFolder = getLiferayPluginsSdkZipFolder();

            if( CoreUtil.isNullOrEmpty( liferayPluginsSdkZipFolder ) )
            {
                ZipUtil.unzip( liferayPluginsSdkZipFile, liferayPluginsSdkDirFile );
            }
            else
            {
                ZipUtil.unzip(
                    liferayPluginsSdkZipFile, liferayPluginsSdkZipFolder, liferayPluginsSdkDirFile,
                    new NullProgressMonitor() );
            }
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        final File ivyCacheDir = new File( liferayPluginsSdkDirFile, ".ivy" );

        if( ! ivyCacheDir.exists() )
        {
         // setup ivy cache

            final File ivyCacheZipFile = getIvyCacheZip().toFile();

            assertEquals( "Expected ivy-cache.zip to be here: " + ivyCacheZipFile.getAbsolutePath(), true, ivyCacheZipFile.exists() );

            ZipUtil.unzip( ivyCacheZipFile, liferayPluginsSdkDirFile );
        }

        assertEquals( "Expected .ivy folder to be here: " + ivyCacheDir.getAbsolutePath(), true, ivyCacheDir.exists() );

        SDK sdk = null;

        final SDK existingSdk = SDKManager.getInstance().getSDK( getLiferayPluginsSdkDir() );

        if( existingSdk == null )
        {
            sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        }
        else
        {
            sdk = existingSdk;
        }

        assertNotNull( sdk );

        sdk.setDefault( true );

        SDKManager.getInstance().setSDKs( new SDK[] { sdk } );

        final IPath customLocationBase = getCustomLocationBase();

        final File customBaseDir = customLocationBase.toFile();

        if( customBaseDir.exists() )
        {
            FileUtil.deleteDir( customBaseDir, true );

            if( customBaseDir.exists() )
            {
                for( File f : customBaseDir.listFiles() )
                {
                    System.out.println(f.getAbsolutePath());
                }
            }

            assertEquals( "Unable to delete pre-existing customBaseDir", false, customBaseDir.exists() );
        }
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

    protected boolean shouldSkipBundleTests() { return "true".equals( skipBundleTests ); }

    protected void waitForBuildAndValidation() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint( true );

            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Thread.sleep( 200 );
            Job.getJobManager().beginRule( root = ResourcesPlugin.getWorkspace().getRoot(), null );
        }
        catch( InterruptedException e )
        {
            // failTest( e );
            // ignore
        }
        catch( IllegalArgumentException e )
        {
            // failTest( e );
        }
        catch( OperationCanceledException e )
        {
            // failTest( e );
        }
        finally
        {
            if( root != null )
            {
                Job.getJobManager().endRule( root );
            }
        }
    }

    protected void waitForBuildAndValidation( IProject project ) throws Exception
    {
        project.build( IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor() );
        waitForBuildAndValidation();
        project.build( IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor() );
        waitForBuildAndValidation();
    }

}
