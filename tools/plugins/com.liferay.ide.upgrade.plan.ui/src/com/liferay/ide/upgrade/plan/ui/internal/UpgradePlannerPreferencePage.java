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

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;
import com.liferay.ide.upgrade.plan.core.UpgradePlanOutline;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
 * @author Simon Jiang
 */
public class UpgradePlannerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public UpgradePlannerPreferencePage() {
		super("Upgrade Planner");

		_preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, UpgradePlanCorePlugin.ID);
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

		_createTableColumn(
			_upgradePlanOutlineTableViewer, "Outline Name", 50, null,
			element -> {
				UpgradePlanOutline outline = (UpgradePlanOutline)element;

				return outline.getName();
			});

		_createTableColumn(
			_upgradePlanOutlineTableViewer, "Outline Url", 150, null,
			element -> {
				UpgradePlanOutline outline = (UpgradePlanOutline)element;

				return outline.getLocation();
			});

		Table table = _upgradePlanOutlineTableViewer.getTable();

		table.setHeaderVisible(true);

		gridData.heightHint = 400;

		table.setLayoutData(gridData);

		_upgradePlanOutlineTableViewer.setContentProvider(new CutomizedOutlineContentProvider());

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
						String name = addOutlineDialog.getName();

						UpgradePlanOutline newOutline = new UpgradePlanOutline(name, url, false);

						_outlines.add(newOutline);

						_upgradePlanOutlineTableViewer.add(newOutline);
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

					UpgradePlanOutline outline = (UpgradePlanOutline)selection.getFirstElement();

					_outlines.remove(outline);

					_upgradePlanOutlineTableViewer.setInput(_outlines.toArray());
				}

			});

		setButtonLayoutData(_removeButton);

		_loadCustomerOutline();

		return composite;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return _preferenceStore;
	}

	@Override
	public void init(IWorkbench workbench) {
		String outlineString = _preferenceStore.getString(UpgradePlanCorePlugin.CUSTOMER_OUTLINE_KEY);

		if (CoreUtil.isNullOrEmpty(outlineString)) {
			return;
		}

		List<String> outlineValueList = StringUtil.stringToList(outlineString, "|");

		if (ListUtil.isEmpty(outlineValueList)) {
			return;
		}

		_outlines.addAll(
			Lists.transform(
				outlineValueList,
				new Function<String, UpgradePlanOutline>() {

					@Override
					public UpgradePlanOutline apply(String input) {
						String[] outlineValue = StringUtil.stringToArray(input, ",");

						return new UpgradePlanOutline(
							outlineValue[0].trim(), outlineValue[1].trim(), Boolean.getBoolean(outlineValue[2].trim()));
					}

				}));

		try {
			UIUtil.async(
				() -> {
					if (_upgradePlanOutlineTableViewer == null) {
						return;
					}

					Table upgradePlanTable = _upgradePlanOutlineTableViewer.getTable();

					if (upgradePlanTable.isDisposed()) {
						return;
					}

					_upgradePlanOutlineTableViewer.setInput(
						_outlines.toArray(new UpgradePlanOutline[_outlines.size()]));

					Stream.of(
						upgradePlanTable.getColumns()
					).forEach(
						obj -> obj.pack()
					);
				});
		}
		catch (Exception e) {
			UpgradePlanUIPlugin.logError(e);
		}
	}

	@Override
	public boolean performOk() {
		String outlineString = StringUtil.objectToString(_outlines, "|");

		_preferenceStore.setValue(UpgradePlanCorePlugin.CUSTOMER_OUTLINE_KEY, outlineString);

		try {
			_preferenceStore.save();
		}
		catch (IOException ioe) {
			UpgradePlanUIPlugin.logError("Can not save outline preference", ioe);
		}

		return super.performOk();
	}

	private static void _createTableColumn(
		TableViewer tableViewer, String name, int width, Function<Object, Image> imageProvider,
		Function<Object, String> textProvider) {

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);

		TableColumn tableColumn = tableViewerColumn.getColumn();

		tableColumn.setText(name);

		if (width > -1) {
			tableColumn.setWidth(width);
		}

		tableColumn.pack();

		tableViewerColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public Image getImage(Object element) {
					if (imageProvider == null) {
						return null;
					}

					return imageProvider.apply(element);
				}

				@Override
				public String getText(Object element) {
					if (textProvider == null) {
						return null;
					}

					return textProvider.apply(element);
				}

			});
	}

	private void _loadCustomerOutline() {
		try {
			UIUtil.async(
				() -> {
					if (_upgradePlanOutlineTableViewer == null) {
						return;
					}

					Table upgradePlanTable = _upgradePlanOutlineTableViewer.getTable();

					if (upgradePlanTable.isDisposed()) {
						return;
					}

					_upgradePlanOutlineTableViewer.setInput(
						_outlines.toArray(new UpgradePlanOutline[_outlines.size()]));

					Stream.of(
						upgradePlanTable.getColumns()
					).forEach(
						obj -> obj.pack()
					);
				});
		}
		catch (Exception e) {
			UpgradePlanUIPlugin.logError(e);
		}
	}

	private Button _addButton;
	private Collection<UpgradePlanOutline> _outlines = new ArrayList<>();
	private final ScopedPreferenceStore _preferenceStore;
	private Button _removeButton;
	private TableViewer _upgradePlanOutlineTableViewer;

	private class CutomizedOutlineContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof UpgradePlanOutline[]) {
				return (UpgradePlanOutline[])inputElement;
			}

			return new Object[] {inputElement};
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

}