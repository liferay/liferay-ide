/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class MavenProjectCoreTests extends ProjectCoreBase
{
    @Test
    public void testCreateNewxMavenProject() throws Exception
    {
        createMavenProjectName( "test-name-1" );
        createMavenProjectName( "Test With Spaces" );
        createMavenProjectName( "test_name_1" );
    }

    protected void createMavenProjectName( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setProjectProvider( "maven" );

        createMavenProject( op );
    }

    protected IProject createMavenProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        IProject project = createProject( op );

        assertEquals( true, project.getFolder( "src" ).exists() );

        return project;
    }
}
