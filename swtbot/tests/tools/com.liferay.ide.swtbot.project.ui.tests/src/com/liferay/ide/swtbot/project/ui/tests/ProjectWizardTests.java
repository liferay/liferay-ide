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

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.liferay.ui.SWTBotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewSdkProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.SelectPortletFrameworkWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.SetSDKLocationWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ThemeWizard;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Shell;
import com.liferay.ide.swtbot.ui.page.Tree;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ProjectWizardTests extends SWTBotBase implements WizardUI
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @AfterClass
    public static void cleanAll()
    {
        ide.closeShell( LABEL_NEW_LIFERAY_PLUGIN_PROJECT );
        ide.closeShell( LABEL_NEW_LIFERAY_PORTLET );
        // ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true
        // );
    }

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        ide.getLiferayPerspective().activate();

        unzipServer();
        unzipPluginsSDK();
    }

    @Ignore
    @Test
    public void createExtProject()
    {
        NewSdkProjectWizard createProjectWizard =
            new NewSdkProjectWizard( bot, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_VALIDATION_MESSAGE );

        createProjectWizard.createSDKProject( "text", MENU_EXT );

        if( hasAddedProject )
        {
            assertEquals( MESAGE_SDK_NOT_SUPPORT, createProjectWizard.getValidationMsg() );
            createProjectWizard.cancel();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot, INDEX_SDK_SETTING_VALIDATION_MESSAGE );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );

            assertEquals( MESAGE_SDK_NOT_SUPPORT, setSDKLocation.getValidationMsg() );
            setSDKLocation.cancel();
        }
    }

    @Test
    public void createHookProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "text", MENU_HOOK );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createLayoutProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "text", MENU_LAYOUT_TEMPLATE );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createPortletProject()
    {
        String projectName = "testPortlet";

        NewSdkProjectWizard createProjectWizard =
            new NewSdkProjectWizard( bot, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_VALIDATION_MESSAGE );

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, createProjectWizard.getValidationMsg() );

        String[] expectedPluginTypeItems = { MENU_EXT, MENU_HOOK, MENU_LAYOUT_TEMPLATE, MENU_PORTLET,
            MENU_SERVICE_BUILDER_PORTLET, MENU_THEME, MENU_WEB };

        for( String expectedPluginTypeItem : expectedPluginTypeItems )
        {
            assertTrue( isInAvailableLists( expectedPluginTypeItems, expectedPluginTypeItem ) );
        }

        assertTrue( createProjectWizard.backBtn().isEnabled() );
        assertFalse( createProjectWizard.nextBtn().isEnabled() );
        assertFalse( createProjectWizard.finishBtn().isEnabled() );
        assertTrue( createProjectWizard.cancelBtn().isEnabled() );

        createProjectWizard.createSDKProject( projectName, MENU_PORTLET, true, false );

        createProjectWizard.next();

        SelectPortletFrameworkWizard selectPortletFramwork = new SelectPortletFrameworkWizard( bot );

        assertTrue( selectPortletFramwork.getLiferayMvc().isSelected() );
        assertFalse( selectPortletFramwork.getJsf().isSelected() );
        assertFalse( selectPortletFramwork.getSpringMvc().isSelected() );
        assertFalse( selectPortletFramwork.getVaadin().isSelected() );

        if( !hasAddedProject )
        {
            selectPortletFramwork.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );
        }

        createProjectWizard.finish();

        Tree projectTree = ide.showPackageExporerView().getProjectTree();

        String fileName = "liferay-display.xml";

        projectTree.expandNode( projectName + "-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        Editor editor = ide.getEditor( fileName );

        assertTrue( editor.isActive() );

        assertContains( "sample", editor.getText() );

        // Regression test IDE-1226,project already exists
        ide.getFileMenu().clickMenu( "New", "Other..." );
        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Plugin Project" ).select();
        bot.button( "Next >" ).click();

        createProjectWizard.createSDKPortletProject( projectName );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, createProjectWizard.getValidationMsg() );

        createProjectWizard.createSDKPortletProject( projectName + "-portlet" );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, createProjectWizard.getValidationMsg() );

        createProjectWizard.cancel();

        // Regression test IDE-1976,project name with space test
        ide.getFileMenu().clickMenu( "New", "Other..." );
        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Plugin Project" ).select();
        bot.button( "Next >" ).click();
        createProjectWizard.createSDKPortletProject( "test with space" );

        assertContains( TEXT_CREATE_NEW_PROJECT_AS_LIFERAY_PLUGIN, createProjectWizard.getValidationMsg() );
        createProjectWizard.finish();
        sleep();

        assertTrue( projectTree.getTreeItem( "test with space-portlet" ).isVisible() );

        ide.getPackageExporerView().deleteResouceByName( projectName + "-portlet", true );
    }

    @Test
    public void createPortletProjectWithoutSampleAndLaunchNewPortletWizard()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "NoSampleTest", MENU_PORTLET, false, true );

        createProjectWizard.next();

        SelectPortletFrameworkWizard selectPortletFramwork = new SelectPortletFrameworkWizard( bot );

        assertTrue( selectPortletFramwork.getLiferayMvc().isSelected() );

        if( !hasAddedProject )
        {
            selectPortletFramwork.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );
        }

        createProjectWizard.finish();

        Shell newPortletPage = new Shell( bot, "New Liferay Portlet" )
        {
        };

        newPortletPage.waitForPageToOpen();

        sleep();
        assertContains( LABEL_NEW_LIFERAY_PORTLET, newPortletPage.getTitle() );

        newPortletPage.closeIfOpen();
    }

    @Test
    public void createServiceBuilderPortletProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "textsb", MENU_SERVICE_BUILDER_PORTLET, true );

        if( hasAddedProject )
        {
            createProjectWizard.finish();

            sleep();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createServiceBuilderPortletProjectWithoutSampleCode()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "textsbwithoutcode", MENU_SERVICE_BUILDER_PORTLET, false );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createThemeProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        ThemeWizard selectThemeOptions = new ThemeWizard( bot, INDEX_THEME_PARENT_AND_FRAMEWORK_VALIDATIONG_MESSAGE );

        String projectThemeName = "test";

        createProjectWizard.createSDKProject( projectThemeName, MENU_THEME );

        if( hasAddedProject )
        {
            // TO-DO
            // assertEquals( THEME_DONOT_SUPPORT_MESSAGE, setSDKLocation.getValidationMsg() );
            createProjectWizard.cancel();
        }
        else
        {
            createProjectWizard.next();

            assertEquals( THEME_DEFAULT_MESSAGE, selectThemeOptions.getValidationMsg() );

            selectThemeOptions.setParentFramework( MENU_THEME_PARENT_UNSTYLED, MENU_THEME_FRAMEWORK_JSP );
            assertEquals( THEME_WARNING_MESSAGE, selectThemeOptions.getValidationMsg() );

            selectThemeOptions.setParentFramework( MENU_THEME_PARENT_CLASSIC, MENU_THEME_FRAMEWORK_VELOCITY );
            assertEquals( THEME_DEFAULT_MESSAGE, selectThemeOptions.getValidationMsg() );

            selectThemeOptions.setParentFramework( MENU_THEME_PARENT_STYLED, MENU_THEME_FRAMEWORK_FREEMARKER );

            selectThemeOptions.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );

            // TO-DO
            // assertEquals( THEME_DONOT_SUPPORT_MESSAGE, setSDKLocation.getValidationMsg() );

            setSDKLocation.cancel();
        }
    }

    @Test
    public void createWebProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "text", MENU_WEB );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        hasAddedProject = addedProjects();

        ide.getFileMenu().clickMenu( "New", "Other..." );

        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Plugin Project" ).select();

        bot.button( "Next >" ).click();
    }

    @Test
    public void validationProjectName()
    {
        String invalidNameDoubleDash = "--";

        NewSdkProjectWizard createProjectWizard =
            new NewSdkProjectWizard( bot, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_VALIDATION_MESSAGE );

        createProjectWizard.createSDKPortletProject( invalidNameDoubleDash );

        sleep();
        assertEquals( TEXT_THE_PROJECT_NAME_INVALID, createProjectWizard.getValidationMsg() );

        createProjectWizard.cancel();
    }

    @After
    public void waitForCreate()
    {
        sleep( 5000 );
    }

}
