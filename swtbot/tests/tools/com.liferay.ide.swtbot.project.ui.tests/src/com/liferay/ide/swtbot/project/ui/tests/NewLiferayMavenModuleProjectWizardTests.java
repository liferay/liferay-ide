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
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayModuleProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayModuleProjectWizardSecondPagePO;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Sunny Shi
 */
public class NewLiferayMavenModuleProjectWizardTests extends AbstractNewLiferayModuleProjectWizard
    implements NewLiferayModuleProjectWizard
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

    NewLiferayModuleProjectWizardPO createMavenModuleProjectWizard =
        new NewLiferayModuleProjectWizardPO( bot, INDEX_NEW_LIFERAY_MODULE_PROJECT_VALIDATION_MESSAGE );

    NewLiferayModuleProjectWizardSecondPagePO createMavenModuleProjectSecondPageWizard =
        new NewLiferayModuleProjectWizardSecondPagePO( bot );

    @After
    public void clean()
    {
        eclipse.closeShell( LABEL_NEW_LIFERAY_MODULE_PROJECT );

        if( addedProjects() )
        {
            eclipse.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() },
                true );
        }
    }

    @BeforeClass
    public static void switchToLiferayWorkspacePerspective()
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        eclipse.getLiferayWorkspacePerspective().activate();
        eclipse.getProjectExplorerView().show();
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

    }

    @Test
    public void createMvcportletModuleProject()
    {
        String projectName = "testMvcportletProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_MVC_PORTLET, eclipseWorkspace, true,
            eclipseWorkspace + "newFolder", TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, true );

        createMavenModuleProjectSecondPageWizard.waitForPageToClose();

        String pomXmlFileName = "pom.xml";

        String pomContent = "<artifactId>testMvcportletProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

        assertTrue( projectTree.expandNode(
            projectName, "src/main/java", "testMvcportletProject.portlet",
            "TestMvcportletProjectPortlet.java" ).isVisible() );
    }

    @Test
    public void createServiceModuleProject()
    {
        String projectName = "testServiceProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_SERVICE, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, "*lifecycleAction", true );

        String javaFileName = "TestServiceProject.java";
        String javaContent = "service = LifecycleAction.class";
        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testServiceProject</artifactId>";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testServiceProject", javaFileName );

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createServiceBuilderModuleProject()
    {
        String projectName = "testServiceBuilderProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_SERVICE_BUILDER, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        assertTrue( projectTree.expandNode( projectName ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-api" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-service" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-service", "service.xml" ).isVisible() );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testServiceBuilderProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

        projectTree.getTreeItem( projectName + "-service" ).doAction( "Liferay", "liferay:build-service" );
        sleep( 10000 );

        assertTrue( projectTree.expandNode(
            projectName + "-api", "src/main/java", "testServiceBuilderProject.service" ).isVisible() );

        assertTrue(
            projectTree.expandNode(
                projectName + "-service", "src/main/java", "testServiceBuilderProject.model.impl" ).isVisible() );

    }

    @Test
    public void createActivatorModuleProject()
    {
        String projectName = "testActivatorProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_ACTIVATOR, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestActivatorProjectActivator.java";
        String javaContent = "implements BundleActivator";

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testActivatorProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
        openEditorAndCheck( javaContent, projectName, projectName, "src/main/java", projectName, javaFileName );
    }

    @Test
    public void createApiModuleProject()
    {
        String projectName = "testApiProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_API, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, false );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testApiProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createContentTargetingReportModuleProject()
    {
        String projectName = "testContentTargetingReportProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_CONTENT_TARGETING_REPORT, eclipseWorkspace, false,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestContentTargetingReportProjectReport.java";
        String javaContent = "extends BaseJSPReport";
        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testContentTargetingReportProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
        openEditorAndCheck( javaContent, projectName, projectName, "src/main/java",
            "testContentTargetingReportProject.content.targeting.report", javaFileName );

    }

    @Test
    public void createContentTargetingRuleModuleProject()
    {
        String projectName = "testContentTargetingRuleProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_CONTENT_TARGETING_RULE, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestContentTargetingRuleProjectRule.java";
        String javaContent = "osgi.web.symbolicname=testContentTargetingRuleProject";

        String pomXmlFileName = "pom.xml";
        String pomContent = "<groupId>com.liferay.content-targeting</groupId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
        openEditorAndCheck( javaContent, projectName, projectName, "src/main/java",
            "testContentTargetingRuleProject.content.targeting.rule", javaFileName );
    }

    @Test
    public void createContentTargetingTrackingActionModuleProject()
    {
        String projectName = "testContentTargetingTrackingActionProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_CONTENT_TARGETING_RULE, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<groupId>testContentTargetingTrackingActionProject</groupId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createControlMenuEntryModuleProject()
    {
        String projectName = "testControlMenuEntryProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_CONTROL_MENU_ENTRY, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestControlMenuEntryProjectProductNavigationControlMenuEntry.java";
        String javaContent = "extends BaseProductNavigationControlMenuEntry";

        String pomXmlFileName = "pom.xml";
        String pomContent = "com.liferay.product.navigation.control.menu.api";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
        openEditorAndCheck( javaContent, projectName, projectName, "src/main/java",
            "testControlMenuEntryProject.control.menu", javaFileName );
    }

    @Test
    public void createFormFieldModuleProject()
    {
        String projectName = "testFormFieldProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_FORM_FIELD, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName1 = "TestFormFieldProjectDDMFormFieldRenderer.java";
        String javaContent1 = "extends BaseDDMFormFieldRenderer";
        String javaFileName2 = "TestFormFieldProjectDDMFormFieldType.java";
        String javaContent2 = "service = DDMFormFieldType.class";

        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testFormFieldProject.form.field", javaFileName1 ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/java", "testFormFieldProject.form.field", javaFileName2 ).isVisible() );

        openEditorAndCheck(
            javaContent1, projectName, projectName, "src/main/java", "testFormFieldProject.form.field", javaFileName1 );
        openEditorAndCheck( javaContent2, projectName, projectName, "src/main/java", "testFormFieldProject.form.field",
            javaFileName2 );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testFormFieldProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createPanelAppModuleProject()
    {
        String projectName = "testPanelAppProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_PANEL_APP, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, false );

        assertTrue( projectTree.getTreeItem( projectName ).isVisible() );
        assertTrue(
            projectTree.expandNode( projectName, "src/main/java", projectName + ".application.list" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "src/main/java", projectName + ".constants" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "src/main/java", projectName + ".portlet" ).isVisible() );

        String javaFileName = "TestPanelAppProjectPortlet.java";
        String javaContent = "TestPanelAppProjectPortletKeys.TestPanelAppProject";
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testPanelAppProject.portlet", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPanelAppProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createPortletModuleProject()
    {
        String projectName = "testPortletProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_PORTLET, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestPortletProjectPortlet.java";
        String javaContent = "service = Portlet.class";
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testPortletProject.portlet", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPortletProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createPortletConfigurationIconModuleProject()
    {
        String projectName = "testPortletConfigurationIconProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_PORTLET_CONFIGURATION_ICON, eclipseWorkspace, false,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestPortletConfigurationIconProjectPortletConfigurationIcon.java";
        String javaContent = "extends BasePortletConfigurationIcon";
        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java",
            "testPortletConfigurationIconProject.portlet.configuration.icon", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPortletConfigurationIconProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createPortletProviderModuleProject()
    {
        String projectName = "testPortletProviderProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_PORTLET_PROVIDER, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName1 = "TestPortletProviderProjectAddPortletProvider.java";
        String javaContent1 = "service = AddPortletProvider.class";
        String javaFileName2 = "TestPortletProviderProjectPortlet.java";
        String javaContent2 = "TestPortletProviderProjectPortletKeys.TestPortletProviderProject";

        openEditorAndCheck(
            javaContent1, projectName, projectName, "src/main/java", "testPortletProviderProject.portlet",
            javaFileName1 );
        openEditorAndCheck( javaContent2, projectName, projectName, "src/main/java",
            "testPortletProviderProject.portlet", javaFileName2 );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPortletProviderProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createPortletToolBarContributorModuleProject()
    {
        String projectName = "testPortletToolBarContributorProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_PORTLET_TOOLBAR_CONTRIBUTOR, eclipseWorkspace, false,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestPortletToolBarContributorProjectPortletToolbarContributor.java";
        String javaContent = "service = PortletToolbarContributor.class";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java",
            "testPortletToolBarContributorProject.portlet.toolbar.contributor", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testPortletToolBarContributorProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createRestModuleProject()

    {
        String projectName = "testRestProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_REST, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestRestProjectApplication.java";
        String javaContent = "TestRestProjectApplication extends Application";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testRestProject.application", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testRestProject</artifactId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createServiceWrapperModuleProject()

    {
        String projectName = "testServiceWrapperProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_SERVICE_WRAPPER, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, "*bookmarksEntryServiceWrapper", true );

        String javaFileName = "TestServiceWrapperProject.java";
        String javaContent = "extends BookmarksEntryServiceWrapper";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testServiceWrapperProject", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<groupId>testServiceWrapperProject</groupId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createSimulationPanelEntryModuleProject()

    {
        String projectName = "testSimulationPanelEntryProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_SIMULATION_PANEL_ENTRY, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestSimulationPanelEntryProjectSimulationPanelApp.java";
        String javaContent = "SimulationPanelCategory.SIMULATION";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java", "testSimulationPanelEntryProject.application.list",
            javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testSimulationPanelEntryProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

    }

    @Test
    public void createTemplateContextContributorModuleProject()

    {
        String projectName = "testTemplateContextContributorProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_TEMPLATE_CONTEXT_CONTRIBUTOR, eclipseWorkspace, false,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        String javaFileName = "TestTemplateContextContributorProjectTemplateContextContributor.java";
        String javaContent = "implements TemplateContextContributor";

        openEditorAndCheck(
            javaContent, projectName, projectName, "src/main/java",
            "testTemplateContextContributorProject.theme.contributor", javaFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testTemplateContextContributorProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }

    @Test
    public void createThemeModuleProject()
    {
        String projectName = "testThemeProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_THEME, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, false );

        String scssFileName = "_custom.scss";
        assertTrue( projectTree.expandNode( projectName, "src", "main", "webapp", "css", scssFileName ).isVisible() );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<artifactId>testThemeProject</artifactId>";

        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );

        DeleteResourcesDialog deleteResources = new DeleteResourcesDialog( bot );
        DeleteResourcesContinueDialog continueDeleteResources =
            new DeleteResourcesContinueDialog( bot, "Delete Resources" );

        projectTree.getTreeItem( projectName ).doAction( BUTTON_DELETE );
        sleep( 2000 );

        deleteResources.confirmDeleteFromDisk();
        deleteResources.confirm();
        continueDeleteResources.clickContinueButton();

    }

    @Test
    public void createThemeContributorModuleProject()

    {
        String projectName = "testThemeContributorProject";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_THEME_CONTRIBUTOR, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        assertTrue( projectTree.expandNode(
            projectName, "src/main/resources", "META-INF", "resources", "css", projectName + "_rtl.css" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "css", projectName + ".css" ).isVisible() );
        assertTrue(
            projectTree.expandNode(
                projectName, "src/main/resources", "META-INF", "resources", "css",
                projectName + ".scss" ).isVisible() );

        projectTree.setFocus();
        String scssFileName = "_body.scss";
        String scssFileContent = "background-color";

        openEditorAndCheck(
            scssFileContent, projectName, projectName, "src/main/resources", "META-INF", "resources", "css",
            projectName, scssFileName );

        String pomXmlFileName = "pom.xml";
        String pomContent = "<groupId>testThemeContributorProject</groupId>";
        openEditorAndCheck( pomContent, projectName, projectName, pomXmlFileName );
    }
}
