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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author Gregory Amerson
 */
public class MigrationUtil
{

    private static IFile getIFileFromTaskProblem( TaskProblem taskProblem )
    {
        // TODO could return more than one
        return ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI( taskProblem.file.toURI() )[0];
    }

    public static List<TaskProblem> getTaskProblemsFromResource( IResource resource )
    {
        final List<TaskProblem> problems = new ArrayList<>();

        try
        {
            final IMarker[] markers =
                resource.findMarkers( MigrationConstants.MIGRATION_MARKER_TYPE, true, IResource.DEPTH_ZERO );

            for( IMarker marker : markers )
            {
                TaskProblem taskProblem = markerToTaskProblem( marker );

                if( taskProblem != null )
                {
                    problems.add( taskProblem );
                }
            }
        }
        catch( CoreException e )
        {
        }

        return problems;
    }

    public static TaskProblem markerToTaskProblem( IMarker marker )
    {
        final String title = marker.getAttribute( IMarker.MESSAGE, "" );
        final String url = marker.getAttribute( "migrationProblem.url", "" );
        final String summary = marker.getAttribute( "migrationProblem.summary", "" );
        final String type = marker.getAttribute( "migrationProblem.type", "" );
        final String ticket = marker.getAttribute( "migrationProblem.ticket", "" );
        final int lineNumber = marker.getAttribute( IMarker.LINE_NUMBER, 0 );
        final int startOffset = marker.getAttribute( IMarker.CHAR_START, 0 );
        final int endOffset = marker.getAttribute( IMarker.CHAR_END, 0 );
        final boolean resolved = marker.getAttribute( "migrationProblem.resolved", false );
        final long timestamp = Long.parseLong( marker.getAttribute( "migrationProblem.timestamp", "0" ) );

        final File file = new File( marker.getResource().getLocationURI() );

        return new TaskProblem(
            title, url, summary, type, ticket, file, lineNumber, startOffset, endOffset, resolved, timestamp );
    }

    public static void openEditor( TaskProblem taskProblem )
    {
        try
        {
            final IEditorPart editor =
                IDE.openEditor(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
                    getIFileFromTaskProblem( taskProblem ) );

            if( editor instanceof ITextEditor )
            {
                final ITextEditor textEditor = (ITextEditor) editor;

                textEditor.selectAndReveal( taskProblem.startOffset, taskProblem.endOffset -
                    taskProblem.startOffset );
            }
        }
        catch( PartInitException e )
        {
        }
    }

    public static void taskProblemToMarker( TaskProblem taskProblem, IMarker marker ) throws CoreException
    {
        marker.setAttribute( IMarker.MESSAGE, taskProblem.title );
        marker.setAttribute( "migrationProblem.url", taskProblem.url );
        marker.setAttribute( "migrationProblem.summary", taskProblem.summary );
        marker.setAttribute( "migrationProblem.type", taskProblem.type );
        marker.setAttribute( "migrationProblem.ticket", taskProblem.ticket );
        marker.setAttribute( IMarker.LINE_NUMBER, taskProblem.lineNumber );
        marker.setAttribute( IMarker.CHAR_START, taskProblem.startOffset );
        marker.setAttribute( IMarker.CHAR_END, taskProblem.endOffset );
        marker.setAttribute( "migrationProblem.resolved", taskProblem.isResolved() );
        marker.setAttribute( "migrationProblem.timestamp", taskProblem.getTimestamp() + "" );

        marker.setAttribute( IMarker.LOCATION, taskProblem.file.getName() );
        marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_ERROR );
    }

}
