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

import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.portlet.ui.template.PortletTemplateContextTypeIds;
import com.liferay.ide.portlet.ui.wizard.NewLiferayPortletWizardPage;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;
import com.liferay.ide.portlet.vaadin.core.operation.NewVaadinPortletClassDataModelProvider;
import com.liferay.ide.portlet.vaadin.ui.VaadinUI;
import com.liferay.ide.project.ui.wizard.ValidProjectChecker;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

import org.osgi.framework.Bundle;

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
		return Msgs.newLiferayVaadinPortlet;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getDataModel();
		ValidProjectChecker checker = new ValidProjectChecker(ID);

		checker.checkValidProjectTypes();
	}

	@Override
	protected void doAddPages() {
		addPage(
			new NewVaadinApplicationClassWizardPage(
				getDataModel(), "pageOne", Msgs.createVaadinPortletApplicationClass, getDefaultPageTitle(), fragment));
		addPage(
			new NewVaadinPortletOptionsWizardPage(
				getDataModel(), "pageTwo", Msgs.specifyVaadinPortletDeployment, getDefaultPageTitle(), fragment));
		addPage(
			new NewLiferayPortletWizardPage(
				getDataModel(), "pageThree", Msgs.specifyLiferayPortletDeployment, getDefaultPageTitle(), fragment));
	}

	@Override
	protected String getDefaultPageTitle() {
		return Msgs.createLiferayVaadinPortlet;
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {

		// for now, no need for own template store and context type

		PortletUIPlugin plugin = PortletUIPlugin.getDefault();

		TemplateStore templateStore = plugin.getTemplateStore();

		ContextTypeRegistry contextTypeRegistry = plugin.getTemplateContextRegistry();

		TemplateContextType contextType = contextTypeRegistry.getContextType(PortletTemplateContextTypeIds.NEW);

		return new NewVaadinPortletClassDataModelProvider(fragment) {

			@Override
			public IDataModelOperation getDefaultOperation() {
				return new AddVaadinApplicationOperation(model, templateStore, contextType);
			}

		};
	}

	@Override
	protected ImageDescriptor getImage() {
		VaadinUI vaadinUI = VaadinUI.getDefault();

		Bundle bundle = vaadinUI.getBundle();

		return ImageDescriptor.createFromURL(bundle.getEntry("/icons/wizban/vaadin_wiz.png"));
	}

	private static class Msgs extends NLS {

		public static String createLiferayVaadinPortlet;
		public static String createVaadinPortletApplicationClass;
		public static String newLiferayVaadinPortlet;
		public static String specifyLiferayPortletDeployment;
		public static String specifyVaadinPortletDeployment;

		static {
			initializeMessages(NewVaadinPortletWizard.class.getName(), Msgs.class);
		}

	}

}