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
import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;

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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class MigrationUtil
{

    public static IResource getIResourceFromProblem( Problem problem )
    {
        IResource retval = null;

        final IFile[] files =
            ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI( problem.file.toURI() );

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

    public static List<Problem> getResolvedProblemsFromResource( IResource resource )
    {
        final List<Problem> problems = new ArrayList<>();

        try
        {
            final IMarker[] markers =
                resource.findMarkers( MigrationConstants.MARKER_TYPE, true, IResource.DEPTH_ZERO );

            for( IMarker marker : markers )
            {
                Problem problem = markerToProblem( marker );

                if( problem != null && problem.getStatus() == Problem.STATUS_RESOLVED )
                {
                    problems.add( problem );
                }
            }
        }
        catch( CoreException e )
        {
        }

        return problems;
    }

    public static Problem getProblemFromSelection( ISelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            Object element = ss.getFirstElement();

            if( element instanceof Problem )
            {
                return (Problem) element;
            }
        }

        return null;
    }

    public static List<Problem> getProblemsFromSelection( ISelection selection )
    {
        final List<Problem> problems = new ArrayList<>();

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            Iterator<?> elements = ss.iterator();

            while( elements.hasNext() )
            {
                Object element = elements.next();

                if( element instanceof Problem )
                {
                    problems.add( (Problem) element );
                }
            }
        }

        return problems;
    }

    public static List<Problem> getProblemsFromResource( IResource resource )
    {
        final List<Problem> problems = new ArrayList<>();

        try
        {
            final IMarker[] markers =
                resource.findMarkers( MigrationConstants.MARKER_TYPE, true, IResource.DEPTH_ZERO );

            for( IMarker marker : markers )
            {
                Problem problem = markerToProblem( marker );

                if( problem != null )
                {
                    problems.add( problem );
                }
            }
        }
        catch( CoreException e )
        {
        }

        return problems;
    }

    public static List<Problem> getProblemsFromTreeNode( ISelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            final Object element = ss.getFirstElement();
            if( element instanceof FileProblems )
            {
                FileProblems fp = (FileProblems) element;
                return fp.getProblems();
            }
        }

        return null;
    }

    public static Problem markerToProblem( IMarker marker )
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
        final int status = marker.getAttribute( "migrationProblem.status", 0 );
        final long markerId = marker.getId();

        final File file = new File( marker.getResource().getLocationURI() );

        return new Problem(
            title, summary, type, ticket, file, lineNumber, startOffset, endOffset, html, autoCorrectContext,
            status, markerId );
    }

    public static void openEditor( Problem Problem )
    {
        try
        {
            final IResource resource = getIResourceFromProblem( Problem );

            if( resource instanceof IFile )
            {
                final IEditorPart editor =
                    IDE.openEditor(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
                        (IFile) resource );

                if( editor instanceof ITextEditor )
                {
                    final ITextEditor textEditor = (ITextEditor) editor;

                    textEditor.selectAndReveal( Problem.startOffset, Problem.endOffset -
                        Problem.startOffset );
                }
            }
        }
        catch( PartInitException e )
        {
        }
    }

    public static void problemToMarker( Problem problem, IMarker marker ) throws CoreException
    {
        marker.setAttribute( IMarker.MESSAGE, problem.title );
        marker.setAttribute( "migrationProblem.summary", problem.summary );
        marker.setAttribute( "migrationProblem.type", problem.type );
        marker.setAttribute( "migrationProblem.ticket", problem.ticket );
        marker.setAttribute( IMarker.LINE_NUMBER, problem.lineNumber );
        marker.setAttribute( IMarker.CHAR_START, problem.startOffset );
        marker.setAttribute( IMarker.CHAR_END, problem.endOffset );
        marker.setAttribute( "migrationProblem.status", problem.status );
        marker.setAttribute( "migrationProblem.html", problem.html );
        marker.setAttribute( "migrationProblem.autoCorrectContext", problem.getAutoCorrectContext() );

        marker.setAttribute( IMarker.LOCATION, problem.file.getName() );
        marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_ERROR );
    }

}
