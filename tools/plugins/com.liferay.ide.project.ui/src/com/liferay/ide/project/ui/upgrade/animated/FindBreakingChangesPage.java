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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.migration.AutoCorrectAction;
import com.liferay.ide.project.ui.migration.IgnoreAction;
import com.liferay.ide.project.ui.migration.IgnoreAlwaysAction;
import com.liferay.ide.project.ui.migration.MarkDoneAction;
import com.liferay.ide.project.ui.migration.MarkUndoneAction;
import com.liferay.ide.project.ui.migration.MigrationContentProvider;
import com.liferay.ide.project.ui.migration.MigrationLabelProvider;
import com.liferay.ide.project.ui.migration.MigrationProblemsContainer;
import com.liferay.ide.project.ui.migration.MigrationUtil;
import com.liferay.ide.project.ui.migration.MigratorComparator;
import com.liferay.ide.project.ui.migration.ProblemsContainer;
import com.liferay.ide.project.ui.migration.RemoveAction;
import com.liferay.ide.project.ui.migration.RunMigrationToolAction;
import com.liferay.ide.project.ui.pref.MigrationProblemPreferencePage;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Lovett Li
 */
public class FindBreakingChangesPage extends Page implements IDoubleClickListener
{

    public static final String ID = "com.liferay.ide.project.ui.findBreakingChangesPage";

    private static final Image IMAGE_CHECKED =
        ProjectUI.getDefault().getImageRegistry().get( ProjectUI.CHECKED_IMAGE_ID );
    private static final Image IMAGE_UNCHECKED =
        ProjectUI.getDefault().getImageRegistry().get( ProjectUI.UNCHECKED_IMAGE_ID );
    public static boolean showAll = false;
    MigrationContentProvider migrationContentProvider;
    private Browser _browser;

    private TableViewer _problemsViewer;
    private MigratorComparator _comparator;
    private TreeViewer _treeViewer;

    public FindBreakingChangesPage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, FINDBREACKINGCHANGES_PAGE_ID, true );

        final Composite findBreakingchangesContainer = SWTUtil.createComposite( this, 3, 1, GridData.FILL_BOTH, 0, 0 );

        Composite left = SWTUtil.createComposite( findBreakingchangesContainer, 1, 1, GridData.FILL_BOTH, 0, 0 );

        GridData treeData = new GridData( GridData.FILL_BOTH );
        treeData.minimumWidth = 200;
        treeData.heightHint = 150;

        _treeViewer = new TreeViewer( left );

        _treeViewer.getTree().setLayoutData( treeData );

        migrationContentProvider = new MigrationContentProvider();

        _treeViewer.setContentProvider( migrationContentProvider );

        ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
        _treeViewer.setLabelProvider( new DecoratingLabelProvider( new MigrationLabelProvider(), decorator ) );
        _treeViewer.setInput( getInitialInput() );

        MenuManager menuMgr = new MenuManager();
        IAction removeAction = new RemoveAction( _treeViewer );
        menuMgr.add( removeAction );
        Menu menu = menuMgr.createContextMenu( _treeViewer.getTree() );

        _treeViewer.getTree().setMenu( menu );
        _treeViewer.expandAll();

        createTableView( left );

        GridData middleData = new GridData( GridData.FILL_BOTH );
        middleData.minimumWidth = 300;

        _browser = new Browser( findBreakingchangesContainer, SWT.BORDER );
        _browser.setLayout( new FillLayout() );
        _browser.setLayoutData( middleData );

        _treeViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {

            @Override
            public void selectionChanged( SelectionChangedEvent event )
            {
                List<Problem> problems = null;

                if( showAll )
                {
                    problems = MigrationUtil.getProblemsFromTreeNode( event.getSelection() );

                }
                else
                {
                    problems = MigrationUtil.getCurrentProblemsFromTreeNode( event.getSelection() );
                }

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
        } );

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
        } );

        Composite buttonContainer = new Composite( findBreakingchangesContainer, SWT.NONE );
        buttonContainer.setLayout( new GridLayout( 1, false ) );
        buttonContainer.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );

        Button b_findbreakingchanges = new Button( buttonContainer, SWT.NONE );
        b_findbreakingchanges.setText( "Find Breaking Changes" );
        b_findbreakingchanges.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );

        b_findbreakingchanges.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                IViewPart view = UIUtil.findView( UpgradeView.ID );
                new RunMigrationToolAction( "Run Migration Tool", view.getViewSite().getShell() ).run();;
            }
        } );

        Button openAll = new Button( buttonContainer, SWT.NONE );
        openAll.setText( "Expand All" );
        openAll.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );

        openAll.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                _treeViewer.expandAll();
            }
        } );

        Button collapseAll = new Button( buttonContainer, SWT.NONE );
        collapseAll.setText( "Collapse All" );
        collapseAll.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );

        collapseAll.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                _treeViewer.collapseAll();
            }
        } );

        Button openIgnoredList = new Button( buttonContainer, SWT.NONE );
        openIgnoredList.setText( "Open Ignored List" );
        openIgnoredList.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );
        openIgnoredList.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
                    parent.getShell(), MigrationProblemPreferencePage.ID, null, null );
                dialog.open();
            }
        } );
    }

    private void createColumns( final TableViewer _problemsViewer )
    {
        final String[] titles = { "Resolved", "Line", "Problem" };
        final int[] bounds = { 65, 55, 200 };

        TableViewerColumn col = createTableViewerColumn( titles[0], bounds[0], _problemsViewer );
        col.setEditingSupport( new EditingSupport( _problemsViewer )
        {

            @Override
            protected boolean canEdit( Object element )
            {
                return true;
            }

            @Override
            protected CellEditor getCellEditor( Object element )
            {
                return new CheckboxCellEditor( _problemsViewer.getTable() );
            }

            @Override
            protected Object getValue( Object element )
            {
                return ( (Problem) element ).getStatus() == Problem.STATUS_RESOLVED;
            }

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
        } );

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
        } );

        col = createTableViewerColumn( titles[1], bounds[1], _problemsViewer );
        col.setLabelProvider( new ColumnLabelProvider()
        {

            @Override
            public String getText( Object element )
            {
                Problem p = (Problem) element;

                return p.lineNumber > -1 ? ( p.lineNumber + "" ) : "";
            }
        } );

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
            public void update( ViewerCell cell )
            {
                super.update( cell );

                Table table = _problemsViewer.getTable();

                table.getColumn( 2 ).pack();
            }
        } );
    }

    public void createTableView( Composite container )
    {
        GridData gridData = new GridData( GridData.FILL_BOTH );
        gridData.minimumWidth = 300;

        _problemsViewer =
            new TableViewer( container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER );

        _problemsViewer.getTable().setLayoutData( gridData );

        createColumns( _problemsViewer );

        final Table table = _problemsViewer.getTable();
        table.setHeaderVisible( true );

        _problemsViewer.setContentProvider( ArrayContentProvider.getInstance() );
        _problemsViewer.setComparer( null );
        _comparator = new MigratorComparator();
        _problemsViewer.setComparator( _comparator );

        MenuManager menuMgr = new MenuManager();
        IAction markDoneAction = new MarkDoneAction( _problemsViewer );
        IAction markUndoneAction = new MarkUndoneAction( _problemsViewer );
        IAction ignoreAction = new IgnoreAction( _problemsViewer );
        IAction ignoreAlways = new IgnoreAlwaysAction( _problemsViewer );
        IAction autoCorrectAction = new AutoCorrectAction( _problemsViewer );

        menuMgr.add( markDoneAction );
        menuMgr.add( markUndoneAction );
        menuMgr.add( ignoreAction );
        menuMgr.add( autoCorrectAction );
        menuMgr.add( ignoreAlways );
        Menu menu = menuMgr.createContextMenu( table );
        table.setMenu( menu );

        _problemsViewer.addDoubleClickListener( this );
    }

    private TableViewerColumn createTableViewerColumn( String title, int bound, TableViewer viewer )
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

    @Override
    public void doubleClick( DoubleClickEvent event )
    {
        if( event.getSelection() instanceof IStructuredSelection )
        {
            final IStructuredSelection ss = (IStructuredSelection) event.getSelection();

            Object element = ss.getFirstElement();

            if( element instanceof Problem )
            {
                MigrationUtil.openEditor( (Problem) element );
            }
            else if( element instanceof FileProblems )
            {
                MigrationUtil.openEditor( (FileProblems) element );
            }
        }
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

    public TableViewer get_problemsViewer()
    {
        return _problemsViewer;
    }

    public List<ProblemsContainer> getInitialInput()
    {

        List<ProblemsContainer> _problems = null;

        try
        {
            MigrationProblemsContainer container;
            container = UpgradeAssistantSettingsUtil.getObjectFromStore( MigrationProblemsContainer.class );

            if( container != null )
            {
                _problems = new ArrayList<>();
                _problems.add( container );
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        migrationContentProvider.set_problems( _problems );

        return _problems;
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

    public TreeViewer getTreeViewer()
    {
        return _treeViewer;
    }

    private void updateForm( SelectionChangedEvent event )
    {
        final ISelection selection = event.getSelection();

        final Problem problem = MigrationUtil.getProblemFromSelection( selection );

        if( problem != null )
        {
            /*
             * if( Platform.getOS().equals( Platform.OS_LINUX ) ) { _form.setText( generateFormText( problem ), true,
             * false ); } else {
             */
            if( CoreUtil.isNullOrEmpty( problem.html ) )
            {
                _browser.setText( generateFormText( problem ) );
            }
            else
            {
                _browser.setText( problem.html );
            }
            // }
        }
        else
        {
            /*
             * if( Platform.getOS().equals( Platform.OS_LINUX ) ) { _form.setText( "", false, false ); } else {
             */
            _browser.setUrl( "about:blank" );
            // }
        }
    }

    @Override
    public String getDescriptor()
    {
        return "This step will help you to find breaking changes for type of java, jsp, xml and properties files. " +
            "It will not support to find the front-end codes( e.g., javascript, css).\n For service builder, you " +
            "just need to modify the changes on xxxServiceImp.java, xxxFinder.java, xxxModel.java. " +
            "Others will be solved in step \"Build Service\".";
    }

    @Override
    public String getPageTitle()
    {
        return "Find Breaking Changes";
    }

    public boolean getGridLayoutEqualWidth()
    {
        return false;
    }

}
