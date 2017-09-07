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

package com.liferay.ide.idea.ui.action;

import com.intellij.ide.util.newProjectWizard.AbstractProjectWizard;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.DefaultModulesProvider;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.ui.wizard.NewLiferayModuleWizard;
import com.liferay.ide.idea.util.LiferayWorkspaceUtil;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Terry Jia
 */
public class NewLiferayModuleAction extends AnAction implements DumbAware {

    public NewLiferayModuleAction() {
        super(LiferayIdeaUI.LIFERAY_ICON);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e);

        if (project == null || !LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(project.getBasePath())) {
            Messages.showErrorDialog(
            	"Unable to detect current project as a Liferay workspace", 
            	"No Liferay workspace");

            return;
        }

        final Object dataFromContext = prepareDataFromContext(e);

        String defaultPath = null;

        final VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (virtualFile != null && virtualFile.isDirectory()) {
            defaultPath = virtualFile.getPath();
        }

        final NewLiferayModuleWizard wizard = new NewLiferayModuleWizard(project, new DefaultModulesProvider(project), defaultPath);

        if (wizard.showAndGet()) {
            createModuleFromWizard(project, dataFromContext, wizard);
        }
    }

    @Nullable
    public Module createModuleFromWizard(Project project, @Nullable Object dataFromContext, AbstractProjectWizard wizard) {
        final ProjectBuilder builder = wizard.getProjectBuilder();
        if (builder instanceof ModuleBuilder) {
            final ModuleBuilder moduleBuilder = (ModuleBuilder) builder;
            if (moduleBuilder.getName() == null) {
                moduleBuilder.setName(wizard.getProjectName());
            }
            if (moduleBuilder.getModuleFilePath() == null) {
                moduleBuilder.setModuleFilePath(wizard.getModuleFilePath());
            }
        }
        if (!builder.validate(project, project)) {
            return null;
        }
        Module module;
        if (builder instanceof ModuleBuilder) {
            module = ((ModuleBuilder) builder).commitModule(project, null);
            if (module != null) {
                processCreatedModule(module, dataFromContext);
            }
            return module;
        } else {
            List<Module> modules = builder.commit(project, null, new DefaultModulesProvider(project));
            if (builder.isOpenProjectSettingsAfter()) {
                ModulesConfigurator.showDialog(project, null, null);
            }
            module = modules == null || modules.isEmpty() ? null : modules.get(0);
        }
        project.save();
        return module;
    }

    @Nullable
    protected Object prepareDataFromContext(final AnActionEvent e) {
        return null;
    }

    protected void processCreatedModule(final Module module, @Nullable final Object dataFromContext) {
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabled(getEventProject(e) != null);
    }
}
