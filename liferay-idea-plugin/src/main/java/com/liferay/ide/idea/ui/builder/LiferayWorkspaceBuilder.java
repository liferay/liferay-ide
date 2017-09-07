/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.idea.ui.builder;

import com.intellij.ide.actions.ImportModuleAction;
import com.intellij.ide.util.newProjectWizard.AddModuleWizard;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.projectImport.ProjectImportProvider;
import com.liferay.ide.idea.ui.util.BladeCLI;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceBuilder extends ModuleBuilder {

    public LiferayWorkspaceBuilder() {
        super();

        this.addListener(new LiferayWorkpaceBuilderListener());
    }

    @Override
    public void setupRootModel(ModifiableRootModel model) throws ConfigurationException {
        final Project project = model.getProject();

        StringBuilder sb = new StringBuilder();

        sb.append("-b ");
        sb.append("\"" + project.getBasePath() + "\"");
        sb.append(" ");
        sb.append("init ");
        sb.append("-f");

        BladeCLI.execute(sb.toString());
    }

    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public String getPresentableName() {
        return _LIFERAY_WORKSPACE;
    }

    @Override
    public String getParentGroup() {
        return "Liferay";
    }

    @Override
    public String getDescription() {
        return _LIFERAY_WORKSPACE;
    }

    @Override
    public Icon getNodeIcon() {
        return IconLoader.getIcon("/icons/liferay.png");
    }

    static class LiferayWorkpaceBuilderListener implements ModuleBuilderListener {
        @Override
        public void moduleCreated(@NotNull Module module) {
            Project project = module.getProject();
            ProjectImportProvider[] importProviders = ProjectImportProvider.PROJECT_IMPORT_PROVIDER.getExtensions();

            for (ProjectImportProvider importProvider : importProviders) {
                if (importProvider.getId().equals("Gradle")) {
                    AddModuleWizard wizard = new AddModuleWizard(project, project.getBasePath(), importProvider);

                    Application application = ApplicationManager.getApplication();

                    application.invokeLater(new Runnable() {
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
    
    private final static String _LIFERAY_WORKSPACE = "Liferay Workspace";

}