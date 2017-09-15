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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ImportLiferayWorkspaceProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayWorkspaceWizard;
import com.liferay.ide.swtbot.liferay.ui.util.ValidationMsg;
import com.liferay.ide.swtbot.ui.util.StringPool;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ValidationLiferayWorkspaceWizardTests extends SwtbotBase
{

    NewLiferayWorkspaceWizard newLiferayWorkspaceProjectWizard = new NewLiferayWorkspaceWizard( bot );
    ImportLiferayWorkspaceProjectWizard importLiferayWorkspaceProject = new ImportLiferayWorkspaceProjectWizard( bot );

    @Test
    public void exsitingLiferayWorkspace()
    {
        wizardAction.openNewLiferayWorkspaceWizard();

        wizardAction.prepareLiferayWorkspace( "test" );

        wizardAction.finish();

        wizardAction.openNewLiferayWorkspaceWizard();

        wizardAction.prepareLiferayWorkspace( "test" );

        assertEquals( A_LIFERAY_WORKSPACE_PROJECT_ALREADY_EXISTS, newLiferayWorkspaceProjectWizard.getValidationMsg() );

        wizardAction.cancel();

        viewAction.deleteProject( "test" );
    }

    @Test
    public void initialStateAndValidationProjectName()
    {
        wizardAction.openImportLiferayWorkspaceWizard();

        assertEquals( PLEASE_SELECT_THE_WORKSPACE_LOCATION, importLiferayWorkspaceProject.getValidationMsg() );

        assertEquals( StringPool.BLANK, importLiferayWorkspaceProject.getWorkspaceLocation().getText() );
        assertEquals( StringPool.BLANK, importLiferayWorkspaceProject.getBuildTypeText().getText() );
        assertFalse( importLiferayWorkspaceProject.getAddProjectToWorkingSet().isChecked() );

        assertTrue( importLiferayWorkspaceProject.backBtn().isEnabled() );
        assertFalse( importLiferayWorkspaceProject.nextBtn().isEnabled() );
        assertFalse( importLiferayWorkspaceProject.finishBtn().isEnabled() );
        assertTrue( importLiferayWorkspaceProject.cancelBtn().isEnabled() );

        for( ValidationMsg msg : getValidationMsgs(
            new File( envAction.getValidationFolder(), "import-liferay-workspace-wizard-location.csv" ) ) )
        {
            importLiferayWorkspaceProject.getWorkspaceLocation().setText( msg.getInput() );

            assertEquals( msg.getExpect(), wizardAction.getValidationMsg( 2 ) );
        }

        wizardAction.cancel();;
    }

    @Test
    public void initialStateTest()
    {
        wizardAction.openNewLiferayWorkspaceWizard();

        assertEquals( PLEASE_ENTER_THE_WORKSPACE_NAME, newLiferayWorkspaceProjectWizard.getValidationMsg() );
        assertEquals( StringPool.BLANK, newLiferayWorkspaceProjectWizard.getProjectName().getText() );

        assertTrue( newLiferayWorkspaceProjectWizard.getUseDefaultLocation().isChecked() );
        assertEquals( false, newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().isChecked() );

        newLiferayWorkspaceProjectWizard.getUseDefaultLocation().deselect();

        assertEquals( eclipseWorkspace, newLiferayWorkspaceProjectWizard.getLocation().getText() );

        newLiferayWorkspaceProjectWizard.getUseDefaultLocation().select();

        newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().select();
        newLiferayWorkspaceProjectWizard.getDownloadLiferayBundle().deselect();

        wizardAction.cancel();
    }

    @Test
    public void validationWorkspaceName()
    {
        wizardAction.openNewLiferayWorkspaceWizard();

        for( ValidationMsg msg : getValidationMsgs(
            new File( envAction.getValidationFolder(), "new-liferay-workspace-wizard-project-name.csv" ) ) )
        {
            newLiferayWorkspaceProjectWizard.getProjectName().setText( msg.getInput() );

            assertEquals( msg.getExpect(), wizardAction.getValidationMsg( 2 ) );
        }

        wizardAction.cancel();
    }

}
