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

package com.liferay.ide.project.ui.dialog;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.LiferayUIPlugin;

/**
 * @author Andy Wu
 */
public abstract class ProjectSelectionDialog extends SelectionStatusDialog
{
    class JavaProjectProvider extends StandardJavaElementContentProvider
    {
        public Object[] getChildren( Object element )
        {
            if( element instanceof IJavaModel )
            {
                final IJavaModel model = (IJavaModel) element;
                final Set<IJavaProject> set = new HashSet<IJavaProject>();

                try
                {
                    final IJavaProject[] projects = model.getJavaProjects();

                    for( int i = 0; i < projects.length; i++ )
                    {
                        if( checkProject(projects[i]) )
                        {
                            set.add( projects[i] );
                        }
                    }
                }
                catch( JavaModelException jme )
                {
                    // ignore
                }

                return set.toArray();
            }

            return super.getChildren( element );
        }
    }

    // sizing constants
    private final static int SIZING_SELECTION_WIDGET_HEIGHT = 250;

    private final static int SIZING_SELECTION_WIDGET_WIDTH = 300;
    /**
     * The filter for the viewer
     */
    private ViewerFilter fFilter;

    // the visual selection widget group
    private TableViewer fTableViewer;

    /**
     * Constructor
     *
     * @param parentShell
     * @param projectsWithSpecifics
     */
    public ProjectSelectionDialog( Shell parentShell, ViewerFilter filter )
    {
        super( parentShell );

        fFilter = filter;
    }

    protected abstract boolean checkProject(IJavaProject project);

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
     */
    protected void computeResult()
    {
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea( Composite parent )
    {
        // page group
        Composite composite = (Composite) super.createDialogArea( parent );

        Font font = parent.getFont();
        composite.setFont( font );

        createMessageArea( composite );

        fTableViewer = new TableViewer( composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI );
        fTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                doSelectionChanged( ( (IStructuredSelection) event.getSelection() ).toArray() );
            }
        });

        fTableViewer.addDoubleClickListener( new IDoubleClickListener()
        {
            public void doubleClick( DoubleClickEvent event )
            {
                okPressed();
            }
        });

        GridData data = new GridData( SWT.FILL, SWT.FILL, true, true );
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
        fTableViewer.getTable().setLayoutData( data );

        fTableViewer.setLabelProvider( new JavaElementLabelProvider() );
        fTableViewer.setContentProvider( getContentProvider() );
        fTableViewer.setComparator( new JavaElementComparator() );
        fTableViewer.getControl().setFont( font );

        updateFilter( true );

        IJavaModel input = JavaCore.create( ResourcesPlugin.getWorkspace().getRoot() );
        fTableViewer.setInput( input );

        doSelectionChanged( new Object[0] );
        Dialog.applyDialogFont( composite );

        return composite;
    }

    /**
     * Handles the change in selection of the viewer and updates the status of the dialog at the same time
     *
     * @param objects
     */
    private void doSelectionChanged( Object[] objects )
    {
        updateStatus( new Status( IStatus.OK, LiferayUIPlugin.PLUGIN_ID, StringPool.EMPTY ) );
        setSelectionResult( objects );
    }

    protected IContentProvider getContentProvider()
    {
        return new JavaProjectProvider();
    }

    /**
     * Updates the viewer filter based on the selection of the 'show project with...' button
     *
     * @param selected
     */
    protected void updateFilter( boolean selected )
    {
        if( fFilter == null )
        {
            return;
        }

        if( selected )
        {
            fTableViewer.addFilter( fFilter );
        }
        else
        {
            fTableViewer.removeFilter( fFilter );
        }
    }

}
