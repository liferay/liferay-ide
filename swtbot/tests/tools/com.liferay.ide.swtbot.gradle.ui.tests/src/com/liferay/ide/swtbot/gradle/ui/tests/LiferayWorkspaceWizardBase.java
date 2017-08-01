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

package com.liferay.ide.swtbot.gradle.ui.tests;

import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayWorkspaceWizard;
import com.liferay.ide.swtbot.project.ui.tests.BaseNewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.ui.page.Tree;

/**
 * @author Terry Jia
 */
public class LiferayWorkspaceWizardBase extends BaseNewLiferayModuleProjectWizard
{

    protected String projectName = "workspace-project";

    protected String serverName = "Liferay 7.0 CE Server";

    protected NewLiferayWorkspaceWizard newLiferayWorkspaceProjectWizard =
        new NewLiferayWorkspaceWizard( bot, INDEX_VALIDATION_WORKSPACE_NAME_MESSAGE );

    protected Tree projectTree = ide.getPackageExporerView().getProjectTree();

}
