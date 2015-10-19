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

package com.liferay.ide.project.ui.tests;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.widgetIsEnabled;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Li Lu
 */
@RunWith( SWTBotJunit4ClassRunner.class )
public class SDKProjectImportWizardTests extends SWTBotTestBase
{

    private static IPath sdkLocation;
    
    public static void openWizard()
    {
        bot.menu( "File" ).menu( "Import..." ).click();

        bot.tree().getTreeItem( "Liferay" ).select();
        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Project From Existing Source" ).select();

        bot.button( "Next >" ).click();

        ICondition condition = shellIsActive( "Import Project" );
        bot.waitUntil( condition, 5000 );
    }
    
    @After
    public void cleanUp()
    {
        try
        {
            bot.button( "Cancel" ).click();
            bot.sleep( 1500 );
        }
        catch( Exception e )
        {
        }
        
		try
		{
			deleteAllWorkspaceProjects();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	protected String getBundleId()
    {
        return BUNDLE_ID;
    }

    public void importSDKProject( String path, String projectName ) throws Exception
    {
        if( sdkLocation == null )
        {
            sdkLocation = SDKUtil.getWorkspaceSDK().getLocation();
        }

        path = sdkLocation.append( path ).toOSString();

        final File projectZipFile = getProjectZip( BUNDLE_ID, projectName );

        ZipUtil.unzip( projectZipFile, new File( path ) );

        openWizard();

        bot.text( 0 ).setText( path + "/" + projectName );

        bot.sleep( 500 );

        String FILE_SEPARATOR = System.getProperty( "file.separator" );
        String[] sdkPath = path.split( "\\" + FILE_SEPARATOR );

        String projectFolder = sdkPath[sdkPath.length - 1];

        String pluginType = projectFolder.endsWith( "s" )
            ? projectFolder.substring( 0, projectFolder.lastIndexOf( 's' ) ) : projectFolder;

        assertTrue( bot.text( pluginType ).isVisible() );
        assertEquals( pluginType, bot.text( pluginType ).getText() );

        assertEquals( pluginType, bot.text( 1 ).getText() );

        assertEquals( "6.2.0", bot.text( 2 ).getText() );

        assertTrue( bot.button( "Finish" ).isEnabled() );
        bot.button( "Finish" ).click();

        ICondition condition = widgetIsEnabled( bot.tree().getTreeItem( projectName ) );
        bot.waitUntil( condition, 5000 );
    }

    @Test
    public void testDefaults()
    {
        if( shouldSkipBundleTests() )return;

        openWizard();

        assertEquals( "Please select an existing project", bot.text( 3 ).getText() );

        assertTrue( bot.toolbarButtonWithTooltip( "Browse" ).isEnabled() );
        assertTrue( bot.text().isEnabled() );

        assertTrue( bot.text( 0 ).isActive() );
        assertTrue( bot.text( 0 ).isVisible() );

        assertFalse( bot.text( 1 ).isActive() );
        assertTrue( bot.text( 1 ).isVisible() );

        assertFalse( bot.text( 2 ).isActive() );
        assertTrue( bot.text( 2 ).isVisible() );

        assertTrue( bot.button( "Finish" ).isEnabled() );

        bot.button( "Cancel" ).click();
    }

    @Test
    public void testPluginType() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        importSDKProject( "hooks", "Import-223-hook" );
        importSDKProject( "themes", "Import-223-theme" );
        importSDKProject( "ext", "Import-223-ext" );
        importSDKProject( "layouttpl", "Import-223-layouttpl" );
    }

    @Test
    public void testValidationNoSDK() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        // test import project when no sdk in wrokspace
        sdkLocation = SDKUtil.getWorkspaceSDKProject().getRawLocation();

        SDKUtil.getWorkspaceSDKProject().delete( false, false, null );

        importSDKProject( "portlet", "Import-223-portlet" );

        assertNotNull( SDKUtil.getWorkspaceSDK() );

        ICondition condition = widgetIsEnabled( bot.tree().getTreeItem( SDKUtil.getWorkspaceSDK().getName() ) );
        bot.waitUntil( condition, 5000 );

        openWizard();

        String projectPath = SDKUtil.getWorkspaceSDK().getLocation().append( "portlet/Import-223-portlet" ).toOSString();

        bot.text( 0 ).setText( projectPath );

        bot.sleep( 1000 );
        assertEquals( " Project name already exists.", bot.text( 3 ).getText() );
        assertFalse( bot.button( "Finish" ).isEnabled() );
    }

    @Test
    public void testValidationProjectLocation() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        // import project outside of SDK
        final File projectZipFile = getProjectZip( BUNDLE_ID, "Import-223-portlet" );

        SDK sdk = SDKUtil.getWorkspaceSDK();
        ZipUtil.unzip( projectZipFile, sdk.getLocation().removeLastSegments( 2 ).toFile() );

        String projectCopyDir = sdk.getLocation().removeLastSegments( 2 ).append( "Import-223-portlet" ).toOSString();

        openWizard();

        bot.text( 0 ).setText( projectCopyDir );

        bot.sleep( 1000 );

        assertTrue( bot.text( 3 ).getText().startsWith( " Could not determine SDK from project location" ) );
        assertFalse( bot.button( "Finish" ).isEnabled() );
    }

    @Test
    public void testValidationProjectPath() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        openWizard();

        bot.text( 0 ).setText( "AAA" );
        bot.sleep( 1000 );
        assertEquals( " \"AAA\" is not an absolute path.", bot.text( 3 ).getText() );
        assertFalse( bot.button( "Finish" ).isEnabled() );

        bot.text( 0 ).setText( "C:/" );
        bot.sleep( 1000 );
        assertEquals( " Invalid project location", bot.text( 3 ).getText() );
        assertFalse( bot.button( "Finish" ).isEnabled() );

        bot.text( 0 ).setText( "C:/AAA" );
        bot.sleep( 1000 );
        assertEquals( " Project isn't exist at \"" + "C:\\AAA" + "\"", bot.text( 3 ).getText() );
        assertFalse( bot.button( "Finish" ).isEnabled() );

    }
}
