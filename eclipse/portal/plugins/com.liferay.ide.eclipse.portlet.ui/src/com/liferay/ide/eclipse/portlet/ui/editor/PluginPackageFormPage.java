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

package com.liferay.ide.eclipse.portlet.ui.editor;

import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.ui.form.FormLayoutFactory;
import com.liferay.ide.eclipse.ui.form.IDEFormPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Greg Amerson
 */
public class PluginPackageFormPage extends IDEFormPage {

	protected ScrolledForm form;

	protected FormToolkit toolkit;

	public PluginPackageFormPage(PluginPackageEditor editor) {
		super(editor, "pluginPackage", "Plugin Package");
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

		form.setText("Plugin Package Properties");
		form.setImage(PortletUIPlugin.imageDescriptorFromPlugin(PortletUIPlugin.PLUGIN_ID, "/icons/e16/plugin.png").createImage());

		toolkit = managedForm.getToolkit();

		Composite body = form.getBody();
		body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));

		PluginPackageGeneralSection generalSection = new PluginPackageGeneralSection(this, body);

		managedForm.addPart(generalSection);

	}

}
