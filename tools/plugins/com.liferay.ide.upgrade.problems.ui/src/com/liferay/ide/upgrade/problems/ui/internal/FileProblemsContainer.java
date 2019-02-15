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

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Terry Jia
 * @author Gregory Amreson
 */
public class FileProblemsContainer {

	public void addUpgradeProblem(UpgradeProblem problem) {
		_upgradeProblems.add(problem);
	}

	public File getFile() {
		return _file;
	}

	public List<UpgradeProblem> getUpgradeProblems() {
		return _upgradeProblems;
	}

	public void setFile(File file) {
		_file = file;
	}

	private File _file;
	private List<UpgradeProblem> _upgradeProblems = new ArrayList<>();

}