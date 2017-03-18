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

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOpMethods;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import java.io.File;
import java.net.URL;

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
import org.junit.Before;
import org.junit.Test;

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
        op.setProjectProvider( "maven-jsf" );
        op.setProjectName( "Test1" );
        Status projectNameOkValidationStatus1 = op.getProjectName().validation();
        assertEquals( projectNameOkValidationStatus1.message(), "ok" );
        op.setProjectProvider( "maven-jsf" );
        op.setProjectName( "#Test1" );
        Status projectNameErrorValidationStatus = op.getProjectName().validation();
        assertEquals( projectNameErrorValidationStatus.message(), "The project name is invalid." );
        op.setProjectProvider( "maven-jsf" );
        op.setProjectName( "Test1_Abc" );
        Status projectNameOkValidationStatus2 = op.getProjectName().validation();
        assertEquals( projectNameOkValidationStatus2.message(), "ok" );
    }

    @Test
    public void testNewLiferayJSFModuleProjectOpProjectExisted() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectProvider( "gradle-jsf" );
        existedProjectop.setProjectName( "Test2" );
        existedProjectop.setTemplateName( "richfaces" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );

        NewLiferayJSFModuleProjectOp newProjectNameop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        newProjectNameop.setProjectProvider( "gradle-jsf" );
        newProjectNameop.setProjectName( "Test2" );
        Status projectNameExistedValidationStatus = newProjectNameop.getProjectName().validation();
        assertEquals( false, projectNameExistedValidationStatus.ok() );
    }

    @Test
    public void testNewLiferayJSFModuleProjectOpDefaultBuildType() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectName( "Test3" );
        existedProjectop.setProjectProvider( "gradle-jsf" );
        existedProjectop.setTemplateName( "primefaces" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );

        NewLiferayJSFModuleProjectOp newBuildTypeop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        DefaultValueService buildTypeDefaultService =
            newBuildTypeop.getProjectProvider().service( DefaultValueService.class );
        assertEquals( buildTypeDefaultService.value(), "gradle-jsf" );
    }

    @Test
    public void testNewLiferayJSFModuleStandardAloneProject() throws Exception
    {
        NewLiferayJSFModuleProjectOp mavenProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        mavenProjectop.setProjectProvider( "gradle-jsf" );
        mavenProjectop.setProjectName( "Test4" );
        mavenProjectop.setTemplateName( "icefaces" );
        Status mavenProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            mavenProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( mavenProjectStatus.ok() );

        IProject mavenProject = CoreUtil.getProject( "Test4" );
        assertNotNull( mavenProject );

        NewLiferayJSFModuleProjectOp graldeProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        graldeProjectop.setProjectProvider( "gradle-jsf" );
        graldeProjectop.setProjectName( "Test5" );
        mavenProjectop.setTemplateName( "alloy" );
        Status gradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            graldeProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( gradleProjectStatus.ok() );

        IProject gradleProject = CoreUtil.getProject( "Test5" );
        assertNotNull( gradleProject );
    }

    @Test
    public void testMavenRichFacesNewLiferayJSFModuleProjectOp() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectProvider( "maven-jsf" );
        existedProjectop.setProjectName( "Test7" );
        existedProjectop.setTemplateName( "richfaces" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );
    }

    @Test
    public void testMavenPrimeFacesNewLiferayJSFModuleProjectOp() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectProvider( "maven-jsf" );
        existedProjectop.setProjectName( "Test8" );
        existedProjectop.setTemplateName( "primefaces" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );
    }

    @Test
    public void testMavenLiferayFacesNewLiferayJSFModuleProjectOp() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectProvider( "maven-jsf" );
        existedProjectop.setProjectName( "Test9" );
        existedProjectop.setTemplateName( "icefaces" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );
    }

    @Test
    public void testMavenICEFacesNewLiferayJSFModuleProjectOp() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectProvider( "maven-jsf" );
        existedProjectop.setProjectName( "Testq10" );
        existedProjectop.setTemplateName( "alloy" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );
    }

    @Test
    public void testMavenJSFStandardNewLiferayJSFModuleProjectOp() throws Exception
    {
        NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        existedProjectop.setProjectProvider( "maven-jsf" );
        existedProjectop.setProjectName( "Test11" );
        existedProjectop.setTemplateName( "jsf" );
        Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            existedProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( exStatus.ok() );

        IProject existedProject = CoreUtil.getProject( existedProjectop.getProjectName().content() );
        assertNotNull( existedProject );
    }

    @Test
    public void testNewLiferayJSFWarWorkspaceMavenJsfProject() throws Exception
    {
        importWorkspaceProject( "testWorkspace" );
        IProject workspaceProject = CoreUtil.getProject( "testWorkspace" );
        assertNotNull( workspaceProject );
        NewLiferayJSFModuleProjectOp gradleProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        gradleProjectop.setProjectProvider( "maven-jsf" );
        gradleProjectop.setProjectName( "Test13" );
        gradleProjectop.setTemplateName( "jsf" );
        assertNotNull( workspaceProject.getLocation() );
        gradleProjectop.setLocation( workspaceProject.getLocation().append( "wars" ).toOSString() );
        Status gradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            gradleProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( gradleProjectStatus.ok() );

        IProject gradleProject = CoreUtil.getProject( "Test13" );
        assertNotNull( gradleProject );

        NewLiferayJSFModuleProjectOp alloyGradleProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        alloyGradleProjectop.setProjectProvider( "gradle-jsf" );
        alloyGradleProjectop.setProjectName( "Test14" );
        alloyGradleProjectop.setTemplateName( "alloy" );
        alloyGradleProjectop.setLocation( workspaceProject.getLocation().append( "wars" ).toOSString() );
        Status alloyGradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            alloyGradleProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( alloyGradleProjectStatus.ok() );

        IProject alloyGradleProject = CoreUtil.getProject( "Test14" );
        assertNotNull( alloyGradleProject );

        NewLiferayJSFModuleProjectOp icefacesGradleProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        icefacesGradleProjectop.setProjectProvider( "gradle-jsf" );
        icefacesGradleProjectop.setProjectName( "Test15" );
        icefacesGradleProjectop.setTemplateName( "icefaces" );
        icefacesGradleProjectop.setLocation( workspaceProject.getLocation().append( "wars" ).toOSString() );
        Status icefacesGradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            icefacesGradleProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( icefacesGradleProjectStatus.ok() );

        IProject icefaceGradleProject = CoreUtil.getProject( "Test15" );
        assertNotNull( icefaceGradleProject );

        NewLiferayJSFModuleProjectOp primefacesGradleProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        primefacesGradleProjectop.setProjectProvider( "gradle-jsf" );
        primefacesGradleProjectop.setProjectName( "Test16" );
        primefacesGradleProjectop.setTemplateName( "primefaces" );
        primefacesGradleProjectop.setLocation( workspaceProject.getLocation().append( "wars" ).toOSString() );
        Status primefacesGradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            primefacesGradleProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( primefacesGradleProjectStatus.ok() );

        IProject primefacesGradleProject = CoreUtil.getProject( "Test16" );
        assertNotNull( primefacesGradleProject );

        NewLiferayJSFModuleProjectOp richefacesGradleProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();
        richefacesGradleProjectop.setProjectProvider( "gradle-jsf" );
        richefacesGradleProjectop.setProjectName( "Test17" );
        richefacesGradleProjectop.setTemplateName( "richfaces" );
        richefacesGradleProjectop.setLocation( workspaceProject.getLocation().append( "wars" ).toOSString() );
        Status richfacesGradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
            richefacesGradleProjectop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( richfacesGradleProjectStatus.ok() );

        IProject richefacesGradleProject = CoreUtil.getProject( "Test17" );
        assertNotNull( richefacesGradleProject );
    }

    @Before
    public void clearWorkspace() throws Exception
    {
        for( IProject project : CoreUtil.getAllProjects())
        {
            project.delete( true, new NullProgressMonitor() );
        }
    }

    private void importWorkspaceProject( String name ) throws Exception
    {
        final URL projectZipUrl =
            Platform.getBundle( "com.liferay.ide.maven.core.tests" ).getEntry( "projects/" + name + ".zip" );

        final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

        IPath wordkspaceProjectLocation = CoreUtil.getWorkspaceRoot().getLocation();
        ZipUtil.unzip( projectZipFile, wordkspaceProjectLocation.toFile() );

        final IPath projectFolder = wordkspaceProjectLocation.append( name );

        assertEquals( true, projectFolder.toFile().exists() );

        ILiferayProjectImporter gradleImporter = LiferayCore.getImporter( "gradle" );
        gradleImporter.importProjects( projectFolder.toOSString(), new NullProgressMonitor() );

        IProject workspaceProject = CoreUtil.getProject( "testWorkspace" );
        assertNotNull( workspaceProject );
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
