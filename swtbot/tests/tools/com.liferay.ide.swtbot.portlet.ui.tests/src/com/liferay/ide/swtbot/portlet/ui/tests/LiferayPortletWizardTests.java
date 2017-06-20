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

package com.liferay.ide.swtbot.portlet.ui.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.portlet.ui.tests.page.CreateLiferayPortletWizardPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.InterfaceSelectionPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.LiferayPortletDeploymentDescriptorPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.ModifiersInterfacesMethodStubsPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.NewSourceFolderPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.PackageSelectionPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.PortletDeploymentDescriptorPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.SuperclassSelectionPO;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.project.ui.tests.page.CreateProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewProjectPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SelectTypePO;
import com.liferay.ide.swtbot.project.ui.tests.page.SetSDKLocationPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.EditorPO;
import com.liferay.ide.swtbot.ui.tests.page.SelectionDialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public class LiferayPortletWizardTests extends SWTBotBase implements LiferayPortletWizard, ProjectWizard
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    CreateProjectWizardPO newLiferayProjectPage =
        new CreateProjectWizardPO( bot, LABEL_NEW_LIFERAY_PLUGIN_PROJECT, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_WIZARD );

    CreateLiferayPortletWizardPO newPortletPage =
        new CreateLiferayPortletWizardPO( bot, TITLE_NEW_LIFERAY_PORTLET, INDEX_DEFAULT_CREATE_LIFERAY_PORTLET_WIZARD );

    LiferayPortletDeploymentDescriptorPO specifyLiferayPortletDeploymentDescriptorPage =
        new LiferayPortletDeploymentDescriptorPO( bot, INDEX_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_PAGE );

    PortletDeploymentDescriptorPO specifyPortletDeploymentDescriptorPage =
        new PortletDeploymentDescriptorPO( bot, INDEX_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_PAGE );

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipPluginsSDK();
        unzipServer();
    }

    @After
    public void cleanAll()
    {
        if( addedProjects() )
        {
            eclipse.getProjectTree().setFocus();

            eclipse.getPackageExporerView().deleteProjectExcludeNames(
                new String[] { getLiferayPluginsSdkName() }, true );
        }

        sleep( 3000 );

        try
        {
            eclipse.getPackageExporerView().deleteProjectExcludeNames(
                new String[] { getLiferayPluginsSdkName() }, true );
        }
        catch( Exception e )
        {
        }

        eclipse.closeShell( LABEL_NEW_LIFERAY_PLUGIN_PROJECT );
        eclipse.closeShell( LABEL_NEW_LIFERAY_PORTLET );
    }

    @Test
    public void createPorltetWithoutLiferayProjects()
    {

        if( addedProjects() )
        {
            eclipse.getPackageExporerView().deleteResouceByName( "test-portlet", true );
        }

        String projectName = "liferayProject";

        // click new liferay portlet wizard without projects
        eclipse.getNewToolbar().getLiferayPortlet().click();

        DialogPO dialogPage1 = new DialogPO( bot, TITLE_NEW_LIFERAY_PORTLET, BUTTON_NO, BUTTON_YES );

        dialogPage1.confirm();

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayProjectPage.getValidationMessage() );
        assertFalse( newLiferayProjectPage.nextButton().isEnabled() );

        newLiferayProjectPage.createSDKPortletProject( projectName );
        assertTrue( newLiferayProjectPage.nextButton().isEnabled() );

        newLiferayProjectPage.cancel();

        DialogPO dialogPage2 = new DialogPO( bot, TITLE_NEW_LIFERAY_PORTLET, BUTTON_YES, BUTTON_NO );

        dialogPage2.confirm();

        try
        {
            if( !newPortletPage.getValidationMessage().equals( TEXT_ENTER_A_PROJECT_NAME ) )
            {
                CreateLiferayPortletWizardPO myNewPortletPage =
                    new CreateLiferayPortletWizardPO( bot, LABEL_NEW_LIFERAY_PORTLET, 1 );
                assertEquals( TEXT_ENTER_A_PROJECT_NAME, myNewPortletPage.getValidationMessage() );
                myNewPortletPage.cancel();
            }
            newPortletPage.cancel();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        // new Java project
        eclipse.getFileMenu().clickMenu( MENU_NEW, MENU_PROJECT );

        createProject(
            "Java", "Java", LABEL_JAVA_PROJECT, "JavaExample", TEXT_CREATE_A_JAVA_PROJECT,
            TEXT_CREATE_A_JAVA_PROJECT_IN_WORKSPACE, INDEX_CREATE_A_PROJECT_THROUGH_NEW_WIZARD_PAGE );
        try
        {
            DialogPO dialogPage3 = new DialogPO( bot, "Show In Package Explorer", BUTTON_YES, BUTTON_NO );
            dialogPage3.confirm();
        }
        catch( Exception e )
        {
        }

        // new general project
        eclipse.getFileMenu().clickMenu( MENU_NEW, MENU_PROJECT );

        createProject(
            "project", "General", "Project", "GeneralExample", TEXT_CREATE_A_NEW_PROJECT_RESOURCE,
            TEXT_CREATE_A_NEW_PROJECT_RESOURCE + '.', INDEX_CREATE_A_PROJECT_THROUGH_NEW_WIZARD_PAGE );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        DialogPO dialogPage4 = new DialogPO( bot, "New Liferay Portlet", BUTTON_YES, BUTTON_NO );

        dialogPage4.confirm();

        // IDE-2425
        try
        {
            newLiferayProjectPage.cancel();
        }
        catch( Exception e )
        {
        }

        eclipse.getNewToolbar().getLiferayPortlet().click();

        DialogPO dialogPage5 = new DialogPO( bot, "New Liferay Portlet", BUTTON_NO, BUTTON_YES );

        dialogPage5.confirm();

        newLiferayProjectPage.createSDKPortletProject( projectName );
        newLiferayProjectPage.finish();

        try
        {
            assertTrue( checkServerConsoleMessage( "BUILD SUCCESSFUL", "Java", 30000 ) );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        newPortletPage.waitForPageToOpen();

        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );
        assertEquals( "liferayProject-portlet", newPortletPage.getPortletPluginProject() );
        assertTrue( newPortletPage.get_createNewPortletRadio().isSelected() );
        assertEquals( "com.liferay.util.bridges.mvc.MVCPortlet", newPortletPage.getSuperClassCombobox() );

        newPortletPage.finish();
    }

    public void createProject(
        String filterText, String projectTypeTree, String projectTypeNode, String projectName, String validateMessage1,
        String validateMessage2, int validationIndex )
    {

        SelectTypePO newProjectPage = new SelectTypePO( bot, validationIndex );

        newProjectPage.selectItem( filterText, projectTypeTree, projectTypeNode );
        assertEquals( validateMessage1, newProjectPage.getValidationMessage() );
        newProjectPage.next();

        NewProjectPO newJavaProjectPage = new NewProjectPO( bot, INDEX_NEW_SOURCE_FOLDER_VALIDATION_MESSAGE );

        newJavaProjectPage.createJavaProject( projectName );
        assertEquals( validateMessage2, newJavaProjectPage.getValidationMessage() );

        newJavaProjectPage.finish();

        if( !projectTypeNode.equals( "Project" ) )
        {
            DialogPO dialogPage = new DialogPO( bot, "Open Associated Perspective", BUTTON_YES, BUTTON_NO );

            dialogPage.confirm();
        }
    }

    @Test
    public void javaPackageTest()
    {

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        // java package tests
        newPortletPage.setJavaPackageText( "123" );

        assertEquals(
            TEXT_INVALID_JAVA_PACKAGE_NAME + "'123'" + TEXT_NOT_A_VALID_JAVA_IDENTIFIER,
            newPortletPage.getValidationMessage() );

        newPortletPage.setJavaPackageText( ".." );
        assertEquals(
            TEXT_INVALID_JAVA_PACKAGE_NAME + TEXT_PACKAGE_NAME_CANNOT_END_WITH_DOT,
            newPortletPage.getValidationMessage() );

        newPortletPage.setJavaPackageText( "MyPackage" );
        assertEquals( TEXT_JAVA_PACKAGE_START_WITH_AN_UPPERCASE_LETTER, newPortletPage.getValidationMessage() );

        newPortletPage.get_browsePackageButton().click();

        PackageSelectionPO selectPackagePage = new PackageSelectionPO( bot, "Package Selection", 0 );

        assertEquals( "Choose a package:", selectPackagePage.getDialogLabel() );
        selectPackagePage.clickPackage( 0 );

        selectPackagePage.confirm();

        assertEquals( TEXT_BLANK, newPortletPage.getJavaPackageText() );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.setJavaPackageText( "myPackage" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.finish();

        TextEditorPO portletJavaPage = new TextEditorPO( bot, "NewPortlet.java" );

        assertContains( "package myPackage;", portletJavaPage.getText() );

        assertTrue( portletJavaPage.isActive() );
        portletJavaPage.setFocus();
        // keyPress.pressShortcut( ctrl, N );

        eclipse.getFileMenu().clickMenu( MENU_NEW, "Other..." );

        SelectTypePO newSelectLiferayPage = new SelectTypePO( bot, INDEX_SELECT_A_WIZARD_VALIDATION_MESSAGE );

        newSelectLiferayPage.selectItem( "liferay", "Liferay", LABEL_LIFERAY_PORTLET );
        newSelectLiferayPage.next();
        newPortletPage.createLiferayPortlet( "test-portlet", "MySecondPortlet", TEXT_BLANK, null );
        newPortletPage.finish();

        TextEditorPO mySecondPortletJavaPage = new TextEditorPO( bot, "MySecondPortlet.java" );

        assertFalse( mySecondPortletJavaPage.getText().contains( "package" ) );

    }

    @Test
    public void launchNewPortletInitialTest()
    {
        eclipse.getNewToolbar().getLiferayPluginProject().click();

        assertEquals( "Portlet", newLiferayProjectPage.get_pluginTypeComboBox().getText() );

        String projectName = "mytest";
        String pluginType = "Portlet";

        newLiferayProjectPage.createSDKProject( projectName, pluginType, false, true );

        newLiferayProjectPage.finish();

        newPortletPage.waitForPageToOpen();

        // check initial state
        assertEquals( projectName + "-portlet", newPortletPage.getPortletPluginProject() );
        assertEquals( "/mytest-portlet/docroot/WEB-INF/src", newPortletPage.getSourceFolderText() );
        assertEquals( "NewPortlet", newPortletPage.getPortletClassText() );
        assertEquals( "com.test", newPortletPage.getJavaPackageText() );
        assertEquals( "com.liferay.util.bridges.mvc.MVCPortlet", newPortletPage.getSuperClassCombobox() );
        assertTrue( newPortletPage.get_createNewPortletRadio().isSelected() );
        assertFalse( newPortletPage.get_useDefaultPortletRadio().isSelected() );
        assertTrue( Arrays.equals( newPortletPage.getAvailableSuperclasses(), availableSuperclasses ) );

        newPortletPage.createLiferayPortlet( true );

        assertFalse( newPortletPage.isPortletClassTextEnabled() );
        assertFalse( newPortletPage.isJavaPackageTextEnabled() );
        assertFalse( newPortletPage.isSuperClassComboboxEnabled() );

        newPortletPage.finish();

        // check editor and generated files
        String fileName = "view.jsp";
        TextEditorPO viewEditor = eclipse.getTextEditor( fileName );
        assertTrue( viewEditor.isActive() );

        fileName = "portlet.xml";

        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();
        sleep();
        projectTree.expandNode( "mytest-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        TextEditorPO portletEditor = eclipse.getTextEditor( fileName );

        assertContains( "<portlet-name>new</portlet-name>", portletEditor.getText() );
        assertContains( "com.liferay.util.bridges.mvc.MVCPortlet", portletEditor.getText() );
    }

    @Test
    public void liferayDisplay()
    {

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "NewPortletOne", null, null );

        newPortletPage.next();
        newPortletPage.next();

        assertEquals(
            "new-portlet-one-portlet", specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapperText() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayPortletInfo(
            TEXT_BLANK, true, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        // display category tests
        specifyLiferayPortletDeploymentDescriptorPage.setDisplayCategoryCombobox( TEXT_BLANK );
        assertEquals(
            TEXT_CATEGORY_NAME_IS_EMPTY, specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setDisplayCategoryCombobox( "my1category" );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, true, null, null, true, null );

        assertTrue(
            Arrays.equals(
                availableEntryCategories70,
                specifyLiferayPortletDeploymentDescriptorPage.getEntryCategoryAvailableComboValues() ) );
        assertEquals( "1.5", specifyLiferayPortletDeploymentDescriptorPage.getEntryWeightText() );
        assertEquals(
            "NewPortletOneControlPanelEntry", specifyLiferayPortletDeploymentDescriptorPage.getEntryClassText() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.isEntryCategoryEnabled() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.isEntryWeightEnabled() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.isEntryClassEnabled() );

        newPortletPage.finish();

        String fileName = "liferay-portlet.xml";

        TreePO porjectTree = eclipse.showPackageExporerView().getProjectTree();
        porjectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        TextEditorPO liferayPortletEditor = new TextEditorPO( bot, fileName );

        assertContains(
            "<portlet-name>new-portlet-one</portlet-name>\n\t\t<icon></icon>\n\t\t<control-panel-entry-category>my</control-panel-entry-category>\n\t\t<control-panel-entry-weight>1.5</control-panel-entry-weight>\n\t\t<control-panel-entry-class>\n\t\t\tcom.test.NewPortletOneControlPanelEntry\n\t\t</control-panel-entry-class>\n\t\t<instanceable>true</instanceable>\n\t\t<header-portlet-css></header-portlet-css>\n\t\t<footer-portlet-javascript></footer-portlet-javascript>\n\t\t<css-class-wrapper></css-class-wrapper>",
            liferayPortletEditor.getText() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "NewPortletSecond", null, null );
        newPortletPage.next();

        PortletDeploymentDescriptorPO portletDeploymentDescriptorPage = new PortletDeploymentDescriptorPO( bot );

        portletDeploymentDescriptorPage.speficyPortletInfo( "new-portlet-second2", null, null );
        newPortletPage.next();

        assertEquals(
            "new-portlet-second2-portlet", specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapperText() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayPortletInfo(
            "unexistentIcon", false, "unexistentCss", "unexistentJavaScript", null );

        assertTrue(
            isInAvailableLists(
                specifyLiferayPortletDeploymentDescriptorPage.getDisplayCategoryAvailableComboValues(),
                "my1category" ) );

        // entry tests after checked add to control panel
        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, true, null, null, true, null );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryCategoryCombobox( TEXT_BLANK );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryWeightText( TEXT_BLANK );
        assertEquals(
            TEXT_MUST_SPECIFY_VALID_ENTRY_WEIGHT,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryWeightText( "**" );
        assertEquals(
            TEXT_MUST_SPECIFY_VALID_ENTRY_WEIGHT,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryWeightText( ".1" );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryClassText( TEXT_BLANK );
        assertEquals(
            TEXT_CLASS_NAME_CANNOT_BE_EMPTY, specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryClassText( "." );
        assertEquals(
            TEXT_DONOT_USE_QUALIDIED_CLASS_NAME, specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryClassText( "**" );
        assertEquals(
            TEXT_INVALID_JAVA_CLASS_NAME + "'**'" + TEXT_NOT_A_VALID_IDENTIFIER,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryClassText( "aA" );
        assertEquals(
            TEXT_JAVA_TYPE_START_WITH_AN_UPPERCASE_LETTER,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryClassText( "MyEntryClass" );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, false, null, null, true, null );

        newPortletPage.finish();

        assertContains(
            "<portlet-name>new-portlet-second2</portlet-name>\n\t\t<icon>unexistentIcon</icon>\n\t\t<header-portlet-css>unexistentCss</header-portlet-css>\n\t\t<footer-portlet-javascript>\n\t\t\tunexistentJavaScript\n\t\t</footer-portlet-javascript>\n\t\t<css-class-wrapper>\n\t\t\tnew-portlet-second2-portlet\n\t\t</css-class-wrapper>",
            liferayPortletEditor.getText() );

        // add to control panel and create entry class tests
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet(
            TEXT_BLANK, "NewPortletThird", TEXT_BLANK, "javax.portlet.GenericPortlet" );

        newPortletPage.next();
        sleep( 500 );
        newPortletPage.next();

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay(
            "Tools", true, "MyEntryCategory", "1", true, null );

        newPortletPage.finish();

        assertContains(
            "<portlet-name>new-portlet-third</portlet-name>\n\t\t<icon>/icon.png</icon>\n\t\t<control-panel-entry-category>\n\t\t\tMyEntryCategory\n\t\t</control-panel-entry-category>\n\t\t<control-panel-entry-weight>1</control-panel-entry-weight>\n\t\t<control-panel-entry-class>\n\t\t\tNewPortletThirdControlPanelEntry\n\t\t</control-panel-entry-class>\n\t\t<header-portlet-css>/css/main.css</header-portlet-css>\n\t\t<footer-portlet-javascript>\n\t\t\t/js/main.js\n\t\t</footer-portlet-javascript>\n\t\t<css-class-wrapper>new-portlet-third-portlet</css-class-wrapper>",
            liferayPortletEditor.getText() );

        fileName = "liferay-display.xml";

        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        TextEditorPO liferayDisplayEditor = new TextEditorPO( bot, fileName );

        assertContains(
            "<display>\n\t<category name=\"category.sample\">\n\t\t<portlet id=\"test\" />\n\t\t<portlet id=\"new-portlet-second2\"></portlet>\n\t</category>\n\t<category name=\"my1category\">\n\t\t<portlet id=\"new-portlet-one\"></portlet>\n\t</category>\n\t<category name=\"category.tools\">\n\t\t<portlet id=\"new-portlet-third\"></portlet>\n\t</category>\n</display>",
            liferayDisplayEditor.getText() );

        projectTree.expandNode( "test-portlet", "docroot/WEB-INF/src", "(default package)" );

        TreeItemPO NewPortletThirdControlPanelEntryJavaFile = new TreeItemPO(
            bot, projectTree, "test-portlet", "docroot/WEB-INF/src", "(default package)",
            "NewPortletThirdControlPanelEntry.java" );
        assertTrue( NewPortletThirdControlPanelEntryJavaFile.isVisible() );

        TreeItemPO NewPortletOneControlPanelEntryJavaFile = new TreeItemPO(
            bot, projectTree, "test-portlet", "docroot/WEB-INF/src", "com.test",
            "NewPortletOneControlPanelEntry.java" );
        assertTrue( NewPortletOneControlPanelEntryJavaFile.isVisible() );

    }

    @Test
    public void liferayPorletInfo()
    {
        // browse icon tests
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet(
            TEXT_BLANK, "NewPortletPortletPortlet", "anotherJavaPackage",
            "com.liferay.portal.kernel.portlet.LiferayPortlet" );
        newPortletPage.next();

        assertEquals( "new-portlet-portlet", specifyPortletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "New Portlet Portlet", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "New Portlet Portlet", specifyPortletDeploymentDescriptorPage.getPortletTitle() );
        assertEquals( "/html/newportletportlet", specifyPortletDeploymentDescriptorPage.getJspFolder() );

        newPortletPage.next();

        assertEquals(
            "new-portlet-portlet-portlet", specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapperText() );

        specifyLiferayPortletDeploymentDescriptorPage.get_browseIconButton().click();;

        SelectionDialogPO iconSelectPage = new SelectionDialogPO( bot, "Icon Selection" );

        assertEquals( "Choose an icon file:", iconSelectPage.getDialogLabel() );
        assertFalse( iconSelectPage.confirmButton().isEnabled() );
        assertTrue( iconSelectPage.cancelButton().isEnabled() );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( iconSelectPage.confirmButton().isEnabled() );
        assertTrue( iconSelectPage.cancelButton().isEnabled() );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "js", "main.js" );
        assertTrue( iconSelectPage.cancelButton().isEnabled() );
        iconSelectPage.confirm();

        assertEquals( "/js/main.js", specifyLiferayPortletDeploymentDescriptorPage.getIconText() );

        // browse css tests
        specifyLiferayPortletDeploymentDescriptorPage.get_browseCssButton().click();

        SelectionDialogPO cssSelectPage = new SelectionDialogPO( bot, "CSS Selection" );

        assertEquals( "Choose a css file:", cssSelectPage.getDialogLabel() );
        assertFalse( cssSelectPage.confirmButton().isEnabled() );
        assertTrue( cssSelectPage.cancelButton().isEnabled() );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( cssSelectPage.confirmButton().isEnabled() );
        assertTrue( cssSelectPage.cancelButton().isEnabled() );

        cssSelectPage.getSelcetFileTree().selectTreeItem( "view.jsp" );
        assertTrue( cssSelectPage.cancelButton().isEnabled() );
        cssSelectPage.confirm();

        assertEquals( "/view.jsp", specifyLiferayPortletDeploymentDescriptorPage.getCssText() );

        // browse javaScript tests
        specifyLiferayPortletDeploymentDescriptorPage.get_browseJavaScriptButton().click();

        SelectionDialogPO javaScriptSelectPage = new SelectionDialogPO( bot, "JavaScript Selection" );

        assertEquals( "Choose a javascript file:", javaScriptSelectPage.getDialogLabel() );
        assertFalse( javaScriptSelectPage.confirmButton().isEnabled() );
        assertTrue( javaScriptSelectPage.cancelButton().isEnabled() );

        javaScriptSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( javaScriptSelectPage.confirmButton().isEnabled() );
        assertTrue( javaScriptSelectPage.cancelButton().isEnabled() );

        javaScriptSelectPage.getSelcetFileTree().selectTreeItem( "view.jsp" );
        assertTrue( javaScriptSelectPage.cancelButton().isEnabled() );
        javaScriptSelectPage.confirm();

        assertEquals( "/view.jsp", specifyLiferayPortletDeploymentDescriptorPage.getJavaScriptText() );

        newPortletPage.finish();

        String fileName = "liferay-portlet.xml";

        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        TextEditorPO liferayPortletEditor = new TextEditorPO( bot, fileName );

        assertContains(
            "<portlet-name>new-portlet-portlet</portlet-name>\n\t\t<icon>/js/main.js</icon>\n\t\t<header-portlet-css>/view.jsp</header-portlet-css>\n\t\t<footer-portlet-javascript>/view.jsp</footer-portlet-javascript>\n\t\t<css-class-wrapper>\n\t\t\tnew-portlet-portlet-portlet\n\t\t</css-class-wrapper>",
            liferayPortletEditor.getText() );
    }

    @Test
    public void liferayPortletDeploymentDescriptorDefaultTest()
    {
        TreePO porjectTree = eclipse.showPackageExporerView().getProjectTree();

        String fileName = "liferay-portlet.xml";
        porjectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        TextEditorPO liferayPortletEditor = new TextEditorPO( bot, fileName );

        assertContains(
            "<portlet-name>test</portlet-name>\n\t\t<icon>/icon.png</icon>\n\t\t<header-portlet-css>/css/main.css</header-portlet-css>\n\t\t<footer-portlet-javascript>/js/main.js</footer-portlet-javascript>\n\t\t<css-class-wrapper>test-portlet</css-class-wrapper>",
            liferayPortletEditor.getText() );

        // new liferay portlet and go to speficy liferay portlet deployment descriptor page

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.next();
        newPortletPage.next();

        // check initial state
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        assertEquals( "/icon.png", specifyLiferayPortletDeploymentDescriptorPage.getIconText() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isAddToControlPanelChecked() );
        assertEquals( "/css/main.css", specifyLiferayPortletDeploymentDescriptorPage.getCssText() );
        assertEquals( "/js/main.js", specifyLiferayPortletDeploymentDescriptorPage.getJavaScriptText() );
        assertEquals( "new-portlet", specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapperText() );

        assertEquals( "Sample", specifyLiferayPortletDeploymentDescriptorPage.getDisplayCategoryCombobox() );
        assertTrue(
            Arrays.equals(
                specifyLiferayPortletDeploymentDescriptorPage.getDisplayCategoryAvailableComboValues(),
                availableDisplayCategories70 ) );

        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isAddToControlPanelChecked() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isEntryCategoryEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isEntryWeightEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isCreateEntryClassEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isEntryClassEnabled() );

        assertEquals(
            "My Account Administration", specifyLiferayPortletDeploymentDescriptorPage.getEntryCategoryCombobox() );
        assertEquals( "1.5", specifyLiferayPortletDeploymentDescriptorPage.getEntryWeightText() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isCreateEntryClassChecked() );
        assertEquals(
            "NewPortletControlPanelEntry", specifyLiferayPortletDeploymentDescriptorPage.getEntryClassText() );

        newPortletPage.finish();

        // check codes generate in liferay-portlet.xml
        assertContains(
            "<portlet-name>new</portlet-name>\n\t\t<icon>/icon.png</icon>\n\t\t<header-portlet-css>/css/main.css</header-portlet-css>\n\t\t<footer-portlet-javascript>\n\t\t\t/js/main.js\n\t\t</footer-portlet-javascript>\n\t\t<css-class-wrapper>new-portlet</css-class-wrapper>\n\t</portlet>\n\t<role-mapper>",
            liferayPortletEditor.getText() );
    }

    @Test
    public void modifiersInterfacesMethodStubs()
    {
        // new liferay portlet project without sample code and launch portlet wizard
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();;

        newPortletPage.createLiferayPortlet( TEXT_BLANK, null, null, "javax.portlet.GenericPortlet" );

        newPortletPage.next();
        newPortletPage.next();

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, true, null, null, true, null );

        newPortletPage.next();

        // check initial state
        ModifiersInterfacesMethodStubsPO modifiersInterfacesMethodStubsPage =
            new ModifiersInterfacesMethodStubsPO( bot, INDEX_SPECIFY_PARAMS_IN_PORTLET_CLASS_PAGE );
        assertEquals(
            TEXT_SPECIFY_STUBS_TO_GENERATE_IN_PORTLET_CLASS,
            modifiersInterfacesMethodStubsPage.getValidationMessage() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_publicCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_publicCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_abstractCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_abstractCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_finalCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_finalCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_constrcutFromSuperClassCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_constrcutFromSuperClassCheckbox().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_inheritedAbstractMethodsCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_inheritedAbstractMethodsCheckbox().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_initCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_initCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_destoryCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_destoryCheckbox().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_doViewCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doViewCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_doEditCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doHelpCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_doHelpCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doAboutCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doAboutCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doConfigCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doConfigCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditDefaultsCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditDefaultsCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditGuestCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditGuestCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doPreviewCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doPreviewCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doPrintCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doPrintCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_processActionCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_processActionCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_serveResourceCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_serveResourceCheckbox().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_addButton().isEnabled() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_removeButton().isEnabled() );

        modifiersInterfacesMethodStubsPage.get_finalCheckbox().select();
        modifiersInterfacesMethodStubsPage.get_serveResourceCheckbox().select();

        modifiersInterfacesMethodStubsPage.get_addButton().click();

        // click Add button to add interface and tests
        InterfaceSelectionPO selectInterfacePage = new InterfaceSelectionPO( bot, "Interface Selection" );

        selectInterfacePage.setItemToOpen( "acceptor" );
        selectInterfacePage.clickMatchItem( 0 );
        selectInterfacePage.confirm();

        modifiersInterfacesMethodStubsPage.selectInterface( 0 );
        modifiersInterfacesMethodStubsPage.get_removeButton().click();

        assertFalse( modifiersInterfacesMethodStubsPage.get_removeButton().isEnabled() );

        modifiersInterfacesMethodStubsPage.get_addButton().click();

        assertTrue( selectInterfacePage.confirmButton().isEnabled() );
        selectInterfacePage.confirm();

        modifiersInterfacesMethodStubsPage.get_constrcutFromSuperClassCheckbox().select();

        newPortletPage.finish();

        // check generate codes
        TextEditorPO newPortletJavaEditor = new TextEditorPO( bot, "NewPortlet.java" );

        assertContains(
            "public final class NewPortlet extends GenericPortlet implements Acceptor",
            newPortletJavaEditor.getText() );
        assertContains( "public void init()", newPortletJavaEditor.getText() );
        assertContains( "public void serveResource", newPortletJavaEditor.getText() );
        assertContains( "public void doView", newPortletJavaEditor.getText() );
        assertContains( "NewPortlet()", newPortletJavaEditor.getText() );
    }

    @Test
    public void portletClassTest()
    {

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        // portlet class tests
        newPortletPage.setPortletClassText( TEXT_BLANK );
        assertEquals( TEXT_CLASS_NAME_CANNOT_BE_EMPTY, newPortletPage.getValidationMessage() );

        newPortletPage.setPortletClassText( "123" );
        assertEquals(
            TEXT_INVALID_JAVA_CLASS_NAME + "'123'" + TEXT_NOT_A_VALID_IDENTIFIER,
            newPortletPage.getValidationMessage() );

        newPortletPage.setPortletClassText( "aaa" );
        assertEquals( TEXT_JAVA_TYPE_START_WITH_AN_UPPERCASE_LETTER, newPortletPage.getValidationMessage() );

        newPortletPage.setPortletClassText( "MyTestPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.finish();

        TextEditorPO myPortletJavaPage = new TextEditorPO( bot, "MyTestPortlet.java" );

        assertContains( "class MyTestPortlet", myPortletJavaPage.getText() );
    }

    @Test
    public void portletDeploymentDescriptorWithoutSampleCode()
    {

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.finish();

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPluginProject().click();

        // create portlet project without sample and launch portlet wizard
        newLiferayProjectPage.createSDKProject( "test-second", "Portlet", false, false );
        newLiferayProjectPage.finish();

        // new liferay portlet wizard with default MVCPortlet
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( "test-second-portlet", true );

        newPortletPage.next();

        assertTrue( specifyPortletDeploymentDescriptorPage.finishButton().isEnabled() );
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMessage() );

        newPortletPage.back();
        newPortletPage.createLiferayPortlet( "test-portlet", true );

        assertFalse( newPortletPage.finishButton().isEnabled() );

        newPortletPage.next();

        assertFalse( specifyPortletDeploymentDescriptorPage.finishButton().isEnabled() );
        assertEquals( TEXT_PORTLET_NAME_ALREADY_EXISTS, specifyPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyPortletDeploymentDescriptorPage.setPortletName( "New" );
        assertEquals(
            TEXT_VIEW_JSP_EXSITS_AND_OVERWRITTEN, specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        assertTrue( specifyPortletDeploymentDescriptorPage.finishButton().isEnabled() );

        specifyPortletDeploymentDescriptorPage.specifyResources( false, null, true, null );
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        assertTrue( specifyPortletDeploymentDescriptorPage.get_resourceBundleFilePathText().isEnabled() );

        newPortletPage.finish();

        String fileName = "portlet.xml";

        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );
        TextEditorPO portletXmlPage = eclipse.getTextEditor( fileName );

        assertContains( "<portlet-name>New</portlet-name>", portletXmlPage.getText() );
        assertContains( "<resource-bundle>content.Language</resource-bundle>", portletXmlPage.getText() );

        fileName = "Language.properties";

        sleep( 2000 );
        projectTree.expandNode( "test-portlet", "docroot/WEB-INF/src", "content" );

        TreeItemPO languageProperties =
            new TreeItemPO( bot, projectTree, "test-portlet", "docroot/WEB-INF/src", "content", fileName );

        assertTrue( languageProperties.isVisible() );
    }

    @Test
    public void portletDeploymentDesriptorInitialState()
    {

        // relate ticket IDE-2156, regression for IDE-119
        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();

        projectTree.expandNode( "test-portlet" );
        sleep( 5000 );
        projectTree.getTreeItem( "test-portlet" ).getTreeItem( "docroot", "WEB-INF", "liferay-display.xml" ).doAction(
            BUTTON_DELETE );

        DialogPO deleteDialog = new DialogPO( bot, "New Liferay Portlet", BUTTON_CANCEL, BUTTON_OK );
        deleteDialog.confirm();

        // new liferay portlet wizard
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.next();

        // initial state check
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        assertEquals( "new", specifyPortletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "New", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "New", specifyPortletDeploymentDescriptorPage.getPortletTitle() );
        assertTrue( specifyPortletDeploymentDescriptorPage.get_viewPortletModeCheckbox().isChecked() );
        assertTrue( specifyPortletDeploymentDescriptorPage.get_createJspFilesCheckbox().isChecked() );
        assertEquals( "/html/new", specifyPortletDeploymentDescriptorPage.getJspFolder() );
        assertFalse( specifyPortletDeploymentDescriptorPage.get_createResourceBundleFileCheckbox().isChecked() );
        assertFalse( specifyPortletDeploymentDescriptorPage.get_resourceBundleFilePathText().isEnabled() );
        assertEquals(
            "content/Language.properties", specifyPortletDeploymentDescriptorPage.getResourceBundleFilePath() );

        newPortletPage.finish();

        // IDE-2156 treeItemPage.isVisible();

        // check generate codes and files
        EditorPO newPortletJavaPage = new EditorPO( bot, "NewPortlet.java" );
        assertTrue( newPortletJavaPage.isActive() );

        String fileName = "view.jsp";

        projectTree.expandNode( "test-portlet", "docroot", "html", "new" );
        TreeItemPO viewJsp = new TreeItemPO( bot, projectTree, "test-portlet", "docroot", "html", "new", fileName );

        assertTrue( viewJsp.isVisible() );

        fileName = "portlet.xml";
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        TextEditorPO portletXmlPage = eclipse.getTextEditor( "portlet.xml" );

        assertContains( "<portlet-name>new</portlet-name>", portletXmlPage.getText() );
        assertContains( "<display-name>New</display-name>", portletXmlPage.getText() );
        assertContains( "<title>New</title>", portletXmlPage.getText() );
    }

    @Test
    public void portletModesAndResources()
    {
        // new portlet with more than two uppercase portlet class name
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "MyNewPortlet", null, null );
        newPortletPage.next();

        assertEquals( "my-new", specifyPortletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "My New", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "My New", specifyPortletDeploymentDescriptorPage.getPortletTitle() );
        assertEquals( "/html/mynew", specifyPortletDeploymentDescriptorPage.getJspFolder() );

        specifyPortletDeploymentDescriptorPage.setPortletName( "mynew" );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getPortletTitle() );
        assertEquals( "/html/mynew", specifyPortletDeploymentDescriptorPage.getJspFolder() );

        newPortletPage.back();

        // check and validate portlet class, dispaly name, title and jsp folder in wizard
        newPortletPage.createLiferayPortlet( TEXT_BLANK, "MyTestPortlet", null, null );
        newPortletPage.next();

        assertEquals( "mynew", specifyPortletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getPortletTitle() );
        assertEquals( "/html/mytest", specifyPortletDeploymentDescriptorPage.getJspFolder() );

        specifyPortletDeploymentDescriptorPage.setDisplayName( "Mynew1" );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getPortletTitle() );

        specifyPortletDeploymentDescriptorPage.setPortletName( TEXT_BLANK );

        assertEquals( TEXT_PORTLET_NAME_IS_EMPTY, specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        assertEquals( "Mynew1", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( TEXT_BLANK, specifyPortletDeploymentDescriptorPage.getPortletTitle() );

        specifyPortletDeploymentDescriptorPage.speficyPortletInfo( "my-new", "Mynew1", TEXT_BLANK );

        specifyPortletDeploymentDescriptorPage.setJspFolder( TEXT_BLANK );
        assertEquals( TEXT_JSP_FOLDER_CANNOT_EMPTY, specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        specifyPortletDeploymentDescriptorPage.setJspFolder( "test." );
        assertEquals( TEXT_FOLDER_VALUE_IS_INVALID, specifyPortletDeploymentDescriptorPage.getValidationMessage() );

        // relate ticket IDE-2158
        // specifyPortletDeploymentDescriptorPage.setJspFolder( "." );
        // assertEquals( TEXT_VIEW_JSP_EXSITS_AND_OVERWRITTEN,
        // specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        // specifyPortletDeploymentDescriptorPage.setJspFolder( ".." );
        // specifyPortletDeploymentDescriptorPage.back

        specifyPortletDeploymentDescriptorPage.specifyResources( true, "/myhtml/myjspfolder", true, TEXT_BLANK );
        assertEquals(
            TEXT_RESOURCE_BUNDLE_FILE_MUST_VALID_PATH, specifyPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyPortletDeploymentDescriptorPage.setResourceBundleFilePath( "content/Language.properties1" );
        assertEquals(
            TEXT_RESOURCE_BUNDLE_FILE_END_WITH_PROPERTIES,
            specifyPortletDeploymentDescriptorPage.getValidationMessage() );

        // relate ticket IDE-2159
        // specifyPortletDeploymentDescriptorPage.setResourceBundleFilePath( ".properties" );

        specifyPortletDeploymentDescriptorPage.setResourceBundleFilePath( "mycontent/Lang.properties" );
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyPortletDeploymentDescriptorPage.speficyPortletModes( true, true );
        specifyPortletDeploymentDescriptorPage.speficyLiferayPortletModes( true, true, true, true, true, true );

        newPortletPage.finish();

        String fileName = "portlet.xml";

        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );
        TextEditorPO portletXmlPage = eclipse.getTextEditor( fileName );

        // check codes generate in portlet.xml file
        assertContains( "<portlet-name>my-new</portlet-name>", portletXmlPage.getText() );
        assertContains( "<display-name>Mynew1</display-name>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/view.jsp</value>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/edit.jsp</value>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/help.jsp</value>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/about.jsp</value>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/config.jsp</value>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/edit-defaults.jsp</value>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/edit-guest.jsp</value>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/preview.jsp</value>", portletXmlPage.getText() );
        assertContains( "<value>/myhtml/myjspfolder/print.jsp</value>", portletXmlPage.getText() );

        assertContains( "<portlet-mode>view</portlet-mode>", portletXmlPage.getText() );
        assertContains( "<portlet-mode>edit</portlet-mode>", portletXmlPage.getText() );
        assertContains( "<portlet-mode>help</portlet-mode>", portletXmlPage.getText() );
        assertContains( "<portlet-mode>about</portlet-mode>", portletXmlPage.getText() );
        assertContains( "<portlet-mode>config</portlet-mode>", portletXmlPage.getText() );
        assertContains( "<portlet-mode>edit_defaults</portlet-mode>", portletXmlPage.getText() );
        assertContains( "<portlet-mode>edit_guest</portlet-mode>", portletXmlPage.getText() );
        assertContains( "<portlet-mode>preview</portlet-mode>", portletXmlPage.getText() );
        assertContains( "<portlet-mode>print</portlet-mode>", portletXmlPage.getText() );

        assertContains( "<resource-bundle>mycontent.Lang</resource-bundle>", portletXmlPage.getText() );
        assertContains( "<title></title>", portletXmlPage.getText() );

        // check language file

        fileName = "Lang.properties";
        projectTree.expandNode( "test-portlet", "docroot/WEB-INF/src", "mycontent" );

        TreeItemPO LangProperties =
            new TreeItemPO( bot, projectTree, "test-portlet", "docroot/WEB-INF/src", "mycontent", fileName );
        assertTrue( LangProperties.isVisible() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "MyPortletPortlet", null, "javax.portlet.GenericPortlet" );
        newPortletPage.next();

        assertEquals( "my-portlet", specifyPortletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "My Portlet", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "My Portlet", specifyPortletDeploymentDescriptorPage.getPortletTitle() );

        assertFalse( specifyPortletDeploymentDescriptorPage.getAboutCheckBox().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getConfigCheckBox().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getEditDefaultsCheckBox().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getEditGuestCheckBox().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getPreviewCheckBox().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getPrintCheckBox().isEnabled() );

        newPortletPage.finish();

        assertContains(
            "<init-param>\n\t\t\t<name>view-template</name>\n\t\t\t<value>/html/myportlet/view.jsp</value>\n\t\t</init-param>\n\t\t<expiration-cache>0</expiration-cache>\n\t\t<supports>\n\t\t\t<mime-type>text/html</mime-type>\n\t\t\t<portlet-mode>view</portlet-mode>\n\t\t</supports>\n\t\t<portlet-info>\n\t\t\t<title>My Portlet</title>\n\t\t\t<short-title>My Portlet</short-title>\n\t\t\t<keywords></keywords>\n\t\t</portlet-info>",
            portletXmlPage.getText() );
    }

    @Before
    public void preparePortletPlguinProject() throws Exception
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        Boolean hasProject = addedProjects();

        eclipse.getNewToolbar().getLiferayPluginProject().click();

        newLiferayProjectPage.createSDKProject( "test", "Portlet", true, false );

        if( !hasProject )
        {
            newLiferayProjectPage.next();

            newLiferayProjectPage.next();

            SetSDKLocationPO setSdkPage = new SetSDKLocationPO( bot );

            setSdkPage.setSdkLocation( getLiferayPluginsSdkDir().toString() );
        }
        sleep( 4000 );
        newLiferayProjectPage.finish();
        sleep( 20000 );

    }

    @Test
    public void sourceFolderTest()
    {
        String fileName = "portlet.xml";
        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();

        sleep();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        TextEditorPO portletEditor = eclipse.getTextEditor( fileName );
        TreeItemPO portletXmlItem =
            new TreeItemPO( bot, projectTree, "test-portlet", "docroot", "WEB-INF", "portlet.xml" );

        // new source folder
        portletXmlItem.doAction( "Open" );
        assertTrue( portletEditor.isActive() );

        portletXmlItem.doAction( "New", "Other..." );
        SelectTypePO newTypePage = new SelectTypePO( bot, INDEX_SELECT_A_WIZARD_VALIDATION_MESSAGE );

        newTypePage.selectItem( "Source Folder", "Java", "Source Folder" );
        assertEquals( "Create a Java source folder", newTypePage.getValidationMessage() );
        newTypePage.next();

        NewSourceFolderPO newSourceFolderPage =
            new NewSourceFolderPO( bot, "New Source Folder", INDEX_NEW_SOURCE_FOLDER_VALIDATION_MESSAGE );

        assertEquals( "test-portlet", newSourceFolderPage.getProjectNameText() );
        assertEquals( TEXT_CREATE_A_NEW_SOURCE_FOLDER, newSourceFolderPage.getValidationMessage() );

        newSourceFolderPage.newSourceFolder( "mysrc" );
        newSourceFolderPage.finish();

        // source folder validation tests
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.setSourceFolderText( TEXT_BLANK );
        assertEquals( TEXT_SOURCE_FOLDER_CANNOT_BE_EMPTY, newPortletPage.getValidationMessage() );

        newPortletPage.setSourceFolderText( "123" );
        assertEquals( TEXT_SOUCCE_FOLDER_MUST_BE_ABSOLUTE_PATH, newPortletPage.getValidationMessage() );

        newPortletPage.get_browseSourceButton().click();
        SelectionDialogPO browseSourceFolderPage = new SelectionDialogPO( bot, "Container Selection", 0 );

        assertEquals( "Choose a Container:", browseSourceFolderPage.getDialogLabel() );
        assertFalse( browseSourceFolderPage.confirmButton().isEnabled() );
        assertTrue( browseSourceFolderPage.cancelButton().isEnabled() );

        browseSourceFolderPage.getSelcetFileTree().selectTreeItem( "test-portlet", "docroot" );
        assertTrue( browseSourceFolderPage.confirmButton().isEnabled() );
        assertTrue( browseSourceFolderPage.cancelButton().isEnabled() );

        browseSourceFolderPage.confirm();

        assertEquals( TEXT_NOT_A_JAVA_SOURCE_FOLDER, newPortletPage.getValidationMessage() );

        newPortletPage.get_browseSourceButton().click();
        sleep();
        browseSourceFolderPage.getSelcetFileTree().selectTreeItem( "test-portlet", "mysrc" );
        browseSourceFolderPage.confirm();
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.finish();

        TreeItemPO mysrcTreeItem =
            new TreeItemPO( bot, projectTree, "test-portlet", "mysrc", "com.test", "NewPortlet.java" );

        assertTrue( mysrcTreeItem.isVisible() );
    }

    @Test
    public void specifyPortletClassPageInitialState()
    {

        // check specfy modifier, interface and method stubs using GenericPortlet superclass
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();;

        newPortletPage.next();
        newPortletPage.next();

        newPortletPage.next();

        // check initial state
        ModifiersInterfacesMethodStubsPO modifiersInterfacesMethodStubsPage =
            new ModifiersInterfacesMethodStubsPO( bot, INDEX_SPECIFY_PARAMS_IN_PORTLET_CLASS_PAGE );
        assertEquals(
            TEXT_SPECIFY_STUBS_TO_GENERATE_IN_PORTLET_CLASS,
            modifiersInterfacesMethodStubsPage.getValidationMessage() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_publicCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_publicCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_abstractCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_abstractCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_finalCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_finalCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_constrcutFromSuperClassCheckbox().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.get_constrcutFromSuperClassCheckbox().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_inheritedAbstractMethodsCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_inheritedAbstractMethodsCheckbox().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_initCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_initCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_destoryCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_destoryCheckbox().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.get_doViewCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doViewCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doHelpCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doHelpCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doAboutCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doAboutCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doConfigCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doConfigCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditDefaultsCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditDefaultsCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditGuestCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doEditGuestCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doPreviewCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doPreviewCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_doPrintCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_doPrintCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_processActionCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_processActionCheckbox().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.get_serveResourceCheckbox().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.get_serveResourceCheckbox().isEnabled() );

        newPortletPage.finish();
    }

    @Test
    public void superClassTest()
    {

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        // superclass tests
        newPortletPage.setSuperClassCombobox( TEXT_BLANK );
        assertEquals( TEXT_MUST_SPECIFY_A_PORTLET_SUPERCLASS, newPortletPage.getValidationMessage() );

        newPortletPage.setSuperClassCombobox( "MyClass123" );
        assertEquals( TEXT_SUPERCLASS_MUST_BE_VALID, newPortletPage.getValidationMessage() );

        newPortletPage.setSuperClassCombobox( "com.test.NewPortlet" );
        assertEquals( TEXT_SUPERCLASS_MUST_BE_VALID, newPortletPage.getValidationMessage() );

        newPortletPage.setSuperClassCombobox( "com.liferay.portal.kernel.portlet.LiferayPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.get_browseSuperclassButton().click();

        SuperclassSelectionPO selectSuperclassPage = new SuperclassSelectionPO( bot, "Superclass Selection", 0 );

        assertEquals( "Choose a superclass:", selectSuperclassPage.getDialogLabel() );

        selectSuperclassPage.clickSuperclass( 0 );
        selectSuperclassPage.confirm();

        assertEquals( "com.liferay.util.bridges.bsf.BaseBSFPortlet", newPortletPage.getSuperClassCombobox() );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.setSuperClassCombobox( "javax.portlet.GenericPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.finish();

        TextEditorPO portletJavaPage = new TextEditorPO( bot, "NewPortlet.java" );

        assertContains( "public class NewPortlet extends GenericPortlet", portletJavaPage.getText() );
        assertTrue( portletJavaPage.isActive() );

        eclipse.getNewToolbar().menuClick( LABEL_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( "test-portlet", "MySecondPortlet", null, "com.test.NewPortlet" );
        newPortletPage.finish();

        TextEditorPO mySecondPortletJavaPage = new TextEditorPO( bot, "MySecondPortlet.java" );

        assertContains( "public class MySecondPortlet extends NewPortlet", mySecondPortletJavaPage.getText() );

        eclipse.getNewToolbar().menuClick( LABEL_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( "test-portlet" );

        assertEquals( " Type 'com.test.NewPortlet'" + TEXT_ALREADY_EXISTS, newPortletPage.getValidationMessage() );

        newPortletPage.cancel();
    }

}
