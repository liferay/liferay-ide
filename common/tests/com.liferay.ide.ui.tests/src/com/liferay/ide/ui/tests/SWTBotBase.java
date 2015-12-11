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

package com.liferay.ide.ui.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.ui.LiferayUIPlugin;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
@RunWith( SWTBotJunit4ClassRunner.class )
public class SWTBotBase implements UIBase
{

    private final static String liferayBundlesDir = System.getProperty( "liferay.bundles.dir" );
    private static IPath liferayBundlesPath;

    public static SWTWorkbenchBot bot;

    public static ButtonBot buttonBot;
    public static CheckBoxBot checkBoxBot;
    public static ComboBoxBot comboBoxBot;
    public static EditorBot editorBot;
    public static LabelBot labelBot;
    public static RadioBot radioBot;
    public static ShellBot shellBot;
    public static TextBot textBot;
    public static ToolbarBot toolbarBot;
    public static TreeBot treeBot;
    public static ViewBot viewBot;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        bot = new SWTWorkbenchBot();

        buttonBot = new ButtonBot( bot );
        textBot = new TextBot( bot );
        toolbarBot = new ToolbarBot( bot );
        comboBoxBot = new ComboBoxBot( bot );
        shellBot = new ShellBot( bot );
        treeBot = new TreeBot( bot );
        viewBot = new ViewBot( bot );
        checkBoxBot = new CheckBoxBot( bot );
        editorBot = new EditorBot( bot );
        labelBot = new LabelBot( bot );
        radioBot = new RadioBot( bot );

        viewBot.close( VIEW_WELCOME );
        bot.perspectiveByLabel( "Liferay" ).activate();

        SWTBotPreferences.TIMEOUT = 30000;

        setupPluginsSDK();
    }

    private static void setupPluginsSDK() throws IOException
    {
        FileUtil.deleteDir( getLiferayPluginsSdkDir().toFile(), true );
        final File liferayPluginsSdkZipFile = getLiferayPluginsSDKZip().toFile();

        assertEquals(
            "Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
            liferayPluginsSdkZipFile.exists() );

        final File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

        liferayPluginsSdkDirFile.mkdirs();

        final String liferayPluginsSdkZipFolder = getLiferayPluginsSdkZipFolder();

        if( CoreUtil.isNullOrEmpty( liferayPluginsSdkZipFolder ) )
        {
            ZipUtil.unzip( liferayPluginsSdkZipFile, liferayPluginsSdkDirFile );
        }
        else
        {
            ZipUtil.unzip(
                liferayPluginsSdkZipFile, liferayPluginsSdkZipFolder, liferayPluginsSdkDirFile,
                new NullProgressMonitor() );
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        final File ivyCacheDir = new File( liferayPluginsSdkDirFile, ".ivy" );

        if( !ivyCacheDir.exists() )
        {
            // setup ivy cache

            final File ivyCacheZipFile = getIvyCacheZip().toFile();

            assertEquals(
                "Expected ivy-cache.zip to be here: " + ivyCacheZipFile.getAbsolutePath(), true,
                ivyCacheZipFile.exists() );

            ZipUtil.unzip( ivyCacheZipFile, liferayPluginsSdkDirFile );
        }

        assertEquals( "Expected .ivy folder to be here: " + ivyCacheDir.getAbsolutePath(), true, ivyCacheDir.exists() );
    }

    protected void sleep()
    {
        sleep( 5000 );
    }

    protected void sleep( long millis )
    {
        bot.sleep( millis );
    }

    protected static IPath getLiferayBundlesPath()
    {
        if( liferayBundlesPath == null )
        {
            liferayBundlesPath = new Path( liferayBundlesDir );
        }

        return liferayBundlesPath;
    }

    protected static IPath getIvyCacheZip()
    {
        return getLiferayBundlesPath().append( "ivy-cache.zip" );
    }

    protected static IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2.zip" );
    }

    protected static String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2/";
    }

    protected static String getLiferayPluginsSdkName()
    {
        return "liferay-plugins-sdk-6.2";
    }

    protected static IPath getLiferayPluginsSdkDir()
    {
        return LiferayUIPlugin.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2" );
    }

}
