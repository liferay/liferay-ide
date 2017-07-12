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

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.server.ui.tests.page.ServerEditor;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ServerTomcatTests extends ServerTestsBase
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @BeforeClass
    public static void prepareServer() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipServer();

        prepareGeoFile();

        preparePortalExtFile();

        preparePortalSetupWizardFile();

        final String serverName = "Liferay 7-initialization";

        addLiferay7Server( serverName );

        final String serverStoppedLabel = serverName + "  [Stopped]";

        serversView.getServers().getTreeItem( serverStoppedLabel ).select();

        serversView.getStart().click();

        sleep( 200000 );

        final String serverStartedLabel = serverName + "  [Started]";

        serversView.getServers().getTreeItem( serverStartedLabel ).contextMenu( "Open Liferay Portal Home" );

        sleep( 20000 );

        serversView.getServers().getTreeItem( serverStartedLabel ).select();

        serversView.getStop().click();

        sleep( 20000 );
    }

    @After
    public void closeWizard()
    {
        eclipse.closeShell( TITLE_NEW_SERVER );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );
    }

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

    @Test
    public void addLiferay7RuntimeFromPreferences()
    {
        addLiferay7Runtime( "Liferay 7-add-runtime" );
    }

    @Test
    public void deleteLiferay7RuntimeFromPreferences()
    {
        final String runtimeName = "Liferay 7-delete";

        addLiferay7Runtime( runtimeName );

        eclipse.getPreferencesMenu().click();

        preferencesDialog.selectServerRuntimeEnvironmentsPage();

        serverRuntimePage.selectRuntime( runtimeName );

        serverRuntimePage.getRemoveButton().click();

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

    @Test
    public void addLiferay7ServerFromMenu()
    {
        addLiferay7Server( "Liferay 7-add-server" );
    }

    @Test
    public void testLiferay7ServerDebug()
    {
        final String serverName = "Liferay 7-debug";

        addLiferay7Server( serverName );

        final String serverStoppedLabel = serverName + "  [Stopped]";

        serversView.getServers().getTreeItem( serverStoppedLabel ).select();

        serversView.getDebug().click();

        sleep( 100000 );

        final String serverDebuggingLabel = serverName + "  [Debugging]";

        serversView.getServers().getTreeItem( serverDebuggingLabel ).select();

        serversView.getStop().click();

        sleep( 20000 );
    }

    @Test
    public void serverEditorPortsChange()
    {
        final String serverName = "Liferay 7-http-port-change";

        addLiferay7Server( serverName );

        final String serverStoppedLabel = serverName + "  [Stopped]";

        serversView.getServers().getTreeItem( serverStoppedLabel ).doubleClick();

        final ServerEditor serverEditor = new ServerEditor( bot, serverName );
        final ServerEditor serverEditorWithLabel = new ServerEditor( bot, serverStoppedLabel );

        try
        {
            serverEditor.getHttpPort().setText( "8081" );
        }
        catch( Exception e )
        {
            serverEditorWithLabel.getHttpPort().setText( "8081" );
        }

        try
        {
            serverEditor.save();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.save();
        }

        try
        {
            serverEditor.close();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.close();
        }

        serversView.getServers().getTreeItem( serverStoppedLabel ).doubleClick();

        try
        {
            serverEditor.getHttpPort().setText( "8080" );
        }
        catch( Exception e )
        {
            serverEditorWithLabel.getHttpPort().setText( "8080" );
        }

        try
        {
            serverEditor.save();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.save();
        }

        try
        {
            serverEditor.close();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.close();
        }
    }

    @Test
    public void serverEditorPortsChangeAndStart()
    {
        final String serverName = "Liferay 7-http-port-change-and-start";

        addLiferay7Server( serverName );

        final String serverStoppedLabel = serverName + "  [Stopped]";

        serversView.getServers().getTreeItem( serverStoppedLabel ).doubleClick();

        final ServerEditor serverEditor = new ServerEditor( bot, serverName );
        final ServerEditor serverEditorWithLabelStopped = new ServerEditor( bot, serverStoppedLabel );

        try
        {
            serverEditor.getHttpPort().setText( "8082" );
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.getHttpPort().setText( "8082" );
        }

        try
        {
            serverEditor.save();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.save();
        }

        try
        {
            serverEditor.close();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.close();
        }

        serversView.getServers().getTreeItem( serverStoppedLabel ).select();

        serversView.getStart().click();

        sleep( 100000 );

        final String serverStartedLabel = serverName + "  [Started]";

        serversView.getServers().getTreeItem( serverStartedLabel ).select();

        serversView.getStop().click();

        sleep( 20000 );

        serversView.getServers().getTreeItem( serverStoppedLabel ).doubleClick();

        try
        {
            serverEditor.getHttpPort().setText( "8080" );
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.getHttpPort().setText( "8080" );
        }

        try
        {
            serverEditor.save();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.save();
        }

        try
        {
            serverEditor.close();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.close();
        }
    }

    @Test
    public void serverEditorCustomLaunchSettingsChange()
    {
        final String serverName = "Liferay 7-custom-launch-settings";

        addLiferay7Server( serverName );

        final String serverStoppedLabel = serverName + "  [Stopped]";

        serversView.getServers().getTreeItem( serverStoppedLabel ).doubleClick();

        final ServerEditor serverEditor = new ServerEditor( bot, serverName );
        final ServerEditor serverEditorWithLabel = new ServerEditor( bot, serverStoppedLabel );

        try
        {
            serverEditor.getCustomLaunchSettings().click();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.getCustomLaunchSettings().click();
        }

        try
        {
            serverEditor.getUseDeveloperMode().select();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.getUseDeveloperMode().select();
        }

        try
        {
            serverEditor.save();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.save();
        }

        try
        {
            serverEditor.close();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.close();
        }

        serversView.getServers().getTreeItem( serverStoppedLabel ).doubleClick();

        try
        {
            serverEditor.getDefaultLaunchSettings().click();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.getDefaultLaunchSettings().click();
        }

        try
        {
            serverEditor.save();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.save();
        }

        try
        {
            serverEditor.close();
        }
        catch( Exception e )
        {
            serverEditorWithLabel.close();
        }
    }

    @Test
    public void serverEditorCustomLaunchSettingsChangeAndStart()
    {
        final String serverName = "Liferay 7-custom-launch-settings-start";

        addLiferay7Server( serverName );

        final String serverStoppedLabel = serverName + "  [Stopped]";

        serversView.getServers().getTreeItem( serverStoppedLabel ).doubleClick();

        final ServerEditor serverEditor = new ServerEditor( bot, serverName );
        final ServerEditor serverEditorWithLabelStopped = new ServerEditor( bot, serverStoppedLabel );

        try
        {
            serverEditor.getCustomLaunchSettings().click();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.getCustomLaunchSettings().click();
        }

        try
        {
            serverEditor.getUseDeveloperMode().select();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.getUseDeveloperMode().select();
        }

        try
        {
            serverEditor.save();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.save();
        }

        try
        {
            serverEditor.close();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.close();
        }

        serversView.getServers().getTreeItem( serverStoppedLabel ).select();

        serversView.getStart().click();

        sleep( 100000 );

        final String serverStartedLabel = serverName + "  [Started]";

        serversView.getServers().getTreeItem( serverStartedLabel ).select();

        serversView.getStop().click();

        sleep( 20000 );

        serversView.getServers().getTreeItem( serverStoppedLabel ).doubleClick();

        try
        {
            serverEditor.getDefaultLaunchSettings().click();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.getDefaultLaunchSettings().click();
        }

        try
        {
            serverEditor.save();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.save();
        }

        try
        {
            serverEditor.close();
        }
        catch( Exception e )
        {
            serverEditorWithLabelStopped.close();
        }
    }

}
