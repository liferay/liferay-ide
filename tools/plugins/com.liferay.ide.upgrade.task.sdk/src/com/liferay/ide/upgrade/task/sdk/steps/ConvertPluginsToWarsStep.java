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

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.base.ProjectsUpgradeTaskStep;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(properties = "OSGI-INF/ConvertPluginsToWarsStep.properties", service = UpgradeTaskStep.class)
public class ConvertPluginsToWarsStep extends ProjectsUpgradeTaskStep {

	@Override
	protected IStatus execute(IProject[] projects, IProgressMonitor progressMonitor) {
		for (IProject project : projects) {
			IPath projectLocation = project.getLocation();

			IPath workspaceLocation = projectLocation.removeLastSegments(2);

			StringBuffer sb = new StringBuffer();

			sb.append("--base ");
			sb.append("\"");
			sb.append(workspaceLocation.toOSString());
			sb.append("\" ");
			sb.append("convert ");

			File file = FileUtil.getFile(project);

			sb.append(file.getName());

			try {
				BladeCLI.execute(sb.toString());

				IWorkspaceProject workspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

				IProject workspaceProject2 = workspaceProject.getProject();

				workspaceProject2.refreshLocal(IResource.DEPTH_ZERO, progressMonitor);

				project.refreshLocal(IResource.DEPTH_ZERO, progressMonitor);

				project.close(progressMonitor);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Status.OK_STATUS;
	}

	protected ViewerFilter getFilter() {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IProject project = (IProject)element;

				StringBuffer sb = new StringBuffer();

				IWorkspaceProject workspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

				String location = FileUtil.getLocationOSString(workspaceProject.getProject());

				sb.append("--base ");
				sb.append("\"");
				sb.append(location);
				sb.append("\" ");
				sb.append("convert -l");

				try {
					String[] lines = BladeCLI.execute(sb.toString());

					for (String line : lines) {
						line = line.trim();

						File file = FileUtil.getFile(project);

						String fileName = file.getName();

						if (line.equals(fileName)) {
							return true;
						}
					}
				}
				catch (BladeCLIException bclie) {
					bclie.printStackTrace();
				}

				return false;
			}

		};
	}

}