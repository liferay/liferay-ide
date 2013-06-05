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

package com.liferay.ide.sdk.ui;

import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Greg Amerson
 */
public class SDKsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public static final String ID = "com.liferay.ide.sdk.preferences.installedSDKs"; //$NON-NLS-1$

    private Composite parent;

    protected InstalledSDKsCompostite installedSDKsComposite;

    private ScopedPreferenceStore prefStore;

    public SDKsPreferencePage()
    {
        setImageDescriptor( SDKUIPlugin.imageDescriptorFromPlugin( SDKUIPlugin.PLUGIN_ID, "/icons/e16/sdk.png" ) ); //$NON-NLS-1$
    }

    @Override
    @SuppressWarnings( "deprecation" )
    public IPreferenceStore getPreferenceStore()
    {
        if( prefStore == null )
        {
            prefStore = new ScopedPreferenceStore( new InstanceScope(), SDKUIPlugin.PREFERENCES_ID );
            String defaultValue =
                new DefaultScope().getNode( SDKUIPlugin.PLUGIN_ID ).get(
                    SDKUIPlugin.PREF_KEY_OVERWRITE_USER_BUILD_FILE, "always" ); //$NON-NLS-1$
            prefStore.setDefault( SDKUIPlugin.PREF_KEY_OVERWRITE_USER_BUILD_FILE, defaultValue );
        }

        return prefStore;
    }

    public void init( IWorkbench workbench )
    {
        noDefaultAndApplyButton();
    }

    @Override
    public boolean isValid()
    {
        return this.installedSDKsComposite != null &&
            ( this.installedSDKsComposite.getSDKs().length == 0 || this.installedSDKsComposite.getDefaultSDK() != null );
    }

    @Override
    public boolean performOk()
    {
        super.performOk();

        if( isValid() )
        {
            SDK[] sdks = installedSDKsComposite.getSDKs();

            if( sdks != null )
            {
                SDKManager.getInstance().saveSDKs( sdks );
            }

            return true;
        }
        else
        {
            setMessage( Msgs.haveOneSDK, IMessageProvider.ERROR );

            return false;
        }
    }

    @Override
    protected Control createContents( Composite parent )
    {
        initializeDialogUnits( parent );

        this.parent = parent;

        // GridLayout layout= new GridLayout();
        // layout.numColumns= 1;
        // layout.marginHeight = 0;
        // layout.marginWidth = 0;
        // parent.setLayout(layout);

        SWTUtil.createWrapLabel( parent, Msgs.addRemoveEditSDKDefinitionsLabel, 1, 100 );

        SWTUtil.createVerticalSpacer( parent, 1, 1 );

        installedSDKsComposite = new InstalledSDKsCompostite( parent, SWT.NONE );
        installedSDKsComposite.setPreferencePage( this );

        GridData data = new GridData( GridData.FILL_BOTH );
        data.horizontalSpan = 1;

        installedSDKsComposite.setLayoutData( data );
        installedSDKsComposite.setSDKs( SDKManager.getInstance().getSDKs() );

        createFieldEditors();

        initialize();
        checkState();

        return parent;
    }

    @Override
    public void applyData( Object data )
    {
        if( "new".equals( data ) ) //$NON-NLS-1$
        {
            this.getShell().getDisplay().asyncExec( new Runnable()
            {
                public void run()
                {
                    installedSDKsComposite.addSDK();
                }
            } );
        }
    }

    @Override
    protected void createFieldEditors()
    {
        FieldEditor edit = new RadioGroupFieldEditor(   SDKUIPlugin.PREF_KEY_OVERWRITE_USER_BUILD_FILE,
                                                        NLS.bind( Msgs.updateProperties, System.getProperty( "user.name" ) ),//$NON-NLS-1$
                                                        3,
                                                        new String[][]
                                                        {
                                                            { Msgs.always, MessageDialogWithToggle.ALWAYS },
                                                            { Msgs.never, MessageDialogWithToggle.NEVER }
                                                        },
                                                        parent,
                                                        true );
        edit.setPreferenceStore( getPreferenceStore() );
        addField( edit );
    }

    @Override
    protected void performDefaults()
    {
        if( installedSDKsComposite != null && !installedSDKsComposite.isDisposed() )
        {
            installedSDKsComposite.setSDKs( SDKManager.getInstance().getSDKs() );
        }

        getPreferenceStore().setValue( MessageDialogWithToggle.PROMPT, SDKUIPlugin.PREF_KEY_OVERWRITE_USER_BUILD_FILE );
    }

    private static class Msgs extends NLS
    {
        public static String addRemoveEditSDKDefinitionsLabel;
        public static String always;
        public static String haveOneSDK;
        public static String never;
//        public static String prompt;
        public static String updateProperties;

        static
        {
            initializeMessages( SDKsPreferencePage.class.getName(), Msgs.class );
        }
    }
}
