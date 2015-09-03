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

import blade.migrate.api.Problem;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Gregory Amerson
 */
public class MigrationContentProvider implements ITreeContentProvider
{

    private Object _input;
    private MigrationTask[] _tasks;

    @Override
    public void dispose()
    {
    }

    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        _input = newInput;

        if( _input instanceof List<?> )
        {
            List<?> tasks = (List<?>) _input;

            _tasks = tasks.toArray( new MigrationTask[0] );
        }
        else if( _input instanceof MigrationTask[] )
        {
            _tasks = (MigrationTask[]) _input;
        }
    }

    @Override
    public Object[] getElements( Object inputElement )
    {
        return _tasks;
    }

    @Override
    public Object[] getChildren( Object parentElement )
    {
        if( parentElement instanceof MigrationTask )
        {
            final MigrationTask task = (MigrationTask) parentElement;

            return task.getProblems().toArray( new Problem[0] );
        }
        else if( parentElement instanceof TaskProblem )
        {
            TaskProblem problem = (TaskProblem) parentElement;

            return new String[] { (String) problem.summary, problem.ticket };
        }

        return null;
    }

    @Override
    public Object getParent( Object element )
    {
        if( element instanceof TaskProblem )
        {
            final TaskProblem problem = (TaskProblem) element;

            return problem.getParent();
        }

        return null;
    }

    @Override
    public boolean hasChildren( Object element )
    {
        return element instanceof Problem || element instanceof MigrationTask;
    }

}
