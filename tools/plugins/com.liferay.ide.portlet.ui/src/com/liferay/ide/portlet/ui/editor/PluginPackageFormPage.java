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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.ui.form.FormLayoutFactory;
import com.liferay.ide.ui.form.IDEFormPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Gregory Amerson
 */
public class PluginPackageFormPage extends IDEFormPage {

	public PluginPackageFormPage(PluginPackageEditor editor) {
		super(editor, "pluginPackage", Msgs.properties);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		form = managedForm.getForm();

		FormToolkit toolkit = managedForm.getToolkit();

		toolkit.decorateFormHeading(form.getForm());

		form.setText(Msgs.liferayPluginPackageProperties);

		ImageDescriptor descriptor = PortletUIPlugin.imageDescriptorFromPlugin(
			PortletUIPlugin.PLUGIN_ID, "/icons/e16/plugin.png");

		form.setImage(descriptor.createImage());

		Composite body = form.getBody();

		body.setLayout(FormLayoutFactory.createFormGridLayout(true, 2));

		toolkit = managedForm.getToolkit();

		Composite left = toolkit.createComposite(body, SWT.NONE);

		left.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		left.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite right = toolkit.createComposite(body, SWT.NONE);

		right.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		right.setLayoutData(new GridData(GridData.FILL_BOTH));

		PluginPackageGeneralSection generalSection = new PluginPackageGeneralSection(this, left);

		managedForm.addPart(generalSection);

		IProject project = getFormEditor().getCommonProject();

		if (SDKUtil.isSDKProject(project)) {
			PortalJarsSection jarsSection = new PortalJarsSection(this, right, _getPortalSectionLabels());

			managedForm.addPart(jarsSection);

			PortalDeployExcludesSection excludesSection = new PortalDeployExcludesSection(
				this, right, _getPortalSectionLabels());

			managedForm.addPart(excludesSection);

			PortalTldsSection tldsSection = new PortalTldsSection(this, right, _getPortalSectionLabels());

			managedForm.addPart(tldsSection);

			RequiredDeploymentContextsSection contextsSection = new RequiredDeploymentContextsSection(
				this, right, _getContextsSectionLabels());

			managedForm.addPart(contextsSection);
		}
	}

	protected ScrolledForm form;
	protected FormToolkit toolkit;

	private String[] _getContextsSectionLabels() {
		return new String[] {Msgs.add, Msgs.remove, Msgs.up, Msgs.down};
	}

	private String[] _getPortalSectionLabels() {
		return new String[] {Msgs.add, Msgs.remove};
	}

	private static class Msgs extends NLS {

		public static String add;
		public static String down;
		public static String liferayPluginPackageProperties;
		public static String properties;
		public static String remove;
		public static String up;

		static {
			initializeMessages(PluginPackageFormPage.class.getName(), Msgs.class);
		}

	}

}