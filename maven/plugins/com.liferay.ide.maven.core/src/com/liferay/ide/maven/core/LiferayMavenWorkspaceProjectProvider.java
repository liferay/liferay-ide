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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceProjectProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.archetype.catalog.Archetype;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Joye Luo
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class LiferayMavenWorkspaceProjectProvider
	extends LiferayMavenProjectProvider implements NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> {

	@Override
	public IStatus createNewProject(NewLiferayWorkspaceOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = Status.OK_STATUS;

		IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

		IPath location = PathBridge.create(op.getLocation().content());

		String projectName = op.getWorkspaceName().content();

		String groupId = projectName;
		String artifactId = projectName;

		String version = "1.0.0-SNAPSHOT";
		String javaPackage = "";

		String archetypeArtifactId = getData("archetypeGAV", String.class, "").get(0);
		Archetype archetype = new Archetype();
		String[] gav = archetypeArtifactId.split(":");

		String archetypeVersion = gav[gav.length - 1];

		archetype.setGroupId(gav[0]);
		archetype.setArtifactId(gav[1]);

		archetype.setVersion(archetypeVersion);

		Properties properties = new Properties();

		ResolverConfiguration resolverConfig = new ResolverConfiguration();

		ProjectImportConfiguration configuration = new ProjectImportConfiguration(resolverConfig);

		List<IProject> newProjects = projectConfigurationManager.createArchetypeProjects(
			location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor);

		if ((newProjects == null) || newProjects.isEmpty()) {
			retval = LiferayMavenCore.createErrorStatus("Unable to create liferay workspace project from archetype.");
		}
		else {
			for (IProject newProject : newProjects) {
				String[] gradleFiles = {"build.gradle", "settings.gradle", "gradle.properties"};

				for (String path : gradleFiles) {
					IFile gradleFile = newProject.getFile(path);

					if (FileUtil.exists(gradleFile)) {
						gradleFile.delete(true, monitor);
					}
				}
			}
		}

		boolean initBundle = op.getProvisionLiferayBundle().content();

		if (retval.isOK() && initBundle) {
			IProject workspaceProject = ProjectUtil.getProject(projectName);
			String bundleUrl = op.getBundleUrl().content();

			MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder(workspaceProject);

			mavenProjectBuilder.initBundle(workspaceProject, bundleUrl, monitor);
		}

		return retval;
	}

	@Override
	public <T> List<T> getData(String key, Class<T> type, Object... params) {
		if ("archetypeGAV".equals(key) && type.equals(String.class)) {
			List<T> retval = new ArrayList<>();

			String gav = LiferayMavenCore.getPreferenceString(
				LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_WORKSPACE, "");

			if (CoreUtil.empty(gav)) {
				gav = "com.liferay:com.liferay.project.templates.workspace:1.0.2";
			}

			retval.add(type.cast(gav));

			return retval;
		}

		return super.getData(key, type, params);
	}

	@Override
	public IStatus importProject(String location, IProgressMonitor monitor, boolean initBundle, String bundleUrl) {
		IStatus retval = Status.OK_STATUS;

		IPath path = new Path(location);

		String projectName = path.lastSegment();

		try {
			MavenUtil.importProject(location, monitor);

			if (initBundle) {
				IProject workspaceProject = ProjectUtil.getProject(projectName);

				MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder(workspaceProject);

				mavenProjectBuilder.initBundle(workspaceProject, bundleUrl, monitor);
			}
		}
		catch (Exception e) {
			retval = ProjectCore.createErrorStatus(e);
		}

		return retval;
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		IStatus retval = Status.OK_STATUS;

		// TODO validation maven project location

		return retval;
	}

}