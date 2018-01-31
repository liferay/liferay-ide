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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import com.liferay.ide.idea.ui.LiferayIdeaUI;

/**
 * @author Andy Wu
 */
public class InitBundleAction extends AbstractLiferayGradleTaskAction {

	public InitBundleAction() {
		super("InitBundle", "Run initBundle task", LiferayIdeaUI.LIFERAY_ICON, "initBundle");
	}

	@Override
	public boolean isEnabledAndVisible(AnActionEvent event) {
		Project project = event.getProject();

		VirtualFile baseDir = project.getBaseDir();

		if (baseDir.equals(getVirtualFile(event))) {
			return true;
		}

		return false;
	}

	@Override
	protected String getWorkingDirectory(AnActionEvent event) {
		Project project = event.getRequiredData(CommonDataKeys.PROJECT);

		return project.getBasePath();
	}

}