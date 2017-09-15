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

package com.liferay.ide.idea.ui.actions;

import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import com.liferay.ide.idea.ui.LiferayIdeaUI;

/**
 * @author Andy Wu
 */
public class DeployModuleAction extends AbstractLiferayGradleTaskAction {

	public DeployModuleAction() {
		super("Deploy", "Run deploy task", LiferayIdeaUI.LIFERAY_ICON);
	}

	@Override
	public boolean isEnableAndVisible(AnActionEvent e) {
		Project project = e.getProject();
		VirtualFile file = getVirtualFile(e);

		if ((file != null) && ProjectRootsUtil.isModuleContentRoot(file, project)) {
			return true;
		}

		return false;
	}

	@Override
	protected String getTaskName() {
		return "deploy";
	}

	@Override
	protected String getWorkDirectory(AnActionEvent e) {
		VirtualFile virtualFile = getVirtualFile(e);

		ProjectRootManager projectRootManager = ProjectRootManager.getInstance(e.getProject());

		Module module = projectRootManager.getFileIndex().getModuleForFile(virtualFile);

		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

		VirtualFile[] roots = moduleRootManager.getModifiableModel().getContentRoots();

		assert roots != null && roots[0] != null;

		return roots[0].getCanonicalPath();
	}

}