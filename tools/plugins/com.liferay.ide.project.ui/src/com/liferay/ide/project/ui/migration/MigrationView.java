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

import blade.migrate.api.AutoMigrateException;
import blade.migrate.api.AutoMigrator;
import blade.migrate.api.Problem;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledFormText;
import org.eclipse.ui.internal.navigator.actions.CommonActionDescriptorManager;
import org.eclipse.ui.internal.navigator.actions.CommonActionProviderDescriptor;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.ui.navigator.NavigatorActionService;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class MigrationView extends CommonNavigator implements IDoubleClickListener
{

    public static final String ID = "com.liferay.ide.project.ui.migrationView";
    private static final Image IMAGE_CHECKED =
        ProjectUI.getDefault().getImageRegistry().get( ProjectUI.CHECKED_IMAGE_ID );
    private static final Image IMAGE_UNCHECKED =
        ProjectUI.getDefault().getImageRegistry().get( ProjectUI.UNCHECKED_IMAGE_ID );

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

                return p.lineNumber > -1 ? ( p.lineNumber + "" ) : "";
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

        _problemsViewer =
            new TableViewer( detailParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER );

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
            MigrationActionProvider mp = (MigrationActionProvider) ap;

            mp.registerSelectionProvider( _problemsViewer );
        }

        ScrolledFormText sft = new ScrolledFormText( detailParent, false );
        sft.setExpandVertical( true );

        _form = new FormText( sft, SWT.NONE );
        sft.setFormText( _form );

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
                    final String url = (String) e.data;

                    final TaskProblem taskProblem = getTaskProblemFromSelection( _problemsViewer.getSelection() );

                    if( "autoCorrect".equals( url ) )
                    {
                        autoCorrect( taskProblem );
                    }
                    else if( "html".equals( url ) )
                    {
                        displayPopupHtml( taskProblem.title, taskProblem.html );
                    }
                    else if( url.startsWith( "http" ) )
                    {
                        openBrowser( url );
                    }
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
            public void selectionChanged( final SelectionChangedEvent event )
            {
                UIUtil.async( new Runnable()
                {
                    public void run()
                    {
                        updateForm( event );
                    }
                }, 50 );
            }
        });

        getCommonViewer().addDoubleClickListener( this );
    }

    private void displayPopupHtml( final String title, final String html )
    {
        final Shell shell = new Shell( this.getViewSite().getShell(), SWT.DIALOG_TRIM | SWT.ON_TOP | SWT.RESIZE );
        shell.setText( title );
        shell.setLayout( new FillLayout() );

        final Browser browser;

        try
        {
            browser = new Browser( shell, SWT.NONE );
            browser.setText( html );
        }
        catch( SWTError e )
        {
            return;
        }

        shell.setSize( 900, 900 );

        Integer popupLastX = getMemento().getInteger( "popupLastX" );
        Integer popupLastY = getMemento().getInteger( "popupLastY" );
        Integer popupSizeX = getMemento().getInteger( "popupSizeX" );
        Integer popupSizeY = getMemento().getInteger( "popupSizeY" );

        if( popupLastX != null && popupLastY != null )
        {
            shell.setLocation( popupLastX, popupLastY );
        }

        if( popupSizeX != null && popupSizeY != null )
        {
            shell.setSize( popupSizeX, popupSizeY );
        }

        shell.addDisposeListener( new DisposeListener()
        {
            public void widgetDisposed( DisposeEvent e )
            {
                savePopupState( shell );
                browser.dispose();
            }
        });

        shell.addListener( SWT.Traverse, new Listener()
        {
            public void handleEvent( Event event )
            {
                switch( event.detail )
                {
                case SWT.TRAVERSE_ESCAPE:
                    savePopupState( shell );
                    shell.close();
                    event.detail = SWT.TRAVERSE_NONE;
                    event.doit = false;
                    break;
                }
            }
        });

        shell.open();
    }

    private void savePopupState( Shell shell )
    {
        final Point location = shell.getLocation();
        final Point size = shell.getSize();

        MigrationView.this.getMemento().putInteger( "popupLastX", location.x );
        MigrationView.this.getMemento().putInteger( "popupLastY", location.y );
        MigrationView.this.getMemento().putInteger( "popupSizeX", size.x );
        MigrationView.this.getMemento().putInteger( "popupSizeY", size.y );

    }

    private void autoCorrect( final TaskProblem problem )
    {
        final BundleContext context = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();
        final IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI( problem.file.toURI() );

        new WorkspaceJob( "auto correct")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    final String autoCorrectKey =
                        problem.autoCorrectContext.substring( 0, problem.autoCorrectContext.indexOf( ":" ) );
                    final Collection<ServiceReference<AutoMigrator>> refs =
                        context.getServiceReferences( AutoMigrator.class, "(auto.correct=" + autoCorrectKey + ")" );

                    for( ServiceReference<AutoMigrator> ref : refs )
                    {
                        final AutoMigrator autoMigrator = context.getService( ref );
                        List<Problem> problems = new ArrayList<>();
                        problems.add( problem );
                        autoMigrator.correctProblems( problem.file, problems );
                    }

                    for( IFile file : files )
                    {
                        file.refreshLocal( IResource.DEPTH_ONE, monitor );
                    }
                }
                catch( InvalidSyntaxException e )
                {
                }
                catch( AutoMigrateException | CoreException e )
                {
                    return ProjectUI.createErrorStatus( "Unable to auto correct problem", e );
                }

                return Status.OK_STATUS;
            }
        }.schedule();
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

    private void fillContextMenu( IMenuManager manager, ISelectionProvider provider )
    {
        CommonActionProvider instance = getCommonActionProvider( provider.getSelection() );

        instance.setContext( new ActionContext( provider.getSelection() ) );
        instance.fillContextMenu( manager );
    }

    private String generateFormText( TaskProblem taskProblem )
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "<form><p>" );

        sb.append( "<b>Problem:</b> " + taskProblem.title + "<br/><br/>" );

        sb.append( "<b>Description:</b><br/>" );
        sb.append( "\t" + taskProblem.summary + "<br/><br/>" );

        // sb.append( "<b>Type:</b><br/>" );
        // sb.append( "\t" + taskProblem.type + "<br/>" );

        if( taskProblem.lineNumber > -1 )
        {
            sb.append( "<b>Line:</b> " + taskProblem.lineNumber + "<br/><br/>" );
        }

        if( taskProblem.getAutoCorrectContext() != null && taskProblem.autoCorrectContext.length() > 0 )
        {
            sb.append( "<a href='autoCorrect'>Correct this problem automatically</a><br/><br/>" );
        }

        if( taskProblem.html != null && taskProblem.html.length() > 0 )
        {
            sb.append( "<a href='html'>See documentation for how to correct this problem.</a><br/><br/>" );
        }

        if( taskProblem.ticket != null && taskProblem.ticket.length() > 0 )
        {
            sb.append( "<b>Tickets:</b> " + getLinkTags( taskProblem.ticket ) + "<br/><br/>" );
        }

        // sb.append( "<b>More details:</b><br/>" );
        // sb.append( "\t" + "<a href='" + taskProblem.url + "'>See full Breaking Changes document</a><br/>" );

        sb.append( "</p></form>" );

        return sb.toString();
    }

    CommonActionProvider getCommonActionProvider( ISelection selection )
    {
        final INavigatorContentService contentService =
            getCommonViewer().getCommonNavigator().getNavigatorContentService();
        final ActionContext context = new ActionContext( selection );
        final CommonActionProviderDescriptor[] providerDescriptors =
            CommonActionDescriptorManager.getInstance().findRelevantActionDescriptors( contentService, context );
        final NavigatorActionService navigatorActionService =
            getCommonViewer().getCommonNavigator().getNavigatorActionService();

        return navigatorActionService.getActionProviderInstance( providerDescriptors[0] );
    }

    private String getLinkTags( String ticketNumbers )
    {
        String[] ticketNumberArray = ticketNumbers.split( "," );

        StringBuilder sb = new StringBuilder();

        for( int i = 0; i < ticketNumberArray.length; i++ )
        {
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

                    IWebBrowser browser = browserSupport.createBrowser(
                        IWorkbenchBrowserSupport.AS_EXTERNAL | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, "",
                        null );

                    browser.openURL( new URL( url ) );
                }
                catch( Exception e )
                {
                    ProjectUI.logError( "error opening browser", e );
                }
            }
        });
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
