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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Gregory Amerson
 */
public abstract class SharedPart {

	public abstract void createControl(Composite parent, int style, int span, FormToolkit toolkit);

	public boolean isEnabled() {
		return _enabled;
	}

	public void setEnabled(boolean enabled) {
		if (enabled != _enabled) {
			_enabled = enabled;
			updateEnabledState();
		}
	}

	protected Composite createComposite(Composite parent, FormToolkit toolkit) {
		if (toolkit == null) {
			return new Composite(parent, SWT.NULL);
		}

		return toolkit.createComposite(parent);
	}

	protected Label createEmptySpace(Composite parent, int span, FormToolkit toolkit) {
		Label label;

		if (toolkit != null) {
			label = toolkit.createLabel(parent, null);
		}
		else {
			label = new Label(parent, SWT.NULL);
		}

		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		gd.horizontalSpan = span;
		gd.widthHint = 0;
		gd.heightHint = 0;
		label.setLayoutData(gd);

		return label;
	}

	protected void updateEnabledState() {
	}

	private boolean _enabled = true;

}