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
import com.liferay.ide.ui.form.FormLayoutFactory;
import com.liferay.ide.ui.form.IDEFormPage;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Gregory Amerson
 */
public class DependenciesFormPage extends IDEFormPage {

	public static final String PAGE_ID = "dependencies";

	public DependenciesFormPage(FormEditor editor) {
		super(editor, PAGE_ID, Msgs.dependencies);
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm form = managedForm.getForm();

		form.setImage(
			PortletUIPlugin.imageDescriptorFromPlugin(
				PortletUIPlugin.PLUGIN_ID, "/icons/e16/plugin.png").createImage());
		form.setText(Msgs.dependencies);
		Composite body = form.getBody();

		body.setLayout(FormLayoutFactory.createFormGridLayout(true, 2));

		Composite left;
		Composite right;

		FormToolkit toolkit = managedForm.getToolkit();

		left = toolkit.createComposite(body, SWT.NONE);

		left.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		left.setLayoutData(new GridData(GridData.FILL_BOTH));

		right = toolkit.createComposite(body, SWT.NONE);

		right.setLayout(FormLayoutFactory.createFormPaneGridLayout(false, 1));
		right.setLayoutData(new GridData(GridData.FILL_BOTH));

		PortalJarsSection jarsSection = new PortalJarsSection(this, left, _getRequiredSectionLabels());

		managedForm.addPart(jarsSection);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);

		gd.widthHint = 150;

		PortalTldsSection tldsSection = new PortalTldsSection(this, right, _getRequiredSectionLabels());

		managedForm.addPart(tldsSection);

		// managedForm.addPart(new PortalJarsSection(this, left,
		// getRequiredSectionLabels()));

	}

	private String[] _getRequiredSectionLabels() {

		// "Up", // "Down"

		return new String[] {Msgs.add, Msgs.remove, };
	}

	private static class Msgs extends NLS {

		public static String add;
		public static String dependencies;
		public static String remove;

		static {
			initializeMessages(DependenciesFormPage.class.getName(), Msgs.class);
		}

	}

}