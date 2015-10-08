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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import blade.migrate.api.Migration;
import blade.migrate.api.Problem;
import blade.migrate.api.ProgressMonitor;

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;

/**
 * @author Andy Wu
 */
public class MigrationViewAction extends Action
{

    private Shell shell ;

    protected MigrationViewAction( String text ,Shell shell)
    {
        super( text );
        setImageDescriptor( ImageDescriptor.createFromURL( ProjectUI.getDefault().getBundle().getEntry(
            "/icons/e16/liferay.png" ) ) );
        this.shell = shell;
    }

    @Override
    public void run()
    {
        ViewerFilter filter = new ViewerFilter()
        {

            @Override
            public boolean select( Viewer viewer, Object parentElement, Object element )
            {
                if( element instanceof IJavaProject )
                {
                    IProject project = ( (IJavaProject) element ).getProject();

                    if( project.getName().equals( "External Plug-in Libraries" ) )
                    {
                        return false;
                    }

                    return true;
                }

                return false;
            }
        };

        JavaProjectSelectionDialog dialog = new JavaProjectSelectionDialog( shell , filter );
        dialog.create();

        if (dialog.open() == Window.OK)
        {
            Object[] selectedProjects = dialog.getResult();
            if( selectedProjects!=null ){
                IJavaProject javaProject = (IJavaProject) selectedProjects[0];
                migration(javaProject.getProject().getLocation());
            }
        }
    }

    public static void migration(final IPath location)
    {
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
