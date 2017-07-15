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

import java.io.IOException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.NewLiferayComponentWizardUI;
import com.liferay.ide.swtbot.liferay.ui.SWTBotBase;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.AddOverriddenFileDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.ComponentModelClassSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.ComponentPackageSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.FragmentHostOSGIBundleDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.SelectModuleServiceNameDialog;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayComponentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayServerRuntimeWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayServerWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewModuleFragmentWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewModuleFragmentWizardSecondPageWizard;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Tree;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class NewLiferayComponentWizardTests extends SWTBotBase implements NewLiferayComponentWizardUI
{

    static String projectName = "testComponent";

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @BeforeClass
    public static void initWizard() throws IOException
    {

        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        ide.getLiferayWorkspacePerspective().activate();

        unzipServer();

        NewLiferayServerWizard newServer = new NewLiferayServerWizard( bot );

        NewLiferayServerRuntimeWizard setRuntime = new NewLiferayServerRuntimeWizard( bot );

        ide.getCreateLiferayProjectToolbar().getNewLiferayServer().click();

        newServer.getServerTypeTree().selectTreeItem( NODE_LIFERAY_INC, NODE_LIFERAY_7X );
        newServer.next();

        setRuntime.getServerLocation().setText( getLiferayServerDir().toOSString() );

        setRuntime.finish();
    }

    NewLiferayModuleProjectWizard createModuleProjectWizard = new NewLiferayModuleProjectWizard( bot );

    NewLiferayComponentWizard newLiferayComponentWizard =
        new NewLiferayComponentWizard( bot, LABEL_NEW_LIFERAY_COMPONENT, INDEX_VALIDATION_MESSAGE );

    NewLiferayComponentWizard newLiferayComponentWizardWithNewId =
        new NewLiferayComponentWizard( bot, LABEL_NEW_LIFERAY_COMPONENT, INDEX_MODEL_LISTENER_VALIDATION_MESSAGE );

    NewModuleFragmentWizard newModuleFragmentProject = new NewModuleFragmentWizard( bot );

    ComponentPackageSelectionDialog selectPackageName = new ComponentPackageSelectionDialog( bot );

    NewModuleFragmentWizardSecondPageWizard setModuleFragmentOSGiBundle =
        new NewModuleFragmentWizardSecondPageWizard( bot );

    public void clickWizardSelectProjectAndTemplate( String projectName, String componentClassTemplate )
    {

        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        newLiferayComponentWizard.getProjectNames().setSelection( ( projectName ) );

        if( !componentClassTemplate.equals( newLiferayComponentWizard.getComponentClassTemplates().getText() ) )
        {
            newLiferayComponentWizard.getComponentClassTemplates().setSelection( ( componentClassTemplate ) );
        }

        assertEquals( TEXT_DEFAULT_PACKAGE_NAME_VALUE, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );

        assertEquals( componentClassTemplate, newLiferayComponentWizard.getComponentClassTemplates().getText() );
    }

    public void createModuleProject( String templateName )
    {

        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();

        createModuleProjectWizard.createModuleProject( projectName );

        if( !( templateName.equals( createModuleProjectWizard.getProjectTemplateNames().getText() ) ||
            templateName.equals( TEXT_BLANK ) ) )
        {
            createModuleProjectWizard.getProjectTemplateNames().setSelection( templateName );
        }

        sleep();

        if( !createModuleProjectWizard.finishBtn().isEnabled() )
        {
            sleep( 120000 );
        }

        createModuleProjectWizard.finish();

        sleep( 15000 );

    }

    @Test
    public void newComponentClassOnModelListenerTest()
    {
        if( !hasAddedProject )
        {
            createModuleProject( TEXT_BLANK );
        }

        clickWizardSelectProjectAndTemplate( projectName, MENU_TEMPLATE_MODEL_LISTENER );

        newLiferayComponentWizard.getPackageBrowseBtn().click();

        ComponentPackageSelectionDialog selectPackageName = new ComponentPackageSelectionDialog( bot );

        selectPackageName.getPackageSelection().setText( "content" );
        selectPackageName.confirm();

        // validation test for model class
        assertEquals( "", newLiferayComponentWizard.getModelClassName().getText() );

        newLiferayComponentWizard.getModelClassName().setText( "tt" );
        sleep();

        assertEquals(
            " \"tt\"" + TEXT_NOT_AMONG_POSSIBLE_VALUES_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getModelClassName().setText( "1" );
        sleep();

        assertEquals(
            " \"1\"" + TEXT_NOT_AMONG_POSSIBLE_VALUES_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getModelClassName().setText( "-" );
        sleep();

        assertEquals(
            " \"-\"" + TEXT_NOT_AMONG_POSSIBLE_VALUES_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getModelClassName().setText( "." );
        sleep();

        assertEquals(
            " \".\"" + TEXT_NOT_AMONG_POSSIBLE_VALUES_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getModelClassName().setText( "" );
        sleep();

        assertEquals( TEXT_VALIDATION_MODEL_LISTENER_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getBrowseBtn().click();
        sleep( 5000 );

        ComponentModelClassSelectionDialog selectModelClass = new ComponentModelClassSelectionDialog( bot );

        selectModelClass.getModelClassSelection().setText( "tt" );

        sleep();
        assertFalse( selectModelClass.confirmBtn().isEnabled() );

        selectModelClass.getModelClassSelection().setText( "*com.liferay.blogs.kernel.model.BlogsEntry" );

        sleep();
        selectModelClass.confirm();
        sleep( 2000 );

        assertEquals(
            "com.liferay.blogs.kernel.model.BlogsEntry", newLiferayComponentWizard.getModelClassName().getText() );
        newLiferayComponentWizard.finish();
        sleep( 5000 );

        Tree projectTree = ide.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentModelListener.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        Editor checkJavaFile = ide.getEditor( javaFileName );

        assertContains( "TestcomponentModelListener extends BaseModelListener<BlogsEntry>", checkJavaFile.getText() );

        checkJavaFile.close();

    }

    @Test
    public void newComponentClassOnPortletTest()
    {
        if( !hasAddedProject )
        {
            createModuleProject( TEXT_BLANK );
        }

        clickWizardSelectProjectAndTemplate( projectName, TEXT_DEFAULT_COMPONENT_CLASS_TEMPLATE_VALUE );

        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizard.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        // validation test for Package name
        newLiferayComponentWizard.getPackageName().setText( "1" );

        sleep( 1000 );
        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "-" );

        sleep( 1000 );
        assertEquals( " \"-\"" + TEXT_VALIDATION_PACKAGE_NAME_MESSAGE, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "." );

        sleep( 1000 );
        assertEquals( " \".\"" + TEXT_VALIDATION_PACKAGE_NAME_MESSAGE, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "/" );

        sleep( 1000 );
        assertEquals( " \"/\"" + TEXT_VALIDATION_PACKAGE_NAME_MESSAGE, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "a" );

        sleep( 1000 );
        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizard.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getPackageBrowseBtn().click();

        selectPackageName.getPackageSelection().setText( "content" );
        selectPackageName.confirm();

        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizard.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        // validation test for Component Class Name
        newLiferayComponentWizard.getComponentClassName().setText( "1" );
        sleep( 1000 );

        assertEquals( TEXT_INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "-" );
        sleep( 1000 );

        assertEquals( TEXT_INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "." );
        sleep( 1000 );

        assertEquals( TEXT_INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "/" );
        sleep( 1000 );

        assertEquals( TEXT_INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "a" );
        sleep( 1000 );

        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizard.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "" );
        newLiferayComponentWizard.finish();
        sleep( 5000 );

        Tree projectTree = ide.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentPortlet.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        Editor checkJavaFile = ide.getEditor( javaFileName );

        assertContains( "TestcomponentPortlet extends GenericPortlet", checkJavaFile.getText() );

        checkJavaFile.close();

    }

    @Test
    public void newComponentClassOnServiceWrapperTest()
    {
        if( !hasAddedProject )
        {
            createModuleProject( TEXT_BLANK );
        }

        clickWizardSelectProjectAndTemplate( projectName, MENU_TEMPLATE_SERVICE_WRAPPER );

        assertEquals( "", newLiferayComponentWizard.getServiceName().getText() );

        newLiferayComponentWizard.getServiceName().setText( "tt" );
        sleep();

        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertTrue( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getServiceName().setText( "" );
        sleep();

        assertEquals( TEXT_VALIDATION_SERVICE_WRAPPER_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.getPackageBrowseBtn().click();

        selectPackageName.getPackageSelection().setText( "content" );
        selectPackageName.confirm();

        newLiferayComponentWizard.getBrowseBtn().click();
        sleep( 5000 );

        SelectModuleServiceNameDialog selectServiceName = new SelectModuleServiceNameDialog( bot );

        selectServiceName.getServiceName().setText( "gg" );
        sleep();
        assertFalse( selectServiceName.confirmBtn().isEnabled() );

        selectServiceName.getServiceName().setText( "*bookmarksEntryLocal" );
        sleep();
        assertTrue( selectServiceName.confirmBtn().isEnabled() );

        selectServiceName.confirm();
        sleep( 2000 );
        assertEquals(
            "com.liferay.bookmarks.service.BookmarksEntryLocalServiceWrapper",
            newLiferayComponentWizard.getServiceName().getText() );

        newLiferayComponentWizard.finish();
        sleep( 5000 );

        Tree projectTree = ide.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentServiceHook.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        Editor checkJavaFile = ide.getEditor( javaFileName );

        assertContains( "TestcomponentServiceHook extends BookmarksEntryLocalServiceWrapper", checkJavaFile.getText() );

        checkJavaFile.close();

    }

    @Test
    public void newComponentClassOnThemeModuleTest()
    {
        if( hasAddedProject )
        {
            ide.getProjectTree().getTreeItem( projectName ).collapse();

            ide.getPackageExporerView().deleteResouceByName( projectName, true );
        }

        createModuleProject( "theme" );

        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        assertEquals( TEXT_DEFAULT_PROJECT_NAME_VALUE, newLiferayComponentWizardWithNewId.getProjectNames().getText() );
        assertEquals( TEXT_DEFAULT_PACKAGE_NAME_VALUE, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_TEMPLATE_VALUE,
            newLiferayComponentWizard.getComponentClassTemplates().getText() );

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.cancel();

        ide.getPackageExporerView().deleteResouceByName( projectName, true );

    }

    @Test
    public void newLiferayComponentClassWithoutAvailableModuleProjectTest()
    {

        if( hasAddedProject )
        {
            ide.getPackageExporerView().deleteResouceByName( projectName, true );
        }

        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        // check default initial state
        assertEquals( TEXT_DEFAULT_PROJECT_NAME_VALUE, newLiferayComponentWizard.getProjectNames().getText() );
        assertEquals( TEXT_DEFAULT_PACKAGE_NAME_VALUE, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_TEMPLATE_VALUE,
            newLiferayComponentWizard.getComponentClassTemplates().getText() );
        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        String[] componentTemplateItems = newLiferayComponentWizard.getComponentClassTemplates().items();

        assertTrue( componentTemplateItems.length >= 1 );

        assertEquals( expectedComponentTemplateItems.length, componentTemplateItems.length );

        for( int i = 0; i < componentTemplateItems.length; i++ )
        {
            assertTrue( componentTemplateItems[i].equals( expectedComponentTemplateItems[i] ) );
        }

        newLiferayComponentWizard.cancel();

        // create Liferay Fragment project
        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleFragmentProject().click();

        newModuleFragmentProject.getProjectName().setText( "fragmentTest" );
        newModuleFragmentProject.next();

        // select OSGi Bundle and Overridden files

        setModuleFragmentOSGiBundle.getBrowseOSGiBundleBtn().click();

        FragmentHostOSGIBundleDialog selectOSGiBundle = new FragmentHostOSGIBundleDialog( bot );

        AddOverriddenFileDialog addJSPFiles = new AddOverriddenFileDialog( bot );

        selectOSGiBundle.getOsgiBundle().setText( "*com.liferay.bookmarks.web" );
        selectOSGiBundle.confirm();

        setModuleFragmentOSGiBundle.getAddOverridFilesBtn().click();
        addJSPFiles.getAddFilesToOverride().selectTreeItem( "META-INF/resources/bookmarks/view.jsp" );
        addJSPFiles.confirm();

        setModuleFragmentOSGiBundle.finish();
        sleep( 15000 );

        // open component wizard again then check state to make sure it couldn't
        // support new component class
        ide.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        // check default state again
        assertEquals( TEXT_DEFAULT_PROJECT_NAME_VALUE, newLiferayComponentWizard.getProjectNames().getText() );
        assertEquals( TEXT_DEFAULT_PACKAGE_NAME_VALUE, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_TEMPLATE_VALUE,
            newLiferayComponentWizard.getComponentClassTemplates().getText() );
        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMsg() );
        assertFalse( newLiferayComponentWizard.finishBtn().isEnabled() );

        newLiferayComponentWizard.cancel();

        ide.getPackageExporerView().deleteResouceByName( "fragmentTest", true );
    }

    @Before
    public void shouldRunTests()
    {

        Assume.assumeTrue( runTest() || runAllTests() );

        hasAddedProject = addedProjects();

    }

}
