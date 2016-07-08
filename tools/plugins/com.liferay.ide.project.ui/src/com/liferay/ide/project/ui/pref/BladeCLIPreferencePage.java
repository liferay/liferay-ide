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

package com.liferay.ide.project.ui.pref;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Andy Wu
 */
public class BladeCLIPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    private final ScopedPreferenceStore prefStore;

    public BladeCLIPreferencePage()
    {
        super( GRID );

        prefStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, ProjectCore.PLUGIN_ID );
    }

    @Override
    protected void createFieldEditors()
    {
        Group group = SWTUtil.createGroup( getFieldEditorParent(), "", 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        group.setLayoutData( gd );
        Composite composite = SWTUtil.createComposite( group, 2, 2, GridData.FILL_HORIZONTAL );

        addField( new StringFieldEditor( BladeCLI.BLADE_CLI_REPO_URL, "Blade CLI Repo URL", composite ) );

        String[][] lableAndValues = new String[3][2];
        lableAndValues[0][0] = "never";
        lableAndValues[0][1] = "-1";
        lableAndValues[1][0] = "always";
        lableAndValues[1][1] = "0";
        lableAndValues[2][0] = "24h";
        lableAndValues[2][1] = "24h";

        addField(
            new RadioGroupFieldEditor( BladeCLI.BLADE_CLI_REPO_UP2DATE_CHECK, "Blade CLI Update Timeout", 3,
                lableAndValues, composite, true ) );
    }

    @Override
    public IPreferenceStore getPreferenceStore()
    {
        return prefStore;
    }

    @Override
    public void init( IWorkbench workbench )
    {
    }

}
