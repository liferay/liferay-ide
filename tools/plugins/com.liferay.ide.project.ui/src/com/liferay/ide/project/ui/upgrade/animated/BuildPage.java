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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ide.IDE.SharedImages;

/**
 * @author Joye Luo
 */
public class BuildPage extends Page {

	public BuildPage(Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, buildPageId, true);

		_createImages();

		Composite container = new Composite(this, SWT.NONE);

		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		_tableViewer = new TableViewer(container);

		_tableViewer.setContentProvider(new TableViewContentProvider());
		_tableViewer.addDoubleClickListener(
			new IDoubleClickListener() {

				@Override
				public void doubleClick(DoubleClickEvent event) {
					final IStructuredSelection selection = (IStructuredSelection)event.getSelection();

					final TableViewElement tableViewElement = (TableViewElement)selection.getFirstElement();

					final String projectName = tableViewElement.projectName;

					final IProject project = ProjectUtil.getProject(projectName);

					Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
						UIUtil.getActiveShell(), "Build Project", "Do you want to build this project again?");

					if (openNewLiferayProjectWizard) {
						final WorkspaceJob workspaceJob = new WorkspaceJob("Build Project......") {

							@Override
							public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
								monitor.beginTask("Start to build project......", 100);
								monitor.setTaskName("Build " + projectName + " Project...");
								monitor.worked(100);

								if (monitor.isCanceled()) {
									return StatusBridge.create(Status.createOkStatus());
								}

								boolean buildFlag = _getBuildStatus(monitor, project);

								for (TableViewElement tableViewElement : _tableViewElements) {
									if (tableViewElement.projectName == projectName) {
										if (buildFlag) {
											tableViewElement.buildStatus = "Build Successful";
										}
										else {
											tableViewElement.buildStatus = "Build Failed";
										}
									}
								}

								UIUtil.async(
									new Runnable() {

										@Override
										public void run() {
											_tableViewer.setInput(_tableViewElements);
											_tableViewer.refresh();
										}

									});

								return StatusBridge.create(Status.createOkStatus());
							}

						};

						workspaceJob.setUser(true);
						workspaceJob.schedule();
					}
				}

			});

		TableViewerColumn colFirstName = new TableViewerColumn(_tableViewer, SWT.NONE);

		TableColumn tableColumn = colFirstName.getColumn();

		tableColumn.setWidth(400);
		tableColumn.setText("projectName");

		colFirstName.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public Image getImage(Object element) {
					return _imageProject;
				}

				@Override
				public String getText(Object element) {
					TableViewElement tableViewElement = (TableViewElement)element;

					return tableViewElement.projectName;
				}

			});

		TableViewerColumn colSecondName = new TableViewerColumn(_tableViewer, SWT.NONE);

		TableColumn tableColumn2 = colSecondName.getColumn();

		tableColumn2.setWidth(200);

		tableColumn2.setText("Build Status");

		colSecondName.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public Image getImage(Object element) {
					TableViewElement tableViewElement = (TableViewElement)element;

					if (tableViewElement.buildStatus.equals("Build Successful")) {
						return _imageSuccess;
					}
					else if (tableViewElement.buildStatus.equals("Build Failed")) {
						return _imageFail;
					}

					return null;
				}

				@Override
				public String getText(Object element) {
					TableViewElement tableViewElement = (TableViewElement)element;

					return tableViewElement.buildStatus;
				}

			});

		final Table table = _tableViewer.getTable();

		final GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);

		table.setLayoutData(tableData);

		table.setLinesVisible(false);

		Button buildButton = new Button(container, SWT.PUSH);

		buildButton.setText("Build...");
		buildButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		buildButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					_buildProjects();
				}

			});
	}

	public void createSpecialDescriptor(Composite parent, int style) {
		final StringBuilder descriptorBuilder = new StringBuilder("This step will help you build all your projects.\n");

		descriptorBuilder.append("You can rebuild the project by double-clicking it.");

		Link link = SWTUtil.createHyperLink(this, style, descriptorBuilder.toString(), 1, "");

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	@Override
	public String getPageTitle() {
		return "Build";
	}

	private void _buildProjects() {
		final List<IProject> selectProjectList = _getSelectedProjects();

		final IProject[] selectProjects = selectProjectList.toArray(new IProject[selectProjectList.size()]);

		try {
			final WorkspaceJob workspaceJob = new WorkspaceJob("Build Project......") {

				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					final List<TableViewElement> tableViewElementList = new ArrayList<>();
					int count = selectProjects.length;

					if (count <= 0) {
						return StatusBridge.create(Status.createOkStatus());
					}

					int unit = 100 / count;

					monitor.beginTask("Start to build project......", 100);

					for (int i = 0; i < count; i++) {
						final IProject project = selectProjects[i];

						final String projectName = project.getName();

						TableViewElement tableViewElement = new TableViewElement(projectName, "Not Build");

						tableViewElementList.add(tableViewElement);
					}

					UIUtil.async(
						new Runnable() {

							@Override
							public void run() {
								_tableViewElements = tableViewElementList.toArray(
									new TableViewElement[tableViewElementList.size()]);

								_tableViewer.setInput(_tableViewElements);

								_tableViewer.refresh();
							}

						});

					for (int i = 0; i < count; i++) {
						monitor.worked(i + 1 * unit);

						if (monitor.isCanceled()) {
							break;
						}

						if (i == (count - 1)) {
							monitor.worked(100);
						}

						TableViewElement viewElement = tableViewElementList.get(i);

						final String projectName = viewElement.projectName;

						final IProject project = ProjectUtil.getProject(projectName);

						monitor.setTaskName("Build " + projectName + " Project...");

						boolean buildFlag = _getBuildStatus(monitor, project);

						if (buildFlag) {
							viewElement.buildStatus = "Build Successful";
						}
						else {
							viewElement.buildStatus = "Build Failed";
						}

						UIUtil.async(
							new Runnable() {

								@Override
								public void run() {
									_tableViewElements = tableViewElementList.toArray(
										new TableViewElement[tableViewElementList.size()]);

									_tableViewer.setInput(_tableViewElements);

									_tableViewer.refresh();
								}

							});
					}

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

	private void _createImages() {
		ISharedImages sharedImages = UIUtil.getSharedImages();

		_imageProject = sharedImages.getImage(SharedImages.IMG_OBJ_PROJECT);

		if (_imageProject.isDisposed()) {
			ImageDescriptor imageDescriptor = sharedImages.getImageDescriptor(SharedImages.IMG_OBJ_PROJECT);

			_imageProject = imageDescriptor.createImage();
		}

		URL greenTickUrl = bundle.getEntry("/images/yes_badge.png");

		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(greenTickUrl);

		_imageSuccess = imageDescriptor.createImage();

		ImageData imageData = _imageSuccess.getImageData();

		imageData.scaledTo(16, 16);

		_imageFail = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
	}

	private boolean _getBuildStatus(IProgressMonitor monitor, IProject project) throws CoreException {
		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);
		IPath outputBundlepath = null;

		try {
			outputBundlepath = bundleProject.getOutputBundle(true, monitor);
		}
		catch (Exception e) {
		}

		if ((outputBundlepath != null) && !outputBundlepath.isEmpty()) {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			return true;
		}

		return false;
	}

	private List<IProject> _getSelectedProjects() {
		List<IProject> projects = new ArrayList<>();

		JavaProjectSelectionDialog dialog = new JavaProjectSelectionDialog(UIUtil.getActiveShell());

		if (dialog.open() == Window.OK) {
			Object[] selectedProjects = dialog.getResult();

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

	private Image _imageFail;
	private Image _imageProject;
	private Image _imageSuccess;
	private TableViewElement[] _tableViewElements;
	private TableViewer _tableViewer;

	private class TableViewContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof TableViewElement[]) {
				return (TableViewElement[])inputElement;
			}

			return new Object[] {inputElement};
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private class TableViewElement {

		public TableViewElement(String projectName, String buildStatus) {
			this.projectName = projectName;
			this.buildStatus = buildStatus;
		}

		public String buildStatus;
		public String projectName;

	}

}