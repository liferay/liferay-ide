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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.spring.NewLiferaySpringProjectOp;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import java.io.File;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class NewMavenSpringMVCPortletProjectProvider
	extends LiferayMavenProjectProvider
	implements NewLiferayProjectProvider<NewLiferaySpringProjectOp>, SapphireContentAccessor {

	@Override
	public IStatus createNewProject(NewLiferaySpringProjectOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = Status.OK_STATUS;

		String projectName = get(op.getProjectName());

		IPath location = PathBridge.create(get(op.getLocation()));

		String className = get(op.getComponentName());

		String liferayVersion = get(op.getLiferayVersion());

		String packageName = get(op.getPackageName());

		String framework = get(op.getFramework());

		String dependencyInjector = get(op.getDependencyInjector());

		String frameworkDependencies = get(op.getFrameworkDependencies());

		String viewType = get(op.getViewType());

		File targetDir = location.toFile();

		targetDir.mkdirs();

		String projectTemplateName = get(op.getProjectTemplateName());

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("-b maven ");
		sb.append("-d \"");
		sb.append(targetDir.getAbsolutePath());
		sb.append("\" ");

		IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (liferayWorkspaceProject != null) {
			LiferayMavenWorkspaceProject mavenWorkspaceProject = LiferayCore.create(
				LiferayMavenWorkspaceProject.class, liferayWorkspaceProject);

			if (mavenWorkspaceProject != null) {
				sb.append("--base \"");

				IPath workspaceLocation = liferayWorkspaceProject.getLocation();

				sb.append(workspaceLocation.toOSString());

				sb.append("\" ");
			}
		}

		sb.append("-v ");
		sb.append(liferayVersion);
		sb.append(" ");
		sb.append("-t ");
		sb.append(projectTemplateName);
		sb.append(" ");

		if (framework != null) {
			sb.append("--framework ");

			Map<String, String> springframeworks = WorkspaceConstants.springFrameworks;

			sb.append(springframeworks.get(framework));

			sb.append(" ");
		}

		if (viewType != null) {
			sb.append("--view-type ");

			Map<String, String> springviewtypes = WorkspaceConstants.springViewTypes;

			sb.append(springviewtypes.get(viewType));

			sb.append(" ");
		}

		if (dependencyInjector != null) {
			sb.append("--dependency-injector ");

			Map<String, String> springdependenciesinjectors = WorkspaceConstants.springDependenciesInjectors;

			sb.append(springdependenciesinjectors.get(dependencyInjector));

			sb.append(" ");
		}

		if (frameworkDependencies != null) {
			sb.append("--framework-dependencies ");

			Map<String, String> springframeworkdependeices = WorkspaceConstants.springFrameworkDependeices;

			sb.append(springframeworkdependeices.get(frameworkDependencies));

			sb.append(" ");
		}

		if (className != null) {
			sb.append("-c ");
			sb.append(className);
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

			IPath projectLocation = location;

			String lastSegment = location.lastSegment();

			if ((location != null) && (location.segmentCount() > 0)) {
				if (!lastSegment.equals(projectName)) {
					projectLocation = location.append(projectName);
				}
			}

			CoreUtil.openProject(projectName, projectLocation, monitor);

			MavenUtil.updateProjectConfiguration(projectName, projectLocation.toOSString(), monitor);
		}
		catch (Exception e) {
			ProjectCore.logError(e);

			retval = ProjectCore.createErrorStatus("can not create module project.", e);
		}

		return retval;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		return Status.OK_STATUS;
	}

}