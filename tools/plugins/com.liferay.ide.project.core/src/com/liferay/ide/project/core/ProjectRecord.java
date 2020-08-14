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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;

import java.io.File;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public class ProjectRecord {

	public ProjectRecord(File file) {
		if (file.isDirectory()) {
			liferayProjectDir = file;
		}
		else {
			projectSystemFile = file;
		}

		_setProjectName();
	}

	public ProjectRecord(IProject preSelectedProject) {
		project = preSelectedProject;

		_setProjectName();
	}

	public ProjectRecord(Object file, Object parent, int level) {
		this.parent = parent;

		this.level = level;

		_setProjectName();
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof ProjectRecord) && (project != null)) {
			ProjectRecord projectRecordObj = (ProjectRecord)obj;

			return project.equals(projectRecordObj.project);
		}

		return super.equals(obj);
	}

	public String getProjectLabel() {
		if (description == null) {
			return projectName;
		}

		URI uri = project.getLocationURI();

		String projectLocation = uri.getPath();

		String path = StringPool.EMPTY;

		if (projectSystemFile != null) {
			path = projectSystemFile.getParent();
		}
		else if (liferayProjectDir != null) {
			path = liferayProjectDir.getPath();
		}
		else if (project != null) {
			path = new Path(
				projectLocation
			).toOSString();
		}

		return NLS.bind("{0} ({1})", projectName, path);
	}

	public IPath getProjectLocation() {
		if (projectSystemFile != null) {
			return new Path(projectSystemFile.getParent());
		}
		else if (liferayProjectDir != null) {
			return new Path(liferayProjectDir.getPath());
		}
		else if (project != null) {
			return project.getRawLocation();
		}

		return null;
	}

	public String getProjectName() {
		return projectName;
	}

	public boolean hasConflicts() {
		return hasConflicts;
	}

	public void setHasConflicts(boolean b) {
		hasConflicts = b;
	}

	public IProjectDescription description;
	public File liferayProjectDir;
	public File projectSystemFile;

	protected boolean hasConflicts;
	protected int level;
	protected Object parent;
	protected IProject project;
	protected String projectName;

	private boolean _isDefaultLocation(IPath path) {

		// The project description file must at least be within the project, which is within the workspace location

		if (path.segmentCount() < 2) {
			return false;
		}

		File file = FileUtil.getFile(path.removeLastSegments(2));

		return file.equals(FileUtil.getFile(Platform.getLocation()));
	}

	private void _setProjectName() {
		if (projectName != null) {
			return;
		}

		try {

			// If we don't have the project name try again

			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			if (projectSystemFile != null) {
				IPath path = new Path(projectSystemFile.getPath());

				// if the file is in the default location, use the directory name as the project name

				if (_isDefaultLocation(path)) {
					projectName = path.segment(path.segmentCount() - 2);

					description = workspace.newProjectDescription(projectName);
				}
				else {
					description = workspace.loadProjectDescription(path);

					projectName = description.getName();
				}
			}
			else if (liferayProjectDir != null) {
				IPath path = new Path(liferayProjectDir.getPath());

				projectName = path.lastSegment();

				description = workspace.newProjectDescription(projectName);
			}
			else if (project != null) {
				projectName = project.getName();

				description = project.getDescription();
			}
		}
		catch (CoreException ce) {

			// no good couldn't get the name

		}
	}

}