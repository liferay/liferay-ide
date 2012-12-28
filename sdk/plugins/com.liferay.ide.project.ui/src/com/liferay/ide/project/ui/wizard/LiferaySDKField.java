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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.sdk.pref.SDKsPreferencePage;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelSynchHelper;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferaySDKField
{
    protected IDataModel model;
    protected String propertyName;
    protected SelectionAdapter selectionAdapter;
    protected DataModelSynchHelper synchHelper;

    public LiferaySDKField(
        Composite parent, IDataModel model, SelectionAdapter selectionAdapter, String fieldPropertyName,
        DataModelSynchHelper synchHelper )
    {
        this( parent, model, selectionAdapter, fieldPropertyName, synchHelper, Msgs.liferayPluginsSDK );
    }

    public LiferaySDKField(
        Composite parent, IDataModel model, SelectionAdapter selectionAdapter, String fieldPropertyName,
        DataModelSynchHelper synchHelper, String labelName )
    {
        this.model = model;
        this.propertyName = fieldPropertyName;
        this.selectionAdapter = selectionAdapter;
        this.synchHelper = synchHelper;

        SWTUtil.createLabel( parent, labelName, 1 );

        Combo sdkCombo = new Combo( parent, SWT.DROP_DOWN | SWT.READ_ONLY );
        sdkCombo.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 1, 1 ) );
        sdkCombo.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                LiferaySDKField.this.selectionAdapter.widgetSelected( e );
                LiferaySDKField.this.synchHelper.synchAllUIWithModel();
            }
        } );

        Link configureSDKsLink = new Link( parent, SWT.UNDERLINE_LINK );
        // Button configureSDKsLink = new Button(group, SWT.PUSH);
        configureSDKsLink.setText( NLS.bind( "<a href=\"#\">{0}</a>", Msgs.configure ) ); //$NON-NLS-1$
        // configureSDKsLink.setText("Configure ...");
        configureSDKsLink.setLayoutData( new GridData( SWT.LEFT, SWT.TOP, false, false ) );
        configureSDKsLink.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                configureSDKsLinkSelected( e );
                LiferaySDKField.this.selectionAdapter.widgetSelected( e );
            }
        } );
        synchHelper.synchCombo( sdkCombo, fieldPropertyName, new Control[] { configureSDKsLink } );
    }

    protected void configureSDKsLinkSelected( SelectionEvent e )
    {
        int retval =
            PreferencesUtil.createPreferenceDialogOn(
                e.display.getActiveShell(), SDKsPreferencePage.ID, new String[] { SDKsPreferencePage.ID }, null ).open();

        if( retval == Window.OK )
        {
            model.notifyPropertyChange( propertyName, IDataModel.VALID_VALUES_CHG );

            selectionAdapter.widgetSelected( e );
        }
    }

    private static class Msgs extends NLS
    {
        public static String configure;
        public static String liferayPluginsSDK;

        static
        {
            initializeMessages( LiferaySDKField.class.getName(), Msgs.class );
        }
    }
}
