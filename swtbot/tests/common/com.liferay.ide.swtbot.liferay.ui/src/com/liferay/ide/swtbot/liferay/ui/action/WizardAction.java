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
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ImportLiferayWorkspaceProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferay7RuntimeWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayComponentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewFragmentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayJsfProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleInfoWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayWorkspaceWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewModuleFragmentInfoWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.ImportProjectWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.NewRuntimeWizard;
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

    private final ImportProjectWizard importProjectWizard = new ImportProjectWizard( bot );
    private final ImportLiferayWorkspaceProjectWizard importLiferayWorkspaceProjectWizard = new ImportLiferayWorkspaceProjectWizard( bot );
    private final NewModuleFragmentInfoWizard newFragmentInfoWizard = new NewModuleFragmentInfoWizard( bot );
    private final NewFragmentWizard newFragmentWizard = new NewFragmentWizard( bot );
    private final NewLiferayJsfProjectWizard newJsfProjectWizard = new NewLiferayJsfProjectWizard( bot );
    private final NewLiferay7RuntimeWizard newLiferay7RuntimeWizard = new NewLiferay7RuntimeWizard( bot );
    private final NewLiferayComponentWizard newLiferayComponentWizard = new NewLiferayComponentWizard( bot );
    private final NewLiferayModuleInfoWizard newModuleInfoWizard = new NewLiferayModuleInfoWizard( bot );
    private final NewLiferayModuleWizard newModuleWizard = new NewLiferayModuleWizard( bot );
    private final NewRuntimeWizard newRuntimeWizard = new NewRuntimeWizard( bot );
    private final NewServerWizard newServerWizard = new NewServerWizard( bot );
    private final NewLiferayWorkspaceWizard newWorkspaceWizard = new NewLiferayWorkspaceWizard( bot );

    private final Wizard wizard = new Wizard( bot );

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

    public String getValidationMsg( final int validationMsgIndex )
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

        prepareImportType( LIFERAY, LIFERAY_WORKSPACE_PROJECT );

        next();
    }

    public void openNewFragmentWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleFragment().click();
    }

    public void openNewLiferayComponentClassWizard()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();
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

    public void openSelectModelClassAndServiceDialog()
    {
        newLiferayComponentWizard.getBrowseBtn().click();
    }

    public void openSelectServiceDialog()
    {
        newModuleInfoWizard.getBrowseBtn().click();
    }

    public void prepareComponentClass( final String projectName )
    {
        newLiferayComponentWizard.getProjectNames().setSelection( projectName );
    }

    public void prepareComponentClass( final String projectName, final String componentClassTemplate )
    {
        newLiferayComponentWizard.getProjectNames().setSelection( projectName );
        newLiferayComponentWizard.getComponentClassTemplates().setSelection( componentClassTemplate );

        ide.sleep();
    }

    public void prepareFragment( final String projectName, final String buildType )
    {
        newFragmentWizard.getProjectName().setText( projectName );
        newFragmentWizard.getBuildTypes().setSelection( buildType );
    }

    public void prepareFragmentGradle( final String projectName )
    {
        prepareFragment( projectName, GRADLE );
    }

    public void prepareImportLiferayWorkspace(String location) {
    		importLiferayWorkspaceProjectWizard.getWorkspaceLocation().setText(location);
    }
    
    public void prepareFragmentMaven( final String projectName )
    {
        prepareFragment( projectName, MAVEN );
    }

    public void prepareImportType( final String category, final String type )
    {
        prepareImportType( StringPool.BLANK, category, type );
    }

    public void prepareImportType( final String filterText, final String category, final String type )
    {
        importProjectWizard.getFilterText().setText( filterText );

        importProjectWizard.getTypes().expandNode( category, type ).select();
    }

    public void prepareJsfProject( final String projectName, final String buildType, final String componentSuite )
    {
        newJsfProjectWizard.getProjectName().setText( projectName );
        newJsfProjectWizard.getBuildTypes().setSelection( buildType );
        newJsfProjectWizard.getComponentSuite().setSelection( componentSuite );
    }

    public void prepareJsfProjectGradle( final String projectName, final String componentSuite )
    {
        prepareJsfProject( projectName, GRADLE, componentSuite );
    }

    public void prepareJsfProjectMaven( final String projectName, final String componentSuite )
    {
        prepareJsfProject( projectName, MAVEN, componentSuite );
    }

    public void prepareLiferay7RuntimeInfo( final String location )
    {
        newLiferay7RuntimeWizard.getLocation().setText( location );
    }

    public void prepareLiferay7RuntimeInfo( final String name, final String location )
    {
        newLiferay7RuntimeWizard.getName().setText( name );
        newLiferay7RuntimeWizard.getLocation().setText( location );
    }

    public void prepareLiferay7RuntimeType()
    {
        prepareRuntimeType( LIFERAY_INC, LIFERAY_7_X );
    }

    public void prepareLiferayModule( final String projectName )
    {
        newModuleWizard.getProjectName().setText( projectName );
    }

    public void prepareLiferayModule( final String projectName, final String buildType )
    {
        newModuleWizard.getProjectName().setText( projectName );
        newModuleWizard.getBuildTypes().setSelection( buildType );
    }

    public void prepareLiferayModule( final String projectName, final String buildType, final String template )
    {
        newModuleWizard.getProjectName().setText( projectName );
        newModuleWizard.getBuildTypes().setSelection( buildType );
        newModuleWizard.getProjectTemplates().setSelection( template );
    }

    public void prepareLiferayModule(
        final String projectName, final String buildType, final String template, final boolean useDefaultLocation,
        final String location )
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

    public void prepareLiferayModuleGradle( final String projectName )
    {
        prepareLiferayModule( projectName, GRADLE, MVC_PORTLET );
    }

    public void prepareLiferayModuleGradle( final String projectName, final String template )
    {
        prepareLiferayModule( projectName, GRADLE, template );
    }

    public void prepareLiferayModuleInfo( final String className, final String packageName )
    {
        newModuleInfoWizard.getComponentClassName().setText( className );

    }

    public void prepareLiferayModuleMaven( final String projectName, final String template )
    {
        prepareLiferayModule( projectName, MAVEN, template );
    }

    public void prepareLiferayWorkspace( final String projectName )
    {
        newWorkspaceWizard.getProjectName().setText( projectName );
    }

    public void prepareLiferayWorkspace( final String projectName, final String buildType )
    {
        newWorkspaceWizard.getProjectName().setText( projectName );
        newWorkspaceWizard.getBuildTypes().setSelection( buildType );
    }

    public void prepareLiferayWorkspace(
        final String projectName, final String buildType, final boolean downloadLiferayBundle, final String serverName,
        final boolean useDefaultBundleUrl, final String bundleUrl )
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

    public void prepareLiferayWorkspaceGradle( final String projectName )
    {
        prepareLiferayWorkspace( projectName, GRADLE );
    }

    public void prepareLiferayWorkspaceGradle(
        final String projectName, final boolean downloadLiferayBundle, final String serverName,
        final boolean useDefaultBundleUrl, final String bundleUrl )
    {
        prepareLiferayWorkspace(
            projectName, GRADLE, downloadLiferayBundle, serverName, useDefaultBundleUrl, bundleUrl );
    }

    public void prepareLiferayWorkspaceMaven( final String projectName )
    {
        prepareLiferayWorkspace( projectName, MAVEN );
    }

    public void prepareNewServer( final String serverName )
    {
        newServerWizard.getServerName().setText( serverName );
    }

    public void prepareRuntimeType( final String category, final String type )
    {
        newRuntimeWizard.getServerTypes().getTreeItem( category ).getTreeItem( type ).select();
    }

}
