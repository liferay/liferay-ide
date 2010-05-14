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

package com.liferay.ide.eclipse.project.ui.wizard;

import org.eclipse.jst.j2ee.internal.wizard.J2EEComponentFacetCreationWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PluginProjectAdvPage extends J2EEComponentFacetCreationWizardPage {

	public PluginProjectAdvPage(NewPluginProjectWizard wizard, IDataModel model) {
		super(model, "advanced.page");
		
		setWizard(wizard);
		
		setImageDescriptor(wizard.getDefaultPageImageDescriptor());
		
		setTitle("Liferay Plug-in Project");
		
		setDescription("Configure advanced settings for Liferay Plug-in project.");
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void restoreDefaultSettings() {
		super.restoreDefaultSettings();
	}

	@Override
	public void storeDefaultSettings() {
		super.storeDefaultSettings();
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createPrimaryFacetComposite(top);
		createPresetPanel(top);
		
		// createWorkingSetGroupPanel(top, new String[] { RESOURCE_WORKING_SET,
		// JAVA_WORKING_SET });
		return top;
	}

	@Override
	protected String getModuleFacetID() {
		return IModuleConstants.JST_WEB_MODULE;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return null;
	}

}
