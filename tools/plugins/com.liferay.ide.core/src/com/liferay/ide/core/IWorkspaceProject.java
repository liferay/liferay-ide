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

package com.liferay.ide.core;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public interface IWorkspaceProject extends ILiferayProject {

	public Set<IProject> getChildProjects();

	public String getLiferayHome();

	public default List<Artifact> getTargetPlatformArtifacts() {
		return Collections.emptyList();
	}

	public String getTargetPlatformVersion();

	public default ProductInfo getWorkspaceProductInfo() {
		return null;
	}

	public default String[] getWorkspaceWarDirs() {
		return new String[] {"wars"};
	}

	public default boolean isWatchable() {
		return false;
	}

	public void watch(Set<IProject> childProjects);

	public Set<IProject> watching();

}