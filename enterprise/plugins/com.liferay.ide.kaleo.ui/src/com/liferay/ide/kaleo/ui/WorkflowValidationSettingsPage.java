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

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.util.WorkflowDefinitionValidator;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.ui.pref.AbstractValidationSettingsPage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class WorkflowValidationSettingsPage extends AbstractValidationSettingsPage {

	public static final int[] ERROR_VALUES = {1, 2, -1};

	public static final String[] ERRORS = {"Error", "Warning", "Ignore"};

	public static final String PORTLET_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID =
		"com.liferay.ide.eclipse.kaleo.ui.propertyPage.workflow.validation";

	public static final String SETTINGS_SECTION_NAME = "WorkflowValidationSeverities";

	public static final String VALIDATION_ID = "com.liferay.ide.eclipse.kaleo.ui.validation.preference";

	public static final Map<Integer, Integer> errorMap = new HashMap<>();

	static {
		errorMap.put(IMarker.SEVERITY_ERROR, 0);
		errorMap.put(IMarker.SEVERITY_WARNING, 1);
		errorMap.put(IMarker.SEVERITY_INFO, 2);
	}

	@Override
	public void dispose() {
		storeSectionExpansionStates(getDialogSettings().addNewSection(SETTINGS_SECTION_NAME));
		super.dispose();
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		storeValues();

		return result;
	}

	protected Combo createCombo(Composite parent, String label, String key) {
		return addComboBox(parent, label, key, ERROR_VALUES, ERRORS, 0);
	}

	@Override
	protected Control createCommonContents(Composite composite) {
		Composite page = new Composite(composite, SWT.NULL);

		GridLayout layout = new GridLayout();

		layout.numColumns = 1;
		page.setLayout(layout);

		_pixelConverter = new PixelConverter(composite);

		Composite content = createValidationSection(page);

		loadPreferences();
		restoreSectionExpansionStates(getDialogSettings().getSection(SETTINGS_SECTION_NAME));

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);

		gridData.heightHint = _pixelConverter.convertHeightInCharsToPixels(20);
		content.setLayoutData(gridData);

		return page;
	}

	protected Composite createValidationSection(Composite parent) {
		GridLayout layout = new GridLayout();

		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		ScrolledPageContent pageContent = new ScrolledPageContent(parent);

		pageContent.setLayoutData(new GridData(GridData.FILL_BOTH));
		pageContent.setExpandHorizontal(true);
		pageContent.setExpandVertical(true);

		Composite body = pageContent.getBody();

		body.setLayout(layout);

		GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);

		gd.horizontalIndent = 0;

		Label description = new Label(body, SWT.NONE);

		description.setText("Select the severity level for the following validation problems:");

		description.setFont(pageContent.getFont());

		description.setLayoutData(gd);

		ExpandableComposite twistie;

		int columns = 3;

		twistie = createTwistie(body, "Workflow Validation", columns);

		twistie.setBackground(parent.getBackground());

		Composite inner = createInnerComposite(parent, twistie, columns);

		inner = createInnerComposite(parent, twistie, columns);

		createCombo(
			inner, "Default workflow validation (logical)", WorkflowDefinitionValidator.WORKFLOW_DEFINITION_VALIDATE);

		return parent;
	}

	protected void enableValues() {
	}

	protected IDialogSettings getDialogSettings() {
		return KaleoUI.getDefault().getDialogSettings();
	}

	@Override
	protected String getPreferenceNodeQualifier() {
		KaleoCore kaleoCore = KaleoCore.getDefault();

		return kaleoCore.getBundle().getSymbolicName();
	}

	@Override
	protected String getPreferencePageID() {
		return VALIDATION_ID;
	}

	@Override
	protected String getProjectSettingsKey() {
		return ProjectCore.USE_PROJECT_SETTINGS;
	}

	@Override
	protected String getPropertyPageID() {
		return PORTLET_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID;
	}

	protected String getQualifier() {
		ProjectCore projectCore = ProjectCore.getDefault();

		return projectCore.getBundle().getSymbolicName();
	}

	protected void initializeValues() {
		/*
		 * for (Map.Entry<String, Combo> entry : combos.entrySet()) { int val =
		 * getPortletCorePreferences().getInt(entry.getKey(), -1);
		 * entry.getValue().select(ERROR_MAP.get(val)); }
		 */
	}

	protected boolean loadPreferences() {
		Runnable runnable = new Runnable() {

			public void run() {
				initializeValues();
				validateValues();
				enableValues();
			}

		};

		BusyIndicator.showWhile(getControl().getDisplay(), runnable);

		return true;
	}

	@Override
	protected void performDefaults() {
		resetSeverities();
		super.performDefaults();
	}

	protected void validateValues() {
		String errorMessage = null;

		setErrorMessage(errorMessage);

		setValid(errorMessage == null);
	}

	private PixelConverter _pixelConverter;

}