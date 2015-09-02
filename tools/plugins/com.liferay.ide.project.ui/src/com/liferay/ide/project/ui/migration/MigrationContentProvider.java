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
    private Problem[] _problems;

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
            List<?> problems = (List<?>) _input;

            _problems = problems.toArray( new Problem[0] );
        }
    }

    @Override
    public Object[] getElements( Object inputElement )
    {
        return _problems;
    }

    @Override
    public Object[] getChildren( Object parentElement )
    {
        if( parentElement instanceof Problem )
        {
            Problem problem = (Problem) parentElement;

            return new String[] { (String) problem.summary, problem.ticket };
        }

        return null;
    }

    @Override
    public Object getParent( Object element )
    {
        return null;
    }

    @Override
    public boolean hasChildren( Object element )
    {
        return element instanceof Problem;
    }

}
