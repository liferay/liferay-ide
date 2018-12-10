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

package com.liferay.ide.upgrade.task.problem.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Simon Jiang
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BreakingChangeSelectedProject {

	@JsonCreator
	public BreakingChangeSelectedProject() {
	}

	public void addSimpleProject(BreakingChangeSimpleProject simplePorject) {
		_selectedProjects.add(simplePorject);
	}

	@JsonProperty("SelectedProjects")
	public List<BreakingChangeSimpleProject> getSelectedProjects() {
		return _selectedProjects;
	}

	public void setSelectedProjects(List<BreakingChangeSimpleProject> selectedProjects) {
		_selectedProjects = selectedProjects;
	}

	private List _selectedProjects = new ArrayList<>();

}