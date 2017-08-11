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

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;

import java.io.IOException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ying Xu
 * @author Sunny Shi
 */
public class NewLiferayModuleProjectWizardTests extends SwtbotBase
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
    public void createMvcportletModuleProject()
    {
        final String projectName = "testMvcportletProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, MVC_PORTLET );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createMvcPortletModuleProjectInLiferayWorkspace() throws IOException
    {
        wizardAction.openNewLiferayWorkspaceWizard();

        final String liferayWorkspaceName = "liferayWorkspace";

        wizardAction.prepareLiferayWorkspaceGradle( liferayWorkspaceName );

        wizardAction.finish();

        wizardAction.openNewLiferayModuleWizard();

        final String projectName = "testMvcportletInLS";

        wizardAction.prepareLiferayModuleGradle( projectName, MVC_PORTLET );

        wizardAction.finish();

        assertTrue( viewAction.fetchProjectFile( liferayWorkspaceName, "modules", projectName ).isVisible() );

        viewAction.deleteProject( liferayWorkspaceName );
    }

    @Test
    public void createServiceModuleProject()
    {
        final String projectName = "testServiceProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE );

        wizardAction.next();

        wizardAction.openSelectServiceDialog();

        dialogAction.prepareText( "*lifecycleAction" );

        dialogAction.confirm();

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createServiceBuilderModuleProject()
    {
        final String projectName = "testServiceBuilderProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE_BUILDER );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createActivatorModuleProject()
    {
        final String projectName = "testActivatorProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, ACTIVATOR );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createApiModuleProject()
    {
        final String projectName = "testApiProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, API );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createContentTargetingReportModuleProject()
    {
        final String projectName = "testContentTargetingReportProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_REPORT );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createContentTargetingRuleModuleProject()
    {
        final String projectName = "testContentTargetingRuleProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_RULE );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createContentTargetingTrackingActionModuleProject()
    {
        final String projectName = "testContentTargetingTrackingActionProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTENT_TARGETING_TRACKING_ACTION );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createControlMenuEntryModuleProject()
    {
        final String projectName = "testControlMenuEntryProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, CONTROL_MENU_ENTRY );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createFormFieldModuleProject()
    {
        final String projectName = "testFormFieldProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, FORM_FIELD );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPanelAppModuleProject()
    {
        final String projectName = "testPanelAppProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PANEL_APP );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPortletModuleProject()
    {
        final String projectName = "testPortletProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPortletConfigurationIconModuleProject()
    {
        final String projectName = "testPortletConfigurationIconProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_CONFIGURATION_ICON );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPortletProviderModuleProject()
    {
        final String projectName = "testPortletProviderProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_PROVIDER );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createPortletToolbarContributorModuleProject()
    {
        final String projectName = "testPortletToolbarContributorProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, PORTLET_TOOLBAR_CONTRIBUTOR );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createRestModuleProject()
    {
        final String projectName = "testRestProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, REST );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createServiceWrapperModuleProject()
    {
        final String projectName = "testServiceWrapperProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SERVICE_WRAPPER );

        wizardAction.next();

        wizardAction.openSelectServiceDialog();

        dialogAction.prepareText( "*BookmarksEntryLocalServiceWrapper" );

        dialogAction.confirm();

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createSimulationPanelEntryModuleProject()
    {
        final String projectName = "testSimulationPanelEntryProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, SIMULATION_PANEL_ENTRY );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createTemplateContextContributorModuleProject()
    {
        final String projectName = "testTemplateContextContributorProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createThemeModuleProject()
    {
        final String projectName = "test-theme";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, THEME );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Test
    public void createThemeContributor()
    {
        final String projectName = "test-theme-contributor";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleGradle( projectName, THEME_CONTRIBUTOR );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );

        viewAction.deleteProject( projectName );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

    }

}
