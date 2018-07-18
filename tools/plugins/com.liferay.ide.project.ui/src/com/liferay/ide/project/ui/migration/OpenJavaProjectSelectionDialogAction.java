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

import com.liferay.ide.project.core.upgrade.BreakingChangeSelectedProject;
import com.liferay.ide.project.core.upgrade.BreakingChangeSimpleProject;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.BreakingChangeVersion;
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.Value;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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

	public OpenJavaProjectSelectionDialogAction(String text, Shell shell, LiferayUpgradeDataModel dataModel) {
		this(text,shell);

		this.dataModel = dataModel;
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
							new BreakingChangeSimpleProject(project.getName(), project.getLocation().toOSString())));
					
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

	protected Label createLabel;
	protected LiferayUpgradeDataModel dataModel;

	private void _createMigrateBreakingChangeVersion(Composite composite) {
		//Composite buttonComposite = new Composite(composite, SWT.NONE);
		Group buttonGroup = SWTUtil.createGroup(composite, "Breaking Changes Version:", 2, 2);
		boolean selectedValue = false;
		boolean inputIsLiferayWorkspace = false;

		Value<String> initBreakingChangeVersion = dataModel.getBreakingChangeVersion();

		Value<Boolean> inputIsLiferayWorkspaceElement = dataModel.getInputIsLiferayWorkspace();

		inputIsLiferayWorkspace = inputIsLiferayWorkspaceElement.content();

		BreakingChangeVersion[] breakingChangeVersions70 = BreakingChangeVersion.getBreakingChangeVersions(
			"7.0", false);

		if (initBreakingChangeVersion != null) {
			String dataModelBreakingChangeValue = initBreakingChangeVersion.content();

			selectedValue = "7.0".equals(dataModelBreakingChangeValue);
		}

		Button breakingChangsVersion70 = SWTUtil.createRadioButton(
			buttonGroup, breakingChangeVersions70[0].getName(), null, selectedValue, 1);

		breakingChangsVersion70.addSelectionListener(
			new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					dataModel.setBreakingChangeVersion(breakingChangeVersions70[0].getValues());
				}

			});

		BreakingChangeVersion[] breakingChangeVersions71 = BreakingChangeVersion.getBreakingChangeVersions(
			"7.1", false);
		
		Button breakingChangsVersion71 = SWTUtil.createRadioButton(
			buttonGroup, breakingChangeVersions71[0].getName(), null, !selectedValue, 1);

		breakingChangsVersion71.addSelectionListener(
			new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					dataModel.setBreakingChangeVersion(breakingChangeVersions71[0].getValues());
				}

			});

		if (inputIsLiferayWorkspace) {
			String[] breakingChangeVersions = BreakingChangeVersion.getBreakingChangeVersionValues(
				inputIsLiferayWorkspace);

			dataModel.setBreakingChangeVersion(breakingChangeVersions[0]);
			
			breakingChangsVersion70.setEnabled(false);
			breakingChangsVersion71.setEnabled(true);
			breakingChangsVersion71.setSelection(true);

			breakingChangsVersion71.addSelectionListener(
				new SelectionAdapter(){

					@Override
					public void widgetSelected(SelectionEvent e) {
						dataModel.setBreakingChangeVersion(breakingChangeVersions[0]);
					}

				});
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

			if (dataModel.getBreakingChangeVersion() != null) {
				_createMigrateBreakingChangeVersion(composite);
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

							IPath projectLocation = project.getLocation();
							
							String projectLocationValue = projectLocation.toOSString();
							
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