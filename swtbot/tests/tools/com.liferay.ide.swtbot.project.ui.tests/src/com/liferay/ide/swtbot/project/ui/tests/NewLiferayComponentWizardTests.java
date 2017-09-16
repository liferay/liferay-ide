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

package com.liferay.ide.swtbot.project.ui.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class NewLiferayComponentWizardTests extends SwtbotBase
{

    @Test
    public void newComponentClassOnModelListenerTest()
    {
        String projectName = "test-component-maven";

        wizardAction.openNewLiferayModuleWizard();
        wizardAction.prepareLiferayModuleMaven( projectName, MVC_PORTLET );
        wizardAction.finishToWait();

        wizardAction.openNewLiferayComponentClassWizard();
        wizardAction.prepareComponentClass( projectName, MODEL_LISTENER );
        wizardAction.openSelectModelClassAndServiceDialog();

        dialogAction.prepareText( "*com.liferay.blogs.kernel.model.BlogsEntry" );
        dialogAction.confirm();

        wizardAction.finishToWait();

        assertTrue(
            viewAction.fetchProjectFile(
                projectName, "src/main/java", "test.component.maven.portlet",
                "TestComponentMavenModelListener.java" ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void newComponentClassOnPortletTest()
    {
        String projectName = "test-component";

        wizardAction.openNewLiferayModuleWizard();
        wizardAction.prepareLiferayModuleGradle( projectName );
        wizardAction.finishToWait();

        wizardAction.openNewLiferayComponentClassWizard();
        wizardAction.prepareComponentClass( projectName );
        wizardAction.finishToWait();

        assertTrue(
            viewAction.fetchProjectFile(
                projectName, "src/main/java", "content", "TestComponentPortlet.java" ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void newComponentClassOnServiceWrapperTest()
    {
        String projectName = "test-component-gradle";

        wizardAction.openNewLiferayModuleWizard();
        wizardAction.prepareLiferayModuleGradle( projectName );
        wizardAction.finishToWait();

        wizardAction.openNewLiferayComponentClassWizard();
        wizardAction.prepareComponentClass( projectName, SERVICE_WRAPPER_UPCASE );
        wizardAction.openSelectModelClassAndServiceDialog();

        dialogAction.prepareText( "*bookmarksEntryLocal" );
        dialogAction.confirm();

        wizardAction.finishToWait();

        assertTrue(
            viewAction.fetchProjectFile(
                projectName, "src/main/java", "content", "TestComponentGradleServiceHook.java" ).isVisible() );

        viewAction.deleteProject( projectName );
    }

}
