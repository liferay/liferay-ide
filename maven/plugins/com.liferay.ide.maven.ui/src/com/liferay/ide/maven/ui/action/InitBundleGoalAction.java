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

package com.liferay.ide.maven.ui.action;

import com.liferay.ide.maven.core.ILiferayMavenConstants;
import com.liferay.ide.maven.core.MavenGoalUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Charles Wu
 * @author Simon Jiang
 */
public class InitBundleGoalAction extends MavenGoalAction {

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);

		action.setEnabled(LiferayWorkspaceUtil.isValidWorkspace(project));
	}

	@Override
	protected void afterGoal() {
		LiferayWorkspaceUtil.addPortalRuntime();
	}

	@Override
	protected String getMavenGoals() {
		return MavenGoalUtil.getMavenInitBundleGoal(plugin);
	}

	@Override
	protected String getPluginKey() {
		return ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_BUNDLE_SUPPORT_KEY;
	}

	@Override
	protected void updateProject(IProject p, IProgressMonitor monitor) {
	}

}