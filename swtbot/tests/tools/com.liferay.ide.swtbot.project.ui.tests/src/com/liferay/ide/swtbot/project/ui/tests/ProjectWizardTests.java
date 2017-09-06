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

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewSdkProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.SelectPortletFrameworkWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.SetSDKLocationWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.ThemeWizard;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Shell;
import com.liferay.ide.swtbot.ui.page.Tree;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ProjectWizardTests extends SwtbotBase
{

    @AfterClass
    public static void cleanAll()
    {
        ide.closeShell( NEW_LIFERAY_PLUGIN_PROJECT );
        ide.closeShell( NEW_LIFERAY_PORTLET );
    }

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        ide.getLiferayPerspective().activate();

        envAction.unzipServer();
        envAction.unzipPluginsSDK();
    }

    @Ignore
    @Test
    public void createExtProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "text", EXT );

        if( hasAddedProject )
        {
            assertEquals( SDK_NOT_SUPPORT, createProjectWizard.getValidationMsg() );
            createProjectWizard.cancel();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );

            assertEquals( SDK_NOT_SUPPORT, setSDKLocation.getValidationMsg() );
            setSDKLocation.cancel();
        }
    }

    @Test
    public void createHookProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "text", HOOK );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createLayoutProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "text", LAYOUT_TEMPLATE );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createPortletProject()
    {
        String projectName = "testPortlet";

        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        assertEquals( PLEASE_ENTER_A_PROJECT_NAME, createProjectWizard.getValidationMsg() );

        assertTrue( createProjectWizard.backBtn().isEnabled() );
        assertFalse( createProjectWizard.nextBtn().isEnabled() );
        assertFalse( createProjectWizard.finishBtn().isEnabled() );
        assertTrue( createProjectWizard.cancelBtn().isEnabled() );

        createProjectWizard.createSDKProject( projectName, PORTLET, true, false );

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

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );
        }

        createProjectWizard.finish();

        Tree projectTree = viewAction.getProjects();

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
        assertEquals( A_PROJECT_WITH_THAT_NAME_ALREADY_EXISTS, createProjectWizard.getValidationMsg() );

        createProjectWizard.createSDKPortletProject( projectName + "-portlet" );
        assertEquals( A_PROJECT_WITH_THAT_NAME_ALREADY_EXISTS, createProjectWizard.getValidationMsg() );

        createProjectWizard.cancel();

        // Regression test IDE-1976,project name with space test
        ide.getFileMenu().clickMenu( "New", "Other..." );
        bot.tree().getTreeItem( "Liferay" ).expand();
        bot.tree().getTreeItem( "Liferay" ).getNode( "Liferay Plugin Project" ).select();
        bot.button( "Next >" ).click();
        createProjectWizard.createSDKPortletProject( "test with space" );

        assertContains( CREATE_NEW_PROJECT_AS_LIFERAY_PLUGIN, createProjectWizard.getValidationMsg() );
        createProjectWizard.finish();
        sleep();

        assertTrue( projectTree.getTreeItem( "test with space-portlet" ).isVisible() );

        viewAction.deleteProject( projectName + "-portlet" );
    }

    @Test
    public void createPortletProjectWithoutSampleAndLaunchNewPortletWizard()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "NoSampleTest", PORTLET, false, true );

        createProjectWizard.next();

        SelectPortletFrameworkWizard selectPortletFramwork = new SelectPortletFrameworkWizard( bot );

        assertTrue( selectPortletFramwork.getLiferayMvc().isSelected() );

        if( !hasAddedProject )
        {
            selectPortletFramwork.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );
        }

        createProjectWizard.finish();

        Shell newPortletPage = new Shell( bot, "New Liferay Portlet" )
        {
        };

        newPortletPage.waitForPageToOpen();

        sleep();
        assertContains( NEW_LIFERAY_PORTLET, newPortletPage.getTitle() );

        newPortletPage.closeIfOpen();
    }

    @Test
    public void createServiceBuilderPortletProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "textsb", SERVICE_BUILDER_PORTLET, true );

        if( hasAddedProject )
        {
            createProjectWizard.finish();

            sleep();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createServiceBuilderPortletProjectWithoutSampleCode()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "textsbwithoutcode", SERVICE_BUILDER_PORTLET, false );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Test
    public void createThemeProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        ThemeWizard selectThemeOptions = new ThemeWizard( bot );

        String projectThemeName = "test";

        createProjectWizard.createSDKProject( projectThemeName, THEME );

        if( hasAddedProject )
        {
            // TO-DO
            // assertEquals( THEME_DONOT_SUPPORT_MESSAGE, setSDKLocation.getValidationMsg() );
            createProjectWizard.cancel();
        }
        else
        {
            createProjectWizard.next();

            assertEquals( SELECT_OPTIONS_FOR_CREATING_NEW_THEME_PROJECT, selectThemeOptions.getValidationMsg() );

            selectThemeOptions.setParentFramework( UNSTYLED, JSP );
            assertEquals( FOR_ADVANCED_THEME_DEVELOPERS_ONLY, selectThemeOptions.getValidationMsg() );

            selectThemeOptions.setParentFramework( CLASSIC, VELOCITY );
            assertEquals( SELECT_OPTIONS_FOR_CREATING_NEW_THEME_PROJECT, selectThemeOptions.getValidationMsg() );

            selectThemeOptions.setParentFramework( STYLED, FREEMARKER );

            selectThemeOptions.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );

            // TO-DO
            // assertEquals( THEME_DONOT_SUPPORT_MESSAGE, setSDKLocation.getValidationMsg() );

            setSDKLocation.cancel();
        }
    }

    @Test
    public void createWebProject()
    {
        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( "text", WEB );

        if( hasAddedProject )
        {
            createProjectWizard.finish();
        }
        else
        {
            createProjectWizard.next();

            SetSDKLocationWizard setSDKLocation = new SetSDKLocationWizard( bot );

            setSDKLocation.getSdkLocation().setText( envAction.getLiferayPluginsSdkDir().toString() );

            setSDKLocation.finish();
        }
    }

    @Before
    public void openWizard()
    {
        hasAddedProject = addedProjects();

        ide.getFileMenu().clickMenu( NEW, "Other..." );

        bot.tree().getTreeItem( LIFERAY ).expand();
        bot.tree().getTreeItem( LIFERAY ).getNode( "Liferay Plugin Project" ).select();

        bot.button( "Next >" ).click();
    }

    @Test
    public void validationProjectName()
    {
        String invalidNameDoubleDash = "--";

        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKPortletProject( invalidNameDoubleDash );

        sleep();
        assertEquals( THE_PROJECT_NAME_IS_INVALID, createProjectWizard.getValidationMsg() );

        createProjectWizard.cancel();
    }

    @After
    public void waitForCreate()
    {
        sleep( 5000 );
    }

}
