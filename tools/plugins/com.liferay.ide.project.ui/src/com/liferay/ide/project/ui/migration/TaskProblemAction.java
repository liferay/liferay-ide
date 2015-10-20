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

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.actions.SelectionProviderAction;


/**
 * @author Gregory Amerson
 */
public abstract class TaskProblemAction extends SelectionProviderAction implements IAction
{

    public TaskProblemAction( ISelectionProvider provider, String text )
    {
        super( provider, text );
    }

    @Override
    public void run()
    {
        final List<TaskProblem> taskProblems = MigrationUtil.getTaskProblemsFromSelection( getSelection() );

        for( TaskProblem taskProblem : taskProblems )
        {
            run( taskProblem, getSelectionProvider() );
        }
    }

    public void run( final TaskProblem taskProblem, final ISelectionProvider provider )
    {
        final IResource resource = MigrationUtil.getIFileOrProjectFromTaskProblem( taskProblem );

        new Job( "Marking migration problem as done" )
        {
            protected IStatus run( IProgressMonitor monitor )
            {
                IStatus retval = Status.OK_STATUS;

                if( resource != null && resource.exists() )
                {
                    final IMarker marker = resource.getMarker( taskProblem.getMarkerId() );

                    if( marker != null )
                    {
                        retval = runWithMarker( taskProblem, marker );

                        if( provider instanceof Viewer )
                        {
                            final Viewer viewer = (Viewer) provider;

                            UIUtil.async( new Runnable()
                            {
                                public void run()
                                {
                                    viewer.refresh();
                                }
                            });
                        }
                    }
                    else
                    {
                        retval = ProjectUI.createErrorStatus( "Unable to get marker from file" );
                    }
                }
                else
                {
                    retval = ProjectUI.createErrorStatus( "Unable to get file from problem" );
                }

                return retval;
            }

        }.schedule();

    }

    protected abstract IStatus runWithMarker( TaskProblem taskProblem, IMarker marker );

    @Override
    public void selectionChanged( IStructuredSelection selection )
    {
        Object element = selection.getFirstElement();

        setEnabled( element instanceof TaskProblem );
    }
}
