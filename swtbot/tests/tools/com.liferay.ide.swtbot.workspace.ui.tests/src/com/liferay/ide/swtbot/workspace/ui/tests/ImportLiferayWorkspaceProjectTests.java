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

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ImportLiferayWorkspaceProjectWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.page.CTabItem;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Tree;

/**
 * @author Sunny Shi
 */
public class ImportLiferayWorkspaceProjectTests extends SwtbotBase
{

    private String liferayWorkspaceRootPath = System.getProperty( "user.dir" );

    Tree projectTree = viewAction.getProjects();

    ImportLiferayWorkspaceProjectWizard importLiferayWorkspaceProject = new ImportLiferayWorkspaceProjectWizard( bot );

    DeleteResourcesContinueDialog continueDeleteResources = new DeleteResourcesContinueDialog( bot );

    @Test
    public void importGradleLiferayWorkspaceProject()
    {
        wizardAction.openImportLiferayWorkspaceWizard();

        wizardAction.prepareImportLiferayWorkspace(liferayWorkspaceRootPath + "/projects/testGradleWorkspace");

        final String workspaceName = "testGradleWorkspace";

        wizardAction.finishToWait();

        viewAction.fetchProjectFile( workspaceName, "bundles" ).doAction( DELETE );

        dialogAction.confirm();

        wizardAction.openImportLiferayWorkspaceWizard();

        wizardAction.prepareImportLiferayWorkspace(liferayWorkspaceRootPath + "/projects/testGradleWorkspace");

        importLiferayWorkspaceProject.getDownloadLiferaybundle().select();

        importLiferayWorkspaceProject.getServerName().setText( "test-lrws" );
        importLiferayWorkspaceProject.finish();

        viewAction.fetchProjectFile( workspaceName, "bundles" ).doAction( DELETE );

        dialogAction.confirm();

        projectTree.getTreeItem( workspaceName ).doAction( "Liferay", "Initialize Server Bundle" );

        sleep( 10000 );
        projectTree.getTreeItem( workspaceName ).expand();

        sleep( 2000 );
        assertTrue( projectTree.getTreeItem( workspaceName ).getTreeItem( "bundles" ).isVisible() );
        assertTrue( projectTree.getTreeItem( workspaceName ).getTreeItem( "configs" ).isVisible() );
        assertTrue( projectTree.getTreeItem( workspaceName ).getTreeItem( "gradle" ).isVisible() );

        String gradlePropertyFileName = "gradle.properties";
        String settingGradleFileName = "settings.gradle";

        projectTree.expandNode( workspaceName, gradlePropertyFileName ).doubleClick();
        Editor gradlePropertiesFile = ide.getEditor( gradlePropertyFileName );

        assertContains( "liferay.workspace.modules.dir", gradlePropertiesFile.getText() );
        assertContains( "liferay.workspace.home.dir", gradlePropertiesFile.getText() );
        gradlePropertiesFile.close();
        sleep();

        projectTree.expandNode( workspaceName, settingGradleFileName ).doubleClick();
        Editor settingGradleFile = ide.getEditor( settingGradleFileName );

        assertContains( "buildscript", settingGradleFile.getText() );
        assertContains( "repositories", settingGradleFile.getText() );

        settingGradleFile.close();
    }

    @Test
    public void importMavenLiferayWorkspaceProject()
    {
        wizardAction.openImportLiferayWorkspaceWizard();

        String liferayWorkspaceName = "testMavenWorkspace";

        wizardAction.prepareImportLiferayWorkspace(liferayWorkspaceRootPath + "/projects/testGradleWorkspace");

        assertEquals(
            SELECT_LOCATION_OF_LIFERAY_WORKSPACE_PARENT_DIRECTORY, importLiferayWorkspaceProject.getValidationMsg() );

        assertEquals( MAVEN_LIFERAY_WORKSPACE, importLiferayWorkspaceProject.getBuildTypeText().getText() );
        assertFalse( importLiferayWorkspaceProject.getDownloadLiferaybundle().isChecked() );

        wizardAction.finishToWait();

        projectTree.setFocus();
        bot.viewByTitle( "Package Explorer" ).show();
        projectTree.expandNode( liferayWorkspaceName ).doubleClick();
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-modules" ).isVisible() );
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-themes" ).isVisible() );
        assertTrue( projectTree.getTreeItem( "testMavenWorkspace-wars" ).isVisible() );
        projectTree.getTreeItem( "testMavenWorkspace-modules" ).getTreeItem( "pom.xml" ).doubleClick();

        CTabItem switchCTabItem = new CTabItem( bot, "pom.xml" );
        sleep();
        switchCTabItem.click();

        Editor pomXmlFileModules = ide.getEditor( "testMavenWorkspace-modules/pom.xml" );
        assertContains( "testMavenWorkspace-modules", pomXmlFileModules.getText() );
        assertContains( "artifactId", pomXmlFileModules.getText() );

        pomXmlFileModules.close();

        projectTree.expandNode( liferayWorkspaceName, "pom.xml" ).doubleClick();

        switchCTabItem.click();

        Editor pomXmlFile = ide.getEditor( "testMavenWorkspace/pom.xml" );
        assertContains( "testMavenWorkspace", pomXmlFile.getText() );

        pomXmlFile.close();
    }

}
