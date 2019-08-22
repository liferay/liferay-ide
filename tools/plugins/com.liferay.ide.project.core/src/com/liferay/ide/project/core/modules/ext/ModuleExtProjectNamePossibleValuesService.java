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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Seiphon Wang
 */
public class ModuleExtProjectNamePossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		IProject[] projects = CoreUtil.getAllProjects();

		IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		String extFolder = LiferayWorkspaceUtil.getExtDir(liferayWorkspaceProject);

		IPath path = liferayWorkspaceProject.getLocation();

		IPath appendPath = path.append(extFolder);

		String parentLcation = appendPath.toString();

		for (IProject project : projects) {
			IPath location = project.getLocation();

			String locationString = location.toString();

			if (locationString.contains(parentLcation) && !locationString.equals(parentLcation)) {
				String projectName = project.getName();

				values.add(projectName);
			}
		}
	}

}