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

package com.liferay.ide.eclipse.portlet.vaadin.ui.wizard;

import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.portlet.ui.template.PortletTemplateContextTypeIds;
import com.liferay.ide.eclipse.portlet.ui.wizard.NewLiferayPortletWizardPage;
import com.liferay.ide.eclipse.portlet.ui.wizard.NewPortletWizard;
import com.liferay.ide.eclipse.portlet.vaadin.core.operation.NewVaadinPortletClassDataModelProvider;
import com.liferay.ide.eclipse.portlet.vaadin.ui.VaadinUI;
import com.liferay.ide.eclipse.project.ui.wizard.ValidProjectChecker;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Henri Sara
 */
@SuppressWarnings("restriction")
public class NewVaadinPortletWizard extends NewPortletWizard {

    public static final String ID = "com.liferay.ide.eclipse.portlet.vaadin.ui.wizard.portlet";

	public NewVaadinPortletWizard() {
		this(null);
	}

	public NewVaadinPortletWizard(IDataModel model) {
		super(model);
	}

	@Override
	public String getTitle() {
		return "New Liferay Vaadin Portlet";
	}

	@Override
	protected void doAddPages() {
		addPage(new NewVaadinApplicationClassWizardPage(
			getDataModel(), "pageOne", "Create a Vaadin portlet application class.", getDefaultPageTitle(), fragment));
		addPage(new NewVaadinPortletOptionsWizardPage(
			getDataModel(), "pageTwo", "Specify Vaadin portlet deployment descriptor details.", getDefaultPageTitle(),
			fragment));
		addPage(new NewLiferayPortletWizardPage(
			getDataModel(), "pageThree", "Specify Liferay portlet deployment descriptor details.",
			getDefaultPageTitle(), fragment));
	}

	@Override
	protected String getDefaultPageTitle() {
		return "Create Liferay Vaadin Portlet";
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		// for now, no need for own template store and context type
		TemplateStore templateStore = PortletUIPlugin.getDefault().getTemplateStore();

		TemplateContextType contextType =
			PortletUIPlugin.getDefault().getTemplateContextRegistry().getContextType(PortletTemplateContextTypeIds.NEW);

		return new NewVaadinPortletClassDataModelProvider(templateStore, contextType, fragment);
	}

	@Override
	protected ImageDescriptor getImage() {
		return ImageDescriptor.createFromURL(VaadinUI.getDefault().getBundle().getEntry("/icons/wizban/vaadin_wiz.png"));
	}

	@Override
	public void init( IWorkbench workbench, IStructuredSelection selection )
	{
	    getDataModel();
        ValidProjectChecker checker = new ValidProjectChecker( ID );
        checker.checkValidProjectTypes();
	}

}
