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

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.ArtifactBuilder;
import com.liferay.ide.core.IWorkspaceProjectBuilder;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.gradle.core.model.GradleBuildScript;
import com.liferay.ide.gradle.core.model.GradleDependency;
import com.liferay.ide.project.core.AbstractProjectBuilder;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 */
@Component(property = "type=gradle", service = IWorkspaceProjectBuilder.class)
public class GradleProjectBuilder extends AbstractProjectBuilder implements ArtifactBuilder, IWorkspaceProjectBuilder {

	public GradleProjectBuilder() {
		super(null);
	}

	public GradleProjectBuilder(IProject project) {
		super(project);

		_gradleBuildFile = project.getFile("build.gradle");
	}

	@Override
	public IStatus buildLang(IFile langFile, IProgressMonitor monitor) throws CoreException {
		return _runGradleTask("buildLang", monitor);
	}

	@Override
	public IStatus buildService(IProgressMonitor monitor) throws CoreException {
		return _runGradleTask("buildService", monitor);
	}

	@Override
	public IStatus buildWSDD(IProgressMonitor monitor) throws CoreException {
		if (FileUtil.notExists(_gradleBuildFile)) {
			return LiferayGradleCore.createErrorStatus("There is no build.gradle file.");
		}

		GradleBuildScript gradleBuildScript = null;

		try {
			gradleBuildScript = new GradleBuildScript(FileUtil.getFile(_gradleBuildFile));
		}
		catch (IOException ioe) {
			return LiferayGradleCore.createErrorStatus("Could not parse dependencies for " + _gradleBuildFile, ioe);
		}

		List<GradleDependency> buildScriptDependencies = gradleBuildScript.getBuildScriptDependencies();

		Optional<GradleDependency> wsddBuilderDependency = buildScriptDependencies.stream(
		).filter(
			dependency -> "com.liferay".equals(dependency.getGroup())
		).filter(
			dependency -> "com.liferay.gradle.plugins.wsdd.builder".equals(dependency.getName())
		).findAny();

		if (wsddBuilderDependency.isPresent()) {
			GradleUtil.runGradleTask(_gradleBuildFile.getProject(), "buildWSDD", false, monitor);

			return Status.OK_STATUS;
		}
		else {
			return LiferayGradleCore.createErrorStatus("Could not find wsdd builder plugin in " + _gradleBuildFile);
		}
	}

	@Override
	public List<Artifact> getDependencies(String configuration) {
		if (FileUtil.notExists(_gradleBuildFile)) {
			return Collections.emptyList();
		}

		GradleBuildScript gradleBuildScript = null;

		try {
			gradleBuildScript = new GradleBuildScript(FileUtil.getFile(_gradleBuildFile));
		}
		catch (IOException ioe) {
		}

		if (gradleBuildScript == null) {
			return Collections.emptyList();
		}

		List<GradleDependency> dependencies = gradleBuildScript.getDependencies(configuration);

		List<Artifact> artifacts = dependencies.stream(
		).map(
			this::_dependencyToArtifact
		).collect(
			Collectors.toList()
		);

		IJavaProject javaProject = JavaCore.create(getProject());

		try {
			IClasspathEntry[] classpath = javaProject.getResolvedClasspath(true);

			for (Artifact artifact : artifacts) {
				Stream.of(
					classpath
				).map(
					this::classpathEntryToArtifact
				).filter(
					Objects::nonNull
				).filter(
					artifactWithSourcePath -> Objects.equals(artifactWithSourcePath, artifact)
				).findFirst(
				).ifPresent(
					artifactWithSourcePath -> {
						artifact.setSource(artifactWithSourcePath.getSource());
						artifact.setVersion(artifactWithSourcePath.getVersion());
					}
				);
			}
		}
		catch (JavaModelException jme) {
		}

		return artifacts;
	}

	public IStatus initBundle(IProject project, String bundleUrl, IProgressMonitor monitor) {
		if (bundleUrl != null) {
			try {
				PropertiesConfiguration config = new PropertiesConfiguration(
					FileUtil.getFile(project.getFile("gradle.properties")));

				config.setProperty(WorkspaceConstants.BUNDLE_URL_PROPERTY, bundleUrl);
				config.save();
			}
			catch (ConfigurationException ce) {
				LiferayGradleCore.logError(ce);
			}
		}

		return _runGradleTask(project, "initBundle", monitor);
	}

	@Override
	public IStatus updateDependencies(IProject project, List<Artifact> dependencyArtifacts) throws CoreException {
		if (FileUtil.notExists(_gradleBuildFile)) {
			return Status.OK_STATUS;
		}

		try {
			GradleBuildScript gradleBuildScript = new GradleBuildScript(FileUtil.getFile(_gradleBuildFile));

			List<GradleDependency> existingDependencies = gradleBuildScript.getDependencies("*");

			List<Artifact> existingArtifacts = existingDependencies.stream(
			).map(
				this::_dependencyToArtifact
			).collect(
				Collectors.toList()
			);

			for (Artifact dependencyArtifact : dependencyArtifacts) {
				if (!existingArtifacts.contains(dependencyArtifact)) {
					gradleBuildScript.insertDependency(_artifactToDependency(dependencyArtifact));

					FileUtils.writeLines(FileUtil.getFile(_gradleBuildFile), gradleBuildScript.getFileContents());

					GradleUtil.refreshProject(project);
				}
			}
		}
		catch (IOException ioe) {
			return LiferayGradleCore.createErrorStatus("Error updating gradle project dependency", ioe);
		}

		return Status.OK_STATUS;
	}

	private GradleDependency _artifactToDependency(Artifact artifact) {
		return new GradleDependency(
			artifact.getConfiguration(), artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), -1,
			-1);
	}

	private Artifact _dependencyToArtifact(GradleDependency gradleDependency) {
		return new Artifact(
			gradleDependency.getGroup(), gradleDependency.getName(), gradleDependency.getVersion(),
			gradleDependency.getConfiguration(), null);
	}

	private IStatus _runGradleTask(IProject project, String task, IProgressMonitor monitor) {
		if (FileUtil.notExists(project.getFile("build.gradle"))) {
			return LiferayGradleCore.createErrorStatus("No build.gradle file");
		}

		IStatus status = Status.OK_STATUS;

		try {
			monitor.beginTask(task, 100);

			GradleUtil.runGradleTask(project, task, false, monitor);

			monitor.worked(80);

			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			monitor.worked(10);
		}
		catch (Exception e) {
			status = LiferayGradleCore.createErrorStatus("Error running Gradle goal " + task, e);
		}

		return status;
	}

	private IStatus _runGradleTask(String task, IProgressMonitor monitor) {
		if (FileUtil.notExists(_gradleBuildFile)) {
			return LiferayGradleCore.createErrorStatus("No build.gradle file");
		}

		IStatus status = Status.OK_STATUS;

		try {
			monitor.beginTask(task, 100);

			GradleUtil.runGradleTask(getProject(), task, false, monitor);

			monitor.worked(80);

			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);

			monitor.worked(10);
		}
		catch (Exception e) {
			status = LiferayGradleCore.createErrorStatus("Error running Gradle goal " + task, e);
		}

		return status;
	}

	private IFile _gradleBuildFile;

}