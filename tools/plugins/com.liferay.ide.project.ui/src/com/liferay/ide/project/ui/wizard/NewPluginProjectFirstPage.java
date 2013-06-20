/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.ui.SDKsPreferencePage;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectFirstPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsSelectionDialog;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class NewPluginProjectFirstPage extends WebProjectFirstPage implements IPluginProjectDataModelProperties
{
    protected Button extType;
    protected Button hookType;
    protected Button layoutTemplateType;
    // private DataModelSynchHelper modelHelper;
    protected Button portletType;
    protected boolean shouldValidatePage = true;
    protected Button themeType;

    public NewPluginProjectFirstPage( NewPluginProjectWizard wizard, IDataModel model, String pageName )
    {
        super( model, pageName );

        this.setImageDescriptor( wizard.getDefaultPageImageDescriptor() );
        this.setTitle( Msgs.liferayPluginProject );
        this.setDescription( Msgs.createNewPluginProject );

        primaryProjectFacet = IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET;

    }

    @Override
    public boolean canFlipToNextPage()
    {
        boolean canFlip = super.canFlipToNextPage();

        if( canFlip )
        {
            // Portlet and theme modes need a 2nd page
            return ( getModel().getBooleanProperty( PLUGIN_TYPE_PORTLET ) ) || ( getModel().getBooleanProperty( PLUGIN_TYPE_THEME ) );
        }

        return canFlip;
    }

    public void setShouldValidatePage( boolean shouldValidate )
    {
        this.shouldValidatePage = shouldValidate;
    }

    protected void configureSDKsLinkSelected( SelectionEvent e )
    {
        // boolean noSDKs = SDKManager.getInstance().getSDKs().length == 0;

        int retval =
            PreferencesUtil.createPreferenceDialogOn(
                this.getShell(), SDKsPreferencePage.ID, new String[] { SDKsPreferencePage.ID }, null ).open();

        if( retval == Window.OK )
        {
            getModel().notifyPropertyChange( LIFERAY_SDK_NAME, IDataModel.VALID_VALUES_CHG );
        }
    }

    protected Group createDefaultGroup( Composite parent, String text, int columns )
    {
        Group group = new Group( parent, SWT.NONE );
        group.setText( text );
        group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        GridLayout gl = new GridLayout( columns, false );

        group.setLayout( gl );

        return group;
    }

    protected void createImportProjectLink( Composite parent )
    {
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.END;
        gd.horizontalIndent = 8;
        gd.verticalIndent = 8;

        Link link = new Link( parent, SWT.UNDERLINE_LINK );
        link.setText( NLS.bind( "<a href=\"#\">{0}</a>", Msgs.createNewProjectLink ) ); //$NON-NLS-1$
        link.setLayoutData( gd );
        link.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                getWizard().getContainer().getShell().close();

                WizardDialog dialog = new WizardDialog( getShell(), new NewProjectFromSourceWizard() );
                dialog.open();
            }

        } );
    }

    protected void createLiferayRuntimeGroup( Composite parent )
    {
        Group group = new Group( parent, SWT.NONE );
        group.setText( Msgs.configuration );
        group.setLayoutData( gdhfill() );
        group.setLayout( new GridLayout( 3, false ) );

        getModel().addListener( this );

        SelectionAdapter selectionAdapter = new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                if( SDKManager.getInstance().getDefaultSDK() != null &&
                    getModel().getBooleanProperty( LIFERAY_USE_SDK_LOCATION ) )
                {
                    // toggling the location property will get the location field to
                    // update
                    getModel().setBooleanProperty( LIFERAY_USE_SDK_LOCATION, false );
                    getModel().setBooleanProperty( LIFERAY_USE_SDK_LOCATION, true );
                }

                validatePage( true );
            }
        };

        new LiferaySDKField( group, getModel(), selectionAdapter, LIFERAY_SDK_NAME, this.synchHelper );

        // SWTUtil.createLabel(group, "Liferay Plugins SDK", 1);
        //
        // Combo sdkCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
        // sdkCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        //
        // Link configureSDKsLink = new Link(group, SWT.UNDERLINE_LINK);
        // configureSDKsLink.setText("<a href=\"#\">Configure</a>");
        // configureSDKsLink.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        // configureSDKsLink.addSelectionListener(selectionAdapter);
        // this.synchHelper.synchCombo(sdkCombo, LIFERAY_SDK_NAME, new Control[] {
        // configureSDKsLink
        // });

        SWTUtil.createLabel( group, Msgs.liferayPortalRuntime, 1 );
        serverTargetCombo = new Combo( group, SWT.BORDER | SWT.READ_ONLY );
        serverTargetCombo.setLayoutData( gdhfill() );

        Button newServerTargetButton = new Button( group, SWT.PUSH );
        newServerTargetButton.setText( Msgs.newButton );
        newServerTargetButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                final DataModelPropertyDescriptor[] preAdditionDescriptors =
                    model.getValidPropertyDescriptors( FACET_RUNTIME );

                boolean isOK = ServerUIUtil.showNewRuntimeWizard( getShell(), getModuleTypeID(), null, "com.liferay." ); //$NON-NLS-1$

                if( isOK )
                {
                    DataModelPropertyDescriptor[] postAdditionDescriptors =
                        model.getValidPropertyDescriptors( FACET_RUNTIME );

                    Object[] preAddition = new Object[preAdditionDescriptors.length];

                    for( int i = 0; i < preAddition.length; i++ )
                    {
                        preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
                    }

                    Object[] postAddition = new Object[postAdditionDescriptors.length];

                    for( int i = 0; i < postAddition.length; i++ )
                    {
                        postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
                    }

                    Object newAddition = CoreUtil.getNewObject( preAddition, postAddition );

                    if( newAddition != null ) // can this ever be null?
                        model.setProperty( FACET_RUNTIME, newAddition );
                }
            }
        } );

        Control[] deps = new Control[] { newServerTargetButton };
        synchHelper.synchCombo( serverTargetCombo, FACET_RUNTIME, deps );
        if( serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0 )
        {
            serverTargetCombo.select( 0 );
        }

        SWTUtil.createLabel( group, StringPool.EMPTY, 1 );

        Link facetsLink = new Link( group, SWT.UNDERLINE_LINK );
        GridData gd = new GridData( SWT.DEFAULT, SWT.DEFAULT, false, false, 1, 1 );
        facetsLink.setLayoutData( gd );
        facetsLink.setText( NLS.bind( "<a href=\"#\">{0}</a>", Msgs.advancedProjectConfigurationLink ) ); //$NON-NLS-1$
        facetsLink.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                FacetsSelectionDialog.openDialog(
                    getShell(), (IFacetedProjectWorkingCopy) getModel().getProperty( FACETED_PROJECT_WORKING_COPY ) );
            }
        } );
        SWTUtil.createLabel( group, StringPool.EMPTY, 1 );
    }

    protected void createPluginTypeGroup( Composite parent )
    {
        Group group = SWTUtil.createGroup( parent, Msgs.pluginType, 2 );
        group.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );

        portletType =
            SWTUtil.createRadioButton(
                group, Msgs.portlet, getPluginImageDescriptor( "/icons/e16/portlet.png" ).createImage(), false, 1 ); //$NON-NLS-1$
        Label l = SWTUtil.createLabel( group, SWT.WRAP, Msgs.createWebApplication, 1 );
        l.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        this.synchHelper.synchCheckbox( portletType, PLUGIN_TYPE_PORTLET, null );

        hookType =
            SWTUtil.createRadioButton(
                group, Msgs.hook, getPluginImageDescriptor( "/icons/e16/hook.png" ).createImage(), false, 1 ); //$NON-NLS-1$
        l =
            SWTUtil.createLabel( group, SWT.WRAP, Msgs.overrideLiferayDefaultBehavior, 1 );
        l.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        this.synchHelper.synchCheckbox( hookType, PLUGIN_TYPE_HOOK, null );

        extType =
            SWTUtil.createRadioButton(
                group, Msgs.ext, getPluginImageDescriptor( "/icons/e16/ext.png" ).createImage(), false, 1 ); //$NON-NLS-1$
        l = SWTUtil.createLabel( group, SWT.WRAP, Msgs.lightWeightExtensionEnvironment, 1 );
        l.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        this.synchHelper.synchCheckbox( extType, PLUGIN_TYPE_EXT, null );

        layoutTemplateType =
            SWTUtil.createRadioButton(
                group, Msgs.layout, getPluginImageDescriptor( "/icons/e16/layout.png" ).createImage(), false, 1 ); //$NON-NLS-1$
        l = SWTUtil.createLabel( group, SWT.WRAP, Msgs.createNewCustomLayout, 1 );
        l.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        this.synchHelper.synchCheckbox( layoutTemplateType, PLUGIN_TYPE_LAYOUTTPL, null );

        themeType =
            SWTUtil.createRadioButton(
                group, Msgs.theme, getPluginImageDescriptor( "/icons/e16/theme.png" ).createImage(), false, 1 ); //$NON-NLS-1$
        l = SWTUtil.createLabel( group, SWT.WRAP, Msgs.buildCustomLookFeel, 1 );
        l.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
        this.synchHelper.synchCheckbox( themeType, PLUGIN_TYPE_THEME, null );
    }

    protected void createProjectGroup( Composite parent )
    {
        IDataModel nestedProjectDM = model.getNestedModel( NESTED_PROJECT_DM );

        nestedProjectDM.addListener( this );

        projectNameGroup = new NewPluginProjectGroup( parent, getModel(), nestedProjectDM );
    }

    protected void createSDKGroup( Composite parent )
    {
        Group group = createDefaultGroup( parent, Msgs.liferayPluginSDK, 2 );
        ( (GridData) group.getLayoutData() ).grabExcessVerticalSpace = false;

        // Composite labelContainer = new Composite(group, SWT.NONE);

        // GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);

        // labelContainer.setLayoutData(gd);
        GridLayout gl = new GridLayout( 1, false );
        gl.marginHeight = 3;
        gl.marginWidth = 0;

        // labelContainer.setLayout(gl);
        // Label label = SWTUtil.createLabel(labelContainer,
        // "Use Liferay SDK: ", 1);
        // label.setLayoutData(gd);

        // IDataModel nestedProjectModel =
        // getModel().getNestedModel(NESTED_PROJECT_DM);
        // nestedProjectModel.addListener(this);
        // modelHelper = new DataModelSynchHelper(nestedProjectModel);
        // Collection props = getModel().getAllProperties();
        // for (Object prop : props) {
        // System.out.println(prop);
        // }

        getModel().addListener( this );

        Combo sdkCombo = new Combo( group, SWT.DROP_DOWN | SWT.READ_ONLY );
        sdkCombo.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );

        Link configureSDKsLink = new Link( group, SWT.UNDERLINE_LINK );
        configureSDKsLink.setText( NLS.bind( "<a href=\"#\">{0}</a>", Msgs.configureSDKsLink ) ); //$NON-NLS-1$
        configureSDKsLink.setLayoutData( new GridData( SWT.RIGHT, SWT.TOP, false, false ) );
        configureSDKsLink.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                configureSDKsLinkSelected( e );
            }
        } );
        this.synchHelper.synchCombo( sdkCombo, LIFERAY_SDK_NAME, new Control[] { configureSDKsLink } );

    }

    protected void createServerTargetComposite( Composite parent )
    {
        Group group = new Group( parent, SWT.NONE );
        ( (GridData) group.getLayoutData() ).grabExcessVerticalSpace = false;
        group.setText( Msgs.liferayRuntime );
        group.setLayoutData( gdhfill() );
        group.setLayout( new GridLayout( 2, false ) );

        serverTargetCombo = new Combo( group, SWT.BORDER | SWT.READ_ONLY );
        serverTargetCombo.setLayoutData( gdhfill() );

        Button newServerTargetButton = new Button( group, SWT.NONE );
        newServerTargetButton.setText( Msgs.newButton );
        newServerTargetButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                final DataModelPropertyDescriptor[] preAdditionDescriptors =
                    model.getValidPropertyDescriptors( FACET_RUNTIME );

                boolean isOK = ServerUIUtil.showNewRuntimeWizard( getShell(), getModuleTypeID(), null, "com.liferay." ); //$NON-NLS-1$

                if( isOK )
                {
                    DataModelPropertyDescriptor[] postAdditionDescriptors =
                        model.getValidPropertyDescriptors( FACET_RUNTIME );

                    Object[] preAddition = new Object[preAdditionDescriptors.length];

                    for( int i = 0; i < preAddition.length; i++ )
                    {
                        preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
                    }

                    Object[] postAddition = new Object[postAdditionDescriptors.length];

                    for( int i = 0; i < postAddition.length; i++ )
                    {
                        postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
                    }

                    Object newAddition = CoreUtil.getNewObject( preAddition, postAddition );

                    if( newAddition != null ) // can this ever be null?
                        model.setProperty( FACET_RUNTIME, newAddition );
                }
            }
        } );

        Control[] deps = new Control[] { newServerTargetButton };
        synchHelper.synchCombo( serverTargetCombo, FACET_RUNTIME, deps );
        if( serverTargetCombo.getSelectionIndex() == -1 && serverTargetCombo.getVisibleItemCount() != 0 )
        {
            serverTargetCombo.select( 0 );
        }
    }

    @Override
    protected Composite createTopLevelComposite( Composite parent )
    {
        Composite top = new Composite( parent, SWT.NONE );
        top.setLayout( new GridLayout() );
        top.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        createProjectGroup( top );
        createImportProjectLink( top );
        createLiferayRuntimeGroup( top );

        // createSDKGroup(top);
        // createServerTargetComposite(top);
        // createPresetPanel(top);

        createPluginTypeGroup( top );

        createWorkingSetGroupPanel( top, new String[] { RESOURCE_WORKING_SET, JAVA_WORKING_SET } );

        updateControls();

        return top;
    }

    protected IDataModel getModel()
    {
        return model;
    }

    protected ImageDescriptor getPluginImageDescriptor( String path )
    {
        return ImageDescriptor.createFromURL( ProjectUIPlugin.getDefault().getBundle().getEntry( path ) );
    }

    @Override
    protected String[] getValidationPropertyNames()
    {
        String[] superProperties = super.getValidationPropertyNames();

        List<String> list = Arrays.asList( superProperties );

        ArrayList<String> arrayList = new ArrayList<String>();

        arrayList.addAll( list );

        arrayList.add( LIFERAY_SDK_NAME );
        arrayList.add( FACET_RUNTIME );
        arrayList.add( PROJECT_NAME );
        arrayList.add( DISPLAY_NAME );
        arrayList.add( FACET_PROJECT_NAME );
        arrayList.add( PLUGIN_TYPE_PORTLET );
        arrayList.add( PLUGIN_TYPE_HOOK );
        arrayList.add( PLUGIN_TYPE_EXT );
        arrayList.add( PLUGIN_TYPE_THEME );
        arrayList.add( PLUGIN_TYPE_LAYOUTTPL );

        return (String[]) arrayList.toArray( new String[0] );
    }

    @Override
    protected void validatePage( boolean showMessage )
    {
        if( shouldValidatePage )
        {
            super.validatePage( showMessage );
        }
    }

    // protected void createSimpleProjectGroup(Composite parent) {
    // IDataModel nestedProjectDM =
    // getDataModel().getNestedModel(NESTED_PROJECT_DM);
    // nestedProjectDM.addListener(this);
    // this.simpleProjectGroup = new NewSimpleProjectGroup(parent,
    // nestedProjectDM);
    // }

    private static class Msgs extends NLS
    {
        public static String advancedProjectConfigurationLink;
        public static String buildCustomLookFeel;
        public static String configuration;
        public static String configureSDKsLink;
        public static String createNewCustomLayout;
        public static String createNewPluginProject;
        public static String createNewProjectLink;
        public static String createWebApplication;
        public static String ext;
        public static String hook;
        public static String layout;
        public static String liferayPluginProject;
        public static String liferayPluginSDK;
        public static String liferayPortalRuntime;
        public static String liferayRuntime;
        public static String lightWeightExtensionEnvironment;
        public static String newButton;
        public static String overrideLiferayDefaultBehavior;
        public static String pluginType;
        public static String portlet;
        public static String theme;

        static
        {
            initializeMessages( NewPluginProjectFirstPage.class.getName(), Msgs.class );
        }
    }
}
