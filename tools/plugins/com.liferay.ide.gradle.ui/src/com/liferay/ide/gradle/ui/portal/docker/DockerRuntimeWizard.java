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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import org.gradle.tooling.model.GradleProject;

/**
 * @author Simon Jiang
 */
public class DockerRuntimeWizard extends WizardFragment {

	public DockerRuntimeWizard() {
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		_composite = new DockerRuntimeSettingComposite(parent, handle);

		return _composite;
	}

	@Override
	public void enter() {
		if (_composite != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

			_composite.setRuntime(runtime);
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		PortalDockerRuntime portalDockerRuntime = getPortalDockerRuntime(runtime);

		if ((portalDockerRuntime.getImageRepo() != null) && (portalDockerRuntime.getImageTag() != null) &&
			(portalDockerRuntime.getImageId() == null) && _composite.isComplete()) {

			return true;
		}

		return false;
	}

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		GradleUtil.runGradleTask(
			LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"buildDockerImage"}, _getWarCoreExtIgngoreTasks(),
			false, monitor);

		PortalDockerRuntime portalDockerRuntime = getPortalDockerRuntime(runtime);

		String imageRepoTag = String.join(":", portalDockerRuntime.getImageRepo(), portalDockerRuntime.getImageTag());

		String dockerImageId = LiferayDockerClient.getDockerImageId(imageRepoTag);

		if (Objects.nonNull(dockerImageId)) {
			portalDockerRuntime.setImageId(dockerImageId);
		}

		runtime.save(true, monitor);
	}

	protected PortalDockerRuntime getPortalDockerRuntime(IRuntimeWorkingCopy runtime) {
		return (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);
	}

	private String[] _getWarCoreExtIgngoreTasks() {
		ArrayList<String> ignorTasks = new ArrayList<>();

		List<IProject> warCoreExtProjects = LiferayWorkspaceUtil.getWarCoreExtModules();

		if (ListUtil.isNotEmpty(warCoreExtProjects)) {
			for (IProject project : warCoreExtProjects) {
				GradleProject gradleProject = GradleUtil.getGradleProject(project);

				if (Objects.nonNull(gradleProject)) {
					ignorTasks.add("-x");
					ignorTasks.add(gradleProject.getPath() + ":buildExtInfo");
					ignorTasks.add("-x");
					ignorTasks.add(gradleProject.getPath() + ":deploy");
					ignorTasks.add("-x");
					ignorTasks.add(gradleProject.getPath() + ":dockerDeploy");
				}
			}
		}

		return ignorTasks.toArray(new String[0]);
	}

	private DockerRuntimeSettingComposite _composite;

}