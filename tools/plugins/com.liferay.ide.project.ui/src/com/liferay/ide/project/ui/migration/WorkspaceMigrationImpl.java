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

package com.liferay.ide.project.ui.migration;

import blade.migrate.api.MigrationConstants;
import blade.migrate.api.MigrationListener;
import blade.migrate.api.Problem;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 */
public class WorkspaceMigrationImpl implements MigrationListener
{

    @Override
    public void problemsFound( List<Problem> problems )
    {
        for( Problem problem : problems )
        {
            final File file = problem.file;
            final IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
            final IFile[] files = ws.findFilesForLocationURI( file.toURI() );

            if( files != null && files.length > 0 )
            {
                final IFile wsFile = files[0];

                try
                {
                    final TaskProblem taskProblem = new TaskProblem( problem, false );
                    final IMarker marker = wsFile.createMarker( MigrationConstants.MIGRATION_MARKER_TYPE );

                    MigrationUtil.taskProblemToMarker( taskProblem, marker );
                }
                catch( CoreException e )
                {
                }
            }
        }
    }

}
