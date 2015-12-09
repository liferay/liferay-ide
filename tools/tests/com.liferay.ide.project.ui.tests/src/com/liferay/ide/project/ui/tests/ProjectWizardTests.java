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
import static org.junit.Assert.assertTrue;

import com.liferay.ide.ui.tests.SWTBotBase;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 * @author Vicky Wang
 * @author Ying Xu
 */
public class ProjectWizardTests extends SWTBotBase implements ProjectWizard
{

    public static boolean added = false;
    public static String currentType = "";

    @After
    public void waitForCreate() {
    	sleep(5000);
    }

    private boolean addedProjecs()
    {
        viewUtil.show( VIEW_PACKAGE_EXPLORER );

        return treeUtil.hasItems();
    }

    @AfterClass
    public static void cleanAll()
    {
        SWTBotTreeItem[] items = treeUtil.getItems();

        try
        {
            for( SWTBotTreeItem item : items )
            {
                if( !item.getText().equals( getLiferayPluginsSdkName() ) )
                {
                    item.contextMenu( BUTTON_DELETE ).click();

                    checkBoxBot.click();

                    buttonBot.click( BUTTON_OK );

                    if( buttonBot.isEnabled( "Continue" ) )
                    {
                        buttonBot.click( "Continue" );
                    }

                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void createExtProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "" );
        page1.createSDKProject( "text", MENU_EXT );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<>( bot, "" );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    public void createHookProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "" );
        page1.createSDKProject( "text", MENU_HOOK );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "" );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    public void createServiceBuilderPortletProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "" );

        page1.createSDKProject( "textsb", MENU_SERVICE_BUILDER_PORTLET, true );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "" );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    public void createServiceBuilderPortletProjectWithoutSampleCode()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "" );

        page1.createSDKProject( "textsbwithoutcode", MENU_SERVICE_BUILDER_PORTLET, false );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "" );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    public void createLayoutProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "" );

        page1.createSDKProject( "text", MENU_LAYOUT_TEMPLATE );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "" );
            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @Test
    @Ignore
    public void createPortletProject()
    {
        // currentType = "portlet";
        //
        // String projectName = "testPortlet";
        //
        // // setProjectName( projectName );
        //
        // buttonUtil.click( BUTTON_NEXT );
        //
        // assertEquals( TEXT_CHOOSE_AVAILABLE_PORTLET_FRAMEWORKS, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        //
        // assertTrue( radioUtil.radio( TEXT_LIFERAY_MVC_FRAMEWORK ).isSelected() );
        // assertTrue( labelUtil.labelInGroup( TEXT_ADDITIONAL_PORTLET_OPTIONS, INDEX_VALIDATION_MESSAGE1 ).isVisible()
        // );
        // assertTrue( labelUtil.labelInGroup( TEXT_ADDITIONAL_PORTLET_OPTIONS, INDEX_VALIDATION_MESSAGE2 ).isVisible()
        // );
        //
        // buttonUtil.click( BUTTON_FINISH );
        // // assertTrue( UITestsUtils.checkConsoleMessage( "BUILD SUCCESSFUL", "Java" ) );
        //
        // treeUtil.expandNode( projectName + "-portlet", "docroot", "WEB-INF" ).getNode( "liferay-display.xml"
        // ).doubleClick();
        // assertTrue( editorUtil.isActive( "liferay-display.xml" ) );
        // assertContains( "sample", textUtil.getStyledText() );
        //
        // toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );
        //
        // // textUtil.setText( TEXT_PROJECT_NAME, projectName );
        //
        // assertEquals( TEXT_PROJECT_ALREADY_EXISTS, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        // assertFalse( buttonUtil.isEnabled( BUTTON_NEXT ) );
        // // enter projet with -portlet and check
        // // textUtil.setText( TEXT_PROJECT_NAME, projectName + "-portlet" );
        // assertEquals( TEXT_PROJECT_ALREADY_EXISTS, textUtil.getText( INDEX_VALIDATION_MESSAGE3 ) );
        // assertFalse( buttonUtil.isEnabled( BUTTON_FINISH ) );
        //
        // buttonUtil.click( BUTTON_CANCEL );
        //
        // // enter project name which is existing in workspace
        // deleteProject( projectName + "-portlet" );
        //
        // toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );
        // comboBoxUtil.select( 1, MENU_PORTLET );
        // // textUtil.setText( TEXT_PROJECT_NAME, projectName );
        // assertContains(
        // projectName + "-portlet\"" + TEXT_PROJECT_EXISTS_IN_LOCATION, textUtil.getText( INDEX_VALIDATION_MESSAGE3 )
        // );
        // // textUtil.setText( TEXT_PROJECT_NAME, projectName + "-portlet" );
        // assertContains(
        // projectName + "-portlet\"" + TEXT_PROJECT_EXISTS_IN_LOCATION, textUtil.getText( INDEX_VALIDATION_MESSAGE3 )
        // );
        // buttonUtil.click( BUTTON_CANCEL );
        // deleteProjectInSdk( projectName + "-portlet", getLiferayPluginsSdkName(), "portlets" );
    }

    @Test
    @Ignore
    public void createPortletProjectWithoutSampleAndLaunchNewPortletWizard()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "" );
        page1.createSDKProject( "text", MENU_PORTLET, false, false );

        if( added )
        {
            page1.finish();
        }
        else
        {
//            page1.next();
//            page1.next();
//            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "" );
//            page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );
//            page2.finish();
            // TODO need add new po for portlet second page.
        }

    }

    @Test
    public void createThemeProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "" );
        ThemeWizardPageObject<SWTWorkbenchBot> page2 =
            new ThemeWizardPageObject<SWTWorkbenchBot>( bot, "", INDEX_THEME_VALIDATION_MESSAGE );

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

        //Need to use view page object but waiting for LiLu finished.
        treeUtil.getTreeItem( projectThemeName + "-theme" ).click();
        treeUtil.expandNode( projectThemeName + "-theme" ).getNode( "build.xml" ).doubleClick();
        assertTrue( editorUtil.isActive( "build.xml" ) );
        assertContains( "_styled", textUtil.getStyledText() );
        assertContains( "ftl", textUtil.getStyledText() );
    }

    @Test
    public void createWebProject()
    {
        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "" );

        page1.createSDKProject( "text", MENU_WEB );

        page1.next();

        SetSDKLocationPageObject<SWTWorkbenchBot> page2 = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "" );

        page2.setSdkLocation( getLiferayPluginsSdkDir().toString() );

        page2.cancel();
    }

    @Test
    public void validationProjectName()
    {
        String invalidNameDoubleDash = "--";
        // String invalidNameDoubleSlash = "//";
        // String invalidNameDot = ".";
        // String invalidNameStar = "*";

        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, "", INDEX_VALIDATION_MESSAGE );

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
        // // assertTrue( checkBoxUtil.isChecked( TEXT_INCLUDE_SAMPLE_CODE ) );
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

    private void deleteProject( String projectName )
    {
        viewUtil.show( VIEW_PACKAGE_EXPLORER );
        treeUtil.getNode( projectName ).contextMenu( BUTTON_DELETE ).click();
        buttonBot.click( BUTTON_OK );
        sleep();
    }

    public static void deleteProjectInSdk( String projectName, String... nodes )
    {
        treeUtil.expandNode( nodes ).getNode( projectName ).contextMenu( BUTTON_DELETE ).click();

        buttonBot.click( BUTTON_OK );
    }

    private SetSDKLocationPageObject<SWTWorkbenchBot> getSetSDKLoactionPage()
    {
        SetSDKLocationPageObject<SWTWorkbenchBot> page = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "" );
        page.setSdkLocation( getLiferayPluginsSdkDir().toString() );

        return page;
    }

    @Before
    public void openWizard()
    {
        added = addedProjecs();

        toolbarUtil.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );
    }

}
