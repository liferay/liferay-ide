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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectImporter;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * @author Andy Wu
 */
public class GradleModuleProjectImporter extends AbstractLiferayProjectImporter {

	@Override
	public IStatus canImport(String location) {
		IStatus retval = null;

		File file = new File(location);

		if (_findGradleFile(file)) {
			if (_findSettingsFile(file)) {
				return Status.OK_STATUS;
			}
			else {
				File parent = file.getParentFile();

				while (parent != null) {
					if (_findGradleFile(parent)) {
						File gradleFile = new File(file, "build.gradle");

						IPath gradleFilelocation = Path.fromOSString(gradleFile.getAbsolutePath());

						IFile gradleWorkspaceFile = CoreUtil.getWorkspaceRoot().getFileForLocation(gradleFilelocation);

						if ((gradleWorkspaceFile != null) && (gradleWorkspaceFile.getProject() != null)) {
							_refreshProject = gradleWorkspaceFile.getProject();

							retval = new Status(
								IStatus.WARNING, GradleCore.PLUGIN_ID,
								"Project is inside \"" + _refreshProject.getName() +
									"\" project. we will just refresh to import");
						}
						else {
							retval = new Status(
								IStatus.ERROR, GradleCore.PLUGIN_ID,
								"Location is not the root location of a multi-module project.");
						}

						return retval;
					}

					parent = parent.getParentFile();
				}

				if (retval == null) {
					return Status.OK_STATUS;
				}
			}
		}

		return retval;
	}

	@Override
	public List<IProject> importProjects(String location, IProgressMonitor monitor) throws CoreException {
		if (_refreshProject != null) {
			GradleUtil.refreshProject(_refreshProject);
			_refreshProject = null;
		}
		else {
			GradleUtil.sychronizeProject(new Path(location), monitor);
		}

		// To-Do need return the projects added

		return new ArrayList<>();
	}

	private boolean _findFile(File dir, String name) {
		if (FileUtil.notExists(dir)) {
			return false;
		}

		File[] files = dir.listFiles();

		for (File file : files) {
			if (FileUtil.isNotDir(file) && file.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}

	private boolean _findGradleFile(File dir) {
		return _findFile(dir, "build.gradle");
	}

	private boolean _findSettingsFile(File dir) {
		return _findFile(dir, "settings.gradle");
	}

	private IProject _refreshProject = null;

}