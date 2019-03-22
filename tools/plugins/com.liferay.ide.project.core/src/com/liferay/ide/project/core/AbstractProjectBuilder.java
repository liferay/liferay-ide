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

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class AbstractProjectBuilder implements IProjectBuilder {

	public AbstractProjectBuilder(IProject project) {
		_project = project;
	}

	@Override
	public List<Artifact> getDependencies(String configuration) {
		return Collections.emptyList();
	}

	public IProject getProject() {
		return _project;
	}

	protected IFile getDocrootFile(String path) {
		IFolder docroot = CoreUtil.getDefaultDocrootFolder(_project);

		if (FileUtil.notExists(docroot)) {
			return null;
		}

		IFile file = docroot.getFile(new Path(path));

		if (FileUtil.exists(file)) {
			return file;
		}

		return null;
	}

	private IProject _project;

}