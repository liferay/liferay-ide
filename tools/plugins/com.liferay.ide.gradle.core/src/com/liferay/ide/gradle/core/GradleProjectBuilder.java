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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.AbstractProjectBuilder;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;

import java.io.IOException;

import java.util.List;

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

/**
 * @author Terry Jia
 */
public class GradleProjectBuilder extends AbstractProjectBuilder implements IWorkspaceProjectBuilder {

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

		// TODO Waiting for IDE-2850

		return null;
	}

	public IStatus initBundle(IProject project, String bundleUrl, IProgressMonitor monitor) {
		if (bundleUrl != null) {
			try {
				PropertiesConfiguration config = new PropertiesConfiguration(
					FileUtil.getFile(project.getFile("gradle.properties")));

				config.setProperty(WorkspaceConstants.DEFAULT_BUNDLE_URL_PROPERTY, bundleUrl);
				config.save();
			}
			catch (ConfigurationException ce) {
				GradleCore.logError(ce);
			}
		}

		_runGradleTask("initBundle", monitor);

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (CoreException ce) {
			GradleCore.logError(ce);
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus updateProjectDependency(IProject project, List<String[]> dependencies) throws CoreException {
		if (FileUtil.notExists(_gradleBuildFile)) {
			return Status.OK_STATUS;
		}

		try {
			GradleDependencyUpdater updater = new GradleDependencyUpdater(FileUtil.getFile(_gradleBuildFile));

			List<GradleDependency> existDependencies = updater.getAllDependencies();

			for (String[] dependency : dependencies) {
				GradleDependency gd = new GradleDependency(dependency[0], dependency[1], dependency[2]);

				if (!existDependencies.contains(gd)) {
					updater.insertDependency(gd);

					FileUtils.writeLines(FileUtil.getFile(_gradleBuildFile), updater.getGradleFileContents());

					GradleUtil.refreshProject(project);
				}
			}
		}
		catch (IOException ioe) {
			return GradleCore.createErrorStatus("Error updating gradle project dependency", ioe);
		}

		return Status.OK_STATUS;
	}

	private IStatus _runGradleTask(String task, IProgressMonitor monitor) {
		if (FileUtil.notExists(_gradleBuildFile)) {
			return GradleCore.createErrorStatus("No build.gradle file");
		}

		IStatus status = Status.OK_STATUS;

		try {
			monitor.beginTask(task, 100);

			GradleUtil.runGradleTask(getProject(), task, monitor);

			monitor.worked(80);

			getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);

			monitor.worked(10);
		}
		catch (Exception e) {
			status = GradleCore.createErrorStatus("Error running Gradle goal " + task, e);
		}

		return status;
	}

	private IFile _gradleBuildFile;

}