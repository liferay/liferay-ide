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

package com.liferay.ide.upgrade.plan.ui.internal;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class ProjectProblemsContainer {

	public FileProblemsContainer[] getFileProblemsContainers() {
		return _fileProblemsContainers;
	}

	public String getProjectName() {
		return _projectName;
	}

	public void setFileProblemsContainers(FileProblemsContainer[] fileProblemsContainers) {
		_fileProblemsContainers = fileProblemsContainers;
	}

	public void setProjectName(String projectName) {
		_projectName = projectName;
	}

	private FileProblemsContainer[] _fileProblemsContainers;
	private String _projectName;

}