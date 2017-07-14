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

import org.junit.Test;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ValidationLiferayWorkspaceWizardTests extends BaseLiferayWorkspaceWizardTests
{

    @Test
    public void initialStateTest()
    {
        assertEquals( TEXT_PLEASE_ENTER_THE_WORKSPACE_NAME, newLiferayWorkspaceProjectWizard.getValidationMessage() );
        assertEquals( "", newLiferayWorkspaceProjectWizard.getWorkspaceName() );

        checkBuildTypes();

        assertTrue( newLiferayWorkspaceProjectWizard.getUseDefaultLocation().isChecked() );
        assertEquals( false, newLiferayWorkspaceProjectWizard.isDownloadLiferayBundleChecked() );

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

}
