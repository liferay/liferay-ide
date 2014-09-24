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

package com.liferay.ide.maven.ui.pref;

import com.liferay.ide.maven.core.LiferayMavenCore;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Simon Jiang
 */
public class ProjectMavenPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    public static final String ID = "com.liferay.ide.maven.ui.projectMavenPreferencePage";

    private ScopedPreferenceStore prefStore;
    private BooleanFieldEditor disableCustomJspValidation;

    public ProjectMavenPreferencePage()
    {
        super( GRID );
    }

    @Override
    protected void createFieldEditors()
    {
        disableCustomJspValidation =
            new BooleanFieldEditor(
                LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION, "Disable custom jsp validation checking",
                getFieldEditorParent() );

        disableCustomJspValidation.fillIntoGrid( getFieldEditorParent(), 1 );

        addField( disableCustomJspValidation );
    }

    @Override
    public IPreferenceStore getPreferenceStore()
    {
        if( prefStore == null )
        {
            prefStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, LiferayMavenCore.PLUGIN_ID );
        }

        return prefStore;
    }

    public void init( IWorkbench workbench )
    {
        noDefaultAndApplyButton();
    }

}
