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

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class GradleProjectProvider
	extends AbstractLiferayProjectProvider implements NewLiferayProjectProvider<NewLiferayModuleProjectOp> {

	public GradleProjectProvider() {
		super(new Class<?>[] {IProject.class});
	}

	@Override
	public IStatus createNewProject(NewLiferayModuleProjectOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = Status.OK_STATUS;

		String projectName = SapphireUtil.getContent(op.getProjectName());

		IPath location = PathBridge.create(SapphireUtil.getContent(op.getLocation()));

		String className = SapphireUtil.getContent(op.getComponentName());

		String liferayVersion = SapphireUtil.getContent(op.getLiferayVersion());

		String serviceName = SapphireUtil.getContent(op.getServiceName());

		String packageName = SapphireUtil.getContent(op.getPackageName());

		ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

		List<String> properties = new ArrayList<>();

		for (PropertyKey propertyKey : propertyKeys) {
			properties.add(
				SapphireUtil.getContent(propertyKey.getName()) + "=" + SapphireUtil.getContent(propertyKey.getValue()));
		}

		File targetDir = location.toFile();

		targetDir.mkdirs();

		String projectTemplateName = SapphireUtil.getContent(op.getProjectTemplateName());

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("-d \"");
		sb.append(targetDir.getAbsolutePath());
		sb.append("\" ");
		sb.append("-v ");
		sb.append(liferayVersion);
		sb.append(" ");
		sb.append("-t ");
		sb.append(projectTemplateName);
		sb.append(" ");

		if (className != null) {
			sb.append("-c ");
			sb.append(className);
			sb.append(" ");
		}

		if (serviceName != null) {
			sb.append("-s ");
			sb.append(serviceName);
			sb.append(" ");
		}

		if (packageName != null) {
			sb.append("-p ");
			sb.append(packageName);
			sb.append(" ");
		}

		sb.append("\"");
		sb.append(projectName);
		sb.append("\" ");

		try {
			BladeCLI.execute(sb.toString());

			ElementList<ProjectName> projectNames = op.getProjectNames();

			ProjectName name = projectNames.insert();

			name.setName(projectName);

			if (projectTemplateName.equals("service-builder")) {
				name = projectNames.insert();

				name.setName(projectName + "-api");

				name = projectNames.insert();

				name.setName(projectName + "-service");
			}

			IPath projectLocation = location;

			String lastSegment = location.lastSegment();

			if ((location != null) && (location.segmentCount() > 0)) {
				if (!lastSegment.equals(projectName)) {
					projectLocation = location.append(projectName);
				}
			}

			boolean hasGradleWorkspace = LiferayWorkspaceUtil.hasGradleWorkspace();
			boolean useDefaultLocation = SapphireUtil.getContent(op.getUseDefaultLocation());
			boolean inWorkspacePath = false;

			IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			if (hasGradleWorkspace && (liferayWorkspaceProject != null) && !useDefaultLocation) {
				IPath workspaceLocation = liferayWorkspaceProject.getLocation();

				if (workspaceLocation != null) {
					inWorkspacePath = workspaceLocation.isPrefixOf(projectLocation);
				}
			}

			if ((hasGradleWorkspace && useDefaultLocation) || inWorkspacePath) {
				GradleUtil.refreshProject(liferayWorkspaceProject);
			}
			else {
				CoreUtil.openProject(projectName, projectLocation, monitor);

				GradleUtil.sychronizeProject(projectLocation, monitor);
			}
		}
		catch (Exception e) {
			retval = GradleCore.createErrorStatus("Can not create module project: " + e.getMessage(), e);
		}

		return retval;
	}

	@Override
	public synchronized ILiferayProject provide(Object adaptable) {
		ILiferayProject retval = null;

		if (adaptable instanceof IProject) {
			IProject project = (IProject)adaptable;

			try {
				if (!LiferayWorkspaceUtil.isValidWorkspace(project) && LiferayNature.hasNature(project) &&
					GradleProjectNature.isPresentOn(project)) {

					if (ProjectUtil.isFacetedGradleBundleProject(project)) {
						return new FacetedGradleBundleProject(project);
					}
					else {
						return new LiferayGradleProject(project);
					}
				}
			}
			catch (Exception e) {

				// ignore errors

			}
		}

		return retval;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		if (LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(path)) {
			retval = GradleCore.createErrorStatus(" Can not set WorkspaceProject root folder as project directory.");
		}

		return retval;
	}

}