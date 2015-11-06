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

import com.liferay.blade.api.MigrationConstants;

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class MigrationUtil
{

    private final static String CONTENT_PROVIDER_ID = "com.liferay.ide.project.ui.migration.content";

    public static List<TaskProblem> getAllTaskProblems( CommonViewer commonViewer )
    {
        final List<TaskProblem> problems = new ArrayList<>();

        final ITreeContentProvider contentProvider =
            commonViewer.getNavigatorContentService().getContentExtensionById( CONTENT_PROVIDER_ID ).getContentProvider();

        if( contentProvider != null && contentProvider instanceof MigrationContentProvider )
        {
            final MigrationContentProvider mcp = (MigrationContentProvider) contentProvider;

            for( IResource resource : mcp._resources )
            {
                problems.addAll( getTaskProblemsFromResource( resource ) );
            }
        }

        return problems;
    }

    public static IResource getIResourceFromTaskProblem( TaskProblem taskProblem )
    {
        IResource retval = null;

        final IFile[] files =
            ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI( taskProblem.file.toURI() );

        for( IFile file : files )
        {
            if( file.exists() )
            {
                // always prefer the file in a liferay project
                if( CoreUtil.isLiferayProject( file.getProject() ) )
                {
                    retval = file;
                    break;
                }

                // if not lets pick the one that is shortest path
                if( retval == null )
                {
                    retval = file;
                }
                else
                {
                    if( file.getFullPath().segmentCount() < retval.getFullPath().segmentCount() )
                    {
                        retval = file;
                    }
                }
            }
            else {
                IPath path = file.getFullPath();

                IProject project =
                    ResourcesPlugin.getWorkspace().getRoot().getProject( path.segment( path.segmentCount() - 1 ) );

                if( project.exists() )
                {
                    retval = project;
                }
            }
        }

        return retval;
    }

    public static List<TaskProblem> getResolvedTaskProblemsFromResource( IResource resource )
    {
        final List<TaskProblem> problems = new ArrayList<>();

        try
        {
            final IMarker[] markers =
                resource.findMarkers( MigrationConstants.MARKER_TYPE, true, IResource.DEPTH_ZERO );

            for( IMarker marker : markers )
            {
                TaskProblem taskProblem = markerToTaskProblem( marker );

                if( taskProblem != null && taskProblem.isResolved() )
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

    public static TaskProblem getTaskProblemFromSelection( ISelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            Object element = ss.getFirstElement();

            if( element instanceof TaskProblem )
            {
                return (TaskProblem) element;
            }
        }

        return null;
    }

    public static List<TaskProblem> getTaskProblemsFromSelection( ISelection selection )
    {
        final List<TaskProblem> problems = new ArrayList<>();

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            Iterator<?> elements = ss.iterator();

            while( elements.hasNext() )
            {
                Object element = elements.next();

                if( element instanceof TaskProblem )
                {
                    problems.add( (TaskProblem) element );
                }
            }
        }

        return problems;
    }

    public static List<TaskProblem> getTaskProblemsFromResource( IResource resource )
    {
        final List<TaskProblem> problems = new ArrayList<>();

        try
        {
            final IMarker[] markers =
                resource.findMarkers( MigrationConstants.MARKER_TYPE, true, IResource.DEPTH_ZERO );

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

    public static List<TaskProblem> getTaskProblemsFromTreeNode( ISelection selection, CommonViewer commonViewer )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            final Object element = ss.getFirstElement();

            IResource resource = null;

            if( element instanceof IResource )
            {
                resource = (IResource) element;
            }
            else if( element instanceof MPNode )
            {
                final MPNode node = (MPNode) element;

                final IResource member = CoreUtil.getWorkspaceRoot().findMember( node.incrementalPath );

                if( member != null && member.exists() )
                {
                    resource = member;
                }
            }

            if( resource != null )
            {
                return getTaskProblemsFromResource( resource );
            }
            else if( element instanceof MPTree )
            {
                return getAllTaskProblems( commonViewer );
            }
        }

        return null;
    }

    public static TaskProblem markerToTaskProblem( IMarker marker )
    {
        final String title = marker.getAttribute( IMarker.MESSAGE, "" );
        final String summary = marker.getAttribute( "migrationProblem.summary", "" );
        final String type = marker.getAttribute( "migrationProblem.type", "" );
        final String ticket = marker.getAttribute( "migrationProblem.ticket", "" );
        final int lineNumber = marker.getAttribute( IMarker.LINE_NUMBER, 0 );
        final int startOffset = marker.getAttribute( IMarker.CHAR_START, 0 );
        final int endOffset = marker.getAttribute( IMarker.CHAR_END, 0 );
        final String html = marker.getAttribute( "migrationProblem.html", "" );
        final String autoCorrectContext = marker.getAttribute( "migrationProblem.autoCorrectContext", "" );
        final boolean resolved = marker.getAttribute( "migrationProblem.resolved", false );
        final long markerId = marker.getId();

        final File file = new File( marker.getResource().getLocationURI() );

        return new TaskProblem(
            title, summary, type, ticket, file, lineNumber, startOffset, endOffset, html, autoCorrectContext,
            resolved, markerId );
    }

    public static void openEditor( TaskProblem taskProblem )
    {
        try
        {
            final IResource resource = getIResourceFromTaskProblem( taskProblem );

            if( resource instanceof IFile )
            {
                final IEditorPart editor =
                    IDE.openEditor(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
                        (IFile) resource );

                if( editor instanceof ITextEditor )
                {
                    final ITextEditor textEditor = (ITextEditor) editor;

                    textEditor.selectAndReveal( taskProblem.startOffset, taskProblem.endOffset -
                        taskProblem.startOffset );
                }
            }
        }
        catch( PartInitException e )
        {
        }
    }

    public static void taskProblemToMarker( TaskProblem taskProblem, IMarker marker ) throws CoreException
    {
        marker.setAttribute( IMarker.MESSAGE, taskProblem.title );
        marker.setAttribute( "migrationProblem.summary", taskProblem.summary );
        marker.setAttribute( "migrationProblem.type", taskProblem.type );
        marker.setAttribute( "migrationProblem.ticket", taskProblem.ticket );
        marker.setAttribute( IMarker.LINE_NUMBER, taskProblem.lineNumber );
        marker.setAttribute( IMarker.CHAR_START, taskProblem.startOffset );
        marker.setAttribute( IMarker.CHAR_END, taskProblem.endOffset );
        marker.setAttribute( "migrationProblem.resolved", taskProblem.isResolved() );
        marker.setAttribute( "migrationProblem.html", taskProblem.html );
        marker.setAttribute( "migrationProblem.autoCorrectContext", taskProblem.getAutoCorrectContext() );

        marker.setAttribute( IMarker.LOCATION, taskProblem.file.getName() );
        marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_ERROR );
    }

}
