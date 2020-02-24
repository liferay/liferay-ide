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
import com.liferay.ide.upgrade.plan.core.UpgradePlan;
import com.liferay.ide.upgrade.plan.core.UpgradePlanner;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;
import com.liferay.ide.upgrade.problems.core.UpgradeProblemSupport;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
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
 * @author Seiphon Wang
 */
public class UpgradeProblemsPreferencePage
	extends PreferencePage implements IWorkbenchPreferencePage, UpgradeProblemSupport {

	public UpgradeProblemsPreferencePage() {
		super("Ignored Upgrade Problems");

		Bundle bundle = FrameworkUtil.getBundle(UpgradeProblemsPreferencePage.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTracker = new ServiceTracker<>(bundleContext, UpgradePlanner.class, null);

		_serviceTracker.open();
	}

	@Override
	public Control createContents(Composite parent) {
		Composite pageComposite = new Composite(parent, SWT.NULL);

		GridLayout gridLayout = new GridLayout();

		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;

		pageComposite.setLayout(gridLayout);

		GridData gridData = new GridData();

		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		pageComposite.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);

		Label label = new Label(pageComposite, SWT.LEFT);

		label.setText(
			"These are ignored breaking change problems.\n You can remove them if you want to show this type of " +
				"problem next time.");

		gridData = new GridData();

		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;

		label.setLayoutData(gridData);

		_ignoredProblemsTableViewer = new TableViewer(pageComposite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);

		gridData = new GridData(GridData.FILL_HORIZONTAL);

		_createColumns();

		Table table = _ignoredProblemsTableViewer.getTable();

		table.setHeaderVisible(true);

		gridData.heightHint = 200;

		table.setLayoutData(gridData);

		_ignoredProblemsTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		Composite groupComponent = new Composite(pageComposite, SWT.NULL);

		GridLayout groupLayout = new GridLayout();

		groupLayout.marginWidth = 0;
		groupLayout.marginHeight = 0;
		groupComponent.setLayout(groupLayout);

		gridData = new GridData();

		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		groupComponent.setLayoutData(gridData);

		_removeButton = new Button(groupComponent, SWT.PUSH);

		_removeButton.setText("Remove");

		_removeButton.addSelectionListener(
			new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent selectionEvent) {
				}

				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					StructuredSelection selection = (StructuredSelection)_ignoredProblemsTableViewer.getSelection();

					if ((selection != null) && (selection.getFirstElement() instanceof UpgradeProblem)) {
						try {
							UpgradeProblem problem = (UpgradeProblem)selection.getFirstElement();

							problem.setStatus(UpgradeProblem.STATUS_NOT_RESOLVED);

							_restoreProblems.add(problem);

							_ignoreProblems.remove(problem);

							_ignoredProblemsTableViewer.setInput(_ignoreProblems.toArray(new UpgradeProblem[0]));

							UIUtil.async(
								() -> {
									_browser.setText("");
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

		label = new Label(pageComposite, SWT.LEFT);
		gridData = new GridData();

		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;

		label.setLayoutData(gridData);

		_detailInfoLabel = new Label(pageComposite, SWT.LEFT);

		_detailInfoLabel.setText("Detail Information");

		gridData = new GridData();

		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;

		_detailInfoLabel.setLayoutData(gridData);

		_browser = new Browser(pageComposite, SWT.BORDER);

		gridData = new GridData(GridData.FILL_BOTH);

		_browser.setLayoutData(gridData);

		groupComponent = new Composite(pageComposite, SWT.NULL);

		groupLayout = new GridLayout();

		groupLayout.marginWidth = 0;
		groupLayout.marginHeight = 0;

		groupComponent.setLayout(groupLayout);

		gridData = new GridData();

		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		groupComponent.setLayoutData(gridData);

		_ignoredProblemsTableViewer.setInput(_ignoreProblems.toArray(new UpgradeProblem[0]));

		_ignoredProblemsTableViewer.addSelectionChangedListener(
			event -> {
				UIUtil.async(
					() -> {
						_updateForm(event);
					},
					50);
			});

		return pageComposite;
	}

	@Override
	public void dispose() {
		_serviceTracker.close();

		super.dispose();
	}

	@Override
	public void init(IWorkbench workbench) {
		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

		if (upgradePlan == null) {
			return;
		}

		_ignoreProblems = upgradePlan.getIgnoredProblems();
	}

	@Override
	public boolean performOk() {
		UpgradePlanner upgradePlanner = _serviceTracker.getService();

		UpgradePlan upgradePlan = upgradePlanner.getCurrentUpgradePlan();

		upgradePlan.addUpgradeProblems(_restoreProblems);

		upgradePlan.removeIgnoredProblems(_restoreProblems);

		addMarkers(_restoreProblems);

		upgradePlanner.saveUpgradePlan(upgradePlan);

		UIUtil.refreshCommonView("org.eclipse.ui.navigator.ProjectExplorer");

		return super.performOk();
	}

	private void _createColumns() {
		String[] titles = {"Tickets", "Problem"};

		int[] bounds = {65, 55};

		TableViewerColumn tableViewerColumn = _createTableViewerColumn(
			titles[0], bounds[0], _ignoredProblemsTableViewer);

		tableViewerColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					UpgradeProblem upgradeProblem = (UpgradeProblem)element;

					return upgradeProblem.getTicket();
				}

			});

		tableViewerColumn = _createTableViewerColumn(titles[1], bounds[1], _ignoredProblemsTableViewer);

		tableViewerColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					UpgradeProblem upgradeProblem = (UpgradeProblem)element;

					return upgradeProblem.getTitle();
				}

				@Override
				public void update(ViewerCell viewerCell) {
					super.update(viewerCell);

					Table table = _ignoredProblemsTableViewer.getTable();

					TableColumn tableColumn = table.getColumn(1);

					tableColumn.pack();
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

	private void _updateForm(SelectionChangedEvent selectionChangedEvent) {
		ISelection selection = selectionChangedEvent.getSelection();

		IStructuredSelection structuredSelection = Adapters.adapt(selection, IStructuredSelection.class);

		if (structuredSelection == null) {
			return;
		}

		UpgradeProblem selectedProblem = Adapters.adapt(structuredSelection.getFirstElement(), UpgradeProblem.class);

		if (selectedProblem == null) {
			return;
		}

		if (CoreUtil.isNullOrEmpty(selectedProblem.getHtml())) {
			_browser.setText(_generateFormText(selectedProblem));
		}
		else {
			_browser.setText(selectedProblem.getHtml());
		}
	}

	private Browser _browser;
	private Label _detailInfoLabel;
	private TableViewer _ignoredProblemsTableViewer;
	private Collection<UpgradeProblem> _ignoreProblems = new HashSet<>();
	private Button _removeButton;
	private Collection<UpgradeProblem> _restoreProblems = new HashSet<>();
	private final ServiceTracker<UpgradePlanner, UpgradePlanner> _serviceTracker;

}