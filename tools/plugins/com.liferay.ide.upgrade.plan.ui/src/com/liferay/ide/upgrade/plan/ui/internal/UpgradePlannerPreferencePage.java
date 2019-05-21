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

import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Terry Jia
 */
public class UpgradePlannerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public UpgradePlannerPreferencePage() {
		super("Upgrade Planner");

		_preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, "com.liferay.ide.upgrade.plan.core");
	}

	@Override
	public Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		GridLayout gridLayout = new GridLayout();

		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;

		composite.setLayout(gridLayout);

		GridData gridData = new GridData();

		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		composite.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);

		Label label = new Label(composite, SWT.LEFT);

		label.setText("Configure Upgrade Planner outlines");

		gridData = new GridData();

		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;

		label.setLayoutData(gridData);

		_upgradePlanOutlineTableViewer = new TableViewer(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);

		gridData = new GridData(GridData.FILL_HORIZONTAL);

		_createColumns();

		Table table = _upgradePlanOutlineTableViewer.getTable();

		table.setHeaderVisible(true);

		gridData.heightHint = 400;

		table.setLayoutData(gridData);

		_upgradePlanOutlineTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		_upgradePlanOutlineTableViewer.setInput(_outlines.toArray());

		Composite groupComponent = new Composite(composite, SWT.NULL);

		GridLayout groupLayout = new GridLayout();

		groupLayout.marginWidth = 0;
		groupLayout.marginHeight = 0;
		groupComponent.setLayout(groupLayout);

		gridData = new GridData();

		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		groupComponent.setLayoutData(gridData);

		_addButton = new Button(groupComponent, SWT.PUSH);

		_addButton.setText("Add");

		_addButton.addSelectionListener(
			new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent selectionEvent) {
				}

				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					AddOutlineDialog addOutlineDialog = new AddOutlineDialog(getShell());

					addOutlineDialog.create();

					if (addOutlineDialog.open() == Window.OK) {
						String url = addOutlineDialog.getURL();

						_outlines.add(url);

						_upgradePlanOutlineTableViewer.add(url);
					}
				}

			});

		setButtonLayoutData(_addButton);

		_removeButton = new Button(groupComponent, SWT.PUSH);

		_removeButton.setText("Remove");

		_removeButton.addSelectionListener(
			new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent selectionEvent) {
				}

				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					StructuredSelection selection = (StructuredSelection)_upgradePlanOutlineTableViewer.getSelection();

					String outline = (String)selection.getFirstElement();

					_outlines.remove(outline);

					_upgradePlanOutlineTableViewer.setInput(_outlines.toArray());
				}

			});

		setButtonLayoutData(_removeButton);

		return composite;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return _preferenceStore;
	}

	@Override
	public void init(IWorkbench workbench) {
		String outlines = _preferenceStore.getString("outlines");

		if ("".equals(outlines)) {
			_outlines.addAll(UpgradePlanCorePlugin.defaultUpgradePlanOutlines);
		}
		else {
			String[] outlineArray = outlines.split(",");

			Collections.addAll(_outlines, outlineArray);
		}
	}

	@Override
	public boolean performOk() {
		String outlines = StringUtil.merge(_outlines.toArray(new String[0]), ",");

		_preferenceStore.setValue("outlines", outlines);

		try {
			_preferenceStore.save();
		}
		catch (IOException ioe) {
			UpgradePlanUIPlugin.logError("Can not save outline preference", ioe);
		}

		return super.performOk();
	}

	private void _createColumns() {
		TableViewerColumn tableViewerColumn = _createTableViewerColumn("URL", 100, _upgradePlanOutlineTableViewer);

		tableViewerColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					return (String)element;
				}

			});
	}

	private TableViewerColumn _createTableViewerColumn(String title, int bound, TableViewer viewer) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);

		TableColumn tableColumn = tableViewerColumn.getColumn();

		tableColumn.setText(title);
		tableColumn.setWidth(bound);
		tableColumn.setResizable(true);
		tableColumn.setMoveable(true);

		return tableViewerColumn;
	}

	private Button _addButton;
	private List<String> _outlines = new ArrayList<>();
	private final ScopedPreferenceStore _preferenceStore;
	private Button _removeButton;
	private TableViewer _upgradePlanOutlineTableViewer;

}