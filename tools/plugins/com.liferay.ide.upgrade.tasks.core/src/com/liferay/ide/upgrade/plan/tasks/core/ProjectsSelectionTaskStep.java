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

package com.liferay.ide.upgrade.plan.tasks.core;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Terry Jia
 */
public abstract class ProjectsSelectionTaskStep extends BaseUpgradeTaskStep {

	public abstract IStatus execute(IProject[] project, IProgressMonitor progressMonitor);

	public boolean selectAllDefault() {
		return false;
	}

	public boolean selectFilter(Object parentElement, Object element) {
		return true;
	}

}