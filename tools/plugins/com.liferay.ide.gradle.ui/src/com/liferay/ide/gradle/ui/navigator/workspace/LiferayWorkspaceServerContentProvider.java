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
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.adapter.NoopLiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class LiferayWorkspaceServerContentProvider extends AbstractNavigatorContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IProject) {
			IProject parentProject = (IProject)parentElement;

			return Stream.of(
				CoreUtil.getAllProjects()
			).filter(
				GradleUtil::isGradleProject
			).filter(
				project -> !ProjectUtil.isModuleExtProject(project)
			).map(
				project -> LiferayCore.create(ILiferayProject.class, project)
			).filter(
				liferayProject -> !(liferayProject instanceof NoopLiferayProject)
			).filter(
				liferayProject -> {
					IProject project = liferayProject.getProject();

					IPath fullPath = project.getLocation();

					IPath parentLocation = parentProject.getLocation();

					if (parentLocation.isPrefixOf(fullPath) && !project.equals(parentProject)) {
						return true;
					}

					return false;
				}
			).map(
				ILiferayProject::getProject
			).collect(
				Collectors.toList()
			).toArray();
		}

		return null;
	}

	public void getPipelinedChildren(Object parent, Set currentChildren) {
		if (parent instanceof IServer) {
			IServer server = (IServer)parent;

			IRuntime runtime = server.getRuntime();

			if (runtime == null) {
				return;
			}

			PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(PortalRuntime.class, null);

			if ((portalRuntime == null) || (portalRuntime.getLiferayHome() == null)) {
				return;
			}

			IPath liferayHome = portalRuntime.getLiferayHome();

			IProject project = LiferayWorkspaceUtil.getWorkspaceProject();

			if (LiferayWorkspaceUtil.isValidGradleWorkspaceProject(project)) {
				IPath projectLocation = project.getLocation();

				IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, project);

				if ((project != null) && projectLocation.isPrefixOf(liferayHome) &&
					ListUtil.isNotEmpty(workspaceProject.getChildProjects())) {

					currentChildren.add(project);
				}
			}
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IServer) {
			return true;
		}

		IWorkspaceProject workspaceProject = LiferayCore.create(IWorkspaceProject.class, element);

		if (workspaceProject != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasPipelinedChildren(Object element, boolean currentHasChildren) {
		return hasChildren(element);
	}

}