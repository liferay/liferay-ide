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

package com.liferay.ide.upgrade.plan.ui.internal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Terry Jia
 */
public class AddOutlineDialog extends Dialog {

	public AddOutlineDialog(Shell parentShell) {
		super(parentShell);
	}

	public String getURL() {
		return _url;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite areaParent = (Composite)super.createDialogArea(parent);

		areaParent.setLayout(new GridLayout(2, false));

		_urlLabel = new Label(areaParent, SWT.LEFT);

		_urlLabel.setText("URL:");

		_urlLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		_urlText = new Text(areaParent, SWT.SINGLE | SWT.BORDER);

		_urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return areaParent;
	}

	@Override
	protected void okPressed() {
		_url = _urlText.getText();

		super.okPressed();
	}

	private String _url;
	private Label _urlLabel;
	private Text _urlText;

}