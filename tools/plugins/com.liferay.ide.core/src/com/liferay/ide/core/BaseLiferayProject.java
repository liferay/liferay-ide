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

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author Gregory Amerson
 */
public abstract class BaseLiferayProject implements ILiferayProject {

	public BaseLiferayProject(IProject project) {
		_project = project;
	}

	public <T> T adapt(Class<T> adapterType) {
		ILiferayProjectAdapter[] adapters = LiferayCore.getProjectAdapters();

		if (ListUtil.isEmpty(adapters)) {
			return null;
		}

		for (ILiferayProjectAdapter adapter : adapters) {
			T adapted = adapter.adapt(this, adapterType);

			if (adapted != null) {
				return adapted;
			}
		}

		return null;
	}

	public IProject getProject() {
		return _project;
	}

	public IFolder getSourceFolder(String classification) {
		List<IFolder> folders = CoreUtil.getSourceFolders(JavaCore.create(_project));

		if (ListUtil.isEmpty(folders)) {
			return null;
		}

		return folders.get(0);
	}

	@Override
	public IFolder[] getSourceFolders() {
		try {
			IProject project = getProject();

			if (project == null) {
				return null;
			}

			IJavaProject javaproject = JavaCore.create(project);

			if (FileUtil.notExists(javaproject)) {
				return null;
			}

			if (!javaproject.isOpen()) {
				javaproject.open(new NullProgressMonitor());
			}

			return CoreUtil.getSourceFolders(javaproject).toArray(new IFolder[0]);
		}
		catch (JavaModelException jme) {
			LiferayCore.logWarning(jme);
		}

		return null;
	}

	protected boolean filterResource(IPath resourcePath, String[] ignorePaths) {
		if ((resourcePath == null) || (resourcePath.segmentCount() < 1)) {
			return false;
		}

		for (String ignorePath : ignorePaths) {
			if (resourcePath.segment(0).equals(ignorePath)) {
				return true;
			}
		}

		return false;
	}

	private IProject _project;

}