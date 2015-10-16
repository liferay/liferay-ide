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

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

import blade.migrate.api.Migration;
import blade.migrate.api.MigrationConstants;
import blade.migrate.api.Problem;
import blade.migrate.api.ProgressMonitor;

import com.liferay.ide.project.ui.ProjectUI;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class MigrateProjectHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            Object element = ( (IStructuredSelection) selection ).getFirstElement();

            IProject project = null;

            if( element instanceof IProject )
            {
                project = (IProject) element;
            }
            else if( element instanceof IAdaptable )
            {
                IAdaptable adaptable = (IAdaptable) element;

                project = (IProject) adaptable.getAdapter( IProject.class );
            }

            if( project != null )
            {
                if( shouldShowMessageDialog( project ) )
                {
                    if( !showMessageDialog( project ) )
                    {
                        return null;
                    }
                    clearExistingMarkersFromResourcet( project );
                }
                final IPath location = project.getLocation();

                Job job = new WorkspaceJob( "Finding migration problems..." )
                {
                    @Override
                    public IStatus runInWorkspace( final IProgressMonitor monitor ) throws CoreException
                    {
                        IStatus retval = Status.OK_STATUS;

                        final BundleContext context =
                            FrameworkUtil.getBundle( this.getClass() ).getBundleContext();

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

                            final List<Problem> problems = m.findProblems( location.toFile(), override );

                            m.reportProblems( problems, Migration.DETAIL_LONG, "ide" );
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
                    PlatformUI.getWorkbench().getProgressService().showInDialog(
                        Display.getDefault().getActiveShell(), job );
                }
                catch( Exception e )
                {
                }

                job.schedule();
            }
        }

        return null;
    }

    private void clearExistingMarkersFromResourcet( IProject project )
    {
        try
        {
            IMarker[] markers = project.findMarkers( MigrationConstants.MARKER_TYPE, false, IResource.DEPTH_INFINITE );

            if( markers.length > 0 )
            {
                for( IMarker m : markers )
                {
                    m.delete();
                }
            }
        }
        catch( CoreException e1 )
        {
        }
    }

    private boolean showMessageDialog( IProject project )
    {
        Display display = Display.getCurrent();
        Shell shell = display.getActiveShell();

        return MessageDialog.openConfirm(
            shell, "Migrator",
            "Do you want to continue migrator this project, it will cause all the Markers of this project are cleared ?" );
    }

    private boolean shouldShowMessageDialog( IProject project )
    {
        try
        {
            IMarker[] markers = project.findMarkers( MigrationConstants.MARKER_TYPE, false, IResource.DEPTH_INFINITE );
            if( markers.length > 0 )
            {
                return true;
            }
            return false;

        }
        catch( CoreException e )
        {
        }
        return false;
    }
}
