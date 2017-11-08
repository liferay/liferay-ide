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

package com.liferay.ide.xml.search.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 */
public class XMLSearchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public XMLSearchPreferencePage() {
		super(GRID);
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		if (_prefStore == null) {
			_prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, LiferayXMLSearchUI.PLUGIN_ID);
		}

		return _prefStore;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		Label label = new Label(getFieldEditorParent(), SWT.NONE);

		label.setData(new GridData(SWT.LEFT, SWT.DEFAULT, true, false, 2, 1));
		label.setText("Specify the list of projects to ignore while searching xml files");

		addField(new StringFieldEditor(LiferayXMLSearchUI.PREF_KEY_IGNORE_PROJECTS_LIST, "", getFieldEditorParent()));
	}

	@Override
	protected void performDefaults() {
		IEclipsePreferences prefs = LiferayXMLSearchUI.getInstancePrefs();

		prefs.remove(LiferayXMLSearchUI.PREF_KEY_IGNORE_PROJECTS_LIST);

		try {
			prefs.flush();
		}
		catch (BackingStoreException bse) {
			LiferayXMLSearchUI.logError(bse);
		}

		super.performDefaults();
	}

	private ScopedPreferenceStore _prefStore;

}