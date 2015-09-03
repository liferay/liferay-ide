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
import blade.migrate.api.Problem;
import blade.migrate.api.Reporter;

import java.io.File;
import java.io.OutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 */
public class IDEReporter implements Reporter
{

    @Override
    public void beginReporting( int format, OutputStream output )
    {
    }

    @Override
    public void endReporting()
    {
    }

    @Override
    public void report( Problem problem )
    {
        final File file = problem.file;
        final IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
        final IFile[] files = ws.findFilesForLocationURI( file.toURI() );

        if( files != null && files.length > 0 )
        {
            final IFile wsFile = files[0];

            try
            {
                final IMarker marker = wsFile.createMarker( MigrationConstants.MIGRATION_MARKER_TYPE );

                marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_ERROR );
                marker.setAttribute( IMarker.MESSAGE, problem.title );
                marker.setAttribute( IMarker.LINE_NUMBER, problem.lineNumber );
                marker.setAttribute( IMarker.LOCATION, problem.file.getName() );
                marker.setAttribute( IMarker.CHAR_START, problem.startOffset );
                marker.setAttribute( IMarker.CHAR_END, problem.endOffset );
                marker.setAttribute( "summary", problem.summary );
                marker.setAttribute( "ticket", problem.ticket );
            }
            catch( CoreException e )
            {
            }
        }
    }

}
