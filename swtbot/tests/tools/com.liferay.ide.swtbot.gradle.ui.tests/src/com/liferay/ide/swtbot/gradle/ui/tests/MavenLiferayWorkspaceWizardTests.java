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
import org.junit.Test;

import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class MavenLiferayWorkspaceWizardTests extends LiferayWorkspaceWizardBase
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
    public void newMavenLiferayWorkspaceProjectWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.getBuildTypes().setSelection( TEXT_BUILD_TYPE_MAVEN );

        assertEquals( TEXT_PLEASE_ENTER_THE_WORKSPACE_NAME, newLiferayWorkspaceProjectWizard.getValidationMsg() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getWorkspaceName().getText() );
        assertEquals( false, newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().isChecked() );

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( projectName );

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
            TEXT_BUILD_TYPE_MAVEN, themeProjectName, MODULE_THEME, eclipseWorkspace + "/" + projectName + "/wars",
            false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue(
            projectTree.expandNode( projectName, projectName + "-wars (in wars)", themeProjectName ).isVisible() );

        String moduleProjectName = "testMavenModuleInLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_MAVEN, moduleProjectName, MODULE_MVC_PORTLET,
            eclipseWorkspace + "/" + projectName + "/modules", false, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK,
            false );
        sleep( 10000 );

        projectTree.setFocus();
        assertTrue(
            projectTree.expandNode(
                projectName, projectName + "-modules (in modules)", "testMavenModuleInLWS" ).isVisible() );

        String projectName = "testGradleModuleInMavenLWS";

        newLiferayModuleProject(
            TEXT_BUILD_TYPE_GRADLE, projectName, MODULE_MVC_PORTLET, eclipseWorkspace, false, TEXT_BLANK, TEXT_BLANK,
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
