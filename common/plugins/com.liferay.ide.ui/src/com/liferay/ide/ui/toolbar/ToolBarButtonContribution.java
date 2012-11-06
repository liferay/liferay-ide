/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

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
		layout.marginLeft = this.marginLeft;
		layout.marginRight = this.marginRight;
		composite.setLayout(layout);

		Control button = createButton(composite);
		int heigtHint = SWT.DEFAULT;
		if (Platform.WS_WIN32.equals(SWT.getPlatform())) {
			heigtHint = 22;
		} else if (Platform.WS_CARBON.equals(SWT.getPlatform())) {
			heigtHint = 32;
		}
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(SWT.DEFAULT, heigtHint).applyTo(button);
		return composite;
	}

}
