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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Gregory Amerson
 */
public class MigrationView extends ViewPart
{
    private TreeViewer _viewer;
    private List<MigrationTask> _tasks;

    @Override
    public void createPartControl( Composite parent )
    {
        _viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        _viewer.setContentProvider( new MigrationContentProvider() );
        _viewer.setLabelProvider( new MigrationLabelProvider() );
        _viewer.setInput( _tasks.toArray( new MigrationTask[0] ) );
    }

    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException
    {
        super.init( site, memento );

        try
        {
            _tasks = ProjectUI.getDefault().getMigrationTasks( false );
        }
        catch( CoreException e )
        {
            throw new PartInitException( e.getStatus() );
        }
    }

    @Override
    public void setFocus()
    {
        if( _viewer != null && _viewer.getControl() != null )
        {
            _viewer.getControl().setFocus();
        }
    }

}
