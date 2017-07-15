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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class TomcatDeployTests extends ServerTestsBase
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    final static String serverName = "Liferay 7-deploy";
    final static String serverStoppedLabel = serverName + "  [Stopped]";
    final static String serverStartedLabel = serverName + "  [Started]";

    @BeforeClass
    public static void startServer() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipServer();

        prepareGeoFile();

        preparePortalExtFile();

        preparePortalSetupWizardFile();

        addLiferay7Server( serverName );

        serversView.getServers().getTreeItem( serverStoppedLabel ).select();

        serversView.clickStartBtn();

        sleep( 200000 );
    }

    @Test
    public void deploySampleProject()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();

        newModuleProjectWizard.createModuleProject( "test" );
        newModuleProjectWizard.finish();

        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();

        newModuleProjectWizard.createModuleProject( "test2" );
        newModuleProjectWizard.finish();

        serversView.getServers().getTreeItem( serverStartedLabel ).contextMenu( "Add and Remove..." );

        addAndRemoveDialog.add( "test" );

        addAndRemoveDialog.confirm();

        sleep( 10000 );
    }

    @AfterClass
    public static void stopServer() throws IOException
    {
        serversView.getServers().getTreeItem( serverStartedLabel ).select();

        serversView.clickStopBtn();

        sleep( 20000 );
    }

}
