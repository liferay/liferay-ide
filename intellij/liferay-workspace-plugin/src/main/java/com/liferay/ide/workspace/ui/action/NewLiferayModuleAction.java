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

package com.liferay.ide.workspace.ui.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.DefaultModulesProvider;
import com.intellij.openapi.roots.ui.configuration.actions.NewModuleAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.liferay.ide.workspace.ui.UI;
import com.liferay.ide.workspace.ui.util.LiferayWorkspaceUtil;
import com.liferay.ide.workspace.ui.wizard.NewLiferayModuleWizard;

/**
 * @author Terry Jia
 */
public class NewLiferayModuleAction extends NewModuleAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e);

        if (project == null || !LiferayWorkspaceUtil.isValidGradleWorkspaceLocation(project.getBasePath())) {
            Messages.showErrorDialog(UI.UNABLE_TO_DETECT_CURRENT_PROJECT_AS_LIFERAY_WORKSPACE, UI.NO_LIFERAY_WORKSPACE);

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

}
