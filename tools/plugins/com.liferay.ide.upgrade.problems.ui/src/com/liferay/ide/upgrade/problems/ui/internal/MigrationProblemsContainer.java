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

package com.liferay.ide.upgrade.problems.ui.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class MigrationProblemsContainer {

	public void addProjectProblemsContainer(ProjectProblemsContainer projectProblemsContainer) {
		_projectProblemsContainers.add(projectProblemsContainer);
	}

	public List<ProjectProblemsContainer> getProjectProblemsConatiners() {
		return _projectProblemsContainers;
	}

	public boolean isNotEmpty() {
		for (ProjectProblemsContainer projectProblemsContainer : _projectProblemsContainers) {
			if (!projectProblemsContainer.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private List<ProjectProblemsContainer> _projectProblemsContainers = new ArrayList<>();

}