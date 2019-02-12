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

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class MigrationProblemsContainer {

	public ProjectProblemsContainer[] getProjectProblemsConatiners() {
		return _projectProblemsConatiners;
	}

	public void setProjectProblemsConatiners(ProjectProblemsContainer[] projectProblemsConatiners) {
		_projectProblemsConatiners = projectProblemsConatiners;
	}

	private ProjectProblemsContainer[] _projectProblemsConatiners;

}