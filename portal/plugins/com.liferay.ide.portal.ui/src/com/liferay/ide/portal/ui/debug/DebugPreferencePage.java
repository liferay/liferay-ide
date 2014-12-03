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
package com.liferay.ide.portal.ui.debug;

import com.liferay.ide.portal.core.PortalCore;
import com.liferay.ide.portal.ui.PortalUI;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Cindy Li
 * @author Tao Tao
 */
public class DebugPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    private class MyIntegerFieldEditor extends IntegerFieldEditor
    {
        public MyIntegerFieldEditor( String name, String labelText, Composite parent )
        {
            super( name, labelText, parent );
        }

        @Override
        public void refreshValidState()
        {
            super.refreshValidState();
        }

        @Override
        protected void valueChanged()
        {
            super.valueChanged();

            if( this.isValid() )
            {
                reValidate();
            }
        }
    }

    private class MyStringFieldEditor extends StringFieldEditor
    {
        public MyStringFieldEditor( String name, String string, Composite composite )
        {
            super( name, string, composite );
        }

        @Override
        public void refreshValidState()
        {
            super.refreshValidState();
        }

        @Override
        protected void valueChanged()
        {
            super.valueChanged();

            if( this.isValid() )
            {
                reValidate();
            }
        }
    }

    private ScopedPreferenceStore prefStore;
    private MyStringFieldEditor passwordEditor;
    private MyIntegerFieldEditor portEditor;

    public DebugPreferencePage()
    {
        super( GRID );
    }

    @Override
    protected void createFieldEditors()
    {
        Group group = SWTUtil.createGroup( getFieldEditorParent(), "FreeMarker Debugger", 1 ); //$NON-NLS-1$
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        group.setLayoutData( gd );
        Composite composite = SWTUtil.createComposite( group, 2, 2, GridData.FILL_HORIZONTAL );

        passwordEditor = new MyStringFieldEditor( PortalCore.PREF_FM_DEBUG_PASSWORD, "Password:", composite );

        passwordEditor.setEmptyStringAllowed( false );
        passwordEditor.setErrorMessage( "Password is invalid." );
        passwordEditor.setValidateStrategy( StringFieldEditor.VALIDATE_ON_KEY_STROKE );
        passwordEditor.setPreferenceStore( getPreferenceStore() );
        addField( passwordEditor );

        portEditor = new MyIntegerFieldEditor( PortalCore.PREF_FM_DEBUG_PORT, "Port:", composite ); //$NON-NLS-1$

        portEditor.setValidRange( 1025, 65535 );
        portEditor.setEmptyStringAllowed( false );
        portEditor.setErrorMessage( "Port value ranges from integer 1025 to 65535." ); //$NON-NLS-1$
        portEditor.setValidateStrategy( StringFieldEditor.VALIDATE_ON_KEY_STROKE );
        portEditor.setPreferenceStore( getPreferenceStore() );
        addField( portEditor );
    }

    @Override
    public IPreferenceStore getPreferenceStore()
    {
        if( prefStore == null )
        {
            prefStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, PortalCore.PLUGIN_ID );
        }

        return prefStore;
    }

    public void init( IWorkbench workbench )
    {
    }

    @Override
    protected void performDefaults()
    {
        IEclipsePreferences prefs = PortalCore.getPrefs();
        prefs.remove( PortalCore.PREF_FM_DEBUG_PASSWORD );
        prefs.remove( PortalCore.PREF_FM_DEBUG_PORT );

        try
        {
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            PortalUI.logError( e );
        }

        super.performDefaults();
    }

    public void reValidate()
    {
        passwordEditor.refreshValidState();

        if( !passwordEditor.isValid() )
        {
            return;
        }

        portEditor.refreshValidState();
    }
}
