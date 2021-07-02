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

package com.liferay.ide.project.core.modules.ext;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class ModuleExtProjectNamePossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (workspaceProject == null) {
			return;
		}

		IPath extDirLocation = LiferayWorkspaceUtil.getExtDirLocation(workspaceProject);

		values.addAll(
			Stream.of(
				CoreUtil.getAllProjects()
			).filter(
				project -> {
					IPath location = project.getLocation();

					if (extDirLocation.isPrefixOf(location) && !location.equals(extDirLocation) &&
						ProjectUtil.isModuleExtProject(project)) {

						return true;
					}

					return false;
				}
			).map(
				IProject::getName
			).collect(
				Collectors.toSet()
			));
	}

}