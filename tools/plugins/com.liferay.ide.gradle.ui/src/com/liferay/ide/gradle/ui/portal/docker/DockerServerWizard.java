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

package com.liferay.ide.gradle.ui.portal.docker;

import com.github.dockerjava.api.model.Container;

import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.io.File;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Simon Jiang
 */
public class DockerServerWizard extends WizardFragment {

	public static IWorkspaceProject getLiferayWorkspaceProject() {
		IProject workspaceProject = getWorkspaceProject();

		if (workspaceProject != null) {
			return LiferayCore.create(IWorkspaceProject.class, getWorkspaceProject());
		}

		return null;
	}

	public static IProject getWorkspaceProject() {
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (isValidWorkspace(project)) {
				return project;
			}
		}

		return null;
	}

	public static boolean isValidGradleWorkspaceLocation(String location) {
		File workspaceDir = new File(location);

		File buildGradle = new File(workspaceDir, _BUILD_GRADLE_FILE_NAME);
		File settingsGradle = new File(workspaceDir, _SETTINGS_GRADLE_FILE_NAME);
		File gradleProperties = new File(workspaceDir, _GRADLE_PROPERTIES_FILE_NAME);

		if (FileUtil.notExists(buildGradle) || FileUtil.notExists(settingsGradle) ||
			FileUtil.notExists(gradleProperties)) {

			return false;
		}

		String settingsContent = FileUtil.readContents(settingsGradle, true);

		if (settingsContent != null) {
			Matcher matcher = _workspacePluginPattern.matcher(settingsContent);

			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

	public static boolean isValidWorkspace(IProject project) {
		if ((project != null) && (project.getLocation() != null) && isValidWorkspaceLocation(project.getLocation())) {
			return true;
		}

		return false;
	}

	public static boolean isValidWorkspaceLocation(IPath path) {
		if (FileUtil.notExists(path)) {
			return false;
		}

		return isValidWorkspaceLocation(path.toOSString());
	}

	public static boolean isValidWorkspaceLocation(String location) {
		if (isValidGradleWorkspaceLocation(location)) {
			return true;
		}

		return false;
	}

	public DockerServerWizard() {
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		_composite = new DockerServerSettingComposite(parent, handle);

		return _composite;
	}

	@Override
	public void enter() {
		if (_composite != null) {
			IServerWorkingCopy server = (IServerWorkingCopy)getTaskModel().getObject(TaskModel.TASK_SERVER);

			_composite.setServer(server);
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		if (_composite != null) {
			IServerWorkingCopy server = (IServerWorkingCopy)getTaskModel().getObject(TaskModel.TASK_SERVER);

			if (server != null) {
				PortalDockerServer dockerServer = (PortalDockerServer)server.loadAdapter(
					PortalDockerServer.class, new NullProgressMonitor());

				if (Objects.nonNull(dockerServer.getContainerName())) {
					Container dockerContainer = LiferayDockerClient.getDockerContainerByName(
						dockerServer.getContainerName());

					if (Objects.nonNull(dockerContainer)) {
						return false;
					}

					return true;
				}
			}

			return false;
		}

		return false;
	}

	@Override
	public boolean isForceLastFragment() {
		return true;
	}

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IServerWorkingCopy server = (IServerWorkingCopy)getTaskModel().getObject(TaskModel.TASK_SERVER);

		PortalDockerServer dockerServer = (PortalDockerServer)server.loadAdapter(PortalDockerServer.class, null);

		GradleUtil.runGradleTask(
			LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"createDockerContainer"},
			new String[] {"-x", "buildDockerImage"}, false, monitor);

		Container dockerContainer = LiferayDockerClient.getDockerContainerByName(dockerServer.getContainerName());

		if (Objects.isNull(dockerContainer)) {
			throw new CoreException(LiferayGradleUI.createErrorStatus("Can not create container."));
		}

		server.setAttribute(PortalDockerServer.DOCKER_CONTAINER_ID, dockerContainer.getId());

		IRuntime runtime = (IRuntime)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		if (Objects.nonNull(runtime) && runtime.isWorkingCopy()) {
			IRuntimeWorkingCopy runtimeWorkingCopy = (IRuntimeWorkingCopy)runtime;

			runtime = runtimeWorkingCopy.getOriginal();

			server.setRuntime(runtime);
		}

		server.save(true, monitor);
	}

	private static final String _BUILD_GRADLE_FILE_NAME = "build.gradle";

	private static final String _GRADLE_PROPERTIES_FILE_NAME = "gradle.properties";

	private static final String _SETTINGS_GRADLE_FILE_NAME = "settings.gradle";

	private static final Pattern _workspacePluginPattern = Pattern.compile(
		".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.workspace[\'\"].*", Pattern.MULTILINE | Pattern.DOTALL);

	private DockerServerSettingComposite _composite;

}