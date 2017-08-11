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
        viewAction.deleteProjects();
    }

    @BeforeClass
    public static void createGradleLiferayWorkspace()
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        wizardAction.openNewLiferayWorkspaceWizard();

        wizardAction.prepareLiferayWorkspaceGradle( "test-liferay-workspace" );

        wizardAction.finish();
    }

    @Test
    public void createActivatorModuleProjectInLiferayWorkspace()
    {
        String projectName = "testActivatorProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, ACTIVATOR );

        wizardAction.finish();
    }

    @Test
    public void createApiModuleProjectInLiferayWorkspace()
    {
        String projectName = "testApiProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, API );

        wizardAction.finish();
    }

    @Test
    public void createContentTargetingReportModuleProjectInLiferayWorkspace()
    {
        String projectName = "testContentTargetingReportProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_REPORT );

        wizardAction.finish();
    }

    @Test
    public void createContentTargetingRuleModuleProjectInLiferayWorkspace()
    {
        String projectName = "testContentTargetingRuleProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_RULE );

        wizardAction.finish();
    }

    @Test
    public void createContentTargetingTrackingActionModuleProjectInLiferayWorkspace()
    {
        String projectName = "testContentTargetingTrackingActionProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_TRACKING_ACTION );

        wizardAction.finish();
    }

    @Test
    public void createControlMenuEntryModuleProjectInLiferayWorkspace()
    {
        String projectName = "testControlMenuEntryProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTROL_MENU_ENTRY );

        wizardAction.finish();
    }

    @Test
    public void createFormFieldModuleProjectInLiferayWorkspace()
    {
        String projectName = "testFormFieldProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, FORM_FIELD );

        wizardAction.finish();
    }

    @Test
    public void createMvcPortletModuleProject()
    {
        String projectName = "testMvcportletProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, MVC_PORTLET );

        wizardAction.finish();
    }

    @Test
    public void createPanelAppModuleProjectInLiferayWorkspace()
    {
        String projectName = "testPanelAppProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PANEL_APP );

        wizardAction.finish();
    }

    @Test
    public void createPortletConfigurationIconModuleProjectInLiferayWorkspace()
    {
        String projectName = "testPortletConfigurationIconProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_CONFIGURATION_ICON );

        wizardAction.finish();
    }

    @Test
    public void createPortletModuleProjectInLiferayWorkspace() throws IOException
    {
        String projectName = "testPortletProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET );

        wizardAction.finish();
    }

    @Test
    public void createPortletProviderModuleProjectInLiferayWorkspace()
    {
        String projectName = "testPortletProviderProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_PROVIDER );

        wizardAction.finish();
    }

    @Test
    public void createPortletToolbarContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "testPortletToolbarContributorProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_TOOLBAR_CONTRIBUTOR );

        wizardAction.finish();
    }

    @Test
    public void createRestModuleProjectInLiferayWorkspace()
    {
        String projectName = "testRestProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, REST );

        wizardAction.finish();
    }

    @Test
    public void createServiceBuilderModuleProjectInLiferayWorkspace()
    {
        String projectName = "testServiceBuilderProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE_BUILDER );

        wizardAction.finish();
    }

    @Test
    public void createServiceModuleProjectInLiferayWorkspace()
    {
        String projectName = "testServiceProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE );

        wizardAction.finish();
    }

    @Test
    public void createServiceWrapperModuleProjectInLiferayWorkspace()
    {
        String projectName = "testServiceWrapperProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE_WRAPPER );

        wizardAction.finish();
    }

    @Test
    public void createSimulationPanelEntryModuleProjectInLiferayWorkspace()
    {
        String projectName = "testSimulationPanelEntryProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SIMULATION_PANEL_ENTRY );

        wizardAction.finish();
    }

    @Test
    public void createTemplateContextContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "testTemplateContextContributorProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR );

        wizardAction.finish();
    }

    @Test
    public void createThemeContributorModuleProjectInLiferayWorkspace()
    {
        String projectName = "testThemeContributorProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, THEME_CONTRIBUTOR );

        wizardAction.finish();
    }

    @Test
    public void createThemeModuleProjectInLiferayWorkspace()
    {
        String projectName = "testThemeProjectInLS";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, THEME );

        wizardAction.finish();
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );
    }

}
