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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import com.liferay.ide.idea.ui.LiferayIdeaUI;

/**
 * @author Joye Luo
 */
public class DeployMavenModuleAction extends AbstractLiferayMavenGoalAction {

	public DeployMavenModuleAction() {
		super("Deploy", "Run deploy goal", LiferayIdeaUI.LIFERAY_ICON, "bundle-support:deploy");
	}

	@Override
	public boolean isEnabledAndVisible(AnActionEvent event) {
		Project project = event.getProject();
		VirtualFile file = getVirtualFile(event);

		VirtualFile baseDir = project.getBaseDir();

		VirtualFile mavenFile = baseDir.findChild("pom.xml");

		if ((file != null) && (mavenFile != null) && ProjectRootsUtil.isModuleContentRoot(file, project) &&
			!baseDir.equals(file)) {

			return true;
		}

		return false;
	}

}