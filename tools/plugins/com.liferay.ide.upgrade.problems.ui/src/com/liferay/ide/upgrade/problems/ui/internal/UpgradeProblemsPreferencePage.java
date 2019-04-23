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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Lovett Li
 * @author Simon Jiang
 */
public class UpgradeProblemsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public UpgradeProblemsPreferencePage() {
		super("Ignored Upgrade Problems");

		Bundle bundle = FrameworkUtil.getBundle(UpgradeProblemsPreferencePage.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	public Control createContents(Composite parent) {
		Composite pageComponent = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();

		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		pageComponent.setLayout(layout);

		GridData data = new GridData();

		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		pageComponent.setLayoutData(data);

		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);

		Label label = new Label(pageComponent, SWT.LEFT);

		label.setText(
			"These are ignored breaking change problems.\n You can remove them if you want to show this type of " +
				"problem next time.");

		data = new GridData();

		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;

		label.setLayoutData(data);

		_ignoredProblemTable = new TableViewer(pageComponent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);

		data = new GridData(GridData.FILL_HORIZONTAL);

		_createColumns(_ignoredProblemTable);

		Table table = _ignoredProblemTable.getTable();

		table.setHeaderVisible(true);

		data.heightHint = 200;

		table.setLayoutData(data);

		_ignoredProblemTable.setContentProvider(ArrayContentProvider.getInstance());

		Composite groupComponent = new Composite(pageComponent, SWT.NULL);
		GridLayout groupLayout = new GridLayout();

		groupLayout.marginWidth = 0;
		groupLayout.marginHeight = 0;
		groupComponent.setLayout(groupLayout);

		data = new GridData();

		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		groupComponent.setLayoutData(data);

		_removeButton = new Button(groupComponent, SWT.PUSH);

		_removeButton.setText("Remove");
		_removeButton.addSelectionListener(
			new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}

				@Override
				public void widgetSelected(SelectionEvent event) {
					StructuredSelection selection = (StructuredSelection)_ignoredProblemTable.getSelection();

					if ((selection != null) && (selection.getFirstElement() instanceof UpgradeProblem)) {
						try {
							UpgradeProblem problem = (UpgradeProblem)selection.getFirstElement();

							_restoreProblems.add(problem);

							_ignoreProblems.remove(problem);

							_ignoredProblemTable.setInput(_ignoreProblems.toArray(new UpgradeProblem[0]));

							UIUtil.async(
								new Runnable() {

									public void run() {
										_browser.setText("");
									}

								},
								50);
						}
						catch (Exception e) {
							UpgradeProblemsUIPlugin.logError(e);
						}
					}
				}

			});

		setButtonLayoutData(_removeButton);

		label = new Label(pageComponent, SWT.LEFT);
		data = new GridData();

		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		_detailInfoLabel = new Label(pageComponent, SWT.LEFT);

		_detailInfoLabel.setText("Detail Information");

		data = new GridData();

		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		_detailInfoLabel.setLayoutData(data);

		_browser = new Browser(pageComponent, SWT.BORDER);

		data = new GridData(GridData.FILL_BOTH);

		_browser.setLayoutData(data);

		groupComponent = new Composite(pageComponent, SWT.NULL);

		groupLayout = new GridLayout();

		groupLayout.marginWidth = 0;
		groupLayout.marginHeight = 0;
		groupComponent.setLayout(groupLayout);

		data = new GridData();

		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;

		groupComponent.setLayoutData(data);

		_ignoredProblemTable.setInput(_ignoreProblems.toArray(new UpgradeProblem[0]));

		_ignoredProblemTable.addSelectionChangedListener(
			new ISelectionChangedListener() {

				public void selectionChanged(final SelectionChangedEvent event) {
					UIUtil.async(
						new Runnable() {

							public void run() {
								_updateForm(event);
							}

						},
						50);
				}

			});

		return pageComponent;
	}

	@Override
	public void dispose() {
		_serviceTracker.close();

		super.dispose();
	}

	@Override
	public void init(IWorkbench arg0) {
		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		_upgradePlan = upgradePlanner.getCurrentUpgradePlan();

		Collection<UpgradeProblem> upgradeProblems = _upgradePlan.getUpgradeProblems();

		_ignoreProblems = upgradeProblems.stream(
		).filter(
			problem -> UpgradeProblem.STATUS_IGNORE == problem.getStatus()
		).collect(
			Collectors.toSet()
		);
	}

	@Override
	public boolean performOk() {
		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		Collection<UpgradeProblem> upgradeProblems = _upgradePlan.getUpgradeProblems();

		upgradeProblems.stream(
		).filter(
			upgradeProblem -> _restoreProblems.contains(upgradeProblem)
		).forEach(
			upgradeProblem -> upgradeProblem.setStatus(UpgradeProblem.STATUS_NOT_RESOLVED)
		);

		upgradePlanner.saveUpgradePlan(_upgradePlan);

		UIUtil.refreshCommonView("org.eclipse.ui.navigator.ProjectExplorer");

		return super.performOk();
	}

	private void _createColumns(final TableViewer problemsViewer) {
		final String[] titles = {"Tickets", "Problem"};
		final int[] bounds = {65, 55};

		TableViewerColumn col = _createTableViewerColumn(titles[0], bounds[0], problemsViewer);

		col.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					UpgradeProblem p = (UpgradeProblem)element;

					return p.getTicket();
				}

			});

		col = _createTableViewerColumn(titles[1], bounds[1], problemsViewer);

		col.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					UpgradeProblem p = (UpgradeProblem)element;

					return p.getTitle();
				}

				@Override
				public void update(ViewerCell cell) {
					super.update(cell);

					Table table = problemsViewer.getTable();

					TableColumn column = table.getColumn(1);

					column.pack();
				}

			});
	}

	private TableViewerColumn _createTableViewerColumn(String title, int bound, TableViewer viewer) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);

		final TableColumn column = viewerColumn.getColumn();

		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);

		return viewerColumn;
	}

	private String _generateFormText(UpgradeProblem problem) {
		StringBuilder sb = new StringBuilder();

		sb.append("<form><p>");

		sb.append("<b>Problem:</b> ");
		sb.append(problem.getTitle());
		sb.append("<br/><br/>");

		sb.append("<b>Description:</b><br/>");
		sb.append("\t");
		sb.append(problem.getSummary());
		sb.append("<br/><br/>");

		String autoCorrectContext = problem.getAutoCorrectContext();

		if (CoreUtil.isNotNullOrEmpty(autoCorrectContext)) {
			sb.append("<a href='autoCorrect'>Correct this problem automatically</a><br/><br/>");
		}

		String problemHtml = problem.getHtml();

		if (CoreUtil.isNotNullOrEmpty(problemHtml)) {
			sb.append("<a href='html'>See documentation for how to correct this problem.</a><br/><br/>");
		}

		String problemTicket = problem.getTicket();

		if (CoreUtil.isNotNullOrEmpty(problemTicket)) {
			sb.append("<b>Tickets:</b> ");
			sb.append(_getLinkTags(problemTicket));
			sb.append("<br/><br/>");
		}

		sb.append("</p></form>");

		return sb.toString();
	}

	private String _getLinkTags(String ticketNumbers) {
		String[] ticketNumberArray = ticketNumbers.split(",");

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < ticketNumberArray.length; i++) {
			String ticketNumber = ticketNumberArray[i];
			sb.append("<a href='https://issues.liferay.com/browse/");
			sb.append(ticketNumber);
			sb.append("'>");
			sb.append(ticketNumber);
			sb.append("</a>");

			if ((ticketNumberArray.length > 1) && (i != (ticketNumberArray.length - 1))) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	private void _updateForm(SelectionChangedEvent event) {
		final ISelection selection = event.getSelection();

		final IStructuredSelection structuredSelection = Adapters.adapt(selection, IStructuredSelection.class);

		if (structuredSelection == null) {
			return;
		}

		UpgradeProblem selectedProblem = Adapters.adapt(structuredSelection.getFirstElement(), UpgradeProblem.class);

		if (selectedProblem == null) {
			return;
		}

		if (selectedProblem != null) {
			if (CoreUtil.isNullOrEmpty(selectedProblem.getHtml())) {
				_browser.setText(_generateFormText(selectedProblem));
			}
			else {
				_browser.setText(selectedProblem.getHtml());
			}
		}
	}

	private Browser _browser;
	private Label _detailInfoLabel;
	private TableViewer _ignoredProblemTable;
	private Collection<UpgradeProblem> _ignoreProblems = new HashSet<>();
	private Button _removeButton;
	private Collection<UpgradeProblem> _restoreProblems = new HashSet<>();
	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;
	private UpgradePlan _upgradePlan;

}