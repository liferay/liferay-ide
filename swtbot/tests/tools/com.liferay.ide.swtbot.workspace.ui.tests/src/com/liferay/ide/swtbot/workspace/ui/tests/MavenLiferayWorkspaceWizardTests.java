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

package com.liferay.ide.swtbot.workspace.ui.tests;

import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class MavenLiferayWorkspaceWizardTests extends SwtbotBase
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @Test
    public void newMavenLiferayWorkspaceProjectWizard()
    {
        String workspaceName = "maven-liferay-workspace";

        wizardAction.openNewLiferayWorkspaceWizard();

        wizardAction.prepareLiferayWorkspaceMaven( workspaceName );

        wizardAction.finishToWait();

        wizardAction.openNewLiferayModuleWizard();

        String projectName = "test-maven-theme-in-lr-ws";

        wizardAction.prepareLiferayModuleMaven( projectName, THEME );

        wizardAction.finishToWait();

        wizardAction.openNewLiferayModuleWizard();

        projectName = "test-maven-mvc-portlet-in-lr-ws";

        wizardAction.prepareLiferayModuleMaven( projectName, MVC_PORTLET );

        wizardAction.finishToWait();

        wizardAction.openNewLiferayModuleWizard();

        projectName = "test-gradle-mvc-portlet";

        wizardAction.prepareLiferayModuleGradle( projectName, MVC_PORTLET );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );

        viewAction.deleteProject( workspaceName );
    }

}
