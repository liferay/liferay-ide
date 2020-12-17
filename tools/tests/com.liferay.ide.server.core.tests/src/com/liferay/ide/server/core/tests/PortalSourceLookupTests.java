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

import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.BundleFactoryDelegate;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOp;
import com.liferay.ide.server.core.portal.PortalServer;

import java.io.File;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.PackageFragmentRootSourceContainer;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
@Ignore
public class PortalSourceLookupTests
{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static IProgressMonitor _NPM = new NullProgressMonitor();

    @Before
    public void setUpTest() throws Exception {
        Stream.of( CoreUtil.getAllProjects() ).forEach(this::deleteProject);

        Assert.assertEquals( 0, CoreUtil.getAllProjects().length );

        Stream.of(ServerCore.getServers()).forEach(this::deleteServer);

        Assert.assertEquals( 0, ServerCore.getServers().length );

        Stream.of( getPortalServerLaunchConfigurations() ).forEach( this::deleteLaunchConfig );

        Assert.assertEquals( 0, getPortalServerLaunchConfigurations().length );

        File tempDir = tempFolder.getRoot();

        TestUtil.copyDir( new File("test-projects/debug-parent"), tempDir );

        ImportLiferayModuleProjectOp op = ImportLiferayModuleProjectOp.TYPE.instantiate();

        op.setLocation( tempDir.getAbsolutePath() );

        op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        TestUtil.waitForBuildAndValidation();

        Assert.assertTrue( CoreUtil.getProject( "debug-lib" ).exists() );

        Assert.assertTrue( CoreUtil.getProject( "debug-test" ).exists() );
    }

    private void deleteProject( IProject project )
    {
        try
        {
            project.delete( true, new NullProgressMonitor() );
        }
        catch( CoreException e )
        {
        }
    }

    private ILaunchConfiguration[] getPortalServerLaunchConfigurations() throws CoreException
    {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        return launchManager.getLaunchConfigurations( launchManager.getLaunchConfigurationType( PortalServer.ID ) );
    }

    private boolean deleteLaunchConfig( ILaunchConfiguration config )
    {
        try
        {
            config.delete();

            return true;
        }
        catch( CoreException e )
        {
            return false;
        }
    }

    private void deleteServer( IServer server )
    {
        try
        {
            server.delete();
        }
        catch( CoreException e )
        {
        }
    }

    @Test
    public void testPortalSourceContainersChangeWhenModulesChange() throws Exception {
        IServer portalServer = createPortalServer();

        IProject debugLibProject = CoreUtil.getProject( "debug-lib" );

        addProjectToServer( debugLibProject, portalServer );

        ILaunchConfiguration launchConfiguration = portalServer.getLaunchConfiguration( true, _NPM );

        ISourcePathComputer portalSourcePathComputer = getPortalSourcePathComputer( launchConfiguration );

        ISourceContainer[] sourceContainers =
            portalSourcePathComputer.computeSourceContainers( launchConfiguration, _NPM );

        Assert.assertNotNull( sourceContainers );

        Assert.assertEquals( 1,
            Stream.of(
                sourceContainers
            ).filter(
                sourceContainer -> sourceContainer instanceof JavaProjectSourceContainer
            ).count()
        );

        Assert.assertFalse(
            Stream.of(
                sourceContainers
            ).filter(
                sourceContainer -> sourceContainer instanceof PackageFragmentRootSourceContainer
            ).map(
                sourceContainer -> ((PackageFragmentRootSourceContainer)sourceContainer).getPath()
            ).anyMatch(
                path -> path.lastSegment().endsWith( "commons-io-2.5.jar" )
            )
        );

        IProject debugTestProject = CoreUtil.getProject( "debug-test" );

        portalServer = addProjectToServer( debugTestProject, portalServer );

        ISourceContainer[] updatedSourceContainers =
            portalSourcePathComputer.computeSourceContainers( launchConfiguration, _NPM );

        Assert.assertNotNull( updatedSourceContainers );

        Assert.assertEquals( 2,
            Stream.of(
                updatedSourceContainers
            ).filter(
                sourceContainer -> sourceContainer instanceof JavaProjectSourceContainer
            ).count()
        );

        Assert.assertTrue(
            Stream.of(
                updatedSourceContainers
            ).filter(
                sourceContainer -> sourceContainer instanceof PackageFragmentRootSourceContainer
            ).map(
                sourceContainer -> ((PackageFragmentRootSourceContainer)sourceContainer).getPath()
            ).anyMatch(
                path -> path.lastSegment().endsWith( "commons-io-2.5.jar" )
            )
        );
    }

    private IServer addProjectToServer( IProject project, IServer server ) throws CoreException
    {
        IServerWorkingCopy serverWorkingCopy = server.createWorkingCopy();

        IModule module = new BundleFactoryDelegate().createSimpleModule( project );

        serverWorkingCopy.modifyModules( new IModule[] { module }, null, _NPM );

        return serverWorkingCopy.save( true, _NPM );
    }

    private IServer createPortalServer() throws CoreException
    {
        return Stream.of(
            ServerCore.getServerTypes()
        ).filter(
            serverType -> serverType.getId().equals( PortalServer.ID )
        ).map(
            this::createServer
        ).findFirst(
        ).get(
        ).save( true, _NPM );
    }

    private IServerWorkingCopy createServer(IServerType serverType) {
        try
        {
            return serverType.createServer( "tempServer", null, new NullProgressMonitor() );
        }
        catch( CoreException e )
        {
            return null;
        }
    }

    @Test
    public void testPortalSourcePathComputerDelegateDefaults() throws Exception
    {
        IServer portalServer = createPortalServer();

        addProjectToServer( CoreUtil.getLiferayProject( "debug-lib" ), portalServer );

        addProjectToServer( CoreUtil.getLiferayProject( "debug-test" ), portalServer );

        ILaunchConfiguration launchConfig = portalServer.getLaunchConfiguration( true, _NPM );

        ISourcePathComputer sourcePathComputer = getPortalSourcePathComputer( launchConfig );

        Assert.assertNotNull( sourcePathComputer );

        ISourceContainer[] sourceContainers =
            sourcePathComputer.computeSourceContainers( launchConfig, new NullProgressMonitor() );

        Assert.assertNotNull( sourceContainers );

        Assert.assertEquals( 2,
            Stream.of( sourceContainers ).filter(
                sourceContainer -> sourceContainer instanceof JavaProjectSourceContainer
            ).count());

        Assert.assertTrue(
            Stream.of( sourceContainers ).filter(
                sourceContainer -> sourceContainer instanceof PackageFragmentRootSourceContainer
            ).map(
                sourceContainer -> ((PackageFragmentRootSourceContainer)sourceContainer).getPath()
            ).anyMatch(
                path -> path.lastSegment().endsWith( "commons-io-2.5.jar" )
            )
        );
    }

    private ISourcePathComputer getPortalSourcePathComputer( ILaunchConfiguration launchConfig ) throws CoreException
    {
        return DebugPlugin.getDefault().getLaunchManager().getSourcePathComputer(launchConfig);
    }
}
