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

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sunny Shi
 */
public class NewLiferayMavenModuleProjectWizardTests extends SwtbotBase
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );
    }

    @Test
    public void createMvcPortlet()
    {
        final String projectName = "test-mvn-portlet";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, MVC_PORTLET );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createService()
    {
        final String projectName = "test-service";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, SERVICE );

        wizardAction.openSelectServiceDialog();

        dialogAction.prepareText( "*lifecycleAction" );

        dialogAction.confirm();

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createServiceBuilder()
    {
        final String projectName = "test-service-builder";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, SERVICE_BUILDER );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createActivator()
    {
        final String projectName = "test-activator";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, ACTIVATOR );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createApi()
    {
        final String projectName = "test-api";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, API );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createContentTargetingReport()
    {
        final String projectName = "test-content-targeting-report";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, CONTENT_TARGETING_REPORT );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createContentTargetingRule()
    {
        final String projectName = "test-content-targeting-rule";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, CONTENT_TARGETING_RULE );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createContentTargetingTrackingAction()
    {
        final String projectName = "test-content-targeting-tracking-action";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, CONTENT_TARGETING_TRACKING_ACTION );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createControlMenuEntry()
    {
        final String projectName = "test-control-menu-entry";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, CONTROL_MENU_ENTRY );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createFormField()
    {
        final String projectName = "test-form-field";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, FORM_FIELD );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createPanelApp()
    {
        final String projectName = "test-panel-app";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, PANEL_APP );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createPortlet()
    {
        final String projectName = "test-portlet";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, PORTLET );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createPortletConfigurationIcon()
    {
        final String projectName = "test-portlet-configuration-icon";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, PORTLET_CONFIGURATION_ICON );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createPortletProvider()
    {
        final String projectName = "test-portlet-provider";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, PORTLET_PROVIDER );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createPortletToolbarContributor()
    {
        final String projectName = "test-portlet-toolbar-contributor";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, PORTLET_TOOLBAR_CONTRIBUTOR );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createRest()
    {
        final String projectName = "test-rest";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, REST );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createServiceWrapper()
    {
        final String projectName = "testServiceWrapperProject";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, SERVICE_WRAPPER );

        wizardAction.openSelectServiceDialog();

        dialogAction.prepareText( "*bookmarksEntryServiceWrapper" );

        dialogAction.confirm();

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createSimulationPanelEntry()
    {
        final String projectName = "test-simulation-panel-entry";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, SIMULATION_PANEL_ENTRY );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createTemplateContextContributor()
    {
        final String projectName = "test-template-context-contributor";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, TEMPLATE_CONTEXT_CONCONTRIBUTOR );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createTheme()
    {
        final String projectName = "test-theme";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, THEME );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

    @Test
    public void createThemeContributor()
    {
        final String projectName = "test-theme-contributor";

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModuleMaven( projectName, THEME_CONTRIBUTOR );

        wizardAction.finish();

        assertTrue( viewAction.getProject( projectName ).isVisible() );
    }

}
