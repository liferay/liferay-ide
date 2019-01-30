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

package com.liferay.ide.upgrade.plan.core;

/**
 * @author Terry Jia
 */
public class MigrationProblemsContainer implements InfoProvider {

	@Override
	public String getDetail() {
		return null;
	}

	@Override
	public String getLabel() {
		return "Liferay Upgrade";
	}

	public ProjectProblems[] getProblemsArray() {
		return _projectProblemsArray;
	}

	public void setProblemsArray(ProjectProblems[] problemsArray) {
		_projectProblemsArray = problemsArray;
	}

	private ProjectProblems[] _projectProblemsArray;

}