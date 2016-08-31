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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class MavenModuleProjectTests extends AbstractMavenProjectTestCase
{

    @Test
    public void testNewLiferayMavenModuleMVCPortletProject() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "foo" );
        op.setProjectProvider( "maven-module" );

        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );
        assertTrue( status.message(), status.ok() );

        waitForJobsToComplete();

        IProject project = CoreUtil.getProject( op.getProjectName().content() );

        verifyProject(project);

        assertTrue(project.getFile( "src/main/java/com/example/portlet/FooPortlet.java" ).exists());
    }

    @Test
    public void testNewLiferayMavenModuleMVCPortletProjectWithDashes() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "foo-bar" );
        op.setProjectProvider( "maven-module" );

        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );
        assertTrue( status.message(), status.ok() );

        waitForJobsToComplete();

        IProject project = CoreUtil.getProject( op.getProjectName().content() );

        verifyProject(project);

        assertTrue(project.getFile( "src/main/java/com/example/portlet/FooBarPortlet.java" ).exists());
    }

    @Test
    public void testNewLiferayMavenModuleMVCPortletProjectWithDots() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "foo.bar" );
        op.setProjectProvider( "maven-module" );

        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );
        assertTrue( status.message(), status.ok() );

        waitForJobsToComplete();

        IProject project = CoreUtil.getProject( op.getProjectName().content() );

        verifyProject(project);
    }

    private void verifyProject(IProject project ) throws Exception
    {
        assertNotNull( project );
        assertTrue( project.exists() );

        assertFalse(project.getFile( "build.gradle" ).exists());

        project.build( IncrementalProjectBuilder.CLEAN_BUILD, monitor );

        waitForJobsToComplete();

        project.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

        waitForJobsToComplete();

        assertNoErrors( project );
    }

    @Test
    public void testNewLiferayMavenModuleApiProject() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "foo-api" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "api" );

        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );
        assertTrue( status.message(), status.ok() );

        waitForJobsToComplete();

        IProject project = CoreUtil.getProject( op.getProjectName().content() );

        verifyProject(project);
    }
}
