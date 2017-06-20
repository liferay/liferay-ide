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
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.liferay.ide.swtbot.project.ui.tests.page.LiferayProjectFromExistSourceWizardPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;
import com.liferay.ide.swtbot.ui.tests.util.FileUtil;
import com.liferay.ide.swtbot.ui.tests.util.ZipUtil;

/**
 * @author Li Lu
 * @author Ying Xu
 */

@RunWith( SWTBotJunit4ClassRunner.class )
public class SDKProjectImportWizardTests extends SWTBotBase implements LiferayProjectFromExistSourceWizard
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

    private LiferayProjectFromExistSourceWizardPO _wizard = new LiferayProjectFromExistSourceWizardPO( bot );

    TreePO projectTreeItem = eclipse.getPackageExporerView().getProjectTree();

    TreeItemPO sdkTreeItem = eclipse.getPackageExporerView().getProjectTree().getTreeItem( getLiferayPluginsSdkName() );

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
            DialogPO shell = new DialogPO( bot, TITLE_NEW_LIFERAY_PROJECT_EXIS_SOURCE );
            shell.closeIfOpen();

            eclipse.getPackageExporerView().deleteProjectExcludeNames(
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
        eclipse.getCreateLiferayProjectToolbar().menuClick( MENU_NEW_LIFERAY_PROJECT_EXIS_SOURCE );
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
            eclipse.getPackageExporerView().deleteResouceByName( getLiferayPluginsSdkName(), true );
        }

        openWizard();

        assertEquals( MESSAGE_DEFAULT, _wizard.getValidationMessage() );

        assertTrue( _wizard.getSdkDirectoryText().isEnabled() );
        assertTrue( _wizard.getBrowseSdkDirectory().isEnabled() );
        assertTrue( _wizard.getSdkVersionText().isEnabled() );

        assertTrue( _wizard.getSdkDirectoryText().isActive() );
        assertFalse( _wizard.getBrowseSdkDirectory().isActive() );
        assertFalse( _wizard.getSdkVersionText().isActive() );

        assertTrue( _wizard.getSelectAllButton().isEnabled() );
        assertTrue( _wizard.getDeselectAllButton().isEnabled() );
        assertTrue( _wizard.getRefreshButton().isEnabled() );

        _wizard.cancel();
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

        if( _wizard.getBrowseSdkDirectory().isEnabled() )
        {
            _wizard.getSdkDirectoryText().setText( getLiferayPluginsSdkDir().toString() );
        }

        sleep( 1000 );
        assertEquals( MESSAGE_DEFAULT, _wizard.getValidationMessage() );

        _wizard.getSelectAllButton().click();
        _wizard.getDeselectAllButton().click();
        _wizard.getRefreshButton().click();

        _wizard.getSelectAllButton().click();
        assertEquals( "7.0.0", _wizard.getSdkVersionText().getText() );
        assertTrue( _wizard.finishButton().isEnabled() );

        _wizard.finish();

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

        if( !_wizard.getBrowseSdkDirectory().isEnabled() )
        {
            _wizard.cancel();

            eclipse.getPackageExporerView().deleteResouceByName( getLiferayPluginsSdkName(), true );

            openWizard();
        }

        unzipPluginsSDK();

        _wizard.getSdkDirectoryText().setText( "AAA" );
        sleep( 1000 );
        assertEquals( " \"AAA\" is not an absolute path.", _wizard.getValidationMessage() );
        assertFalse( _wizard.finishButton().isEnabled() );

        _wizard.getSdkDirectoryText().setText( "C:/" );
        sleep( 1000 );
        assertEquals( MESSAGE_INVALID_PROJECT_LOCATION, _wizard.getValidationMessage() );
        assertFalse( _wizard.finishButton().isEnabled() );

        unzipSDKProject( "portlets", "Import-223-portlet" );

        _wizard.getSdkDirectoryText().setText( getLiferayPluginsSdkDir().toString() );
        sleep( 1000 );
        assertEquals( MESSAGE_MUST_SPECIFY_ONE_PROJECT, _wizard.getValidationMessage() );

        _wizard.getSelectAllButton().click();
        assertEquals( "7.0.0", _wizard.getSdkVersionText().getText() );
        assertTrue( _wizard.finishButton().isEnabled() );

        _wizard.finish();

        TreeItemPO sdkTreeItem =
            eclipse.getPackageExporerView().getProjectTree().getTreeItem( getLiferayPluginsSdkName() );

        assertTrue( sdkTreeItem.isVisible() );

        assertTrue( projectTreeItem.getTreeItem( "Import-223-portlet" ).isVisible() );

        // import project from another SDK
        IPath sdk2Dir = getLiferayPluginsSdkDir().removeLastSegments( 1 ).append( "sdk2" );

        FileUtil.copyDirectiory( getLiferayPluginsSdkDir().toOSString(), sdk2Dir.toOSString() );

        openWizard();
        sleep( 1000 );

        assertFalse( _wizard.getSdkDirectoryText().isEnabled() );
        assertFalse( _wizard.getBrowseSdkDirectory().isEnabled() );
        assertFalse( _wizard.getSdkVersionText().isEnabled() );
        assertFalse( _wizard.finishButton().isEnabled() );

        _wizard.cancel();

        FileUtil.deleteDir( sdk2Dir.toFile(), true );
    }
}
