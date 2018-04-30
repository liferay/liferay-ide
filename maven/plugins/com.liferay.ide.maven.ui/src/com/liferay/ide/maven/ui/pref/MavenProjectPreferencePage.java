/**
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
 */

package com.liferay.ide.maven.ui.pref;

import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
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
 * @author Eric Min
 */
public class MavenProjectPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.liferay.ide.maven.ui.mavenProjectPreferencePage";

	public MavenProjectPreferencePage() {
		super(GRID);

		_prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, LiferayMavenCore.PLUGIN_ID);
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return _prefStore;
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		Composite archetypeComposite = _createGroupCompostie(Msgs.mavenDefaultArchetyepGroup);

		_createStringEditior(archetypeComposite, Msgs.portletMVCArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_MVC);
		_createStringEditior(archetypeComposite, Msgs.portletJSFArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_JSF);
		_createStringEditior(
			archetypeComposite, Msgs.portletJSFICEfacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_ICEFACES);
		_createStringEditior(
			archetypeComposite, Msgs.portletJSFFacesAlloyArchetype,
			LiferayMavenCore.PREF_ARCHETYPE_GAV_LIFERAY_FACES_ALLOY);
		_createStringEditior(
			archetypeComposite, Msgs.portletJSFPrimeFacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_PRIMEFACES);
		_createStringEditior(
			archetypeComposite, Msgs.portletJSFRichFacesArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_RICHFACES);
		_createStringEditior(
			archetypeComposite, Msgs.portletSpringMVCArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_SPRING_MVC);
		_createStringEditior(
			archetypeComposite, Msgs.portletVaadinArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_VAADIN);
		_createStringEditior(archetypeComposite, Msgs.hookArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_HOOK);
		_createStringEditior(archetypeComposite, Msgs.themeArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_THEME);
		_createStringEditior(
			archetypeComposite, Msgs.layoutTemplateArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_LAYOUTTPL);
		_createStringEditior(
			archetypeComposite, Msgs.serviceBuilderArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_SERVICEBUILDER);
		_createStringEditior(archetypeComposite, Msgs.extArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_EXT);
		_createStringEditior(archetypeComposite, Msgs.webArchetype, LiferayMavenCore.PREF_ARCHETYPE_GAV_WEB);

		Composite customJspComposite = _createGroupCompostie(Msgs.mavenProjectConfiguratorOptions);

		_createBooleanEditior(
			customJspComposite, Msgs.addPluginTypeSuffix, LiferayMavenCore.PREF_ADD_MAVEN_PLUGIN_SUFFIX);
		_createBooleanEditior(
			customJspComposite, Msgs.disableCustomJSPValidation, LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION);
	}

	private void _createBooleanEditior(Composite parent, String label, String key) {
		BooleanFieldEditor booleanEditor = new BooleanFieldEditor(key, label, parent);

		booleanEditor.setPreferenceStore(getPreferenceStore());
		addField(booleanEditor);
	}

	private Composite _createGroupCompostie(String groupName) {
		Group group = SWTUtil.createGroup(getFieldEditorParent(), groupName, 1);
		GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);

		gd.horizontalIndent = 0;
		group.setLayoutData(gd);

		Composite composite = SWTUtil.createComposite(group, 1, 2, GridData.FILL_HORIZONTAL);

		return composite;
	}

	private void _createStringEditior(Composite parent, String label, String key) {
		StringFieldEditor stringEditor = new StringFieldEditor(key, label, parent);

		stringEditor.setPreferenceStore(getPreferenceStore());
		addField(stringEditor);
	}

	private ScopedPreferenceStore _prefStore;

	private static class Msgs extends NLS {

		public static String addPluginTypeSuffix;
		public static String disableCustomJSPValidation;
		public static String extArchetype;
		public static String hookArchetype;
		public static String layoutTemplateArchetype;
		public static String mavenDefaultArchetyepGroup;
		public static String mavenProjectConfiguratorOptions;
		public static String portletJSFArchetype;
		public static String portletJSFFacesAlloyArchetype;
		public static String portletJSFICEfacesArchetype;
		public static String portletJSFPrimeFacesArchetype;
		public static String portletJSFRichFacesArchetype;
		public static String portletMVCArchetype;
		public static String portletSpringMVCArchetype;
		public static String portletVaadinArchetype;
		public static String serviceBuilderArchetype;
		public static String themeArchetype;
		public static String webArchetype;

		static {
			initializeMessages(MavenProjectPreferencePage.class.getName(), Msgs.class);
		}

	}

}