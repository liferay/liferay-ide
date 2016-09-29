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

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.LiferayUpgradePerspectiveFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
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

    private Action closeAction;

    private final FormToolkit toolkit = new FormToolkit( Display.getCurrent() );
    private Form form;

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

        dataModel.getSdkLocation().attach( new LiferayUpgradeStoreListener() );
        dataModel.getHasExt().attach( new LiferayUpgradeStoreListener() );
        dataModel.getHasHook().attach( new LiferayUpgradeStoreListener() );
        dataModel.getHasLayout().attach( new LiferayUpgradeStoreListener() );
        dataModel.getHasPortlet().attach( new LiferayUpgradeStoreListener() );
        dataModel.getHasServiceBuilder().attach( new LiferayUpgradeStoreListener() );
        dataModel.getHasTheme().attach( new LiferayUpgradeStoreListener() );
        dataModel.getHasWeb().attach( new LiferayUpgradeStoreListener() );
        dataModel.getBundleUrl().attach( new LiferayUpgradeStoreListener() );
        dataModel.getLiferay70ServerName().attach( new LiferayUpgradeStoreListener() );
        dataModel.getLiferay62ServerLocation().attach( new LiferayUpgradeStoreListener() );

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

        currentPageList.add( targetPage );
    }

    public static void resumePages()
    {
        dataModel.setHasExt( true );
        dataModel.setHasHook( true );
        dataModel.setHasLayout( true );
        dataModel.setHasPortlet( true );
        dataModel.setHasServiceBuilder( true );
        dataModel.setHasTheme( true );
        dataModel.setHasWeb( true );

        currentPageList.clear();
        currentPageList.addAll( staticPageList );
        pages = currentPageList.toArray( new Page[0] );
    }

    public static void resetPages()
    {
        currentPageList.clear();

        addPage( Page.WELCOME_PAGE_ID );
        addPage( Page.INIT_CONFIGURE_PROJECT_PAGE_ID );

        boolean hasPortlet = dataModel.getHasPortlet().content();
        boolean hasServiceBuilder = dataModel.getHasServiceBuilder().content();
        boolean hasHook = dataModel.getHasHook().content();
        boolean hasLayout = dataModel.getHasLayout().content();
        boolean hasTheme = dataModel.getHasTheme().content();
        boolean hasExt = dataModel.getHasExt().content();

        if( hasPortlet || hasHook || hasServiceBuilder || hasLayout )
        {
            addPage( Page.DESCRIPTORS_PAGE_ID );
        }

        if( hasPortlet || hasHook || hasServiceBuilder )
        {
            addPage( Page.FINDBREACKINGCHANGES_PAGE_ID );
        }

        if( hasServiceBuilder )
        {
            addPage( Page.BUILDSERVICE_PAGE_ID );
        }

        if( hasLayout )
        {
            addPage( Page.LAYOUTTEMPLATE_PAGE_ID );
        }

        if( hasHook )
        {
            addPage( Page.CUSTOMJSP_PAGE_ID );
        }

        if( hasExt || hasTheme )
        {
            addPage( Page.EXTANDTHEME_PAGE_ID );
        }

        if( hasPortlet || hasHook || hasServiceBuilder || hasLayout )
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
        createActions();

        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        Composite container = this.toolkit.createComposite( scrolledComposite, 0 );
        this.toolkit.paintBordersFor( container );
        container.setLayout( new FillLayout() );

        this.form = this.toolkit.createForm( container );
        this.form.setImage( getImage() );
        this.toolkit.paintBordersFor( this.form );
        this.toolkit.decorateFormHeading( this.form );
        this.form.setText( "Liferay Code Upgrade" );

        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginWidth = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginHeight = 0;
        this.form.getBody().setLayout( gridLayout );

        this.form.getToolBarManager().add( this.closeAction );
        this.form.getToolBarManager().update( true );

        Composite composite = new Composite( form.getBody(), SWT.NONE );

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

        Page welcomePage = new WelcomePage( pagesSwitchControler, SWT.NONE, dataModel );
        welcomePage.setIndex( 0 );
        welcomePage.setTitle( "Welcome" );
        welcomePage.setBackPage( false );
        welcomePage.addPageNavigateListener( gear );

        Page initConfigureProjectPage = new InitConfigureProjectPage( pagesSwitchControler, SWT.NONE, dataModel );
        initConfigureProjectPage.setIndex( 1 );
        initConfigureProjectPage.setTitle( "Cofigure Projects" );
        initConfigureProjectPage.addPageNavigateListener( gear );
        initConfigureProjectPage.addPageValidationListener( gear );
        initConfigureProjectPage.setNextPage( false );

        Page descriptorsPage = new DescriptorsPage( pagesSwitchControler, SWT.NONE, dataModel );
        descriptorsPage.setIndex( 2 );
        descriptorsPage.setTitle( "Update Descriptor Files" );

        Page findBreakingChangesPage = new FindBreakingChangesPage( pagesSwitchControler, SWT.NONE, dataModel );
        findBreakingChangesPage.setIndex( 3 );
        findBreakingChangesPage.setTitle( "Find Breaking Changes" );

        Page buildServicePage = new BuildServicePage( pagesSwitchControler, SWT.NONE, dataModel );
        buildServicePage.setIndex( 4 );
        buildServicePage.setTitle( "Build Service" );

        Page layoutTemplatePage = new LayoutTemplatePage( pagesSwitchControler, SWT.NONE, dataModel );
        layoutTemplatePage.setIndex( 5 );
        layoutTemplatePage.setTitle( "Layout Template" );

        Page customJspPage = new CustomJspPage( pagesSwitchControler, SWT.NONE, dataModel );
        customJspPage.setIndex( 6 );
        customJspPage.setTitle( "Custom Jsp" );
        customJspPage.addPageValidationListener( gear );

        Page extAndThemePage = new ExtAndThemePage( pagesSwitchControler, SWT.NONE, dataModel );
        extAndThemePage.setIndex( 7 );
        extAndThemePage.setTitle( "Ext and Theme" );

        Page compilePage = new CompilePage( pagesSwitchControler, SWT.NONE, dataModel );
        compilePage.setIndex( 8 );
        compilePage.setTitle( "Compile" );

        Page buildPage = new BuildPage( pagesSwitchControler, SWT.NONE, dataModel );
        buildPage.setIndex( 9 );
        buildPage.setTitle( "Build" );

        Page summaryPage = new SummaryPage( pagesSwitchControler, SWT.NONE, dataModel );
        summaryPage.setIndex( 10 );
        summaryPage.setTitle( "Summary" );
        summaryPage.setNextPage( false );

        staticPageList.clear();

        staticPageList.add( welcomePage );
        staticPageList.add( initConfigureProjectPage );
        staticPageList.add( descriptorsPage );
        staticPageList.add( findBreakingChangesPage );
        staticPageList.add( buildServicePage );
        staticPageList.add( layoutTemplatePage );
        staticPageList.add( customJspPage );
        staticPageList.add( extAndThemePage );
        staticPageList.add( buildPage );
        staticPageList.add( summaryPage );

        resetPages();

        final NavigatorControl navigator = new NavigatorControl( composite, SWT.NONE );

        navigator.addPageNavigateListener( gear );
        navigator.addPageActionListener( gear );

        gear.addSelectionChangedListener( navigator );
        gear.addSelectionChangedListener( this );
        gear.addSelectionChangedListener( (SelectionChangedListener) summaryPage );

        GridData navData = new GridData( GridData.FILL_HORIZONTAL );

        navData.grabExcessHorizontalSpace = true;
        navData.widthHint = 400;
        navData.heightHint = 55;

        navigator.setLayoutData( navData );
        navigator.setBackground( backgroundColor );

        scrolledComposite.setContent(container);
        scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, 670));

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

    private void createActions()
    {
        this.closeAction = new Action( "Close Perspective", getImageDescriptor() )
        {

            public void run()
            {
                closeUpgradePerspective();
            }
        };
    }

    private Image getImage()
    {
        final URL url = ProjectUI.getDefault().getBundle().getEntry( "/icons/e16/liferay_logo_16.png" );
        return ImageDescriptor.createFromURL( url ).createImage();
    }

    private ImageDescriptor getImageDescriptor()
    {
        final URL url = ProjectUI.getDefault().getBundle().getEntry( "/icons/upgrade_back_32.gif" );
        return ImageDescriptor.createFromURL( url );
    }

    private void closeUpgradePerspective()
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if( window != null )
        {
            IWorkbenchPage page = window.getActivePage();
            if( page != null )
            {
                IPerspectiveDescriptor perspective =
                    PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(
                        LiferayUpgradePerspectiveFactory.ID );
                page.closePerspective( perspective, true, false );
            }
        }
    }
}

