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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.model.GradleBuildScript;
import com.liferay.ide.gradle.core.model.GradleDependency;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class GradleProjectProvider
	extends AbstractLiferayProjectProvider
	implements NewLiferayProjectProvider<NewLiferayModuleProjectOp>, SapphireContentAccessor {

	public GradleProjectProvider() {
		super(new Class<?>[] {IProject.class});
	}

	@Override
	public IStatus createNewProject(NewLiferayModuleProjectOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = Status.OK_STATUS;

		String projectName = get(op.getProjectName());

		IPath location = PathBridge.create(get(op.getLocation()));

		String className = get(op.getComponentName());

		String liferayVersion = get(op.getLiferayVersion());

		String serviceName = get(op.getServiceName());

		String packageName = get(op.getPackageName());

		String contributorType = get(op.getContributorType());

		ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

		List<String> properties = new ArrayList<>();

		for (PropertyKey propertyKey : propertyKeys) {
			properties.add(get(propertyKey.getName()) + "=" + get(propertyKey.getValue()));
		}

		File targetDir = location.toFile();

		targetDir.mkdirs();

		String projectTemplateName = get(op.getProjectTemplateName());

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("-q ");
		sb.append("-d \"");
		sb.append(targetDir.getAbsolutePath());
		sb.append("\" ");
		sb.append("--base \"");

		IProject liferayWorkspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		IPath workspaceLocation = liferayWorkspaceProject.getLocation();

		sb.append(workspaceLocation.toOSString());

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

		if (contributorType != null) {
			sb.append("-C ");
			sb.append(contributorType);
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

			GradleUtil.refreshProject(liferayWorkspaceProject);
		}
		catch (Exception e) {
			retval = LiferayGradleCore.createErrorStatus("Can not create module project: " + e.getMessage(), e);
		}

		return retval;
	}

	@Override
	public synchronized ILiferayProject provide(Class<?> type, Object adaptable) {
		if (adaptable instanceof IProject) {
			IProject project = (IProject)adaptable;

			try {
				if (!LiferayWorkspaceUtil.isValidWorkspace(project) && LiferayNature.hasNature(project) &&
					project.hasNature("org.eclipse.buildship.core.gradleprojectnature")) {

					boolean hasDynamicWebFaceSet = ProjectUtil.hasFacet(
						project, ProjectFacetsManager.getProjectFacet("jst.web"));

					if ((ProjectUtil.isFacetedGradleBundleProject(project) || hasDynamicWebFaceSet) &&
						_inGradleWorkspaceWars(project) && type.isAssignableFrom(FacetedGradleBundleProject.class)) {

						return new FacetedGradleBundleProject(project);
					}
					else if (type.isAssignableFrom(LiferayGradleProject.class)) {
						return new LiferayGradleProject(project);
					}
				}
			}
			catch (Exception e) {
			}
		}

		return null;
	}

	private boolean _inGradleWorkspaceWars(IProject project) {
		IProject workspaceProject = LiferayWorkspaceUtil.getWorkspaceProject();

		if (Objects.isNull(workspaceProject)) {
			return false;
		}

		IFile settingsGradleFile = workspaceProject.getFile("settings.gradle");

		GradleBuildScript gradleBuildScript = null;

		try {
			gradleBuildScript = new GradleBuildScript(FileUtil.getFile(settingsGradleFile));
		}
		catch (IOException ioe) {
		}

		String workspacePluginVersion = Optional.ofNullable(
			gradleBuildScript
		).flatMap(
			buildScript -> {
				List<GradleDependency> dependencies = buildScript.getBuildScriptDependencies();

				return dependencies.stream(
				).filter(
					dep -> Objects.equals("com.liferay", dep.getGroup())
				).filter(
					dep -> Objects.equals("com.liferay.gradle.plugins.workspace", dep.getName())
				).filter(
					dep -> CoreUtil.isNotNullOrEmpty(dep.getVersion())
				).map(
					dep -> dep.getVersion()
				).findFirst();
			}
		).get();

		if (CoreUtil.compareVersions(Version.parseVersion(workspacePluginVersion), new Version("2.5.0")) < 0) {
			return ProjectUtil.isWorkspaceWars(project);
		}

		return true;
	}

}