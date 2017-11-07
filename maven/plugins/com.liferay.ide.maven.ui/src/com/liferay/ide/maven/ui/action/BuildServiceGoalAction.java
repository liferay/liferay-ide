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
import com.liferay.ide.maven.core.MavenProjectBuilder;
import com.liferay.ide.maven.core.MavenUtil;
import com.liferay.ide.maven.ui.LiferayMavenUI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class BuildServiceGoalAction extends MavenGoalAction {

	@Override
	protected String getMavenGoals() {
		return MavenGoalUtil.getMavenBuildServiceGoal(plugin);
	}

	@Override
	protected String getPluginKey() {
		return ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_SERVICE_BUILDER_KEY;
	}

	@Override
	protected void updateProject(IProject p, IProgressMonitor monitor) {
		MavenProjectBuilder builder = new MavenProjectBuilder(p);

		try {
			IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(p, monitor);

			builder.refreshSiblingProject(projectFacade, monitor);
		}
		catch (CoreException ce) {
			LiferayMavenUI.logError("Unable to refresh sibling project", ce);
		}
	}

}