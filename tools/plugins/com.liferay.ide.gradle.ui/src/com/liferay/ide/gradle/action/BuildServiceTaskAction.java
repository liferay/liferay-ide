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

package com.liferay.ide.gradle.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.LiferayGradleWorkspaceProject;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;

import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;

/**
 * @author Lovett Li
 * @author Terry Jia
 * @author Andy Wu
 */
public class BuildServiceTaskAction extends GradleTaskAction {

	@Override
	public GradleTask getCheckedTask(GradleTask gradleTask, GradleProject gradleProject, String taskName) {
		Set<String> projectNameSet = _getServiceBuilderProjects();

		if (projectNameSet.contains(gradleProject.getName())) {
			return gradleTask;
		}

		return null;
	}

	@Override
	public boolean needCheckTask() {
		return true;
	}

	protected void afterAction() {
		boolean refresh = false;

		IProject[] projects = CoreUtil.getClasspathProjects(project);

		for (IProject project : projects) {
			List<IFolder> folders = CoreUtil.getSourceFolders(JavaCore.create(project));

			if (ListUtil.isEmpty(folders)) {
				refresh = true;
			}
			else {
				try {
					project.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
				catch (CoreException ce) {
				}
			}
		}

		List<IFolder> folders = CoreUtil.getSourceFolders(JavaCore.create(project));

		if (ListUtil.isEmpty(folders) || refresh) {
			GradleUtil.refreshProject(project);
		}
		else {
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			catch (CoreException ce) {
			}
		}
	}

	@Override
	protected String getGradleTaskName() {
		return "buildService";
	}

	private Set<String> _getServiceBuilderProjects() {
		IPath projectLocation = project.getLocation();

		String projectPath = projectLocation.toString();

		LiferayGradleWorkspaceProject liferayGradleWorkspaceProject = new LiferayGradleWorkspaceProject(
			LiferayWorkspaceUtil.getWorkspaceProject());

		Set<IProject> childProjects = liferayGradleWorkspaceProject.getChildProjects();

		Stream<IProject> projectStream = childProjects.stream();

		return projectStream.filter(
			project -> ProjectUtil.isServiceBuilderProject(project)
		).filter(
			project -> {
				IPath location = project.getLocation();

				String path = location.toString();

				return path.startsWith(projectPath);
			}
		).map(
			project -> project.getName()
		).collect(
			Collectors.toSet()
		);
	}

}