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

import blade.migrate.api.MigrationConstants;

import com.liferay.ide.project.ui.ProjectUI;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author Gregory Amerson
 */
public class MigrationView extends CommonNavigator implements IDoubleClickListener
{

    public static final String ID = "com.liferay.ide.project.ui.migrationView";
    private static final Image IMAGE_CHECKED = ProjectUI.getDefault().getImageRegistry().get( ProjectUI.CHECKED_IMAGE_ID );
    private static final Image IMAGE_UNCHECKED = ProjectUI.getDefault().getImageRegistry().get( ProjectUI.UNCHECKED_IMAGE_ID );

    private FormText _form;
    private TableViewer _problemsViewer;

    private void createColumns( TableViewer _problemsViewer )
    {
        final String[] titles = { "Title", "Summary", "Line", "Resolved" };
        final int[] bounds = { 100, 100, 100, 100 };

        TableViewerColumn col = createTableViewerColumn( titles[0], bounds[0], 0, _problemsViewer );
        col.setLabelProvider( new ColumnLabelProvider()
        {
            public String getText( Object element )
            {
                TaskProblem p = (TaskProblem) element;

                return p.title;
            }
        });

        col = createTableViewerColumn( titles[1], bounds[1], 1, _problemsViewer );
        col.setLabelProvider( new ColumnLabelProvider()
        {
            public String getText( Object element )
            {
                TaskProblem p = (TaskProblem) element;

                return p.summary;
            }
        });

        col = createTableViewerColumn( titles[2], bounds[2], 2, _problemsViewer );
        col.setLabelProvider( new ColumnLabelProvider()
        {
            public String getText( Object element )
            {
                TaskProblem p = (TaskProblem) element;

                return p.lineNumber + "";
            }
        });

        col = createTableViewerColumn( titles[3], bounds[3], 3, _problemsViewer );
        col.setLabelProvider( new ColumnLabelProvider()
        {
            @Override
            public Image getImage( Object element )
            {
                TaskProblem p = (TaskProblem) element;

                if( p.isResolved() )
                {
                    return IMAGE_CHECKED;
                }
                else
                {
                    return IMAGE_UNCHECKED;
                }
            }

            public String getText( Object element )
            {
                return null;
            }
        });
    }

    @Override
    public void createPartControl( Composite parent )
    {
        SashForm viewParent = new SashForm( parent, SWT.HORIZONTAL );

        super.createPartControl( viewParent );

        SashForm detailParent = new SashForm( viewParent, SWT.VERTICAL );

        _problemsViewer = new TableViewer( detailParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL |
            SWT.FULL_SELECTION | SWT.BORDER );

        createColumns( _problemsViewer );

        final Table table = _problemsViewer.getTable();
        table.setHeaderVisible( true );

        _problemsViewer.setContentProvider( ArrayContentProvider.getInstance() );

        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown( true );
        menuMgr.addMenuListener( new IMenuListener()
        {
            public void menuAboutToShow( IMenuManager manager )
            {
                MigrationView.this.fillContextMenu( manager, _problemsViewer );
            }
        });

        Menu menu = menuMgr.createContextMenu( _problemsViewer.getControl() );
        _problemsViewer.getControl().setMenu( menu );
        getSite().registerContextMenu( menuMgr, _problemsViewer );

        _problemsViewer.addDoubleClickListener( this );

        _form = new FormText( detailParent, SWT.NONE );

        getCommonViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                List<TaskProblem> problems = getTaskProblemsFromSelection( event.getSelection() );

                if( problems != null )
                {
                    _problemsViewer.setInput( problems.toArray() );
                }
            }
        });

        _problemsViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                updateForm( event );
            }
        });

        getCommonViewer().addDoubleClickListener( this );
    }

    private TableViewerColumn createTableViewerColumn(
        String title, int bound, final int colNumber, TableViewer viewer )
    {
        final TableViewerColumn viewerColumn = new TableViewerColumn( viewer, SWT.NONE );
        final TableColumn column = viewerColumn.getColumn();
        column.setText( title );
        column.setWidth( bound );
        column.setResizable( true );
        column.setMoveable( true );

        return viewerColumn;
    }

    @Override
    public void doubleClick( DoubleClickEvent event )
    {
        TaskProblem taskProblem = getTaskProblemFromSelection( event.getSelection() );

        if( taskProblem != null )
        {
            try
            {
                final IEditorPart editor =
                    IDE.openEditor( getSite().getPage(), getIFileFromTaskProblem( taskProblem ) );

                if( editor instanceof ITextEditor )
                {
                    final ITextEditor textEditor = (ITextEditor) editor;

                    textEditor.selectAndReveal( taskProblem.startOffset, taskProblem.endOffset -
                        taskProblem.startOffset );
                }
            }
            catch( PartInitException e )
            {
            }
        }
    }

    private void fillContextMenu( IMenuManager manager, ISelectionProvider provider  )
    {
       new MigrationActionProvider().makeActions( provider ).fillContextMenu( manager );
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

    private IFile getIFileFromTaskProblem( TaskProblem taskProblem )
    {
        return ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI( taskProblem.file.toURI() )[0];
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

    private List<TaskProblem> getTaskProblemsFromFile( IFile file )
    {
        final List<TaskProblem> problems = new ArrayList<>();

        try
        {
            final IMarker[] markers =
                file.findMarkers( MigrationConstants.MIGRATION_MARKER_TYPE, true, IResource.DEPTH_INFINITE );

            for( IMarker marker : markers )
            {
                TaskProblem taskProblem = MigrationUtil.markerToTaskProblem( marker );

                if( taskProblem != null )
                {
                    problems.add( taskProblem );
                }
            }
        }
        catch( CoreException e )
        {
        }

        return problems;
    }

    private List<TaskProblem> getTaskProblemsFromSelection( ISelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            Object element = ss.getFirstElement();

            if( element instanceof IFile )
            {
                final IFile file = (IFile) element;

                return getTaskProblemsFromFile( file );
            }
        }

        return null;
    }

    private void updateForm( SelectionChangedEvent event )
    {
        ISelection selection = event.getSelection();

        TaskProblem taskProblem = getTaskProblemFromSelection( selection );

        if( taskProblem != null )
        {
            _form.setText( generateFormText( taskProblem ), true, false );
        }
        else
        {
            _form.setText( "", false, false );
        }
    };

}
