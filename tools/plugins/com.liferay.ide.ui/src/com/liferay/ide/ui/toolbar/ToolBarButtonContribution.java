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

package com.liferay.ide.ui.toolbar;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Steffen Pingel
 */
public abstract class ToolBarButtonContribution extends ControlContribution {

	public int marginLeft = 0;
	public int marginRight = 0;

	protected ToolBarButtonContribution(String id) {
		super(id);
	}

	protected abstract Control createButton(Composite parent);

	@Override
	protected Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		composite.setBackground(null);

		GridLayout layout = new GridLayout();

		layout.marginWidth = 0;
		layout.marginHeight = 0;

		if (Platform.WS_CARBON.equals(SWT.getPlatform())) {
			layout.marginTop = -3;
		}

		layout.marginLeft = marginLeft;
		layout.marginRight = marginRight;
		composite.setLayout(layout);

		Control button = createButton(composite);

		int heigtHint = SWT.DEFAULT;

		if (Platform.WS_WIN32.equals(SWT.getPlatform())) {
			heigtHint = 22;
		}
		else if (Platform.WS_CARBON.equals(SWT.getPlatform())) {
			heigtHint = 32;
		}

		GridDataFactory gdFactory = GridDataFactory.fillDefaults();

		GridDataFactory align = gdFactory.align(SWT.BEGINNING, SWT.CENTER);

		GridDataFactory hint = align.hint(SWT.DEFAULT, heigtHint);

		hint.applyTo(button);

		return composite;
	}

}