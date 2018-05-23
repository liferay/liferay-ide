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

package com.liferay.ide.kaleo.ui;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Gregory Amerson
 */
public class KaleoUIPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public KaleoUIPreferencePage() {
		super(GRID);

		setPreferenceStore(KaleoUI.getDefault().getPreferenceStore());
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		String[][] labelAndValues = {
			{"Always", MessageDialogWithToggle.ALWAYS}, {"Never", MessageDialogWithToggle.NEVER},
			{"Prompt", MessageDialogWithToggle.PROMPT}
		};

		addField(
			new RadioGroupFieldEditor(
				KaleoUIPreferenceConstants.EDITOR_PERSPECTIVE_SWITCH,
				"Open the Kaleo Designer perspective when opening kaleo workflow files.", 3, labelAndValues,
				getFieldEditorParent(), true));
	}

}