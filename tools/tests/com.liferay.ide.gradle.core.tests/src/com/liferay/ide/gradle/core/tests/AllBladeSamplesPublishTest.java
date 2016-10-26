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

package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.workspace.ImportLiferayWorkspaceOp;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.Module;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class AllBladeSamplesPublishTest
{

    private static final String WORKSPACE_SERVER_NAME = "workspace-server";

    @BeforeClass
    public static void importAllBladeSamples() throws Exception
    {
        ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

        File projectDir = copyTestProjectToWorkspace( "projects/all-blade-samples" );

        op.setWorkspaceLocation( projectDir.getCanonicalPath() );

        op.setProvisionLiferayBundle( true );

        op.setServerName( WORKSPACE_SERVER_NAME );

        final NullProgressMonitor monitor = new NullProgressMonitor();

        op.execute( ProgressMonitorBridge.create( monitor ) );

        waitForBuildAndValidation();

        IProject rootProject = CoreUtil.getWorkspaceRoot().getProject( "all-blade-samples" );

        assertNotNull( rootProject );

        assertTrue( rootProject.exists() );

        assertLiferayProject( "blade.portlet.jsp" );

        assertLiferayProject( "blade.gogo" );

        IServer wsServer = ServerUtil.getServer( WORKSPACE_SERVER_NAME );

        assertNotNull( wsServer );

        long timeoutExpiredMs = System.currentTimeMillis() + 600000;

        wsServer.start( "run", monitor );

        while( true )
        {
            Thread.sleep( 500 );

            if( wsServer.getServerState() == IServer.STATE_STARTED )
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

    @AfterClass
    public static void stopServer() throws Exception
    {
        IServer wsServer = ServerUtil.getServer( WORKSPACE_SERVER_NAME );

        wsServer.stop( true );
    }

    private static File copyTestProjectToWorkspace( String path ) throws Exception
    {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = ws.getRoot();

        File src = new File( path );
        File dst = new File( root.getLocation().toFile(), src.getName() );

        TestUtil.copyDir( src, dst );

        return dst;
    }

    @Test
    public void testAllProjectsImported() throws Exception
    {
        List<IProject> bladeProjects = getAllBladeProjects();

        assertEquals( 35, bladeProjects.size() );
    }

    private List<IProject> getAllBladeProjects()
    {
        List<IProject> bladeProjects = new ArrayList<>();

        for( IProject project : CoreUtil.getAllProjects() )
        {
            if( project.getName().startsWith( "blade" ) )
            {
                bladeProjects.add( project );
            }
        }

        return bladeProjects;
    }

    @Test
    public void testPublishAllBladeSamples() throws Exception
    {
        IServer server = ServerUtil.getServer( WORKSPACE_SERVER_NAME );

        IServerWorkingCopy serverWC = server.createWorkingCopy();

        List<IProject> bladeProjects = getAllBladeProjects();

        List<IModule> modules = new ArrayList<>();

        for( IProject bladeProject : bladeProjects )
        {
            final String name = bladeProject.getName();

            IModule module = new Module( null, name, name, "liferay.bundle", "1.0", bladeProject );

            modules.add( module );
        }

        IProgressMonitor monitor = new NullProgressMonitor();

        serverWC.modifyModules( modules.toArray( new IModule[0] ), null, monitor );

        server = serverWC.save( true, monitor );

        server.publish( IServer.PUBLISH_FULL, monitor );

        waitForBuildAndValidation();

        IModule[] serverModules = server.getModules();

        assertEquals( 35, serverModules.length );

        for( IModule serverModule : serverModules )
        {
            assertEquals( IServer.PUBLISH_STATE_NONE, server.getModulePublishState( new IModule[] { serverModule } ) );
        }

        String[] retval = BladeCLI.execute( "sh lb -s blade" );

        for( IModule serverModule : serverModules )
        {
            IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, serverModule.getProject() );

            String bsn = bundleProject.getSymbolicName();

            boolean foundBundle = false;

            for( String bundleString : retval )
            {
                if( bundleString.contains( bsn ) )
                {
                    foundBundle = true;

                    if( !bundleString.contains( "blade.hook.jsp" ) )
                    {
                        assertTrue( "Error in bundle state: " + bundleString, bundleString.contains( "Active" ) );
                    }
                    else
                    {
                        assertTrue( "Error in bundle state: " + bundleString, bundleString.contains( "Resolved" ) );
                    }

                    break;
                }
            }

            assertTrue( foundBundle );
        }
    }

    private static void assertLiferayProject( String projectName )
    {
        IProject project = CoreUtil.getProject( projectName );

        assertTrue( "Project " + projectName + " doesn't exist.", project.exists() );
        assertTrue( "Project " + projectName + " doesn't haven liferay nature", LiferayNature.hasNature( project ) );
    }

    public static void waitForBuildAndValidation() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint( true );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( GradleCore.JobFamilyId, new NullProgressMonitor() );
            Thread.sleep( 200 );
            Job.getJobManager().beginRule( root = ResourcesPlugin.getWorkspace().getRoot(), null );
        }
        catch( InterruptedException e )
        {
            failTest( e );
        }
        catch( IllegalArgumentException e )
        {
            failTest( e );
        }
        catch( OperationCanceledException e )
        {
            failTest( e );
        }
        finally
        {
            if( root != null )
            {
                Job.getJobManager().endRule( root );
            }
        }
    }

    public static void failTest( Exception e )
    {
        StringWriter s = new StringWriter();
        e.printStackTrace( new PrintWriter( s ) );
        fail( s.toString() );
    }
}
