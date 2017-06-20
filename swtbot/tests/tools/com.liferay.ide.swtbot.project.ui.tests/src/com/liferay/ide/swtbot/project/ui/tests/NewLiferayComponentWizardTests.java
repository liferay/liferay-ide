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

import com.liferay.ide.swtbot.project.ui.tests.page.AddOverriddenFilePO;
import com.liferay.ide.swtbot.project.ui.tests.page.ComponentModelClassSelectionPO;
import com.liferay.ide.swtbot.project.ui.tests.page.ComponentPackageSelectionPO;
import com.liferay.ide.swtbot.project.ui.tests.page.FragmentHostOSGIBundlePO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayComponentWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayModuleProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayServerPO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayServerRuntimePO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewModuleFragmentWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewModuleFragmentWizardSecondPagePO;
import com.liferay.ide.swtbot.project.ui.tests.page.SelectModuleServiceNamePO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Ying Xu
 * @author Ashley Yuan
 */
public class NewLiferayComponentWizardTests extends SWTBotBase
    implements NewLiferayComponentWizard, NewLiferayModuleProjectWizard, NewServerRuntimeWizard
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

        eclipse.getLiferayWorkspacePerspective().activate();

        unzipServer();

        NewLiferayServerPO newServer = new NewLiferayServerPO( bot );

        NewLiferayServerRuntimePO setRuntime = new NewLiferayServerRuntimePO( bot );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayServer().click();

        newServer.getServerTypeTree().selectTreeItem( NODE_LIFERAY_INC, NODE_LIFERAY_7X );
        newServer.next();

        setRuntime.getServerLocation().setText( getLiferayServerDir().toOSString() );

        setRuntime.finish();
    }

    NewLiferayModuleProjectWizardPO createModuleProjectWizard = new NewLiferayModuleProjectWizardPO( bot );

    NewLiferayComponentWizardPO newLiferayComponentWizard =
        new NewLiferayComponentWizardPO( bot, LABEL_NEW_LIFERAY_COMPONENT, INDEX_VALIDATION_MESSAGE );

    NewLiferayComponentWizardPO newLiferayComponentWizardWithNewId =
        new NewLiferayComponentWizardPO( bot, LABEL_NEW_LIFERAY_COMPONENT, INDEX_MODEL_LISTENER_VALIDATION_MESSAGE );

    NewModuleFragmentWizardPO newModuleFragmentProject = new NewModuleFragmentWizardPO( bot );

    ComponentPackageSelectionPO selectPackageName = new ComponentPackageSelectionPO( bot, BUTTON_OK );

    NewModuleFragmentWizardSecondPagePO setModuleFragmentOSGiBundle = new NewModuleFragmentWizardSecondPagePO( bot );

    public void clickWizardSelectProjectAndTemplate( String projectName, String componentClassTemplate )
    {

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        newLiferayComponentWizard.selectProject( projectName );

        if( !componentClassTemplate.equals( newLiferayComponentWizard.getComponentClassTemplate().getText() ) )
        {
            newLiferayComponentWizard.selectTemplate( componentClassTemplate );
        }

        assertEquals( TEXT_DEFAULT_PACKAGE_NAME_VALUE, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );

        assertEquals( componentClassTemplate, newLiferayComponentWizard.getComponentClassTemplate().getText() );
    }

    public void createModuleProject( String templateName )
    {

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();

        createModuleProjectWizard.createModuleProject( projectName );

        if( !( templateName.equals( createModuleProjectWizard.getProjectTemplateNameComboBox().getText() ) ||
            templateName.equals( TEXT_BLANK ) ) )
        {
            createModuleProjectWizard.getProjectTemplateNameComboBox().setSelection( templateName );
        }

        sleep();

        if( !createModuleProjectWizard.finishButton().isEnabled() )
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

        newLiferayComponentWizard.getPackageBrowseButton().click();

        ComponentPackageSelectionPO selectPackageName = new ComponentPackageSelectionPO( bot, BUTTON_OK );

        selectPackageName.setPackageSelectionText( "content" );
        selectPackageName.confirm();

        // validation test for model class
        assertEquals( "", newLiferayComponentWizard.getModelClass().getText() );

        newLiferayComponentWizard.setModelClassName( "tt" );
        sleep();

        assertEquals( " \"tt\"" + TEXT_NOT_AMONG_POSSIBLE_VALUES_MESSAGE,
            newLiferayComponentWizardWithNewId.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.setModelClassName( "1" );
        sleep();

        assertEquals( " \"1\"" + TEXT_NOT_AMONG_POSSIBLE_VALUES_MESSAGE,
            newLiferayComponentWizardWithNewId.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.setModelClassName( "-" );
        sleep();

        assertEquals( " \"-\"" + TEXT_NOT_AMONG_POSSIBLE_VALUES_MESSAGE,
            newLiferayComponentWizardWithNewId.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.setModelClassName( "." );
        sleep();

        assertEquals( " \".\"" + TEXT_NOT_AMONG_POSSIBLE_VALUES_MESSAGE,
            newLiferayComponentWizardWithNewId.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.setModelClassName( "" );
        sleep();

        assertEquals( TEXT_VALIDATION_MODEL_LISTENER_MESSAGE,
            newLiferayComponentWizardWithNewId.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.getBrowseButton().click();
        sleep( 5000 );

        ComponentModelClassSelectionPO selectModelClass = new ComponentModelClassSelectionPO( bot, BUTTON_OK );

        selectModelClass.setModelClassSelectionText( "tt" );

        sleep();
        assertFalse( selectModelClass.confirmButton().isEnabled() );

        selectModelClass.setModelClassSelectionText( "*com.liferay.blogs.kernel.model.BlogsEntry" );

        sleep();
        selectModelClass.confirm();
        sleep( 2000 );

        assertEquals( "com.liferay.blogs.kernel.model.BlogsEntry",
            newLiferayComponentWizard.getModelClass().getText() );
        newLiferayComponentWizard.finish();
        sleep( 5000 );

        TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentModelListener.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        TextEditorPO checkJavaFile = eclipse.getTextEditor( javaFileName );

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

        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizard.getValidationMessage() );
        assertTrue( newLiferayComponentWizard.finishButton().isEnabled() );

        // validation test for Package name
        newLiferayComponentWizard.getPackageName().setText( "1" );

        sleep( 1000 );
        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "-" );

        sleep( 1000 );
        assertEquals( " \"-\"" + TEXT_VALIDATION_PACKAGE_NAME_MESSAGE,
            newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "." );

        sleep( 1000 );
        assertEquals( " \".\"" + TEXT_VALIDATION_PACKAGE_NAME_MESSAGE,
            newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "/" );

        sleep( 1000 );
        assertEquals( " \"/\"" + TEXT_VALIDATION_PACKAGE_NAME_MESSAGE,
            newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );
        newLiferayComponentWizard.getPackageName().setText( "a" );

        sleep( 1000 );
        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizard.getValidationMessage() );
        assertTrue( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.getPackageBrowseButton().click();

        selectPackageName.setPackageSelectionText( "content" );
        selectPackageName.confirm();

        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizard.getValidationMessage() );
        assertTrue( newLiferayComponentWizard.finishButton().isEnabled() );

        // validation test for Component Class Name
        newLiferayComponentWizard.getComponentClassName().setText( "1" );
        sleep( 1000 );

        assertEquals( TEXT_INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "-" );
        sleep( 1000 );

        assertEquals( TEXT_INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "." );
        sleep( 1000 );

        assertEquals( TEXT_INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "/" );
        sleep( 1000 );

        assertEquals( TEXT_INVALID_CLASS_NAME, newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "a" );
        sleep( 1000 );

        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizard.getValidationMessage() );
        assertTrue( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.getComponentClassName().setText( "" );
        newLiferayComponentWizard.finish();
        sleep( 5000 );

        TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentPortlet.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        TextEditorPO checkJavaFile = eclipse.getTextEditor( javaFileName );

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

        newLiferayComponentWizard.setServiceName( "tt" );
        sleep();

        assertEquals( TEXT_CREATE_COMPONENT_CLASS_MESSAGE, newLiferayComponentWizardWithNewId.getValidationMessage() );
        assertTrue( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.setServiceName( "" );
        sleep();

        assertEquals( TEXT_VALIDATION_SERVICE_WRAPPER_MESSAGE,
            newLiferayComponentWizardWithNewId.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.getPackageBrowseButton().click();

        selectPackageName.setPackageSelectionText( "content" );
        selectPackageName.confirm();

        newLiferayComponentWizard.getBrowseButton().click();
        sleep( 5000 );

        SelectModuleServiceNamePO selectServiceName = new SelectModuleServiceNamePO( bot );

        selectServiceName.selectServiceName( "gg" );
        sleep();
        assertFalse( selectServiceName.confirmButton().isEnabled() );

        selectServiceName.selectServiceName( "*bookmarksEntryLocal" );
        sleep();
        assertTrue( selectServiceName.confirmButton().isEnabled() );

        selectServiceName.confirm();
        sleep( 2000 );
        assertEquals( "com.liferay.bookmarks.service.BookmarksEntryLocalServiceWrapper",
            newLiferayComponentWizard.getServiceName().getText() );

        newLiferayComponentWizard.finish();
        sleep( 5000 );

        TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

        String javaFileName = "TestcomponentServiceHook.java";

        assertTrue( projectTree.expandNode( projectName, "src/main/java", "content", javaFileName ).isVisible() );

        projectTree.expandNode( projectName, "src/main/java", "content" ).doubleClick( javaFileName );

        TextEditorPO checkJavaFile = eclipse.getTextEditor( javaFileName );

        assertContains( "TestcomponentServiceHook extends BookmarksEntryLocalServiceWrapper", checkJavaFile.getText() );

        checkJavaFile.close();

    }

    @Test
    public void newComponentClassOnThemeModuleTest()
    {
        if( hasAddedProject )
        {
            eclipse.getProjectTree().getTreeItem( projectName ).collapse();

            eclipse.getPackageExporerView().deleteResouceByName( projectName, true );
        }

        createModuleProject( MENU_MODULE_THEME );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        assertEquals( TEXT_DEFAULT_PROJECT_NAME_VALUE, newLiferayComponentWizardWithNewId.getProjectName().getText() );
        assertEquals( TEXT_DEFAULT_PACKAGE_NAME_VALUE, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_TEMPLATE_VALUE,
            newLiferayComponentWizard.getComponentClassTemplate().getText() );

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.cancel();

        eclipse.getPackageExporerView().deleteResouceByName( projectName, true );

    }

    @Test
    public void newLiferayComponentClassWithoutAvailableModuleProjectTest()
    {

        if( hasAddedProject )
        {
            eclipse.getPackageExporerView().deleteResouceByName( projectName, true );
        }

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        // check default initial state
        assertEquals( TEXT_DEFAULT_PROJECT_NAME_VALUE, newLiferayComponentWizard.getProjectName().getText() );
        assertEquals( TEXT_DEFAULT_PACKAGE_NAME_VALUE, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_TEMPLATE_VALUE,
            newLiferayComponentWizard.getComponentClassTemplate().getText() );
        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        String[] componentTemplateItems =
            newLiferayComponentWizard.getComponentClassTemplate().getAvailableComboValues();

        assertTrue( componentTemplateItems.length >= 1 );

        assertEquals( expectedComponentTemplateItems.length, componentTemplateItems.length );

        for( int i = 0; i < componentTemplateItems.length; i++ )
        {
            assertTrue( componentTemplateItems[i].equals( expectedComponentTemplateItems[i] ) );
        }

        newLiferayComponentWizard.cancel();

        // create Liferay Fragment project
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayModuleFragmentProject().click();

        newModuleFragmentProject.setProjectName( "fragmentTest" );
        newModuleFragmentProject.next();

        // select OSGi Bundle and Overridden files

        setModuleFragmentOSGiBundle.getOSGiBundleButton().click();

        FragmentHostOSGIBundlePO selectOSGiBundle = new FragmentHostOSGIBundlePO( bot, BUTTON_OK );

        AddOverriddenFilePO addJSPFiles = new AddOverriddenFilePO( bot );

        selectOSGiBundle.setOSGiBundle( "*com.liferay.bookmarks.web" );
        selectOSGiBundle.confirm();

        setModuleFragmentOSGiBundle.getAddOverridFilesButton().click();
        addJSPFiles.select( "META-INF/resources/bookmarks/view.jsp" );
        addJSPFiles.confirm();

        setModuleFragmentOSGiBundle.finish();
        sleep( 15000 );

        // open component wizard again then check state to make sure it couldn't
        // support new component class
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayComponentClass().click();

        newLiferayComponentWizard.waitForPageToOpen();

        // check default state again
        assertEquals( TEXT_DEFAULT_PROJECT_NAME_VALUE, newLiferayComponentWizard.getProjectName().getText() );
        assertEquals( TEXT_DEFAULT_PACKAGE_NAME_VALUE, newLiferayComponentWizard.getPackageName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_NAME_VALUE, newLiferayComponentWizard.getComponentClassName().getText() );
        assertEquals(
            TEXT_DEFAULT_COMPONENT_CLASS_TEMPLATE_VALUE,
            newLiferayComponentWizard.getComponentClassTemplate().getText() );
        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayComponentWizard.getValidationMessage() );
        assertFalse( newLiferayComponentWizard.finishButton().isEnabled() );

        newLiferayComponentWizard.cancel();

        eclipse.getPackageExporerView().deleteResouceByName( "fragmentTest", true );
    }

    @Before
    public void shouldRunTests()
    {

        Assume.assumeTrue( runTest() || runAllTests() );

        hasAddedProject = addedProjects();

    }

}
