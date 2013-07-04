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
package com.liferay.ide.debug.ui;

import com.liferay.ide.debug.core.LiferayDebugCore;
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
 */
public class DebugPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    private ScopedPreferenceStore prefStore;

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

        StringFieldEditor passwordEditor =
            new StringFieldEditor( LiferayDebugCore.PREF_FM_DEBUG_PASSWORD, "Password:", composite ); //$NON-NLS-1$

        passwordEditor.setPreferenceStore( getPreferenceStore() );
        addField( passwordEditor );

        IntegerFieldEditor portEditor =
            new IntegerFieldEditor( LiferayDebugCore.PREF_FM_DEBUG_PORT, "Port:", composite ); //$NON-NLS-1$

        portEditor.setPreferenceStore( getPreferenceStore() );
        portEditor.setErrorMessage( "Port value must be an integer." ); //$NON-NLS-1$
        portEditor.setValidateStrategy( StringFieldEditor.VALIDATE_ON_KEY_STROKE );
        addField( portEditor );
    }

    @Override
    public IPreferenceStore getPreferenceStore()
    {
        if( prefStore == null )
        {
            prefStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, LiferayDebugCore.PLUGIN_ID );
        }

        return prefStore;
    }

    public void init( IWorkbench workbench )
    {
    }

    @Override
    protected void performDefaults()
    {
        IEclipsePreferences prefs = LiferayDebugCore.getPrefs();
        prefs.remove( LiferayDebugCore.PREF_FM_DEBUG_PASSWORD );
        prefs.remove( LiferayDebugCore.PREF_FM_DEBUG_PORT );

        try
        {
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            LiferayDebugUI.logError( e );
        }

        super.performDefaults();
    }
}
