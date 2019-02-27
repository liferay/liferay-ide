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

package com.liferay.ide.upgrade.tasks.core.internal.buildservice;

import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.upgrade.tasks.core.LiferayProjectPredicate;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public class ServiceBuilderProjectPredicate extends LiferayProjectPredicate {

	@Override
	public boolean test(IProject project) {
		if (super.test(project)) {
			List<IFile> serviceXmls = (new SearchFilesVisitor()).searchFiles(project, "service.xml");

			return !serviceXmls.isEmpty();
		}

		return false;
	}

}