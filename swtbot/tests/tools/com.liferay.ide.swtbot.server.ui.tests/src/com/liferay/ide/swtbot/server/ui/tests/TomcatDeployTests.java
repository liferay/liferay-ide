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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class TomcatDeployTests extends SwtbotBase
{

    private static String serverName = "Liferay 7-deploy";
    private static String serverStoppedLabel = serverName + "  [Stopped]";
    private static String serverStartedLabel = serverName + "  [Started]";

    @BeforeClass
    public static void startServer() throws IOException
    {
        envAction.unzipServer();

        envAction.prepareGeoFile();

        envAction.preparePortalExtFile();

        envAction.preparePortalSetupWizardFile();

        dialogAction.openPreferencesDialog();

        dialogAction.openServerRuntimeEnvironmentsDialog();

        dialogAction.openNewRuntimeWizard();

        wizardAction.prepareLiferay7RuntimeType();

        wizardAction.next();

        wizardAction.prepareLiferay7RuntimeInfo( serverName, envAction.getLiferayServerFullDir().toOSString() );

        wizardAction.finish();

        dialogAction.confirm();

        wizardAction.openNewLiferayServerWizard();

        wizardAction.prepareNewServer( serverName );

        wizardAction.finish();

        viewAction.showServersView();

        viewAction.serverStart( serverStoppedLabel );

        sleep( 200000 );
    }

    @Test
    public void deploySampleProject()
    {
        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModule( "test" );

        wizardAction.finishToWait();

        wizardAction.openNewLiferayModuleWizard();

        wizardAction.prepareLiferayModule( "test2" );

        wizardAction.finishToWait();

        viewAction.openAddAndRemoveDialog( serverStartedLabel );

        dialogAction.addModule( "test" );

        dialogAction.confirm();

        sleep( 10000 );
    }

    @AfterClass
    public static void stopServer() throws IOException
    {
        viewAction.serverStop( serverStartedLabel );

        sleep( 20000 );
    }

}
