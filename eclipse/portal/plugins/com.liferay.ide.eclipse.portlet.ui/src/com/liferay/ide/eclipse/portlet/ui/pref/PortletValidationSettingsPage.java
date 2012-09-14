/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.ui.pref;

import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.ValidationPreferences;
import com.liferay.ide.eclipse.ui.pref.AbstractValidationSettingsPage;

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

@SuppressWarnings("restriction")
public class PortletValidationSettingsPage extends AbstractValidationSettingsPage {

	public static final String PORTLET_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID =
		"com.liferay.ide.eclipse.portlet.ui.propertyPage.project.validation";

	public static final String VALIDATION_ID = "com.liferay.ide.eclipse.portlet.ui.validation";

	protected static final Map<Integer, Integer> ERROR_MAP = new HashMap<Integer, Integer>();

	protected static final int[] ERROR_VALUES = new int[] {
		1, 2, -1
	};

	protected static final String[] ERRORS = new String[] {
		"Error", "Warning", "Ignore"
	};

	protected static final String SETTINGS_SECTION_NAME = "PortletValidationSeverities";

	static {
		ERROR_MAP.put(IMarker.SEVERITY_ERROR, 0);
		ERROR_MAP.put(IMarker.SEVERITY_WARNING, 1);
		ERROR_MAP.put(IMarker.SEVERITY_INFO, 2);
	}

	protected PixelConverter pixelConverter;

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
		final Composite page = new Composite(composite, SWT.NULL);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		page.setLayout(layout);

		this.pixelConverter = new PixelConverter(composite);

		final Composite content = createValidationSection(page);

		loadPreferences();
		restoreSectionExpansionStates(getDialogSettings().getSection(SETTINGS_SECTION_NAME));

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.heightHint = pixelConverter.convertHeightInCharsToPixels(20);
		content.setLayoutData(gridData);

		return page;
	}

	protected Composite createValidationSection(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		final ScrolledPageContent pageContent = new ScrolledPageContent(parent);
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

		twistie = createTwistie(body, "Portlet XML Descriptor", columns);
		Composite inner = createInnerComposite(parent, twistie, columns);

		createCombo(inner, "Portlet class not found", ValidationPreferences.PORTLET_XML_PORTLET_CLASS_NOT_FOUND);

		createCombo( inner, "Filter class not found", ValidationPreferences.PORTLET_XML_FILTER_CLASS_NOT_FOUND );

		createCombo( inner, "Listener class not found", ValidationPreferences.PORTLET_XML_LISTENER_CLASS_NOT_FOUND );

		createCombo(inner, "Resource bundle not found", ValidationPreferences.PORTLET_XML_RESOURCE_BUNDLE_NOT_FOUND);

		twistie = createTwistie(body, "Liferay Portlet XML Descriptor", columns);
		inner = createInnerComposite(parent, twistie, columns);

		createCombo(inner, "Icon not found", ValidationPreferences.LIFERAY_PORTLET_XML_ICON_NOT_FOUND);
        createCombo(inner, "Entry class not found", ValidationPreferences.LIFERAY_PORTLET_XML_ENTRY_CLASS_NOT_FOUND);
        createCombo(inner, "Entry weight not valid", ValidationPreferences.LIFERAY_PORTLET_XML_ENTRY_WEIGHT_NOT_VALID);
		createCombo(
			inner, "Header portal css not found", ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTAL_CSS_NOT_FOUND);
		createCombo(
			inner, "Header portlet css not found",
			ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTLET_CSS_NOT_FOUND);
		createCombo(
			inner, "Header portal javascript not found",
			ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTAL_JAVASCRIPT_NOT_FOUND);
		createCombo(
			inner, "Header portlet javascript not found",
			ValidationPreferences.LIFERAY_PORTLET_XML_HEADER_PORTLET_JAVASCRIPT_NOT_FOUND);
		createCombo(
			inner, "Footer portal css not found", ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTAL_CSS_NOT_FOUND);
		createCombo(
			inner, "Footer portlet css not found",
			ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTLET_CSS_NOT_FOUND);
		createCombo(
			inner, "Footer portal javascript not found",
			ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTAL_JAVASCRIPT_NOT_FOUND);
		createCombo(
			inner, "Footer portlet javascript not found",
			ValidationPreferences.LIFERAY_PORTLET_XML_FOOTER_PORTLET_JAVASCRIPT_NOT_FOUND);
		createCombo(inner, "Portlet name not found", ValidationPreferences.LIFERAY_PORTLET_XML_PORTLET_NAME_NOT_FOUND);

		twistie = createTwistie(body, "Liferay Display XML Descriptor", columns);
		inner = createInnerComposite(parent, twistie, columns);

		createCombo(inner, "Portlet id not found", ValidationPreferences.LIFERAY_DISPLAY_XML_PORTLET_ID_NOT_FOUND);

		twistie = createTwistie(body, "Liferay Hook XML Descriptor", columns);
		inner = createInnerComposite(parent, twistie, columns);

		createCombo(
			inner, "Portal properties resource not found",
			ValidationPreferences.LIFERAY_HOOK_XML_PORTAL_PROPERTIES_NOT_FOUND);
		createCombo(
			inner, "Language properties resource not found",
			ValidationPreferences.LIFERAY_HOOK_XML_LANGUAGE_PROPERTIES_NOT_FOUND);
		createCombo(
			inner, "Custom jsp directory not found", ValidationPreferences.LIFERAY_HOOK_XML_CUSTOM_JSP_DIR_NOT_FOUND);
		createCombo(inner, "Service type not found", ValidationPreferences.LIFERAY_HOOK_XML_SERVICE_TYPE_NOT_FOUND);
		createCombo(inner, "Service impl not found", ValidationPreferences.LIFERAY_HOOK_XML_SERVICE_IMPL_NOT_FOUND);

		twistie = createTwistie(body, "Liferay Layout Templates Descriptor", columns);
		inner = createInnerComposite(parent, twistie, columns);

		createCombo(
			inner, "Template path resource not found",
			ValidationPreferences.LIFERAY_LAYOUTTPL_XML_TEMPLATE_PATH_NOT_FOUND);
		createCombo(
			inner, "WAP Template path resource not found",
			ValidationPreferences.LIFERAY_LAYOUTTPL_XML_WAP_TEMPLATE_PATH_NOT_FOUND);
		createCombo(
			inner, "Thumbnail path resource not found",
			ValidationPreferences.LIFERAY_LAYOUTTPL_XML_THUMBNAIL_PATH_NOT_FOUND);

		return parent;
	}

	protected void enableValues() {
	}

	protected IDialogSettings getDialogSettings() {
		return PortletUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected String getPreferenceNodeQualifier() {
		return ProjectCorePlugin.getDefault().getBundle().getSymbolicName();
	}

	@Override
	protected String getPreferencePageID() {
		return VALIDATION_ID;
	}

	@Override
	protected String getProjectSettingsKey() {
		return ProjectCorePlugin.USE_PROJECT_SETTINGS;
	}

	@Override
	protected String getPropertyPageID() {
		return PORTLET_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID;
	}

	protected String getQualifier() {
		return PortletCore.getDefault().getBundle().getSymbolicName();
	}

	protected void initializeValues() {
		// for (Map.Entry<String, Combo> entry : combos.entrySet()) {
		// int val = getPortletCorePreferences().getInt(entry.getKey(), -1);
		// entry.getValue().select(ERROR_MAP.get(val));
		// }
	}

	protected boolean loadPreferences() {
		BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {

			public void run() {
				initializeValues();
				validateValues();
				enableValues();
			}
		});
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

}
