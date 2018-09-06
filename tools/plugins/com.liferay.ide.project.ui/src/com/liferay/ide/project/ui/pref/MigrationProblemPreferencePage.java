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

package com.liferay.ide.project.ui.pref;

import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.upgrade.IgnoredProblemsContainer;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.migration.MigrationUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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

/**
 * @author Lovett Li
 */
public class MigrationProblemPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.liferay.ide.project.ui.migrationProblemPreferencePage";

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
				public void widgetSelected(SelectionEvent arg0) {
					StructuredSelection selection = (StructuredSelection)_ignoredProblemTable.getSelection();

					if ((selection != null) && (selection.getFirstElement() instanceof Problem)) {
						try {
							Problem problem = (Problem)selection.getFirstElement();

							_mpContainer.remove(problem);

							Map<String, Problem> problemMap = _mpContainer.getProblemMap();

							Collection<Problem> problems = problemMap.values();

							_ignoredProblemTable.setInput(problems.toArray(new Problem[0]));
						}
						catch (Exception e) {
							ProjectUI.logError(e);
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

		_fillIgnoredProblemTable();

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
	public void init(IWorkbench arg0) {
		_mpContainer = MigrationUtil.getIgnoredProblemsContainer();
	}

	@Override
	public boolean performOk() {
		try {
			UpgradeAssistantSettingsUtil.setObjectToStore(IgnoredProblemsContainer.class, _mpContainer);
		}
		catch (IOException ioe) {
			ProjectUI.logError(ioe);
		}

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
					Problem p = (Problem)element;

					return p.getTicket();
				}

			});

		col = _createTableViewerColumn(titles[1], bounds[1], problemsViewer);

		col.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					Problem p = (Problem)element;

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

	private void _fillIgnoredProblemTable() {
		try {
			IgnoredProblemsContainer mpContainer = UpgradeAssistantSettingsUtil.getObjectFromStore(
				IgnoredProblemsContainer.class);

			if (mpContainer != null) {
				Map<String, Problem> problemMap = mpContainer.getProblemMap();

				Collection<Problem> p = problemMap.values();

				Problem[] problems = p.toArray(new Problem[0]);

				_ignoredProblemTable.setInput(problems);
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private String _generateFormText(Problem problem) {
		StringBuilder sb = new StringBuilder();

		sb.append("<form><p>");

		sb.append("<b>Problem:</b> ");
		sb.append(problem.title);
		sb.append("<br/><br/>");

		sb.append("<b>Description:</b><br/>");
		sb.append("\t");
		sb.append(problem.summary);
		sb.append("<br/><br/>");

		if ((problem.getAutoCorrectContext() != null) && (problem.autoCorrectContext.length() > 0)) {
			sb.append("<a href='autoCorrect'>Correct this problem automatically</a><br/><br/>");
		}

		if ((problem.html != null) && (problem.html.length() > 0)) {
			sb.append("<a href='html'>See documentation for how to correct this problem.</a><br/><br/>");
		}

		if ((problem.ticket != null) && (problem.ticket.length() > 0)) {
			sb.append("<b>Tickets:</b> ");
			sb.append(_getLinkTags(problem.ticket));
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

		final Problem problem = MigrationUtil.getProblemFromSelection(selection);

		if (problem != null) {
			if (CoreUtil.isNullOrEmpty(problem.html)) {
				_browser.setText(_generateFormText(problem));
			}
			else {
				_browser.setText(problem.html);
			}
		}
		else {
			_browser.setUrl("about:blank");
		}
	}

	private Browser _browser;
	private Label _detailInfoLabel;
	private TableViewer _ignoredProblemTable;
	private IgnoredProblemsContainer _mpContainer;
	private Button _removeButton;

}