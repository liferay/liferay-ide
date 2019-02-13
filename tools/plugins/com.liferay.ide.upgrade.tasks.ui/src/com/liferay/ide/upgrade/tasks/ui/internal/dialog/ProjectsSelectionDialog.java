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

package com.liferay.ide.upgrade.tasks.ui.internal.dialog;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.LiferayUIPlugin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
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
 * @author Gregory Amerson
 */
public class ProjectsSelectionDialog extends SelectionStatusDialog {

	public ProjectsSelectionDialog(
		Shell parentShell, ViewerFilter viewerFilter, boolean initialSelectAll, String message) {

		super(parentShell);

		_filter = viewerFilter;
		_initialSelectAll = initialSelectAll;

		setMessage(message);
		setTitle("Project Selection");
	}

	@Override
	public void create() {
		super.create();

		Object[] checkedElements = _tableViewer.getCheckedElements();

		if (ListUtil.isEmpty(checkedElements)) {
			getOkButton().setEnabled(false);
		}
	}

	public List<IProject> getSelectedProjects() {
		return Stream.of(
			getResult()
		).map(
			IProject.class::cast
		).collect(
			Collectors.toList()
		);
	}

	protected void computeResult() {
		Object[] checkedElements = _tableViewer.getCheckedElements();

		setSelectionResult(checkedElements);
	}

	protected Control createDialogArea(Composite parentComposite) {
		Composite composite = (Composite)super.createDialogArea(parentComposite);

		Font font = parentComposite.getFont();

		composite.setFont(font);

		createMessageArea(composite);

		_tableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);

		_tableViewer.addPostSelectionChangedListener(
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

		Table table = _tableViewer.getTable();

		table.setLayoutData(data);

		_tableViewer.setLabelProvider(new JavaElementLabelProvider());
		_tableViewer.setContentProvider(new ProjectsContentProvider());
		_tableViewer.setComparator(new JavaElementComparator());

		Control control = _tableViewer.getControl();

		control.setFont(font);

		if (_filter != null) {
			_tableViewer.addFilter(_filter);
		}

		_tableViewer.setInput(CoreUtil.getWorkspaceRoot());

		if (_initialSelectAll && ListUtil.isNotEmpty(table.getItems())) {
			_tableViewer.setAllChecked(true);
		}

		updateStatus(new Status(IStatus.OK, LiferayUIPlugin.PLUGIN_ID, StringPool.EMPTY));

		setSelectionResult(new Object[0]);

		Dialog.applyDialogFont(composite);

		return composite;
	}

	private ViewerFilter _filter;
	private boolean _initialSelectAll;
	private CheckboxTableViewer _tableViewer;

}