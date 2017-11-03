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

package com.liferay.ide.project.ui.migration;

import com.liferay.ide.project.core.upgrade.MigrationProblems;

/**
 * @author Terry Jia
 */
public class MigrationProblemsContainer implements ProblemsContainer {

	public MigrationProblems[] getProblemsArray() {
		return _problemsArray;
	}

	public String getType() {
		return _type;
	}

	public void setProblemsArray(MigrationProblems[] problemsArray) {
		_problemsArray = problemsArray;
		_type = problemsArray[0].getType();
	}

	private MigrationProblems[] _problemsArray;
	private String _type;

}