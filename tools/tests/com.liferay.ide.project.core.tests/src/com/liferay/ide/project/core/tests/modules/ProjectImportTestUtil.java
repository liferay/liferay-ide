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

package com.liferay.ide.project.core.tests.modules;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

/**
 * @author Andy Wu
 */
public class ProjectImportTestUtil
{

    public static void importExistingProject( File dir, IProgressMonitor monitor ) throws CoreException
    {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();

        final IProjectDescription description =
            workspace.loadProjectDescription( new Path( dir.getAbsolutePath() ).append( ".project" ) );

        final String name = description.getName();

        final IProject project = workspace.getRoot().getProject( name );

        if( project.exists() )
        {
            return;
        }
        else
        {
            project.create( description, monitor );
            project.open( IResource.BACKGROUND_REFRESH, monitor );

            project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
    }
}
