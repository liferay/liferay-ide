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

package com.liferay.ide.swtbot.liferay.ui.action;

import com.liferay.ide.swtbot.liferay.ui.UIAction;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferay7RuntimeWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewFragmentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayJsfProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayWorkspaceWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewModuleFragmentInfoWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.ImportProjectWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.NewServerWizard;
import com.liferay.ide.swtbot.ui.page.Wizard;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

/**
 * @author Terry Jia
 * @author Ying Xu
 */
public class WizardAction extends UIAction
{

    final ImportProjectWizard importProjectWizard = new ImportProjectWizard( bot );
    final NewModuleFragmentInfoWizard newFragmentInfoWizard = new NewModuleFragmentInfoWizard( bot );
    final NewFragmentWizard newFragmentWizard = new NewFragmentWizard( bot );
    final NewLiferayJsfProjectWizard newJsfProjectWizard = new NewLiferayJsfProjectWizard( bot );
    final NewLiferay7RuntimeWizard newLiferay7RuntimeWizard = new NewLiferay7RuntimeWizard( bot );
    final NewLiferayModuleInfoWizard newModuleInfoWizard = new NewLiferayModuleInfoWizard( bot );
    final NewLiferayModuleWizard newModuleWizard = new NewLiferayModuleWizard( bot );
    final NewServerWizard newServerWizard = new NewServerWizard( bot );
    final NewLiferayWorkspaceWizard newWorkspaceWizard = new NewLiferayWorkspaceWizard( bot );

    Wizard wizard = new Wizard( bot );

    public WizardAction( SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public void cancel()
    {
        wizard.cancel();
    }

    public void finish()
    {
        wizard.finish();
    }

    public void finishToWait()
    {
        wizard.finish();

        long origin = SWTBotPreferences.TIMEOUT;

        SWTBotPreferences.TIMEOUT = 1000 * 60;

        openNewLiferayModuleWizard();

        cancel();

        SWTBotPreferences.TIMEOUT = origin;
    }

    public String getValidationMsg( int validationMsgIndex )
    {
        return wizard.getValidationMsg( validationMsgIndex );
    }

    public void next()
    {
        wizard.next();
    }

    public void openAddOverrideFilesDialog()
    {
        newFragmentInfoWizard.getAddOverrideFilesBtn().click();
    }

    public void openBrowseOsgiBundleDialog()
    {
        newFragmentInfoWizard.getBrowseOsgiBtn().click();
    }

    public void openImportLiferayWorkspaceWizard()
    {
        ide.getFileMenu().clickMenu( IMPORT );

        selectImportType( LIFERAY, LIFERAY_WORKSPACE_PROJECT );

        next();
    }

    public void openNewFragmentWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleFragment().click();
    }

    public void openNewLiferayJsfProjectWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayJSFProject().click();
    }

    public void openNewLiferayModuleWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayModule().click();
    }

    public void openNewLiferayPluginProjectsFromExistingSourceWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayPluginProjectFromExistingSource().click();
    }

    public void openNewLiferayServerWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayServer().click();
    }

    public void openNewLiferayWorkspaceWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();
    }

    public void openNewRuntimeWizardFragment()
    {
        newFragmentWizard.getNewRuntimeBtn().click();
    }

    public void openSelectServiceDialog()
    {
        newModuleInfoWizard.getBrowseBtn().click();
    }

    public void prepareFragment( String projectName, String buildType )
    {
        newFragmentWizard.getProjectName().setText( projectName );
        newFragmentWizard.getBuildTypes().setSelection( buildType );
    }

    public void prepareFragmentGradle( String projectName )
    {
        prepareFragment( projectName, GRADLE );
    }

    public void prepareFragmentMaven( String projectName )
    {
        prepareFragment( projectName, MAVEN );
    }

    public void prepareJsfProject( String projectName, String buildType, String componentSuite )
    {
        newJsfProjectWizard.getProjectName().setText( projectName );
        newJsfProjectWizard.getBuildTypes().setSelection( buildType );
        newJsfProjectWizard.getComponentSuite().setSelection( componentSuite );
    }

    public void prepareJsfProjectGradle( String projectName, String componentSuite )
    {
        prepareJsfProject( projectName, GRADLE, componentSuite );
    }

    public void prepareJsfProjectMaven( String projectName, String componentSuite )
    {
        prepareJsfProject( projectName, MAVEN, componentSuite );
    }

    public void prepareLiferay7Runtime( String location )
    {
        newLiferay7RuntimeWizard.getLocation().setText( location );
    }

    public void prepareLiferay7Runtime( String name, String location )
    {
        newLiferay7RuntimeWizard.getName().setText( name );
        newLiferay7RuntimeWizard.getLocation().setText( location );
    }

    public void prepareLiferayModule( String projectName )
    {
        newModuleWizard.getProjectName().setText( projectName );
    }

    public void prepareLiferayModule( String projectName, String buildType )
    {
        newModuleWizard.getProjectName().setText( projectName );
        newModuleWizard.getBuildTypes().setSelection( buildType );
    }

    public void prepareLiferayModule( String projectName, String buildType, String template )
    {
        newModuleWizard.getProjectName().setText( projectName );
        newModuleWizard.getBuildTypes().setSelection( buildType );
        newModuleWizard.getProjectTemplates().setSelection( template );
    }

    public void prepareLiferayModule(
        String projectName, String buildType, String template, boolean useDefaultLocation, String location )
    {
        prepareLiferayModule( projectName, buildType, template );

        if( useDefaultLocation )
        {
            newModuleWizard.getUseDefaultLocation().deselect();
            newModuleWizard.getLocation().setText( location );
        }
        else
        {
            newModuleWizard.getUseDefaultLocation().select();
        }
    }

    public void prepareLiferayModuleGradle( String projectName )
    {
        prepareLiferayModule( projectName, GRADLE, MVC_PORTLET );
    }

    public void prepareLiferayModuleGradle( String projectName, String template )
    {
        prepareLiferayModule( projectName, GRADLE, template );
    }

    public void prepareLiferayModuleInfo( String className, String packageName )
    {
        newModuleInfoWizard.getComponentClassName().setText( className );

    }

    public void prepareLiferayModuleMaven( String projectName, String template )
    {
        prepareLiferayModule( projectName, MAVEN, template );
    }

    public void prepareLiferayWorkspace( String projectName )
    {
        newWorkspaceWizard.getProjectName().setText( projectName );
    }

    public void prepareLiferayWorkspace( String projectName, String buildType )
    {
        newWorkspaceWizard.getProjectName().setText( projectName );
        newWorkspaceWizard.getBuildTypes().setSelection( buildType );
    }

    public void prepareLiferayWorkspace(
        String projectName, String buildType, boolean downloadLiferayBundle, String serverName,
        boolean useDefaultBundleUrl, String bundleUrl )
    {
        prepareLiferayWorkspace( projectName, buildType );

        if( downloadLiferayBundle )
        {
            newWorkspaceWizard.getDownloadLiferayBundle().select();

            newWorkspaceWizard.getServerName().setText( serverName );

            if( !useDefaultBundleUrl )
            {
                newWorkspaceWizard.getBundleUrl().setText( bundleUrl );
            }
        }
        else
        {
            newWorkspaceWizard.getDownloadLiferayBundle().deselect();
        }
    }

    public void prepareLiferayWorkspaceGradle( String projectName )
    {
        prepareLiferayWorkspace( projectName, GRADLE );
    }

    public void prepareLiferayWorkspaceGradle(
        String projectName, boolean downloadLiferayBundle, String serverName, boolean useDefaultBundleUrl,
        String bundleUrl )
    {
        prepareLiferayWorkspace(
            projectName, GRADLE, downloadLiferayBundle, serverName, useDefaultBundleUrl, bundleUrl );
    }

    public void prepareLiferayWorkspaceMaven( String projectName )
    {
        prepareLiferayWorkspace( projectName, MAVEN );
    }

    public void selectImportType( String selectTypeTree, String selectTypeNode )
    {
        selectImportType( StringPool.BLANK, selectTypeTree, selectTypeNode );
    }

    public void selectImportType( String filterText, String category, String type )
    {
        importProjectWizard.getFilterText().setText( filterText );

        importProjectWizard.getTypes().expandNode( category, type ).select();
    }

    public void selectServerType( String category, String type )
    {
        selectImportType( StringPool.BLANK, category, type );
    }

    public void selectServerType( String filterText, String category, String type )
    {
        newServerWizard.getServerTypes().expandNode( category, type ).select();
    }

}
