/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.ui.wizard;

import com.liferay.ide.eclipse.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

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
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class NewPortletOptionsWizardPage extends DataModelWizardPage implements INewPortletClassDataModelProperties {

	protected Button aboutButton;

	protected Button configButton;

	protected Button createJspsButton;

	protected Text displayName;

	protected Button editButton;

	protected Button editDefaultsButton;

	protected Button editGuestButton;

	protected Button helpButton;

	protected Text jspFolder;

	protected Text portletName;

	protected Button previewButton;

	protected Button printButton;

	protected Text title;

	protected Button viewButton;

	public NewPortletOptionsWizardPage(IDataModel dataModel, String pageName, String desc, String title) {
		super(dataModel, pageName);

		setTitle(title);

		setDescription(desc);
	}

	protected void createLiferayPortletModesGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, "Liferay Portlet Modes", 6);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		aboutButton = new Button(group, SWT.CHECK);
		aboutButton.setText("A&bout"); //$NON-NLS-1$
		synchHelper.synchCheckbox(aboutButton, INewPortletClassDataModelProperties.ABOUT_MODE, null);

		configButton = new Button(group, SWT.CHECK);
		configButton.setText("Co&nfig"); //$NON-NLS-1$
		synchHelper.synchCheckbox(configButton, INewPortletClassDataModelProperties.CONFIG_MODE, null);

		editDefaultsButton = new Button(group, SWT.CHECK);
		editDefaultsButton.setText("E&dit Defaults"); //$NON-NLS-1$
		synchHelper.synchCheckbox(editDefaultsButton, INewPortletClassDataModelProperties.EDITDEFAULTS_MODE, null);

		editGuestButton = new Button(group, SWT.CHECK);
		editGuestButton.setText("Ed&it Guest"); //$NON-NLS-1$
		synchHelper.synchCheckbox(editGuestButton, INewPortletClassDataModelProperties.EDITGUEST_MODE, null);

		previewButton = new Button(group, SWT.CHECK);
		previewButton.setText("Pre&view"); //$NON-NLS-1$
		synchHelper.synchCheckbox(previewButton, INewPortletClassDataModelProperties.PREVIEW_MODE, null);

		printButton = new Button(group, SWT.CHECK);
		printButton.setText("P&rint"); //$NON-NLS-1$
		synchHelper.synchCheckbox(printButton, INewPortletClassDataModelProperties.PRINT_MODE, null);
	}

	protected void createPortletInfoGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, "Portlet Info", 2);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		SWTUtil.createLabel(group, "Name:", 1);

		this.portletName = SWTUtil.createText(group, 1);
		this.synchHelper.synchText(portletName, PORTLET_NAME, null);

		SWTUtil.createLabel(group, "Display name:", 1);

		this.displayName = SWTUtil.createText(group, 1);
		this.synchHelper.synchText(displayName, DISPLAY_NAME, null);

		SWTUtil.createLabel(group, "Title:", 1);

		this.title = SWTUtil.createText(group, 1);
		this.synchHelper.synchText(title, TITLE, null);
	}

	protected void createPortletModesGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, "Portlet Modes", 3);

		GridLayout gl = (GridLayout) group.getLayout();
		gl.makeColumnsEqualWidth = true;

		group.setLayout(gl);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		viewButton = new Button(group, SWT.CHECK);
		viewButton.setText("Vie&w"); //$NON-NLS-1$
		synchHelper.synchCheckbox(viewButton, INewPortletClassDataModelProperties.VIEW_MODE, null);

		editButton = new Button(group, SWT.CHECK);
		editButton.setText("Edi&t"); //$NON-NLS-1$
		synchHelper.synchCheckbox(editButton, INewPortletClassDataModelProperties.EDIT_MODE, null);

		helpButton = new Button(group, SWT.CHECK);
		helpButton.setText("Hel&p"); //$NON-NLS-1$
		synchHelper.synchCheckbox(helpButton, INewPortletClassDataModelProperties.HELP_MODE, null);

	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite composite = SWTUtil.createTopComposite(parent, 3);

		createPortletInfoGroup(composite);

		createPortletModesGroup(composite);

		createLiferayPortletModesGroup(composite);

		Composite composite2 = new Composite(composite, SWT.NONE);

		GridLayout gl = new GridLayout(2, false);
		gl.marginLeft = 3;

		composite2.setLayout(gl);

		createJspsButton = new Button(composite2, SWT.CHECK);
		createJspsButton.setText("Create JSP &files"); //$NON-NLS-1$
		createJspsButton.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 2, 1));
		synchHelper.synchCheckbox(createJspsButton, INewPortletClassDataModelProperties.CREATE_JSPS, null);

		final Label jspLabel = SWTUtil.createLabel(composite2, "JSP folder:", 1);

		jspFolder = SWTUtil.createText(composite2, 1);
		((GridData) jspFolder.getLayoutData()).widthHint = 150;
		synchHelper.synchText(jspFolder, INewPortletClassDataModelProperties.CREATE_JSPS_FOLDER, null);

		createJspsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				jspLabel.setEnabled(createJspsButton.getSelection());

				jspFolder.setEnabled(createJspsButton.getSelection());
			}
		});

		return composite;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {
			PORTLET_NAME, DISPLAY_NAME, VIEW_MODE, EDIT_MODE, HELP_MODE, ABOUT_MODE, CONFIG_MODE, EDITDEFAULTS_MODE,
			EDITGUEST_MODE, PREVIEW_MODE, PRINT_MODE
		};
	}

	@Override
	protected boolean showValidationErrorsOnEnter() {
		return true;
	}

}
