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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;

import java.util.Properties;

import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;
import org.apache.maven.archetype.ArchetypeManager;
import org.apache.maven.archetype.catalog.Archetype;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.embedder.MavenImpl;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 * @author Seiphon Wang
 */
@SuppressWarnings("restriction")
public class NewMavenJSFModuleProjectProvider
	extends LiferayMavenProjectProvider implements NewLiferayProjectProvider<NewLiferayJSFModuleProjectOp> {

	public NewMavenJSFModuleProjectProvider() {
	}

	@Override
	public IStatus createNewProject(NewLiferayJSFModuleProjectOp op, IProgressMonitor monitor)
		throws CoreException, InterruptedException {

		IPath projectLocation = createArchetypeProject(op, monitor);

		FileUtil.delete(projectLocation.append("build.gradle"));

		Value<String> projectNameValue = op.getProjectName();

		String projectName = projectNameValue.content();

		CoreUtil.openProject(projectName, projectLocation, monitor);

		MavenUtil.updateProjectConfiguration(projectName, projectLocation.toOSString(), monitor);

		return Status.OK_STATUS;
	}

	protected IPath createArchetypeProject(NewLiferayJSFModuleProjectOp op, IProgressMonitor monitor)
		throws CoreException {

		IPath projectLocation = null;
		String javaPackage = "com.example";

		String projectName = get(op.getProjectName());

		IPath location = PathBridge.create(get(op.getLocation()));

		// for location we should use the parent location

		if (StringUtil.equals(location.lastSegment(), projectName)) {

			// use parent dir since maven archetype will generate new dir under this
			// location

			location = location.removeLastSegments(1);
		}

		String groupId = get(op.getProjectName());
		String artifactId = get(op.getProjectName());
		String version = "1.0.0";

		String archetypeArtifactId = get(op.getArchetype());

		Archetype archetype = new Archetype();

		String[] gav = archetypeArtifactId.split(":");

		String archetypeVersion = gav[gav.length - 1];

		archetype.setGroupId(gav[0]);
		archetype.setArtifactId(gav[1]);

		archetype.setVersion(archetypeVersion);

		Artifact artifact = AetherUtil.getAvailableArtifact(archetypeArtifactId);

		if (artifact == null) {
			throw new CoreException(LiferayCore.createErrorStatus("Unable to create project from archetype."));
		}

		Properties properties = new Properties();

		IWorkspaceRoot workspaceRoot = CoreUtil.getWorkspaceRoot();

		if (location == null) {
			location = workspaceRoot.getLocation();
		}

		try {
			MavenPluginActivator pluginActivator = MavenPluginActivator.getDefault();

			ArchetypeGenerationRequest request = new ArchetypeGenerationRequest();

			MavenImpl mavenImpl = pluginActivator.getMaven();

			request.setTransferListener(mavenImpl.createTransferListener(monitor));

			request.setArchetypeGroupId(artifact.getGroupId());
			request.setArchetypeArtifactId(artifact.getArtifactId());
			request.setArchetypeVersion(artifact.getVersion());

			RemoteRepository remoteRepository = AetherUtil.newCentralRepository();

			request.setArchetypeRepository(remoteRepository.getUrl());

			request.setGroupId(groupId);
			request.setArtifactId(artifactId);
			request.setVersion(version);
			request.setPackage(javaPackage);

			// the model does not have a package field

			request.setLocalRepository(mavenImpl.getLocalRepository());
			request.setRemoteArtifactRepositories(mavenImpl.getArtifactRepositories(true));
			request.setProperties(properties);
			request.setOutputDirectory(location.toPortableString());

			ArchetypeGenerationResult result = _getArchetyper().generateProjectFromArchetype(request);

			Exception cause = result.getCause();

			if (cause != null) {
				throw new CoreException(LiferayCore.createErrorStatus("Unable to create project from archetype."));
			}

			projectLocation = location.append(artifactId);

			if (FileUtil.notExists(projectLocation)) {
				throw new CoreException(LiferayCore.createErrorStatus("Can not create gradle JSF project. "));
			}
		}
		catch (Exception e) {
			throw new CoreException(LiferayCore.createErrorStatus("Failed to create JSF project. ", e));
		}

		return projectLocation;
	}

	private ArchetypeManager _getArchetyper() {
		MavenPluginActivator plugin = MavenPluginActivator.getDefault();

		org.eclipse.m2e.core.internal.archetype.ArchetypeManager archetypeManager = plugin.getArchetypeManager();

		return archetypeManager.getArchetyper();
	}

}