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

package com.liferay.ide.upgrade.commands.ui.internal;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.model.ProjectNamedItem;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.wizard.ProjectsCheckboxCustomPart;
import com.liferay.ide.upgrade.commands.core.MigrateExistingPluginsToWorkspaceOp;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.swt.widgets.Table;

/**
 * @author Simon Jiang
 */
public class MigrateExistingPluginsToWorkspaceCheckboxCustomPart extends ProjectsCheckboxCustomPart {

	@Override
	public void dispose() {
		if (_listener != null) {
			Value<Object> sdkProperty = _op().property(MigrateExistingPluginsToWorkspaceOp.PROP_SDK_LOCATION);

			sdkProperty.detach(_listener);
		}

		super.dispose();
	}

	@Override
	protected ElementList<ProjectNamedItem> getCheckboxList() {
		return _op().getSelectedProjects();
	}

	@Override
	protected List<ProjectCheckboxElement> getInitItemsList() {
		List<ProjectCheckboxElement> checkboxElementList = new ArrayList<>();

		Path sdkLocation = get(_op().getSdkLocation());

		if (FileUtil.notExists(sdkLocation)) {
			return checkboxElementList;
		}

		ProjectRecord[] projectRecords = _updateProjectsList(sdkLocation.toPortableString());

		if (projectRecords == null) {
			return checkboxElementList;
		}

		for (ProjectRecord projectRecord : projectRecords) {
			String projectLocation = FileUtil.toPortableString(projectRecord.getProjectLocation());

			String context = projectRecord.getProjectName() + " (" + projectLocation + ")";

			ProjectCheckboxElement checkboxElement = new ProjectCheckboxElement(
				projectRecord.getProjectName(), context, projectLocation);

			if (!projectRecord.hasConflicts()) {
				checkboxElementList.add(checkboxElement);
			}
		}

		_sortProjectCheckboxElement(checkboxElementList);

		return checkboxElementList;
	}

	@Override
	protected IStyledLabelProvider getLableProvider() {
		return new SDKImportProjectsLabelProvider();
	}

	@Override
	protected ElementList<ProjectNamedItem> getSelectedElements() {
		return _op().getSelectedProjects();
	}

	@Override
	protected void init() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(final PropertyContentEvent event) {
				PropertyDef eventDef = SapphireUtil.getPropertyDef(event);

				if (eventDef.equals(MigrateExistingPluginsToWorkspaceOp.PROP_SDK_LOCATION)) {
					Value<Path> sdkLocationPath = _op().getSdkLocation();

					Path sdkLocation = sdkLocationPath.content();

					if (sdkLocation != null) {
						IStatus status = ProjectImportUtil.validateSDKPath(sdkLocation.toPortableString());

						if (status.isOK()) {
							if (SapphireUtil.exists(sdkLocation)) {
								checkAndUpdateCheckboxElement();
							}
						}
						else {
							checkBoxViewer.remove(checkboxElements);
							updateValidation();
						}
					}
				}
			}

		};

		Value<Object> sdkLocation = _op().property(MigrateExistingPluginsToWorkspaceOp.PROP_SDK_LOCATION);

		sdkLocation.attach(_listener);
	}

	@Override
	protected void updateValidation() {
		retval = Status.createOkStatus();

		Value<Path> sdkLocationPath = _op().getSdkLocation();

		Path sdkLocation = sdkLocationPath.content();

		if (sdkLocation != null) {
			IStatus status = ProjectImportUtil.validateSDKPath(sdkLocation.toPortableString());

			if (status.isOK()) {
				Table table = checkBoxViewer.getTable();

				int projectsCount = table.getItemCount();

				Object[] elements = checkBoxViewer.getCheckedElements();

				int selectedProjectsCount = elements.length;

				if (projectsCount == 0) {
					retval = Status.createErrorStatus("No available projects can be imported.");
				}

				if ((projectsCount > 0) && (selectedProjectsCount == 0)) {
					retval = Status.createErrorStatus("At least one project must be specified.");
				}
			}
		}
		else {
			retval = Status.createErrorStatus("SDK path cannot be empty");
		}

		refreshValidation();
	}

	private IProject[] _getPluginSDKProjects() {
		if (_pluginSDKProjects == null) {
			_pluginSDKProjects = ProjectUtil.getAllPluginsSDKProjects();
		}

		return _pluginSDKProjects;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private Object[] _getProjectRecords() {
		List projectRecords = new ArrayList();

		for (Object project : _selectedProjects) {
			ProjectRecord projectRecord = (ProjectRecord)project;

			if (_isProjectInWorkspace(projectRecord.getProjectName())) {
				projectRecord.setHasConflicts(true);
			}

			projectRecords.add(project);
		}

		return projectRecords.toArray(new ProjectRecord[projectRecords.size()]);
	}

	private boolean _isProjectInWorkspace(String projectName) {
		if (projectName == null) {
			return false;
		}

		for (IProject project : _getPluginSDKProjects()) {
			if (FileUtil.nameEquals(project, projectName)) {
				return true;
			}
		}

		return false;
	}

	private MigrateExistingPluginsToWorkspaceOp _op() {
		return getLocalModelElement().nearest(MigrateExistingPluginsToWorkspaceOp.class);
	}

	private void _sortProjectCheckboxElement(List<ProjectCheckboxElement> checkboxElementList) {
		Comparator<ProjectCheckboxElement> projectElementComparator = new Comparator<ProjectCheckboxElement>() {

			@Override
			public int compare(ProjectCheckboxElement element1, ProjectCheckboxElement element2) {
				return element1.name.compareTo(element2.name);
			}

		};

		checkboxElementList.sort(projectElementComparator);
	}

	private ProjectRecord[] _updateProjectsList(final String path) {

		// on an empty path empty selectedProjects

		if ((path == null) || (path.length() == 0)) {
			_selectedProjects = new ProjectRecord[0];

			return null;
		}

		final File directory = new File(path);

		final boolean dirSelected = true;

		try {
			_selectedProjects = new ProjectRecord[0];

			Collection<File> eclipseProjectFiles = new ArrayList<>();

			Collection<File> liferayProjectDirs = new ArrayList<>();

			if (dirSelected && directory.isDirectory()) {
				if (!ProjectUtil.collectSDKProjectsFromDirectory(
						eclipseProjectFiles, liferayProjectDirs, directory, null, true, new NullProgressMonitor())) {

					return null;
				}

				_selectedProjects = new ProjectRecord[eclipseProjectFiles.size() + liferayProjectDirs.size()];

				int index = 0;

				for (File eclipseProjectFile : eclipseProjectFiles) {
					_selectedProjects[index++] = new ProjectRecord(eclipseProjectFile);
				}

				for (File liferayProjectDir : liferayProjectDirs) {
					_selectedProjects[index++] = new ProjectRecord(liferayProjectDir);
				}
			}
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}

		Object[] projects = _getProjectRecords();

		return (ProjectRecord[])projects;
	}

	private FilteredListener<PropertyContentEvent> _listener;
	private IProject[] _pluginSDKProjects;
	private Object[] _selectedProjects = new ProjectRecord[0];

}