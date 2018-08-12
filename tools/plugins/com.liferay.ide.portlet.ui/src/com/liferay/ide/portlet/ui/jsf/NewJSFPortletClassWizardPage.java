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

package com.liferay.ide.portlet.ui.jsf;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.jsf.INewJSFPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.jsf.JSFPortletUtil;
import com.liferay.ide.portlet.ui.wizard.NewPortletClassWizardPage;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings("restriction")
public class NewJSFPortletClassWizardPage
	extends NewPortletClassWizardPage implements INewJSFPortletClassDataModelProperties {

	public NewJSFPortletClassWizardPage(
		IDataModel model, String pageName, String pageDesc, String pageTitle, boolean fragment) {

		super(model, pageName, pageDesc, pageTitle, fragment);
	}

	@Override
	protected void createClassnameGroup(Composite parent) {

		// don't create classname group

	}

	protected void createCustomPortletClassGroup(Composite parent) {

		// portlet class

		jsfPortletClassLabel = new Label(parent, SWT.LEFT);

		jsfPortletClassLabel.setText(Msgs.portletClass);
		jsfPortletClassLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		jsfPortletClassText = new Text(parent, SWT.BORDER);

		jsfPortletClassText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		synchHelper.synchText(jsfPortletClassText, JSF_PORTLET_CLASS, null);

		if (fragment) {
			SWTUtil.createLabel(parent, StringPool.EMPTY, 1);
		}
		else {
			jsfPortletClassButton = new Button(parent, SWT.PUSH);

			jsfPortletClassButton.setText(J2EEUIMessages.BROWSE_BUTTON_LABEL);
			jsfPortletClassButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

			SelectionListener listener = new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {

					// handlePortletClassButtonPressed();

					handlePortletClassButtonSelected(jsfPortletClassText);
				}

			};

			jsfPortletClassButton.addSelectionListener(listener);
		}
	}

	@Override
	protected void createPackageGroup(Composite parent) {

		// don't create package group

	}

	@Override
	protected void createSuperclassGroup(Composite parent) {

		// instead of superclass field create our portlet class field

		createCustomPortletClassGroup(parent);
	}

	protected String[] getValidationPropertyNames() {
		List<String> validationPropertyNames = new ArrayList<>();

		if (fragment) {
			return new String[] {
				IArtifactEditOperationDataModelProperties.COMPONENT_NAME, INewJavaClassDataModelProperties.JAVA_PACKAGE,
				JSF_PORTLET_CLASS
			};
		}
		else {
			validationPropertyNames.add(IArtifactEditOperationDataModelProperties.PROJECT_NAME);
			validationPropertyNames.add(INewJavaClassDataModelProperties.SOURCE_FOLDER);
			validationPropertyNames.add(JSF_PORTLET_CLASS);
		}

		return validationPropertyNames.toArray(new String[0]);
	}

	protected void handlePortletClassButtonSelected(Control control) {
		handleClassButtonSelected(
			control, "javax.portlet.GenericPortlet", Msgs.portletClassSelection, Msgs.choosePortletClass);
	}

	@Override
	protected boolean isProjectValid(IProject project) {
		boolean projectValid = super.isProjectValid(project);

		if (!projectValid) {
			return false;
		}

		return JSFPortletUtil.isJSFProject(project);
	}

	@Override
	protected void setFocusOnClassText() {

		// dont set focus nothing really needs to be done on this page

	}

	protected Button jsfPortletClassButton;
	protected Label jsfPortletClassLabel;
	protected Text jsfPortletClassText;

	private static class Msgs extends NLS {

		public static String choosePortletClass;
		public static String portletClass;
		public static String portletClassSelection;

		static {
			initializeMessages(NewJSFPortletClassWizardPage.class.getName(), Msgs.class);
		}

	}

}