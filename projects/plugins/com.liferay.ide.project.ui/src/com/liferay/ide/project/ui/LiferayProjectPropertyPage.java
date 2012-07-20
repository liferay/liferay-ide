/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui;

import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.pref.SDKsPreferencePage;
import com.liferay.ide.sdk.util.SDKUtil;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

/**
 * @author Greg Amerson
 */
public class LiferayProjectPropertyPage extends PropertyPage
    implements IWorkbenchPropertyPage, IPluginProjectDataModelProperties
{

    public LiferayProjectPropertyPage()
    {
        super();

        setImageDescriptor( ProjectUIPlugin.imageDescriptorFromPlugin(
            ProjectUIPlugin.PLUGIN_ID, "/icons/e16/liferay.png" ) );
    }

    protected void configureSDKsLinkSelected( SelectionEvent e )
    {
        // boolean noSDKs = SDKManager.getAllSDKs().length == 0;

        String[] id = new String[] { SDKsPreferencePage.ID };

        PreferenceDialog dialog =
            PreferencesUtil.createPreferenceDialogOn( this.getShell(), SDKsPreferencePage.ID, id, null );

        int retval = dialog.open();

        if( retval == Window.OK )
        {
            getContainer().updateButtons();
        }

        // if
        // (getModel().getProperty(IPortalPluginProjectDataModelProperties.LIFERAY_SDK_NAME).equals(
        // IPortalPluginProjectDataModelProperties.LIFERAY_SDK_NAME_DEFAULT_VALUE))
        // {
        // no default sdk set, lets set one if it exists
        // SDK sdk = LiferayCore.getDefaultSDK();
        // if (sdk != null) {
        // getModel().setProperty(IPortalPluginProjectDataModelProperties.LIFERAY_SDK_NAME,
        // sdk.getName());
        // sdkCombo.setItems(new String[0]);//refreish items
        // }
        // }
        // modelHelper.synchAllUIWithModel();
        // }
    }

    @Override
    protected Control createContents( Composite parent )
    {
        Composite top = SWTUtil.createTopComposite( parent, 3 );

        createSDKGroup( top );

        return top;
    }

    protected Group createDefaultGroup( Composite parent, String text, int columns )
    {
        GridLayout gl = new GridLayout( columns, false );

        Group group = new Group( parent, SWT.NONE );
        group.setText( text );
        group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        group.setLayout( gl );

        return group;
    }

    protected void createSDKGroup( Composite parent )
    {
        new Label( parent, SWT.LEFT ).setText( "Liferay Plugin SDK:" );

        Text sdkLabel = new Text( parent, SWT.READ_ONLY | SWT.BORDER );

        sdkLabel.setEnabled( false );
        sdkLabel.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );

        SDK sdk = SDKUtil.getSDK( getProject() );

        if( sdk != null )
        {
            sdkLabel.setText( sdk.getName() );
        }

        // labelContainer = new Composite(group, SWT.NONE);
        // labelContainer.setLayout(gl);
        // labelContainer.setLayoutData(gd);

        Link configureSDKsLink = new Link( parent, SWT.UNDERLINE_LINK );

        configureSDKsLink.setText( "<a href=\"#\">Configure SDKs</a>" );
        configureSDKsLink.setLayoutData( new GridData( SWT.RIGHT, SWT.TOP, false, false ) );
        configureSDKsLink.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                configureSDKsLinkSelected( e );
            }

        } );

    }

    protected IFacetedProject getFacetedProject()
    {
        IFacetedProject retval = null;

        IProject project = getProject();

        if( project != null )
        {
            retval = ProjectUtil.getFacetedProject( project );
        }

        return retval;
    }

    protected IProject getProject()
    {
        IAdaptable adaptable = getElement();

        IProject project = (IProject) adaptable.getAdapter( IProject.class );
        return project;
    }

}
