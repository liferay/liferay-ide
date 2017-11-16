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
import com.liferay.ide.portlet.core.operation.PortletSupertypesValidator;

import org.eclipse.jst.servlet.ui.internal.wizard.NewWebClassOptionsWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings("restriction")
public class NewPortletClassOptionsWizardPage
	extends NewWebClassOptionsWizardPage implements INewPortletClassDataModelProperties {

	public NewPortletClassOptionsWizardPage(
		IDataModel model, String pageName, String pageDesc, String pageTitle, boolean fragment) {

		super(model, pageName, pageDesc, pageTitle);
		this.fragment = fragment;
	}

	@Override
	protected void createStubsComposite(Composite parent) {
		super.createStubsComposite(parent);

		Composite comp = new Composite(methodStubs, SWT.NULL);

		GridLayout layout = new GridLayout(4, false);

		layout.marginWidth = 0;
		layout.makeColumnsEqualWidth = true;

		comp.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);

		comp.setLayoutData(data);

		initButton = new Button(comp, SWT.CHECK);

		initButton.setText("&init");
		synchHelper.synchCheckbox(initButton, INewPortletClassDataModelProperties.INIT_OVERRIDE, null);

		destroyButton = new Button(comp, SWT.CHECK);

		destroyButton.setText("destro&y");
		synchHelper.synchCheckbox(destroyButton, INewPortletClassDataModelProperties.DESTROY_OVERRIDE, null);

		doViewButton = new Button(comp, SWT.CHECK);

		doViewButton.setText("doVie&w");
		synchHelper.synchCheckbox(doViewButton, INewPortletClassDataModelProperties.DOVIEW_OVERRIDE, null);

		doEditButton = new Button(comp, SWT.CHECK);

		doEditButton.setText("doEdi&t");
		synchHelper.synchCheckbox(doEditButton, INewPortletClassDataModelProperties.DOEDIT_OVERRIDE, null);

		doHelpButton = new Button(comp, SWT.CHECK);

		doHelpButton.setText("doHel&p");
		synchHelper.synchCheckbox(doHelpButton, INewPortletClassDataModelProperties.DOHELP_OVERRIDE, null);

		doAboutButton = new Button(comp, SWT.CHECK);

		doAboutButton.setText("doA&bout");
		synchHelper.synchCheckbox(doAboutButton, INewPortletClassDataModelProperties.DOABOUT_OVERRIDE, null);

		doConfigButton = new Button(comp, SWT.CHECK);

		doConfigButton.setText("doCon&fig");
		synchHelper.synchCheckbox(doConfigButton, INewPortletClassDataModelProperties.DOCONFIG_OVERRIDE, null);

		doEditDefaultsButton = new Button(comp, SWT.CHECK);

		doEditDefaultsButton.setText("doEditDefa&ults");
		synchHelper.synchCheckbox(
			doEditDefaultsButton, INewPortletClassDataModelProperties.DOEDITDEFAULTS_OVERRIDE, null);

		doEditGuestButton = new Button(comp, SWT.CHECK);

		doEditGuestButton.setText("doEditGu&est");
		synchHelper.synchCheckbox(doEditGuestButton, INewPortletClassDataModelProperties.DOEDITGUEST_OVERRIDE, null);

		doPreviewButton = new Button(comp, SWT.CHECK);

		doPreviewButton.setText("doPre&view");
		synchHelper.synchCheckbox(doPreviewButton, INewPortletClassDataModelProperties.DOPREVIEW_OVERRIDE, null);

		doPrintButton = new Button(comp, SWT.CHECK);

		doPrintButton.setText("doP&rint");
		synchHelper.synchCheckbox(doPrintButton, INewPortletClassDataModelProperties.DOPRINT_OVERRIDE, null);

		processActionButton = new Button(comp, SWT.CHECK);

		processActionButton.setText("&processAction");
		synchHelper.synchCheckbox(
			processActionButton, INewPortletClassDataModelProperties.PROCESSACTION_OVERRIDE, null);

		serveResourceButton = new Button(comp, SWT.CHECK);

		serveResourceButton.setText("&serveResource");
		synchHelper.synchCheckbox(
			serveResourceButton, INewPortletClassDataModelProperties.SERVERESOURCE_OVERRIDE, null);
	}

	@Override
	protected void enter() {
		super.enter();

		boolean mvcPortlet = PortletSupertypesValidator.isMVCPortletSuperclass(getDataModel());

		boolean liferayPortlet = PortletSupertypesValidator.isLiferayPortletSuperclass(getDataModel());

		inheritButton.setEnabled(!mvcPortlet);

		destroyButton.setEnabled(!mvcPortlet);

		doViewButton.setEnabled(!mvcPortlet && !getDataModel().getBooleanProperty(VIEW_MODE));

		doEditButton.setEnabled(!mvcPortlet && !getDataModel().getBooleanProperty(EDIT_MODE));

		doHelpButton.setEnabled(!mvcPortlet && !getDataModel().getBooleanProperty(HELP_MODE));

		doAboutButton.setEnabled(!mvcPortlet && liferayPortlet && !getDataModel().getBooleanProperty(ABOUT_MODE));

		doConfigButton.setEnabled(!mvcPortlet && liferayPortlet && !getDataModel().getBooleanProperty(CONFIG_MODE));

		doEditDefaultsButton.setEnabled(
			!mvcPortlet && liferayPortlet && !getDataModel().getBooleanProperty(EDITDEFAULTS_MODE));

		doEditGuestButton.setEnabled(
			!mvcPortlet && liferayPortlet && !getDataModel().getBooleanProperty(EDITGUEST_MODE));

		doPreviewButton.setEnabled(!mvcPortlet && liferayPortlet && !getDataModel().getBooleanProperty(PREVIEW_MODE));

		doPrintButton.setEnabled(!mvcPortlet && liferayPortlet && !getDataModel().getBooleanProperty(PRINT_MODE));

		processActionButton.setEnabled(!mvcPortlet);

		serveResourceButton.setEnabled(!mvcPortlet);
	}

	protected Button destroyButton;
	protected Button doAboutButton;
	protected Button doConfigButton;
	protected Button doEditButton;
	protected Button doEditDefaultsButton;
	protected Button doEditGuestButton;
	protected Button doHelpButton;
	protected Button doPreviewButton;
	protected Button doPrintButton;
	protected Button doViewButton;
	protected boolean fragment;
	protected Button initButton;
	protected Button processActionButton;
	protected Button serveResourceButton;

}