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

package com.liferay.ide.swtbot.gradle.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Tree;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class GradleLiferayWorkspaceWizardTests extends LiferayWorkspaceWizardBase
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
    @Ignore
    public void newGradleLiferayWorksapceProjectWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( projectName );
        sleep();
        newLiferayWorkspaceProjectWizard.getBuildTypes().setSelection( TEXT_BUILD_TYPE_GRADLE );

        newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().select();

        assertEquals( "", newLiferayWorkspaceProjectWizard.getServerName().getText() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getBundleUrl().getText() );

        newLiferayWorkspaceProjectWizard.getServerName().setText( serverName );
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

        Tree serverTree = new Tree( bot, 1 );

        serverTree.getTreeItem( "bundles [Stopped]" );

        String moduleProjectName = "testModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, moduleProjectName, MODULE_MVC_PORTLET,
            eclipseWorkspace + "/" + projectName + "/modules", false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK,
            false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "modules", moduleProjectName ).isVisible() );

        String themeProjectName = "testThemeModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, themeProjectName, MODULE_THEME, eclipseWorkspace + "/" + projectName + "/wars",
            false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "wars", themeProjectName ).isVisible() );

        String projectName = "testMavenModuleInGradleLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, projectName, MODULE_MVC_PORTLET, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, TEXT_BLANK, false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue( projectTree.getTreeItem( projectName ).isVisible() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( "test" );
        sleep();
        assertEquals( TEXT_WORKSPACE_ALREADY_EXISTS, newLiferayWorkspaceProjectWizard.getValidationMsg() );

        newLiferayWorkspaceProjectWizard.cancel();
    }

    @Test
    public void newGradleLiferayWorkspaceProjectWithoutDownloadBundle()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.getBuildTypes().setSelection( TEXT_BUILD_TYPE_GRADLE );

        assertEquals( TEXT_PLEASE_ENTER_THE_WORKSPACE_NAME, newLiferayWorkspaceProjectWizard.getValidationMsg() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getWorkspaceName().getText() );

        assertEquals( false, newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().isChecked() );

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( projectName );

        newLiferayWorkspaceProjectWizard.finish();

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

        Editor gradlePropertiesEditor = ide.getEditor( "gradle.properties" );

        gradlePropertiesEditor.setText(
            "liferay.workspace.home.dir=bundlesTest" + "\r" + "liferay.workspace.modules.dir=modulesTest" + "\r" +
                "liferay.workspace.wars.dir=warsTest" );

        gradlePropertiesEditor.save();

        // create module project in liferay workspace
        String moduleProjectName = "testModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, moduleProjectName, MODULE_MVC_PORTLET,
            eclipseWorkspace + "/" + projectName + "/modulesTest", false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK,
            TEXT_BLANK, false );

        projectTree.setFocus();

        assertTrue( projectTree.expandNode( projectName, "modulesTest", moduleProjectName ).isVisible() );

        String themeProjectName = "testThemeModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, themeProjectName, MODULE_THEME, eclipseWorkspace + "/" + projectName + "/warsTest",
            false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "warsTest", themeProjectName ).isVisible() );

        // init bundle
        projectTree.getTreeItem( projectName ).doAction( "Liferay", "Initialize Server Bundle" );
        sleep( 45000 );

        projectTree.setFocus();
        assertTrue( projectTree.expandNode( projectName, "bundlesTest" ).isVisible() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( "test" );

        assertEquals( TEXT_WORKSPACE_ALREADY_EXISTS, newLiferayWorkspaceProjectWizard.getValidationMsg() );

        newLiferayWorkspaceProjectWizard.cancel();
    }

    @After
    public void deleteLiferayWorkspace() throws IOException
    {
        killGradleProcess();

        if( ide.getPackageExporerView().hasProjects() )
        {
            DeleteResourcesDialog deleteResources = new DeleteResourcesDialog( bot );

            DeleteResourcesContinueDialog continueDeleteResources =
                new DeleteResourcesContinueDialog( bot, "Delete Resources" );

            projectTree.getTreeItem( projectName ).doAction( DELETE );

            deleteResources.getDeleteFromDisk().select();
            deleteResources.confirm();

            try
            {
                continueDeleteResources.getContinueBtn().click();
            }
            catch( Exception e )
            {
            }
        }
    }

    @Before
    public void importModuleProject()
    {
        Assume.assumeTrue( runTest() || runAllTests() );
    }

}
