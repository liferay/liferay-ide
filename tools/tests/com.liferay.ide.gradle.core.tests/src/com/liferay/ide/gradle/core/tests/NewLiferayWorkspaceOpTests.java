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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Andy Wu
 */
public class NewLiferayWorkspaceOpTests
{

    @BeforeClass
    public static void removeAllProjects() throws Exception
    {
        IProgressMonitor monitor = new NullProgressMonitor();

        for( IProject project : CoreUtil.getAllProjects() )
        {
            project.delete( true, monitor );

            assertFalse( project.exists() );
        }
    }

    @Test
    public void testNewLiferayWorkspaceOp() throws Exception
    {
        GradleProjectTests.fullImportGradleProject( "projects/existingProject" );

        NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

        op.setWorkspaceName( "existingProject" );

        String message = op.validation().message();

        assertNotNull( message );

        assertEquals( "A project with that name(ignore case) already exists.", message );

        op.setWorkspaceName( "ExistingProject" );

        message = op.validation().message();

        assertTrue( message.equals( "A project with that name(ignore case) already exists." ) );

        String projectName = "test-liferay-workspace";

        IPath workspaceLocation = CoreUtil.getWorkspaceRoot().getLocation();

        op.setWorkspaceName( projectName );
        op.setUseDefaultLocation( false );
        op.setLocation( workspaceLocation.toPortableString() );

        op.execute( new ProgressMonitor() );

        String wsLocation = workspaceLocation.append( projectName ).toPortableString();

        File wsFile = new File( wsLocation );

        assertTrue( wsFile.exists() );

        assertTrue( LiferayWorkspaceUtil.isValidWorkspaceLocation( wsLocation ) );
    }
}
