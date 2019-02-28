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

package com.liferay.ide.upgrade.tasks.core;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.adapter.NoopLiferayProject;
import com.liferay.ide.project.core.LiferayWorkspaceProject;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.resources.IProject;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class SelectableLiferayProjectFilter extends SelectableJavaProjectFilter {

	@Override
	public boolean test(IProject project) {
		if (!super.test(project)) {
			return false;
		}

		boolean result = Optional.ofNullable(
			project
		).filter(
			p -> LiferayCore.create(LiferayWorkspaceProject.class, project) == null
		).map(
			p -> LiferayCore.create(ILiferayProject.class, p)
		).filter(
			Objects::nonNull
		).filter(
			p -> !(p instanceof NoopLiferayProject)
		).isPresent();

		return result;
	}

}