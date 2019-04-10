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

package com.liferay.ide.ui.form;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Gregory Amerson
 */
public abstract class FormDetails extends AbstractFormPart implements IContextPart, IDetailsPage {

	public FormDetails() {
	}

	public void cancelEdit() {
		super.refresh();
	}

	/**
	 * @param selection
	 * @return
	 */
	public boolean canCopy(ISelection selection) {

		// Sub-classes to override

		return false;
	}

	/**
	 * @param selection
	 * @return
	 */
	public boolean canCut(ISelection selection) {

		// Sub-classes to override

		return false;
	}

	public boolean canPaste(Clipboard clipboard) {
		return true;
	}

	public boolean doGlobalAction(String actionId) {
		return false;
	}

	protected void createSpacer(FormToolkit toolkit, Composite parent, int span) {
		Label spacer = toolkit.createLabel(parent, "");
		GridData gd = new GridData();

		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
	}

	protected void markDetailsPart(Control control) {
		control.setData("part", this);
	}

}