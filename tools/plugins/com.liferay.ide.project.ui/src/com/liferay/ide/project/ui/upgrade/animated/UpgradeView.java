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

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.LiferayUpgradePerspectiveFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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

    private LiferayUpgradeDataModel dataModel;

    private static List<Page> currentPageList = new ArrayList<Page>();

    private static List<Page> staticPageList = new ArrayList<Page>();

    private static Composite pagesSwitchControler = null;

    private static Page[] pages = null;

    private static Properties codeUpgradeProperties;
    private static File codeUpgradeFile;

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

                storeProperty( property.name(), property.toString() );
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
        dataModel.getBundleName().attach( new LiferayUpgradeStoreListener() );
        dataModel.getBundleUrl().attach( new LiferayUpgradeStoreListener() );

        final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

        File stateDir = stateLocation.toFile();

        codeUpgradeFile = new File( stateDir, "liferay-code-upgrade.properties" );

        if( !codeUpgradeFile.exists() )
        {
            try
            {
                codeUpgradeFile.createNewFile();
            }
            catch( IOException e1 )
            {
            }
        }

        if( codeUpgradeProperties == null )
        {
            codeUpgradeProperties = new Properties();

            try(InputStream in = new FileInputStream( codeUpgradeFile ))
            {
                codeUpgradeProperties.load( in );
            }
            catch( Exception e )
            {
            }
        }
    }

    public static void resumePages()
    {
        currentPageList.clear();
        currentPageList.addAll( staticPageList );
    }

    public static void removePage( String pageid )
    {
        for( Page page : currentPageList )
        {
            if( page.getPageId().equals( pageid ) )
            {
                currentPageList.remove( page );

                return;
            }
        }
    }

    public static void resetPages()
    {
        pages = currentPageList.toArray( new Page[0] );
    }

    public static void resetStoreProperties()
    {
        codeUpgradeProperties = new Properties();
        codeUpgradeFile = null;
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


    public void storeProperty( Object key, Object value )
    {
        codeUpgradeProperties.setProperty( String.valueOf( key ), String.valueOf( value ) );

        try(OutputStream out = new FileOutputStream( codeUpgradeFile ))
        {
            codeUpgradeProperties.store( out, "" );
        }
        catch( Exception e )
        {
        }
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

        Composite container = this.toolkit.createComposite( parent, 0 );
        this.toolkit.paintBordersFor( container );
        container.setLayout( new FillLayout() );

        this.form = this.toolkit.createForm( container );
        this.form.setImage( getImage() );
        this.toolkit.paintBordersFor( this.form );
        this.toolkit.decorateFormHeading( this.form );
        this.form.setText( "Liferay Upgrade Tools" );

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

        Page deployPage = new DeployPage( pagesSwitchControler, SWT.NONE, dataModel );
        deployPage.setIndex( 9 );
        deployPage.setTitle( "Deploy" );
        deployPage.setNextPage( false );

        staticPageList.clear();

        staticPageList.add( welcomePage );
        staticPageList.add( initConfigureProjectPage );
        staticPageList.add( descriptorsPage );
        staticPageList.add( findBreakingChangesPage );
        staticPageList.add( buildServicePage );
        staticPageList.add( layoutTemplatePage );
        staticPageList.add( customJspPage );
        staticPageList.add( extAndThemePage );
        staticPageList.add( deployPage );

        currentPageList.clear();

        currentPageList.add( welcomePage );
        currentPageList.add( initConfigureProjectPage );
        //currentPageList.addAll( staticPageList );

        boolean hasPortlet = Boolean.parseBoolean( codeUpgradeProperties.getProperty( "HasPortlet", "false" ) );
        boolean hasServiceBuilder = Boolean.parseBoolean( codeUpgradeProperties.getProperty( "HasServiceBuilder", "false" ) );
        boolean hasHook = Boolean.parseBoolean( codeUpgradeProperties.getProperty( "HasHook", "false" ) );
        boolean hasLayout = Boolean.parseBoolean( codeUpgradeProperties.getProperty( "HasLayout", "false" ) );
        boolean hasTheme = Boolean.parseBoolean( codeUpgradeProperties.getProperty( "HasTheme", "false" ) );
        boolean hasExt = Boolean.parseBoolean( codeUpgradeProperties.getProperty( "HasExt", "false" ) );

        if( hasPortlet || hasHook || hasServiceBuilder || hasLayout )
        {
            currentPageList.add( descriptorsPage );
        }

        if( hasPortlet || hasHook || hasServiceBuilder )
        {
            currentPageList.add( findBreakingChangesPage );
        }

        if( hasServiceBuilder )
        {
            currentPageList.add( buildServicePage );
        }

        if( hasLayout )
        {
            currentPageList.add( layoutTemplatePage );
        }

        if( hasHook )
        {
            currentPageList.add( customJspPage );
        }

        if( hasExt || hasTheme )
        {
            currentPageList.add( extAndThemePage );
        }

        if( hasPortlet || hasHook || hasServiceBuilder || hasLayout )
        {
            currentPageList.add( deployPage );
        }

        resetPages();

        final NavigatorControl navigator = new NavigatorControl( composite, SWT.NONE );

        navigator.addPageNavigateListener( gear );
        navigator.addPageActionListener( gear );

        gear.addSelectionChangedListener( navigator );
        gear.addSelectionChangedListener( this );

        GridData navData = new GridData( GridData.FILL_HORIZONTAL );

        navData.grabExcessHorizontalSpace = true;
        navData.widthHint = 400;
        navData.heightHint = 55;

        navigator.setLayoutData( navData );
        navigator.setBackground( backgroundColor );

        setSelectPage( 0 );
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
        final URL url = ProjectUI.getDefault().getBundle().getEntry( "/icons/liferay_logo_32.png" );
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
