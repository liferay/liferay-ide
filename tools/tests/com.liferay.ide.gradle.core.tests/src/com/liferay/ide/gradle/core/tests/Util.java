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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Gregory Amerson
 */
public class Util
{

    public static void deleteAllWorkspaceProjects() throws Exception
    {
        for( IProject project : CoreUtil.getAllProjects())
        {
            project.delete( true, new NullProgressMonitor() );
        }
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
            AllBladeSamplesPublishTest.failTest( e );
        }
        catch( IllegalArgumentException e )
        {
            AllBladeSamplesPublishTest.failTest( e );
        }
        catch( OperationCanceledException e )
        {
            AllBladeSamplesPublishTest.failTest( e );
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
