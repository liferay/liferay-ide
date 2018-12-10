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

package com.liferay.ide.upgrade.task.problem.api;

import com.liferay.ide.upgrade.plan.api.Summary;

import java.io.File;

import java.util.Arrays;

/**
 * @author Terry Jia
 */
public class FileProblems implements Summary {

	public void addProblem(Problem problem) {
		Problem[] problems = Arrays.copyOf(_problems, _problems.length + 1);

		problems[problems.length - 1] = problem;

		_problems = problems;
	}

	@Override
	public String doDetail() {
		StringBuffer sb = new StringBuffer();

		sb.append(_file);
		sb.append("<br />");
		sb.append("It has " + _problems.length + " issue(s) need to be solved.");
		sb.append("<br />");

		for (Problem problem : _problems) {
			sb.append(problem.title);
			sb.append("<br />");
		}

		return sb.toString();
	}

	@Override
	public String doLabel() {
		String fileName = _file.getName();
		String path = _file.getParent();

		return fileName + " [" + path + "]";
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