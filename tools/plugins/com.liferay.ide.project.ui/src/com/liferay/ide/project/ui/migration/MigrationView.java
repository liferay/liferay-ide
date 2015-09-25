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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.ProjectUI;

import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IResource;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.internal.navigator.actions.CommonActionDescriptorManager;
import org.eclipse.ui.internal.navigator.actions.CommonActionProviderDescriptor;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.ui.navigator.NavigatorActionService;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;


/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
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
        CommonActionProvider ap = getCommonActionProvider( new StructuredSelection( new TaskProblem() ) );

        if( ap instanceof MigrationActionProvider )
        {
            MigrationActionProvider mp = (MigrationActionProvider)ap;

            mp.registerSelectionProvider( _problemsViewer );
        }

        _form = new FormText( detailParent, SWT.NONE );

        _form.addHyperlinkListener( new IHyperlinkListener()
        {

            @Override
            public void linkExited( org.eclipse.ui.forms.events.HyperlinkEvent e )
            {
            }

            @Override
            public void linkEntered( org.eclipse.ui.forms.events.HyperlinkEvent e )
            {
            }

            @Override
            public void linkActivated( org.eclipse.ui.forms.events.HyperlinkEvent e )
            {
                if( e.data instanceof String )
                {
                    String url = (String) e.data;

                    openBrowser( url );
                }
            }
        } );

        getCommonViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                List<TaskProblem> problems = getTaskProblemsFromSelection( event.getSelection() );

                if( problems != null && problems.size() > 0 )
                {
                    _problemsViewer.setInput( problems.toArray() );
                    _problemsViewer.setSelection( new StructuredSelection( problems.get( 0 ) ) );
                }
                else
                {
                    _problemsViewer.setInput( null );
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
            MigrationUtil.openEditor( taskProblem );
        }
    }

    private void fillContextMenu( IMenuManager manager, ISelectionProvider provider  )
    {
        CommonActionProvider instance = getCommonActionProvider( provider.getSelection() );

        instance.setContext( new ActionContext( provider.getSelection() ) );
        instance.fillContextMenu( manager );
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
        sb.append( "\t" );
        sb.append( getLinkTags( taskProblem.ticket ) );
        sb.append( "<br/>" );

        sb.append( "<b>More details:</b><br/>" );
        sb.append( "\t" + "<a href='" + taskProblem.url + "'>See full Breaking Changes document</a><br/>" );

        sb.append( "</p></form>" );

        return sb.toString();
    }

    CommonActionProvider getCommonActionProvider( ISelection selection )
    {
        final INavigatorContentService contentService = getCommonViewer().getCommonNavigator().getNavigatorContentService();
        final ActionContext context = new ActionContext( selection );
        final CommonActionProviderDescriptor[] providerDescriptors = CommonActionDescriptorManager
                        .getInstance().findRelevantActionDescriptors(contentService, context);
        final NavigatorActionService navigatorActionService = getCommonViewer().getCommonNavigator().getNavigatorActionService();

        return navigatorActionService.getActionProviderInstance( providerDescriptors[0] );
    }

    private String getLinkTags(String ticketNumbers) {
        String[] ticketNumberArray =  ticketNumbers.split( "," );

        StringBuilder sb =  new StringBuilder();

        for (int i=0;i<ticketNumberArray.length;i++) {
            String ticketNumber = ticketNumberArray[i];
            sb.append( "<a href='https://issues.liferay.com/browse/" );
            sb.append( ticketNumber );
            sb.append( "'>" );
            sb.append( ticketNumber );
            sb.append( "</a>" );

            if( ticketNumberArray.length > 1 && i != ticketNumberArray.length - 1 )
            {
                sb.append( "," );
            }
        }

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

    private List<TaskProblem> getTaskProblemsFromSelection( ISelection selection )
    {
        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) selection;

            final Object element = ss.getFirstElement();

            IResource resource = null;

            if( element instanceof IResource )
            {
                resource = (IResource) element;
            }
            else if( element instanceof MPNode )
            {
                final MPNode node = (MPNode) element;

                final IResource member = CoreUtil.getWorkspaceRoot().findMember( node.incrementalPath );

                if( member != null && member.exists() )
                {
                    resource = member;
                }
            }

            if( resource != null )
            {
                return MigrationUtil.getTaskProblemsFromResource( resource );
            }
        }

        return null;
    }

    private void openBrowser( final String url )
    {
        Display.getDefault().asyncExec( new Runnable()
        {

            public void run()
            {
                try
                {
                    IWorkbenchBrowserSupport browserSupport =
                        ServerUIPlugin.getInstance().getWorkbench().getBrowserSupport();

                    IWebBrowser browser =
                        browserSupport.createBrowser( IWorkbenchBrowserSupport.AS_EXTERNAL |
                            IWorkbenchBrowserSupport.NAVIGATION_BAR, null, "", null );

                    browser.openURL( new URL( url ) );
                }
                catch( Exception e )
                {
                    ProjectUI.logError( e );
                }
            }
        } );
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
