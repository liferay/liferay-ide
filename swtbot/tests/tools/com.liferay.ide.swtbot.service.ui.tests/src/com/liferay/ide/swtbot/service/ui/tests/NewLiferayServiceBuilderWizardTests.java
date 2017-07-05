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

package com.liferay.ide.swtbot.service.ui.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.project.ui.tests.page.CreateProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SetSDKLocationPO;
import com.liferay.ide.swtbot.service.ui.tests.page.ServiceBuilderEntitiesPO;
import com.liferay.ide.swtbot.service.ui.tests.page.ServiceBuilderPackageSelectionPO;
import com.liferay.ide.swtbot.service.ui.tests.page.ServiceBuilderWizardPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.NewJavaPackagePO;
import com.liferay.ide.swtbot.ui.tests.page.CTabItemPO;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Ying Xu
 */
public class NewLiferayServiceBuilderWizardTests extends SWTBotBase implements ServiceBuilderWizard, ProjectWizard
{

    ServiceBuilderWizardPO newServiceBuilderWizard = new ServiceBuilderWizardPO( bot, TITLE_NEW_SERVICE_BUILDER );

    String author = System.getenv( "USERNAME" );

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
        eclipse.closeShell( TITLE_NEW_SERVICE_BUILDER );

        try
        {
            eclipse.getPackageExporerView().deleteProjectExcludeNames(
                new String[] { getLiferayPluginsSdkName() }, true );
        }
        catch( Exception e )
        {
        }
    }

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipServer();
        unzipPluginsSDK();
    }

    @Test
    public void createServiceBuilderWizard() throws Exception
    {
        String projectName = "sbwizardtest";

        DialogPO noProjectDialog = new DialogPO( bot, LABEL_NEW_LIFERAY_SERVICE_BUILDER, BUTTON_NO, BUTTON_YES );

        noProjectDialog.confirm();

        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot, LABEL_NEW_LIFERAY_PLUGIN_PROJECT );

        createProjectWizard.waitForPageToOpen();
        createProjectWizard.createSDKProject( projectName, MENU_PORTLET, true );
        createProjectWizard.next();

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

        ServiceBuilderWizardPO newServiceBuilderWizard = new ServiceBuilderWizardPO( bot, TITLE_NEW_SERVICE_BUILDER );

        // newServiceBuilderWizard.waitForPageToOpen();
        newServiceBuilderWizard.setFocus();

        // check initial state
        assertEquals( author, newServiceBuilderWizard.getAuthorText().getText() );
        assertEquals( projectName + "-portlet", newServiceBuilderWizard.getPluginProjectComboBox().getText() );
        assertEquals( TEXT_SERVICE_FILE_VALUE, newServiceBuilderWizard.getServiceFileText().getText() );
        assertEquals( TEXT_DEFAULT_PACKAGE_PAHT_VALUE, newServiceBuilderWizard.getPackagePathText().getText() );
        assertEquals( TEXT_DEFAULT_NAMESPACE_VALUE, newServiceBuilderWizard.getNamespaceText().getText() );
        assertTrue( newServiceBuilderWizard.getIncludeSampleEntityCheckBox().isChecked() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        newServiceBuilderWizard.NewServiceBuilder( "packagePath", "namespace" );

        // validation test for package path
        newServiceBuilderWizard.getPackagePathText().setText( "_" );
        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        assertTrue( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getPackagePathText().setText( "1" );
        assertEquals(
            TEXT_VALIDATION_PACKAGE_PATH_MESSAGE + "'1' is not a valid Java identifier",
            newServiceBuilderWizard.getValidationMessage() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getPackagePathText().setText( "-" );
        assertEquals(
            TEXT_VALIDATION_PACKAGE_PATH_MESSAGE + "'-' is not a valid Java identifier",
            newServiceBuilderWizard.getValidationMessage() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getPackagePathText().setText( "P" );
        assertEquals(
            " Warning: By convention, package names usually start with a lowercase letter",
            newServiceBuilderWizard.getValidationMessage() );
        assertTrue( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getPackagePathText().setText( "a1" );
        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        assertTrue( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getPackagePathText().setText( "a_" );
        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        assertTrue( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getPackagePathText().setText( "a-" );
        assertEquals(
            TEXT_VALIDATION_PACKAGE_PATH_MESSAGE + "'a-' is not a valid Java identifier",
            newServiceBuilderWizard.getValidationMessage() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getPackagePathText().setText( "packagePath" );
        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        assertTrue( newServiceBuilderWizard.finishButton().isEnabled() );

        // validation test for namespace
        newServiceBuilderWizard.getNamespaceText().setText( "a1" );
        assertEquals( TEXT_VALIDATION_NAMESPACE_MESSAGE, newServiceBuilderWizard.getValidationMessage() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getNamespaceText().setText( "a-" );
        assertEquals( TEXT_VALIDATION_NAMESPACE_MESSAGE, newServiceBuilderWizard.getValidationMessage() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getNamespaceText().setText( "a_" );
        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        assertTrue( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getNamespaceText().setText( "namespace" );
        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        assertTrue( newServiceBuilderWizard.finishButton().isEnabled() );

        newServiceBuilderWizard.finish();
        sleep();

        TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

        String fileName = "service.xml";

        projectTree.expandNode( new String[] { projectName + "-portlet", "docroot", "WEB-INF" } ).doubleClick(
            fileName );

        TextEditorPO textEditor = eclipse.getTextEditor( fileName );

        assertTrue( textEditor.isActive() );
        assertContains( "Foo", textEditor.getText() );
        assertContains( "packagePath", textEditor.getText() );
        assertContains( "namespace", textEditor.getText() );
        assertContains( author, textEditor.getText() );

        Keyboard keyPress = KeyboardFactory.getAWTKeyboard();

        // regression test for IDE-1395,check 9 items which start with sb
        // to do in the future

        // regression test for IDE-726 and IDE-777,check DTD version
        assertContains( SERVICE_BUILDER_DTD_VERSION, textEditor.getText() );

        textEditor.close();

        File serviceXMLFile = getLiferayBundlesPath().append( fileName ).toFile();

        if( !serviceXMLFile.exists() )
        {
            try
            {
                serviceXMLFile.createNewFile();
            }
            catch( IOException e )
            {
            }
        }

        openFile( serviceXMLFile.getAbsolutePath() );

        assertTrue( textEditor.isActive() );

        CTabItemPO switchCTabItem = new CTabItemPO( bot, "Overview" );

        switchCTabItem.click();

        TreePO outlineTree = new TreePO( bot, 1 );

        TreeItemPO addEntities = new TreeItemPO( bot, outlineTree, "Service Builder", "Entities" );

        addEntities.doAction( "Add Entity" );

        ServiceBuilderEntitiesPO requireAttributeName = new ServiceBuilderEntitiesPO( bot );

        requireAttributeName.SetServiceBuilderEntitiesName( "entity" );

        switchCTabItem = new CTabItemPO( bot, "Source" );

        switchCTabItem.click();
        keyPress.pressShortcut( ctrl, S );
        assertContains( "6.0.0", textEditor.getText() );

        textEditor.close();
        serviceXMLFile.delete();

        eclipse.getNewToolbar().getLiferayServiceBuilder().click();
        newServiceBuilderWizard.NewServiceBuilder( "packagePath1", "namespace1" );
        assertEquals(
            TEXT_ALREADY_HAS_SERVICE_BUILDER_XML_FILE_MESSAGE, newServiceBuilderWizard.getValidationMessage() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.cancel();

        eclipse.getPackageExporerView().deleteResouceByName( projectName + "-portlet", true );
    }

    @Test
    public void createServiceBuilderWizardWithoutPortletProject()
    {
        DialogPO noProjectDialog = new DialogPO( bot, LABEL_NEW_LIFERAY_SERVICE_BUILDER, BUTTON_NO, BUTTON_YES );

        noProjectDialog.confirm();

        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot, LABEL_NEW_LIFERAY_PLUGIN_PROJECT );

        createProjectWizard.waitForPageToOpen();
        createProjectWizard.cancel();
        noProjectDialog.cancel();

        /*
         * ServiceBuilderWizardPO newServiceBuilderWizard = new ServiceBuilderWizardPO( bot, TITLE_NEW_SERVICE_BUILDER
         * ); newServiceBuilderWizard.waitForPageToOpen();
         */

        newServiceBuilderWizard.setFocus();

        // check service builder default initial state
        assertEquals( author, newServiceBuilderWizard.getAuthorText().getText() );
        assertEquals( TEXT_DEFAULT_PLUGIN_PROJECT_VALUE, newServiceBuilderWizard.getPluginProjectComboBox().getText() );
        assertEquals( TEXT_SERVICE_FILE_VALUE, newServiceBuilderWizard.getServiceFileText().getText() );
        assertEquals( TEXT_DEFAULT_PACKAGE_PAHT_VALUE, newServiceBuilderWizard.getPackagePathText().getText() );
        assertEquals( TEXT_DEFAULT_NAMESPACE_VALUE, newServiceBuilderWizard.getNamespaceText().getText() );
        assertTrue( newServiceBuilderWizard.getIncludeSampleEntityCheckBox().isChecked() );
        assertTrue( newServiceBuilderWizard.getBrowseButton().isEnabled() );
        assertTrue( newServiceBuilderWizard.cancelButton().isEnabled() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );

        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        newServiceBuilderWizard.NewServiceBuilder( "packagePath", "namespace" );
        assertEquals( TEXT_ENTER_A_PROJECT_NAME, newServiceBuilderWizard.getValidationMessage() );
        newServiceBuilderWizard.cancel();

    }

    @Test
    public void createServiceBuilderWizardWithoutSmapleEntity()
    {
        String projectName = "sbwizardtestwithoutentity";

        DialogPO noProjectDialog = new DialogPO( bot, LABEL_NEW_LIFERAY_SERVICE_BUILDER, BUTTON_NO, BUTTON_YES );

        noProjectDialog.confirm();

        CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot, LABEL_NEW_LIFERAY_PLUGIN_PROJECT );

        createProjectWizard.waitForPageToOpen();
        createProjectWizard.createSDKProject( projectName, MENU_PORTLET, true );
        createProjectWizard.next();

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

        ServiceBuilderWizardPO newServiceBuilderWizard = new ServiceBuilderWizardPO( bot, TITLE_NEW_SERVICE_BUILDER );

        // newServiceBuilderWizard.waitForPageToOpen();
        // assertTrue( newServiceBuilderWizard.getBrowseButton().isEnabled() );

        // package which is not exist test
        newServiceBuilderWizard.setFocus();
        newServiceBuilderWizard.getBrowseButton().click();

        ServiceBuilderPackageSelectionPO chooseOnePackage =
            new ServiceBuilderPackageSelectionPO( bot, BUTTON_CANCEL, BUTTON_OK );

        chooseOnePackage.selectPackage( "a" );
        sleep( 200 );
        assertFalse( chooseOnePackage.getOkButton().isEnabled() );
        chooseOnePackage.selectPackage( "" );
        sleep( 200 );
        assertTrue( chooseOnePackage.getOkButton().isEnabled() );
        chooseOnePackage.confirm();
        assertEquals( TEXT_PACKAGE_PATH_EMPTY_MESSAGE, newServiceBuilderWizard.getValidationMessage() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.getPackagePathText().setText( "path1" );
        assertEquals( TEXT_NAMESPACE_EMPTY_MESSAGE, newServiceBuilderWizard.getValidationMessage() );
        assertFalse( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.cancel();

        // new a java package
        TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

        eclipse.getNewToolbar().getNewPackage().click();

        NewJavaPackagePO newJavaPackage = new NewJavaPackagePO( bot );

        newJavaPackage.setName( "newpackage" );
        assertEquals( projectName + "-portlet/docroot/WEB-INF/src", newJavaPackage.getSourceFolderText().getText() );
        newJavaPackage.finish();
        assertTrue(
            projectTree.expandNode( projectName + "-portlet", "docroot/WEB-INF/src", "newpackage" ).isVisible() );

        eclipse.getFileMenu().clickMenu( MENU_NEW, LABEL_LIFERAY_SERVICE_BUILDER );
        newServiceBuilderWizard.getBrowseButton().click();
        chooseOnePackage.selectPackage( "newpackage" );
        assertTrue( chooseOnePackage.getOkButton().isEnabled() );
        chooseOnePackage.confirm();
        newServiceBuilderWizard.getNamespaceText().setText( "namespace" );
        newServiceBuilderWizard.getAuthorText().setText( author + "-v" );
        newServiceBuilderWizard.getIncludeSampleEntityCheckBox().deselect();
        assertEquals( TEXT_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMessage() );
        assertTrue( newServiceBuilderWizard.finishButton().isEnabled() );
        newServiceBuilderWizard.finish();

        String fileName = "service.xml";

        projectTree.expandNode( new String[] { projectName + "-portlet", "docroot", "WEB-INF" } ).doubleClick(
            fileName );

        TextEditorPO textEditor = eclipse.getTextEditor( fileName );

        assertTrue( textEditor.isActive() );

        assertContains( "newpackage", textEditor.getText() );
        assertContains( "namespace", textEditor.getText() );
        assertContains( author + "-v", textEditor.getText() );

        eclipse.getPackageExporerView().deleteResouceByName( projectName + "-portlet", true );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        hasAddedProject = addedProjects();

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayServiceBuilder().click();
    }

    @After
    public void waitForCreate()
    {
    }

}
