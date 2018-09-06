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

package com.liferay.ide.project.ui.pref;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ITargetPlatformConstant;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.TargetPlatformUtil;

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

/**
 * @author Lovett Li
 */
public class TargetPlatformSettingsPage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String PROJECT_UI_TARGETPLATFORM_PAGE_ID =
		"com.liferay.ide.project.ui.targetPlatformSettingsPage";

	public TargetPlatformSettingsPage() {
		_preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, ProjectCore.PLUGIN_ID);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		_storeValues();

		return result;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);

		layout.horizontalSpacing = 10;
		comp.setLayout(layout);

		new Label(comp, SWT.NONE).setText("Liferay Target Platform Version:");

		_targetPlatFormVersion = new ComboViewer(comp, SWT.READ_ONLY);

		_targetPlatFormVersion.setLabelProvider(
			new LabelProvider() {

				@Override
				public String getText(Object element) {
					return element.toString();
				}

			});
		_targetPlatFormVersion.setContentProvider(new ArrayContentProvider());

		try {
			_targetPlatFormVersion.setInput(TargetPlatformUtil.getAllTargetPlatfromVersions());
		}
		catch (IOException ioe) {
		}

		_initvaules();

		return comp;
	}

	private IPreferenceStore _getPreStore() {
		return _preferenceStore;
	}

	private void _initvaules() {
		IPreferenceStore store = _getPreStore();
		String version = ITargetPlatformConstant.DEFAULT_TARGETFORM_VERSION;

		if (store != null) {
			String targetVersion = store.getString(ITargetPlatformConstant.CURRENT_TARGETFORM_VERSION);

			version = targetVersion.replace("[", "");

			version = version.replace("]", "");

			if (CoreUtil.isNullOrEmpty(version)) {
				version = ITargetPlatformConstant.DEFAULT_TARGETFORM_VERSION;
			}
		}

		ISelection selection = new StructuredSelection(version);

		_targetPlatFormVersion.setSelection(selection);
	}

	private void _storeValues() {
		_preferenceStore.setValue(
			ITargetPlatformConstant.CURRENT_TARGETFORM_VERSION,
			StringUtil.toString(_targetPlatFormVersion.getSelection()));

		try {
			_preferenceStore.save();
		}
		catch (IOException ioe) {
			ProjectCore.logError("Can not save target platform preference", ioe);
		}
	}

	private ScopedPreferenceStore _preferenceStore;
	private ComboViewer _targetPlatFormVersion;

}