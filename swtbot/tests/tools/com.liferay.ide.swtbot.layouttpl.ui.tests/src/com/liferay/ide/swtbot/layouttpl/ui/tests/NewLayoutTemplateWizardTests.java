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

package com.liferay.ide.swtbot.layouttpl.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ChooseInitialTemplateWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.CreateLayoutTemplateWizardWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewSdkProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.SetSDKLocationWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.TreeDialog;
import com.liferay.ide.swtbot.ui.util.StringPool;

import java.io.IOException;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class NewLayoutTemplateWizardTests extends SwtbotBase
{

    static String projectName = "test-layouttpl";

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @BeforeClass
    public static void createProject() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipPluginsSDK();
        unzipServer();

        ide.getCreateLiferayProjectToolbar().getNewLiferayPlugin().click();

        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.createSDKProject( projectName, LAYOUT_TEMPLATE );

        if( createProjectWizard.finishBtn().isEnabled() )
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

        sleep( 60000 );
    }

    CreateLayoutTemplateWizardWizard createLayoutTemplate = new CreateLayoutTemplateWizardWizard( bot );

    ChooseInitialTemplateWizard chooseOneInitialTemplate = new ChooseInitialTemplateWizard( bot );

    @After
    public void closeWizard()
    {
        ide.closeShell( NEW_LAYOUT_TEMPLATE );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayLayoutTemplate().click();
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values
        assertEquals( projectName, createLayoutTemplate.getLayoutPluginProjects().getText() );
        assertEquals( "New Template", createLayoutTemplate.getName().getText() );
        assertEquals( "newtemplate", createLayoutTemplate.getId().getText() );
        assertEquals( "/newtemplate.tpl", createLayoutTemplate.getTemplateFile().getText() );
        assertEquals( "/newtemplate.wap.tpl", createLayoutTemplate.getWapTemplateFile().getText() );
        assertEquals( "/newtemplate.png", createLayoutTemplate.getThumbnailFile().getText() );
        // Select initial template to start designing.
        createLayoutTemplate.next();
        assertEquals( true, chooseOneInitialTemplate.isRadioSelected( "1 Column" ) );
        assertEquals( false, chooseOneInitialTemplate.isRadioSelected( "1-2 Columns" ) );
        assertEquals( false, chooseOneInitialTemplate.isRadioSelected( 2 ) );
        assertEquals( false, chooseOneInitialTemplate.isRadioSelected( "1-2-1 Columns" ) );
        assertEquals( false, chooseOneInitialTemplate.isRadioSelected( "2 Columns" ) );
        assertEquals( false, chooseOneInitialTemplate.isRadioSelected( 5 ) );
        assertEquals( false, chooseOneInitialTemplate.isRadioSelected( 6 ) );
        assertEquals( false, chooseOneInitialTemplate.isRadioSelected( 7 ) );
        assertEquals( false, chooseOneInitialTemplate.isRadioSelected( "3 Columns" ) );
    }

    @Test
    public void testCreateLayoutTemplate()
    {
        createLayoutTemplate.getName().setText( "Test Template" );
        createLayoutTemplate.getId().setText( "testtesttemplate" );
        createLayoutTemplate.getTemplateFile().setText( "testtemplate.tpl" );
        createLayoutTemplate.getWapTemplateFile().setText( "testtemplate.wap.tpl" );
        createLayoutTemplate.getThumbnailFile().setText( "testtemplate.png" );

        createLayoutTemplate.next();
        ChooseInitialTemplateWizard chooseOneInitialTemplate = new ChooseInitialTemplateWizard( bot );
        chooseOneInitialTemplate.selectRadio( "1-2 Columns" );
        createLayoutTemplate.finish();

        sleep( 2000 );
        assertEquals( true, ide.getProjectTree().hasTreeItem( projectName, "docroot", "test.tpl" ) );
        assertEquals( true, ide.getProjectTree().hasTreeItem( projectName, "docroot", "blank_columns.wap.tpl" ) );
        assertEquals( true, ide.getProjectTree().hasTreeItem( projectName, "docroot", "testtemplate.png" ) );

    }

    @Test
    public void testID()
    {
        createLayoutTemplate.getId().setText( StringPool.BLANK );

        assertEquals( "New Template", createLayoutTemplate.getName().getText() );
        assertEquals( "/.tpl", createLayoutTemplate.getTemplateFile().getText() );
        assertEquals( "/.wap.tpl", createLayoutTemplate.getWapTemplateFile().getText() );
        assertEquals( "/.png", createLayoutTemplate.getThumbnailFile().getText() );
        assertEquals( ID_CANNT_BE_EMPTY, createLayoutTemplate.getValidationMsg() );
        assertEquals( false, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getId().setText( "layout test" );
        assertEquals( "New Template", createLayoutTemplate.getName().getText() );
        assertEquals( "/layout test.tpl", createLayoutTemplate.getTemplateFile().getText() );
        assertEquals( "/layout test.wap.tpl", createLayoutTemplate.getWapTemplateFile().getText() );
        assertEquals( "/layout test.png", createLayoutTemplate.getThumbnailFile().getText() );
        assertEquals( TEMPLATE_ID_IS_INVALID, createLayoutTemplate.getValidationMsg() );
        assertEquals( false, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getId().setText( "newtemplate" );
        assertEquals( CREATE_A_LIFERAY_LAYOUT_TEMPLATE, createLayoutTemplate.getValidationMsg() );
    }

    @Test
    public void testName()
    {
        createLayoutTemplate.getName().setText( StringPool.BLANK );

        assertEquals( StringPool.BLANK, createLayoutTemplate.getId().getText() );
        assertEquals( "/.tpl", createLayoutTemplate.getTemplateFile().getText() );
        assertEquals( "/.wap.tpl", createLayoutTemplate.getWapTemplateFile().getText() );
        assertEquals( "/.png", createLayoutTemplate.getThumbnailFile().getText() );
        assertEquals( ID_CANNT_BE_EMPTY, createLayoutTemplate.getValidationMsg() );
        assertEquals( false, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getName().setText( "New_ Template" );
        assertEquals( "newtemplate", createLayoutTemplate.getId().getText() );
        assertEquals( CREATE_A_LIFERAY_LAYOUT_TEMPLATE, createLayoutTemplate.getValidationMsg() );

        createLayoutTemplate.getName().setText( StringPool.BLANK );
        createLayoutTemplate.getId().setText( "newtemplate" );
        assertEquals( CREATE_A_LIFERAY_LAYOUT_TEMPLATE, createLayoutTemplate.getValidationMsg() );
    }

    @Test
    public void testTemplateFile()
    {
        createLayoutTemplate.clickBrowseButton( 0 );

        TreeDialog templateFileSelection = new TreeDialog( bot );

        assertTrue( templateFileSelection.getItems().hasTreeItem( "test.tpl" ) );
        assertTrue( templateFileSelection.getItems().hasTreeItem( "test.png" ) );
        assertTrue( templateFileSelection.getItems().hasTreeItem( "blank_columns.wap.tpl" ) );

        templateFileSelection.getItems().selectTreeItem( "WEB-INF" );

        templateFileSelection.getItems().selectTreeItem( "test.tpl" );
        templateFileSelection.confirm();

        assertEquals( TEMPLATE_FILE_ALREDAY_EXIST_OVERWRITTEN, createLayoutTemplate.getValidationMsg() );
        assertEquals( true, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getTemplateFile().setText( StringPool.BLANK );
        assertEquals( TEMPLATE_FILE_NAME_IS_INVALID, createLayoutTemplate.getValidationMsg() );
        assertEquals( false, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getTemplateFile().setText( "aa.tpl" );
        assertEquals( true, createLayoutTemplate.finishBtn().isEnabled() );
    }

    @Test
    public void testThumbnailFile()
    {
        createLayoutTemplate.clickBrowseButton( 2 );

        TreeDialog thumbnailFileSelection = new TreeDialog( bot );

        assertTrue( thumbnailFileSelection.getItems().hasTreeItem( "test.tpl" ) );
        assertTrue( thumbnailFileSelection.getItems().hasTreeItem( "test.png" ) );
        assertTrue( thumbnailFileSelection.getItems().hasTreeItem( "blank_columns.wap.tpl" ) );

        thumbnailFileSelection.getItems().selectTreeItem( "WEB-INF" );

        assertEquals( false, thumbnailFileSelection.confirmBtn().isEnabled() );

        thumbnailFileSelection.getItems().selectTreeItem( "blank_columns.wap.tpl" );
        thumbnailFileSelection.confirm();

        assertEquals( THUMBNAIL_FILE_ALREDAY_EXIST_AND_OVERWRITTEN, createLayoutTemplate.getValidationMsg() );
        assertEquals( true, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getThumbnailFile().setText( StringPool.BLANK );
        assertEquals( THUMBNAIL_FILE_NAME_IS_INVALID, createLayoutTemplate.getValidationMsg() );
        assertEquals( false, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getThumbnailFile().setText( "/aa.wap.tpl" );
        assertEquals( true, createLayoutTemplate.finishBtn().isEnabled() );
    }

    @Test
    public void testWapTemplateFile()
    {
        createLayoutTemplate.clickBrowseButton( 1 );

        TreeDialog templateFileSelection = new TreeDialog( bot );

        assertTrue( templateFileSelection.getItems().hasTreeItem( "test.tpl" ) );
        assertTrue( templateFileSelection.getItems().hasTreeItem( "test.png" ) );
        assertTrue( templateFileSelection.getItems().hasTreeItem( "blank_columns.wap.tpl" ) );

        templateFileSelection.getItems().selectTreeItem( "WEB-INF" );
        assertEquals( false, templateFileSelection.confirmBtn().isEnabled() );

        templateFileSelection.getItems().selectTreeItem( "blank_columns.wap.tpl" );
        templateFileSelection.confirm();

        assertEquals( WAP_TEMPLATE_FILE_EXIST, createLayoutTemplate.getValidationMsg() );
        assertEquals( true, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getWapTemplateFile().setText( StringPool.BLANK );
        assertEquals( WAP_TEMPLATE_FILE_NAME_IS_INVALID, createLayoutTemplate.getValidationMsg() );
        assertEquals( false, createLayoutTemplate.finishBtn().isEnabled() );

        createLayoutTemplate.getWapTemplateFile().setText( "/aa.wap.tpl" );
        assertEquals( true, createLayoutTemplate.finishBtn().isEnabled() );
    }
}
