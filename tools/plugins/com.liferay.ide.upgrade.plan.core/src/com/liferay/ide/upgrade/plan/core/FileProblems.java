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

import java.io.File;

import java.util.Arrays;

/**
 * @author Terry Jia
 */
public class FileProblems {

	public void addProblem(Problem problem) {
		Problem[] problems = Arrays.copyOf(_problems, _problems.length + 1);

		problems[problems.length - 1] = problem;

		_problems = problems;
	}

	public File getFile() {
		return _file;
	}

	public Problem[] getProblems() {
		return _problems;
	}

	public void setFile(File file) {
		_file = file;
	}

	public void setProblems(Problem[] problems) {
		_problems = problems;
	}

	private File _file;
	private Problem[] _problems = new Problem[0];

}