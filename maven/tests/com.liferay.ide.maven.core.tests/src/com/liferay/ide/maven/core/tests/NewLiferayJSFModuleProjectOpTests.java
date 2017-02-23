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

package com.liferay.ide.maven.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.junit.Test;

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOpMethods;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class NewLiferayJSFModuleProjectOpTests extends ProjectCoreBase
{

    @Test
    public void testNewLiferayJSFModuleProjectOpProjectName() throws Exception
    {
        NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        op.setJsfProjectProvider( "maven-jsf" );
        op.setProjectName( "Test1" );
        Status projectNameOkValidationStatus1 = op.getProjectName().validation();
        assertEquals( projectNameOkValidationStatus1.message(), "ok" );
        op.setJsfProjectProvider( "maven-jsf" );
        op.setProjectName( "#Test1" );
        Status projectNameErrorValidationStatus = op.getProjectName().validation();
        assertEquals( projectNameErrorValidationStatus.message(), "The project name is invalid." );
        op.setJsfProjectProvider( "maven-jsf" );
        op.setProjectName( "Test1_Abc" );
        Status projectNameOkValidationStatus2 = op.getProjectName().validation();
        assertEquals( projectNameOkValidationStatus2.message(), "ok" );
    }

    @Test
    public void testNewLiferayJSFModuleProjectOpProjectExisted() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setJsfProjectProvider( "maven-jsf" );
        existedProjectop.setProjectName( "Test2" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );

        waitForBuildAndValidation( existedProject );
        IMarker[] findMarkers1 = MarkerUtil.findMarkers( existedProject, IMarker.PROBLEM, null );
        assertTrue( findMarkers1.length == 0 );

        NewLiferayJSFModuleProjectOp newProjectNameop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setJsfProjectProvider( "maven-jsf" );
        newProjectNameop.setProjectName( "Test2" );
        Status projectNameExistedValidationStatus = newProjectNameop.getProjectName().validation();
        assertEquals(
            projectNameExistedValidationStatus.message(), "A project with that name(ignore case) already exists." );
    }

    @Test
    public void testNewLiferayJSFModuleProjectOpDefaultBuildType() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectName( "Test3" );
        existedProjectop.setJsfProjectProvider( "maven-jsf" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );

        waitForBuildAndValidation( existedProject );
        IMarker[] findMarkers1 = MarkerUtil.findMarkers( existedProject, IMarker.PROBLEM, null );
        assertTrue( findMarkers1.length == 0 );

        NewLiferayJSFModuleProjectOp newBuildTypeop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        DefaultValueService buildTypeDefaultService =
            newBuildTypeop.getJsfProjectProvider().service( DefaultValueService.class );
        assertEquals( buildTypeDefaultService.value(), "maven-jsf" );
    }

    @Test
    public void testNewLiferayJSFModuleStandardAloneProject() throws Exception
    {
        NewLiferayJSFModuleProjectOp mavenProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        mavenProjectop.setJsfProjectProvider( "maven-jsf" );
        mavenProjectop.setProjectName( "Test4" );
        Status mavenProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            mavenProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( mavenProjectStatus.ok() );

        IProject mavenProject = CoreUtil.getProject( "Test4" );
        assertNotNull( mavenProject );

        waitForBuildAndValidation( mavenProject );
        IMarker[] findMarkers1 = MarkerUtil.findMarkers( mavenProject, IMarker.PROBLEM, null );
        assertTrue( findMarkers1.length == 0 );

        NewLiferayJSFModuleProjectOp graldeProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        graldeProjectop.setJsfProjectProvider( "gradle-jsf" );
        graldeProjectop.setProjectName( "Test5" );
        Status gradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            graldeProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( gradleProjectStatus.ok() );

        IProject gradleProject = CoreUtil.getProject( "Test5" );
        assertNotNull( gradleProject );

        waitForBuildAndValidation( gradleProject );
        IMarker[] findMarkers2 = MarkerUtil.findMarkers( gradleProject, IMarker.PROBLEM, null );
        assertTrue( findMarkers2.length == 0 );
    }

    @Test
    public void testNewLiferayJSFModuleWorkspaceProject() throws Exception
    {
        IPath importWorkspaceProjectLocation = importWorkspaceProject( "testWorkspace" );

        ILiferayProjectImporter gradleImporter = LiferayCore.getImporter( "gradle" );
        gradleImporter.importProjects( importWorkspaceProjectLocation.toOSString(), new NullProgressMonitor() );

        IProject workspaceProject = CoreUtil.getProject( "testWorkspace" );
        assertNotNull( workspaceProject );

        NewLiferayJSFModuleProjectOp gradleProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        gradleProjectop.setJsfProjectProvider( "gradle-jsf" );
        gradleProjectop.setProjectName( "Test6" );
        Status gradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            gradleProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( gradleProjectStatus.ok() );

        IPath workspaceWarLocation = importWorkspaceProjectLocation.append( "wars" );
        IPath projectLocation = workspaceWarLocation.append( gradleProjectop.getProjectName().content() );

        IProject gradleProject = CoreUtil.getProject( projectLocation.toFile() );
        assertNotNull( gradleProject );

        waitForBuildAndValidation( gradleProject );
        IMarker[] findMarkers1 = MarkerUtil.findMarkers( gradleProject, IMarker.PROBLEM, null );
        assertTrue( findMarkers1.length == 0 );

    }

    private IPath importWorkspaceProject( String name ) throws Exception
    {
        final URL projectZipUrl =
            Platform.getBundle( "com.liferay.ide.maven.core.tests" ).getEntry( "projects/" + name + ".zip" );

        final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

        IPath wordkspaceProjectLocation = CoreUtil.getWorkspaceRoot().getLocation();
        ZipUtil.unzip( projectZipFile, wordkspaceProjectLocation.toFile() );

        final IPath projectFolder = wordkspaceProjectLocation.append( name );

        assertEquals( true, projectFolder.toFile().exists() );

        return projectFolder;
    }

    @Override
    protected void waitForBuildAndValidation( IProject project ) throws Exception
    {
        project.build( IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor() );
        waitForBuildAndValidation2();
        project.build( IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor() );
        waitForBuildAndValidation2();
    }

    private static void waitForBuildAndValidation2() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint( true );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Thread.sleep( 800 );
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
}
