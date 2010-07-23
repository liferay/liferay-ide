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
import com.liferay.ide.eclipse.portlet.core.operation.NewPortletClassDataModelProvider;
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.portlet.ui.template.PortletTemplateContextTypeIds;
import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.ui.wizard.IPluginWizardFragment;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebArtifactWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class NewPortletWizard extends NewWebArtifactWizard
	implements IPluginWizardFragment, INewPortletClassDataModelProperties {

	private boolean fragment;
	private IWizardPage hostPage;

	public NewPortletWizard() {
		this(null);
	}

	public NewPortletWizard(IDataModel model) {
		super(model);
		setDefaultPageImageDescriptor(getImage());
	}

	public String getFragmentPluginFacetId() {
		return IPluginFacetConstants.LIFERAY_PORTLET_PLUGIN_FACET_ID;
	}

	@Override
	public String getNextPage(String currentPageName, String expectedNextPageName) {
		// if ("pageOne".equals(currentPageName) &&
		// "pageTwo".equals(expectedNextPageName) &&
		// !shouldShowClassOptionsPage()) {
		// return "pageThree";
		// }

		return super.getNextPage(currentPageName, expectedNextPageName);
	}

	@Override
	public String getPreviousPage(String currentPageName, String expectedPreviousPageName) {
		// if ("pageThree".equals(currentPageName) &&
		// "pageTwo".equals(expectedPreviousPageName) &&
		// !shouldShowClassOptionsPage()) {
		// return "pageOne";
		// }

		return super.getPreviousPage(currentPageName, expectedPreviousPageName);
	}

	public String getTitle() {
		return "New Liferay Portlet";
	}

	@Override
	protected void doAddPages() {
		addPage(new NewPortletClassWizardPage(
			getDataModel(), "pageOne", "Create a portlet class.", getDefaultPageTitle()));

		addPage(new NewPortletOptionsWizardPage(
			getDataModel(), "pageTwo", "Specify portlet deployment descriptor details.", getDefaultPageTitle()));

		addPage(new NewLiferayPortletWizardPage(
			getDataModel(), "pageThree", "Specify Liferay portlet deployment descriptor details.",
			getDefaultPageTitle()));

		addPage(new NewPortletClassOptionsWizardPage(
			getDataModel(), "pageFour",
			"Specify modifiers, interfaces, and method stubs to generate in Portlet class.", getDefaultPageTitle()));
	}

	protected String getDefaultPageTitle() {
		return "Create Liferay Portlet";
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		TemplateStore templateStore = PortletUIPlugin.getDefault().getTemplateStore();

		TemplateContextType contextType =
			PortletUIPlugin.getDefault().getTemplateContextRegistry().getContextType(PortletTemplateContextTypeIds.NEW);

		return new NewPortletClassDataModelProvider(templateStore, contextType);
	}

	protected ImageDescriptor getImage() {
		return PortletUIPlugin.imageDescriptorFromPlugin(PortletUIPlugin.PLUGIN_ID, "/icons/wizban/portlet_wiz.png");
	}

	@Override
	protected void postPerformFinish()
		throws InvocationTargetException {

		openJavaClass();
	}

	public void setFragment(boolean fragment) {
		this.fragment = fragment;
	}

	public void setHostPage(IWizardPage hostPage) {
		this.hostPage = hostPage;
	}

}
