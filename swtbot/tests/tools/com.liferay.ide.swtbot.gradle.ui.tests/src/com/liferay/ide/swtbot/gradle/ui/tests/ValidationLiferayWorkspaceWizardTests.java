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
public class ValidationLiferayWorkspaceWizardTests extends LiferayWorkspaceWizardBase
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
    public void initialStateTest()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        assertEquals( TEXT_PLEASE_ENTER_THE_WORKSPACE_NAME, newLiferayWorkspaceProjectWizard.getValidationMsg() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getWorkspaceName().getText() );

        checkBuildTypes();

        assertTrue( newLiferayWorkspaceProjectWizard.getUseDefaultLocation().isChecked() );
        assertEquals( false, newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().isChecked() );

        newLiferayWorkspaceProjectWizard.getUseDefaultLocation().deselect();
        assertEquals( eclipseWorkspace, newLiferayWorkspaceProjectWizard.getLocation().getText() );
        newLiferayWorkspaceProjectWizard.getUseDefaultLocation().select();

        newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().select();
        assertEquals( "", newLiferayWorkspaceProjectWizard.getServerName().getText() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getBundleUrl().getText() );
        newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().deselect();

        newLiferayWorkspaceProjectWizard.cancel();
    }

    @Test
    public void validationWorkspaceName()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( ".." );

        assertEquals( " '..'" + TEXT_INVALID_NAME_ON_PLATFORM, newLiferayWorkspaceProjectWizard.getValidationMsg() );

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( "##" );

        assertEquals( TEXT_INVALID_NAME_PROJECT, newLiferayWorkspaceProjectWizard.getValidationMsg() );

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( "*" );

        assertEquals(
            " *" + TEXT_INVALID_CHARACTER_IN_RESOURCE_NAME + "'*'.",
            newLiferayWorkspaceProjectWizard.getValidationMsg() );

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( TEXT_BLANK );

        assertEquals( TEXT_WORKSPACE_NAME_COULD_NOT_EMPTY, newLiferayWorkspaceProjectWizard.getValidationMsg() );

        newLiferayWorkspaceProjectWizard.getWorkspaceName().setText( projectName );

        assertEquals( TEXT_CREATE_LIFERAY_WORKSPACE, newLiferayWorkspaceProjectWizard.getValidationMsg() );

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
