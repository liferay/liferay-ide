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

		String projectName = op.getProjectName().content();

		IPath location = PathBridge.create(op.getLocation().content());

		String className = op.getComponentName().content();

		String liferayVersion = op.getLiferayVersion().content();

		String serviceName = op.getServiceName().content();

		String packageName = op.getPackageName().content();

		ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

		List<String> properties = new ArrayList<>();

		for (PropertyKey propertyKey : propertyKeys) {
			properties.add(propertyKey.getName().content(true) + "=" + propertyKey.getValue().content(true));
		}

		File targetDir = location.toFile();

		targetDir.mkdirs();

		String projectTemplateName = op.getProjectTemplateName().content();

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

			projectNames.insert().setName(projectName);

			if (projectTemplateName.equals("service-builder")) {
				projectNames.insert().setName(projectName + "-api");
				projectNames.insert().setName(projectName + "-service");
			}

			IPath projectLocation = location;

			String lastSegment = location.lastSegment();

			if ((location != null) && (location.segmentCount() > 0)) {
				if (!lastSegment.equals(projectName)) {
					projectLocation = location.append(projectName);
				}
			}

			boolean hasGradleWorkspace = LiferayWorkspaceUtil.hasGradleWorkspace();
			boolean useDefaultLocation = op.getUseDefaultLocation().content(true);
			boolean inWorkspacePath = false;

			IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

			if (hasGradleWorkspace && (liferayWorkspaceProject != null) && !useDefaultLocation) {
				IPath workspaceLocation = liferayWorkspaceProject.getLocation();

				if (workspaceLocation != null) {
					inWorkspacePath = workspaceLocation.isPrefixOf(projectLocation);
				}
			}

			if ((hasGradleWorkspace && useDefaultLocation) || inWorkspacePath) {
				GradleUtil.refreshGradleProject(liferayWorkspaceProject);
			}
			else {
				GradleUtil.importGradleProject(projectLocation.toFile(), monitor);
			}
		}
		catch (Exception e) {
			retval = GradleCore.createErrorStatus("can't create module project.", e);
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

		if (path != null) {
			if (LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(path.toOSString())) {
				retval = GradleCore.createErrorStatus(" Can't set WorkspaceProject root folder as project directory. ");
			}
		}

		return retval;
	}

}