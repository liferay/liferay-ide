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
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;

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
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewMavenModuleProjectProvider
	extends LiferayMavenProjectProvider implements NewLiferayProjectProvider<NewLiferayModuleProjectOp> {

	@Override
	public IStatus createNewProject(NewLiferayModuleProjectOp op, IProgressMonitor monitor) throws CoreException {
		IStatus retval = null;

		IProjectConfigurationManager projectConfigurationManager = MavenPlugin.getProjectConfigurationManager();

		IPath location = PathBridge.create(op.getLocation().content());

		String groupId = op.getGroupId().content();
		String artifactId = op.getProjectName().content();
		String version = op.getArtifactVersion().content();
		String javaPackage = op.getPackageName().content();
		String className = op.getComponentName().content();
		String serviceName = op.getServiceName().content();

		String archetypeArtifactId = op.getArchetype().content();

		Archetype archetype = new Archetype();

		String[] gav = archetypeArtifactId.split(":");

		String archetypeVersion = gav[gav.length - 1];

		archetype.setGroupId(gav[0]);
		archetype.setArtifactId(gav[1]);

		archetype.setVersion(archetypeVersion);

		Properties properties = new Properties();

		if (archetype.getArtifactId().endsWith("service.builder")) {
			String apiPath = ":" + artifactId + "-api";

			properties.put("apiPath", apiPath);
		}

		properties.put("buildType", "maven");
		properties.put("package", javaPackage);
		properties.put("className", className == null ? "" : className);
		properties.put("projectType", "standalone");
		properties.put("serviceClass", serviceName == null ? "" : serviceName);
		properties.put("serviceWrapperClass", serviceName == null ? "" : serviceName);
		properties.put("contributorType", artifactId);
		properties.put("author", "liferay");

		for (PropertyKey propertyKey : op.getPropertyKeys()) {
			String key = propertyKey.getName().content();
			String value = propertyKey.getValue().content();

			properties.put(key, value);
		}

		if (serviceName != null) {
			properties.put("service", serviceName);
		}

		ResolverConfiguration resolverConfig = new ResolverConfiguration();

		ProjectImportConfiguration configuration = new ProjectImportConfiguration(resolverConfig);

		List<IProject> newProjects = projectConfigurationManager.createArchetypeProjects(
			location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor);

		ElementList<ProjectName> projectNames = op.getProjectNames();

		if ((newProjects == null) || newProjects.isEmpty()) {
			retval = LiferayMavenCore.createErrorStatus("Unable to create project from archetype.");
		}
		else {
			for (IProject newProject : newProjects) {
				projectNames.insert().setName(newProject.getName());

				String[] gradleFiles = {"build.gradle", "settings.gradle"};

				for (String path : gradleFiles) {
					IFile gradleFile = newProject.getFile(path);

					if (FileUtil.exists(gradleFile)) {
						gradleFile.delete(true, monitor);
					}
				}
			}

			retval = Status.OK_STATUS;
		}

		return retval;
	}

	@Override
	public <T> List<T> getData(String key, Class<T> type, Object... params) {
		if ("archetypeGAV".equals(key) && type.equals(String.class) && (params.length == 1)) {
			List<T> retval = new ArrayList<>();

			String templateName = params[0].toString();

			String gav = LiferayMavenCore.getPreferenceString(
				LiferayMavenCore.PREF_ARCHETYPE_PROJECT_TEMPLATE_PREFIX + templateName, "");

			if (CoreUtil.empty(gav)) {
				gav = "com.liferay:com.liferay.project.templates." + templateName.replace("-", ".") + ":1.0.0";
			}

			retval.add(type.cast(gav));

			return retval;
		}

		return super.getData(key, type, params);
	}

	@Override
	public IStatus validateProjectLocation(String projectName, IPath path) {
		return Status.OK_STATUS;
	}

}