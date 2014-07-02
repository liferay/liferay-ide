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
package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.ADTCore;
import com.liferay.ide.adt.ui.ADTUI;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;


/**
 * @author Gregory Amerson
 */
public class MobileSDKPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public class MyFileFieldEditor extends FileFieldEditor
    {

        public MyFileFieldEditor( String s1, String s2, boolean b, Composite d )
        {
            super( s1, s2, b, d );
        }

        @Override
        public Button getChangeControl( Composite parent )
        {
            return super.getChangeControl( parent );
        }
    }

    public static final String ID = "com.liferay.ide.adt.ui.MobileSDKPreferencePage";

    private ScopedPreferenceStore prefStore;
    private MyFileFieldEditor fileEditor;
    private RadioGroupFieldEditor radioGroupEditor;
    private Composite fileEditorParent;

    public MobileSDKPreferencePage()
    {
        super( GRID );
    }

    @Override
    protected void createFieldEditors()
    {
        final String[][] labelAndValues =
        {
            { "Default sample project template", ADTCore.VALUE_USE_EMBEDDED_TEMPLATE },
            { "Custom sample project template", ADTCore.VALUE_USE_CUSTOM_TEMPLATE }
        };

        Composite c = SWTUtil.createComposite( getFieldEditorParent(), 1, 1, SWT.FILL );
        c.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );

        radioGroupEditor =
            new RadioGroupFieldEditor(
                ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION, "Liferay Android Sample Project Template", 1,
                labelAndValues, c, true );

        radioGroupEditor.fillIntoGrid( c, 1 );

        fileEditorParent = SWTUtil.createComposite( radioGroupEditor.getRadioBoxControl( c ), 3, 1, GridData.FILL_HORIZONTAL );

        fileEditor = new MyFileFieldEditor( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, "Location:", true, fileEditorParent );

        fileEditor.getLabelControl( fileEditorParent ).setEnabled( false );
        ((GridData)fileEditor.getTextControl( fileEditorParent ).getLayoutData()).widthHint = 250;
        fileEditor.getTextControl( fileEditorParent ).setEnabled( false );
        fileEditor.getChangeControl( fileEditorParent ).setEnabled( false );

        addField( radioGroupEditor );

        addField( fileEditor );

        fileEditor.setEnabled(
            ADTCore.VALUE_USE_CUSTOM_TEMPLATE.equals( getPreferenceStore().getString(
                ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION ) ), fileEditorParent );
    }

    @Override
    public void propertyChange( PropertyChangeEvent event )
    {
        super.propertyChange( event );

        if( event.getSource().equals( radioGroupEditor ) )
        {
            final boolean e = ADTCore.VALUE_USE_CUSTOM_TEMPLATE.equals( event.getNewValue() );
            fileEditor.setEnabled( e, fileEditorParent );

            if( ADTCore.VALUE_USE_EMBEDDED_TEMPLATE.equals( event.getNewValue() ) )
            {
                fileEditor.setStringValue( getPreferenceStore().getString( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION ) );
            }
        }
    }

    @Override
    public IPreferenceStore getPreferenceStore()
    {
        if( prefStore == null )
        {
            prefStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, ADTCore.PLUGIN_ID );
        }

        return prefStore;
    }

    public void init( IWorkbench workbench )
    {
    }

    @Override
    protected void performDefaults()
    {
        final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ADTCore.PLUGIN_ID );
        prefs.remove( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION );
        prefs.remove( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION );

        try
        {
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            ADTUI.logError( "Unable to restore defaults", e );
        }

        super.performDefaults();

        fileEditor.setEnabled(
            ADTCore.VALUE_USE_CUSTOM_TEMPLATE.equals( getPreferenceStore().getString(
                ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION ) ), fileEditorParent );
    }
}
