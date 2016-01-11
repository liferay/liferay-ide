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

import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.internal.navigator.actions.CommonActionDescriptorManager;
import org.eclipse.ui.internal.navigator.actions.CommonActionProviderDescriptor;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.ui.navigator.NavigatorActionService;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Lovett li
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class MigrationView extends CommonNavigator implements IDoubleClickListener
{

    public static final String ID = "com.liferay.ide.project.ui.migrationView";
    private static final Image IMAGE_CHECKED =
        ProjectUI.getDefault().getImageRegistry().get( ProjectUI.CHECKED_IMAGE_ID );
    private static final Image IMAGE_UNCHECKED =
        ProjectUI.getDefault().getImageRegistry().get( ProjectUI.UNCHECKED_IMAGE_ID );

    private Browser _browser;
//    private FormText _form;
    private TableViewer _problemsViewer;
    private MigratorComparator _comparator;

    private void createColumns( final TableViewer _problemsViewer )
    {
        final String[] titles = { "Resolved", "Line", "Problem" };
        final int[] bounds = { 65, 55, 200 };

        TableViewerColumn col = createTableViewerColumn( titles[0], bounds[0], _problemsViewer );
        col.setEditingSupport( new EditingSupport( _problemsViewer )
        {
            @Override
            protected void setValue( Object element, Object value )
            {
                if( value == Boolean.TRUE )
                {
                    new MarkDoneAction().run( (Problem) element, _problemsViewer );
                }
                else
                {
                    new MarkUndoneAction().run( (Problem) element, _problemsViewer );
                }
            }

            @Override
            protected Object getValue( Object element )
            {
                return ( (Problem) element ).getStatus() == Problem.STATUS_RESOLVED;
            }

            @Override
            protected CellEditor getCellEditor( Object element )
            {
                return new CheckboxCellEditor( _problemsViewer.getTable() );
            }

            @Override
            protected boolean canEdit( Object element )
            {
                return true;
            }
        });

        col.setLabelProvider( new ColumnLabelProvider()
        {
            @Override
            public Image getImage( Object element )
            {
                Problem p = (Problem) element;

                if( p.getStatus() == Problem.STATUS_RESOLVED )
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

        col = createTableViewerColumn( titles[1], bounds[1], _problemsViewer );
        col.setLabelProvider( new ColumnLabelProvider()
        {
            @Override
            public String getText( Object element )
            {
                Problem p = (Problem) element;

                return p.lineNumber > -1 ? ( p.lineNumber + "" ) : "";
            }
        });

        col = createTableViewerColumn( titles[2], bounds[2], _problemsViewer );
        col.setLabelProvider( new ColumnLabelProvider()
        {
            @Override
            public String getText( Object element )
            {
                Problem p = (Problem) element;

                return p.title;
            }

            @Override
            public void update(ViewerCell cell)
            {
                super.update(cell);

                Table table = _problemsViewer.getTable();

                table.getColumn(2).pack();
            }
        });
    }

    @Override
    public void createPartControl( Composite parent )
    {
        SashForm viewParent = new SashForm( parent, SWT.HORIZONTAL );

        viewParent.setLayout( new FillLayout( SWT.HORIZONTAL ) );

        super.createPartControl( viewParent );

        SashForm detailParent = new SashForm( viewParent, SWT.VERTICAL );
        viewParent.setWeights( new int[] { 2, 3 } );

        _problemsViewer =
            new TableViewer( detailParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER );

        createColumns( _problemsViewer );

        final Table table = _problemsViewer.getTable();
        table.setHeaderVisible( true );

        _problemsViewer.setContentProvider( ArrayContentProvider.getInstance() );
        _problemsViewer.setComparer(null);
        _comparator = new MigratorComparator();
        _problemsViewer.setComparator(_comparator);

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

        contributeToActionBars();

        _problemsViewer.addDoubleClickListener( this );
        CommonActionProvider ap = getCommonActionProvider( new StructuredSelection( new Problem() ) );

        if( ap instanceof MigrationActionProvider )
        {
            MigrationActionProvider mp = (MigrationActionProvider) ap;

            mp.makeActions( _problemsViewer );
            mp.registerSelectionProvider( _problemsViewer );
        }


        /*if( Platform.getOS().equals( Platform.OS_LINUX ) )
        {
            ScrolledFormText sft = new ScrolledFormText( detailParent, false );
            sft.setExpandVertical( true );

            _form = new FormText( sft, SWT.NONE );
            sft.setFormText( _form );

            _form.addHyperlinkListener( new IHyperlinkListener()
            {
                public void linkExited( org.eclipse.ui.forms.events.HyperlinkEvent e )
                {
                }

                public void linkEntered( org.eclipse.ui.forms.events.HyperlinkEvent e )
                {
                }

                public void linkActivated( org.eclipse.ui.forms.events.HyperlinkEvent e )
                {
                    if( e.data instanceof String )
                    {
                        final String url = (String) e.data;

                        final Problem problem =
                            MigrationUtil.getProblemFromSelection( _problemsViewer.getSelection() );

                        if( "autoCorrect".equals( url ) )
                        {
                            AutoCorrectAction.run( problem, _problemsViewer );
                        }
                        else if( "html".equals( url ) )
                        {
                            displayPopupHtml( problem.title, problem.html );
                        }
                        else if( url.startsWith( "http" ) )
                        {
                            openBrowser( url );
                        }
                    }
                }
            });
        }
        else
        {*/
            _browser = new Browser( detailParent, SWT.BORDER );
        //}

        detailParent.setWeights( new int[] { 2, 3 } );

        getCommonViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                List<Problem> problems = MigrationUtil.getProblemsFromTreeNode( event.getSelection() );

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

    /*private void displayPopupHtml( final String title, final String html )
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
    }*/

    private TableViewerColumn createTableViewerColumn(
        String title, int bound, TableViewer viewer )
    {
        final TableViewerColumn viewerColumn = new TableViewerColumn( viewer, SWT.NONE );
        final TableColumn column = viewerColumn.getColumn();
        column.setText( title );
        column.setWidth( bound );
        column.setResizable( true );
        column.setMoveable( true );
        column.addSelectionListener( getSelectionAdapter( column, viewer.getTable().indexOf( column ) ) );

        return viewerColumn;
    }

    private void contributeToActionBars()
    {
        final IActionBars bars = getViewSite().getActionBars();
        final IToolBarManager manager = bars.getToolBarManager();

        final IAction migrateAction = new RunMigrationToolAction( "Run Migration Tool" , getViewSite().getShell() );
        final IAction expandAllAction = new ExpandAllAction( "Expand All", this );
        manager.add( migrateAction );
        manager.add( expandAllAction );
    }

    @Override
    public void doubleClick( DoubleClickEvent event )
    {
        Problem problem = MigrationUtil.getProblemFromSelection( event.getSelection() );

        if( problem != null )
        {
            MigrationUtil.openEditor( problem );
        }
    }

    private void fillContextMenu( IMenuManager manager, ISelectionProvider provider )
    {
        CommonActionProvider instance = getCommonActionProvider( provider.getSelection() );

        instance.setContext( new ActionContext( provider.getSelection() ) );
        instance.fillContextMenu( manager );
    }

    private String generateFormText( Problem problem )
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "<form><p>" );

        sb.append( "<b>Problem:</b> " + problem.title + "<br/><br/>" );

        sb.append( "<b>Description:</b><br/>" );
        sb.append( "\t" + problem.summary + "<br/><br/>" );

        if( problem.getAutoCorrectContext() != null && problem.autoCorrectContext.length() > 0 )
        {
            sb.append( "<a href='autoCorrect'>Correct this problem automatically</a><br/><br/>" );
        }

        if( problem.html != null && problem.html.length() > 0 )
        {
            sb.append( "<a href='html'>See documentation for how to correct this problem.</a><br/><br/>" );
        }

        if( problem.ticket != null && problem.ticket.length() > 0 )
        {
            sb.append( "<b>Tickets:</b> " + getLinkTags( problem.ticket ) + "<br/><br/>" );
        }

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

    private SelectionAdapter getSelectionAdapter( final TableColumn column, final int index )
    {
        return new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                _comparator.setColumn( index );
                int dir = _comparator.getDirection();
                _problemsViewer.getTable().setSortDirection( dir );
                _problemsViewer.getTable().setSortColumn( column );
                _problemsViewer.refresh();
            }
        };
    }

    /*private void openBrowser( final String url )
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

    private void savePopupState( Shell shell )
    {
        final Point location = shell.getLocation();
        final Point size = shell.getSize();

        MigrationView.this.getMemento().putInteger( "popupLastX", location.x );
        MigrationView.this.getMemento().putInteger( "popupLastY", location.y );
        MigrationView.this.getMemento().putInteger( "popupSizeX", size.x );
        MigrationView.this.getMemento().putInteger( "popupSizeY", size.y );
    }*/

    private void updateForm( SelectionChangedEvent event )
    {
        final ISelection selection = event.getSelection();

        final Problem problem = MigrationUtil.getProblemFromSelection( selection );

        if( problem != null )
        {
        /*if( Platform.getOS().equals( Platform.OS_LINUX ) )
            {
                _form.setText( generateFormText( problem ), true, false );
            }
            else
            {*/
                if( CoreUtil.isNullOrEmpty( problem.html ) )
                {
                    _browser.setText( generateFormText( problem ) );
                }
                else
                {
                    _browser.setText( problem.html );
                }
            //}
        }
        else
        {
           /* if( Platform.getOS().equals( Platform.OS_LINUX ) )
            {
                _form.setText( "", false, false );
            }
            else
            {*/
                _browser.setUrl( "about:blank" );
            //}
        }
    };

    public TableViewer getProblemsViewer()
    {
        return _problemsViewer;
    }

}
