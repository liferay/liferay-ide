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

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.MigrationConstants;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.ProgressMonitor;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.FileProblemsUtil;
import com.liferay.ide.project.core.upgrade.MigrationProblems;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.core.upgrade.UpgradeProblems;
import com.liferay.ide.project.ui.ProjectUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 * @author Lovett Li
 * @author Terry Jia
 */
public class MigrateProjectHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            Object element = null;

            if( ( (IStructuredSelection) selection ).size() > 1 )
            {
                element = ( (IStructuredSelection) selection ).toArray();
            }
            else
            {
                element = ( (IStructuredSelection) selection ).getFirstElement();
            }

            IProject project = null;
            IProject[] projects = null;

            if( element instanceof IProject )
            {
                project = (IProject) element;
            }
            else if( element instanceof IAdaptable )
            {
                IAdaptable adaptable = (IAdaptable) element;

                project = (IProject) adaptable.getAdapter( IProject.class );
            }
            else if( element instanceof Object[] )
            {
                projects = Arrays.copyOf( (Object[]) element, ( (Object[]) element ).length, IProject[].class );
            }

            if( project != null )
            {
                if( shouldShowMessageDialog( project ) )
                {
                    final boolean shouldContinue = showMessageDialog( project );

                    if( !shouldContinue )
                    {
                        return null;
                    }

                    MarkerUtil.clearMarkers( project, MigrationConstants.MARKER_TYPE, null );
                }

                final IPath location = project.getLocation();

                findMigrationProblems( new IPath[] { location }, new String[] { project.getName() } );
            }
            else if( projects != null )
            {
                final List<IPath> locations = new ArrayList<>();
                final List<String> projectNames = new ArrayList<>();
                boolean shouldContinue = false;

                for( IProject iProject : projects )
                {
                    if( shouldContinue == false && shouldShowMessageDialog( iProject ) )
                    {
                        shouldContinue = showMessageDialog( project );

                        if( !shouldContinue )
                        {
                            return null;
                        }

                    }
                    if( shouldContinue && shouldShowMessageDialog( iProject ) )
                    {
                        MarkerUtil.clearMarkers( iProject, MigrationConstants.MARKER_TYPE, null );
                    }

                    locations.add( iProject.getLocation() );
                    projectNames.add( iProject.getName() );
                }

                findMigrationProblems( locations.toArray( new IPath[0] ), projectNames.toArray( new String[0] ) );
            }
        }

        return null;
    }

    public void findMigrationProblems( final IPath[] locations )
    {
        findMigrationProblems( locations, new String[]{""} );
    }

    public void findMigrationProblems( final IPath[] locations, final String[] projectName )
    {
        Job job = new WorkspaceJob( "Finding migration problems..." )
        {

            @Override
            public IStatus runInWorkspace( final IProgressMonitor monitor ) throws CoreException
            {
                IStatus retval = Status.OK_STATUS;

                final BundleContext context = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();

                ProgressMonitor override = new ProgressMonitor()
                {

                    @Override
                    public void worked( int work )
                    {
                        monitor.worked( work );
                    }

                    @Override
                    public void setTaskName( String taskName )
                    {
                        monitor.setTaskName( taskName );
                    }

                    @Override
                    public boolean isCanceled()
                    {
                        return monitor.isCanceled();
                    }

                    @Override
                    public void done()
                    {
                        monitor.done();
                    }

                    @Override
                    public void beginTask( String taskName, int totalWork )
                    {
                        monitor.beginTask( taskName, totalWork );
                    }
                };

                try
                {
                    final ServiceReference<Migration> sr = context.getServiceReference( Migration.class );
                    final Migration m = context.getService( sr );

                    for( int j = 0; j < locations.length; j++ )
                    {
                        List<Problem> allProblems = new ArrayList<>();

                        if( !override.isCanceled() )
                        {
                            final List<Problem> problems = m.findProblems( locations[j].toFile(), override );

                            for( Problem problem : problems )
                            {
                                if( shouldAdd( problem ) )
                                {
                                    allProblems.add( problem );
                                }
                            }
                        }

                        MigrationProblemsContainer container = null;
                        List<MigrationProblems> migrationProblemsList = new ArrayList<MigrationProblems>();

                        if( allProblems.size() > 0 )
                        {
                            container =
                                UpgradeAssistantSettingsUtil.getObjectFromStore( MigrationProblemsContainer.class );

                            MigrationProblems[] migrationProblemsArray = null;

                            if( container == null )
                            {
                                container = new MigrationProblemsContainer();
                            }

                            if( container.getProblemsArray() != null )
                            {
                                migrationProblemsArray = container.getProblemsArray();
                            }

                            if( migrationProblemsArray != null )
                            {
                                List<MigrationProblems> mpList = Arrays.asList( migrationProblemsArray );

                                for( MigrationProblems mp : mpList )
                                {
                                    migrationProblemsList.add( mp );
                                }
                            }

                            MigrationProblems migrationProblems = new MigrationProblems();

                            List<FileProblems> fileProblemsList =
                                FileProblemsUtil.newFileProblemsListFrom( allProblems.toArray( new Problem[0] ) );

                            migrationProblems.setProblems( fileProblemsList.toArray( new FileProblems[0] ) );

                            migrationProblems.setType( "Code Problems" );
                            migrationProblems.setSuffix( projectName[j] );

                            boolean existing = false;
                            int index = -1;

                            for( int i = 0; i < migrationProblemsList.size(); i++ )
                            {
                                UpgradeProblems upgradeProblems = migrationProblemsList.get( i );

                                if( ( (MigrationProblems) upgradeProblems ).getSuffix().equals( projectName[j] ) )
                                {
                                    existing = true;

                                    index = i;

                                    break;
                                }
                            }

                            if( existing )
                            {
                                migrationProblemsList.set( index, migrationProblems );
                            }
                            else
                            {
                                migrationProblemsList.add( migrationProblems );
                            }
                        }

                        container.setProblemsArray( migrationProblemsList.toArray( new MigrationProblems[0] ) );

                        UpgradeAssistantSettingsUtil.setObjectToStore( MigrationProblemsContainer.class, container );

                        m.reportProblems( allProblems, Migration.DETAIL_LONG, "ide" );
                    }
                }
                catch( Exception e )
                {
                    retval = ProjectUI.createErrorStatus( "Error in migrate command", e );
                }

                return retval;
            }
        };

        try
        {
            PlatformUI.getWorkbench().getProgressService().showInDialog( Display.getDefault().getActiveShell(), job );
        }
        catch( Exception e )
        {
        }

        job.schedule();
    }

    private boolean showMessageDialog( IProject project )
    {
        final Display display = Display.getDefault();
        final Shell shell = display.getActiveShell();

        return MessageDialog.openConfirm(
            shell, "Migrate Liferay Plugin",
            "This project already contains migration problem markers.  All existing markers will be deleted.  "
                + "Do you want to continue to run migration tool?" );
    }

    private boolean shouldShowMessageDialog( IProject project )
    {
        final IMarker[] markers = MarkerUtil.findMarkers( project, MigrationConstants.MARKER_TYPE, null );

        return markers != null && markers.length > 0;
    }

    private boolean shouldAdd( Problem problem )
    {
        String path = problem.getFile().getAbsolutePath().replaceAll( "\\\\", "/" );

        if( path.contains( "WEB-INF/classes" ) )
        {
            return false;
        }

        return true;
    }

}
