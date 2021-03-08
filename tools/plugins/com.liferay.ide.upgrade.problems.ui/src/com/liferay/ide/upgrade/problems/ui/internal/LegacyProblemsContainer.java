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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Adapters;

/**
 * @author Seiphon Wang
 */
public class LegacyProblemsContainer {

	public void addProjectProblemsContainer(ProjectProblemsContainer projectProblemsContainer) {
		_legacyProjectProblemsContainers.add(projectProblemsContainer);
	}

	public boolean equals(Object object) {
		if ((object instanceof LegacyProblemsContainer) == false) {
			return false;
		}

		LegacyProblemsContainer legacyFileProblemsContainer = Adapters.adapt(object, LegacyProblemsContainer.class);

		if (legacyFileProblemsContainer == null) {
			return false;
		}

		if (isEqualProjectProblem(
				_legacyProjectProblemsContainers, legacyFileProblemsContainer._legacyProjectProblemsContainers)) {

			return true;
		}

		return false;
	}

	public List<ProjectProblemsContainer> getProjectProblemsConatiners() {
		return _legacyProjectProblemsContainers;
	}

	@Override
	public int hashCode() {
		int hash = 31;

		Stream<ProjectProblemsContainer> projectProblemStream = _legacyProjectProblemsContainers.stream();

		int projectProblemsHashCodes = projectProblemStream.map(
			Object::hashCode
		).reduce(
			0, Integer::sum
		).intValue();

		return 31 * hash + projectProblemsHashCodes;
	}

	public boolean isEqualProjectProblem(
		Collection<ProjectProblemsContainer> source, Collection<ProjectProblemsContainer> target) {

		boolean sizeEquals = ListUtil.sizeEquals(source, target);

		if (!sizeEquals) {
			return false;
		}

		Stream<ProjectProblemsContainer> targetStream = target.stream();

		Collection<String> targetTitles = targetStream.map(
			ProjectProblemsContainer::getProjectName
		).collect(
			Collectors.toList()
		);

		Stream<ProjectProblemsContainer> projectProblemStream = source.stream();

		return projectProblemStream.map(
			ProjectProblemsContainer::getProjectName
		).filter(
			targetTitles::contains
		).findAny(
		).isPresent();
	}

	public boolean isNotEmpty() {
		for (ProjectProblemsContainer projectProblemsContainer : _legacyProjectProblemsContainers) {
			if (!projectProblemsContainer.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private List<ProjectProblemsContainer> _legacyProjectProblemsContainers = new ArrayList<>();

}