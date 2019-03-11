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

package com.liferay.ide.project.core.model;

import com.liferay.ide.core.util.MultiStatusBuilder;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectImportUtil;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

/**
 * @author Simon Jiang
 */
public class SDKImportProjectsOpMethods {

	public static final Status execute(SDKProjectsImportOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Importing Liferay plugin projects...", 100);

		Status retval = Status.createOkStatus();

		Path projectLocation = _getter.get(op.getSdkLocation());

		if ((projectLocation == null) || projectLocation.isEmpty()) {
			return Status.createErrorStatus("Project cannot be empty");
		}

		Job job = new WorkspaceJob("Importing Liferay projects...") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				MultiStatusBuilder statusBuilder = new MultiStatusBuilder(ProjectCore.PLUGIN_ID);
				ElementList<ProjectNamedItem> projectItems = op.getSelectedProjects();

				for (NamedItem projectNamedItem : projectItems) {
					try {
						if (projectNamedItem instanceof ProjectNamedItem) {
							String projectPath = _getter.get(((ProjectNamedItem)projectNamedItem).getLocation());

							String projectLocation = new Path(projectPath).toPortableString();

							ProjectImportUtil.importProject(
								PathBridge.create(new Path(projectLocation)), new NullProgressMonitor(), null);
						}
					}
					catch (Exception e) {
						statusBuilder.add(ProjectCore.createErrorStatus(e));
					}
				}

				return statusBuilder.build();
			}

		};

		job.schedule();

		return retval;
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {};

}