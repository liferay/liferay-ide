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

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class ProjectProblemsContainer {

	public void addFileProblemsContainer(FileProblemsContainer fileProblemsContainer) {
		_fileProblemsContainers.add(fileProblemsContainer);
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

	public boolean isEmpty() {
		return _fileProblemsContainers.isEmpty();
	}

	public void setProjectName(String projectName) {
		_projectName = projectName;
	}

	private List<FileProblemsContainer> _fileProblemsContainers = new ArrayList<>();
	private String _projectName;

}