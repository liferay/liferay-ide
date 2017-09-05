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

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.LiferayProjectFromExistSourceWizard;
import com.liferay.ide.swtbot.liferay.ui.util.FileUtil;
import com.liferay.ide.swtbot.liferay.ui.util.ZipUtil;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class SDKProjectImportWizardTests extends SwtbotBase
{

    private static final String BUNDLE_ID = "com.liferay.ide.swtbot.project.ui.tests";

    private LiferayProjectFromExistSourceWizard wizard = new LiferayProjectFromExistSourceWizard( bot );

    Tree projectTreeItem = viewAction.getProjects();

    TreeItem sdkTreeItem = viewAction.getProjects().getTreeItem( envAction.getLiferayPluginsSdkName() );

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        envAction.unzipServer();
        envAction.unzipPluginsSDK();
    }

    @After
    public void cleanUp()
    {
        viewAction.deleteProjectsExcludeNames( envAction.getLiferayPluginsSdkName() );
    }

    public void unzipSDKProject( String path, String projectName ) throws Exception
    {
        path = envAction.getLiferayPluginsSdkDir().append( path ).toOSString();

        final File projectZipFile = getProjectZip( BUNDLE_ID, projectName );

        ZipUtil.unzip( projectZipFile, new File( path ) );
    }

    @Test
    public void testAllPlugins() throws Exception
    {
        unzipSDKProject( "portlets", "Import-223-portlet" );
        unzipSDKProject( "hooks", "Import-223-hook" );
        unzipSDKProject( "themes", "Import-223-theme" );
        unzipSDKProject( "ext", "Import-223-ext" );
        unzipSDKProject( "layouttpl", "Import-223-layouttpl" );

        wizardAction.openNewLiferayPluginProjectsFromExistingSourceWizard();

        wizard.getSdkDirectory().setText( envAction.getLiferayPluginsSdkDir().toString() );

        wizard.getSelectAllBtn().click();
        wizard.getDeselectAllBtn().click();
        wizard.getRefreshBtn().click();

        wizard.getSelectAllBtn().click();

        assertEquals( "7.0.0", wizard.getSdkVersion().getText() );

        wizardAction.finish();

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
        wizardAction.openNewLiferayPluginProjectsFromExistingSourceWizard();

        if( !wizard.getBrowseSdkDirectoryBtn().isEnabled() )
        {
            wizard.cancel();

            viewAction.deleteProjectsExcludeNames( envAction.getLiferayPluginsSdkName() );

            wizardAction.openNewLiferayPluginProjectsFromExistingSourceWizard();
        }

        envAction.unzipPluginsSDK();

        wizard.getSdkDirectory().setText( "AAA" );

        assertEquals( " \"AAA\" is not an absolute path.", wizard.getValidationMsg() );
        assertFalse( wizard.finishBtn().isEnabled() );

        wizard.getSdkDirectory().setText( "C:/" );

        assertEquals( SDK_DOES_NOT_EXIST, wizard.getValidationMsg() );
        assertFalse( wizard.finishBtn().isEnabled() );

        unzipSDKProject( "portlets", "Import-223-portlet" );

        wizard.getSdkDirectory().setText( envAction.getLiferayPluginsSdkDir().toString() );

        assertEquals( AT_LEAST_ONE_PROJECT_MUST_BE_SPECIFY, wizard.getValidationMsg() );

        wizard.getSelectAllBtn().click();
        assertEquals( "7.0.0", wizard.getSdkVersion().getText() );
        assertTrue( wizard.finishBtn().isEnabled() );

        wizard.finish();

        TreeItem sdkTreeItem = viewAction.getProjects().getTreeItem( envAction.getLiferayPluginsSdkName() );

        assertTrue( sdkTreeItem.isVisible() );

        assertTrue( projectTreeItem.getTreeItem( "Import-223-portlet" ).isVisible() );

        IPath sdk2Dir = envAction.getLiferayPluginsSdkDir().removeLastSegments( 1 ).append( "sdk2" );

        FileUtil.copyDirectiory( envAction.getLiferayPluginsSdkDir().toOSString(), sdk2Dir.toOSString() );

        wizardAction.openNewLiferayPluginProjectsFromExistingSourceWizard();

        assertFalse( wizard.getSdkDirectory().isEnabled() );
        assertFalse( wizard.getBrowseSdkDirectoryBtn().isEnabled() );
        assertFalse( wizard.getSdkVersion().isEnabled() );
        assertFalse( wizard.finishBtn().isEnabled() );

        wizard.cancel();

        FileUtil.deleteDir( sdk2Dir.toFile(), true );
    }

}
