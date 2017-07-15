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

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;

import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayWorkspaceWizard;
import com.liferay.ide.swtbot.project.ui.tests.BaseNewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.page.Tree;

/**
 * @author Terry Jia
 */
public class BaseLiferayWorkspaceWizardTests extends BaseNewLiferayModuleProjectWizard
{

    protected String projectName = "workspace-project";

    protected String serverName = "Liferay 7.0 CE Server";

    protected NewLiferayWorkspaceWizard newLiferayWorkspaceProjectWizard =
        new NewLiferayWorkspaceWizard( bot, INDEX_VALIDATION_WORKSPACE_NAME_MESSAGE );

    protected Tree projectTree = ide.getPackageExporerView().getProjectTree();

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();
        sleep();
    }

    @AfterClass
    public static void cleanAll()
    {
        ide.closeShell( LABEL_NEW_LIFERAY_WORPSPACE_PROJECT );
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
            sleep( 2000 );

            deleteResources.confirmDeleteFromDisk();
            deleteResources.confirm();

            try
            {
                continueDeleteResources.clickContinueButton();
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

            sleep( 5000 );

            ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
        }
    }

}
