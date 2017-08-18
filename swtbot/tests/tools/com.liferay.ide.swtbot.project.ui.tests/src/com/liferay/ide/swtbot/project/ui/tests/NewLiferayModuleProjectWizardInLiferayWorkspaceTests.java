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

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public class NewLiferayModuleProjectWizardInLiferayWorkspaceTests extends SwtbotBase
{

    static String liferayWorkspaceName = "test-liferay-workspace";

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @AfterClass
    public static void cleanAll()
    {
        viewAction.deleteProject( liferayWorkspaceName );
    }

    @BeforeClass
    public static void createGradleLiferayWorkspace()
    {
        wizardAction.openNewLiferayWorkspaceWizard();

        wizardAction.prepareLiferayWorkspaceGradle( liferayWorkspaceName );

        wizardAction.finishToWait();
    }

    @Test
    public void createActivatorModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-activator-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, ACTIVATOR );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createApiModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-api-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, API );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createContentTargetingReportModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-content-targeting-report-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_REPORT );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createContentTargetingRuleModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-content-targeting-rule-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_RULE );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createContentTargetingTrackingActionModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-content-targeting-tracking-action-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_TRACKING_ACTION );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createControlMenuEntryModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-control-menu-entry-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTROL_MENU_ENTRY );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createFormFieldModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-form-field-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, FORM_FIELD );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createMvcPortletModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-mvc-portlet";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, MVC_PORTLET );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createPanelAppModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-panel-app-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PANEL_APP );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createPortletConfigurationIconModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-portlet-configuration-icon-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_CONFIGURATION_ICON );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createPortletModuleProjectInLiferayWorkspace() throws IOException
    {
        String projectName = "test-portlet-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createPortletProviderModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-portlet-provider-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_PROVIDER );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createPortletToolbarContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-portlet-toolbar-contributor-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_TOOLBAR_CONTRIBUTOR );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createRestModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-rest-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, REST );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createServiceBuilderModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-service-builder-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE_BUILDER );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createServiceModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-service-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createServiceWrapperModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-service-wrapper-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE_WRAPPER );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createSimulationPanelEntryModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-simulation-panel-entry-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SIMULATION_PANEL_ENTRY );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createTemplateContextContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-template-context-contributor-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createThemeContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-theme-contributor-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, THEME_CONTRIBUTOR );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "modules", projectName );
    }

    @Test
    public void createThemeModuleProjectInLiferayWorkspace()
    {
        String projectName = "test-theme-in-lws";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, THEME );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName, "wars", projectName );
    }

    @Before
    public void init()
    {
        Assume.assumeTrue( runTest() || runAllTests() );
    }

}
