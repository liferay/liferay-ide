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

package com.liferay.ide.idea.ui.modules;

import com.intellij.ide.actions.ImportModuleAction;
import com.intellij.ide.util.newProjectWizard.AddModuleWizard;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;

import icons.GradleIcons;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;

/**
 * @author Joye Luo
 */
public class LiferayGradleWorkspaceBuilder extends LiferayWorkspaceBuilder {

	public LiferayGradleWorkspaceBuilder() {
		super(LiferayProjectType.LIFERAY_GRADLE_WORKSPACE);
		addListener(new LiferayGradleWorkpaceBuilderListener());
	}

	@Override
	public Icon getNodeIcon() {
		return GradleIcons.Gradle;
	}

	@Override
	public void setupRootModel(ModifiableRootModel model) {
		Project project = model.getProject();

		initWorkspace(project);
	}

	private static class LiferayGradleWorkpaceBuilderListener implements ModuleBuilderListener {

		@Override
		public void moduleCreated(@NotNull Module module) {
			Project project = module.getProject();

			ProjectDataManager projectDataManager = ServiceManager.getService(ProjectDataManager.class);

			LiferayGradleProjectImportBuilder gradleProjectImportBuilder = new LiferayGradleProjectImportBuilder(
				projectDataManager);

			LiferayGradleProjectImportProvider gradleProjectImportProvider = new LiferayGradleProjectImportProvider(
				gradleProjectImportBuilder);

			AddModuleWizard wizard = new AddModuleWizard(project, project.getBasePath(), gradleProjectImportProvider);

			Application application = ApplicationManager.getApplication();

			application.invokeLater(
				new Runnable() {

					@Override
					public void run() {
						if (wizard.showAndGet()) {
							ImportModuleAction.createFromWizard(project, wizard);
						}
					}

				});
		}

	}

}