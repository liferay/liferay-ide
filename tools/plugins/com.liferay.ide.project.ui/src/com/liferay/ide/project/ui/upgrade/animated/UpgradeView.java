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

import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.migration.MigrationProblemsContainer;
import com.liferay.ide.project.ui.upgrade.CustomJspConverter;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Lovett Li
 */
public class UpgradeView extends ViewPart implements SelectionChangedListener
{
    public static final String ID = "com.liferay.ide.project.ui.upgradeView";

    private static LiferayUpgradeDataModel dataModel;

    private static List<Page> currentPageList = new ArrayList<Page>();

    private static List<Page> staticPageList = new ArrayList<Page>();

    private static Composite pagesSwitchControler = null;

    private static Page[] pages = null;

    private LiferayUpgradeDataModel createUpgradeModel()
    {
        return LiferayUpgradeDataModel.TYPE.instantiate();
    }

    private class LiferayUpgradeStoreListener extends Listener
    {

        @Override
        public void handle( Event event )
        {
            if( event instanceof ValuePropertyContentEvent )
            {
                ValuePropertyContentEvent propertyEvetn = (ValuePropertyContentEvent) event;
                final Property property = propertyEvetn.property();

                UpgradeSettingsUtil.storeProperty( property.name(), property.toString() );
            }
        }
    }

    public UpgradeView()
    {
        super();
        dataModel = createUpgradeModel();

        dataModel.attach( new LiferayUpgradeStoreListener(), "*" );

        UpgradeSettingsUtil.init( dataModel );
    }

    public static void addPage( String pageid )
    {
        Page targetPage = null;

        for( Page page : staticPageList )
        {
            if( page.getPageId().equals( pageid ) )
            {
                targetPage = page;

                break;
            }
        }

        if( targetPage != null )
        {
            currentPageList.add( targetPage );
        }
    }

    public static void resumePages()
    {
        dataModel.setHasMavenProject( true );
        dataModel.setHasExt( true );
        dataModel.setHasHook( true );
        dataModel.setHasLayout( true );
        dataModel.setHasPortlet( true );
        dataModel.setHasServiceBuilder( true );
        dataModel.setHasTheme( true );
        dataModel.setHasWeb( true );
        dataModel.setConvertLiferayWorkspace( false );

        currentPageList.clear();
        currentPageList.addAll( staticPageList );
        pages = currentPageList.toArray( new Page[0] );
    }

    public static void resetPages()
    {
        currentPageList.clear();

        addPage( Page.WELCOME_PAGE_ID );
        addPage( Page.INIT_CONFIGURE_PROJECT_PAGE_ID );

        boolean hasMavenProject = dataModel.getHasMavenProject().content();
        boolean hasPortlet = dataModel.getHasPortlet().content();
        boolean hasServiceBuilder = dataModel.getHasServiceBuilder().content();
        boolean hasHook = dataModel.getHasHook().content();
        boolean hasLayout = dataModel.getHasLayout().content();
/*        boolean hasTheme = dataModel.getHasTheme().content();
        boolean hasExt = dataModel.getHasExt().content();*/
        boolean hasWorkspace = dataModel.getConvertLiferayWorkspace().content();

        if( hasMavenProject )
        {
            addPage( Page.UPGRADE_POM_PAGE_ID );
        }

        if( hasPortlet || hasHook || hasServiceBuilder || hasWorkspace )
        {
            addPage( Page.FINDBREACKINGCHANGES_PAGE_ID );
        }

        if( hasPortlet || hasHook || hasServiceBuilder || hasLayout || hasWorkspace )
        {
            addPage( Page.DESCRIPTORS_PAGE_ID );
        }

        if( hasServiceBuilder || hasWorkspace )
        {
            addPage( Page.BUILDSERVICE_PAGE_ID );
        }

        if( hasLayout || hasWorkspace )
        {
            addPage( Page.LAYOUTTEMPLATE_PAGE_ID );
        }

        if( hasHook || hasWorkspace )
        {
            addPage( Page.CUSTOMJSP_PAGE_ID );
        }

/*        if( hasExt || hasTheme || hasWorkspace )
        {
            addPage( Page.EXTANDTHEME_PAGE_ID );
        }*/

        if( hasPortlet || hasHook || hasServiceBuilder || hasLayout || hasWorkspace )
        {
            addPage( Page.BUILD_PAGE_ID );
            addPage( Page.SUMMARY_PAGE_ID );
        }

        pages = currentPageList.toArray( new Page[0] );

        for( Page page : pages )
        {
            String pageActionName = UpgradeSettingsUtil.getProperty( page.getPageId() );

            if( pageActionName != null )
            {
                PageAction pageAction = page.getSelectedAction( pageActionName );
                page.setSelectedAction( pageAction );
            }
        }
    }

    public static int getPageNumber()
    {
        return pages.length;
    }

    public void setSelectPage( int i )
    {
        StackLayout stackLayout = (StackLayout) pagesSwitchControler.getLayout();

        stackLayout.topControl = pages[i];

        pagesSwitchControler.layout();
    }

    public static Page getPage( int i )
    {
        if( i < 0 || i > pages.length - 1 )
        {
            return null;
        }
        else
        {
            return pages[i];
        }
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T getPage( String pageId, Class<T> clazz )
    {
        for( Page page : pages )
        {
            if( page.getPageId().equals( pageId ) )
            {
                return (T) page;
            }
        }

        return null;
    }

    @Override
    public void createPartControl( Composite parent )
    {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.DOUBLE_BUFFERED | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        Composite container = SWTUtil.createComposite( scrolledComposite, 1, 0, GridData.FILL_BOTH);

        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginWidth = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginHeight = 0;
        container.setLayout( gridLayout );

        Composite composite = new Composite( container, SWT.NONE );

        composite.setLayout( new GridLayout( 1, true ) );

        GridData grData = new GridData( GridData.FILL_BOTH );
        Color backgroundColor = composite.getDisplay().getSystemColor( SWT.COLOR_WIDGET_BACKGROUND );

        grData.grabExcessVerticalSpace = true;
        grData.grabExcessHorizontalSpace = true;
        composite.setLayoutData( grData );
        composite.setBackground( backgroundColor );

        final GearControl gear = new GearControl( composite, SWT.NONE );

        GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
        gridData.grabExcessHorizontalSpace = true;
        gridData.widthHint = 400;
        gridData.heightHint = 150;

        gear.setLayoutData( gridData );
        gear.setBackground( backgroundColor );

        StackLayout stackLayout = new StackLayout();

        pagesSwitchControler = new Composite(composite , SWT.BORDER );
        pagesSwitchControler.setLayout( stackLayout );

        GridData containerData = new GridData( GridData.FILL_BOTH );
        containerData.grabExcessHorizontalSpace = true;
        containerData.grabExcessVerticalSpace = true;
        containerData.grabExcessHorizontalSpace = true;
        pagesSwitchControler.setLayoutData( containerData );

        int pageIndex = 0;

        Page welcomePage = new WelcomePage( pagesSwitchControler, SWT.NONE, dataModel );
        welcomePage.setIndex( pageIndex++ );
        welcomePage.setTitle( "Welcome" );
        welcomePage.setBackPage( false );
        welcomePage.addPageNavigateListener( gear );

        Page initConfigureProjectPage = new InitConfigureProjectPage( pagesSwitchControler, SWT.NONE, dataModel );
        initConfigureProjectPage.setIndex( pageIndex++ );
        initConfigureProjectPage.setTitle( "Configure the Project" );
        initConfigureProjectPage.addPageNavigateListener( gear );
        initConfigureProjectPage.addPageValidationListener( gear );
        initConfigureProjectPage.setNextPage( false );

        Page upgradePomPage = new UpgradePomPage( pagesSwitchControler, SWT.NONE, dataModel );
        upgradePomPage.setIndex( pageIndex++ );
        upgradePomPage.setTitle( "Upgrade POM Files" );
        upgradePomPage.addPageValidationListener( gear );

        Page findBreakingChangesPage = new FindBreakingChangesPage( pagesSwitchControler, SWT.NONE, dataModel );
        findBreakingChangesPage.setIndex( pageIndex++ );
        findBreakingChangesPage.setTitle( "Find Breaking Changes" );

        Page descriptorsPage = new DescriptorsPage( pagesSwitchControler, SWT.NONE, dataModel );
        descriptorsPage.setIndex( pageIndex++ );
        descriptorsPage.setTitle( "Update Descriptor Files" );
        descriptorsPage.addPageValidationListener( gear );

        Page buildServicePage = new BuildServicePage( pagesSwitchControler, SWT.NONE, dataModel );
        buildServicePage.setIndex( pageIndex++ );
        buildServicePage.setTitle( "Build Services" );

        Page layoutTemplatePage = new LayoutTemplatePage( pagesSwitchControler, SWT.NONE, dataModel );
        layoutTemplatePage.setIndex( pageIndex++ );
        layoutTemplatePage.setTitle( "Layout Templates" );
        layoutTemplatePage.addPageValidationListener( gear );

        Page customJspPage = new CustomJspPage( pagesSwitchControler, SWT.NONE, dataModel );
        customJspPage.setIndex( pageIndex++ );
        customJspPage.setTitle( "Custom Jsp" );
        customJspPage.addPageValidationListener( gear );

//        Page extAndThemePage = new ExtAndThemePage( pagesSwitchControler, SWT.NONE, dataModel );
//        extAndThemePage.setIndex( 7 );
//        extAndThemePage.setTitle( "Ext and Theme" );

        Page buildPage = new BuildPage( pagesSwitchControler, SWT.NONE, dataModel );
        buildPage.setIndex( pageIndex++ );
        buildPage.setTitle( "Build" );

        Page summaryPage = new SummaryPage( pagesSwitchControler, SWT.NONE, dataModel );
        summaryPage.setIndex( pageIndex++ );
        summaryPage.setTitle( "Summary" );
        summaryPage.setNextPage( false );
        summaryPage.addPageNavigateListener( gear );

        staticPageList.clear();

        staticPageList.add( welcomePage );
        staticPageList.add( initConfigureProjectPage );
        staticPageList.add( upgradePomPage );
        staticPageList.add( findBreakingChangesPage );
        staticPageList.add( descriptorsPage );
        staticPageList.add( buildServicePage );
        staticPageList.add( layoutTemplatePage );
        staticPageList.add( customJspPage );
//        staticPageList.add( extAndThemePage );
        staticPageList.add( buildPage );
        staticPageList.add( summaryPage );

        resetPages();

        final NavigatorControl navigator = new NavigatorControl( composite, SWT.NONE );

        navigator.addPageNavigateListener( gear );
        navigator.addPageActionListener( gear );

        gear.addSelectionChangedListener( navigator );
        gear.addSelectionChangedListener( this );
        gear.addSelectionChangedListener( (SelectionChangedListener) initConfigureProjectPage );
        gear.addSelectionChangedListener( (SelectionChangedListener) summaryPage );

        GridData navData = new GridData( GridData.FILL_HORIZONTAL );

        navData.grabExcessHorizontalSpace = true;
//        navData.widthHint = 400;
//        navData.heightHint = 55;

        navigator.setLayoutData( navData );
        navigator.setBackground( backgroundColor );

        scrolledComposite.setContent(container);
//        scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, 670));

        setSelectPage( 0 );

        parent.addDisposeListener( new DisposeListener()
        {

            @Override
            public void widgetDisposed( DisposeEvent e )
            {

                int pageNum = getPageNumber();

                for( int i = 0; i < pageNum; i++ )
                {
                    Page page = UpgradeView.getPage( i );

                    String pageId = page.getPageId();
                    PageAction pageAction = page.getSelectedAction();

                    if( pageAction != null )
                    {
                        UpgradeSettingsUtil.storeProperty( pageId, pageAction.getPageActionName() );
                    }
                }
            }
        } );

        final IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();

        final IAction restart = new Action(
            "Restart Upgrade", ImageDescriptor.createFromURL( ProjectUI.getDefault().getBundle().getEntry( "icons/e16/restart.gif" ) ))
        {

            @Override
            public void run()
            {
                restartUpgradeTool();
            }
        };

        final IAction showAllPages = new Action(
            "Show All Pages", ImageDescriptor.createFromURL( ProjectUI.getDefault().getBundle().getEntry( "icons/e16/showall.gif" ) ))
        {
            @Override
            public void run()
            {
                showAllPages();
            }
        };

        mgr.add( restart );
        mgr.add( showAllPages );
    }

    private void showAllPages()
    {
        Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
            UIUtil.getActiveShell(), "Show All Pages",
            "If you fail to import projects, you can skip step 2 by "+
            "doing following steps:\n" +
            "   1.upgrade SDK 6.2 to SDK 7.0 manually\n" +
            "   or use blade cli to create a Liferay workspace for your SDK\n" +
            "   2.import projects you want to upgrade into Eclipse workspace\n" +
            "   3.click \"yes\" to show all the steps");

        if( openNewLiferayProjectWizard )
        {
            UpgradeView.resumePages();

            PageNavigateEvent event = new PageNavigateEvent();

            event.setTargetPage( 2 );

            StackLayout stackLayout = (StackLayout) pagesSwitchControler.getLayout();

            Page currentPage = (Page) stackLayout.topControl;

            for( PageNavigatorListener listener : currentPage.naviListeners )
            {
                listener.onPageNavigate( event );
            }

            InitConfigureProjectPage importPage = UpgradeView.getPage( Page.INIT_CONFIGURE_PROJECT_PAGE_ID,  InitConfigureProjectPage.class );
            importPage.setNextPage( true );

            dataModel.setImportFinished( true );

        }
    }

    private void restartUpgradeTool()
    {
        boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
            UIUtil.getActiveShell(), "Restart code upgrade?",
            "All previous configuration files will be deleted. Do you want to restart the code upgrade tool?" );

        if( openNewLiferayProjectWizard )
        {
            CustomJspConverter.clearConvertResults();

            try
            {
                MigrationProblemsContainer container =
                    UpgradeAssistantSettingsUtil.getObjectFromStore( MigrationProblemsContainer.class );

                if( container != null )
                {
                    UpgradeAssistantSettingsUtil.setObjectToStore( MigrationProblemsContainer.class, null );
                }
            }
            catch( IOException excepiton )
            {
                ProjectUI.logError( excepiton );
            }

            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            UpgradeView view = (UpgradeView) UIUtil.findView( UpgradeView.ID );

            CustomJspConverter.clearConvertResults();

            page.hideView( view );

            UpgradeSettingsUtil.resetStoreProperties();

            try
            {
                page.showView( UpgradeView.ID );
            }
            catch( PartInitException e1 )
            {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void setFocus()
    {
    }

    public interface PageActionListener
    {
        public void onPageAction( PageActionEvent event );
    }

    public interface PageNavigatorListener
    {
        public void onPageNavigate( PageNavigateEvent event );
    }

    public interface PageValidationListener
    {
        public void onValidation( PageValidateEvent event );
    }

    @Override
    public void onSelectionChanged( int targetSelection )
    {
        StackLayout stackLayout = (StackLayout) pagesSwitchControler.getLayout();

        stackLayout.topControl = pages[targetSelection];

        pagesSwitchControler.layout();
    }

}
