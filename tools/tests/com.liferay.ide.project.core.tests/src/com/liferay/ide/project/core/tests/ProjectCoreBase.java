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
package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.tests.ServerCoreBase;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.junit.Before;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ProjectCoreBase extends ServerCoreBase
{

    private static final String bundleId = "com.liferay.ide.project.core.tests";

    protected void waitForBuildAndValidation() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint(true);
            Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
            Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
            Job.getJobManager().join(ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor());
            Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
            Thread.sleep(200);
            Job.getJobManager().beginRule(root = ResourcesPlugin.getWorkspace().getRoot(), null);
        }
        catch (InterruptedException e)
        {
            failTest( e );
        }
        catch (IllegalArgumentException e)
        {
            failTest( e );
        }
        catch (OperationCanceledException e)
        {
            failTest( e );
        }
        finally
        {
            if (root != null) {
                Job.getJobManager().endRule(root);
            }
        }
    }

    protected void waitForBuildAndValidation(IProject project) throws Exception
    {
        project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
        waitForBuildAndValidation();
        project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
        waitForBuildAndValidation();
    }

    protected IProject createAntProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        final IProject project = createProject( op );

        assertEquals(
            "SDK project layout is not standard, /src folder exists.", false, project.getFolder( "src" ).exists() );

        switch( op.getPluginType().content() )
        {
            case ext:
                break;
            case hook:
            case portlet:
            case web:

                assertEquals(
                    "java source folder docroot/WEB-INF/src doesn't exist.", true,
                    project.getFolder( "docroot/WEB-INF/src" ).exists() );

                break;
            case layouttpl:
                break;
            case theme:
                break;
            default:
                break;
        }

        return project;
    }

    protected IRuntime createNewRuntime( final String name ) throws Exception
    {
        final IPath newRuntimeLocation = new Path( getLiferayRuntimeDir().toString() + "-new" );

        if( ! newRuntimeLocation.toFile().exists() )
        {
            FileUtils.copyDirectory( getLiferayRuntimeDir().toFile(), newRuntimeLocation.toFile() );
        }

        assertEquals( true, newRuntimeLocation.toFile().exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        IRuntime runtime = ServerCore.findRuntime( name );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

            runtimeWC.setName( name );
            runtimeWC.setLocation( newRuntimeLocation );

            runtime = runtimeWC.save( true, npm );
        }

        ServerCore.getRuntimes();
        assertNotNull( runtime );

        return runtime;
    }

    protected SDK createNewSDK() throws Exception
    {
        final IPath newSDKLocation = new Path( getLiferayPluginsSdkDir().toString() + "-new" );

        if( ! newSDKLocation.toFile().exists() )
        {
            FileUtils.copyDirectory( getLiferayPluginsSdkDir().toFile(), newSDKLocation.toFile() );
        }

        assertEquals( true, newSDKLocation.toFile().exists() );

        SDK newSDK = SDKUtil.createSDKFromLocation( newSDKLocation );

        if( newSDK == null )
        {
            FileUtils.copyDirectory( getLiferayPluginsSdkDir().toFile(), newSDKLocation.toFile() );
            newSDK = SDKUtil.createSDKFromLocation( newSDKLocation );
        }

        SDKManager.getInstance().addSDK( newSDK );

        return newSDK;
    }

    public IProject createProject( NewLiferayPluginProjectOp op )
    {
        return createProject( op, null );
    }

    public IProject createProject( NewLiferayPluginProjectOp op, String projectName )
    {
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );

        assertEquals(
            status.toString(), Status.createOkStatus().message().toLowerCase(), status.message().toLowerCase() );

        if( projectName == null || op.getProjectProvider().content().getShortName().equalsIgnoreCase( "ant" ) )
        {
            projectName = op.getFinalProjectName().content();
        }

        final IProject newLiferayPluginProject = project( projectName );

        assertNotNull( newLiferayPluginProject );

        assertEquals( true, newLiferayPluginProject.exists() );

        final IFacetedProject facetedProject = ProjectUtil.getFacetedProject( newLiferayPluginProject );

        assertNotNull( facetedProject );

        final IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( facetedProject );

        assertNotNull( liferayFacet );

        final PluginType pluginTypeValue = op.getPluginType().content( true );

        if( pluginTypeValue.equals( PluginType.servicebuilder ) )
        {
            assertEquals( "liferay.portlet", liferayFacet.getId() );
        }
        else
        {
            assertEquals( "liferay." + pluginTypeValue, liferayFacet.getId() );
        }

        return newLiferayPluginProject;
    }

    protected String getBundleId()
    {
        return bundleId;
    }

    @SuppressWarnings( "restriction" )
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

    protected IProject getProject( String path, String projectName ) throws Exception
    {
        IProject project = CoreUtil.getWorkspaceRoot().getProject( projectName );

        if( project != null && project.exists() )
        {
            return project;
        }

        return importProject( path, getBundleId(), projectName );
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

    protected NewLiferayPluginProjectOp newProjectOp( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName + "-" + getRuntimeVersion() );

        return op;
    }

    protected void removeAllRuntimes() throws Exception
    {
        for( IRuntime r : ServerCore.getRuntimes() )
        {
            r.delete();
        }
    }

    /**
     * @throws Exception
     */
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

    @Override
    public void setupRuntime() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.setupRuntime();
    }

    public void setupPluginsSDKAndRuntime() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        setupPluginsSDK();
        setupRuntime();
    }

}
