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

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.project.ui.tests.page.CreateLiferayPortletWizardPageObject;
import com.liferay.ide.project.ui.tests.page.CreateProjectWizardPageObject;
import com.liferay.ide.project.ui.tests.page.ProjectTreePageObject;
import com.liferay.ide.project.ui.tests.page.SelectPortletFrameworkPageObject;
import com.liferay.ide.project.ui.tests.page.SetSDKLocationPageObject;
import com.liferay.ide.project.ui.tests.page.ThemeWizardPageObject;
import com.liferay.ide.project.ui.tests.swtbot.ProjectWizard;
import com.liferay.ide.ui.tests.SWTBotBase;
import com.liferay.ide.ui.tests.UITestsUtils;
import com.liferay.ide.ui.tests.WizardBase;
import com.liferay.ide.ui.tests.swtbot.page.EditorPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextEditorPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TreeItemPageObject;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 * @author Li Lu
 */
public class ProjectWizardTests extends SWTBotBase implements ProjectWizard, WizardBase
{

    public static boolean added = false;

    @AfterClass
    public static void cleanAll()
    {
        deleteALLWSProjects();
    }

    private boolean addedProjecs()
    {
        viewBot.show( VIEW_PACKAGE_EXPLORER );

        return treeBot.hasItems();
    }

    @After
    public void closeWizard()
    {
        try
        {
            CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );
            page1.cancel();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void createExtProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );
        page1.createSDKProject( "text", MENU_EXT );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<>( bot );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    public void createHookProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );
        page1.createSDKProject( "text", MENU_HOOK );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    public void createLayoutProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );

        page1.createSDKProject( "text", MENU_LAYOUT_TEMPLATE );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    public void createPortletProject()
    {
        String projectName = "testPortlet";

        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        page1.createSDKProject( projectName, MENU_PORTLET, true, false );

        assertTrue( page1.getIncludeSimpleCodeCheckBox().isChecked() );
        assertFalse( page1.getLaunchNewPortletWizardCheckBox().isChecked() );

        page1.next();

        SelectPortletFrameworkPageObject<SWTWorkbenchBot> page2 =
            new SelectPortletFrameworkPageObject<SWTWorkbenchBot>( bot );

        assertTrue( page2.getLiferayMVCRadio().isSelected() );
        assertTrue( page2.isVisibleProjectNameAndDisplayName() );

        if( !added )
        {
            page1.next();

            SetSDKLocationPageObject<SWTWorkbenchBot> page3 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot );

            page3.setSdkLocation( getLiferayPluginsSdkDir().toString() );
        }

        page1.finish();

        // check in console and package explorer
        assertTrue( UITestsUtils.checkConsoleMessage( "BUILD SUCCESSFUL", "Java" ) );

        treeBot.doubleClick( "liferay-display.xml", projectName + "-portlet", "docroot", "WEB-INF" );
        EditorPageObject editorPage = new EditorPageObject( bot, "liferay-display.xml" );
        assertTrue( editorPage.isActive() );

        TextEditorPageObject textEditorPage = new TextEditorPageObject( bot, "liferay-display.xml" );
        assertContains( "sample", textEditorPage.getText() );

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> page4 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE3 );

        page4.createSDKProject( projectName );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, page4.getValidationMessage() );

        page4.createSDKProject( projectName + "-portlet" );
        assertEquals( TEXT_PROJECT_ALREADY_EXISTS, page4.getValidationMessage() );

        page4.cancel();

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> page5 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE3 );

        page5.createSDKProject( projectName );
        assertContains( TEXT_PROJECT_ALREADY_EXISTS, page5.getValidationMessage() );

        page5.createSDKProject( projectName + "-portlet" );
        assertContains( TEXT_PROJECT_ALREADY_EXISTS, page5.getValidationMessage() );

        page5.cancel();

        deleteProjectInSdk( projectName + "-portlet" );
    }

    @Test
    public void createPortletProjectWithoutSampleAndLaunchNewPortletWizard()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );

        page1.createSDKProject( "NoSampleTest", MENU_PORTLET, false, true );

        assertFalse( page1.getIncludeSimpleCodeCheckBox().isChecked() );
        assertTrue( page1.getLaunchNewPortletWizardCheckBox().isChecked() );

        page1.next();

        SelectPortletFrameworkPageObject<SWTWorkbenchBot> page2 =
            new SelectPortletFrameworkPageObject<SWTWorkbenchBot>( bot );

        assertTrue( page2.getLiferayMVCRadio().isSelected() );
        assertFalse( page2.isVisibleProjectNameAndDisplayName() );

        if( !added )
        {
            page1.next();

            SetSDKLocationPageObject<SWTWorkbenchBot> page3 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot );

            page3.setSdkLocation( getLiferayPluginsSdkDir().toString() );
        }

        page1.finish();

        CreateLiferayPortletWizardPageObject<SWTWorkbenchBot> newPortletPage =
            new CreateLiferayPortletWizardPageObject<SWTWorkbenchBot>( bot, "New Liferay Portlet" );

        newPortletPage.waitForPageToOpen();

        assertEquals( TOOLTIP_NEW_LIFERAY_PORTLET, newPortletPage.getTitle() );

        newPortletPage.closeIfOpen();
    }

    @Test
    public void createServiceBuilderPortletProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );

        page1.createSDKProject( "textsb", MENU_SERVICE_BUILDER_PORTLET, true );

        if( added )
        {
            page1.finish();

            // sleep(5000);
            // assertTrue((treeUtil.expandNode( "textsb-portlet","docroot").getNode("view.jsp").isVisible()));
            // assertTrue((treeUtil.expandNode( "textsb-portlet","docroot","css").getNode("main.css").isVisible()));
            // assertTrue((treeUtil.expandNode( "textsb-portlet","docroot","js").getNode("main.js").isVisible()));
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();

            // sleep(5000);
            // assertTrue((treeUtil.expandNode( "textsb-portlet","docroot").getNode("view.jsp").isVisible()));
            // assertTrue((treeUtil.expandNode( "textsb-portlet","docroot","css").getNode("main.css").isVisible()));
            // assertTrue((treeUtil.expandNode( "textsb-portlet","docroot","js").getNode("main.js").isVisible()));
        }
    }

    @Test
    public void createServiceBuilderPortletProjectWithoutSampleCode()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );

        page1.createSDKProject( "textsbwithoutcode", MENU_SERVICE_BUILDER_PORTLET, false );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    public void createThemeProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );
        ThemeWizardPageObject<SWTWorkbenchBot> page2 = new ThemeWizardPageObject<SWTWorkbenchBot>( bot );

        String defaultMessage = "Select options for creating new theme project.";
        String warningMessage = " For advanced theme developers only.";

        String projectThemeName = "test";

        page1.createSDKProject( projectThemeName, MENU_THEME );

        page1.next();

        assertEquals( defaultMessage, page2.getValidationMessage() );

        page2.setParentFramework( MENU_THEME_PARENT_UNSTYLED, MENU_THEME_FRAMEWORK_JSP );
        assertEquals( warningMessage, page2.getValidationMessage() );

        page2.setParentFramework( MENU_THEME_PARENT_CLASSIC, MENU_THEME_FRAMEWORK_VELOCITY );
        assertEquals( defaultMessage, page2.getValidationMessage() );

        page2.setParentFramework( MENU_THEME_PARENT_STYLED, MENU_THEME_FRAMEWORK_FREEMARKER );

        if( added )
        {
            page2.finish();
        }
        else
        {
            page2.next();

            SetSDKLocationPageObject<SWTWorkbenchBot> page3 = getSetSDKLoactionPage();

            page3.finish();
        }

        sleep( 15000 );

        TreeItemPageObject<SWTWorkbenchBot> buildXml =
            new TreeItemPageObject<SWTWorkbenchBot>( bot, projectThemeName + "-theme", "build.xml" );
        buildXml.doubleClick();

        // assertTrue( editorUtil.isActive( "build.xml" ) );
        // assertContains( "_styled", textUtil.getStyledText() );
        // assertContains( "ftl", textUtil.getStyledText() );

        ProjectTreePageObject<SWTWorkbenchBot> project =
            new ProjectTreePageObject<SWTWorkbenchBot>( bot, projectThemeName + "-theme" );
        project.deleteProject();
    }

    @Test
    public void createWebProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE3 );

        page1.createSDKProject( "text", MENU_WEB );

        if( added )
        {
            sleep( 1500 );
            assertEquals( TEXT_WEB_SDK_62_ERRORR_MESSAGE, page1.getValidationMessage() );
            page1.cancel();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 =
                new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "", INDEX_VALIDATION_MESSAGE2 );

            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );

            sleep( 1500 );
            assertEquals( TEXT_WEB_SDK_62_ERRORR_MESSAGE, page2.getValidationMessage() );
            page2.cancel();
        }
    }

    private SetSDKLocationPageObject<SWTWorkbenchBot> getSetSDKLoactionPage()
    {
        SetSDKLocationPageObject<SWTWorkbenchBot> page = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot );
        page.setSdkLocation( getLiferayPluginsSdkDir().toString() );

        return page;
    }

    @Before
    public void openWizard()
    {
        added = addedProjecs();

        openWizard( MENU_NEW_LIFERAY_PLUGIN_PROJECT );
    }

    @Test
    public void validationProjectName()
    {
        String invalidNameDoubleDash = "--";
        // String invalidNameDoubleSlash = "//";
        // String invalidNameDot = ".";
        // String invalidNameStar = "*";

        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE3 );

        page1.createSDKProject( invalidNameDoubleDash );

        sleep( 1500 );
        assertEquals( " The project name is invalid.", page1.getValidationMessage() );

        page1.cancel();

        // textUtil.setText( TEXT_PROJECT_NAME, TEXT_BLANK );

        // assertEquals( TEXT_BLANK, textUtil.getText( TEXT_PROJECT_NAME ) );
        // assertEquals( TEXT_BLANK, textUtil.getText( TEXT_DISPLAY_NAME ) );

        // assertEquals( MENU_BUILD_TYPE_ANT, comboBoxUtil.getText( TEXT_BUILD_TYPE ) );
        // assertEquals( MENU_PORTLET, comboBoxUtil.getText( TEXT_PLUGIN_TYPE ) );

        // assertTrue( buttonUtil.isTooltipEnabled( TOOLTIP_LEARN_MORE ) );
        // assertTrue( checkBoxUtil.isChecked( TEXT_INCLUDE_SAMPLE_CODE ) );
        // assertFalse( checkBoxUtil.isChecked( TEXT_ADD_PROJECT_TO_WORKING_SET ) );
        // assertFalse( comboBoxUtil.isEnabled( TEXT_WORKING_SET ) );
        // assertFalse( buttonUtil.isEnabled( BUTTON_BACK ) );
        // assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );
        // assertFalse( buttonUtil.isEnabled( BUTTON_FINISH ) );
        // assertTrue( buttonUtil.isEnabled( BUTTON_CANCEL ) );
        //
        // // textUtil.setText( TEXT_PROJECT_NAME, invalidNameDoubleDash );
        // assertEquals( " The project name is invalid.", textUtil.getText( INDEX_VALIDATION_MESSAGE ) );
        //
        // // textUtil.setText( TEXT_PROJECT_NAME, invalidNameDoubleSlash );
        // assertEquals( " / is an invalid character in resource name '//'.", textUtil.getText( INDEX_VALIDATION_MESSAGE
        // ) );
        //
        // // textUtil.setText( TEXT_PROJECT_NAME, invalidNameDot );
        // assertEquals( " '.' is an invalid name on this platform.", textUtil.getText( INDEX_VALIDATION_MESSAGE ) );
        //
        // // textUtil.setText( TEXT_PROJECT_NAME, invalidNameStar );
        // assertEquals( " * is an invalid character in resource name '*'.", textUtil.getText( INDEX_VALIDATION_MESSAGE
        // ) );
        //
        // buttonUtil.click( BUTTON_CANCEL );
    }

    @After
    public void waitForCreate()
    {
        sleep( 5000 );
    }

}
