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

package com.liferay.ide.eclipse.portlet.ui.editor;

import com.liferay.ide.eclipse.ui.form.FormLayoutFactory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Greg Amerson
 */
public class ServiceBuilderFormPage extends FormPage {

	protected ScrolledForm form;

	protected FormToolkit toolkit;

	public ServiceBuilderFormPage(ServiceBuilderEditor editor) {
		super(editor, "serviceBuilder", "Service Builder");
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		form = managedForm.getForm();

		toolkit = managedForm.getToolkit();
		// toolkit.setBorderStyle(SWT.NULL);

		Composite body = form.getBody();

		body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));

		ServiceBuilderGeneralSection generalSection = new ServiceBuilderGeneralSection(this, body);

		managedForm.addPart(generalSection);

	}

}
