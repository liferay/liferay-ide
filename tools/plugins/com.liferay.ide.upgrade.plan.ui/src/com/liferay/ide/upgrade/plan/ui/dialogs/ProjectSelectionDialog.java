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

package com.liferay.ide.upgrade.plan.ui.dialogs;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.LiferayUIPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * @author Terry Jia
 */
public class ProjectSelectionDialog extends SelectionStatusDialog {

	public ProjectSelectionDialog(Shell parentShell, ViewerFilter filter, boolean selectAllDefault) {
		super(parentShell);

		_filter = filter;
		_selectAllDefault = selectAllDefault;

		setTitle("Project Selection");
		setMessage("Select project");
	}

	@Override
	public void create() {
		super.create();

		Object[] checkedElements = tableViewer.getCheckedElements();

		if (ListUtil.isEmpty(checkedElements)) {
			getOkButton().setEnabled(false);
		}
	}

	protected void computeResult() {
		Object[] checkedElements = tableViewer.getCheckedElements();

		setSelectionResult(checkedElements);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);

		Font font = parent.getFont();

		composite.setFont(font);

		createMessageArea(composite);

		tableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);

		tableViewer.addPostSelectionChangedListener(
			new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					Object element = event.getSource();

					Object[] checkedElements = ((CheckboxTableViewer)element).getCheckedElements();

					getOkButton().setEnabled(checkedElements.length != 0);
				}

			});

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);

		data.heightHint = 250;
		data.widthHint = 300;

		Table table = tableViewer.getTable();

		table.setLayoutData(data);

		tableViewer.setLabelProvider(new JavaElementLabelProvider());
		tableViewer.setContentProvider(new ProjectsContentProvider());
		tableViewer.setComparator(new JavaElementComparator());

		Control control = tableViewer.getControl();

		control.setFont(font);

		if (_filter != null) {
			tableViewer.addFilter(_filter);
		}

		tableViewer.setInput(CoreUtil.getWorkspaceRoot());

		if (_selectAllDefault && ListUtil.isNotEmpty(table.getItems())) {
			tableViewer.setAllChecked(true);
		}

		updateStatus(new Status(IStatus.OK, LiferayUIPlugin.PLUGIN_ID, StringPool.EMPTY));

		setSelectionResult(new Object[0]);

		Dialog.applyDialogFont(composite);

		return composite;
	}

	protected CheckboxTableViewer tableViewer;

	private ViewerFilter _filter;
	private boolean _selectAllDefault;

}