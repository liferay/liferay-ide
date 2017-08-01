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

import com.liferay.ide.swtbot.liferay.ui.LiferayPortletWizardUI;
import com.liferay.ide.swtbot.liferay.ui.SWTBotBase;
import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.InterfaceSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.PackageSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.SuperClassSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.CreateLiferayPortletWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.LiferayPortletDeploymentDescriptorWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ModifiersInterfacesMethodStubsWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewSdkProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewSourceFolderWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.PortletDeploymentDescriptorWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.SelectTypeWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.SetSDKLocationWizard;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.SelectionDialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

/**
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public class LiferayPortletWizardTests extends SWTBotBase implements LiferayPortletWizardUI, WizardUI
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    NewSdkProjectWizard newLiferayProjectPage =
        new NewSdkProjectWizard( bot, LABEL_NEW_LIFERAY_PLUGIN_PROJECT, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_WIZARD );

    CreateLiferayPortletWizard newPortletPage =
        new CreateLiferayPortletWizard( bot, TITLE_NEW_LIFERAY_PORTLET, INDEX_DEFAULT_CREATE_LIFERAY_PORTLET_WIZARD );

    LiferayPortletDeploymentDescriptorWizard specifyLiferayPortletDeploymentDescriptorPage =
        new LiferayPortletDeploymentDescriptorWizard( bot, INDEX_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_PAGE );

    PortletDeploymentDescriptorWizard specifyPortletDeploymentDescriptorPage =
        new PortletDeploymentDescriptorWizard( bot, INDEX_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_PAGE );

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        ide.getLiferayPerspective().activate();

        unzipPluginsSDK();
        unzipServer();
    }

    @After
    public void cleanAll()
    {
        if( addedProjects() )
        {
            ide.getProjectTree().setFocus();

            ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
        }

        sleep( 3000 );

        try
        {
            ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
        }
        catch( Exception e )
        {
        }

        ide.closeShell( LABEL_NEW_LIFERAY_PLUGIN_PROJECT );
        ide.closeShell( LABEL_NEW_LIFERAY_PORTLET );
    }

    @Test
    public void createPorltetWithoutLiferayProjects()
    {

        if( addedProjects() )
        {
            ide.getPackageExporerView().deleteResouceByName( "test-portlet", true );
        }

        String projectName = "liferayProject";

        // click new liferay portlet wizard without projects
        ide.getNewBtn().getLiferayPortlet().click();

        Dialog dialogPage1 = new Dialog( bot, TITLE_NEW_LIFERAY_PORTLET, NO, YES );

        dialogPage1.confirm();

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayProjectPage.getValidationMsg() );
        assertFalse( newLiferayProjectPage.nextBtn().isEnabled() );

        newLiferayProjectPage.createSDKPortletProject( projectName );
        sleep();
        assertTrue( newLiferayProjectPage.nextBtn().isEnabled() );

        newLiferayProjectPage.cancel();

        Dialog dialogPage2 = new Dialog( bot, TITLE_NEW_LIFERAY_PORTLET, YES, NO );

        dialogPage2.confirm();

        try
        {
            if( !newPortletPage.getValidationMsg().equals( TEXT_ENTER_A_PROJECT_NAME ) )
            {
                CreateLiferayPortletWizard myNewPortletPage =
                    new CreateLiferayPortletWizard( bot, LABEL_NEW_LIFERAY_PORTLET, 1 );
                assertEquals( TEXT_ENTER_A_PROJECT_NAME, myNewPortletPage.getValidationMsg() );
                myNewPortletPage.cancel();
            }
            newPortletPage.cancel();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        // new Java project
        ide.getFileMenu().clickMenu( MENU_NEW, MENU_PROJECT );

        createProject(
            "Java", "Java", LABEL_JAVA_PROJECT, "JavaExample", TEXT_CREATE_A_JAVA_PROJECT,
            TEXT_CREATE_A_JAVA_PROJECT_IN_WORKSPACE, INDEX_CREATE_A_PROJECT_THROUGH_NEW_WIZARD_PAGE );
        try
        {
            Dialog dialogPage3 = new Dialog( bot, "Show In Package Explorer", YES, NO );
            dialogPage3.confirm();
        }
        catch( Exception e )
        {
        }

        // new general project
        ide.getFileMenu().clickMenu( MENU_NEW, MENU_PROJECT );

        createProject(
            "project", "General", "Project", "GeneralExample", TEXT_CREATE_A_NEW_PROJECT_RESOURCE,
            TEXT_CREATE_A_NEW_PROJECT_RESOURCE + '.', INDEX_CREATE_A_PROJECT_THROUGH_NEW_WIZARD_PAGE );

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        Dialog dialogPage4 = new Dialog( bot, "New Liferay Portlet", YES, NO );

        dialogPage4.confirm();

        // IDE-2425
        try
        {
            newLiferayProjectPage.cancel();
        }
        catch( Exception e )
        {
        }

        ide.getNewBtn().getLiferayPortlet().click();

        Dialog dialogPage5 = new Dialog( bot, "New Liferay Portlet", NO, YES );

        dialogPage5.confirm();

        newLiferayProjectPage.createSDKPortletProject( projectName );
        newLiferayProjectPage.finish();

        try
        {
            assertTrue( checkServerConsoleMsg( "BUILD SUCCESSFUL", "Java", 30000 ) );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        newPortletPage.waitForPageToOpen();

        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMsg() );
        assertEquals( "liferayProject-portlet", newPortletPage.getPortletPluginProjects().getText() );
        assertTrue( newPortletPage.getNewPortlet().isSelected() );
        assertEquals( "com.liferay.util.bridges.mvc.MVCPortlet", newPortletPage.getSuperClasses().getText() );

        newPortletPage.finish();
    }

    public void createProject(
        String filterText, String projectTypeTree, String projectTypeNode, String projectName, String validateMsg1,
        String validateMsg2, int validationIndex )
    {

        SelectTypeWizard newProjectPage = new SelectTypeWizard( bot, validationIndex );

        newProjectPage.selectItem( filterText, projectTypeTree, projectTypeNode );
        assertEquals( validateMsg1, newProjectPage.getValidationMsg() );
        newProjectPage.next();

        NewProjectWizard newJavaProjectPage = new NewProjectWizard( bot, INDEX_NEW_SOURCE_FOLDER_VALIDATION_MESSAGE );

        newJavaProjectPage.createJavaProject( projectName );
        assertEquals( validateMsg2, newJavaProjectPage.getValidationMsg() );

        newJavaProjectPage.finish();

        if( !projectTypeNode.equals( "Project" ) )
        {
            Dialog dialogPage = new Dialog( bot, "Open Associated Perspective", YES, NO );

            dialogPage.confirm();
        }
    }

    @Test
    public void javaPackageTest()
    {

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        // java package tests
        newPortletPage.getJavaPackage().setText( "123" );

        assertEquals(
            TEXT_INVALID_JAVA_PACKAGE_NAME + "'123'" + TEXT_NOT_A_VALID_JAVA_IDENTIFIER,
            newPortletPage.getValidationMsg() );

        newPortletPage.getJavaPackage().setText( ".." );
        assertEquals(
            TEXT_INVALID_JAVA_PACKAGE_NAME + TEXT_PACKAGE_NAME_CANNOT_END_WITH_DOT, newPortletPage.getValidationMsg() );

        newPortletPage.getJavaPackage().setText( "MyPackage" );
        assertEquals( TEXT_JAVA_PACKAGE_START_WITH_AN_UPPERCASE_LETTER, newPortletPage.getValidationMsg() );

        newPortletPage.getBrowsePackageBtn().click();

        PackageSelectionDialog selectPackagePage = new PackageSelectionDialog( bot, "Package Selection", 0 );

        assertEquals( "Choose a package:", selectPackagePage.getDialogLabel( 0 ) );
        // selectPackagePage.getAvailablePackages().click( 0 );

        selectPackagePage.confirm();

        assertEquals( TEXT_BLANK, newPortletPage.getJavaPackage().getText() );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMsg() );

        newPortletPage.getJavaPackage().setText( "myPackage" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMsg() );

        newPortletPage.finish();

        Editor portletJavaPage = new Editor( bot, "NewPortlet.java" );

        assertContains( "package myPackage;", portletJavaPage.getText() );

        assertTrue( portletJavaPage.isActive() );
        portletJavaPage.setFocus();
        // keyPress.pressShortcut( ctrl, N );

        ide.getFileMenu().clickMenu( MENU_NEW, "Other..." );

        SelectTypeWizard newSelectLiferayPage = new SelectTypeWizard( bot, INDEX_SELECT_A_WIZARD_VALIDATION_MESSAGE );

        newSelectLiferayPage.selectItem( "liferay", "Liferay", LABEL_LIFERAY_PORTLET );
        newSelectLiferayPage.next();
        newPortletPage.createLiferayPortlet( "test-portlet", "MySecondPortlet", TEXT_BLANK, null );
        newPortletPage.finish();

        Editor mySecondPortletJavaPage = new Editor( bot, "MySecondPortlet.java" );

        assertFalse( mySecondPortletJavaPage.getText().contains( "package" ) );

    }

    @Test
    public void launchNewPortletInitialTest()
    {
        ide.getNewBtn().getLiferayPluginProject().click();

        assertEquals( "Portlet", newLiferayProjectPage.getPluginTypes().getText() );

        String projectName = "mytest";
        String pluginType = "Portlet";

        newLiferayProjectPage.createSDKProject( projectName, pluginType, false, true );

        newLiferayProjectPage.finish();

        newPortletPage.waitForPageToOpen();

        // check initial state
        assertEquals( projectName + "-portlet", newPortletPage.getPortletPluginProjects().getText() );
        assertEquals( "/mytest-portlet/docroot/WEB-INF/src", newPortletPage.getSourceFolder().getText() );
        assertEquals( "NewPortlet", newPortletPage.getPortletClass().getText() );
        assertEquals( "com.test", newPortletPage.getJavaPackage().getText() );
        assertEquals( "com.liferay.util.bridges.mvc.MVCPortlet", newPortletPage.getSuperClasses().getText() );
        assertTrue( newPortletPage.getNewPortlet().isSelected() );
        assertFalse( newPortletPage.getUseDefault().isSelected() );
        assertTrue( Arrays.equals( newPortletPage.getAvailableSuperClasses(), availableSuperclasses ) );

        newPortletPage.createLiferayPortlet( true );

        assertFalse( newPortletPage.getPortletClass().isEnabled() );
        assertFalse( newPortletPage.getJavaPackage().isEnabled() );
        assertFalse( newPortletPage.getSuperClasses().isEnabled() );

        newPortletPage.finish();

        // check editor and generated files
        String fileName = "view.jsp";
        Editor viewEditor = ide.getEditor( fileName );
        assertTrue( viewEditor.isActive() );

        fileName = "portlet.xml";

        Tree projectTree = ide.showPackageExporerView().getProjectTree();
        sleep();
        projectTree.expandNode( "mytest-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        Editor portletEditor = ide.getEditor( fileName );

        assertContains( "<portlet-name>new</portlet-name>", portletEditor.getText() );
        assertContains( "com.liferay.util.bridges.mvc.MVCPortlet", portletEditor.getText() );
    }

    @Test
    public void liferayDisplay()
    {

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "NewPortletOne", null, null );

        newPortletPage.next();
        newPortletPage.next();

        assertEquals(
            "new-portlet-one-portlet", specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapper().getText() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayPortletInfo(
            TEXT_BLANK, true, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        // display category tests
        specifyLiferayPortletDeploymentDescriptorPage.setDisplayCategoryCombobox( TEXT_BLANK );
        assertEquals( TEXT_CATEGORY_NAME_IS_EMPTY, specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.setDisplayCategoryCombobox( "my1category" );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, true, null, null, true, null );

        assertTrue(
            Arrays.equals(
                availableEntryCategories70,
                specifyLiferayPortletDeploymentDescriptorPage.getEntryCategory().items() ) );
        assertEquals( "1.5", specifyLiferayPortletDeploymentDescriptorPage.getEntryWeight().getText() );
        assertEquals(
            "NewPortletOneControlPanelEntry", specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().getText() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.getEntryCategory().isEnabled() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.getEntryWeight().isEnabled() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().isEnabled() );

        newPortletPage.finish();

        String fileName = "liferay-portlet.xml";

        Tree porjectTree = ide.showPackageExporerView().getProjectTree();
        porjectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        Editor liferayPortletEditor = new Editor( bot, fileName );

        assertContains(
            "<portlet-name>new-portlet-one</portlet-name>\n\t\t<icon></icon>\n\t\t<control-panel-entry-category>my</control-panel-entry-category>\n\t\t<control-panel-entry-weight>1.5</control-panel-entry-weight>\n\t\t<control-panel-entry-class>\n\t\t\tcom.test.NewPortletOneControlPanelEntry\n\t\t</control-panel-entry-class>\n\t\t<instanceable>true</instanceable>\n\t\t<header-portlet-css></header-portlet-css>\n\t\t<footer-portlet-javascript></footer-portlet-javascript>\n\t\t<css-class-wrapper></css-class-wrapper>",
            liferayPortletEditor.getText() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "NewPortletSecond", null, null );
        newPortletPage.next();

        PortletDeploymentDescriptorWizard portletDeploymentDescriptorPage =
            new PortletDeploymentDescriptorWizard( bot );

        portletDeploymentDescriptorPage.speficyPortletInfo( "new-portlet-second2", null, null );
        newPortletPage.next();

        assertEquals(
            "new-portlet-second2-portlet",
            specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapper().getText() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayPortletInfo(
            "unexistentIcon", false, "unexistentCss", "unexistentJavaScript", null );

        assertTrue(
            isInAvailableLists(
                specifyLiferayPortletDeploymentDescriptorPage.getDisplayCategory().items(), "my1category" ) );

        // entry tests after checked add to control panel
        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, true, null, null, true, null );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryCategoryCombobox( TEXT_BLANK );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.getEntryWeight().setText( TEXT_BLANK );
        assertEquals(
            TEXT_MUST_SPECIFY_VALID_ENTRY_WEIGHT, specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.getEntryWeight().setText( "**" );
        assertEquals(
            TEXT_MUST_SPECIFY_VALID_ENTRY_WEIGHT, specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.getEntryWeight().setText( ".1" );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().setText( TEXT_BLANK );
        assertEquals(
            TEXT_CLASS_NAME_CANNOT_BE_EMPTY, specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().setText( "." );
        assertEquals(
            TEXT_DONOT_USE_QUALIDIED_CLASS_NAME, specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().setText( "**" );
        assertEquals(
            TEXT_INVALID_JAVA_CLASS_NAME + "'**'" + TEXT_NOT_A_VALID_IDENTIFIER,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().setText( "aA" );
        assertEquals(
            TEXT_JAVA_TYPE_START_WITH_AN_UPPERCASE_LETTER,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().setText( "MyEntryClass" );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, false, null, null, true, null );

        newPortletPage.finish();

        assertContains(
            "<portlet-name>new-portlet-second2</portlet-name>\n\t\t<icon>unexistentIcon</icon>\n\t\t<header-portlet-css>unexistentCss</header-portlet-css>\n\t\t<footer-portlet-javascript>\n\t\t\tunexistentJavaScript\n\t\t</footer-portlet-javascript>\n\t\t<css-class-wrapper>\n\t\t\tnew-portlet-second2-portlet\n\t\t</css-class-wrapper>",
            liferayPortletEditor.getText() );

        // add to control panel and create entry class tests
        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

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

        Tree projectTree = ide.showPackageExporerView().getProjectTree();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        Editor liferayDisplayEditor = new Editor( bot, fileName );

        assertContains(
            "<display>\n\t<category name=\"category.sample\">\n\t\t<portlet id=\"test\" />\n\t\t<portlet id=\"new-portlet-second2\"></portlet>\n\t</category>\n\t<category name=\"my1category\">\n\t\t<portlet id=\"new-portlet-one\"></portlet>\n\t</category>\n\t<category name=\"category.tools\">\n\t\t<portlet id=\"new-portlet-third\"></portlet>\n\t</category>\n</display>",
            liferayDisplayEditor.getText() );

        projectTree.expandNode( "test-portlet", "docroot/WEB-INF/src", "(default package)" );

        TreeItem NewPortletThirdControlPanelEntryJavaFile = new TreeItem(
            bot, projectTree, "test-portlet", "docroot/WEB-INF/src", "(default package)",
            "NewPortletThirdControlPanelEntry.java" );
        assertTrue( NewPortletThirdControlPanelEntryJavaFile.isVisible() );

        TreeItem NewPortletOneControlPanelEntryJavaFile = new TreeItem(
            bot, projectTree, "test-portlet", "docroot/WEB-INF/src", "com.test",
            "NewPortletOneControlPanelEntry.java" );
        assertTrue( NewPortletOneControlPanelEntryJavaFile.isVisible() );

    }

    @Test
    public void liferayPorletInfo()
    {
        // browse icon tests
        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet(
            TEXT_BLANK, "NewPortletPortletPortlet", "anotherJavaPackage",
            "com.liferay.portal.kernel.portlet.LiferayPortlet" );
        newPortletPage.next();

        assertEquals( "new-portlet-portlet", specifyPortletDeploymentDescriptorPage.getPortletName().getText() );
        assertEquals( "New Portlet Portlet", specifyPortletDeploymentDescriptorPage.getDisplayName().getText() );
        assertEquals( "New Portlet Portlet", specifyPortletDeploymentDescriptorPage.getPortletTitle().getText() );
        assertEquals( "/html/newportletportlet", specifyPortletDeploymentDescriptorPage.getJspFolder().getText() );

        newPortletPage.next();

        assertEquals(
            "new-portlet-portlet-portlet",
            specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapper().getText() );

        specifyLiferayPortletDeploymentDescriptorPage.getBrowseIconBtn().click();

        SelectionDialog iconSelectPage = new SelectionDialog( bot, "Icon Selection" );

        assertEquals( "Choose an icon file:", iconSelectPage.getDialogLabel( 0 ) );
        assertFalse( iconSelectPage.confirmBtn().isEnabled() );
        assertTrue( iconSelectPage.cancelBtn().isEnabled() );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( iconSelectPage.confirmBtn().isEnabled() );
        assertTrue( iconSelectPage.cancelBtn().isEnabled() );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "js", "main.js" );
        assertTrue( iconSelectPage.cancelBtn().isEnabled() );
        iconSelectPage.confirm();

        assertEquals( "/js/main.js", specifyLiferayPortletDeploymentDescriptorPage.getIcon().getText() );

        // browse css tests
        specifyLiferayPortletDeploymentDescriptorPage.getBrowseCssBtn().click();

        SelectionDialog cssSelectPage = new SelectionDialog( bot, "CSS Selection" );

        assertEquals( "Choose a css file:", cssSelectPage.getDialogLabel( 0 ) );
        assertFalse( cssSelectPage.confirmBtn().isEnabled() );
        assertTrue( cssSelectPage.cancelBtn().isEnabled() );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( cssSelectPage.confirmBtn().isEnabled() );
        assertTrue( cssSelectPage.cancelBtn().isEnabled() );

        cssSelectPage.getSelcetFileTree().selectTreeItem( "view.jsp" );
        assertTrue( cssSelectPage.cancelBtn().isEnabled() );
        cssSelectPage.confirm();

        assertEquals( "/view.jsp", specifyLiferayPortletDeploymentDescriptorPage.getCss().getText() );

        // browse javaScript tests
        specifyLiferayPortletDeploymentDescriptorPage.getBrowseJavaScriptBtn().click();

        SelectionDialog javaScriptSelectPage = new SelectionDialog( bot, "JavaScript Selection" );

        assertEquals( "Choose a javascript file:", javaScriptSelectPage.getDialogLabel( 0 ) );
        assertFalse( javaScriptSelectPage.confirmBtn().isEnabled() );
        assertTrue( javaScriptSelectPage.cancelBtn().isEnabled() );

        javaScriptSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( javaScriptSelectPage.confirmBtn().isEnabled() );
        assertTrue( javaScriptSelectPage.cancelBtn().isEnabled() );

        javaScriptSelectPage.getSelcetFileTree().selectTreeItem( "view.jsp" );
        assertTrue( javaScriptSelectPage.cancelBtn().isEnabled() );
        javaScriptSelectPage.confirm();

        assertEquals( "/view.jsp", specifyLiferayPortletDeploymentDescriptorPage.getJavaScript().getText() );

        newPortletPage.finish();

        String fileName = "liferay-portlet.xml";

        Tree projectTree = ide.showPackageExporerView().getProjectTree();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        Editor liferayPortletEditor = new Editor( bot, fileName );

        assertContains(
            "<portlet-name>new-portlet-portlet</portlet-name>\n\t\t<icon>/js/main.js</icon>\n\t\t<header-portlet-css>/view.jsp</header-portlet-css>\n\t\t<footer-portlet-javascript>/view.jsp</footer-portlet-javascript>\n\t\t<css-class-wrapper>\n\t\t\tnew-portlet-portlet-portlet\n\t\t</css-class-wrapper>",
            liferayPortletEditor.getText() );
    }

    @Test
    public void liferayPortletDeploymentDescriptorDefaultTest()
    {
        Tree porjectTree = ide.showPackageExporerView().getProjectTree();

        String fileName = "liferay-portlet.xml";
        porjectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        Editor liferayPortletEditor = new Editor( bot, fileName );

        assertContains(
            "<portlet-name>test</portlet-name>\n\t\t<icon>/icon.png</icon>\n\t\t<header-portlet-css>/css/main.css</header-portlet-css>\n\t\t<footer-portlet-javascript>/js/main.js</footer-portlet-javascript>\n\t\t<css-class-wrapper>test-portlet</css-class-wrapper>",
            liferayPortletEditor.getText() );

        // new liferay portlet and go to speficy liferay portlet deployment descriptor page

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.next();
        newPortletPage.next();

        // check initial state
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMsg() );

        assertEquals( "/icon.png", specifyLiferayPortletDeploymentDescriptorPage.getIcon().getText() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.getAddToControlPanel().isChecked() );
        assertEquals( "/css/main.css", specifyLiferayPortletDeploymentDescriptorPage.getCss().getText() );
        assertEquals( "/js/main.js", specifyLiferayPortletDeploymentDescriptorPage.getJavaScript().getText() );
        assertEquals( "new-portlet", specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapper().getText() );

        assertEquals( "Sample", specifyLiferayPortletDeploymentDescriptorPage.getDisplayCategory().getText() );
        assertTrue(
            Arrays.equals(
                specifyLiferayPortletDeploymentDescriptorPage.getDisplayCategory().items(),
                availableDisplayCategories70 ) );

        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.getAddToControlPanel().isEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.getEntryCategory().isEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.getEntryWeight().isEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.getCreateEntryClass().isEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().isEnabled() );

        assertEquals(
            "My Account Administration", specifyLiferayPortletDeploymentDescriptorPage.getEntryCategory().getText() );
        assertEquals( "1.5", specifyLiferayPortletDeploymentDescriptorPage.getEntryWeight().getText() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.getCreateEntryClass().isChecked() );
        assertEquals(
            "NewPortletControlPanelEntry", specifyLiferayPortletDeploymentDescriptorPage.getEntryClass().getText() );

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
        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();;

        newPortletPage.createLiferayPortlet( TEXT_BLANK, null, null, "javax.portlet.GenericPortlet" );

        newPortletPage.next();
        newPortletPage.next();

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, true, null, null, true, null );

        newPortletPage.next();

        // check initial state
        ModifiersInterfacesMethodStubsWizard modifiersInterfacesMethodStubsPage =
            new ModifiersInterfacesMethodStubsWizard( bot, INDEX_SPECIFY_PARAMS_IN_PORTLET_CLASS_PAGE );
        assertEquals(
            TEXT_SPECIFY_STUBS_TO_GENERATE_IN_PORTLET_CLASS, modifiersInterfacesMethodStubsPage.getValidationMsg() );

        assertTrue( modifiersInterfacesMethodStubsPage.getIsPublic().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getIsPublic().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getIsAbstract().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getIsAbstract().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getIsFinal().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getIsFinal().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getConstrcutFromSuperClass().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getConstrcutFromSuperClass().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.getInheritedAbstractMethods().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getInheritedAbstractMethods().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.getInit().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getInit().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDestory().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getDestory().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.getDoView().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoView().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoEdit().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getDoEdit().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoHelp().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getDoHelp().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoAbout().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoAbout().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoConfig().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoConfig().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoEditDefaults().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoEditDefaults().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoEditGuest().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoEditGuest().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoPreview().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoPreview().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoPrint().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoPrint().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getProcessAction().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getProcessAction().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getServeResource().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getServeResource().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.getAddBtn().isEnabled() );
        assertFalse( modifiersInterfacesMethodStubsPage.getRemoveBtn().isEnabled() );

        modifiersInterfacesMethodStubsPage.getIsFinal().select();
        modifiersInterfacesMethodStubsPage.getServeResource().select();

        modifiersInterfacesMethodStubsPage.getAddBtn().click();

        // click Add button to add interface and tests
        InterfaceSelectionDialog selectInterfacePage = new InterfaceSelectionDialog( bot, "Interface Selection" );

        selectInterfacePage.getItemToOpen().setText( "acceptor" );
        selectInterfacePage.getMatchItems().click( 0 );
        selectInterfacePage.confirm();

        modifiersInterfacesMethodStubsPage.getInterfaces().click( 0 );
        modifiersInterfacesMethodStubsPage.getRemoveBtn().click();

        assertFalse( modifiersInterfacesMethodStubsPage.getRemoveBtn().isEnabled() );

        modifiersInterfacesMethodStubsPage.getAddBtn().click();

        assertTrue( selectInterfacePage.confirmBtn().isEnabled() );
        selectInterfacePage.confirm();

        modifiersInterfacesMethodStubsPage.getConstrcutFromSuperClass().select();

        newPortletPage.finish();

        // check generate codes
        Editor newPortletJavaEditor = new Editor( bot, "NewPortlet.java" );

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

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        // portlet class tests
        newPortletPage.getPortletClass().setText( TEXT_BLANK );
        assertEquals( TEXT_CLASS_NAME_CANNOT_BE_EMPTY, newPortletPage.getValidationMsg() );

        newPortletPage.getPortletClass().setText( "123" );
        assertEquals(
            TEXT_INVALID_JAVA_CLASS_NAME + "'123'" + TEXT_NOT_A_VALID_IDENTIFIER, newPortletPage.getValidationMsg() );

        newPortletPage.getPortletClass().setText( "aaa" );
        assertEquals( TEXT_JAVA_TYPE_START_WITH_AN_UPPERCASE_LETTER, newPortletPage.getValidationMsg() );

        newPortletPage.getPortletClass().setText( "MyTestPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMsg() );

        newPortletPage.finish();

        Editor myPortletJavaPage = new Editor( bot, "MyTestPortlet.java" );

        assertContains( "class MyTestPortlet", myPortletJavaPage.getText() );
    }

    @Test
    public void portletDeploymentDescriptorWithoutSampleCode()
    {

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.finish();

        ide.getCreateLiferayProjectToolbar().getNewLiferayPluginProject().click();

        // create portlet project without sample and launch portlet wizard
        newLiferayProjectPage.createSDKProject( "test-second", "Portlet", false, false );
        newLiferayProjectPage.finish();

        // new liferay portlet wizard with default MVCPortlet
        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( "test-second-portlet", true );

        newPortletPage.next();

        assertTrue( specifyPortletDeploymentDescriptorPage.finishBtn().isEnabled() );
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMsg() );

        newPortletPage.back();
        newPortletPage.createLiferayPortlet( "test-portlet", true );

        assertFalse( newPortletPage.finishBtn().isEnabled() );

        newPortletPage.next();

        assertFalse( specifyPortletDeploymentDescriptorPage.finishBtn().isEnabled() );
        assertEquals( TEXT_PORTLET_NAME_ALREADY_EXISTS, specifyPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyPortletDeploymentDescriptorPage.getPortletName().setText( "New" );
        assertEquals( TEXT_VIEW_JSP_EXSITS_AND_OVERWRITTEN, specifyPortletDeploymentDescriptorPage.getValidationMsg() );
        assertTrue( specifyPortletDeploymentDescriptorPage.finishBtn().isEnabled() );

        specifyPortletDeploymentDescriptorPage.specifyResources( false, null, true, null );
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMsg() );
        assertTrue( specifyPortletDeploymentDescriptorPage.getResourceBundleFilePath().isEnabled() );

        newPortletPage.finish();

        String fileName = "portlet.xml";

        Tree projectTree = ide.showPackageExporerView().getProjectTree();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );
        Editor portletXmlPage = ide.getEditor( fileName );

        assertContains( "<portlet-name>New</portlet-name>", portletXmlPage.getText() );
        assertContains( "<resource-bundle>content.Language</resource-bundle>", portletXmlPage.getText() );

        fileName = "Language.properties";

        sleep( 2000 );
        projectTree.expandNode( "test-portlet", "docroot/WEB-INF/src", "content" );

        TreeItem languageProperties =
            new TreeItem( bot, projectTree, "test-portlet", "docroot/WEB-INF/src", "content", fileName );

        assertTrue( languageProperties.isVisible() );
    }

    @Test
    public void portletDeploymentDesriptorInitialState()
    {

        // relate ticket IDE-2156, regression for IDE-119
        Tree projectTree = ide.showPackageExporerView().getProjectTree();

        projectTree.expandNode( "test-portlet" );
        sleep( 5000 );
        projectTree.getTreeItem( "test-portlet" ).getTreeItem( "docroot", "WEB-INF", "liferay-display.xml" ).doAction(
            DELETE );

        Dialog deleteDialog = new Dialog( bot, "New Liferay Portlet", CANCEL, OK );
        deleteDialog.confirm();

        // new liferay portlet wizard
        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.next();

        // initial state check
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMsg() );
        assertEquals( "new", specifyPortletDeploymentDescriptorPage.getPortletName().getText() );
        assertEquals( "New", specifyPortletDeploymentDescriptorPage.getDisplayName().getText() );
        assertEquals( "New", specifyPortletDeploymentDescriptorPage.getPortletTitle().getText() );
        assertTrue( specifyPortletDeploymentDescriptorPage.getView().isChecked() );
        assertTrue( specifyPortletDeploymentDescriptorPage.getCreateJspFiles().isChecked() );
        assertEquals( "/html/new", specifyPortletDeploymentDescriptorPage.getJspFolder().getText() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getCreateResourceBundleFile().isChecked() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getResourceBundleFilePath().isEnabled() );
        assertEquals(
            "content/Language.properties",
            specifyPortletDeploymentDescriptorPage.getResourceBundleFilePath().getText() );

        newPortletPage.finish();

        // IDE-2156 treeItemPage.isVisible();

        // check generate codes and files
        Editor newPortletJavaPage = new Editor( bot, "NewPortlet.java" );
        assertTrue( newPortletJavaPage.isActive() );

        String fileName = "view.jsp";

        projectTree.expandNode( "test-portlet", "docroot", "html", "new" );
        TreeItem viewJsp = new TreeItem( bot, projectTree, "test-portlet", "docroot", "html", "new", fileName );

        assertTrue( viewJsp.isVisible() );

        fileName = "portlet.xml";
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        Editor portletXmlPage = ide.getEditor( "portlet.xml" );

        assertContains( "<portlet-name>new</portlet-name>", portletXmlPage.getText() );
        assertContains( "<display-name>New</display-name>", portletXmlPage.getText() );
        assertContains( "<title>New</title>", portletXmlPage.getText() );
    }

    @Test
    public void portletModesAndResources()
    {
        // new portlet with more than two uppercase portlet class name
        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "MyNewPortlet", null, null );
        newPortletPage.next();

        assertEquals( "my-new", specifyPortletDeploymentDescriptorPage.getPortletName().getText() );
        assertEquals( "My New", specifyPortletDeploymentDescriptorPage.getDisplayName().getText() );
        assertEquals( "My New", specifyPortletDeploymentDescriptorPage.getPortletTitle().getText() );
        assertEquals( "/html/mynew", specifyPortletDeploymentDescriptorPage.getJspFolder().getText() );

        specifyPortletDeploymentDescriptorPage.getPortletName().setText( "mynew" );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getDisplayName().getText() );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getPortletTitle().getText() );
        assertEquals( "/html/mynew", specifyPortletDeploymentDescriptorPage.getJspFolder().getText() );

        newPortletPage.back();

        // check and validate portlet class, dispaly name, title and jsp folder in wizard
        newPortletPage.createLiferayPortlet( TEXT_BLANK, "MyTestPortlet", null, null );
        newPortletPage.next();

        assertEquals( "mynew", specifyPortletDeploymentDescriptorPage.getPortletName().getText() );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getDisplayName().getText() );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getPortletTitle().getText() );
        assertEquals( "/html/mytest", specifyPortletDeploymentDescriptorPage.getJspFolder().getText() );

        specifyPortletDeploymentDescriptorPage.getDisplayName().setText( "Mynew1" );
        assertEquals( "Mynew", specifyPortletDeploymentDescriptorPage.getPortletTitle().getText() );

        specifyPortletDeploymentDescriptorPage.getPortletName().setText( TEXT_BLANK );

        assertEquals( TEXT_PORTLET_NAME_IS_EMPTY, specifyPortletDeploymentDescriptorPage.getValidationMsg() );
        assertEquals( "Mynew1", specifyPortletDeploymentDescriptorPage.getDisplayName().getText() );
        assertEquals( TEXT_BLANK, specifyPortletDeploymentDescriptorPage.getPortletTitle().getText() );

        specifyPortletDeploymentDescriptorPage.speficyPortletInfo( "my-new", "Mynew1", TEXT_BLANK );

        specifyPortletDeploymentDescriptorPage.getJspFolder().setText( TEXT_BLANK );
        assertEquals( TEXT_JSP_FOLDER_CANNOT_EMPTY, specifyPortletDeploymentDescriptorPage.getValidationMsg() );
        specifyPortletDeploymentDescriptorPage.getJspFolder().setText( "test." );
        assertEquals( TEXT_FOLDER_VALUE_IS_INVALID, specifyPortletDeploymentDescriptorPage.getValidationMsg() );

        // relate ticket IDE-2158
        // specifyPortletDeploymentDescriptorPage.getJspFolder().setText( "." );
        // assertEquals( TEXT_VIEW_JSP_EXSITS_AND_OVERWRITTEN,
        // specifyPortletDeploymentDescriptorPage.getValidationMsg() );
        // specifyPortletDeploymentDescriptorPage.getJspFolder().setText( ".." );
        // specifyPortletDeploymentDescriptorPage.back

        specifyPortletDeploymentDescriptorPage.specifyResources( true, "/myhtml/myjspfolder", true, TEXT_BLANK );
        assertEquals(
            TEXT_RESOURCE_BUNDLE_FILE_MUST_VALID_PATH, specifyPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyPortletDeploymentDescriptorPage.getResourceBundleFilePath().setText( "content/Language.properties1" );
        assertEquals(
            TEXT_RESOURCE_BUNDLE_FILE_END_WITH_PROPERTIES, specifyPortletDeploymentDescriptorPage.getValidationMsg() );

        // relate ticket IDE-2159
        // specifyPortletDeploymentDescriptorPage.getResourceBundleFilePath().setText( ".properties" );

        specifyPortletDeploymentDescriptorPage.getResourceBundleFilePath().setText( "mycontent/Lang.properties" );
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMsg() );

        specifyPortletDeploymentDescriptorPage.speficyPortletModes( true, true );
        specifyPortletDeploymentDescriptorPage.speficyLiferayPortletModes( true, true, true, true, true, true );

        newPortletPage.finish();

        String fileName = "portlet.xml";

        Tree projectTree = ide.showPackageExporerView().getProjectTree();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );
        Editor portletXmlPage = ide.getEditor( fileName );

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

        TreeItem LangProperties =
            new TreeItem( bot, projectTree, "test-portlet", "docroot/WEB-INF/src", "mycontent", fileName );
        assertTrue( LangProperties.isVisible() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "MyPortletPortlet", null, "javax.portlet.GenericPortlet" );
        newPortletPage.next();

        assertEquals( "my-portlet", specifyPortletDeploymentDescriptorPage.getPortletName().getText() );
        assertEquals( "My Portlet", specifyPortletDeploymentDescriptorPage.getDisplayName().getText() );
        assertEquals( "My Portlet", specifyPortletDeploymentDescriptorPage.getPortletTitle().getText() );

        assertFalse( specifyPortletDeploymentDescriptorPage.getAbout().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getConfig().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getEditDefaults().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getEditGuest().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getPreview().isEnabled() );
        assertFalse( specifyPortletDeploymentDescriptorPage.getPrint().isEnabled() );

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

        ide.getNewBtn().getLiferayPluginProject().click();

        newLiferayProjectPage.createSDKProject( "test", "Portlet", true, false );

        if( !hasProject )
        {
            newLiferayProjectPage.next();

            newLiferayProjectPage.next();

            SetSDKLocationWizard setSdkPage = new SetSDKLocationWizard( bot );

            setSdkPage.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );
        }
        sleep( 4000 );
        newLiferayProjectPage.finish();
        sleep( 20000 );

    }

    @Test
    public void sourceFolderTest()
    {
        String fileName = "portlet.xml";
        Tree projectTree = ide.showPackageExporerView().getProjectTree();

        sleep();
        projectTree.expandNode( "test-portlet", "docroot", "WEB-INF" ).doubleClick( fileName );

        Editor portletEditor = ide.getEditor( fileName );
        TreeItem portletXmlItem = new TreeItem( bot, projectTree, "test-portlet", "docroot", "WEB-INF", "portlet.xml" );

        // new source folder
        portletXmlItem.doAction( "Open" );
        assertTrue( portletEditor.isActive() );

        portletXmlItem.doAction( "New", "Other..." );
        SelectTypeWizard newTypePage = new SelectTypeWizard( bot, INDEX_SELECT_A_WIZARD_VALIDATION_MESSAGE );

        newTypePage.selectItem( "Source Folder", "Java", "Source Folder" );
        assertEquals( "Create a Java source folder", newTypePage.getValidationMsg() );
        newTypePage.next();

        NewSourceFolderWizard newSourceFolderPage =
            new NewSourceFolderWizard( bot, "New Source Folder", INDEX_NEW_SOURCE_FOLDER_VALIDATION_MESSAGE );

        assertEquals( "test-portlet", newSourceFolderPage.getProjectName().getText() );
        assertEquals( TEXT_CREATE_A_NEW_SOURCE_FOLDER, newSourceFolderPage.getValidationMsg() );

        newSourceFolderPage.newSourceFolder( "mysrc" );
        newSourceFolderPage.finish();

        // source folder validation tests
        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        newPortletPage.getSourceFolder().setText( TEXT_BLANK );
        assertEquals( TEXT_SOURCE_FOLDER_CANNOT_BE_EMPTY, newPortletPage.getValidationMsg() );

        newPortletPage.getSourceFolder().setText( "123" );
        assertEquals( TEXT_SOUCCE_FOLDER_MUST_BE_ABSOLUTE_PATH, newPortletPage.getValidationMsg() );

        newPortletPage.getBrowseSourceBtn().click();
        SelectionDialog browseSourceFolderPage = new SelectionDialog( bot, "Container Selection" );

        assertEquals( "Choose a Container:", browseSourceFolderPage.getDialogLabel( 0 ) );
        assertFalse( browseSourceFolderPage.confirmBtn().isEnabled() );
        assertTrue( browseSourceFolderPage.cancelBtn().isEnabled() );

        browseSourceFolderPage.getSelcetFileTree().selectTreeItem( "test-portlet", "docroot" );
        assertTrue( browseSourceFolderPage.confirmBtn().isEnabled() );
        assertTrue( browseSourceFolderPage.cancelBtn().isEnabled() );

        browseSourceFolderPage.confirm();

        assertEquals( TEXT_NOT_A_JAVA_SOURCE_FOLDER, newPortletPage.getValidationMsg() );

        newPortletPage.getBrowseSourceBtn().click();
        sleep();
        browseSourceFolderPage.getSelcetFileTree().selectTreeItem( "test-portlet", "mysrc" );
        browseSourceFolderPage.confirm();
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMsg() );

        newPortletPage.finish();

        TreeItem mysrcTreeItem =
            new TreeItem( bot, projectTree, "test-portlet", "mysrc", "com.test", "NewPortlet.java" );

        assertTrue( mysrcTreeItem.isVisible() );
    }

    @Test
    public void specifyPortletClassPageInitialState()
    {

        // check specfy modifier, interface and method stubs using GenericPortlet superclass
        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();;

        newPortletPage.next();
        newPortletPage.next();

        newPortletPage.next();

        // check initial state
        ModifiersInterfacesMethodStubsWizard modifiersInterfacesMethodStubsPage =
            new ModifiersInterfacesMethodStubsWizard( bot, INDEX_SPECIFY_PARAMS_IN_PORTLET_CLASS_PAGE );
        assertEquals(
            TEXT_SPECIFY_STUBS_TO_GENERATE_IN_PORTLET_CLASS, modifiersInterfacesMethodStubsPage.getValidationMsg() );

        assertTrue( modifiersInterfacesMethodStubsPage.getIsPublic().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getIsPublic().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getIsAbstract().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getIsAbstract().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getIsFinal().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getIsFinal().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getConstrcutFromSuperClass().isChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.getConstrcutFromSuperClass().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.getInheritedAbstractMethods().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getInheritedAbstractMethods().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.getInit().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getInit().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDestory().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDestory().isEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.getDoView().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoView().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoEdit().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoEdit().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoHelp().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoHelp().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoAbout().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoAbout().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoConfig().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoConfig().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoEditDefaults().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoEditDefaults().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoEditGuest().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoEditGuest().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoPreview().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoPreview().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getDoPrint().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getDoPrint().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getProcessAction().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getProcessAction().isEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.getServeResource().isChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.getServeResource().isEnabled() );

        newPortletPage.finish();
    }

    @Test
    public void superClassTest()
    {

        ide.getCreateLiferayProjectToolbar().getNewLiferayPortlet().click();

        // superclass tests
        newPortletPage.setSuperClassCombobox( TEXT_BLANK );
        assertEquals( TEXT_MUST_SPECIFY_A_PORTLET_SUPERCLASS, newPortletPage.getValidationMsg() );

        newPortletPage.setSuperClassCombobox( "MyClass123" );
        assertEquals( TEXT_SUPERCLASS_MUST_BE_VALID, newPortletPage.getValidationMsg() );

        newPortletPage.setSuperClassCombobox( "com.test.NewPortlet" );
        assertEquals( TEXT_SUPERCLASS_MUST_BE_VALID, newPortletPage.getValidationMsg() );

        newPortletPage.setSuperClassCombobox( "com.liferay.portal.kernel.portlet.LiferayPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMsg() );

        newPortletPage.getBrowseSuperClassBtn().click();
        sleep();

        SuperClassSelectionDialog selectSuperclassPage =
            new SuperClassSelectionDialog( bot, "Superclass Selection", 0 );

        assertEquals( "Choose a superclass:", selectSuperclassPage.getDialogLabel( 0 ) );

        // selectSuperclassPage.getAvailableSuperClasses().click( 0 );
        selectSuperclassPage.confirm();

        assertEquals( "com.liferay.util.bridges.bsf.BaseBSFPortlet", newPortletPage.getSuperClasses().getText() );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMsg() );

        newPortletPage.setSuperClassCombobox( "javax.portlet.GenericPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMsg() );

        newPortletPage.finish();

        Editor portletJavaPage = new Editor( bot, "NewPortlet.java" );

        assertContains( "public class NewPortlet extends GenericPortlet", portletJavaPage.getText() );
        assertTrue( portletJavaPage.isActive() );

        ide.getNewBtn().menuClick( LABEL_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( "test-portlet", "MySecondPortlet", null, "com.test.NewPortlet" );
        newPortletPage.finish();

        Editor mySecondPortletJavaPage = new Editor( bot, "MySecondPortlet.java" );

        assertContains( "public class MySecondPortlet extends NewPortlet", mySecondPortletJavaPage.getText() );

        ide.getNewBtn().menuClick( LABEL_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( "test-portlet" );

        assertEquals( " Type 'com.test.NewPortlet'" + TEXT_ALREADY_EXISTS, newPortletPage.getValidationMsg() );

        newPortletPage.cancel();
    }

}
