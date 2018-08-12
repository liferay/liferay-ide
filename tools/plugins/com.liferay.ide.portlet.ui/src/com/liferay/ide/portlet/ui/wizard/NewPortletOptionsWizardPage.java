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

package com.liferay.ide.portlet.ui.wizard;

import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.LiferayDataModelWizardPage;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class NewPortletOptionsWizardPage
	extends LiferayDataModelWizardPage implements INewPortletClassDataModelProperties {

	public NewPortletOptionsWizardPage(
		IDataModel dataModel, String pageName, String desc, String title, boolean fragment) {

		super(dataModel, pageName, title, null);

		this.fragment = fragment;
		setDescription(desc);
	}

	protected void createJSPsField(Composite parent) {
		createJspsButton = new Button(parent, SWT.CHECK);

		createJspsButton.setText("Create JSP &files");
		createJspsButton.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 2, 1));

		synchHelper.synchCheckbox(createJspsButton, INewPortletClassDataModelProperties.CREATE_JSPS, null);

		jspLabel = SWTUtil.createLabel(parent, Msgs.jspFolderLabel, 1);

		jspFolder = SWTUtil.createText(parent, 1);

		((GridData)jspFolder.getLayoutData()).widthHint = 150;
		synchHelper.synchText(jspFolder, INewPortletClassDataModelProperties.CREATE_JSPS_FOLDER, null);

		SelectionAdapter adapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				jspLabel.setEnabled(createJspsButton.getSelection());
				jspFolder.setEnabled(createJspsButton.getSelection());
			}

		};

		createJspsButton.addSelectionListener(adapter);
	}

	protected void createLiferayPortletModesGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, Msgs.liferayPortletModes, 6);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		aboutButton = new Button(group, SWT.CHECK);

		aboutButton.setText("A&bout");

		synchHelper.synchCheckbox(aboutButton, INewPortletClassDataModelProperties.ABOUT_MODE, null);

		configButton = new Button(group, SWT.CHECK);

		configButton.setText("Co&nfig");

		synchHelper.synchCheckbox(configButton, INewPortletClassDataModelProperties.CONFIG_MODE, null);

		editDefaultsButton = new Button(group, SWT.CHECK);

		editDefaultsButton.setText("E&dit Defaults");

		synchHelper.synchCheckbox(editDefaultsButton, INewPortletClassDataModelProperties.EDITDEFAULTS_MODE, null);

		editGuestButton = new Button(group, SWT.CHECK);

		editGuestButton.setText("Ed&it Guest");

		synchHelper.synchCheckbox(editGuestButton, INewPortletClassDataModelProperties.EDITGUEST_MODE, null);

		previewButton = new Button(group, SWT.CHECK);

		previewButton.setText("Pre&view");

		synchHelper.synchCheckbox(previewButton, INewPortletClassDataModelProperties.PREVIEW_MODE, null);

		printButton = new Button(group, SWT.CHECK);

		printButton.setText("P&rint");

		synchHelper.synchCheckbox(printButton, INewPortletClassDataModelProperties.PRINT_MODE, null);
	}

	protected void createPortletInfoGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, Msgs.portletInfo, 2);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		SWTUtil.createLabel(group, Msgs.nameLabel, 1);

		portletName = SWTUtil.createText(group, 1);

		this.synchHelper.synchText(portletName, PORTLET_NAME, null);

		SWTUtil.createLabel(group, Msgs.displayNameLabel, 1);

		displayName = SWTUtil.createText(group, 1);

		this.synchHelper.synchText(displayName, DISPLAY_NAME, null);

		SWTUtil.createLabel(group, Msgs.titleLabel, 1);

		title = SWTUtil.createText(group, 1);

		this.synchHelper.synchText(title, TITLE, null);

		IDataModelListener listener = new IDataModelListener() {

			public void propertyChanged(DataModelEvent event) {
				if (CLASS_NAME.equals(event.getPropertyName()) || PORTLET_NAME.equals(event.getPropertyName())) {
					synchHelper.synchAllUIWithModel();
				}
			}

		};

		IDataModel dataModel = synchHelper.getDataModel();

		dataModel.addListener(listener);
	}

	protected void createPortletModesGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, Msgs.portletModes, 3);

		GridLayout gl = (GridLayout)group.getLayout();

		gl.makeColumnsEqualWidth = true;

		group.setLayout(gl);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		viewButton = new Button(group, SWT.CHECK);

		viewButton.setText("Vie&w");

		synchHelper.synchCheckbox(viewButton, INewPortletClassDataModelProperties.VIEW_MODE, null);

		editButton = new Button(group, SWT.CHECK);

		editButton.setText("Edi&t");

		synchHelper.synchCheckbox(editButton, INewPortletClassDataModelProperties.EDIT_MODE, null);

		helpButton = new Button(group, SWT.CHECK);

		helpButton.setText("Hel&p");

		synchHelper.synchCheckbox(helpButton, INewPortletClassDataModelProperties.HELP_MODE, null);
	}

	protected void createResourceBundleField(Composite parent) {
		createResourceBundleFileButton = new Button(parent, SWT.CHECK);

		createResourceBundleFileButton.setText(Msgs.createResourceBundleFile);
		createResourceBundleFileButton.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 2, 1));

		synchHelper.synchCheckbox(
			createResourceBundleFileButton, INewPortletClassDataModelProperties.CREATE_RESOURCE_BUNDLE_FILE, null);

		Label resourceBundleFileLabel = SWTUtil.createLabel(parent, Msgs.resourceBundleFilePathLabel, 1);

		resourceBundleFilePath = SWTUtil.createText(parent, 1);

		((GridData)resourceBundleFilePath.getLayoutData()).widthHint = 150;
		synchHelper.synchText(
			resourceBundleFilePath, INewPortletClassDataModelProperties.CREATE_RESOURCE_BUNDLE_FILE_PATH, null);

		SelectionAdapter adapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				resourceBundleFileLabel.setEnabled(createResourceBundleFileButton.getSelection());

				resourceBundleFilePath.setEnabled(createResourceBundleFileButton.getSelection());
			}

		};

		createResourceBundleFileButton.addSelectionListener(adapter);
	}

	protected void createResourcesGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, Msgs.resources, 2);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		createJSPsField(group);
		createResourceBundleField(group);
		createViewTemplateGroup(group);
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite composite = SWTUtil.createTopComposite(parent, 3);

		createPortletInfoGroup(composite);

		createPortletModesGroup(composite);

		createLiferayPortletModesGroup(composite);

		createResourcesGroup(composite);

		return composite;
	}

	protected void createViewTemplateGroup(Composite composite) {

		// don't create view template section

	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {
			PORTLET_NAME, DISPLAY_NAME, VIEW_MODE, EDIT_MODE, HELP_MODE, ABOUT_MODE, CONFIG_MODE, EDITDEFAULTS_MODE,
			EDITGUEST_MODE, PREVIEW_MODE, PRINT_MODE, CREATE_RESOURCE_BUNDLE_FILE, CREATE_RESOURCE_BUNDLE_FILE_PATH,
			CREATE_JSPS, CREATE_JSPS_FOLDER
		};
	}

	protected boolean isProjectValid(IProject project) {
		return ProjectUtil.isPortletProject(project);
	}

	@Override
	protected boolean showValidationErrorsOnEnter() {
		return true;
	}

	protected Button aboutButton;
	protected Text bundleFile;
	protected Button configButton;
	protected Button createJspsButton;
	protected Button createResourceBundleFileButton;
	protected Text displayName;
	protected Button editButton;
	protected Button editDefaultsButton;
	protected Button editGuestButton;
	protected boolean fragment;
	protected Button helpButton;
	protected Text jspFolder;
	protected Label jspLabel;
	protected Text portletName;
	protected Button previewButton;
	protected Button printButton;
	protected Text resourceBundleFilePath;
	protected Text title;
	protected Button viewButton;

	private static class Msgs extends NLS {

		public static String createResourceBundleFile;
		public static String displayNameLabel;
		public static String jspFolderLabel;
		public static String liferayPortletModes;
		public static String nameLabel;
		public static String portletInfo;
		public static String portletModes;
		public static String resourceBundleFilePathLabel;
		public static String resources;
		public static String titleLabel;

		static {
			initializeMessages(NewPortletOptionsWizardPage.class.getName(), Msgs.class);
		}

	}

}