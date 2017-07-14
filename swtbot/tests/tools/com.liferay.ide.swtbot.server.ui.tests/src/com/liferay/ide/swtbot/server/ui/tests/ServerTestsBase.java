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

import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.AddAndRemoveDialog;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.NewRuntimeWizard;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.NewServerWizard;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.PreferencesDialog;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.ServerRuntimeEnvironmentsPreferencesPage;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.ServersView;
import com.liferay.ide.swtbot.ui.tests.liferay.page.NewLiferay7RuntimeWizard;
import com.liferay.ide.swtbot.ui.tests.liferay.page.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.ui.tests.page.BrowserPO;

/**
 * @author Terry Jia
 */
public class ServerTestsBase extends SWTBotBase
{

    protected static AddAndRemoveDialog addAndRemoveDialog = new AddAndRemoveDialog( bot );

    protected static BrowserPO browser = eclipse.getBrowser();

    protected static NewLiferay7RuntimeWizard newliferay7RuntimeWizard = new NewLiferay7RuntimeWizard( bot );

    protected static NewRuntimeWizard newRuntimeWizard = new NewRuntimeWizard( bot );

    protected static NewServerWizard newServerWizard = new NewServerWizard( bot );

    protected static PreferencesDialog preferencesDialog = new PreferencesDialog( bot );

    protected static ServerRuntimeEnvironmentsPreferencesPage serverRuntimePage =
        new ServerRuntimeEnvironmentsPreferencesPage( bot );

    protected static NewLiferayModuleProjectWizard newModuleProjectWizard = new NewLiferayModuleProjectWizard( bot );

    protected static ServersView serversView = eclipse.getServersView();

    public static void addLiferay7Runtime( String runtimeName )
    {
        eclipse.getPreferencesMenu().click();

        preferencesDialog.selectServerRuntimeEnvironmentsPage();

        serverRuntimePage.getAddButton().click();

        newRuntimeWizard.selectServerType( "Liferay, Inc.", "Liferay 7.x" );

        newRuntimeWizard.next();

        newliferay7RuntimeWizard.getName().setText( runtimeName );

        newliferay7RuntimeWizard.getServerLocation().setText(
            getLiferayServerDir().toString() + "/" + getLiferayPluginServerName() );

        newliferay7RuntimeWizard.finish();

        serverRuntimePage.confirm();
    }

    public static void addLiferay7Server( String serverName )
    {
        addLiferay7Runtime( serverName );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayServer().click();

        newServerWizard.getServerName().setText( serverName );

        newServerWizard.finish();

        eclipse.showServersView();
    }

}
