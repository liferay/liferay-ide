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

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.swtbot.project.ui.tests.page.ImportLiferayModuleProjectPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SelectTypePO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Ashley Yuan
 */
public class ImportLiferayModuleProjectTests extends SWTBotBase implements ImportLiferayModuleProject
{

    TextEditorPO buildGradleText = new TextEditorPO( bot, "build.gradle" );
    private String existingDirectory = getLiferayServerDir().toOSString();
    ImportLiferayModuleProjectPO importLiferayModulePage = new ImportLiferayModuleProjectPO(
        bot, TITLE_IMPORT_LIFERAY_MODULE_PROJECT, INDEX_IMPORT_LIFERAY_WORKSPACE_LOCATION_VALIDATION_MESSAGE );
    private String inexistentLocation = "c:/123";
    private String invalidLocation = "1.*";

    private String moduleRootPath = System.getProperty( "user.dir" );

    TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();

    SelectTypePO selectImportPage =
        new SelectTypePO( bot, INDEX_SELECT_IMPORT_LIFERAY_MODULE_PROJECTS_VALIDATION_MESSAGE );

    TextEditorPO settingsGradleText = new TextEditorPO( bot, "settings.gradle" );

    private String wrongPath = "123";

    @After
    public void clean()
    {
        eclipse.closeShell( TITLE_IMPORT_LIFERAY_MODULE_PROJECT );

        eclipse.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, false );
    }

    @Test
    public void importASingleModule()
    {
        // import module that out of liferay workpsace
        importLiferayModulePage.get_locationText().setText( moduleRootPath + "/projects/MvcportletModule" );

        sleep();
        assertContains( TEXT_SELECT_LOCATION_OF_MODULE_PROJECT_DIRECTORY,
            importLiferayModulePage.getValidationMessage() );

        assertEquals( "gradle", importLiferayModulePage.get_buildTypeText().getText() );
        assertTrue( importLiferayModulePage.finishButton().isEnabled() );
        assertFalse( importLiferayModulePage.nextButton().isEnabled() );

        importLiferayModulePage.finish();
        importLiferayModulePage.waitForPageToClose();

        projectTree.expandNode( "MvcportletModule" );
        projectTree.getTreeItem( "MvcportletModule" ).getTreeItem( "build.gradle" ).doubleClick();

        assertContains( "com.liferay.gradle.plugins", buildGradleText.getText() );

        projectTree.getTreeItem( "MvcportletModule" ).collapse();

        // import an existing module project
        importLiferayModuleProjects();

        importLiferayModulePage.get_locationText().setText( moduleRootPath + "/projects/MvcportletModule" );

        sleep();
        assertContains( TEXT_PROJECT_ALREADY_EXISTS, importLiferayModulePage.getValidationMessage() );

        // import servicebuilder module which have sub-modules
        importLiferayModulePage.get_locationText().setText( moduleRootPath + "/projects/ServicebuilderModule" );

        sleep();
        assertContains( TEXT_SELECT_LOCATION_OF_MODULE_PROJECT_DIRECTORY,
            importLiferayModulePage.getValidationMessage() );
        assertEquals( "gradle", importLiferayModulePage.get_buildTypeText().getText() );

        assertTrue( importLiferayModulePage.finishButton().isEnabled() );
        assertFalse( importLiferayModulePage.nextButton().isEnabled() );

        importLiferayModulePage.finish();
        importLiferayModulePage.waitForPageToClose();
        sleep( 5000 );

        projectTree.expandNode( "ServicebuilderModule" );
        sleep();
        projectTree.getTreeItem( "ServicebuilderModule" ).getTreeItem( "settings.gradle" ).doubleClick();

        assertContains(
            "include " + "\"" + "ServicebuilderModule-api" + "\", " + "\"" + "ServicebuilderModule-service" + "\"",
            settingsGradleText.getText() );
        assertTrue( projectTree.getTreeItem( "ServicebuilderModule-api" ).isVisible() );
        assertTrue( projectTree.getTreeItem( "ServicebuilderModule-service" ).isVisible() );

        projectTree.expandNode( "ServicebuilderModule-api" );
        projectTree.getTreeItem( "ServicebuilderModule-api" ).getTreeItem( "build.gradle" ).doubleClick();

        assertContains(
            "buildscript {\n\tdependencies {\n\t\tclasspath group: \"com.liferay\", name: \"com.liferay.gradle.plugins\", version: \"1.0.369\"\n\t}\n\n\trepositories {\n\t\tmavenLocal()\n\n\t\tmaven {\n\t\t\turl \"https://cdn.lfrs.sl/repository.liferay.com/nexus/content/groups/public\"\n\t\t}\n\t}\n}\n\napply plugin: \"com.liferay.plugin\"\n\ndependencies {\n\tcompile group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompile group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompile group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompile group: \"javax.servlet\", name: \"servlet-api\", version: \"2.5\"\n\tcompile group: \"jstl\", name: \"jstl\", version: \"1.2\"\n\tcompile group: \"org.osgi\", name: \"org.osgi.compendium\", version: \"5.0.0\"\n}\n\nrepositories {\n\tmavenLocal()\n\n\tmaven {\n\t\turl \"https://cdn.lfrs.sl/repository.liferay.com/nexus/content/groups/public\"\n\t}\n}",
            buildGradleText.getText() );

        projectTree.expandNode( "ServicebuilderModule-service" );
        projectTree.getTreeItem( "ServicebuilderModule-service" ).getTreeItem( "build.gradle" ).doubleClick();
        sleep( 200 );

        TreeItemPO serviceXml = new TreeItemPO( bot, projectTree, "ServicebuilderModule-service", "service.xml" );

        assertTrue( serviceXml.isVisible() );
        assertContains(
            "buildscript {\n\tdependencies {\n\t\tclasspath group: \"com.liferay\", name: \"com.liferay.gradle.plugins\", version: \"1.0.369\"\n\t}\n\n\trepositories {\n\t\tmavenLocal()\n\n\t\tmaven {\n\t\t\turl \"https://cdn.lfrs.sl/repository.liferay.com/nexus/content/groups/public\"\n\t\t}\n\t}\n}\n\napply plugin: \"com.liferay.plugin\"\n\ndependencies {\n\tcompile group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"\n\tcompile group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\", version: \"2.0.0\"\n\tcompile group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"\n\tcompile group: \"javax.servlet\", name: \"servlet-api\", version: \"2.5\"\n\tcompile group: \"jstl\", name: \"jstl\", version: \"1.2\"\n\tcompile group: \"org.osgi\", name: \"org.osgi.compendium\", version: \"5.0.0\"\n}\n\nrepositories {\n\tmavenLocal()\n\n\tmaven {\n\t\turl \"https://cdn.lfrs.sl/repository.liferay.com/nexus/content/groups/public\"\n\t}\n}",
            buildGradleText.getText() );

        projectTree.getTreeItem( "ServicebuilderModule" ).collapse();
        projectTree.getTreeItem( "ServicebuilderModule-api" ).collapse();
        projectTree.getTreeItem( "ServicebuilderModule-service" ).collapse();

        // import a module which in liferay workspace
        importLiferayModuleProjects();

        importLiferayModulePage.get_locationText().setText(
            moduleRootPath + "/projects/testWorkspace/modules/PortletModule" );

        sleep();
        assertEquals( TEXT_BLANK, importLiferayModulePage.get_buildTypeText().getText() );
        assertContains(
            TEXT_NOT_ROOT_LOCATION_OF_MULTI_MODULE_PROJECT, importLiferayModulePage.getValidationMessage() );
        assertFalse( importLiferayModulePage.finishButton().isEnabled() );

        importLiferayModulePage.cancel();

        eclipse.getPackageExporerView().deleteResouceByName( "ServicebuilderModule", false );
    }

    public void importLiferayModuleProjects()
    {
        eclipse.getFileMenu().clickMenu( LABEL_IMPORT );

        selectImportPage.selectItem( "liferay", "Liferay", LABEL_IMPORT_MODULE_PROJECTS );
        assertEquals(
            TEXT_SELECT_IMPORT_LIFERAY_MODULE_PROJECTS_VALIDATION_MESSAGE, selectImportPage.getValidationMessage() );

        selectImportPage.next();
    }

    @Before
    public void importModuleProject()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        importLiferayModuleProjects();
    }

    @Test
    public void importMultipleModules()
    {
        importLiferayModulePage.get_locationText().setText( moduleRootPath + "/projects" );

        sleep();
        assertFalse( importLiferayModulePage.finishButton().isEnabled() );
        assertContains(
            TEXT_LOCATION_NOT_RECOGNIZED_AS_A_VALID_PROJECT_TYPE, importLiferayModulePage.getValidationMessage() );

        importLiferayModulePage.get_locationText().setText( moduleRootPath + "/projects/testWorkspace" );

        sleep();
        assertContains( TEXT_SELECT_LOCATION_OF_MODULE_PROJECT_DIRECTORY,
            importLiferayModulePage.getValidationMessage() );

        importLiferayModulePage.finish();
        importLiferayModulePage.waitForPageToClose();

        assertTrue( projectTree.getTreeItem( "testWorkspace" ).isVisible() );

        eclipse.getLiferayWorkspacePerspective().activate();

        projectTree.expandNode( "testWorkspace", "modules" );

        assertTrue( projectTree.getTreeItem( "testWorkspace" ).getTreeItem( "modules", "PortletModule" ).isVisible() );
        assertTrue(
            projectTree.getTreeItem( "testWorkspace" ).getTreeItem( "modules", "ServicewrapperModule" ).isVisible() );

    }

    @Test
    public void liferayModuleLocationTest()
    {
        // initial state check
        assertContains(
            TEXT_SELECT_LOCATION_OF_MODULE_PROJECT_DIRECTORY, importLiferayModulePage.getValidationMessage() );
        assertEquals( TEXT_BLANK, importLiferayModulePage.get_locationText().getText() );
        assertEquals( TEXT_BLANK, importLiferayModulePage.get_buildTypeText().getText() );

        assertTrue( importLiferayModulePage.get_locationText().isEnabled() );
        assertFalse( importLiferayModulePage.get_buildTypeText().isActive() );
        assertTrue( importLiferayModulePage.get_browseButton().isEnabled() );

        // location validation
        importLiferayModulePage.get_locationText().setText( invalidLocation );

        sleep();
        assertContains( "\"" + invalidLocation + "\"" + TEXT_IS_NOT_A_VALID_PATH,
            importLiferayModulePage.getValidationMessage() );
        assertEquals( TEXT_BLANK, importLiferayModulePage.get_buildTypeText().getText() );

        importLiferayModulePage.get_locationText().setText( wrongPath );

        sleep();
        assertContains( "\"" + wrongPath + "\"" + TEXT_IS_NOT_AN_ABSOLUTE_PATH,
            importLiferayModulePage.getValidationMessage() );
        assertEquals( TEXT_BLANK, importLiferayModulePage.get_buildTypeText().getText() );

        importLiferayModulePage.get_locationText().setText( inexistentLocation );

        sleep();
        assertContains( TEXT_DIRECTORY_DOESNT_EXIST, importLiferayModulePage.getValidationMessage() );
        assertEquals( TEXT_BLANK, importLiferayModulePage.get_buildTypeText().getText() );

        importLiferayModulePage.get_locationText().setText( existingDirectory );

        sleep();
        assertContains( TEXT_LOCATION_NOT_RECOGNIZED_AS_A_VALID_PROJECT_TYPE,
            importLiferayModulePage.getValidationMessage() );
        assertEquals( TEXT_BLANK, importLiferayModulePage.get_buildTypeText().getText() );

        importLiferayModulePage.get_locationText().setText( "projects/MvcportletModule" );

        sleep();
        assertContains( TEXT_IS_NOT_AN_ABSOLUTE_PATH, importLiferayModulePage.getValidationMessage() );
        assertEquals( "gradle", importLiferayModulePage.get_buildTypeText().getText() );

        assertFalse( importLiferayModulePage.finishButton().isEnabled() );
        assertTrue( importLiferayModulePage.backButton().isEnabled() );
        assertFalse( importLiferayModulePage.nextButton().isEnabled() );

        importLiferayModulePage.cancel();
    }
}
