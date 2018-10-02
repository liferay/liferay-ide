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

package com.liferay.ide.gradle.ui.navigator.workspace;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.adapter.NoopLiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Terry Jia
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class LiferayWorkspaceServerContentProvider extends AbstractNavigatorContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {
			IProject parentProject = (IProject)parentElement;

			List<IProject> projects = new ArrayList<>();

			for (IProject project : CoreUtil.getAllProjects()) {
				ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

				if (!(liferayProject instanceof NoopLiferayProject)) {
					IPath fullPath = project.getLocation();

					IPath parentLocation = parentProject.getLocation();

					if (parentLocation.isPrefixOf(fullPath) && !project.equals(parentProject)) {
						projects.add(project);
					}
				}
			}

			return projects.toArray();
		}

		return null;
	}

	public void getPipelinedChildren(Object parent, Set currentChildren) {
		IProject project = LiferayWorkspaceUtil.getWorkspaceProject();

		if (project != null) {
			IFile settingsFile = project.getFile("settings.gradle");

			if (GradleUtil.isWatchableProject(settingsFile)) {
				currentChildren.add(project);
			}
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		return true;
	}

	@Override
	public boolean hasPipelinedChildren(Object element, boolean currentHasChildren) {
		return hasChildren(element);
	}

}