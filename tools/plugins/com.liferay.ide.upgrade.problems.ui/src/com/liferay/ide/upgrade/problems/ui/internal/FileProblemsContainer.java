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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Adapters;

/**
 * @author Terry Jia
 * @author Gregory Amreson
 * @author Simon Jiang
 */
public class FileProblemsContainer {

	public void addUpgradeProblem(UpgradeProblem problem) {
		_upgradeProblems.add(problem);
	}

	public boolean equals(Object object) {
		if ((object instanceof FileProblemsContainer) == false) {
			return false;
		}

		FileProblemsContainer fileProblemsContainer = Adapters.adapt(object, FileProblemsContainer.class);

		if (fileProblemsContainer == null) {
			return false;
		}

		if (_isEqualUpgradeProblem(_upgradeProblems, fileProblemsContainer._upgradeProblems) &&
			_file.equals(fileProblemsContainer._file)) {

			return true;
		}

		return false;
	}

	public File getFile() {
		return _file;
	}

	public List<UpgradeProblem> getUpgradeProblems() {
		return _upgradeProblems;
	}

	@Override
	public int hashCode() {
		int hash = 31;

		hash = 31 * hash + ((_file != null) ? _file.hashCode() : 0);

		Stream<UpgradeProblem> problemStream = _upgradeProblems.stream();

		int problemsHashCodes = problemStream.map(
			Object::hashCode
		).reduce(
			0, Integer::sum
		).intValue();

		return 31 * hash + problemsHashCodes;
	}

	public void setFile(File file) {
		_file = file;
	}

	private boolean _isEqualUpgradeProblem(Collection<UpgradeProblem> source, Collection<UpgradeProblem> target) {
		boolean sizeEquals = ListUtil.sizeEquals(source, target);

		if (!sizeEquals) {
			return false;
		}

		Stream<UpgradeProblem> targetStream = target.stream();

		Collection<String> targetTitles = targetStream.map(
			UpgradeProblem::getUuid
		).collect(
			Collectors.toList()
		);

		Stream<UpgradeProblem> sourceStream = source.stream();

		return sourceStream.map(
			UpgradeProblem::getUuid
		).filter(
			targetTitles::contains
		).findAny(
		).isPresent();
	}

	private File _file;
	private List<UpgradeProblem> _upgradeProblems = new ArrayList<>();

}