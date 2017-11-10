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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Gregory Amerson
 */
public class TablePart extends StructuredViewerPart {

	/**
	 * Constructor for TablePart.
	 *
	 * @param buttonLabels
	 */
	public TablePart(String[] buttonLabels) {
		super(buttonLabels);
	}

	public TableViewer getTableViewer() {
		return (TableViewer)getViewer();
	}

	/**
	 * @see SharedPartWithButtons#buttonSelected(int)
	 */
	protected void buttonSelected(Button button, int index) {
	}

	/**
	 * @see StructuredViewerPart#createStructuredViewer(Composite,
	 * FormWidgetFactory)
	 */
	protected StructuredViewer createStructuredViewer(Composite parent, int style, FormToolkit toolkit) {
		style |= SWT.H_SCROLL | SWT.V_SCROLL;

		if (toolkit == null) {
			style |= SWT.BORDER;
		}
		else {
			style |= toolkit.getBorderStyle();
		}

		TableViewer tableViewer = new TableViewer(parent, style);

		tableViewer.addSelectionChangedListener(
			new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent e) {
					TablePart.this.selectionChanged((IStructuredSelection)e.getSelection());
				}

			});
		tableViewer.addDoubleClickListener(
			new IDoubleClickListener() {

				public void doubleClick(DoubleClickEvent e) {
					TablePart.this.handleDoubleClick((IStructuredSelection)e.getSelection());
				}

			});

		return tableViewer;
	}

	protected void handleDoubleClick(IStructuredSelection selection) {
	}

	protected void selectionChanged(IStructuredSelection selection) {
	}

}