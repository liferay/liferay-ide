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

package com.liferay.ide.gradle.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;

/**
 * @author Terry Jia
 */
public class WatchingProjects {

	public static WatchingProjects getInstance() {
		if (_instance == null) {
			_instance = new WatchingProjects();
		}

		return _instance;
	}

	public void addProject(IProject project) {
		_watchingProjects.add(project);
	}

	public void clearProjects() {
		_watchingProjects.clear();
	}

	public Set<IProject> getProjects() {
		return _watchingProjects;
	}

	public void removeProject(IProject project) {
		_watchingProjects.remove(project);
	}

	private WatchingProjects() {
	}

	private static WatchingProjects _instance;

	private Set<IProject> _watchingProjects = new HashSet<>();

}