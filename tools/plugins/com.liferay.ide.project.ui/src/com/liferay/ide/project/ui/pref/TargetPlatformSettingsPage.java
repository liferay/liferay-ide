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

import java.io.IOException;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.liferay.ide.project.core.ITargetPlatformConstant;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.TargetPlatformUtil;

/**
 * @author Lovett Li
 */
public class TargetPlatformSettingsPage extends PreferencePage implements IWorkbenchPreferencePage
{

    public static final String PROJECT_UI_TARGETPLATFORM_PAGE_ID =
        "com.liferay.ide.project.ui.targetPlatformSettingsPage";
    private ComboViewer targetPlatFormVersion;
    private ScopedPreferenceStore preferenceStore;

    public TargetPlatformSettingsPage()
    {
        super();
        preferenceStore = new ScopedPreferenceStore( InstanceScope.INSTANCE, ProjectCore.PLUGIN_ID );
    }

    @Override
    public void init( IWorkbench workbench )
    {
    }

    private void initvaules()
    {
        IPreferenceStore store = getPreStore();
        String version;

        if( store != null )
        {
            version = store.getString( ITargetPlatformConstant.CURRENT_TARGETFORM_VERSION ).replace( "[", "" ).replace(
                "]", "" );

            if( version == null || version.equals( "" ) )
            {
                version = ITargetPlatformConstant.DEFAULT_TARGETFORM_VERSION;
            }
        }
        else
        {
            version = ITargetPlatformConstant.DEFAULT_TARGETFORM_VERSION;
        }

        final ISelection selection = new StructuredSelection( version );
        targetPlatFormVersion.setSelection( selection );
    }

    @Override
    protected Control createContents( Composite parent )
    {
        Composite comp = new Composite( parent, SWT.NONE );

        GridLayout layout = new GridLayout( 2, false );
        layout.horizontalSpacing = 10;
        comp.setLayout( layout );

        new Label( comp, SWT.NONE ).setText( "Liferay Target Platform Version:" );

        targetPlatFormVersion = new ComboViewer( comp, SWT.READ_ONLY );
        targetPlatFormVersion.setLabelProvider( new LabelProvider()
        {

            @Override
            public String getText( Object element )
            {
                return element.toString();
            }
        } );
        targetPlatFormVersion.setContentProvider( new ArrayContentProvider() );

        try
        {
            targetPlatFormVersion.setInput( TargetPlatformUtil.getAllTargetPlatfromVersions() );
        }
        catch( IOException e )
        {
        }

        initvaules();

        return comp;
    }

    @Override
    public boolean performOk()
    {
        boolean result = super.performOk();
        storeValues();

        return result;
    }

    private void storeValues()
    {
        preferenceStore.setValue(
            ITargetPlatformConstant.CURRENT_TARGETFORM_VERSION, targetPlatFormVersion.getSelection().toString() );
    }

    private IPreferenceStore getPreStore()
    {
        return preferenceStore;
    }

}
