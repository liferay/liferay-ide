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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.projectImport.ProjectImportProvider;

import icons.MavenIcons;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;

/**
 * @author Joye Luo
 */
public class LiferayMavenWorkspaceBuilder extends LiferayWorkspaceBuilder {

	public LiferayMavenWorkspaceBuilder() {
		super(LiferayProjectType.LIFERAY_MAVEN_WORKSPACE);

		addListener(new LiferayMavenWorkspaceBuilderListener());
	}

	@Override
	public Icon getNodeIcon() {
		return MavenIcons.MavenLogo;
	}

	@Override
	public void setupRootModel(ModifiableRootModel model) {
		Project project = model.getProject();

		initWorkspace(project);
	}

	private static class LiferayMavenWorkspaceBuilderListener implements ModuleBuilderListener {

		@Override
		public void moduleCreated(@NotNull Module module) {
			Project project = module.getProject();
			ProjectImportProvider[] importProviders = ProjectImportProvider.PROJECT_IMPORT_PROVIDER.getExtensions();

			for (ProjectImportProvider importProvider : importProviders) {
				String importProviderId = importProvider.getId();

				if (importProviderId.equals("Maven")) {
					AddModuleWizard wizard = new AddModuleWizard(project, project.getBasePath(), importProvider);

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

					break;
				}
			}
		}

	}

}