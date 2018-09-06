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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ValidationUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Simon Jiang
 * @author Joye Luo
 */
public abstract class AbstractLiferayTableViewCustomPart extends Page {

	public static IPath getTempLocation(String prefix, String fileName) {
		IPath tmpStation = ProjectUI.getPluginStateLocation();

		tmpStation = tmpStation.append("tmp");
		tmpStation = tmpStation.append(prefix);
		tmpStation = tmpStation.append("/");
		tmpStation = tmpStation.append(String.valueOf(System.currentTimeMillis()));
		tmpStation = tmpStation.append(CoreUtil.isNullOrEmpty(fileName) ? StringPool.EMPTY : "/");
		tmpStation = tmpStation.append(fileName);

		return tmpStation;
	}

	public AbstractLiferayTableViewCustomPart(
		Composite parent, int style, LiferayUpgradeDataModel dataModel, String pageId, boolean hasFinishAndSkipAction) {

		super(parent, style, dataModel, pageId, hasFinishAndSkipAction);

		GridLayout layout = new GridLayout(2, false);

		layout.marginHeight = 0;
		layout.marginWidth = 0;

		setLayout(layout);

		final GridData descData = new GridData(GridData.FILL_BOTH);

		descData.grabExcessVerticalSpace = true;
		descData.grabExcessHorizontalSpace = true;

		setLayoutData(descData);

		final Table table = new Table(this, SWT.FULL_SELECTION);

		final GridData tableData = new GridData(GridData.FILL_BOTH);

		tableData.grabExcessVerticalSpace = true;
		tableData.grabExcessHorizontalSpace = true;
		tableData.horizontalAlignment = SWT.FILL;

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.setLayoutData(tableData);
		Composite buttonContainer = new Composite(this, SWT.NONE);

		buttonContainer.setLayout(new GridLayout(1, false));
		buttonContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		final Button upgradeButton = new Button(buttonContainer, SWT.NONE);

		upgradeButton.setText("Upgrade...");
		upgradeButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		upgradeButton.addListener(
			SWT.Selection,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					_handleUpgradeEvent();
				}

			});

		tableViewer = new TableViewer(table);

		tableViewer.setContentProvider(new TableViewContentProvider());
		tableViewer.addDoubleClickListener(
			new IDoubleClickListener() {

				@Override
				public void doubleClick(DoubleClickEvent event) {
					_handleCompare((IStructuredSelection)event.getSelection());
				}

			});

		TableViewerColumn colFileName = new TableViewerColumn(tableViewer, SWT.NONE);

		TableColumn fileNameColumn = colFileName.getColumn();

		fileNameColumn.setWidth(50);
		fileNameColumn.setText("File Name");

		colFileName.setLabelProvider(getLableProvider());

		TableViewerColumn colProjectName = new TableViewerColumn(tableViewer, SWT.NONE);

		TableColumn projectNameColumn = colProjectName.getColumn();

		projectNameColumn.setWidth(200);
		projectNameColumn.setText("Project Name");

		colProjectName.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					LiferayUpgradeElement tableViewElement = (LiferayUpgradeElement)element;

					return tableViewElement.getProjectName();
				}

			});

		TableViewerColumn colLocation = new TableViewerColumn(tableViewer, SWT.NONE);

		TableColumn locationColumn = colLocation.getColumn();

		locationColumn.setWidth(200);
		locationColumn.setText("File Location");

		colLocation.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					LiferayUpgradeElement tableViewElement = (LiferayUpgradeElement)element;

					return tableViewElement.getFileLocation();
				}

			});

		TableViewerColumn colUpgradeStatus = new TableViewerColumn(tableViewer, SWT.NONE);

		TableColumn upgradeStatusColumn = colUpgradeStatus.getColumn();

		upgradeStatusColumn.setWidth(200);
		upgradeStatusColumn.setText("Upgrade Status");

		colUpgradeStatus.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					LiferayUpgradeElement tableViewElement = (LiferayUpgradeElement)element;

					if (tableViewElement.getUpgradeStatus()) {
						return "Yes";
					}

					return "Not";
				}

			});

		SapphireUtil.attachListener(dataModel.getImportFinished(), new LiferayUpgradeValidationListener());
	}

	@Override
	public void onSelectionChanged(int targetSelection) {
		Page selectedPage = UpgradeView.getPage(targetSelection);

		String selectedPageId = selectedPage.getPageId();

		if (!selectedPageId.equals(getPageId())) {
			return;
		}

		_handleFindEvent();
	}

	public class LiferayUpgradeElement {

		public LiferayUpgradeElement(IFile file, IProject project) {
			_file = file;
			_project = project;

			_upgradeStatus = false;
		}

		public IFile getFile() {
			return _file;
		}

		public String getFileLocation() {
			return FileUtil.getLocationOSString(_file);
		}

		public String getFileName() {
			return _file.getName();
		}

		public IProject getProject() {
			return _project;
		}

		public String getProjectName() {
			return _project.getName();
		}

		public boolean getUpgradeStatus() {
			return _upgradeStatus;
		}

		public void setUpgradeStatus(boolean upgradeStatus) {
			_upgradeStatus = upgradeStatus;
		}

		private IFile _file;
		private IProject _project;
		private boolean _upgradeStatus;

	}

	protected abstract void createTempFile(final IFile srcFile, final File templateFile, final String projectName);

	protected abstract void doUpgrade(IFile srcFile, IProject project);

	protected abstract IFile[] getAvaiableUpgradeFiles(IProject project);

	protected abstract CellLabelProvider getLableProvider();

	protected List<IProject> getSelectedProjects() {
		List<IProject> projects = new ArrayList<>();

		final JavaProjectSelectionDialog dialog = new JavaProjectSelectionDialog(UIUtil.getActiveShell());

		if (dialog.open() == Window.OK) {
			final Object[] selectedProjects = dialog.getResult();

			if (selectedProjects != null) {
				for (Object project : selectedProjects) {
					if (project instanceof IJavaProject) {
						IJavaProject p = (IJavaProject)project;

						projects.add(p.getProject());
					}
				}
			}
		}

		return projects;
	}

	protected String getUpgradeVersion() {
		String upgradeVersion = SapphireUtil.getContent(dataModel.getUpgradeVersion());

		if (StringUtil.contains(upgradeVersion, "7.1")) {
			return "7.1.0";
		}
		else {
			return "7.0.0";
		}
	}

	protected abstract boolean isUpgradeNeeded(IFile srcFile);

	protected Status retval = Status.createOkStatus();
	protected TableViewer tableViewer;

	protected abstract class LiferayUpgradeTabeViewLabelProvider extends ColumnLabelProvider {

		public LiferayUpgradeTabeViewLabelProvider() {
			_imageRegistry = new ImageRegistry();

			initalizeImageRegistry(_imageRegistry);
		}

		public LiferayUpgradeTabeViewLabelProvider(final String greyColorName) {
			_imageRegistry = new ImageRegistry();

			initalizeImageRegistry(_imageRegistry);
		}

		@Override
		public void dispose() {
			_imageRegistry.dispose();
		}

		@Override
		public String getText(Object element) {
			LiferayUpgradeElement tableViewElement = (LiferayUpgradeElement)element;

			return tableViewElement.getFileName();
		}

		@Override
		public void update(ViewerCell cell) {
			super.update(cell);

			TableItem item = (TableItem)cell.getItem();

			Control control = tableViewer.getControl();

			if (_oddFlag) {
				item.setBackground(new Color(control.getDisplay(), 225, 225, 225));
			}
			else {
				item.setBackground(new Color(control.getDisplay(), 250, 253, 253));
			}

			_oddFlag = !_oddFlag;
		}

		protected ImageRegistry getImageRegistry() {
			return _imageRegistry;
		}

		protected abstract void initalizeImageRegistry(ImageRegistry imageRegistry);

		private final ImageRegistry _imageRegistry;

	}

	protected class TableViewContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof LiferayUpgradeElement[]) {
				return (LiferayUpgradeElement[])inputElement;
			}

			return new Object[] {inputElement};
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private IPath _createPreviewerFile(final IProject project, final IFile srcFile) {
		IPath templateLocation = getTempLocation(project.getName(), srcFile.getName());

		File file = templateLocation.toFile();

		File parentFile = file.getParentFile();

		parentFile.mkdirs();

		try {
			createTempFile(srcFile, templateLocation.toFile(), project.getName());
		}
		catch (Exception e) {
			ProjectCore.logError(e);
		}

		return templateLocation;
	}

	private List<LiferayUpgradeElement> _getInitItemsList(List<IProject> projects, IProgressMonitor monitor) {
		final List<LiferayUpgradeElement> tableViewElementList = new ArrayList<>();

		int count = projects.size();

		if (count <= 0) {
			return tableViewElementList;
		}

		int unit = 100 / count;

		monitor.beginTask("Find needed upgrade file......", 100);

		for (int i = 0; i < count; i++) {
			monitor.worked(i + 1 * unit);

			if (i == (count - 1)) {
				monitor.worked(100);
			}

			IProject project = projects.get(i);

			monitor.setTaskName("Finding needed upgrade file for " + project.getName());
			IFile[] upgradeFiles = getAvaiableUpgradeFiles(project);

			for (IFile upgradeFile : upgradeFiles) {
				IPath filePath = upgradeFile.getLocation();

				if (!ValidationUtil.isProjectTargetDirFile(filePath.toFile()) && isUpgradeNeeded(upgradeFile)) {
					LiferayUpgradeElement tableViewElement = new LiferayUpgradeElement(upgradeFile, project);

					tableViewElementList.add(tableViewElement);
				}
			}
		}

		for (int i = 0; i < tableViewElementList.size() - 1; i++) {
			for (int j = tableViewElementList.size() - 1; j > i; j--) {
				LiferayUpgradeElement elementSource = tableViewElementList.get(j);

				IFile sourceFile = elementSource.getFile();

				IPath jLocation = sourceFile.getLocation();

				LiferayUpgradeElement elementTarget = tableViewElementList.get(i);

				IFile targetFile = elementTarget.getFile();

				IPath iLocation = targetFile.getLocation();

				if (jLocation.equals(iLocation)) {
					tableViewElementList.remove(j);
				}
			}
		}

		return tableViewElementList;
	}

	private void _handleCompare(IStructuredSelection selection) {
		LiferayUpgradeElement descriptorElement = (LiferayUpgradeElement)selection.getFirstElement();

		IFile file = descriptorElement.getFile();

		IPath createPreviewerFile = _createPreviewerFile(descriptorElement.getProject(), file);

		LiferayUpgradeCompare lifeayDescriptorUpgradeCompre = new LiferayUpgradeCompare(
			file.getLocation(), createPreviewerFile, file.getName());

		lifeayDescriptorUpgradeCompre.openCompareEditor();
	}

	private void _handleFindEvent() {
		IProject[] projectArrys = CoreUtil.getAllProjects();

		List<IProject> projectList = UpgradeUtil.getAvailableProject(projectArrys);

		try {
			final WorkspaceJob workspaceJob = new WorkspaceJob("Find needed upgrade files......") {

				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					final List<LiferayUpgradeElement> tableViewElementList = _getInitItemsList(projectList, monitor);

					_tableViewElements = tableViewElementList.toArray(
						new LiferayUpgradeElement[tableViewElementList.size()]);

					UIUtil.async(
						new Runnable() {

							@Override
							public void run() {
								String message = "ok";

								tableViewer.setInput(_tableViewElements);

								Table table = tableViewer.getTable();

								Stream.of(
									table.getColumns()
								).forEach(
									obj -> obj.pack()
								);

								if (_tableViewElements.length < 1) {
									message = "No file needs to be upgraded";
								}

								PageValidateEvent pe = new PageValidateEvent();

								pe.setMessage(message);
								pe.setType(PageValidateEvent.warning);

								triggerValidationEvent(pe);
							}

						});

					return StatusBridge.create(Status.createOkStatus());
				}

			};

			workspaceJob.setUser(true);
			workspaceJob.schedule();
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
	}

	private void _handleUpgradeEvent() {
		try {
			IProgressService progressService = UIUtil.getProgressService();

			progressService.run(
				true, false,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InterruptedException, InvocationTargetException {
						int count = _tableViewElements != null ? _tableViewElements.length : 0;

						if (count == 0) {
							UIUtil.async(
								new Runnable() {

									@Override
									public void run() {
										String message = "No files that need to be upgraded were found.";
										PageValidateEvent pe = new PageValidateEvent();

										pe.setMessage(message);
										pe.setType(PageValidateEvent.warning);

										triggerValidationEvent(pe);
									}

								});

							return;
						}

						int unit = 100 / count;

						monitor.beginTask("Start to upgrade files.....", 100);

						for (int i = 0; i < count; i++) {
							monitor.worked(i + 1 * unit);

							if (i == (count - 1)) {
								monitor.worked(100);
							}

							LiferayUpgradeElement tableViewElement = _tableViewElements[i];

							monitor.setTaskName("Upgrading files for " + tableViewElement.getProjectName());

							if (tableViewElement.getUpgradeStatus() == true) {
								continue;
							}

							try {
								IProject project = tableViewElement.getProject();

								doUpgrade(tableViewElement.getFile(), project);

								if (project != null) {
									project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
								}

								final int loopNum = i;

								UIUtil.async(
									new Runnable() {

										@Override
										public void run() {
											tableViewElement.setUpgradeStatus(true);

											_tableViewElements[loopNum] = tableViewElement;

											tableViewer.setInput(_tableViewElements);

											Table table = tableViewer.getTable();

											Stream.of(
												table.getColumns()
											).forEach(
												obj -> obj.pack()
											);

											tableViewer.refresh();
										}

									});
							}
							catch (Exception e) {
								ProjectCore.logError("Error upgrade files...... ", e);
							}
						}
					}

				});
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
	}

	private boolean _oddFlag = true;
	private LiferayUpgradeElement[] _tableViewElements;

	private class LiferayUpgradeValidationListener extends org.eclipse.sapphire.Listener {

		@Override
		public void handle(org.eclipse.sapphire.Event event) {
			if (event instanceof ValuePropertyContentEvent) {
				ValuePropertyContentEvent propertyEvetn = (ValuePropertyContentEvent)event;

				final Property property = propertyEvetn.property();

				if ("ImportFinished".equals(property.name())) {
					_handleFindEvent();
				}
			}
		}

	}

}