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

package com.liferay.ide.hook.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSectionCallback;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewServicesHookWizardPage extends DataModelWizardPage implements INewHookDataModelProperties {

	public NewServicesHookWizardPage(IDataModel dataModel, String pageName) {
		super(
			dataModel, pageName, Msgs.createServiceHook,
			HookUI.imageDescriptorFromPlugin(HookUI.PLUGIN_ID, "/icons/wizban/hook_wiz.png"));

		setDescription(Msgs.specifyLiferayServices);
	}

	protected void createServicesFileGroup(Composite topComposite) {
		Composite composite = SWTUtil.createTopComposite(topComposite, 2);

		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		String[] titles = {Msgs.serviceTypeTitle, Msgs.implClassTitle};
		String[] labels = {Msgs.serviceTypeLabel, Msgs.impleClassLabel};

		servicesSection = new ServicesTableWizardSection(
			composite, Msgs.definePortalServices, Msgs.addServiceWrapper, Msgs.add, Msgs.edit, Msgs.remove, titles,
			labels, null, getDataModel(), SERVICES_ITEMS);

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);

		gd.heightHint = 150;

		servicesSection.setLayoutData(gd);

		servicesSection.setCallback(new StringArrayTableWizardSectionCallback());

		IProject project = CoreUtil.getProject(getDataModel().getStringProperty(PROJECT_NAME));

		if (project != null) {
			servicesSection.setProject(project);
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 3);

		createServicesFileGroup(topComposite);

		return topComposite;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {SERVICES_ITEMS};
	}

	protected ServicesTableWizardSection servicesSection;

	private static class Msgs extends NLS {

		public static String add;
		public static String addServiceWrapper;
		public static String createServiceHook;
		public static String definePortalServices;
		public static String edit;
		public static String implClassTitle;
		public static String impleClassLabel;
		public static String remove;
		public static String serviceTypeLabel;
		public static String serviceTypeTitle;
		public static String specifyLiferayServices;

		static {
			initializeMessages(NewServicesHookWizardPage.class.getName(), Msgs.class);
		}

	}

}