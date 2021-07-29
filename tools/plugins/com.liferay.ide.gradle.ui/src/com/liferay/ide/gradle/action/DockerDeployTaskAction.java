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

package com.liferay.ide.gradle.action;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Seiphon Wang
 */
public class DockerDeployTaskAction extends GradleTaskAction {

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);

		if (Objects.isNull(project)) {
			return;
		}

		boolean hasUnsupportedModules = false;

		if (fSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)fSelection;

			Object element = structuredSelection.getFirstElement();

			if (Objects.nonNull(element) && (element instanceof IProject)) {
				IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, (IProject)element);

				if ((bundleProject != null) &&
					(bundleProject.isWarCoreExtModule() || bundleProject.isFragmentBundle())) {

					hasUnsupportedModules = true;
				}
			}
		}

		action.setEnabled(
			ServerUtil.isDockerServerExist() && LiferayWorkspaceUtil.inLiferayWorkspace(project) &&
			!hasUnsupportedModules);
	}

	@Override
	protected String getGradleTaskName() {
		return "dockerDeploy";
	}

}