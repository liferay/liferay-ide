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

package com.liferay.ide.portlet.vaadin.ui.wizard;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.ui.wizard.NewPortletClassWizardPage;
import com.liferay.ide.portlet.vaadin.core.operation.INewVaadinPortletClassDataModelProperties;
import com.liferay.ide.portlet.vaadin.ui.VaadinUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

import org.osgi.framework.Bundle;

/**
 * @author Henri Sara - borrows from superclass by Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings("restriction")
public class NewVaadinApplicationClassWizardPage
	extends NewPortletClassWizardPage implements INewVaadinPortletClassDataModelProperties {

	// superclass has unused fields that are named similarly, don't depend on them

	public NewVaadinApplicationClassWizardPage(
		IDataModel model, String pageName, String pageDesc, String pageTitle, boolean fragment) {

		super(model, pageName, pageDesc, pageTitle, fragment);
	}

	protected void createApplicationClassnameGroup(Composite parent) {

		// class name

		classLabel = new Label(parent, SWT.LEFT);

		classLabel.setText(Msgs.applicationClass);
		classLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		classText = new Text(parent, SWT.SINGLE | SWT.BORDER);

		classText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		synchHelper.synchText(classText, INewJavaClassDataModelProperties.CLASS_NAME, null);

		new Label(parent, SWT.LEFT);
	}

	@Override
	protected void createClassnameGroup(Composite parent) {
		createApplicationClassnameGroup(parent);
	}

	protected void createCustomPortletClassGroup(Composite parent) {

		// portlet class

		vaadinPortletClassLabel = new Label(parent, SWT.LEFT);

		vaadinPortletClassLabel.setText(Msgs.portletClass);
		vaadinPortletClassLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		vaadinPortletClassCombo = new Combo(parent, SWT.DROP_DOWN);

		vaadinPortletClassCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		synchHelper.synchCombo(vaadinPortletClassCombo, VAADIN_PORTLET_CLASS, null);

		if (fragment) {
			SWTUtil.createLabel(parent, StringPool.EMPTY, 1);
		}
		else {
			vaadinPortletClassButton = new Button(parent, SWT.PUSH);

			vaadinPortletClassButton.setText(J2EEUIMessages.BROWSE_BUTTON_LABEL);
			vaadinPortletClassButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

			SelectionListener selectionListener = new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {

					// Do nothing

				}

				public void widgetSelected(SelectionEvent e) {

					// handlePortletClassButtonPressed();

					handlePortletClassButtonSelected(vaadinPortletClassCombo);
				}

			};

			vaadinPortletClassButton.addSelectionListener(selectionListener);
		}
	}

	protected void createSuperclassGroup(Composite parent) {
		super.createSuperclassGroup(parent);

		createCustomPortletClassGroup(parent);
	}

	protected String[] getValidationPropertyNames() {
		List<String> validationPropertyNames = new ArrayList<>();

		if (fragment) {
			return new String[] {
				IArtifactEditOperationDataModelProperties.COMPONENT_NAME, INewJavaClassDataModelProperties.JAVA_PACKAGE,
				INewJavaClassDataModelProperties.CLASS_NAME, INewJavaClassDataModelProperties.SUPERCLASS,
				VAADIN_PORTLET_CLASS
			};
		}
		else {
			validationPropertyNames.add(IArtifactEditOperationDataModelProperties.PROJECT_NAME);
			validationPropertyNames.add(INewJavaClassDataModelProperties.SOURCE_FOLDER);
			validationPropertyNames.add(INewJavaClassDataModelProperties.CLASS_NAME);
			validationPropertyNames.add(INewJavaClassDataModelProperties.JAVA_PACKAGE);
			validationPropertyNames.add(INewJavaClassDataModelProperties.SUPERCLASS);
			validationPropertyNames.add(VAADIN_PORTLET_CLASS);
		}

		return validationPropertyNames.toArray(new String[0]);
	}

	protected void handleClassButtonSelected(Control control) {
		handleClassButtonSelected(
			control, QUALIFIED_VAADIN_APPLICATION, J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_TITLE,
			J2EEUIMessages.SUPERCLASS_SELECTION_DIALOG_DESC);
	}

	protected void handlePortletClassButtonSelected(Control control) {
		handleClassButtonSelected(
			control, "javax.portlet.GenericPortlet", Msgs.portletClassSelection, Msgs.choosePortletClass);
	}

	protected void setShellImage() {
		VaadinUI vaadinUI = VaadinUI.getDefault();

		Bundle bundle = vaadinUI.getBundle();

		URL url = bundle.getEntry("/icons/e16/vaadinportlet.png");

		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

		Image shellImage = imageDescriptor.createImage();

		getShell().setImage(shellImage);
	}

	// superclass selection - different base class than in new portlet wizard

	protected Button vaadinPortletClassButton;
	protected Combo vaadinPortletClassCombo;
	protected Label vaadinPortletClassLabel;

	private static class Msgs extends NLS {

		public static String applicationClass;
		public static String choosePortletClass;
		public static String portletClass;
		public static String portletClassSelection;

		static {
			initializeMessages(NewVaadinApplicationClassWizardPage.class.getName(), Msgs.class);
		}

	}

}