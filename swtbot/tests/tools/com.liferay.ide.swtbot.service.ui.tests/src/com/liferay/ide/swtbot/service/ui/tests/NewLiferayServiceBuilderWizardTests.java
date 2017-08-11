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

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ServiceBuilderWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewSdkProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.SetSDKLocationWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.NewJavaPackageWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.TextDialog;
import com.liferay.ide.swtbot.ui.page.CTabItem;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;
import com.liferay.ide.swtbot.ui.util.StringPool;

import java.io.File;
import java.io.IOException;

import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ying Xu
 */
public class NewLiferayServiceBuilderWizardTests extends SwtbotBase
{

    ServiceBuilderWizard newServiceBuilderWizard = new ServiceBuilderWizard( bot );

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
        ide.closeShell( NEW_LIFERAY_PLUGIN_PROJECT );
        ide.closeShell( NEW_SERVICE_BUILDER );

        try
        {
            viewAction.deleteProjectsExcludeNames( getLiferayPluginsSdkName() );
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

        Dialog noProjectDialog = new Dialog( bot, NEW_LIFERAY_SERVICE_BUILDER, NO, YES );

        noProjectDialog.confirm();

        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.waitForPageToOpen();
        createProjectWizard.createSDKProject( projectName, PORTLET, true );
        createProjectWizard.next();

        if( hasAddedProject )
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

        ServiceBuilderWizard newServiceBuilderWizard = new ServiceBuilderWizard( bot );

        // newServiceBuilderWizard.waitForPageToOpen();
        newServiceBuilderWizard.setFocus();

        // check initial state
        assertEquals( author, newServiceBuilderWizard.getAuthor().getText() );
        assertEquals( projectName + "-portlet", newServiceBuilderWizard.getIncludeSampleEntity().getText() );
        assertEquals( SERVICE_XML, newServiceBuilderWizard.getServiceFile().getText() );
        assertEquals( StringPool.BLANK, newServiceBuilderWizard.getPackagePath().getText() );
        assertEquals( StringPool.BLANK, newServiceBuilderWizard.getNamespace().getText() );
        assertTrue( newServiceBuilderWizard.getIncludeSampleEntity().isChecked() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        newServiceBuilderWizard.createServiceBuilder( "packagePath", "namespace" );

        // validation test for package path
        newServiceBuilderWizard.getPackagePath().setText( "_" );
        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        assertTrue( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getPackagePath().setText( "1" );
        assertEquals(
            INVALID_JAVA_PACKAGE_NAME + "'1' is not a valid Java identifier",
            newServiceBuilderWizard.getValidationMsg() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getPackagePath().setText( "-" );
        assertEquals(
            INVALID_JAVA_PACKAGE_NAME + "'-' is not a valid Java identifier",
            newServiceBuilderWizard.getValidationMsg() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getPackagePath().setText( "P" );
        assertEquals(
            " Warning: By convention, package names usually start with a lowercase letter",
            newServiceBuilderWizard.getValidationMsg() );
        assertTrue( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getPackagePath().setText( "a1" );
        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        assertTrue( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getPackagePath().setText( "a_" );
        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        assertTrue( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getPackagePath().setText( "a-" );
        assertEquals(
            INVALID_JAVA_PACKAGE_NAME + "'a-' is not a valid Java identifier",
            newServiceBuilderWizard.getValidationMsg() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getPackagePath().setText( "packagePath" );
        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        assertTrue( newServiceBuilderWizard.finishBtn().isEnabled() );

        // validation test for namespace
        newServiceBuilderWizard.getNamespace().setText( "a1" );
        assertEquals( THE_NAMESPACE_ELEMENT_MUST_BE_A_VALID_KEYWORD, newServiceBuilderWizard.getValidationMsg() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getNamespace().setText( "a-" );
        assertEquals( THE_NAMESPACE_ELEMENT_MUST_BE_A_VALID_KEYWORD, newServiceBuilderWizard.getValidationMsg() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getNamespace().setText( "a_" );
        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        assertTrue( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getNamespace().setText( "namespace" );
        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        assertTrue( newServiceBuilderWizard.finishBtn().isEnabled() );

        newServiceBuilderWizard.finish();
        sleep();

        Tree projectTree = viewAction.getProjects();

        String fileName = "service.xml";

        projectTree.expandNode( new String[] { projectName + "-portlet", "docroot", "WEB-INF" } ).doubleClick(
            fileName );

        Editor textEditor = ide.getEditor( fileName );

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

        CTabItem switchCTabItem = new CTabItem( bot, "Overview" );

        switchCTabItem.click();

        Tree outlineTree = new Tree( bot, 1 );

        TreeItem addEntities = new TreeItem( bot, outlineTree, "Service Builder", "Entities" );

        addEntities.doAction( "Add Entity" );

        Text requireAttributeName = new Text( bot, NAME );

        requireAttributeName.setText( "entity" );

        switchCTabItem = new CTabItem( bot, "Source" );

        switchCTabItem.click();
        keyPress.pressShortcut( ctrl, S );
        assertContains( "6.0.0", textEditor.getText() );

        textEditor.close();
        serviceXMLFile.delete();

        ide.getNewBtn().getLiferayServiceBuilder().click();
        newServiceBuilderWizard.createServiceBuilder( "packagePath1", "namespace1" );
        assertEquals( PROJECT_ALREADY_CONTAINS_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.cancel();

        viewAction.deleteProject( projectName + "-portlet" );
    }

    @Test
    public void createServiceBuilderWizardWithoutPortletProject()
    {
        Dialog noProjectDialog = new Dialog( bot, NEW_LIFERAY_SERVICE_BUILDER, NO, YES );

        noProjectDialog.confirm();

        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.waitForPageToOpen();
        createProjectWizard.cancel();
        noProjectDialog.cancel();

        /*
         * ServiceBuilderWizardPO newServiceBuilderWizard = new ServiceBuilderWizardPO( bot, NEW_SERVICE_BUILDER );
         * newServiceBuilderWizard.waitForPageToOpen();
         */

        newServiceBuilderWizard.setFocus();

        // check service builder default initial state
        assertEquals( author, newServiceBuilderWizard.getAuthor().getText() );
        assertEquals( StringPool.BLANK, newServiceBuilderWizard.getIncludeSampleEntity().getText() );
        assertEquals( SERVICE_XML, newServiceBuilderWizard.getServiceFile().getText() );
        assertEquals( StringPool.BLANK, newServiceBuilderWizard.getPackagePath().getText() );
        assertEquals( StringPool.BLANK, newServiceBuilderWizard.getNamespace().getText() );
        assertTrue( newServiceBuilderWizard.getIncludeSampleEntity().isChecked() );
        assertTrue( newServiceBuilderWizard.getBrowseButton().isEnabled() );
        assertTrue( newServiceBuilderWizard.cancelBtn().isEnabled() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );

        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        newServiceBuilderWizard.createServiceBuilder( "packagePath", "namespace" );
        assertEquals( ENTER_A_PROJECT_NAME, newServiceBuilderWizard.getValidationMsg() );
        newServiceBuilderWizard.cancel();

    }

    @Test
    public void createServiceBuilderWizardWithoutSmapleEntity()
    {
        String projectName = "sbwizardtestwithoutentity";

        Dialog noProjectDialog = new Dialog( bot, NEW_LIFERAY_SERVICE_BUILDER, NO, YES );

        noProjectDialog.confirm();

        NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot );

        createProjectWizard.waitForPageToOpen();
        createProjectWizard.createSDKProject( projectName, PORTLET, true );
        createProjectWizard.next();

        if( hasAddedProject )
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

        ServiceBuilderWizard newServiceBuilderWizard = new ServiceBuilderWizard( bot );

        // newServiceBuilderWizard.waitForPageToOpen();
        // assertTrue( newServiceBuilderWizard.getBrowseButton().isEnabled() );

        // package which is not exist test
        newServiceBuilderWizard.setFocus();
        newServiceBuilderWizard.getBrowseButton().click();

        TextDialog chooseOnePackage = new TextDialog( bot );

        chooseOnePackage.getText().setText( "a" );
        sleep( 200 );
        assertFalse( chooseOnePackage.confirmBtn().isEnabled() );
        chooseOnePackage.getText().setText( StringPool.BLANK );
        sleep( 200 );
        assertTrue( chooseOnePackage.confirmBtn().isEnabled() );
        chooseOnePackage.confirm();
        assertEquals( PACKAGE_PATH_CANNOT_BE_EMPTY, newServiceBuilderWizard.getValidationMsg() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.getPackagePath().setText( "path1" );
        assertEquals( NAMESPACE_EMPTY_BE_MESSAGE, newServiceBuilderWizard.getValidationMsg() );
        assertFalse( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.cancel();

        // new a java package
        Tree projectTree = viewAction.getProjects();

        ide.getNewBtn().getNewPackage().click();

        NewJavaPackageWizard newJavaPackage = new NewJavaPackageWizard( bot );

        newJavaPackage.getName().setText( "newpackage" );
        assertEquals( projectName + "-portlet/docroot/WEB-INF/src", newJavaPackage.getSourceFolder().getText() );
        newJavaPackage.finish();
        assertTrue(
            projectTree.expandNode( projectName + "-portlet", "docroot/WEB-INF/src", "newpackage" ).isVisible() );

        ide.getFileMenu().clickMenu( NEW, LIFERAY_SERVICE_BUILDER );
        newServiceBuilderWizard.getBrowseButton().click();
        chooseOnePackage.getText().setText( "newpackage" );
        assertTrue( chooseOnePackage.confirmBtn().isEnabled() );
        chooseOnePackage.confirm();
        newServiceBuilderWizard.getNamespace().setText( "namespace" );
        newServiceBuilderWizard.getAuthor().setText( author + "-v" );
        newServiceBuilderWizard.getIncludeSampleEntity().deselect();
        assertEquals( CREATE_A_NEW_SERVICE_BUILDER_XML_FILE, newServiceBuilderWizard.getValidationMsg() );
        assertTrue( newServiceBuilderWizard.finishBtn().isEnabled() );
        newServiceBuilderWizard.finish();

        String fileName = "service.xml";

        projectTree.expandNode( new String[] { projectName + "-portlet", "docroot", "WEB-INF" } ).doubleClick(
            fileName );

        Editor textEditor = ide.getEditor( fileName );

        assertTrue( textEditor.isActive() );

        assertContains( "newpackage", textEditor.getText() );
        assertContains( "namespace", textEditor.getText() );
        assertContains( author + "-v", textEditor.getText() );

        viewAction.deleteProject( projectName + "-portlet" );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        hasAddedProject = addedProjects();

        ide.getCreateLiferayProjectToolbar().getNewLiferayServiceBuilder().click();
    }

}
