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

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Adapters;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class ProjectProblemsContainer {

	public void addFileProblemsContainer(FileProblemsContainer fileProblemsContainer) {
		_fileProblemsContainers.add(fileProblemsContainer);
	}

	public boolean equals(Object object) {
		if ((object instanceof ProjectProblemsContainer) == false) {
			return false;
		}

		ProjectProblemsContainer projectProblemsContainer = Adapters.adapt(object, ProjectProblemsContainer.class);

		if (projectProblemsContainer == null) {
			return false;
		}

		if (_isEqualFileProblem(_fileProblemsContainers, projectProblemsContainer._fileProblemsContainers) &&
			_projectName.equals(projectProblemsContainer._projectName)) {

			return true;
		}

		return false;
	}

	public FileProblemsContainer getFileProblemsContainer(File file) {
		Stream<FileProblemsContainer> stream = _fileProblemsContainers.stream();

		return stream.filter(
			fileProblemContainer -> file.equals(fileProblemContainer.getFile())
		).findFirst(
		).orElse(
			null
		);
	}

	public List<FileProblemsContainer> getFileProblemsContainers() {
		return _fileProblemsContainers;
	}

	public String getProjectName() {
		return _projectName;
	}

	public int hashCode() {
		int hash = 31;

		hash = 31 * hash + ((_projectName != null) ? _projectName.hashCode() : 0);

		Stream<FileProblemsContainer> fileProblemStream = _fileProblemsContainers.stream();

		int fileProblemsHashCodes = fileProblemStream.map(
			Object::hashCode
		).reduce(
			0, Integer::sum
		).intValue();

		return 31 * hash + fileProblemsHashCodes;
	}

	public boolean isEmpty() {
		return _fileProblemsContainers.isEmpty();
	}

	public void setProjectName(String projectName) {
		_projectName = projectName;
	}

	private boolean _isEqualFileProblem(
		Collection<FileProblemsContainer> source, Collection<FileProblemsContainer> target) {

		boolean sizeEquals = ListUtil.sizeEquals(source, target);

		if (!sizeEquals) {
			return false;
		}

		Stream<FileProblemsContainer> targetStream = target.stream();

		Collection<File> targetTitles = targetStream.map(
			FileProblemsContainer::getFile
		).collect(
			Collectors.toList()
		);

		Stream<FileProblemsContainer> sourceStream = source.stream();

		return sourceStream.map(
			FileProblemsContainer::getFile
		).filter(
			targetTitles::contains
		).findAny(
		).isPresent();
	}

	private List<FileProblemsContainer> _fileProblemsContainers = new ArrayList<>();
	private String _projectName;

}