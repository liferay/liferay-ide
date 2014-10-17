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
import com.liferay.ide.ui.util.SWTUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Simon Jiang
 */
public class ProjectMavenPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

    protected Map<String, FieldEditor> elements;

    public static final String ID = "com.liferay.ide.maven.ui.projectMavenPreferencePage";

    private ScopedPreferenceStore prefStore;

    public ProjectMavenPreferencePage()
    {
        super( GRID );
        elements = new HashMap<String, FieldEditor>();
    }

    private void createBooleanEditior( Composite parent, final String label, final String key )
    {
        final BooleanFieldEditor booleanEditor = new BooleanFieldEditor( key, label, parent );
        booleanEditor.setPreferenceStore( getPreferenceStore() );
        addField( booleanEditor );

        elements.put( key, booleanEditor );
    }

    @Override
    protected void createFieldEditors()
    {
        Composite archetypeComposite = createGroupCompostie( Msgs.mavenDefaultArchetyepGroup );

        createStringEditior( archetypeComposite, Msgs.portletMVCArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_MVC );
        createStringEditior( archetypeComposite, Msgs.portletJSFArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_JSF );
        createStringEditior(
            archetypeComposite, Msgs.portletJSFICEfacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_ICEFACES );
        createStringEditior(
            archetypeComposite, Msgs.portletJSFFacesAlloyArchetype,
            LiferayMavenCore.PREF_ARCHETYPE_GAV_LIFERAY_FACES_ALLOY );
        createStringEditior(
            archetypeComposite, Msgs.portletJSFPrimeFacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_PRIMEFACES );
        createStringEditior(
            archetypeComposite, Msgs.portletJSFRichFacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_RICHFACES );
        createStringEditior(
            archetypeComposite, Msgs.portletVaadinArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_VAADIN );
        createStringEditior( archetypeComposite, Msgs.hookArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_HOOK );
        createStringEditior( archetypeComposite, Msgs.themeArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_THEME );
        createStringEditior(
            archetypeComposite, Msgs.layoutTemplateArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_LAYOUTTPL );
        createStringEditior(
            archetypeComposite, Msgs.serviceBuilderArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_SERVICEBUILDER );
        createStringEditior( archetypeComposite, Msgs.ExtArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_EXT );
        createStringEditior( archetypeComposite, Msgs.WebArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_WEB );

        Composite customJspComposite = createGroupCompostie( Msgs.mavenHookCustomJspGroup );

        createBooleanEditior(
            customJspComposite, Msgs.disableCustomJSPValidation, LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION );
    }

    private Composite createGroupCompostie( final String groupName )
    {
        Group group = SWTUtil.createGroup( getFieldEditorParent(), groupName, 1 ); //$NON-NLS-1$
        GridData gd = new GridData( GridData.FILL, GridData.CENTER, true, false, 2, 1 );
        gd.horizontalIndent = 0;
        group.setLayoutData( gd );
        Composite composite = SWTUtil.createComposite( group, 1, 2, GridData.FILL_HORIZONTAL );

        return composite;
    }

    private void createStringEditior( Composite parent, final String label, final String key )
    {
        final StringFieldEditor stringEditor = new StringFieldEditor( key, label, parent );
        stringEditor.setPreferenceStore( getPreferenceStore() );
        addField( stringEditor );

        elements.put( key, stringEditor );
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
    }

    @Override
    protected void performDefaults()
    {
        resetValues();
        super.performDefaults();
    }

    @Override
    protected void performApply()
    {
        storeValues();
        super.performApply();
    }

    private void resetValues()
    {
        Set<String> keys = elements.keySet();
        for( String key : keys )
        {
            FieldEditor editor = elements.get( key );

            editor.loadDefault();
        }
    }

    private void storeValues()
    {
        Iterator<String> it = elements.keySet().iterator();

        while( it.hasNext() )
        {
            final String key = it.next();

            FieldEditor editor = elements.get( key );
            editor.store();
        }
    }

    private static class Msgs extends NLS
    {

        public static String portletMVCArchetype;
        public static String portletJSFArchetype;
        public static String portletVaadinArchetype;
        public static String portletJSFICEfacesArchetype;
        public static String portletJSFFacesAlloyArchetype;
        public static String portletJSFPrimeFacesArchetype;
        public static String portletJSFRichFacesArchetype;
        public static String hookArchetype;
        public static String serviceBuilderArchetype;
        public static String layoutTemplateArchetype;
        public static String themeArchetype;
        public static String ExtArchetype;
        public static String WebArchetype;
        public static String mavenDefaultArchetyepGroup;
        public static String mavenHookCustomJspGroup;
        public static String disableCustomJSPValidation;

        static
        {
            initializeMessages( ProjectMavenPreferencePage.class.getName(), Msgs.class );
        }
    }
}
