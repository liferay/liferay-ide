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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;

import java.util.Collection;
import java.util.List;

import javax.swing.Icon;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.model.MavenExplicitProfiles;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

/**
 * @author Joye Luo
 */
public abstract class AbstractLiferayMavenGoalAction extends AnAction {

	public AbstractLiferayMavenGoalAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
		super(text, description, icon);
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		checkOrPerform(e.getDataContext(), true);
	}

	public boolean checkOrPerform(DataContext context, boolean perform) {
		Project project = MavenActionUtil.getProject(context);

		MavenProject mavenProject = MavenActionUtil.getMavenProject(context);

		if ((project == null) || (mavenProject == null)) {
			return false;
		}

		if (!perform) {
			return true;
		}

		String projectDir = mavenProject.getDirectory();

		MavenProjectsManager mavenProjectsManager = MavenActionUtil.getProjectsManager(context);

		MavenExplicitProfiles explicitProfiles = mavenProjectsManager.getExplicitProfiles();

		Collection<String> enabledProfiles = explicitProfiles.getEnabledProfiles();
		Collection<String> disabledProfiles = explicitProfiles.getDisabledProfiles();

		MavenRunnerParameters params = new MavenRunnerParameters(
			true, projectDir, goals, enabledProfiles, disabledProfiles);

		MavenRunConfigurationType.runConfiguration(project, params, null);

		return true;
	}

	public boolean isEnabledAndVisible(AnActionEvent e) {
		return checkOrPerform(e.getDataContext(), false);
	}

	@Override
	public void update(AnActionEvent event) {
		super.update(event);

		Presentation eventPresentation = event.getPresentation();

		eventPresentation.setEnabledAndVisible(isEnabledAndVisible(event));
	}

	protected List<String> goals;

}