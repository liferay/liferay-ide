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

import com.liferay.ide.portlet.core.jsf.INewJSFPortletClassDataModelProperties;
import com.liferay.ide.portlet.ui.wizard.NewPortletOptionsWizardPage;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class NewJSFPortletOptionsWizardPage
	extends NewPortletOptionsWizardPage implements INewJSFPortletClassDataModelProperties {

	public NewJSFPortletOptionsWizardPage(
		IDataModel dataModel, String pageName, String desc, String title, boolean fragment) {

		super(dataModel, pageName, desc, title, fragment);
	}

	@Override
	protected void createJSPsField(Composite parent) {
		super.createJSPsField(parent);

		createJspsButton.setText(Msgs.createViewFiles);
		jspLabel.setText(Msgs.viewFolder);

		SelectionAdapter adapter = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				boolean selection = createJspsButton.getSelection();

				standardJSFRadio.setEnabled(selection);
				iceFacesRadio.setEnabled(selection);
				liferayFacesAlloyRadio.setEnabled(selection);
				primeFacesRadio.setEnabled(selection);
				richFacesRadio.setEnabled(selection);
			}

		};

		createJspsButton.addSelectionListener(adapter);
	}

	@Override
	protected void createLiferayPortletModesGroup(Composite composite) {

		// don't create liferay portlet modes section

	}

	@Override
	protected void createResourceBundleField(Composite parent) {

		// don't create resource bundle field section

	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite top = super.createTopLevelComposite(parent);

		IDataModelListener listener = new IDataModelListener() {

			public void propertyChanged(DataModelEvent event) {
				if (PORTLET_NAME.equals(event.getPropertyName())) {
					synchHelper.synchAllUIWithModel();
				}
			}

		};

		IDataModel dataModel = synchHelper.getDataModel();

		dataModel.addListener(listener);

		return top;
	}

	@Override
	protected void createViewTemplateGroup(Composite composite) {
		Group group = SWTUtil.createGroup(composite, Msgs.viewTemplate, 1);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = 3;

		group.setLayoutData(gd);

		standardJSFRadio = new Button(group, SWT.RADIO);

		standardJSFRadio.setText(Msgs.standardJSF);

		iceFacesRadio = new Button(group, SWT.RADIO);

		iceFacesRadio.setText(Msgs.iceFaces);

		liferayFacesAlloyRadio = new Button(group, SWT.RADIO);

		liferayFacesAlloyRadio.setText(Msgs.liferayFacesAlloy);

		primeFacesRadio = new Button(group, SWT.RADIO);

		primeFacesRadio.setText(Msgs.primeFaces);

		richFacesRadio = new Button(group, SWT.RADIO);

		richFacesRadio.setText(Msgs.richFaces);

		synchHelper.synchRadio(standardJSFRadio, STANDARD_JSF, null);
		synchHelper.synchRadio(iceFacesRadio, ICE_FACES, null);
		synchHelper.synchRadio(liferayFacesAlloyRadio, LIFERAY_FACES_ALLOY, null);
		synchHelper.synchRadio(primeFacesRadio, PRIME_FACES, null);
		synchHelper.synchRadio(richFacesRadio, RICH_FACES, null);
	}

	protected Button iceFacesRadio;
	protected Button liferayFacesAlloyRadio;
	protected Button primeFacesRadio;
	protected Button richFacesRadio;
	protected Button standardJSFRadio;

	private static class Msgs extends NLS {

		public static String createViewFiles;
		public static String iceFaces;
		public static String liferayFacesAlloy;
		public static String primeFaces;
		public static String richFaces;
		public static String standardJSF;
		public static String viewFolder;
		public static String viewTemplate;

		static {
			initializeMessages(NewJSFPortletOptionsWizardPage.class.getName(), Msgs.class);
		}

	}

}