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
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Seiphon Wang
 * @author Terry Jia
 */
public class ModuleExtProjectNamePossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		IProject[] projects = CoreUtil.getAllProjects();

		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		IPath extDirLocation = LiferayWorkspaceUtil.getExtDirLocation(workspaceProject);

		Set<String> projectNames = Stream.of(
			projects
		).filter(
			project -> {
				IPath location = project.getLocation();

				if (extDirLocation.isPrefixOf(location) && !location.equals(extDirLocation)) {
					return true;
				}

				return false;
			}
		).map(
			IProject::getName
		).collect(
			Collectors.toSet()
		);

		values.addAll(projectNames);
	}

}