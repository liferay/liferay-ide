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
import com.liferay.ide.project.core.upgrade.MigrationProblems;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
 * @author Lovett Li
 */
public class MigrationUtil
{

    public static IResource getIResourceFromProblem( Problem problem )
    {
        return getIResourceFromFile( problem.file );
    }

    public static IResource getIResourceFromFileProblems( FileProblems problem )
    {
        return getIResourceFromFile( problem.getFile() );
    }

    public static IResource getIResourceFromFile( File f )
    {
        IResource retval = null;

        final IFile[] files =
            ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI( f.toURI() );

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

    public static IResource getResourceFromMigrationProblems( MigrationProblems problem )
    {
        String projectName = problem.getSuffix();
        IProject project = CoreUtil.getWorkspaceRoot().getProject( projectName );

        if( project.exists() )
        {
            return project;
        }

        return null;
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

    public static List<Problem> getCurrentProblemsFromTreeNode( ISelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            final Object element = ss.getFirstElement();
            if( element instanceof FileProblems )
            {
                FileProblems fp = (FileProblems) element;
                List<Problem> notIgnoreProblems = new ArrayList<>();

                for (Problem problem : fp.getProblems()) {
                    if (problem.getStatus() != Problem.STATUS_IGNORE) {
                        notIgnoreProblems.add( problem );
                    }
                }

                return notIgnoreProblems;
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

        return new Problem( UUID.randomUUID().toString(),
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

    public static void openEditor( FileProblems problem )
    {
        try
        {
            final IResource resource = getIResourceFromFileProblems( problem );

            if( resource instanceof IFile )
            {
                IDE.openEditor(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), (IFile) resource );
            }
        }
        catch( PartInitException e )
        {
        }
    }

    public static void updateMigrationProblemToStore( Problem problem )
    {
        File file = problem.getFile();

        try
        {
            MigrationProblemsContainer container =
                UpgradeAssistantSettingsUtil.getObjectFromStore( MigrationProblemsContainer.class );

            boolean found = false;

            for( MigrationProblems mp : container.getProblemsArray() )
            {
                for( FileProblems fileProblem : mp.getProblems() )
                {
                    if( fileProblem.getFile().equals( file ) )
                    {
                        for( int i = 0; i < fileProblem.getProblems().size(); i++ )
                        {
                            Problem p = fileProblem.getProblems().get( i );

                            if( p.equals( problem ) )
                            {
                                fileProblem.getProblems().set( i, problem );
                                found = true;
                                break;
                            }
                        }
                    }

                    if( found )
                    {
                        break;
                    }
                }

                if( found )
                {
                    break;
                }
            }

            UpgradeAssistantSettingsUtil.setObjectToStore( MigrationProblemsContainer.class, container );
        }
        catch( IOException e )
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

    public static boolean removeMigrationProblem( MigrationProblems migrationProblem )
    {
        MigrationProblemsContainer container = getMigrationProblemsContainer();

        return removeProblemFromMigrationContainer( migrationProblem.getSuffix(), container );
    }

    public static boolean removeMigrationProblemsFromResource( IResource resource )
    {
        MigrationProblemsContainer container = getMigrationProblemsContainer();

        return removeProblemFromMigrationContainer( resource.getName(), container );
    }

    public static MigrationProblemsContainer getMigrationProblemsContainer()
    {
        try
        {
            return UpgradeAssistantSettingsUtil.getObjectFromStore( MigrationProblemsContainer.class );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    private static boolean removeProblemFromMigrationContainer(
        String projectName, MigrationProblemsContainer container )
    {
        boolean isRemoved = false;

        if( container != null )
        {
            List<MigrationProblems> problems =
                new ArrayList<MigrationProblems>( Arrays.asList( container.getProblemsArray() ) );

            for( MigrationProblems mp : problems )
            {
                if( mp.getSuffix().equals( projectName ) )
                {
                    problems.remove( mp );
                    isRemoved = true;
                    break;
                }
            }
            try
            {
                if( problems.size() != 0 )
                {
                    container.setProblemsArray( problems.toArray( new MigrationProblems[0] ) );
                    UpgradeAssistantSettingsUtil.setObjectToStore( MigrationProblemsContainer.class, container );
                }
                else
                {
                    UpgradeAssistantSettingsUtil.setObjectToStore( MigrationProblemsContainer.class, null );
                }

            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        }

        return isRemoved;
    }

}
