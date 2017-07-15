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

package com.liferay.ide.swtbot.project.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.SWTBotBase;
import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.LiferayProjectFromExistSourceWizard;
import com.liferay.ide.swtbot.liferay.ui.util.FileUtil;
import com.liferay.ide.swtbot.liferay.ui.util.ZipUtil;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class SDKProjectImportWizardTests extends SWTBotBase implements WizardUI
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    private static final String BUNDLE_ID = "com.liferay.ide.swtbot.project.ui.tests";

    private LiferayProjectFromExistSourceWizard wizard = new LiferayProjectFromExistSourceWizard( bot );

    Tree projectTreeItem = ide.getPackageExporerView().getProjectTree();

    TreeItem sdkTreeItem = ide.getPackageExporerView().getProjectTree().getTreeItem( getLiferayPluginsSdkName() );

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipServer();
        unzipPluginsSDK();
    }

    @After
    public void cleanUp()
    {
        try
        {
            Dialog shell = new Dialog( bot, TITLE_NEW_LIFERAY_PROJECT_EXIS_SOURCE );
            shell.closeIfOpen();

            ide.getPackageExporerView().deleteProjectExcludeNames(
                ( new String[] { getLiferayPluginsSdkName() } ), true );
        }
        catch( Exception e )
        {
        }
    }

    public void unzipSDKProject( String path, String projectName ) throws Exception
    {
        path = getLiferayPluginsSdkDir().append( path ).toOSString();

        final File projectZipFile = getProjectZip( BUNDLE_ID, projectName );

        ZipUtil.unzip( projectZipFile, new File( path ) );
    }

    public void openWizard()
    {
        ide.getCreateLiferayProjectToolbar().menuClick( MENU_NEW_LIFERAY_PROJECT_EXIS_SOURCE );
    }

    @Before
    public void shouldRunTests()
    {
        Assume.assumeTrue( runTest() || runAllTests() );
    }

    @Test
    public void testDefaults()
    {
        if( sdkTreeItem.isVisible() )
        {
            ide.getPackageExporerView().deleteResouceByName( getLiferayPluginsSdkName(), true );
        }

        openWizard();

        assertEquals( MESSAGE_DEFAULT, wizard.getValidationMsg() );

        assertTrue( wizard.getSdkDirectory().isEnabled() );
        assertTrue( wizard.getBrowseSdkDirectoryBtn().isEnabled() );
        assertTrue( wizard.getSdkVersion().isEnabled() );

        assertTrue( wizard.getSdkDirectory().isActive() );
        assertFalse( wizard.getBrowseSdkDirectoryBtn().isActive() );
        assertFalse( wizard.getSdkVersion().isActive() );

        assertTrue( wizard.getSelectAllBtn().isEnabled() );
        assertTrue( wizard.getDeselectAllBtn().isEnabled() );
        assertTrue( wizard.getRefreshBtn().isEnabled() );

        wizard.cancel();
    }

    @Test
    public void testAllPlugins() throws Exception
    {
        unzipSDKProject( "portlets", "Import-223-portlet" );
        unzipSDKProject( "hooks", "Import-223-hook" );
        unzipSDKProject( "themes", "Import-223-theme" );
        unzipSDKProject( "ext", "Import-223-ext" );
        unzipSDKProject( "layouttpl", "Import-223-layouttpl" );

        openWizard();

        if( wizard.getBrowseSdkDirectoryBtn().isEnabled() )
        {
            wizard.getSdkDirectory().setText( getLiferayPluginsSdkDir().toString() );
        }

        sleep( 1000 );
        assertEquals( MESSAGE_DEFAULT, wizard.getValidationMsg() );

        wizard.getSelectAllBtn().click();
        wizard.getDeselectAllBtn().click();
        wizard.getRefreshBtn().click();

        wizard.getSelectAllBtn().click();
        assertEquals( "7.0.0", wizard.getSdkVersion().getText() );
        assertTrue( wizard.finishBtn().isEnabled() );

        wizard.finish();

        assertTrue( sdkTreeItem.isVisible() );

        assertTrue( projectTreeItem.getTreeItem( "Import-223-portlet" ).isVisible() );
        assertTrue( projectTreeItem.getTreeItem( "Import-223-hook" ).isVisible() );
        assertTrue( projectTreeItem.getTreeItem( "Import-223-theme" ).isVisible() );
        assertTrue( projectTreeItem.getTreeItem( "Import-223-ext" ).isVisible() );
        assertTrue( projectTreeItem.getTreeItem( "Import-223-layouttpl" ).isVisible() );
    }

    @Test
    public void testSdkDirValidation() throws Exception
    {
        openWizard();

        if( !wizard.getBrowseSdkDirectoryBtn().isEnabled() )
        {
            wizard.cancel();

            ide.getPackageExporerView().deleteResouceByName( getLiferayPluginsSdkName(), true );

            openWizard();
        }

        unzipPluginsSDK();

        wizard.getSdkDirectory().setText( "AAA" );
        sleep( 1000 );
        assertEquals( " \"AAA\" is not an absolute path.", wizard.getValidationMsg() );
        assertFalse( wizard.finishBtn().isEnabled() );

        wizard.getSdkDirectory().setText( "C:/" );
        sleep( 1000 );
        assertEquals( MESSAGE_INVALID_PROJECT_LOCATION, wizard.getValidationMsg() );
        assertFalse( wizard.finishBtn().isEnabled() );

        unzipSDKProject( "portlets", "Import-223-portlet" );

        wizard.getSdkDirectory().setText( getLiferayPluginsSdkDir().toString() );
        sleep( 1000 );
        assertEquals( MESSAGE_MUST_SPECIFY_ONE_PROJECT, wizard.getValidationMsg() );

        wizard.getSelectAllBtn().click();
        assertEquals( "7.0.0", wizard.getSdkVersion().getText() );
        assertTrue( wizard.finishBtn().isEnabled() );

        wizard.finish();

        TreeItem sdkTreeItem = ide.getPackageExporerView().getProjectTree().getTreeItem( getLiferayPluginsSdkName() );

        assertTrue( sdkTreeItem.isVisible() );

        assertTrue( projectTreeItem.getTreeItem( "Import-223-portlet" ).isVisible() );

        // import project from another SDK
        IPath sdk2Dir = getLiferayPluginsSdkDir().removeLastSegments( 1 ).append( "sdk2" );

        FileUtil.copyDirectiory( getLiferayPluginsSdkDir().toOSString(), sdk2Dir.toOSString() );

        openWizard();
        sleep( 1000 );

        assertFalse( wizard.getSdkDirectory().isEnabled() );
        assertFalse( wizard.getBrowseSdkDirectoryBtn().isEnabled() );
        assertFalse( wizard.getSdkVersion().isEnabled() );
        assertFalse( wizard.finishBtn().isEnabled() );

        wizard.cancel();

        FileUtil.deleteDir( sdk2Dir.toFile(), true );
    }
}
