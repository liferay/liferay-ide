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

import java.io.IOException;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.layouttpl.ui.tests.pages.ChooseInitialTemplatePO;
import com.liferay.ide.swtbot.layouttpl.ui.tests.pages.CreateLayoutTemplateWizardPO;
import com.liferay.ide.swtbot.layouttpl.ui.tests.pages.TempalteSelectionDialogPO;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.project.ui.tests.page.CreateProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SetSDKLocationPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class NewLayoutTemplateWizardTests extends SWTBotBase implements CreateLayouttplWizard, ProjectWizard
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

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPluginProject().click();

        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot );

        createProjectWizard.createSDKProject( projectName, MENU_LAYOUT_TEMPLATE );

        if( createProjectWizard.finishButton().isEnabled() )
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

        sleep( 60000 );
    }

    CreateLayoutTemplateWizardPO createLayoutTemplate = new CreateLayoutTemplateWizardPO( bot );

    ChooseInitialTemplatePO chooseOneInitialTemplate = new ChooseInitialTemplatePO( bot );

    @After
    public void closeWizard()
    {
        eclipse.closeShell( TITLE_NEW_LAYOUT_TEMPLATE );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayLayoutTemplate().click();
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values
        assertEquals( projectName, createLayoutTemplate.getLayoutPluginProjectText() );
        assertEquals( "New Template", createLayoutTemplate.getNameText() );
        assertEquals( "newtemplate", createLayoutTemplate.getIdText() );
        assertEquals( "/newtemplate.tpl", createLayoutTemplate.getTemplateFileText() );
        assertEquals( "/newtemplate.wap.tpl", createLayoutTemplate.getWapTemplateFileText() );
        assertEquals( "/newtemplate.png", createLayoutTemplate.getThumbnailFileText() );
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
        createLayoutTemplate.setNameText( "Test Template" );
        createLayoutTemplate.setIdText( "testtesttemplate" );
        createLayoutTemplate.setTemplateFileText( "testtemplate.tpl" );
        createLayoutTemplate.setWapTemplateFileText( "testtemplate.wap.tpl" );
        createLayoutTemplate.setThumbnailFileText( "testtemplate.png" );

        createLayoutTemplate.next();
        ChooseInitialTemplatePO chooseOneInitialTemplate = new ChooseInitialTemplatePO( bot );
        chooseOneInitialTemplate.selectRadio( "1-2 Columns" );
        createLayoutTemplate.finish();

        sleep( 2000 );
        assertEquals( true, eclipse.getProjectTree().hasTreeItem( projectName, "docroot", "test.tpl" ) );
        assertEquals( true, eclipse.getProjectTree().hasTreeItem( projectName, "docroot", "blank_columns.wap.tpl" ) );
        assertEquals( true, eclipse.getProjectTree().hasTreeItem( projectName, "docroot", "testtemplate.png" ) );

    }

    @Test
    public void testID()
    {
        createLayoutTemplate.setIdText( "" );

        assertEquals( "New Template", createLayoutTemplate.getNameText() );
        assertEquals( "/.tpl", createLayoutTemplate.getTemplateFileText() );
        assertEquals( "/.wap.tpl", createLayoutTemplate.getWapTemplateFileText() );
        assertEquals( "/.png", createLayoutTemplate.getThumbnailFileText() );
        assertEquals( TEXT_ID_CANNT_BE_EMPTY, createLayoutTemplate.getValidationMessage() );
        assertEquals( false, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setIdText( "layout test" );
        assertEquals( "New Template", createLayoutTemplate.getNameText() );
        assertEquals( "/layout test.tpl", createLayoutTemplate.getTemplateFileText() );
        assertEquals( "/layout test.wap.tpl", createLayoutTemplate.getWapTemplateFileText() );
        assertEquals( "/layout test.png", createLayoutTemplate.getThumbnailFileText() );
        assertEquals( TEXT_ID_INVALID, createLayoutTemplate.getValidationMessage() );
        assertEquals( false, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setIdText( "newtemplate" );
        assertEquals( TEXT_DEFAULT_MESSAGE, createLayoutTemplate.getValidationMessage() );
    }

    @Test
    public void testName()
    {
        createLayoutTemplate.setNameText( "" );

        assertEquals( "", createLayoutTemplate.getIdText() );
        assertEquals( "/.tpl", createLayoutTemplate.getTemplateFileText() );
        assertEquals( "/.wap.tpl", createLayoutTemplate.getWapTemplateFileText() );
        assertEquals( "/.png", createLayoutTemplate.getThumbnailFileText() );
        assertEquals( TEXT_ID_CANNT_BE_EMPTY, createLayoutTemplate.getValidationMessage() );
        assertEquals( false, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setNameText( "New_ Template" );
        assertEquals( "newtemplate", createLayoutTemplate.getIdText() );
        assertEquals( TEXT_DEFAULT_MESSAGE, createLayoutTemplate.getValidationMessage() );

        createLayoutTemplate.setNameText( "" );
        createLayoutTemplate.setIdText( "newtemplate" );
        assertEquals( TEXT_DEFAULT_MESSAGE, createLayoutTemplate.getValidationMessage() );
    }

    @Test
    public void testTemplateFile()
    {
        createLayoutTemplate.clickBrowseButton( 0 );

        TempalteSelectionDialogPO templateFileSelection = new TempalteSelectionDialogPO( bot );

        assertTrue( templateFileSelection.containsItem( "test.tpl" ) );
        assertTrue( templateFileSelection.containsItem( "test.png" ) );
        assertTrue( templateFileSelection.containsItem( "blank_columns.wap.tpl" ) );

        templateFileSelection.select( "WEB-INF" );
        assertEquals( TEXT_CHOOSE_VALID_PROJECT_FILE, templateFileSelection.getValidationMessage() );
        assertEquals( false, templateFileSelection.canFinish() );

        templateFileSelection.select( "test.tpl" );
        templateFileSelection.confirm();

        assertEquals( TEXT_TEMPLATE_FILE_EXIST, createLayoutTemplate.getValidationMessage() );
        assertEquals( true, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setTemplateFileText( "" );
        assertEquals( TEXT_TEMPLATE_FILE_INVALID, createLayoutTemplate.getValidationMessage() );
        assertEquals( false, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setTemplateFileText( "aa.tpl" );
        assertEquals( true, createLayoutTemplate.finishButton().isEnabled() );
    }

    @Test
    public void testThumbnailFile()
    {
        createLayoutTemplate.clickBrowseButton( 2 );

        TempalteSelectionDialogPO thumbnailFileSelection = new TempalteSelectionDialogPO( bot );

        assertTrue( thumbnailFileSelection.containsItem( "test.tpl" ) );
        assertTrue( thumbnailFileSelection.containsItem( "test.png" ) );
        assertTrue( thumbnailFileSelection.containsItem( "blank_columns.wap.tpl" ) );

        thumbnailFileSelection.select( "WEB-INF" );

        assertEquals( TEXT_CHOOSE_VALID_PROJECT_FILE, thumbnailFileSelection.getValidationMessage() );
        assertEquals( false, thumbnailFileSelection.canFinish() );

        thumbnailFileSelection.select( "blank_columns.wap.tpl" );
        thumbnailFileSelection.confirm();

        assertEquals( TEXT_THUMBNAIL_FILE_EXIST, createLayoutTemplate.getValidationMessage() );
        assertEquals( true, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setThumbnailFileText( "" );
        assertEquals( TEXT_THUMBNAIL_FILE_INVALID, createLayoutTemplate.getValidationMessage() );
        assertEquals( false, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setThumbnailFileText( "/aa.wap.tpl" );
        assertEquals( true, createLayoutTemplate.finishButton().isEnabled() );
    }

    @Test
    public void testWapTemplateFile()
    {
        createLayoutTemplate.clickBrowseButton( 1 );

        TempalteSelectionDialogPO templateFileSelection = new TempalteSelectionDialogPO( bot );

        assertTrue( templateFileSelection.containsItem( "test.tpl" ) );
        assertTrue( templateFileSelection.containsItem( "test.png" ) );
        assertTrue( templateFileSelection.containsItem( "blank_columns.wap.tpl" ) );

        templateFileSelection.select( "WEB-INF" );
        assertEquals( TEXT_CHOOSE_VALID_PROJECT_FILE, templateFileSelection.getValidationMessage() );
        assertEquals( false, templateFileSelection.canFinish() );

        templateFileSelection.select( "blank_columns.wap.tpl" );
        templateFileSelection.confirm();

        assertEquals( TEXT_WAP_TEMPLATE_FILE_EXIST, createLayoutTemplate.getValidationMessage() );
        assertEquals( true, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setWapTemplateFileText( "" );
        assertEquals( TEXT_WAP_TEMPLATE_FILE_INVALID, createLayoutTemplate.getValidationMessage() );
        assertEquals( false, createLayoutTemplate.finishButton().isEnabled() );

        createLayoutTemplate.setWapTemplateFileText( "/aa.wap.tpl" );
        assertEquals( true, createLayoutTemplate.finishButton().isEnabled() );
    }
}
