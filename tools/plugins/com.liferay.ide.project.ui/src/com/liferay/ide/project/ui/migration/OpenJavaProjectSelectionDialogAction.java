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

package com.liferay.ide.project.ui.migration;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.upgrade.BreakingChangeSelectedProject;
import com.liferay.ide.project.core.upgrade.BreakingChangeSimpleProject;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;
import com.liferay.ide.project.ui.upgrade.animated.LiferayUpgradeDataModel;
import com.liferay.ide.ui.util.SWTUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class OpenJavaProjectSelectionDialogAction extends Action {

	public OpenJavaProjectSelectionDialogAction(String text, Shell shell) {
		super(text);

		_shell = shell;

		ImageRegistry pluginImageRegistry = ProjectUI.getPluginImageRegistry();

		setImageDescriptor(pluginImageRegistry.getDescriptor(ProjectUI.MIGRATION_TASKS_IMAGE_ID));
	}

	public OpenJavaProjectSelectionDialogAction(
		String text, Shell shell, LiferayUpgradeDataModel liferayUpgradeDataModel) {

		this(text, shell);

		dataModel = liferayUpgradeDataModel;
	}

	protected Boolean getCombineExistedProjects() {
		return _combineProject;
	}

	protected ISelection getSelectionProjects() {
		final BreakingChangeProjectDialog dialog = new BreakingChangeProjectDialog(_shell);

		if (dialog.open() == Window.OK) {
			final Object[] selectedProjects = dialog.getResult();

			if (selectedProjects != null) {
				List<IProject> projects = new ArrayList<>();

				for (Object project : selectedProjects) {
					if (project instanceof IJavaProject) {
						IJavaProject p = (IJavaProject)project;

						projects.add(p.getProject());
					}
				}

				try {
					BreakingChangeSelectedProject bkProject = new BreakingChangeSelectedProject();

					Stream<IProject> projectStream = projects.stream();

					projectStream.forEach(
						project -> bkProject.addSimpleProject(
							new BreakingChangeSimpleProject(project.getName(), FileUtil.getLocationOSString(project))));

					UpgradeAssistantSettingsUtil.setObjectToStore(BreakingChangeSelectedProject.class, bkProject);
				}
				catch (IOException ioe) {
					ProjectUI.logError(ioe);
				}

				return new StructuredSelection(projects.toArray(new IProject[0]));
			}
		}

		return null;
	}

	protected LiferayUpgradeDataModel dataModel;

	private void _createUpgradeVersionControl(Composite composite) {
		Group upgradeVersionGroup = SWTUtil.createGroup(composite, "Upgrade to Liferay Version", 2, 2);

		String upgradeVersion = SapphireUtil.getContent(dataModel.getUpgradeVersion());

		boolean selectedValue = "7.0".equals(upgradeVersion);

		boolean liferayWorkspace = SapphireUtil.getContent(dataModel.getIsLiferayWorkspace());

		Button upgradeVersion70Button = SWTUtil.createRadioButton(upgradeVersionGroup, "7.0", null, selectedValue, 1);

		upgradeVersion70Button.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					dataModel.setUpgradeVersion("7.0");
				}

			});

		Button upgradeVersion71Button = SWTUtil.createRadioButton(upgradeVersionGroup, "7.1", null, !selectedValue, 1);

		upgradeVersion71Button.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					dataModel.setUpgradeVersion("7.1");
				}

			});

		if (liferayWorkspace) {
			dataModel.setUpgradeVersion("7.1");

			upgradeVersion70Button.setEnabled(true);
			upgradeVersion71Button.setEnabled(true);
			upgradeVersion71Button.setSelection(true);
		}
	}

	private Boolean _combineProject = true;
	private Shell _shell;

	private class BreakingChangeProjectDialog extends JavaProjectSelectionDialog {

		public BreakingChangeProjectDialog(Shell parentShell) {
			super(parentShell);

			try {
				_selectedProject = UpgradeAssistantSettingsUtil.getObjectFromStore(BreakingChangeSelectedProject.class);
			}
			catch (IOException ioe) {
			}
		}

		@Override
		protected void addSelectionButtons(Composite composite) {
			Composite buttonComposite = new Composite(composite, SWT.NONE);
			GridLayout layout = new GridLayout();

			layout.makeColumnsEqualWidth = true;
			layout.marginWidth = 0;
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			buttonComposite.setLayout(layout);

			buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			_combineExistedProblemCheckbox = SWTUtil.createCheckButton(
				buttonComposite, "Combine existed problems list.", null, true, layout.numColumns);

			SelectionListener listener = new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					_combineProject = _combineExistedProblemCheckbox.getSelection();

					_initializeSelectedProject(_selectedProject, _combineProject);

					updateOKButtonState(e);
				}

			};

			_combineExistedProblemCheckbox.addSelectionListener(listener);

			if (dataModel.getUpgradeVersion() != null) {
				_createUpgradeVersionControl(composite);
			}

			super.addSelectionButtons(composite);
		}

		@Override
		protected void deSelectAllAction() {
			fTableViewer.setAllChecked(false);
			_combineExistedProblemCheckbox.setSelection(false);
			getOkButton().setEnabled(false);
		}

		@Override
		protected void initialize() {
			if ((_selectedProject == null) || !_combineProject) {
				return;
			}

			_initializeSelectedProject(_selectedProject, _combineProject);
		}

		@Override
		protected boolean isResizable() {
			return false;
		}

		@Override
		protected void selectAllAction() {
			fTableViewer.setAllChecked(true);
			_combineExistedProblemCheckbox.setSelection(true);
			getOkButton().setEnabled(true);
		}

		@Override
		protected void updateOKButtonState(EventObject event) {
			Object[] checkedElements = fTableViewer.getCheckedElements();

			if (checkedElements.length == 0) {
				getOkButton().setEnabled(false);
			}
			else {
				getOkButton().setEnabled(true);
			}
		}

		private void _initializeSelectedProject(BreakingChangeSelectedProject selectedProject, Boolean combineProject) {
			Table table = fTableViewer.getTable();

			TableItem[] children = table.getItems();

			for (TableItem item : children) {
				if (item.getData() != null) {
					if (item.getData() instanceof IJavaProject) {
						IJavaProject projectItem = (IJavaProject)item.getData();

						if (selectedProject == null) {
							break;
						}

						List<BreakingChangeSimpleProject> simpleProjects = selectedProject.getSelectedProjects();

						for (BreakingChangeSimpleProject sProject : simpleProjects) {
							IProject project = projectItem.getProject();

							String projectLocationValue = FileUtil.getLocationString(project);

							String projectName = project.getName();

							if (projectName.equals(sProject.getName()) &&
								projectLocationValue.equals(sProject.getLocation())) {

								item.setChecked(combineProject);
							}
						}
					}
				}
			}
		}

		private Button _combineExistedProblemCheckbox;
		private BreakingChangeSelectedProject _selectedProject = null;

	}

}