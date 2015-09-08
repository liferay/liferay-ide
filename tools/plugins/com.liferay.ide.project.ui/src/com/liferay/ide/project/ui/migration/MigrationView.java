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

import blade.migrate.api.MigrationListener;
import blade.migrate.api.Problem;

import com.liferay.ide.project.ui.ProjectUI;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Gregory Amerson
 */
public class MigrationView extends ViewPart implements MigrationListener
{
    private TreeViewer _viewer;
    private List<MigrationTask> _tasks;
    private ServiceRegistration<MigrationListener> _listenerRef;

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

        Dictionary<String, ?> properties = new Hashtable<>();
        _listenerRef =
            ProjectUI.getDefault().getBundle().getBundleContext().registerService(
                MigrationListener.class, this, properties );
    }

    @Override
    public void dispose()
    {
        super.dispose();

        if( _listenerRef != null )
        {
            _listenerRef.unregister();
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

    @Override
    public void problemsFound( List<Problem> problems )
    {
    }

}
