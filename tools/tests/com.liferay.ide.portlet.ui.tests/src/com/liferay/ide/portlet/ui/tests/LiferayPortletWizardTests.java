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

package com.liferay.ide.portlet.ui.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.portlet.ui.tests.page.ContainerSelectionPageObject;
import com.liferay.ide.portlet.ui.tests.page.CreateLiferayPortletWizardPageObject;
import com.liferay.ide.portlet.ui.tests.page.CssSelectionPageObject;
import com.liferay.ide.portlet.ui.tests.page.IconSelectionPageObject;
import com.liferay.ide.portlet.ui.tests.page.InterfaceSelectionPageObject;
import com.liferay.ide.portlet.ui.tests.page.JavaScriptSelectionPageObject;
import com.liferay.ide.portlet.ui.tests.page.LiferayPortletDeploymentDescriptorPageObject;
import com.liferay.ide.portlet.ui.tests.page.ModifiersInterfacesMethodStubsPageObject;
import com.liferay.ide.portlet.ui.tests.page.NewSourceFolderPageObject;
import com.liferay.ide.portlet.ui.tests.page.PackageSelectionPageObject;
import com.liferay.ide.portlet.ui.tests.page.PortletDeploymentDescriptorPageObject;
import com.liferay.ide.portlet.ui.tests.page.SelectTypePageObject;
import com.liferay.ide.portlet.ui.tests.page.SuperclassSelectionPageObject;
import com.liferay.ide.project.ui.tests.NewProjectPageObject;
import com.liferay.ide.project.ui.tests.ProjectWizardTests;
import com.liferay.ide.project.ui.tests.page.CreateProjectWizardPageObject;
import com.liferay.ide.project.ui.tests.page.ProjectTreePageObject;
import com.liferay.ide.project.ui.tests.page.SetSDKLocationPageObject;
import com.liferay.ide.ui.tests.ProjectWizard;
import com.liferay.ide.ui.tests.SWTBotBase;
import com.liferay.ide.ui.tests.UITestsUtils;
import com.liferay.ide.ui.tests.swtbot.page.DialogPageObject;
import com.liferay.ide.ui.tests.swtbot.page.EditorPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextEditorPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TreeItemPageObject;

import java.util.Arrays;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ashley Yuan
 */
public class LiferayPortletWizardTests extends SWTBotBase implements LiferayPortletWizard, ProjectWizard
{

    protected static IPath getCustomLocationBase()
    {
        final IPath customLocationBase =
            org.eclipse.core.internal.utils.FileUtil.canonicalPath( new Path( System.getProperty( "java.io.tmpdir" ) ) ).append(
                "custom-project-location-tests" );

        return customLocationBase;
    }

    @BeforeClass
    public static void openPluginsSDKProject() throws Exception
    {
        String projectName = "SdkProject";

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> projectPage =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );

        projectPage.createSDKProject( projectName, MENU_HOOK );
        projectPage.next();

        SetSDKLocationPageObject<SWTWorkbenchBot> sdkPage = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot );

        sdkPage.setSdkLocation( getLiferayPluginsSdkDir().toString() );
        sdkPage.finish();

        ProjectTreePageObject<SWTWorkbenchBot> deleteProject = new ProjectTreePageObject<>( bot, "SdkProject-hook" );

        deleteProject.deleteProject();

    }

    @After
    public void cleanAll()
    {
        SWTBotTreeItem[] items = treeBot.getItems();

        try
        {
            for( SWTBotTreeItem item : items )
            {
                if( !item.getText().equals( getLiferayPluginsSdkName() ) )
                {
                    item.contextMenu( BUTTON_DELETE ).click();

                    checkBoxBot.click();

                    buttonBot.click( BUTTON_OK );

                    try
                    {
                        if( buttonBot.isEnabled( "Continue" ) )
                        {
                            buttonBot.click( "Continue" );
                        }
                    }
                    catch( Exception e )
                    {
                        e.printStackTrace();
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
    public void createPortletClassPageTest()
    {
        // new liferay plugin project
        toolbarBot.menuClick( TOOLTIP_NEW, TOOLTIP_MENU_ITEM_LIFERAY_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> newLiferayProjectPage =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>(
                bot, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT, INDEX_VALIDATION_MESSAGE3 );
        assertEquals( "Portlet", newLiferayProjectPage.getPluginTypeComboBox() );

        // create portlet project with launch new MVC default portlet
        newLiferayProjectPage.createSDKProject( "test", "Portlet", false, true );
        newLiferayProjectPage.finish();

        CreateLiferayPortletWizardPageObject<SWTWorkbenchBot> newPortletPage =
            new CreateLiferayPortletWizardPageObject<SWTWorkbenchBot>(
                bot, TOOLTIP_NEW_LIFERAY_PORTLET, INDEX_VALIDATION_MESSAGE4 );

        newPortletPage.waitForPageToOpen();

        // check initial state
        assertEquals( "test-portlet", newPortletPage.getPortletPluginProject() );
        assertEquals( "/test-portlet/docroot/WEB-INF/src", newPortletPage.getSourceFolderText() );
        assertEquals( "NewPortlet", newPortletPage.getPortletClassText() );
        assertEquals( "com.test", newPortletPage.getJavaPackageText() );
        assertEquals( "com.liferay.util.bridges.mvc.MVCPortlet", newPortletPage.getSuperClassCombobox() );
        assertTrue( newPortletPage.isRadioSelected( RADIO_CREATE_NEW_PORTLET ) );
        assertFalse( newPortletPage.isRadioSelected( RADIO_USE_DEFAULT_PORTLET ) );
        assertTrue( Arrays.equals( newPortletPage.getAvailableSuperclasses(), availableSuperclasses ) );

        newPortletPage.createLiferayPortlet( true );

        assertFalse( newPortletPage.isPortletClassTextEnabled() );
        assertFalse( newPortletPage.isJavaPackageTextEnabled() );
        assertFalse( newPortletPage.isSuperClassComboboxEnabled() );

        newPortletPage.finish();

        EditorPageObject editorPage = new EditorPageObject( bot, "view.jsp" );
        assertTrue( editorPage.isActive() );

        TreeItemPageObject<SWTWorkbenchBot> treeItemPage =
            new TreeItemPageObject<SWTWorkbenchBot>( bot, "test-portlet", "docroot", "WEB-INF", "portlet.xml" );
        treeItemPage.doubleClick();

        TextEditorPageObject portletXmlPage = new TextEditorPageObject( bot, "portlet.xml" );

        assertContains( "<portlet-name>new</portlet-name>", portletXmlPage.getText() );
        assertContains( "com.liferay.util.bridges.mvc.MVCPortlet", portletXmlPage.getText() );

        // Ctrl+N shortcut to new liferay project with launch portlet wizard
        Keyboard keyPress = KeyboardFactory.getAWTKeyboard();
        treeItemPage.doubleClick();
        keyPress.pressShortcut( ctrl, N );

        SelectTypePageObject<SWTWorkbenchBot> newSelectLiferayPage =
            new SelectTypePageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE2 );

        newSelectLiferayPage.createProject( "liferay", "Liferay", TOOLTIP_MENU_ITEM_LIFERAY_PROJECT );
        newSelectLiferayPage.next();

        newLiferayProjectPage.createSDKProject( "test-second", "Portlet", false, true );
        assertEquals( TEXT_CREATE_NEW_PROJECT_AS_LIFERAY_PLUGIN, newLiferayProjectPage.getValidationMessage() );

        newLiferayProjectPage.finish();
        newPortletPage.waitForPageToOpen();

        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );
        assertEquals( "test-second-portlet", newPortletPage.getPortletPluginProject() );
        assertTrue( newPortletPage.isRadioSelected( RADIO_CREATE_NEW_PORTLET ) );
        assertTrue( newPortletPage.isButtonEnabled( BUTTON_FINISH ) );

        newPortletPage.finish();

        treeItemPage.doAction( "New", "Liferay Portlet" );

        assertEquals( "test-portlet", newPortletPage.getPortletPluginProject() );
        assertTrue( buttonBot.isEnabled( BUTTON_NEXT ) );
        assertFalse( buttonBot.isEnabled( BUTTON_FINISH ) );

        newPortletPage.createLiferayPortlet( "test-portlet", true );
        assertTrue( buttonBot.isEnabled( BUTTON_NEXT ) );
        assertFalse( buttonBot.isEnabled( BUTTON_FINISH ) );

        newPortletPage.cancel();

        portletXmlPage.close();

        // new source folder
        treeItemPage.doAction( "Open" );
        assertTrue( portletXmlPage.isActive() );
        treeItemPage.doAction( "New", "Other..." );

        SelectTypePageObject<SWTWorkbenchBot> newTypePage =
            new SelectTypePageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE2 );

        newTypePage.createProject( "Source Folder", "Java", "Source Folder" );
        assertEquals( "Create a Java source folder", newTypePage.getValidationMessage() );
        newTypePage.next();

        NewSourceFolderPageObject<SWTWorkbenchBot> newSourceFolderPage =
            new NewSourceFolderPageObject<SWTWorkbenchBot>( bot, "New Source Folder", INDEX_VALIDATION_MESSAGE3 );

        assertEquals( "test-portlet", newSourceFolderPage.getProjectNameText() );
        assertEquals( TEXT_CREATE_A_NEW_SOURCE_FOLDER, newSourceFolderPage.getValidationMessage() );

        newSourceFolderPage.newSourceFolder( "mysrc" );
        newSourceFolderPage.finish();

        // source folder tests
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        newPortletPage.setSourceFolderText( TEXT_BLANK );
        assertEquals( TEXT_SOURCE_FOLDER_CANNOT_BE_EMPTY, newPortletPage.getValidationMessage() );

        newPortletPage.setSourceFolderText( "123" );
        assertEquals( TEXT_SOUCCE_FOLDER_MUST_BE_ABSOLUTE_PATH, newPortletPage.getValidationMessage() );

        newPortletPage.browse( 0 );

        ContainerSelectionPageObject browseSourceFolderPage =
            new ContainerSelectionPageObject( bot, "Container Selection", 0 );

        assertEquals( "Choose a Container:", browseSourceFolderPage.getDialogLabel() );
        assertFalse( browseSourceFolderPage.isButtonEnabled( BUTTON_OK ) );
        assertTrue( browseSourceFolderPage.isButtonEnabled( BUTTON_CANCEL ) );

        browseSourceFolderPage.getSelcetFileTree().selectTreeItem( "test-portlet", "docroot" );
        assertTrue( browseSourceFolderPage.isButtonEnabled( BUTTON_OK ) );
        assertTrue( browseSourceFolderPage.isButtonEnabled( BUTTON_CANCEL ) );

        buttonBot.click( BUTTON_OK );
        assertEquals( TEXT_NOT_A_JAVA_SOURCE_FOLDER, newPortletPage.getValidationMessage() );

        newPortletPage.browse( 0 );
        browseSourceFolderPage.getSelcetFileTree().selectTreeItem( "test-portlet", "mysrc" );
        buttonBot.click( BUTTON_OK );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        // portlet class tests
        newPortletPage.setPortletClassText( TEXT_BLANK );
        assertEquals( TEXT_CLASS_NAME_CANNOT_BE_EMPTY, newPortletPage.getValidationMessage() );

        newPortletPage.setPortletClassText( "123" );
        assertEquals(
            TEXT_INVALID_JAVA_CLASS_NAME + "'123'" + TEXT_NOT_A_VALID_IDENTIFIER, newPortletPage.getValidationMessage() );

        newPortletPage.setPortletClassText( "aaa" );
        assertEquals( TEXT_JAVA_TYPE_START_WITH_AN_UPPERCASE_LETTER, newPortletPage.getValidationMessage() );

        newPortletPage.setPortletClassText( "MyPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

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

        newPortletPage.browse( 1 );

        PackageSelectionPageObject selectPackagePage = new PackageSelectionPageObject( bot, "Package Selection", 0 );

        assertEquals( "Choose a package:", browseSourceFolderPage.getDialogLabel() );
        selectPackagePage.clickPackage( 0 );

        buttonBot.click( BUTTON_OK );
        assertEquals( TEXT_BLANK, newPortletPage.getJavaPackageText() );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.setJavaPackageText( "myPackage" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        // superclass tests
        newPortletPage.setSuperClassCombobox( TEXT_BLANK );
        assertEquals( TEXT_MUST_SPECIFY_A_PORTLET_SUPERCLASS, newPortletPage.getValidationMessage() );

        newPortletPage.setSuperClassCombobox( "MyClass123" );
        assertEquals( TEXT_SUPERCLASS_MUST_BE_VALID, newPortletPage.getValidationMessage() );

        newPortletPage.setSuperClassCombobox( "com.test.NewPortlet" );
        assertEquals( TEXT_SUPERCLASS_MUST_BE_VALID, newPortletPage.getValidationMessage() );

        newPortletPage.setSuperClassCombobox( "com.liferay.portal.kernel.portlet.LiferayPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.browse( 2 );

        SuperclassSelectionPageObject selectSuperclassPage =
            new SuperclassSelectionPageObject( bot, "Superclass Selection", 0 );

        assertEquals( "Choose a superclass:", selectSuperclassPage.getDialogLabel() );
        selectPackagePage.clickPackage( 0 );

        buttonBot.click( BUTTON_OK );
        assertEquals( "com.liferay.util.bridges.alloy.AlloyPortlet", newPortletPage.getSuperClassCombobox() );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.setSuperClassCombobox( "javax.portlet.GenericPortlet" );
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage.getValidationMessage() );

        newPortletPage.finish();

        TextEditorPageObject myPortletJavaPage = new TextEditorPageObject( bot, "MyPortlet.java" );

        assertContains( "package myPackage;", myPortletJavaPage.getText() );
        assertContains( "public class MyPortlet extends GenericPortlet", myPortletJavaPage.getText() );

        TreeItemPageObject<SWTWorkbenchBot> mysrcTreePage =
            new TreeItemPageObject<SWTWorkbenchBot>( bot, "test-portlet", "mysrc", "myPackage" );
        mysrcTreePage.expand();

        assertTrue( mysrcTreePage.isVisible() );
        assertTrue( myPortletJavaPage.isActive() );

        keyPress.pressShortcut( ctrl, N );

        newSelectLiferayPage.createProject( "liferay", "Liferay", TOOLTIP_MENU_ITEM_LIFERAY_PORTLET );
        newSelectLiferayPage.next();

        newPortletPage.createLiferayPortlet(
            "test-second-portlet", "MySecondPortlet", TEXT_BLANK, "com.test.NewPortlet" );
        newPortletPage.finish();

        TextEditorPageObject mySecondPortletJavaPage = new TextEditorPageObject( bot, "MySecondPortlet.java" );

        assertContains( "import com.test.NewPortlet", mySecondPortletJavaPage.getText() );
        assertContains( "public class MySecondPortlet extends NewPortlet", mySecondPortletJavaPage.getText() );
        assertFalse( mySecondPortletJavaPage.getText().contains( "package" ) );

        toolbarBot.menuClick( TOOLTIP_NEW, TOOLTIP_MENU_ITEM_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( "test-second-portlet" );

        assertEquals( " Type 'com.test.NewPortlet'" + TEXT_ALREADY_EXISTS, newPortletPage.getValidationMessage() );

        newPortletPage.cancel();
    }

    public void createProject(
        String filterText, String projectTypeTree, String projectTypeNode, String projectName, String validateMessage1,
        String validateMessage2 )
    {

        // toolbarBot.menuClick( MENU_FILE, MENU_NEW, MENU_PROJECT );

        SelectTypePageObject<SWTWorkbenchBot> newProjectPage =
            new SelectTypePageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE2 );

        newProjectPage.createProject( filterText, projectTypeTree, projectTypeNode );
        assertEquals( validateMessage1, newProjectPage.getValidationMessage() );
        newProjectPage.next();

        NewProjectPageObject<SWTWorkbenchBot> newJavaProjectPage =
            new NewProjectPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE3 );

        newJavaProjectPage.createJavaProject( projectName );
        assertEquals( validateMessage2, newJavaProjectPage.getValidationMessage() );

        newJavaProjectPage.finish();

        if( !projectTypeNode.equals( "Project" ) )
        {
            DialogPageObject<SWTWorkbenchBot> dialogPage =
                new DialogPageObject<SWTWorkbenchBot>( bot, "Open Associated Perspective", BUTTON_YES, BUTTON_NO );

            dialogPage.confirm();
        }
    }

    public boolean isInAvailableLists( String[] avaiable, String excepted )
    {
        for( String temp : avaiable )
        {
            if( temp.equals( excepted ) )
            {
                return true;
            }
        }
        return false;
    }

    @Test
    public void liferayPortletDeploymentDescriptor()
    {

        // create portlet project with sample code and check
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> newLiferayProjectPage =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        newLiferayProjectPage.createSDKProject( "test", "Portlet", true, false );
        newLiferayProjectPage.finish();

        TreeItemPageObject<SWTWorkbenchBot> liferayPortletXmlPage =
            new TreeItemPageObject<SWTWorkbenchBot>( bot, "test-portlet", "docroot", "WEB-INF", "liferay-portlet.xml" );

        liferayPortletXmlPage.doubleClick();

        TextEditorPageObject liferayPortletEditor = new TextEditorPageObject( bot, "liferay-portlet.xml" );

        assertContains(
            "<portlet-name>test</portlet-name>\n\t\t<icon>/icon.png</icon>\n\t\t<header-portlet-css>/css/main.css</header-portlet-css>\n\t\t<footer-portlet-javascript>/js/main.js</footer-portlet-javascript>\n\t\t<css-class-wrapper>test-portlet</css-class-wrapper>",
            liferayPortletEditor.getText() );

        // new liferay portlet and go to speficy liferay portlet deployment descriptor page
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        CreateLiferayPortletWizardPageObject<SWTWorkbenchBot> newPortletPage =
            new CreateLiferayPortletWizardPageObject<SWTWorkbenchBot>( bot );
        newPortletPage.next();

        PortletDeploymentDescriptorPageObject<SWTWorkbenchBot> specifyPortletDeploymentDescriptorPage =
            new PortletDeploymentDescriptorPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE6 );

        newPortletPage.next();

        LiferayPortletDeploymentDescriptorPageObject<SWTWorkbenchBot> specifyLiferayPortletDeploymentDescriptorPage =
            new LiferayPortletDeploymentDescriptorPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE7 );

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
        assertTrue( Arrays.equals(
            specifyLiferayPortletDeploymentDescriptorPage.getDisplayCategoryAvailableComboValues(),
            availableDisplayCategories ) );

        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isAddToControlPanelChecked() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isEntryCategoryEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isEntryWeightEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isCreateEntryClassEnabled() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isEntryClassEnabled() );

        assertEquals(
            "My Account Administration", specifyLiferayPortletDeploymentDescriptorPage.getEntryCategoryCombobox() );
        assertEquals( "1.5", specifyLiferayPortletDeploymentDescriptorPage.getEntryWeightText() );
        assertFalse( specifyLiferayPortletDeploymentDescriptorPage.isCreateEntryClassChecked() );
        assertEquals( "NewPortletControlPanelEntry", specifyLiferayPortletDeploymentDescriptorPage.getEntryClassText() );

        newPortletPage.finish();

        // check codes generate in liferay-portlet.xml
        assertContains(
            "<portlet-name>new</portlet-name>\n\t\t<icon>/icon.png</icon>\n\t\t<header-portlet-css>/css/main.css</header-portlet-css>\n\t\t<footer-portlet-javascript>\n\t\t\t/js/main.js\n\t\t</footer-portlet-javascript>\n\t\t<css-class-wrapper>new-portlet</css-class-wrapper>\n\t</portlet>\n\t<role-mapper>",
            liferayPortletEditor.getText() );

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "NewPortletOne", null, null );
        newPortletPage.next();
        sleep( 500 );
        newPortletPage.next();

        assertEquals( "new-portlet-one-portlet", specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapperText() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayPortletInfo(
            TEXT_BLANK, true, TEXT_BLANK, TEXT_BLANK, TEXT_BLANK );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        // display category tests
        specifyLiferayPortletDeploymentDescriptorPage.setDisplayCategoryCombobox( TEXT_BLANK );
        assertEquals( TEXT_CATEGORY_NAME_IS_EMPTY, specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setDisplayCategoryCombobox( "my1category" );
        assertEquals(
            TEXT_SPECIFY_LIFERAY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, true, null, null, true, null );

        assertTrue( Arrays.equals(
            availableEntryCategories,
            specifyLiferayPortletDeploymentDescriptorPage.getEntryCategoryAvailableComboValues() ) );
        assertEquals( "1.5", specifyLiferayPortletDeploymentDescriptorPage.getEntryWeightText() );
        assertEquals(
            "NewPortletOneControlPanelEntry", specifyLiferayPortletDeploymentDescriptorPage.getEntryClassText() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.isEntryCategoryEnabled() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.isEntryWeightEnabled() );
        assertTrue( specifyLiferayPortletDeploymentDescriptorPage.isEntryClassEnabled() );

        newPortletPage.finish();

        assertContains(
            "<portlet-name>new-portlet-one</portlet-name>\n\t\t<icon></icon>\n\t\t<control-panel-entry-category>my</control-panel-entry-category>\n\t\t<control-panel-entry-weight>1.5</control-panel-entry-weight>\n\t\t<control-panel-entry-class>\n\t\t\tcom.test.NewPortletOneControlPanelEntry\n\t\t</control-panel-entry-class>\n\t\t<instanceable>true</instanceable>\n\t\t<header-portlet-css></header-portlet-css>\n\t\t<footer-portlet-javascript></footer-portlet-javascript>\n\t\t<css-class-wrapper></css-class-wrapper>",
            liferayPortletEditor.getText() );

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "NewPortletSecond", null, null );
        newPortletPage.next();

        PortletDeploymentDescriptorPageObject<SWTWorkbenchBot> portletDeploymentDescriptorPage =
            new PortletDeploymentDescriptorPageObject<SWTWorkbenchBot>( bot );

        portletDeploymentDescriptorPage.speficyPortletInfo( "new-portlet-second2", null, null );
        newPortletPage.next();

        assertEquals(
            "new-portlet-second2-portlet", specifyLiferayPortletDeploymentDescriptorPage.getCssClassWrapperText() );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayPortletInfo(
            "unexistentIcon", false, "unexistentCss", "unexistentJavaScript", null );

        assertTrue( isInAvailableLists(
            specifyLiferayPortletDeploymentDescriptorPage.getDisplayCategoryAvailableComboValues(), "my1category" ) );

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
            TEXT_MUST_SPECIFY_VALID_ENTRY_WEIGHT, specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyLiferayPortletDeploymentDescriptorPage.setEntryWeightText( "**" );
        assertEquals(
            TEXT_MUST_SPECIFY_VALID_ENTRY_WEIGHT, specifyLiferayPortletDeploymentDescriptorPage.getValidationMessage() );

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
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "NewPortletThird", TEXT_BLANK, "javax.portlet.GenericPortlet" );
        newPortletPage.next();
        sleep( 500 );
        newPortletPage.next();

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay(
            "Tools", true, "MyEntryCategory", "1", true, null );

        newPortletPage.finish();

        assertContains(
            "<portlet-name>new-portlet-third</portlet-name>\n\t\t<icon>/icon.png</icon>\n\t\t<control-panel-entry-category>\n\t\t\tMyEntryCategory\n\t\t</control-panel-entry-category>\n\t\t<control-panel-entry-weight>1</control-panel-entry-weight>\n\t\t<control-panel-entry-class>\n\t\t\tNewPortletThirdControlPanelEntry\n\t\t</control-panel-entry-class>\n\t\t<header-portlet-css>/css/main.css</header-portlet-css>\n\t\t<footer-portlet-javascript>\n\t\t\t/js/main.js\n\t\t</footer-portlet-javascript>\n\t\t<css-class-wrapper>new-portlet-third-portlet</css-class-wrapper>",
            liferayPortletEditor.getText() );

        TreeItemPageObject<SWTWorkbenchBot> liferayDisplayXmlPage =
            new TreeItemPageObject<SWTWorkbenchBot>( bot, "test-portlet", "docroot", "WEB-INF", "liferay-display.xml" );
        liferayDisplayXmlPage.expand();
        liferayDisplayXmlPage.doubleClick();

        TextEditorPageObject liferayDisplayEditor = new TextEditorPageObject( bot, "liferay-display.xml" );

        assertContains(
            "<display>\n\t<category name=\"category.sample\">\n\t\t<portlet id=\"test\" />\n\t\t<portlet id=\"new\"></portlet>\n\t\t<portlet id=\"new-portlet-second2\"></portlet>\n\t</category>\n\t<category name=\"my1category\">\n\t\t<portlet id=\"new-portlet-one\"></portlet>\n\t</category>\n\t<category name=\"category.tools\">\n\t\t<portlet id=\"new-portlet-third\"></portlet>\n\t</category>\n</display>",
            liferayDisplayEditor.getText() );

        TreeItemPageObject<SWTWorkbenchBot> NewPortletThirdControlPanelEntryJavaFile =
            new TreeItemPageObject<SWTWorkbenchBot>(
                bot, "test-portlet", "docroot/WEB-INF/src", "(default package)",
                "NewPortletThirdControlPanelEntry.java" );
        NewPortletThirdControlPanelEntryJavaFile.expand();
        assertTrue( NewPortletThirdControlPanelEntryJavaFile.isVisible() );

        TreeItemPageObject<SWTWorkbenchBot> NewPortletOneControlPanelEntryJavaFile =
            new TreeItemPageObject<SWTWorkbenchBot>(
                bot, "test-portlet", "docroot/WEB-INF/src", "com.test", "NewPortletOneControlPanelEntry.java" );
        NewPortletOneControlPanelEntryJavaFile.expand();
        assertTrue( NewPortletOneControlPanelEntryJavaFile.isVisible() );

        // browse icon tests
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

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

        specifyLiferayPortletDeploymentDescriptorPage.browse( 0 );

        IconSelectionPageObject iconSelectPage =
            new IconSelectionPageObject( bot, "Icon Selection", INDEX_VALIDATION_MESSAGE1 );

        assertEquals( "Choose an icon file:", iconSelectPage.getDialogLabel() );
        assertFalse( iconSelectPage.isButtonEnabled( ProjectWizard.BUTTON_OK ) );
        assertTrue( iconSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( iconSelectPage.isButtonEnabled( ProjectWizard.BUTTON_OK ) );
        assertTrue( iconSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "js", "main.js" );
        assertTrue( iconSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );
        buttonBot.click( BUTTON_OK );

        assertEquals( "/js/main.js", specifyLiferayPortletDeploymentDescriptorPage.getIconText() );

        // browse css tests
        specifyLiferayPortletDeploymentDescriptorPage.browse( 1 );

        CssSelectionPageObject cssSelectPage =
            new CssSelectionPageObject( bot, "CSS Selection", INDEX_VALIDATION_MESSAGE1 );

        assertEquals( "Choose a css file:", cssSelectPage.getDialogLabel() );
        assertFalse( cssSelectPage.isButtonEnabled( ProjectWizard.BUTTON_OK ) );
        assertTrue( cssSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );

        iconSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( cssSelectPage.isButtonEnabled( ProjectWizard.BUTTON_OK ) );
        assertTrue( cssSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );

        cssSelectPage.getSelcetFileTree().selectTreeItem( "view.jsp" );
        assertTrue( cssSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );
        buttonBot.click( BUTTON_OK );

        assertEquals( "/view.jsp", specifyLiferayPortletDeploymentDescriptorPage.getCssText() );

        // browse javaScript tests
        specifyLiferayPortletDeploymentDescriptorPage.browse( 2 );

        JavaScriptSelectionPageObject javaScriptSelectPage =
            new JavaScriptSelectionPageObject( bot, "JavaScript Selection", INDEX_VALIDATION_MESSAGE1 );

        assertEquals( "Choose a javascript file:", javaScriptSelectPage.getDialogLabel() );
        assertFalse( javaScriptSelectPage.isButtonEnabled( ProjectWizard.BUTTON_OK ) );
        assertTrue( javaScriptSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );

        javaScriptSelectPage.getSelcetFileTree().selectTreeItem( "WEB-INF", "lib" );
        assertFalse( javaScriptSelectPage.isButtonEnabled( ProjectWizard.BUTTON_OK ) );
        assertTrue( javaScriptSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );

        javaScriptSelectPage.getSelcetFileTree().selectTreeItem( "html", "new", "view.jsp" );
        assertTrue( javaScriptSelectPage.isButtonEnabled( ProjectWizard.BUTTON_CANCEL ) );
        buttonBot.click( BUTTON_OK );

        assertEquals( "/html/new/view.jsp", specifyLiferayPortletDeploymentDescriptorPage.getJavaScriptText() );

        newPortletPage.finish();

        assertContains(
            "<portlet-name>new-portlet-portlet</portlet-name>\n\t\t<icon>/js/main.js</icon>\n\t\t<header-portlet-css>/view.jsp</header-portlet-css>\n\t\t<footer-portlet-javascript>\n\t\t\t/html/new/view.jsp\n\t\t</footer-portlet-javascript>\n\t\t<css-class-wrapper>\n\t\t\tnew-portlet-portlet-portlet\n\t\t</css-class-wrapper>",
            liferayPortletEditor.getText() );

    }

    @Test
    public void modifiersInterfacesMethodStubs()
    {

        // new liferay portlet project without sample code and launch portlet wizard
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> newLiferayProjectPage =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        newLiferayProjectPage.createSDKProject( "test", "Portlet", false, false );
        newLiferayProjectPage.finish();

        // check specfy modifier, interface and method stubs using GenericPortlet superclass
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        CreateLiferayPortletWizardPageObject<SWTWorkbenchBot> newPortletPage =
            new CreateLiferayPortletWizardPageObject<SWTWorkbenchBot>( bot );

        newPortletPage.createLiferayPortlet( TEXT_BLANK, null, null, "javax.portlet.GenericPortlet" );

        newPortletPage.next();
        sleep( 500 );
        newPortletPage.next();

        LiferayPortletDeploymentDescriptorPageObject<SWTWorkbenchBot> specifyLiferayPortletDeploymentDescriptorPage =
            new LiferayPortletDeploymentDescriptorPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE7 );

        specifyLiferayPortletDeploymentDescriptorPage.specifyLiferayDisplay( null, true, null, null, true, null );

        newPortletPage.next();

        // check initial state
        ModifiersInterfacesMethodStubsPageObject<SWTWorkbenchBot> modifiersInterfacesMethodStubsPage =
            new ModifiersInterfacesMethodStubsPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE1 );
        assertEquals(
            TEXT_SPECIFY_STUBS_TO_GENERATE_IN_PORTLET_CLASS, modifiersInterfacesMethodStubsPage.getValidationMessage() );

        assertTrue( modifiersInterfacesMethodStubsPage.isPublicChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isPublicEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isAbstractChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isAbstractEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isFinalChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isFinalEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isConstrcutFromSuperClassChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isConstrcutFromSuperClassEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.isInheritedAbstractMethodsChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isInheritedAbstractMethodsEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.isInitChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isInitEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDestoryChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isDestoryEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.isDoViewChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoViewEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isDoEditEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoHelpChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isDoHelpEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoAboutChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoAboutEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoConfigChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoConfigEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditDefaultsChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditDefaultsEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditGuestChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditGuestEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoPreviewChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoPreviewEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoPrintChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoPrintEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isProcessActionChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isProcessActionEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isServeResourceChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isServeResourceEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.isButtonEnabled( BUTTON_ADD ) );
        assertFalse( modifiersInterfacesMethodStubsPage.isButtonEnabled( BUTTON_REMOVE ) );

        modifiersInterfacesMethodStubsPage.selectCheckbox( LABEL_FINAL );
        modifiersInterfacesMethodStubsPage.selectCheckbox( LABEL_SERVERESOURCE );

        modifiersInterfacesMethodStubsPage.clickButton( BUTTON_ADD );

        // click Add button to add interface and tests
        InterfaceSelectionPageObject selectInterfacePage =
            new InterfaceSelectionPageObject( bot, "Interface Selection", INDEX_VALIDATION_MESSAGE1 );

        selectInterfacePage.setItemToOpen( "acceptor" );
        selectInterfacePage.clickMatchItem( 0 );
        selectInterfacePage.confirm();

        modifiersInterfacesMethodStubsPage.selectInterface( 0 );
        modifiersInterfacesMethodStubsPage.clickButton( BUTTON_REMOVE );

        assertFalse( modifiersInterfacesMethodStubsPage.isButtonEnabled( BUTTON_REMOVE ) );

        modifiersInterfacesMethodStubsPage.clickButton( BUTTON_ADD );

        assertTrue( selectInterfacePage.isButtonEnabled( BUTTON_OK ) );
        selectInterfacePage.confirm();

        newPortletPage.finish();

        // check generate codes
        TextEditorPageObject newPortletJavaEditor = new TextEditorPageObject( bot, "NewPortlet.java" );

        assertContains(
            "public final class NewPortlet extends GenericPortlet implements Acceptor", newPortletJavaEditor.getText() );
        assertContains( "public void init()", newPortletJavaEditor.getText() );
        assertContains( "public void serveResource", newPortletJavaEditor.getText() );
        assertContains( "public void doView", newPortletJavaEditor.getText() );

        // new liferay portlet project using superclass MVCPortlet
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( null, "NewSecondPortlet", null, null );

        newPortletPage.next();
        newPortletPage.next();
        newPortletPage.next();

        // go to page and check state
        assertEquals(
            TEXT_SPECIFY_STUBS_TO_GENERATE_IN_PORTLET_CLASS, modifiersInterfacesMethodStubsPage.getValidationMessage() );

        assertTrue( modifiersInterfacesMethodStubsPage.isPublicChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isPublicEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isAbstractChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isAbstractEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isFinalChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isFinalEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isConstrcutFromSuperClassChecked() );
        assertTrue( modifiersInterfacesMethodStubsPage.isConstrcutFromSuperClassEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.isInheritedAbstractMethodsChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isInheritedAbstractMethodsEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.isInitChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isInitEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDestoryChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDestoryEnabled() );

        assertTrue( modifiersInterfacesMethodStubsPage.isDoViewChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoViewEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoHelpChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoHelpEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoAboutChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoAboutEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoConfigChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoConfigEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditDefaultsChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditDefaultsEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditGuestChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoEditGuestEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoPreviewChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoPreviewEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isDoPrintChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isDoPrintEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isProcessActionChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isProcessActionEnabled() );

        assertFalse( modifiersInterfacesMethodStubsPage.isServeResourceChecked() );
        assertFalse( modifiersInterfacesMethodStubsPage.isServeResourceEnabled() );

        modifiersInterfacesMethodStubsPage.selectCheckbox( LABEL_CONSTRUCTORS_FROM_SUPERCLASS );

        newPortletPage.finish();

        // check generate codes in java file
        TextEditorPageObject newSecondPortletJavaEditor = new TextEditorPageObject( bot, "NewSecondPortlet.java" );

        assertContains( "public NewSecondPortlet()", newSecondPortletJavaEditor.getText() );

    }

    @Test
    public void noLiferayProjectsTest()
    {
        // click new liferay portlet wizard without projects
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        DialogPageObject<SWTWorkbenchBot> dialogPage1 =
            new DialogPageObject<SWTWorkbenchBot>( bot, "New Liferay Portlet", BUTTON_NO, BUTTON_YES );

        dialogPage1.confirm();

        CreateProjectWizardPageObject<SWTWorkbenchBot> newLiferayProjectPage1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>(
                bot, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT, INDEX_VALIDATION_MESSAGE3 );

        assertEquals( TEXT_ENTER_PROJECT_NAME, newLiferayProjectPage1.getValidationMessage() );
        assertFalse( buttonBot.isEnabled( ProjectWizard.BUTTON_NEXT ) );

        newLiferayProjectPage1.createSDKProject( "test" );
        assertTrue( buttonBot.isEnabled( ProjectWizard.BUTTON_NEXT ) );

        newLiferayProjectPage1.cancel();

        DialogPageObject<SWTWorkbenchBot> dialogPage2 =
            new DialogPageObject<SWTWorkbenchBot>( bot, "New Liferay Portlet", BUTTON_YES, BUTTON_NO );

        dialogPage2.confirm();

        CreateLiferayPortletWizardPageObject<SWTWorkbenchBot> newPortletPage1 =
            new CreateLiferayPortletWizardPageObject<SWTWorkbenchBot>(
                bot, TOOLTIP_NEW_LIFERAY_PORTLET, INDEX_VALIDATION_MESSAGE4 );

        newPortletPage1.waitForPageToOpen();
        assertEquals( TEXT_ENTER_A_PROJECT_NAME, newPortletPage1.getValidationMessage() );

        newPortletPage1.cancel();

        // new Java project
        toolbarBot.menuClick( MENU_FILE, MENU_NEW, MENU_PROJECT );
        createProject(
            "Java", "Java", LABEL_JAVA_PROJECT, "JavaExample", TEXT_CREATE_A_JAVA_PROJECT,
            TEXT_CREATE_A_JAVA_PROJECT_IN_WORKSPACE );

        // new general project
        toolbarBot.menuClick( MENU_FILE, MENU_NEW, MENU_PROJECT );
        createProject(
            "project", "General", "Project", "GeneralExample", TEXT_CREATE_A_NEW_PROJECT_RESOURCE,
            TEXT_CREATE_A_NEW_PROJECT_RESOURCE + '.' );

        toolbarBot.menuClick( TOOLTIP_NEW, TOOLTIP_MENU_ITEM_LIFERAY_PORTLET );

        DialogPageObject<SWTWorkbenchBot> dialogPage3 =
            new DialogPageObject<SWTWorkbenchBot>( bot, "New Liferay Portlet", BUTTON_YES, BUTTON_NO );

        dialogPage3.confirm();

        CreateLiferayPortletWizardPageObject<SWTWorkbenchBot> newPortletPage2 =
            new CreateLiferayPortletWizardPageObject<SWTWorkbenchBot>(
                bot, TOOLTIP_NEW_LIFERAY_PORTLET, INDEX_VALIDATION_MESSAGE4 );

        newPortletPage2.waitForPageToOpen();
        newPortletPage2.cancel();

        toolbarBot.menuClick( MENU_FILE, TOOLTIP_NEW, TOOLTIP_MENU_ITEM_LIFERAY_PORTLET );

        DialogPageObject<SWTWorkbenchBot> dialogPage4 =
            new DialogPageObject<SWTWorkbenchBot>( bot, "New Liferay Portlet", BUTTON_NO, BUTTON_YES );

        dialogPage4.confirm();

        CreateProjectWizardPageObject<SWTWorkbenchBot> newLiferayProjectPage2 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        newLiferayProjectPage2.createSDKProject( "test" );
        newLiferayProjectPage2.finish();
        sleep();
        assertTrue( UITestsUtils.checkConsoleMessage( "BUILD SUCCESSFUL", "Java" ) );

        CreateLiferayPortletWizardPageObject<SWTWorkbenchBot> newPortletPage3 =
            new CreateLiferayPortletWizardPageObject<SWTWorkbenchBot>(
                bot, TOOLTIP_NEW_LIFERAY_PORTLET, INDEX_VALIDATION_MESSAGE4 );

        newPortletPage3.waitForPageToOpen();
        assertEquals( TEXT_CREATE_A_PORTLET_CLASS, newPortletPage3.getValidationMessage() );
        assertEquals( "test-portlet", newPortletPage3.getPortletPluginProject() );
        assertTrue( newPortletPage3.isRadioSelected( RADIO_CREATE_NEW_PORTLET ) );
        assertEquals( "com.liferay.util.bridges.mvc.MVCPortlet", newPortletPage3.getSuperClassCombobox() );

        newPortletPage3.finish();

        ProjectWizardTests.deleteProjectInSdk( "test-portlet" );
    }

    @Test
    public void portletDeploymentDescriptorTest()
    {
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> newLiferayProjectPage =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        // create portlet project without sample and launch portlet wizard
        newLiferayProjectPage.createSDKProject( "test", "Portlet", false, false );
        newLiferayProjectPage.finish();

        // relate ticket IDE-2156, regression for IDE-119
        // TreeItemPageObject<SWTWorkbenchBot> treeItemPage =
        new TreeItemPageObject<SWTWorkbenchBot>( bot, "test-portlet", "docroot", "WEB-INF", "liferay-display.xml" );
        // treeItemPage.expand();
        // treeItemPage.doAction( "Delete" );
        // DialogPageObject<SWTWorkbenchBot> deleteDialogPage =new DialogPageObject<SWTWorkbenchBot>( bot, "Delete",
        // BUTTON_CANCEL,BUTTON_OK );
        // deleteDialogPage.confirm();

        // new liferay portlet wizard
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );
        CreateLiferayPortletWizardPageObject<SWTWorkbenchBot> newPortletPage =
            new CreateLiferayPortletWizardPageObject<SWTWorkbenchBot>( bot );
        newPortletPage.next();

        PortletDeploymentDescriptorPageObject<SWTWorkbenchBot> specifyPortletDeploymentDescriptorPage =
            new PortletDeploymentDescriptorPageObject<SWTWorkbenchBot>( bot, INDEX_VALIDATION_MESSAGE6 );

        // initial state check
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        assertEquals( "new", specifyPortletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "New", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "New", specifyPortletDeploymentDescriptorPage.getPortletTitle() );
        assertTrue( specifyPortletDeploymentDescriptorPage.isViewPortletModeChecked() );
        assertTrue( specifyPortletDeploymentDescriptorPage.isCreateJspFilesChecked() );
        assertEquals( "/html/new", specifyPortletDeploymentDescriptorPage.getJspFolder() );
        assertFalse( specifyPortletDeploymentDescriptorPage.isCreateResourceBundleFileChecked() );
        assertFalse( specifyPortletDeploymentDescriptorPage.isBundleFilePathEnabled() );
        assertEquals( "content/Language.properties", specifyPortletDeploymentDescriptorPage.getResourceBundleFilePath() );

        newPortletPage.finish();

        // IDE-2156 treeItemPage.isVisible();

        // check generate codes and files
        EditorPageObject newPortletJavaPage = new EditorPageObject( bot, "NewPortlet.java" );
        assertTrue( newPortletJavaPage.isActive() );
        TreeItemPageObject<SWTWorkbenchBot> jspFile =
            new TreeItemPageObject<SWTWorkbenchBot>( bot, "test-portlet", "docroot", "html", "new", "view.jsp" );
        assertTrue( jspFile.isVisible() );

        TreeItemPageObject<SWTWorkbenchBot> treeItemPage =
            new TreeItemPageObject<SWTWorkbenchBot>( bot, "test-portlet", "docroot", "WEB-INF", "portlet.xml" );
        treeItemPage.doubleClick();

        TextEditorPageObject portletXmlPage = new TextEditorPageObject( bot, "portlet.xml" );
        assertContains( "<portlet-name>new</portlet-name>", portletXmlPage.getText() );
        assertContains( "<display-name>New</display-name>", portletXmlPage.getText() );
        assertContains( "<title>New</title>", portletXmlPage.getText() );

        // new liferay portlet wizard with default MVCPortlet
        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( true );
        newPortletPage.next();

        assertFalse( specifyPortletDeploymentDescriptorPage.isButtonEnabled( BUTTON_FINISH ) );
        assertEquals( TEXT_PORTLET_NAME_ALREADY_EXISTS, specifyPortletDeploymentDescriptorPage.getValidationMessage() );

        specifyPortletDeploymentDescriptorPage.setPortletName( "New" );
        assertEquals(
            TEXT_VIEW_JSP_EXSITS_AND_OVERWRITTEN, specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        assertTrue( specifyPortletDeploymentDescriptorPage.isButtonEnabled( BUTTON_FINISH ) );

        specifyPortletDeploymentDescriptorPage.specifyResources( false, null, true, null );
        assertEquals(
            TEXT_SPECIFY_PORTLET_DEPLOYMENT_DESCRIPTOR_DETAILS,
            specifyPortletDeploymentDescriptorPage.getValidationMessage() );
        assertTrue( specifyPortletDeploymentDescriptorPage.isBundleFilePathEnabled() );

        newPortletPage.finish();

        assertContains( "<portlet-name>New</portlet-name>", portletXmlPage.getText() );
        assertContains( "<resource-bundle>content.Language</resource-bundle>", portletXmlPage.getText() );

        TreeItemPageObject<SWTWorkbenchBot> languagePropertiesFile =
            new TreeItemPageObject<SWTWorkbenchBot>(
                bot, "test-portlet", "docroot/WEB-INF/src", "content", "Language.properties" );
        languagePropertiesFile.expand();
        languagePropertiesFile.isVisible();

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        // new portlet with more than two uppercase portlet class name
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
        TreeItemPageObject<SWTWorkbenchBot> languagePropertiesFile2 =
            new TreeItemPageObject<SWTWorkbenchBot>(
                bot, "test-portlet", "docroot/WEB-INF/src", "mycontent", "Lang.properties" );
        languagePropertiesFile2.expand();
        languagePropertiesFile2.isVisible();

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PORTLET );

        newPortletPage.createLiferayPortlet( TEXT_BLANK, "MyPortletPortlet", null, "javax.portlet.GenericPortlet" );
        newPortletPage.next();

        assertEquals( "my-portlet", specifyPortletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "My Portlet", specifyPortletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "My Portlet", specifyPortletDeploymentDescriptorPage.getPortletTitle() );

        assertFalse( specifyPortletDeploymentDescriptorPage.isCheckboxEnabled( CHECKBOX_LIFERAY_PORTLET_MODE_ABOUT ) );
        assertFalse( specifyPortletDeploymentDescriptorPage.isCheckboxEnabled( CHECKBOX_LIFERAY_PORTLET_MODE_CONFIG ) );
        assertFalse( specifyPortletDeploymentDescriptorPage.isCheckboxEnabled( CHECKBOX_LIFERAY_PORTLET_MODE_EDITDEFAULTS ) );
        assertFalse( specifyPortletDeploymentDescriptorPage.isCheckboxEnabled( CHECKBOX_LIFERAY_PORTLET_MODE_EDITGUEST ) );
        assertFalse( specifyPortletDeploymentDescriptorPage.isCheckboxEnabled( CHECKBOX_LIFERAY_PORTLET_MODE_PREVIEW ) );
        assertFalse( specifyPortletDeploymentDescriptorPage.isCheckboxEnabled( CHECKBOX_LIFERAY_PORTLET_MODE_PRINT ) );

        newPortletPage.finish();

        assertContains(
            "<init-param>\n\t\t\t<name>view-template</name>\n\t\t\t<value>/html/myportlet/view.jsp</value>\n\t\t</init-param>\n\t\t<expiration-cache>0</expiration-cache>\n\t\t<supports>\n\t\t\t<mime-type>text/html</mime-type>\n\t\t\t<portlet-mode>view</portlet-mode>\n\t\t</supports>\n\t\t<portlet-info>\n\t\t\t<title>My Portlet</title>\n\t\t\t<short-title>My Portlet</short-title>\n\t\t\t<keywords></keywords>\n\t\t</portlet-info>",
            portletXmlPage.getText() );
    }

    @After
    public void waitForCreate()
    {
        sleep( 5000 );
    }

}
