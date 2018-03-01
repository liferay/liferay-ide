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

package com.liferay.ide.idea.core;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.DefaultProjectTypeEP;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectType;

import com.liferay.ide.idea.ui.modules.LiferayProjectType;
import com.liferay.ide.idea.util.LiferayWorkspaceUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Simon Jiang
 */
@State(name = "ProjectType", storages = @Storage(file = StoragePathMacros.PROJECT_FILE))
public class LiferayProjectTypeService implements PersistentStateComponent<ProjectType> {

	public static LiferayProjectTypeService getInstance(@NotNull Project project) {
		return ServiceManager.getService(project, LiferayProjectTypeService.class);
	}

	@Nullable
	public static ProjectType getProjectType(@Nullable Project project) {
		ProjectType projectType;

		if (project != null) {
			projectType = getInstance(project)._projectType;

			if (projectType != null) {
				return projectType;
			}
		}

		boolean liferayGradleProject = LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(project.getBasePath());

		if (liferayGradleProject == true) {
			return new ProjectType(LiferayProjectType.LIFERAY_GRADLE_WORKSPACE);
		}

		boolean liferayMavenProject = LiferayWorkspaceUtil.isValidMavenWorkspaceLocation(project);

		if (liferayMavenProject == true) {
			return new ProjectType(LiferayProjectType.LIFERAY_MAVEN_WORKSPACE);
		}

		return DefaultProjectTypeEP.getDefaultProjectType();
	}

	public static void setProjectType(@NotNull Project project, @Nullable ProjectType projectType) {
		getInstance(project).loadState(projectType);
	}

	@Nullable
	@Override
	public ProjectType getState() {
		return _projectType;
	}

	@Override
	public void loadState(ProjectType state) {
		_projectType = state;
	}

	private ProjectType _projectType;

}