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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Gregory Amerson
 */
public class MigrationView extends ViewPart implements MigrationListener
{
    private TreeViewer _viewer;
    private List<MigrationTask> _tasks;
    private ServiceRegistration<MigrationListener> _listenerRef;
    private FormText _form;

    @Override
    public void createPartControl( Composite parent )
    {
        SashForm viewParent = new SashForm( parent, SWT.HORIZONTAL );

        _viewer = new TreeViewer( viewParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        _viewer.setContentProvider( new MigrationContentProvider() );
        _viewer.setLabelProvider( new MigrationLabelProvider() );
        _viewer.setInput( _tasks.toArray( new MigrationTask[0] ) );
        _viewer.addOpenListener( new IOpenListener()
        {
            @Override
            public void open( OpenEvent event )
            {
                TaskProblem taskProblem = getTaskProblemFromSelection( event.getSelection() );

                if( taskProblem != null )
                {
                    try
                    {
                        IEditorPart editor = IDE.openEditor( getSite().getPage(), getIFileFromTaskProblem( taskProblem ) );

                        if( editor instanceof ITextEditor )
                        {
                            ITextEditor textEditor = (ITextEditor) editor;
                            textEditor.selectAndReveal( taskProblem.startOffset, taskProblem.endOffset - taskProblem.startOffset );
                        }
                    }
                    catch( PartInitException e )
                    {
                    }
                }
            }
        });

        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown( true );
        menuMgr.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                MigrationView.this.fillContextMenu( manager );
            }
        });
        Menu menu = menuMgr.createContextMenu( _viewer.getControl() );
        _viewer.getControl().setMenu( menu );
        getSite().registerContextMenu( menuMgr, _viewer );

        _form = new FormText( viewParent, SWT.NONE );

        _viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged( SelectionChangedEvent event )
            {
                updateForm( event );
            }
        });
    }

    protected void fillContextMenu( IMenuManager manager )
    {
        manager.add( new OpenAction( _viewer, "Open" ) );
        manager.add( new OpenAction( _viewer, "Auto-migrate" ) );
        manager.add( new Separator() );
        manager.add( new OpenAction( _viewer, "Mark done" ) );
        manager.add( new OpenAction( _viewer, "Mark undone" ) );
        manager.add( new OpenAction( _viewer, "Ignore" ) );
        manager.add( new Separator() );
        manager.add( new OpenAction( _viewer, "Remove from task" ) );
    }

    protected IFile getIFileFromTaskProblem( TaskProblem taskProblem )
    {
        return ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI( taskProblem.file.toURI() )[0];
    }

    private void updateForm( SelectionChangedEvent event )
    {
        ISelection selection = event.getSelection();

        TaskProblem taskProblem = getTaskProblemFromSelection( selection );

        if( taskProblem != null )
        {
            _form.setText( generateFormText( taskProblem ), true, false );
        }
    }

    private String generateFormText( TaskProblem taskProblem )
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "<form><p>" );

        sb.append( "<b>Problem:</b><br/>" );
        sb.append( "\t" + taskProblem.title + "<br/>" );

        sb.append( "<b>Description:</b><br/>" );
        sb.append( "\t" + taskProblem.summary + "<br/>" );

        sb.append( "<b>Type:</b><br/>" );
        sb.append( "\t" + taskProblem.type + "<br/>" );

        sb.append( "<b>Line:</b><br/>" );
        sb.append( "\t" + taskProblem.lineNumber + "<br/>" );

        sb.append( "<b>Tickets:</b><br/>" );
        sb.append( "\t" + "<a href='lps'>" + taskProblem.ticket + "</a><br/>" );

        sb.append( "<b>More details:</b><br/>" );
        sb.append( "\t" + "<a href='lps'>See full Breaking Changes document</a><br/>" );

        sb.append( "</p></form>" );

        return sb.toString();
    }

    private TaskProblem getTaskProblemFromSelection( ISelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            Object element = ss.getFirstElement();

            if( element instanceof TaskProblem )
            {
                return (TaskProblem) element;
            }
        }

        return null;
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
