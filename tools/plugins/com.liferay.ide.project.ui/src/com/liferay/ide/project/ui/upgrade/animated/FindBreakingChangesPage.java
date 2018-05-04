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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.blade.api.MigrationConstants;
import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.project.core.upgrade.FileProblems;
import com.liferay.ide.project.core.upgrade.MigrationProblemsContainer;
import com.liferay.ide.project.core.upgrade.ProblemsContainer;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.migration.AutoCorrectAction;
import com.liferay.ide.project.ui.migration.AutoCorrectAllAction;
import com.liferay.ide.project.ui.migration.IgnoreAction;
import com.liferay.ide.project.ui.migration.IgnoreAlwaysAction;
import com.liferay.ide.project.ui.migration.MarkDoneAction;
import com.liferay.ide.project.ui.migration.MarkUndoneAction;
import com.liferay.ide.project.ui.migration.MigrationContentProvider;
import com.liferay.ide.project.ui.migration.MigrationLabelProvider;
import com.liferay.ide.project.ui.migration.MigrationUtil;
import com.liferay.ide.project.ui.migration.MigratorComparator;
import com.liferay.ide.project.ui.migration.RemoveAction;
import com.liferay.ide.project.ui.migration.RunMigrationToolAction;
import com.liferay.ide.project.ui.pref.MigrationProblemPreferencePage;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Lovett Li
 * @author Terry Jia
 */
public class FindBreakingChangesPage extends Page implements IDoubleClickListener {

	public static final String ID = "com.liferay.ide.project.ui.findBreakingChangesPage";

	public static boolean showAll = false;

	public FindBreakingChangesPage(Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, findbreackingchangesPageId, true);

		final Composite findBreakingchangesContainer = SWTUtil.createComposite(this, 2, 1, GridData.FILL_BOTH, 0, 0);

		_sashForm = new SashForm(findBreakingchangesContainer, SWT.HORIZONTAL | SWT.H_SCROLL);

		_sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		SashForm nestedSashForm = new SashForm(_sashForm, SWT.VERTICAL | SWT.H_SCROLL);

		nestedSashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridData treeData = new GridData(GridData.FILL_BOTH);

		treeData.minimumWidth = 200;
		treeData.heightHint = 200;

		_treeViewer = new TreeViewer(nestedSashForm);

		_treeViewer.getTree().setLayoutData(treeData);

		_migrationContentProvider = new MigrationContentProvider();

		_treeViewer.setContentProvider(_migrationContentProvider);

		IDecoratorManager decoratorManager = PlatformUI.getWorkbench().getDecoratorManager();

		ILabelDecorator decorator = decoratorManager.getLabelDecorator();

		_treeViewer.setLabelProvider(new DecoratingLabelProvider(new MigrationLabelProvider(), decorator));

		_treeViewer.setInput(getInitialInput());

		MenuManager menuMgr = new MenuManager();
		IAction removeAction = new RemoveAction(_treeViewer);

		menuMgr.add(removeAction);

		Menu menu = menuMgr.createContextMenu(_treeViewer.getTree());

		_treeViewer.getTree().setMenu(menu);

		_treeViewer.expandAll();

		createTableView(nestedSashForm);

		_browser = new Browser(_sashForm, SWT.BORDER);

		_browser.setLayoutData(new GridData(GridData.FILL_BOTH));

		_treeViewer.addSelectionChangedListener(
			new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					List<Problem> problems = null;

					if (showAll) {
						problems = MigrationUtil.getProblemsFromTreeNode(event.getSelection());
					}
					else {
						problems = MigrationUtil.getCurrentProblemsFromTreeNode(event.getSelection());
					}

					_problemsViewer.setInput(problems.toArray());

					if (ListUtil.isNotEmpty(problems)) {
						_problemsViewer.setSelection(new StructuredSelection(problems.get(0)));
					}
				}

			});

		_problemsViewer.addSelectionChangedListener(
			new ISelectionChangedListener() {

				@Override
				public void selectionChanged(final SelectionChangedEvent event) {
					UIUtil.async(
						new Runnable() {

							@Override
							public void run() {
								_updateForm(event);
							}

						},
						50);
				}

			});

		_treeViewer.addDoubleClickListener(
			new IDoubleClickListener() {

				@Override
				public void doubleClick(DoubleClickEvent event) {
					ISelection selection = event.getSelection();

					if (selection instanceof ITreeSelection) {
						ITreeSelection treeSelection = (ITreeSelection)selection;

						Object selectedItem = treeSelection.getFirstElement();

						if (selectedItem instanceof FileProblems) {
							MigrationUtil.openEditor((FileProblems)selectedItem);

							return;
						}
						else {
							TreePath[] paths = treeSelection.getPathsFor(selectedItem);

							for (int i = 0; i < paths.length; i++) {
								_treeViewer.setExpandedState(paths[i], !_treeViewer.getExpandedState(paths[i]));
							}
						}
					}
				}

			});

		Composite buttonContainer = new Composite(findBreakingchangesContainer, SWT.NONE);
		GridLayout buttonContainerLayout = new GridLayout(1, false);

		buttonContainerLayout.marginHeight = 0;
		buttonContainerLayout.marginWidth = 0;

		buttonContainer.setLayout(buttonContainerLayout);

		buttonContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		_findbreakingchangesButton = new Button(buttonContainer, SWT.NONE);

		_findbreakingchangesButton.setImage(_getImage("migration-tasks.png"));
		_findbreakingchangesButton.setToolTipText("Find Breaking Changes");

		_findbreakingchangesButton.addListener(
			SWT.Selection,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					IViewPart view = UIUtil.findView(UpgradeView.ID);

					new RunMigrationToolAction("Run Migration Tool", view.getViewSite().getShell()).run();
				}

			});

		_correctAllImportIssuesButton = new Button(buttonContainer, SWT.NONE);

		_correctAllImportIssuesButton.setImage(_getImage("bandaid.gif"));
		_correctAllImportIssuesButton.setToolTipText("Automatically Correct Problems");

		_correctAllImportIssuesButton.addListener(
			SWT.Selection,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					new AutoCorrectAllAction(getInitialInput()).run();
				}

			});

		if ((_migrationContentProvider != null) && (_migrationContentProvider.getProblemsCount() > 0)) {
			_correctAllImportIssuesButton.setEnabled(true);
		}
		else {
			_correctAllImportIssuesButton.setEnabled(false);
		}

		Button expendAll = new Button(buttonContainer, SWT.NONE);

		expendAll.setImage(_getImage("expandall.gif"));
		expendAll.setToolTipText("Expand All");

		expendAll.addListener(
			SWT.Selection,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					_treeViewer.expandAll();
				}

			});

		Button collapseAll = new Button(buttonContainer, SWT.NONE);

		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

		Image collapseAllImage = sharedImages.getImage(ISharedImages.IMG_ELCL_COLLAPSEALL);

		collapseAll.setImage(collapseAllImage);

		collapseAll.setToolTipText("Collapse All");

		collapseAll.addListener(
			SWT.Selection,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					_treeViewer.collapseAll();
				}

			});

		Button openIgnoredList = new Button(buttonContainer, SWT.NONE);

		openIgnoredList.setImage(_getImage("properties.png"));
		openIgnoredList.setToolTipText("Open Ignored List");

		openIgnoredList.addListener(
			SWT.Selection,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
						parent.getShell(), MigrationProblemPreferencePage.ID, null, null);

					dialog.open();
				}

			});

		Button hideTree = new Button(buttonContainer, SWT.TOGGLE);

		hideTree.setImage(_getImage("hide_tree.png"));
		hideTree.setToolTipText("Hide Tree");

		hideTree.addListener(
			SWT.Selection,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					_browserMaximized = !_browserMaximized;

					_sashForm.setMaximizedControl(_browserMaximized ? _browser : null);
				}

			});

		_sashForm.setWeights(new int[] {2, 3});
	}

	@Override
	public void createSpecialDescriptor(Composite parent, int style) {
		final StringBuilder descriptorBuilder = new StringBuilder(
			"This step will help you find breaking changes for Java, JSP, XML, and properties files. ");

		descriptorBuilder.append(
			"It does not support front-end code (e.g., JavaScript, CSS).\n For service builder, you ");
		descriptorBuilder.append(
			"just need to modify changes in *ServiceImp.java, *Finder.java, and *Model.java classes. ");
		descriptorBuilder.append("Others will be resolved in the Build Service step.");

		String url = "";

		Link link = SWTUtil.createHyperLink(this, style, descriptorBuilder.toString(), 1, url);

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	public void createTableView(Composite container) {
		GridData gridData = new GridData(GridData.FILL_BOTH);

		gridData.minimumWidth = 200;
		gridData.minimumHeight = 200;

		Composite tableComposite = new Composite(container, SWT.NONE);

		_problemsViewer = new TableViewer(
			tableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL);

		_problemsViewer.getTable().setLayoutData(gridData);

		_createColumns(_problemsViewer);

		final Table table = _problemsViewer.getTable();

		table.setHeaderVisible(true);

		_problemsViewer.setContentProvider(ArrayContentProvider.getInstance());
		_problemsViewer.setComparer(null);
		_comparator = new MigratorComparator();

		_problemsViewer.setComparator(_comparator);

		MenuManager menuMgr = new MenuManager();
		IAction markDoneAction = new MarkDoneAction(_problemsViewer);
		IAction markUndoneAction = new MarkUndoneAction(_problemsViewer);
		IAction ignoreAction = new IgnoreAction(_problemsViewer);
		IAction ignoreAlways = new IgnoreAlwaysAction(_problemsViewer);
		IAction autoCorrectAction = new AutoCorrectAction(_problemsViewer);

		menuMgr.add(markDoneAction);
		menuMgr.add(markUndoneAction);
		menuMgr.add(ignoreAction);
		menuMgr.add(autoCorrectAction);
		menuMgr.add(ignoreAlways);
		Menu menu = menuMgr.createContextMenu(table);

		table.setMenu(menu);

		_problemsViewer.addDoubleClickListener(this);
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (event.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection)event.getSelection();

			Object element = ss.getFirstElement();

			if (element instanceof Problem) {
				MigrationUtil.openEditor((Problem)element);
			}
			else if (element instanceof FileProblems) {
				MigrationUtil.openEditor((FileProblems)element);
			}
		}
	}

	public TableViewer get_problemsViewer() {
		return _problemsViewer;
	}

	@Override
	public boolean getGridLayoutEqualWidth() {
		return false;
	}

	public List<ProblemsContainer> getInitialInput() {
		List<ProblemsContainer> problems = null;

		try {
			MigrationProblemsContainer container = UpgradeAssistantSettingsUtil.getObjectFromStore(
				MigrationProblemsContainer.class);

			if (container != null) {
				problems = new ArrayList<>();

				problems.add(container);
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		_migrationContentProvider.setProblems(problems);

		return problems;
	}

	@Override
	public String getPageTitle() {
		return "Find Breaking Changes";
	}

	public TableViewer getProblemsViewer() {
		return _problemsViewer;
	}

	public TreeViewer getTreeViewer() {
		return _treeViewer;
	}

	@Override
	public void onPageAction(PageActionEvent event) {
		PageAction action = event.getAction();

		if ((action instanceof PageFinishAction) && (event.getTargetPageIndex() == getIndex())) {
			Stream.of(
				CoreUtil.getAllProjects()).forEach(project -> {
				MarkerUtil.clearMarkers(project, MigrationConstants.MARKER_TYPE, null);
			});
		}
	}

	public void setButtonState(boolean state) {
		_findbreakingchangesButton.setEnabled(state);

		if (_migrationContentProvider != null) {
			_correctAllImportIssuesButton.setEnabled(
				(state == true && _migrationContentProvider.getProblemsCount() > 0) ? true : false);
		}
		else {
			_correctAllImportIssuesButton.setEnabled(false);
		}
	}

	private void _createColumns(final TableViewer problemsViewer) {
		final String[] titles = {"Resolved", "Line", "Problem"};

		TableViewerColumn col0 = _createTableViewerColumn(titles[0], problemsViewer);

		col0.getColumn().pack();

		col0.setEditingSupport(
			new EditingSupport(problemsViewer) {

				@Override
				protected boolean canEdit(Object element) {
					return true;
				}

				@Override
				protected CellEditor getCellEditor(Object element) {
					return new CheckboxCellEditor(problemsViewer.getTable());
				}

				@Override
				protected Object getValue(Object element) {
					return ((Problem)element).getStatus() == Problem.STATUS_RESOLVED;
				}

				@Override
				protected void setValue(Object element, Object value) {
					if (value == Boolean.TRUE) {
						new MarkDoneAction().run((Problem)element, problemsViewer);
					}
					else {
						new MarkUndoneAction().run((Problem)element, problemsViewer);
					}
				}

			});

		col0.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public Image getImage(Object element) {
					Problem p = (Problem)element;

					if (p.getStatus() == Problem.STATUS_RESOLVED) {
						return _imageChecked;
					}
					else {
						return _imageUnchecked;
					}
				}

				@Override
				public String getText(Object element) {
					return null;
				}

			});

		TableViewerColumn col1 = _createTableViewerColumn(titles[1], problemsViewer);

		col1.getColumn().pack();

		col1.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					Problem p = (Problem)element;

					return p.lineNumber > -1 ? (p.lineNumber + "") : "";
				}

			});

		TableViewerColumn col2 = _createTableViewerColumn(titles[2], problemsViewer);

		col2.getColumn().pack();

		col2.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					Problem p = (Problem)element;

					return p.title;
				}

				@Override
				public void update(ViewerCell cell) {
					super.update(cell);

					Table table = problemsViewer.getTable();

					table.getColumn(2).pack();
				}

			});

		TableColumnLayout tableLayout = new TableColumnLayout();

		TableColumn column0 = col0.getColumn();
		TableColumn column1 = col1.getColumn();
		TableColumn column2 = col2.getColumn();

		tableLayout.setColumnData(column0, new ColumnWeightData(50, column0.getWidth()));
		tableLayout.setColumnData(column1, new ColumnWeightData(50, column1.getWidth()));
		tableLayout.setColumnData(column2, new ColumnWeightData(100, column2.getWidth()));

		Table table = problemsViewer.getTable();

		table.getParent().setLayout(tableLayout);
	}

	private TableViewerColumn _createTableViewerColumn(String title, TableViewer viewer) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);

		final TableColumn column = viewerColumn.getColumn();

		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(_getSelectionAdapter(column, viewer.getTable().indexOf(column)));

		return viewerColumn;
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

	private Image _getImage(String imageName) {
		return ProjectUI.getDefault().getImage(imageName);
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

	private SelectionAdapter _getSelectionAdapter(final TableColumn column, final int index) {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				_comparator.setColumn(index);
				int dir = _comparator.getDirection();

				_problemsViewer.getTable().setSortDirection(dir);

				_problemsViewer.getTable().setSortColumn(column);
				_problemsViewer.refresh();
			}

		};
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

	private static final Image _imageChecked =
		ProjectUI.getDefault().getImageRegistry().get(ProjectUI.CHECKED_IMAGE_ID);
	private static final Image _imageUnchecked =
		ProjectUI.getDefault().getImageRegistry().get(ProjectUI.UNCHECKED_IMAGE_ID);

	private Browser _browser;
	private boolean _browserMaximized = false;
	private MigratorComparator _comparator;
	private Button _correctAllImportIssuesButton;
	private Button _findbreakingchangesButton;
	private MigrationContentProvider _migrationContentProvider;
	private TableViewer _problemsViewer;
	private SashForm _sashForm;
	private TreeViewer _treeViewer;

}