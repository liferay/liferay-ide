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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeEvent;
import com.liferay.ide.upgrade.plan.core.UpgradeListener;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanStartedEvent;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.ui.util.SWTUtil;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.nio.file.Path;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Simon Jiang
 */
public class SwitchUpgradePlanCustomPart extends FormComponentPart implements UpgradeListener {

	public SwitchUpgradePlanCustomPart() {
		Bundle bundle = FrameworkUtil.getBundle(UpgradePlanViewer.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();

		_upgradePlanner = _serviceTracker.getService();

		_upgradePlanner.addListener(this);
	}

	@Override
	public FormComponentPresentation createPresentation(SwtPresentation parent, Composite composite) {
		return new FormComponentPresentation(this, parent, composite) {

			@Override
			public void render() {
				final Composite composite = SWTUtil.createComposite(composite(), 2, 2, GridData.FILL_BOTH);

				final Table table = new Table(composite, SWT.FULL_SELECTION);

				final GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);

				tableData.grabExcessVerticalSpace = true;
				tableData.grabExcessHorizontalSpace = true;
				tableData.horizontalAlignment = SWT.FILL;

				tableData.heightHint = 225;
				tableData.widthHint = 550;

				table.setLayoutData(tableData);

				table.setHeaderVisible(true);
				table.setLinesVisible(true);

				_tableViewer = new TableViewer(table);

				_tableViewer.setContentProvider(new SwitchUpgradePlanContentProvider());
				_tableViewer.addDoubleClickListener(
					event -> {
						ISelection selection = event.getSelection();

						IStructuredSelection currentSelection = (IStructuredSelection)selection;

						UpgradePlan upgradePlan = (UpgradePlan)currentSelection.getFirstElement();

						_currentUpgradePlan = upgradePlan;

						_enableButtons(upgradePlan);

						_handleDoubleClick(upgradePlan);
					});

				_createTableColumn(
					_tableViewer, "", -1,
					element -> {
						UpgradePlan upgradePlan = (UpgradePlan)element;

						UpgradePlan currentUpgradePlan = _upgradePlanner.getCurrentUpgradePlan();

						if (_currentUpgradePlan != null) {
							currentUpgradePlan = _currentUpgradePlan;
						}

						if (upgradePlan.equals(currentUpgradePlan)) {
							return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.CHECKED_IMAGE_ID);
						}

						return UpgradePlanUIPlugin.getImage(UpgradePlanUIPlugin.UNCHECKED_IMAGE_ID);
					},
					element -> null);

				_createTableColumn(
					_tableViewer, "Name", 50, null,
					element -> {
						UpgradePlan upgradePlan = (UpgradePlan)element;

						return upgradePlan.getName();
					});

				_createTableColumn(
					_tableViewer, "Current Version", 50, null,
					element -> {
						UpgradePlan upgradePlan = (UpgradePlan)element;

						return upgradePlan.getCurrentVersion();
					});

				_createTableColumn(
					_tableViewer, "Target Version", 50, null,
					element -> {
						UpgradePlan upgradePlan = (UpgradePlan)element;

						return upgradePlan.getTargetVersion();
					});

				_createTableColumn(
					_tableViewer, "Outline", 50, null,
					element -> {
						UpgradePlan upgradePlan = (UpgradePlan)element;

						return upgradePlan.getUpgradePlanOutline();
					});

				_createTableColumn(
					_tableViewer, "Current Location", 200, null,
					element -> {
						UpgradePlan upgradePlan = (UpgradePlan)element;

						Path currentProjectLocation = upgradePlan.getCurrentProjectLocation();

						String projectLocationString = currentProjectLocation.toString();

						if (CoreUtil.isNotNullOrEmpty(projectLocationString)) {
							return projectLocationString;
						}

						return "";
					});

				_createTableColumn(
					_tableViewer, "Target Location", 200, null,
					element -> {
						UpgradePlan upgradePlan = (UpgradePlan)element;

						Path targetProjectLocation = upgradePlan.getTargetProjectLocation();

						String projectLocationString = targetProjectLocation.toString();

						if (CoreUtil.isNotNullOrEmpty(projectLocationString)) {
							return projectLocationString;
						}

						return "";
					});

				_buttons.add(
					_createButton(
						composite, "Start Plan", " Start this upgrade plan.", event -> _handleStartPlanEvent()));
				_buttons.add(
					_createButton(
						composite, "Remove Plan", " Remove this upgrade plan.", event -> _handleRemovePlanEvent()));

				Button closeButton = _createButton(
					composite, "Close", "Close this dialog",
					event -> {
						Shell shell = composite.getShell();

						shell.close();
					});

				closeButton.setEnabled(true);

				_tableViewer.addSelectionChangedListener(
					event -> {
						IStructuredSelection currentSelection = event.getStructuredSelection();

						UpgradePlan upgradePlan = Adapters.adapt(currentSelection.getFirstElement(), UpgradePlan.class);

						_enableButtons(upgradePlan);
					});

				_loadUpgradePlans();
			}

		};
	}

	@Override
	public void dispose() {
		_serviceTracker.close();

		super.dispose();
	}

	@Override
	public void onUpgradeEvent(UpgradeEvent upgradeEvent) {
		if (upgradeEvent instanceof UpgradePlanStartedEvent) {
			UpgradePlanStartedEvent upgradePlanStartedEvent = (UpgradePlanStartedEvent)upgradeEvent;

			_currentUpgradePlan = upgradePlanStartedEvent.getUpgradePlan();

			_loadUpgradePlans();
		}
	}

	private static Button _createButton(Composite composite, String text, String tooltip, Listener listener) {
		Button button = new Button(composite, SWT.PUSH | SWT.BORDER);

		button.addListener(SWT.Selection, listener);
		button.setEnabled(false);
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		button.setText(text);
		button.setToolTipText(tooltip);

		return button;
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

	private void _enableButtons(UpgradePlan upgradePlan) {
		if ((upgradePlan == null) || upgradePlan.equals(_currentUpgradePlan)) {
			_buttons.forEach(button -> button.setEnabled(false));
		}
		else {
			_buttons.forEach(button -> button.setEnabled(true));
		}
	}

	private void _handleDoubleClick(UpgradePlan upgradePlan) {
		_upgradePlanner.startUpgradePlan(upgradePlan);
	}

	private void _handleRemovePlanEvent() {
		ISelection selection = _tableViewer.getSelection();

		if (selection != null) {
			IStructuredSelection selected = (IStructuredSelection)selection;

			UpgradePlan upgradePlan = (UpgradePlan)selected.getFirstElement();

			if (_currentUpgradePlan == null) {
				return;
			}

			if (upgradePlan.equals(_currentUpgradePlan)) {
				return;
			}

			_upgradePlanner.removeUpgradePlan(upgradePlan);

			_loadUpgradePlans();
		}
	}

	private void _handleStartPlanEvent() {
		ISelection selection = _tableViewer.getSelection();

		if (selection != null) {
			IStructuredSelection currentSelection = (IStructuredSelection)selection;

			UpgradePlan upgradePlan = (UpgradePlan)currentSelection.getFirstElement();

			_handleDoubleClick(upgradePlan);
		}
	}

	private void _loadUpgradePlans() {
		try {
			List<UpgradePlan> loadUpgradePlans = _upgradePlanner.loadAllUpgradePlans();

			if (_currentUpgradePlan == null) {
				_currentUpgradePlan = _upgradePlanner.getCurrentUpgradePlan();
			}

			UIUtil.async(
				() -> {
					if (_tableViewer == null) {
						return;
					}

					Table upgradePlanTable = _tableViewer.getTable();

					if (upgradePlanTable.isDisposed()) {
						return;
					}

					_tableViewer.setInput(loadUpgradePlans.toArray(new UpgradePlan[loadUpgradePlans.size()]));

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

	private Set<Button> _buttons = new HashSet<>();
	private UpgradePlan _currentUpgradePlan;
	private ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private TableViewer _tableViewer;
	private UpgradePlanner _upgradePlanner;

	private class SwitchUpgradePlanContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof UpgradePlan[]) {
				return (UpgradePlan[])inputElement;
			}

			return new Object[] {inputElement};
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

}