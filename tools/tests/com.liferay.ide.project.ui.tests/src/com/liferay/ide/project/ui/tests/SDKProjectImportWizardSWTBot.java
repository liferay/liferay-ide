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
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.ZipUtil;

import java.io.File;

import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * @author Li Lu
 */
public class SDKProjectImportWizardSWTBot extends SWTBotTestBase
{

    public static void openWizard()
    {
        bot.menu( "File" ).setFocus();
        bot.menu( "File" ).click();
        bot.menu( "Import..." ).click();

        bot.tree().getTreeItem( "Liferay" ).select();
        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Project From Existing Source" ).select();

        bot.button( "Next >" ).click();

        ICondition condition = shellIsActive( "Import Project" );
        bot.waitUntil( condition, 5000 );
    }

    public void importSDKProject( String path, String projectName ) throws Exception
    {
        final File projectZipFile = getProjectZip( bundleId, projectName );

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

}
