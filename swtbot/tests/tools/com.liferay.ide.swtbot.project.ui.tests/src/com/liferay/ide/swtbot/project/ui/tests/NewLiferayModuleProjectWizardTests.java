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
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 */
public class NewLiferayModuleProjectWizardTests extends SwtbotBase
{

    @Test
    public void createMvcportletModuleProject()
    {
        final String projectName = "test-mvc-portlet";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, MVC_PORTLET );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createMvcPortletModuleProjectInLiferayWorkspace() throws IOException
    {
        wizardAction.openNewLiferayWorkspaceWizard();

        final String liferayWorkspaceName = "liferayWorkspace";

        wizardAction.prepareLiferayWorkspaceGradle( liferayWorkspaceName );

        wizardAction.finishToWait();

        wizardAction.openNewLiferayModuleWizard();

        final String projectName = "test-mvc-portlet-in_lws";

        wizardAction.prepareLiferayModuleGradle( projectName, MVC_PORTLET );

        wizardAction.finishToWait();

        viewAction.deleteProject( liferayWorkspaceName );
    }

    @Test
    public void createServiceModuleProject()
    {
        final String projectName = "test-service";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE );

        wizardAction.next();

        wizardAction.openSelectServiceDialog();

        dialogAction.prepareText( "*lifecycleAction" );

        dialogAction.confirm();

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createServiceBuilderModuleProject()
    {
        final String projectName = "test-service-builder";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE_BUILDER );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createActivatorModuleProject()
    {
        final String projectName = "test-activator";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, ACTIVATOR );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createApiModuleProject()
    {
        final String projectName = "test-api";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, API );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createContentTargetingReportModuleProject()
    {
        final String projectName = "test-content-targeting-report";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_REPORT );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createContentTargetingRuleModuleProject()
    {
        final String projectName = "test-content-targeting-rule";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_RULE );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createContentTargetingTrackingActionModuleProject()
    {
        final String projectName = "test-content-targeting-tracking-action";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_TRACKING_ACTION );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createControlMenuEntryModuleProject()
    {
        final String projectName = "test-control-menu-entry";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTROL_MENU_ENTRY );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createFormFieldModuleProject()
    {
        final String projectName = "test-form-field";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, FORM_FIELD );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPanelAppModuleProject()
    {
        final String projectName = "test-panel-app";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PANEL_APP );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPortletModuleProject()
    {
        final String projectName = "test-portlet";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPortletConfigurationIconModuleProject()
    {
        final String projectName = "test-portlet-configuration-icon";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_CONFIGURATION_ICON );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPortletProviderModuleProject()
    {
        final String projectName = "test-portlet-provider";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_PROVIDER );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPortletToolbarContributorModuleProject()
    {
        final String projectName = "test-portlet-toolbar-contributor";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_TOOLBAR_CONTRIBUTOR );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createRestModuleProject()
    {
        final String projectName = "test-rest";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, REST );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createServiceWrapperModuleProject()
    {
        final String projectName = "test-service-wrapper";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE_WRAPPER );

        wizardAction.next();

        wizardAction.openSelectServiceDialog();

        dialogAction.prepareText( "*BookmarksEntryLocalServiceWrapper" );

        dialogAction.confirm();

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createSimulationPanelEntryModuleProject()
    {
        final String projectName = "test-simulation-panel-entry";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SIMULATION_PANEL_ENTRY );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createTemplateContextContributorModuleProject()
    {
        final String projectName = "test-template-context-contributor";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createThemeModuleProject()
    {
        final String projectName = "test-theme";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, THEME );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createThemeContributor()
    {
        final String projectName = "test-theme-contributor";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, THEME_CONTRIBUTOR );

        wizardAction.finishToWait();

        viewAction.deleteProject( projectName );
    }

}
