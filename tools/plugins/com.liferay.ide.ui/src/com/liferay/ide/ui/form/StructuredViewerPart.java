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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Gregory Amerson
 */
public abstract class StructuredViewerPart extends SharedPartWithButtons {

	public StructuredViewerPart(String[] buttonLabels) {
		super(buttonLabels);
	}

	public Control getControl() {
		return _fViewer.getControl();
	}

	public StructuredViewer getViewer() {
		return _fViewer;
	}

	public void setMinimumSize(int width, int height) {
		_fMinSize = new Point(width, height);

		if (_fViewer != null) {
			_applyMinimumSize();
		}
	}

	protected void createMainControl(Composite parent, int style, int span, FormToolkit toolkit) {
		_fViewer = createStructuredViewer(parent, style, toolkit);

		Control control = _fViewer.getControl();

		GridData gd = new GridData(GridData.FILL_BOTH);

		gd.horizontalSpan = span;
		control.setLayoutData(gd);

		_applyMinimumSize();
	}

	protected abstract StructuredViewer createStructuredViewer(Composite parent, int style, FormToolkit toolkit);

	protected void updateEnabledState() {
		getControl().setEnabled(isEnabled());
		super.updateEnabledState();
	}

	private void _applyMinimumSize() {
		if (_fMinSize != null) {
			Control control = _fViewer.getControl();

			GridData gd = (GridData)control.getLayoutData();

			gd.widthHint = _fMinSize.x;
			gd.heightHint = _fMinSize.y;
		}
	}

	private Point _fMinSize;
	private StructuredViewer _fViewer;

}