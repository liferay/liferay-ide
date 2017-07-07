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

package com.liferay.ide.swtbot.gradle.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.gradle.ui.tests.page.CreateWorkspaceProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.AbstractNewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class LiferayWorkspaceProjectWizardTests extends AbstractNewLiferayModuleProjectWizard
    implements LiferayWorkspaceProjectWizard
{

    String projectName = "workspace-project";
    String serverName = "Liferay 7.0 CE Server";

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    CreateWorkspaceProjectWizardPO newLiferayWorkspaceProjectWizard =
        new CreateWorkspaceProjectWizardPO( bot, INDEX_VALIDATION_WORKSPACE_NAME_MESSAGE );

    TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

    @BeforeClass
    public static void switchToLiferayWorkspacePerspective()
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        eclipse.getLiferayWorkspacePerspective().activate();
        eclipse.getProjectExplorerView().show();
    }

    @AfterClass
    public static void cleanAll()
    {

        eclipse.closeShell( LABEL_NEW_LIFERAY_WORPSPACE_PROJECT );
    }

    @Test
    public void newGradleLiferayWorksapceProjectWizard()
    {
        newLiferayWorkspaceProjectWizard.setWorkspaceName( projectName );
        sleep();
        newLiferayWorkspaceProjectWizard.getBuildType().setSelection( TEXT_BUILD_TYPE_GRADLE );

        newLiferayWorkspaceProjectWizard.getDownloadLiferayBundleCheckbox().select();

        assertEquals( "", newLiferayWorkspaceProjectWizard.getServerNameText().getText() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getBundleUrlText().getText() );

        newLiferayWorkspaceProjectWizard.setServerName( serverName );
        newLiferayWorkspaceProjectWizard.finish();
        sleep( 90000 );

        projectTree.setFocus();
        assertTrue( projectTree.getTreeItem( projectName ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "bundles" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "modules" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "themes" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "wars" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "build.gradle" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "gradle.properties" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "settings.gradle" ).isVisible() );

        // add popupmenu action for adding a new server based off of folder
        // location in workspace
        projectTree.expandNode( projectName, "bundles" ).doAction( "Create New Liferay Server from location" );
        sleep();

        TreePO serverTree = new TreePO( bot, 1 );

        serverTree.getTreeItem( "bundles [Stopped]" );

        String moduleProjectName = "testModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, moduleProjectName, MENU_MODULE_MVC_PORTLET,
            eclipseWorkspace + "/" + projectName + "/modules", false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK,
            false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "modules", moduleProjectName ).isVisible() );

        String themeProjectName = "testThemeModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, themeProjectName, MENU_MODULE_THEME, eclipseWorkspace + "/" + projectName + "/wars",
            false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "wars", themeProjectName ).isVisible() );

        String projectName = "testMavenModuleInGradleLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MENU_MODULE_MVC_PORTLET, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.getTreeItem( projectName ).isVisible() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.setWorkspaceName( "test" );
        sleep();
        assertEquals( TEXT_WORKSPACE_ALREADY_EXISTS, newLiferayWorkspaceProjectWizard.getValidationMessage() );

        newLiferayWorkspaceProjectWizard.cancel();
    }

    @Test
    public void newGradleLiferayWorkspaceProjectWithoutDownloadBundle()
    {
        newLiferayWorkspaceProjectWizard.getBuildType().setSelection( TEXT_BUILD_TYPE_GRADLE );

        assertEquals( TEXT_PLEASE_ENTER_THE_WORKSPACE_NAME, newLiferayWorkspaceProjectWizard.getValidationMessage() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getWorkspaceNameText() );

        assertEquals( false, newLiferayWorkspaceProjectWizard.isDownloadLiferayBundleChecked() );

        newLiferayWorkspaceProjectWizard.setWorkspaceName( projectName );
        sleep();
        newLiferayWorkspaceProjectWizard.finish();
        sleep( 30000 );

        projectTree.setFocus();
        assertTrue( projectTree.getTreeItem( projectName ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "modules" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "themes" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "wars" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "build.gradle" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "gradle.properties" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "settings.gradle" ).isVisible() );

        // rename folder in liferay workspace
        projectTree.expandNode( projectName, "gradle.properties" ).doubleClick();
        sleep( 3000 );

        TextEditorPO setGradlePropertiesText = eclipse.getTextEditor( "gradle.properties" );

        setGradlePropertiesText.setText(
            "liferay.workspace.home.dir=bundlesTest" + "\r" + "liferay.workspace.modules.dir=modulesTest" + "\r" +
                "liferay.workspace.wars.dir=warsTest" );
        sleep();

        newLiferayWorkspaceProjectWizard.getSaveToolbar().click();

        sleep();

        // create module project in liferay workspace
        String moduleProjectName = "testModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, moduleProjectName, MENU_MODULE_MVC_PORTLET,
            eclipseWorkspace + "/" + projectName + "/modulesTest", false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "modulesTest", moduleProjectName ).isVisible() );

        String themeProjectName = "testThemeModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, themeProjectName, MENU_MODULE_THEME,
            eclipseWorkspace + "/" + projectName + "/warsTest", false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK,
            false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "warsTest", themeProjectName ).isVisible() );

        // init bundle
        projectTree.getTreeItem( projectName ).doAction( "Liferay", "Initialize Server Bundle" );

        sleep( 90000 );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "bundlesTest" ).isVisible() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.setWorkspaceName( "test" );
        sleep();
        assertEquals( TEXT_WORKSPACE_ALREADY_EXISTS, newLiferayWorkspaceProjectWizard.getValidationMessage() );

        newLiferayWorkspaceProjectWizard.cancel();
    }

    @Test
    public void newMavenLiferayWorkspaceProjectWizard()
    {
        newLiferayWorkspaceProjectWizard.getBuildType().setSelection( TEXT_BUILD_TYPE_MAVEN );

        assertEquals( TEXT_PLEASE_ENTER_THE_WORKSPACE_NAME, newLiferayWorkspaceProjectWizard.getValidationMessage() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getWorkspaceNameText() );
        assertEquals( false, newLiferayWorkspaceProjectWizard.isDownloadLiferayBundleChecked() );

        newLiferayWorkspaceProjectWizard.setWorkspaceName( projectName );
        sleep();
        newLiferayWorkspaceProjectWizard.finish();
        sleep( 30000 );

        projectTree.setFocus();
        assertTrue( projectTree.getTreeItem( projectName ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-modules (in modules)" ).isVisible() );
        assertTrue(
            projectTree.expandNode( projectName, projectName + "-modules (in modules)", "pom.xml" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-themes (in themes)" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-themes (in themes)", "pom.xml" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-wars (in wars)" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, projectName + "-wars (in wars)", "pom.xml" ).isVisible() );
        assertTrue( projectTree.expandNode( projectName, "pom.xml" ).isVisible() );

        String themeProjectName = "testMavenThemeModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, themeProjectName, MENU_MODULE_THEME, eclipseWorkspace + "/" + projectName + "/wars",
            false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue(
            projectTree.expandNode( projectName, projectName + "-wars (in wars)", themeProjectName ).isVisible() );

        String moduleProjectName = "testMavenModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, moduleProjectName, MENU_MODULE_MVC_PORTLET,
            eclipseWorkspace + "/" + projectName + "/modules", false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK,
            false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue(
            projectTree.expandNode(
                projectName, projectName + "-modules (in modules)", "testMavenModuleInLWS" ).isVisible() );

        String projectName = "testGradleModuleInMavenLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, projectName, MENU_MODULE_MVC_PORTLET, eclipseWorkspace, false, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.getTreeItem( projectName ).isVisible() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.setWorkspaceName( "test" );
        sleep();
        assertEquals( TEXT_WORKSPACE_ALREADY_EXISTS, newLiferayWorkspaceProjectWizard.getValidationMessage() );

        newLiferayWorkspaceProjectWizard.cancel();
    }

    @Test
    public void validationWorkspaceName()
    {
        newLiferayWorkspaceProjectWizard.setWorkspaceName( ".." );
        sleep();
        assertEquals(
            " '..'" + TEXT_INVALID_NAME_ON_PLATFORM, newLiferayWorkspaceProjectWizard.getValidationMessage() );

        newLiferayWorkspaceProjectWizard.setWorkspaceName( "##" );
        sleep();
        assertEquals( TEXT_INVALID_NAME_PROJECT, newLiferayWorkspaceProjectWizard.getValidationMessage() );

        newLiferayWorkspaceProjectWizard.setWorkspaceName( "*" );
        sleep();
        assertEquals(
            " *" + TEXT_INVALID_CHARACTER_IN_RESOURCE_NAME + "'*'.",
            newLiferayWorkspaceProjectWizard.getValidationMessage() );

        newLiferayWorkspaceProjectWizard.setWorkspaceName( TEXT_BLANK );
        sleep();
        assertEquals( TEXT_WORKSPACE_NAME_COULD_NOT_EMPTY, newLiferayWorkspaceProjectWizard.getValidationMessage() );

        newLiferayWorkspaceProjectWizard.setWorkspaceName( projectName );
        sleep();
        assertEquals( TEXT_CREATE_LIFERAY_WORKSPACE, newLiferayWorkspaceProjectWizard.getValidationMessage() );

        newLiferayWorkspaceProjectWizard.cancel();
    }

    @Test
    public void initialStateTest()
    {
        assertEquals( TEXT_PLEASE_ENTER_THE_WORKSPACE_NAME, newLiferayWorkspaceProjectWizard.getValidationMessage() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getWorkspaceNameText() );

        checkBuildTypes();

        assertTrue( newLiferayWorkspaceProjectWizard.getUseDefaultLocation().isChecked() );
        assertEquals( false, newLiferayWorkspaceProjectWizard.isDownloadLiferayBundleChecked() );

        newLiferayWorkspaceProjectWizard.getUseDefaultLocation().deselect();
        assertEquals( eclipseWorkspace, newLiferayWorkspaceProjectWizard.getLocation().getText() );
        newLiferayWorkspaceProjectWizard.getUseDefaultLocation().select();

        newLiferayWorkspaceProjectWizard.getDownloadLiferayBundleCheckbox().select();
        assertEquals( "", newLiferayWorkspaceProjectWizard.getServerNameText().getText() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getBundleUrlText().getText() );
        newLiferayWorkspaceProjectWizard.getDownloadLiferayBundleCheckbox().deselect();

        newLiferayWorkspaceProjectWizard.cancel();
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();
        sleep();
    }

    @After
    public void deleteLiferayWorkspace() throws IOException
    {
        killGradleProcess();

        if( eclipse.getPackageExporerView().hasProjects() )
        {
            DeleteResourcesDialog deleteResources = new DeleteResourcesDialog( bot );

            DeleteResourcesContinueDialog continueDeleteResources =
                new DeleteResourcesContinueDialog( bot, "Delete Resources" );

            projectTree.getTreeItem( projectName ).doAction( BUTTON_DELETE );
            sleep( 2000 );

            deleteResources.confirmDeleteFromDisk();
            deleteResources.confirm();

            try
            {
                sleep();
                continueDeleteResources.clickContinueButton();
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

            sleep( 5000 );

            eclipse.getPackageExporerView().deleteProjectExcludeNames(
                new String[] { getLiferayPluginsSdkName() }, true );
        }
    }

}
