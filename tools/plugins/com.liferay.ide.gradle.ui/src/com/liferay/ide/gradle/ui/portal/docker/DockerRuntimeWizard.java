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

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Image;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.server.core.portal.docker.PortalDockerRuntime;
import com.liferay.ide.server.util.LiferayDockerClient;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

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
		return _composite.isComplete();
	}

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		GradleUtil.runGradleTask(
			LiferayWorkspaceUtil.getWorkspaceProject(), new String[] {"buildDockerImage"}, monitor);
		    
		try (DockerClient dockerClient = LiferayDockerClient.getDockerClient()) {
			ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();

			listImagesCmd.withShowAll(true);
			listImagesCmd.withDanglingFilter(false);

			PortalDockerRuntime portalDockerRuntime = getPortalDockerRuntime(runtime);

			listImagesCmd.withImageNameFilter(portalDockerRuntime.getImageTag());

			List<Image> imagetList = listImagesCmd.exec();

			if (ListUtil.isNotEmpty(imagetList)) {
				Image image = imagetList.get(0);

				portalDockerRuntime.setImageId(image.getId());
			}
		}
		catch (Exception e) {
			LiferayGradleUI.logError("Failed to create docker runtime", e);
		}
	}

	protected PortalDockerRuntime getPortalDockerRuntime(IRuntimeWorkingCopy runtime) {
		return (PortalDockerRuntime)runtime.loadAdapter(PortalDockerRuntime.class, null);
	}

	protected List<WizardFragment> childFragments;

	private DockerRuntimeSettingComposite _composite;

}