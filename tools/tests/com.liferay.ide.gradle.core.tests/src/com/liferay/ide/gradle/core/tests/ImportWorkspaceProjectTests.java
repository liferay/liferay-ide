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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.LiferayGradleProject;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ImportWorkspaceProjectTests
{
    @BeforeClass
    public static void clearWorkspace() throws Exception
    {
        Util.deleteAllWorkspaceProjects();
    }

    @Test
    public void testImportLiferayWorkspace() throws Exception
    {
        LiferayGradleProject rootProject = GradleProjectTests.fullImportGradleProject( "projects/testWorkspace" );

        assertNotNull( rootProject );

        waitForBuildAndValidation();

        assertLiferayProject( "jstl.test" );
        assertLiferayProject( "roster-api" );
        assertLiferayProject( "roster-service" );
        assertLiferayProject( "roster-web" );
        assertLiferayProject( "sample-portlet" );
        assertLiferayProject( "sample-model-listener" );
        assertLiferayProject( "sample-theme" );
    }

    private void assertLiferayProject( String projectName )
    {
        IProject project = CoreUtil.getProject( projectName );

        assertTrue( "Project " + projectName + " doesn't exist.", project.exists() );
        assertTrue( "Project " + projectName + " doesn't haven liferay nature", LiferayNature.hasNature( project ) );
    }

    public void waitForBuildAndValidation() throws Exception
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

    public void failTest( Exception e )
    {
        StringWriter s = new StringWriter();
        e.printStackTrace( new PrintWriter( s ) );
        fail( s.toString() );
    }
}
