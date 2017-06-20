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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.liferay.ide.swtbot.server.ui.tests.page.DeleteRuntimeConfirmPO;
import com.liferay.ide.swtbot.server.ui.tests.page.DeleteRuntimePO;
import com.liferay.ide.swtbot.server.ui.tests.page.NewServerPO;
import com.liferay.ide.swtbot.server.ui.tests.page.NewServerRuntimeEnvPO;
import com.liferay.ide.swtbot.server.ui.tests.page.ServerTreePO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Vicky Wang
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class NewServerRuntimeWizardTests extends SWTBotBase implements ServerRuntimeWizard
{
    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    NewServerPO newServerPage = new NewServerPO( bot );

    NewServerRuntimeEnvPO setRuntimePage = new NewServerRuntimeEnvPO( bot );

    TreePO serverTree = new TreePO( bot, 1 );

    ServerTreePO clickServer = new ServerTreePO( bot );

    @BeforeClass
    public static void prepareServer() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );
        
        unzipServer();

        copyFileToStartServer();
    }

    @After
    public void closeWizard()
    {
        eclipse.closeShell( TITLE_NEW_SERVER );
    }

    public void deleteRuntime()
    {
        eclipse.getPreferencesMenu().click();

        TreePO preferencesTree = new TreePO( bot );

        preferencesTree.expandNode( "Server", "Runtime Environments" ).select();
        sleep();

        DeleteRuntimePO deleteRuntime = new DeleteRuntimePO( bot );

        if( deleteRuntime.getServerRuntimeEnvironments().containsItem( NODE_LIFERAY_7X ) )
        {
            deleteRuntime.getServerRuntimeEnvironments().click( NODE_LIFERAY_7X );
            deleteRuntime.getRemove().click();

            DeleteRuntimeConfirmPO confirmDelete = new DeleteRuntimeConfirmPO( bot );

            confirmDelete.confirm();
        }

        deleteRuntime.confirm();

        sleep();
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayServer().click();
        sleep();
    }

    @Test
    public void serverRuntimeTest() throws Exception
    {

        newServerPage.getServerTypeTree().selectTreeItem( NODE_LIFERAY_INC, NODE_LIFERAY_7X );
        newServerPage.next();

        setRuntimePage.getServerLocation().setText(
            getLiferayServerDir().toString() + "/" + getLiferayPluginServerName() );

        assertEquals( "Tomcat", setRuntimePage.getPortalBundleType().getText() );

        setRuntimePage.getServerLocation().setText( getLiferayServerDir().toOSString() );

        assertEquals( "Tomcat", setRuntimePage.getPortalBundleType().getText() );

        setRuntimePage.finish();

        String serversStopped = "Liferay 7.x at localhost  [Stopped]";
        String serversStarted = "Liferay 7.x at localhost  [Started]";
        String serversDebugging = "Liferay 7.x at localhost  [Debugging]";

        serverTree.getTreeItem( serversStopped ).select();

        clickServer.getStart().click();
        sleep( 120000 );
        assertTrue( checkServerConsoleMessage( SERVER_STARTUP_MESSAGE, "Liferay", 600000 ) );

        serverTree.setFocus();
        serverTree.getTreeItem( serversStarted ).select();

        clickServer.getStop().click();
        sleep( 60000 );
        assertTrue( checkServerConsoleMessage( SERVER_STOP_MESSAGE, "Liferay", 600000 ) );

        serverTree.setFocus();
        serverTree.getTreeItem( serversStopped ).select();

        clickServer.getDebug().click();
        sleep( 120000 );
        assertTrue( checkServerConsoleMessage( SERVER_STARTUP_MESSAGE, "Liferay", 600000 ) );

        serverTree.setFocus();
        serverTree.getTreeItem( serversDebugging ).select();

        clickServer.getStop().click();
        sleep( 60000 );
        assertTrue( checkServerConsoleMessage( SERVER_STOP_MESSAGE, "Liferay", 600000 ) );

        // Future: need to add jboss test
    }

    @Ignore
    @Test
    public void initialStateTest() throws Exception
    {
        if( serverTree.hasItems() )
        {
            deleteRuntime();
        }

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayServer().click();
        sleep();

        newServerPage.getServerTypeTree().selectTreeItem( NODE_LIFERAY_INC, NODE_LIFERAY_7X );
        sleep();

        assertEquals( TEXT_CHOOSE_THE_SERVER_TYPE, newServerPage.getValidationMessage() );
        assertEquals( TEXT_DEFAULT_SERVER_HOST_NAME, newServerPage.getServerHostName().getText() );
        assertEquals( TEXT_DEFAULT_SERVER_NAME, newServerPage.getServerName().getText() );
        assertTrue( newServerPage.nextButton().isEnabled() );

        newServerPage.getServerHostName().setText( "" );
        sleep();
        assertEquals( TEXT_SERVER_HOST_NAME_MUST_BE_SET, newServerPage.getValidationMessage() );
        assertFalse( newServerPage.nextButton().isEnabled() );
        newServerPage.getServerHostName().setText( "localhost" );

        newServerPage.getServerName().setText( "" );
        sleep();
        assertEquals( TEXT_CHOOSE_THE_SERVER_TYPE, newServerPage.getValidationMessage() );
        assertTrue( newServerPage.nextButton().isEnabled() );

        newServerPage.getResetDefault().click();
        sleep();
        assertEquals( TEXT_DEFAULT_SERVER_NAME, newServerPage.getServerName().getText() );

        newServerPage.next();
        sleep();

        assertEquals( TEXT_DEFAULT_SERVER_NAME, setRuntimePage.getName().getText() );
        assertEquals( TEXT_SPECIFY_PORTAL_BUNDLE_LOCATION, setRuntimePage.getValidationMessage() );

        setRuntimePage.getName().setText( "" );
        setRuntimePage.back();
        setRuntimePage.next();
        assertEquals( TEXT_ENTER_RUNTIME_ENVIRONMENT_NAME, setRuntimePage.getValidationMessage() );
        assertFalse( setRuntimePage.finishButton().isEnabled() );

        setRuntimePage.getName().setText( NODE_LIFERAY_7X );
        setRuntimePage.getServerLocation().setText( "test" );
        assertEquals( TEXT_PORTAL_BUNDLE_DOES_NOT_EXIST, setRuntimePage.getValidationMessage() );
        assertFalse( setRuntimePage.finishButton().isEnabled() );

        setRuntimePage.getServerLocation().setText( "" );
        assertEquals( TEXT_SPECIFY_PORTAL_BUNDLE_LOCATION, setRuntimePage.getValidationMessage() );
        setRuntimePage.cancel();
    }
}
