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

package com.liferay.ide.upgrade.task.sdk.steps;

import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.base.ProjectsUpgradeTaskStep;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(properties = "OSGI-INF/RemoveClosedProjectsStep.properties", service = UpgradeTaskStep.class)
public class RemoveClosedProjectsStep extends ProjectsUpgradeTaskStep {

	@Override
	protected IStatus execute(IProject[] projects, IProgressMonitor progressMonitor) {
		for (IProject project : projects) {
			try {
				project.delete(true, progressMonitor);
			}
			catch (CoreException ce) {
			}
		}

		return Status.OK_STATUS;
	}

	protected ViewerFilter getFilter() {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IProject project = (IProject)element;

				return !project.isOpen();
			}

		};
	}

	@Override
	protected boolean selectAllDefault() {
		return true;
	}

}