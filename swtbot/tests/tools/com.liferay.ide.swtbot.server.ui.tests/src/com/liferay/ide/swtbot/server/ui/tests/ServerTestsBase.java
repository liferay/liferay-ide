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

package com.liferay.ide.swtbot.server.ui.tests;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferay7RuntimeWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewLiferayModuleWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.AddAndRemoveDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.NewRuntimeWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.NewServerWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.PreferencesDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.ServerRuntimeEnvironmentsPreferencesDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.ServersView;
import com.liferay.ide.swtbot.ui.page.Browser;

/**
 * @author Terry Jia
 */
public class ServerTestsBase extends SwtbotBase
{

    protected static AddAndRemoveDialog addAndRemoveDialog = new AddAndRemoveDialog( bot );

    protected static Browser browser = ide.getBrowser();

    protected static NewLiferay7RuntimeWizard newliferay7RuntimeWizard = new NewLiferay7RuntimeWizard( bot );

    protected static NewRuntimeWizard newRuntimeWizard = new NewRuntimeWizard( bot );

    protected static NewServerWizard newServerWizard = new NewServerWizard( bot );

    protected static PreferencesDialog preferencesDialog = new PreferencesDialog( bot );

    protected static ServerRuntimeEnvironmentsPreferencesDialog serverRuntimePage =
        new ServerRuntimeEnvironmentsPreferencesDialog( bot );

    protected static NewLiferayModuleWizard newModuleProjectWizard = new NewLiferayModuleWizard( bot );

    protected static ServersView serversView = ide.getServersView();

    public static void addLiferay7Runtime( String runtimeName )
    {
        ide.getPreferencesMenu().click();

        preferencesDialog.selectServerRuntimeEnvironmentsPage();

        serverRuntimePage.getAddBtn().click();

        newRuntimeWizard.selectServerType( "Liferay, Inc.", "Liferay 7.x" );

        newRuntimeWizard.next();

        newliferay7RuntimeWizard.getName().setText( runtimeName );

        newliferay7RuntimeWizard.getLocation().setText(
            getLiferayServerDir().toString() + "/" + getLiferayPluginServerName() );

        newliferay7RuntimeWizard.finish();

        serverRuntimePage.confirm();
    }

    public static void addLiferay7Server( String serverName )
    {
        addLiferay7Runtime( serverName );

        wizardAction.openNewLiferayServerWizard();

        newServerWizard.getServerName().setText( serverName );

        newServerWizard.finish();

        ide.showServersView();
    }

}
