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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class NewLiferayComponentProjectNameDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		IProject[] allProjects = CoreUtil.getAllProjects();

		for (IProject project : allProjects) {
			IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

			if ((bundleProject != null) && Objects.equals("jar", bundleProject.getBundleShape()) &&
				!bundleProject.isFragmentBundle()) {

				return project.getName();
			}
		}

		return null;
	}

}