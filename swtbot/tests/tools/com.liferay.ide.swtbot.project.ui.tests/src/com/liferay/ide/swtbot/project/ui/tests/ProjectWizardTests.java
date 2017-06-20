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

import com.liferay.ide.swtbot.project.ui.tests.page.CreateProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SelectPortletFrameworkPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SetSDKLocationPO;
import com.liferay.ide.swtbot.project.ui.tests.page.ThemeWizardPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.ShellPO;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ProjectWizardTests extends SWTBotBase implements ProjectWizard
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
        eclipse.closeShell( LABEL_NEW_LIFERAY_PLUGIN_PROJECT );
        eclipse.closeShell( LABEL_NEW_LIFERAY_PORTLET );
        // eclipse.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true
        // );
    }

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipServer();
        unzipPluginsSDK();
    }

    @Ignore
    @Test
    public void createExtProject()
    {
        CreateProjectWizardPO createProjectWizard =
            new CreateProjectWizardPO( bot, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_VALIDATION_MESSAGE );

        createProjectWizard.createSDKProject( "text", MENU_EXT );

        if( hasAddedProject )
        {
            assertEquals( MESAGE_SDK_NOT_SUPPORT, createProjectWizard.getValidationMessage() );
            createProjectWizard.cancel();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot, INDEX_SDK_SETTING_VALIDATION_MESSAGE );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );

            assertEquals( MESAGE_SDK_NOT_SUPPORT, setSDKLocation.getValidationMessage() );
            setSDKLocation.cancel();
        }
    }

    @Test
    public void createHookProject()
    {
        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot );

        createProjectWizard.createSDKProject( "text", MENU_HOOK );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createLayoutProject()
    {
        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot );

        createProjectWizard.createSDKProject( "text", MENU_LAYOUT_TEMPLATE );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createPortletProject()
    {
        String projectName = "testPortlet";

        CreateProjectWizardPO createProjectWizard =
            new CreateProjectWizardPO( bot, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_VALIDATION_MESSAGE );

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, createProjectWizard.getValidationMessage() );
        assertEquals( "", createProjectWizard.get_projectNameText().getText() );
        assertEquals( MENU_BUILD_TYPE_ANT, createProjectWizard.get_buildTypeComboBox().getText() );
        assertEquals( MENU_PORTLET, createProjectWizard.get_pluginTypeComboBox().getText() );

        String[] expectedPluginTypeItems = { MENU_EXT, MENU_HOOK, MENU_LAYOUT_TEMPLATE, MENU_PORTLET,
            MENU_SERVICE_BUILDER_PORTLET, MENU_THEME, MENU_WEB };

        for( String expectedPluginTypeItem : expectedPluginTypeItems )
        {
            assertTrue( isInAvailableLists( expectedPluginTypeItems, expectedPluginTypeItem ) );
        }

        assertTrue( createProjectWizard.backButton().isEnabled() );
        assertFalse( createProjectWizard.nextButton().isEnabled() );
        assertFalse( createProjectWizard.finishButton().isEnabled() );
        assertTrue( createProjectWizard.cancelButton().isEnabled() );

        createProjectWizard.createSDKProject( projectName, MENU_PORTLET, true, false );

        assertTrue( createProjectWizard.get_includeSimpleCodeCheckBox().isChecked() );
        assertFalse( createProjectWizard.get_launchNewPortletWizardCheck().isChecked() );

        createProjectWizard.next();

        SelectPortletFrameworkPO selectPortletFramwork = new SelectPortletFrameworkPO( bot );

        assertTrue( selectPortletFramwork.IsLiferayMVCRadioSelected() );
        assertFalse( selectPortletFramwork.IsJSFRadioSelected() );
        assertFalse( selectPortletFramwork.IsSpringMVCRadioSelected() );
        assertFalse( selectPortletFramwork.IsVaadinRadioSelected() );

        if( !hasAddedProject )
        {
            selectPortletFramwork.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );
        }

        createProjectWizard.finish();

        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();

        String fileName = "liferay-display.xml";

        projectTree.expandNode( projectName + "-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        TextEditorPO editor = eclipse.getTextEditor( fileName );

        assertTrue( editor.isActive() );

        assertContains( "sample", editor.getText() );

        // Regression test IDE-1226,project already exists
        eclipse.getFileMenu().clickMenu( "New", "Other..." );
        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Plugin Project" ).select();
        bot.button( "Next >" ).click();

        createProjectWizard.createSDKPortletProject( projectName );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, createProjectWizard.getValidationMessage() );

        createProjectWizard.createSDKPortletProject( projectName + "-portlet" );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, createProjectWizard.getValidationMessage() );

        createProjectWizard.cancel();

        // Regression test IDE-1976,project name with space test
        eclipse.getFileMenu().clickMenu( "New", "Other..." );
        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Plugin Project" ).select();
        bot.button( "Next >" ).click();
        createProjectWizard.createSDKPortletProject( "test with space" );

        assertContains( TEXT_CREATE_NEW_PROJECT_AS_LIFERAY_PLUGIN, createProjectWizard.getValidationMessage() );
        createProjectWizard.finish();
        sleep();

        assertTrue( projectTree.getTreeItem( "test with space-portlet" ).isVisible() );

        eclipse.getPackageExporerView().deleteResouceByName( projectName + "-portlet", true );
    }

    @Test
    public void createPortletProjectWithoutSampleAndLaunchNewPortletWizard()
    {
        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot );

        createProjectWizard.createSDKProject( "NoSampleTest", MENU_PORTLET, false, true );

        assertFalse( createProjectWizard.get_includeSimpleCodeCheckBox().isChecked() );
        assertTrue( createProjectWizard.get_launchNewPortletWizardCheck().isChecked() );

        createProjectWizard.next();

        SelectPortletFrameworkPO selectPortletFramwork = new SelectPortletFrameworkPO( bot );

        assertTrue( selectPortletFramwork.IsLiferayMVCRadioSelected() );

        if( !hasAddedProject )
        {
            selectPortletFramwork.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );
        }

        createProjectWizard.finish();

        ShellPO newPortletPage = new ShellPO( bot, "New Liferay Portlet" )
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
        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot );

        createProjectWizard.createSDKProject( "textsb", MENU_SERVICE_BUILDER_PORTLET, true );

        if( hasAddedProject )
        {
            createProjectWizard.finish();

            sleep();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createServiceBuilderPortletProjectWithoutSampleCode()
    {
        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot );

        createProjectWizard.createSDKProject( "textsbwithoutcode", MENU_SERVICE_BUILDER_PORTLET, false );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createThemeProject()
    {
        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot );

        ThemeWizardPO selectThemeOptions =
            new ThemeWizardPO( bot, INDEX_THEME_PARENT_AND_FRAMEWORK_VALIDATIONG_MESSAGE );

        String projectThemeName = "test";

        createProjectWizard.createSDKProject( projectThemeName, MENU_THEME );

        if( hasAddedProject )
        {
            // TO-DO
            // assertEquals( THEME_DONOT_SUPPORT_MESSAGE, setSDKLocation.getValidationMessage() );
            createProjectWizard.cancel();
        }
        else
        {
            createProjectWizard.next();

            assertEquals( THEME_DEFAULT_MESSAGE, selectThemeOptions.getValidationMessage() );

            selectThemeOptions.setParentFramework( MENU_THEME_PARENT_UNSTYLED, MENU_THEME_FRAMEWORK_JSP );
            assertEquals( THEME_WARNING_MESSAGE, selectThemeOptions.getValidationMessage() );

            selectThemeOptions.setParentFramework( MENU_THEME_PARENT_CLASSIC, MENU_THEME_FRAMEWORK_VELOCITY );
            assertEquals( THEME_DEFAULT_MESSAGE, selectThemeOptions.getValidationMessage() );

            selectThemeOptions.setParentFramework( MENU_THEME_PARENT_STYLED, MENU_THEME_FRAMEWORK_FREEMARKER );

            selectThemeOptions.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );

            // TO-DO
            // assertEquals( THEME_DONOT_SUPPORT_MESSAGE, setSDKLocation.getValidationMessage() );

            setSDKLocation.cancel();
        }
    }

    @Test
    public void createWebProject()
    {
        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot );

        createProjectWizard.createSDKProject( "text", MENU_WEB );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationPO setSDKLocation = new SetSDKLocationPO( bot );

            setSDKLocation.setSdkLocation( getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        hasAddedProject = addedProjects();

        eclipse.getFileMenu().clickMenu( "New", "Other..." );

        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Plugin Project" ).select();

        bot.button( "Next >" ).click();
    }

    @Test
    public void validationProjectName()
    {
        String invalidNameDoubleDash = "--";

        CreateProjectWizardPO createProjectWizard =
            new CreateProjectWizardPO( bot, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_VALIDATION_MESSAGE );

        createProjectWizard.createSDKPortletProject( invalidNameDoubleDash );

        sleep();
        assertEquals( TEXT_THE_PROJECT_NAME_INVALID, createProjectWizard.getValidationMessage() );

        createProjectWizard.cancel();
    }

    @After
    public void waitForCreate()
    {
        sleep( 5000 );
    }

}
