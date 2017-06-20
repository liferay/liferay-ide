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

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayModuleProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayModuleProjectWizardSecondPagePO;
import com.liferay.ide.swtbot.project.ui.tests.page.NewLiferayWorkspaceProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SelectModuleServiceNamePO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.DeleteResourcesDialogPO;
import com.liferay.ide.swtbot.ui.tests.page.CTabItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public abstract class AbstractNewLiferayModuleProjectWizard extends SWTBotBase implements NewLiferayModuleProjectWizard
{

    static NewLiferayWorkspaceProjectWizardPO newLiferayWorkspace = new NewLiferayWorkspaceProjectWizardPO( bot );

    static TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

    static DeleteResourcesDialogPO deleteResources = new DeleteResourcesDialogPO( bot );

    @AfterClass
    public static void cleanAll()
    {
        eclipse.closeShell( LABEL_NEW_LIFERAY_MODULE_PROJECT );
        eclipse.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
    }

    @BeforeClass
    public static void createLiferayWorkspace()
    {
        eclipse.getLiferayWorkspacePerspective().activate();

        eclipse.getProjectExplorerView().show();
    }

    public static void newLiferayWorkspace( String liferayWorkspaceName, String buildType )
    {

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();
        sleep( 2000 );

        newLiferayWorkspace.setWorkspaceNameText( liferayWorkspaceName );

        newLiferayWorkspace.get_buildType().setSelection( buildType );

        newLiferayWorkspace.finish();
        sleep( 20000 );
    }

    NewLiferayModuleProjectWizardPO createModuleProjectWizard =
        new NewLiferayModuleProjectWizardPO( bot, INDEX_NEW_LIFERAY_MODULE_PROJECT_VALIDATION_MESSAGE );

    NewLiferayModuleProjectWizardSecondPagePO createModuleProjectSecondPageWizard =
        new NewLiferayModuleProjectWizardSecondPagePO( bot, INDEX_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE );

    NewLiferayModuleProjectWizardSecondPagePO createServiceModuleProjectSecondPage =
        new NewLiferayModuleProjectWizardSecondPagePO(
            bot, INDEX_SERVICE_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE );

    public void checkBuildTypes()
    {
        String[] liferayWorkspaceBuildTypeItems = createModuleProjectWizard.getBuildType().getAvailableComboValues();

        for( int i = 0; i < liferayWorkspaceBuildTypeItems.length; i++ )
        {
            if( liferayWorkspaceBuildTypeItems[0].equals( TEXT_BUILD_TYPE_GRADLE ) )
            {
                assertTrue( liferayWorkspaceBuildTypeItems[i].equals( expectedBuildTypeItems[i] ) );
            }
            else
            {
                assertTrue(
                    liferayWorkspaceBuildTypeItems[i].equals(
                        expectedBuildTypeItems[liferayWorkspaceBuildTypeItems.length - i - 1] ) );
            }
        }
    }

    public void newLiferayModuleProject(
        String buildType, String projectName, String projectTemplate, String defaultLocation,
        boolean isCustomizeLocation, String customizeLocation, String componentClass, String packageName,
        String serviceName, boolean addProperties )
    {
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();
        sleep( 1000 );

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, createModuleProjectWizard.getValidationMessage() );
        assertEquals( MENU_MODULE_MVC_PORTLET, createModuleProjectWizard.getProjectTemplateNameComboBox().getText() );
        assertTrue( createModuleProjectWizard.get_useDefaultLocation().isChecked() );

        createModuleProjectWizard.get_useDefaultLocation().deselect();
        sleep();

        assertEquals( eclipseWorkspace, createModuleProjectWizard.getLocation().getText() );

        createModuleProjectWizard.createModuleProject( projectName, projectTemplate, buildType );
        sleep();

        assertEquals( buildType, createModuleProjectWizard.getBuildType().getText() );

        createModuleProjectWizard.getLocation().setText( TEXT_BLANK );

        sleep();
        assertEquals( TEXT_LOCATION_MUST_BE_SPECIFIED, createModuleProjectWizard.getValidationMessage() );

        if( isCustomizeLocation )
        {

            File customizeDir = new File( customizeLocation );

            if( !customizeDir.exists() )
            {
                customizeDir.mkdir();
            }

            createModuleProjectWizard.setLocation( customizeDir.toString() );

            sleep();
            assertEquals( TEXT_NEW_LIFERAY_MODULE_MESSAGE, createModuleProjectWizard.getValidationMessage() );
        }
        else
        {
            createModuleProjectWizard.get_useDefaultLocation().select();
            sleep();

            createModuleProjectWizard.get_useDefaultLocation().deselect();
            sleep();
            assertEquals( defaultLocation, createModuleProjectWizard.getLocation().getText() );

            createModuleProjectWizard.get_useDefaultLocation().select();
        }
        sleep();
        assertEquals( TEXT_NEW_LIFERAY_MODULE_MESSAGE, createModuleProjectWizard.getValidationMessage() );

        String[] moduleProjectTemplateItems =
            createModuleProjectWizard.getProjectTemplateNameComboBox().getAvailableComboValues();

        for( int i = 0; i < moduleProjectTemplateItems.length; i++ )
        {
            assertTrue( moduleProjectTemplateItems[i].equals( expectedModuleProjectTemplateItems[i] ) );
        }

        if( !projectTemplate.equals( MENU_MODULE_THEME ) )
        {
            createModuleProjectWizard.next();
            sleep();

            if( addProperties )
            {
                if( projectTemplate.equals( MENU_MODULE_SERVICE ) ||
                    projectTemplate.equals( MENU_MODULE_SERVICE_WRAPPER ) )
                {
                    assertEquals(
                        TEXT_CONFIGURE_COMPONENT_CLASS, createServiceModuleProjectSecondPage.getValidationMessage() );
                }
                else
                {
                    assertEquals(
                        TEXT_CONFIGURE_COMPONENT_CLASS, createModuleProjectSecondPageWizard.getValidationMessage() );
                }
                assertEquals( "", createModuleProjectSecondPageWizard.getComponentClassName().getText() );
                assertEquals( "", createModuleProjectSecondPageWizard.getPackageName().getText() );

                if( projectTemplate.equals( MENU_MODULE_SERVICE ) ||
                    projectTemplate.equals( MENU_MODULE_SERVICE_WRAPPER ) )
                {

                    assertEquals( "", createModuleProjectSecondPageWizard.getServiceName().getText() );

                    createModuleProjectSecondPageWizard.getBrowseButton().click();

                    SelectModuleServiceNamePO selectOneServiceName = new SelectModuleServiceNamePO( bot );
                    selectOneServiceName.cancel();

                    if( !serviceName.equals( TEXT_BLANK ) )
                    {
                        createModuleProjectSecondPageWizard.getBrowseButton().click();
                        sleep();

                        selectOneServiceName.selectServiceName( "gg" );
                        sleep( 2000 );

                        assertFalse( selectOneServiceName.confirmButton().isEnabled() );

                        selectOneServiceName.selectServiceName( serviceName );
                        sleep();

                        assertTrue( selectOneServiceName.confirmButton().isEnabled() );
                        selectOneServiceName.confirm();
                    }
                }

                if( !componentClass.equals( TEXT_BLANK ) )
                {
                    createModuleProjectSecondPageWizard.getComponentClassName().setText( componentClass );
                }

                if( !packageName.equals( TEXT_BLANK ) )
                {
                    createModuleProjectSecondPageWizard.getPackageName().setText( packageName );
                }

                if( !isInAvailableLists( templatesWithoutPropertyKeys, projectTemplate ) )
                {

                    // add properties then check toolbarButton state
                    assertTrue( createModuleProjectSecondPageWizard.getAddPropertyKeyButton().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getDeleteButton().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveUpButton().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveDownButton().isEnabled() );

                    createModuleProjectSecondPageWizard.getAddPropertyKeyButton().click();
                    sleep();

                    if( projectTemplate.equals( MENU_MODULE_SERVICE ) ||
                        projectTemplate.equals( MENU_MODULE_SERVICE_WRAPPER ) )
                    {
                        createModuleProjectSecondPageWizard.setPropertiesText( 3, "a" );
                        sleep( 500 );
                        createModuleProjectSecondPageWizard.getProperties().doubleClick( 0, 1 );
                        sleep();
                        createModuleProjectSecondPageWizard.setPropertiesText( 3, "b" );
                        sleep( 500 );
                    }
                    else
                    {
                        createModuleProjectSecondPageWizard.setPropertiesText( 2, "a" );
                        sleep( 500 );
                        createModuleProjectSecondPageWizard.getProperties().doubleClick( 0, 1 );
                        sleep();
                        createModuleProjectSecondPageWizard.setPropertiesText( 2, "b" );

                    }
                    sleep();
                    createModuleProjectSecondPageWizard.getProperties().setFocus();
                    sleep();

                    assertTrue( createModuleProjectSecondPageWizard.getDeleteButton().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveUpButton().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveDownButton().isEnabled() );

                    createModuleProjectSecondPageWizard.getAddPropertyKeyButton().click();
                    sleep();

                    if( projectTemplate.equals( MENU_MODULE_SERVICE ) ||
                        projectTemplate.equals( MENU_MODULE_SERVICE_WRAPPER ) )
                    {
                        createModuleProjectSecondPageWizard.setPropertiesText( 3, "c" );
                        sleep( 500 );
                        createModuleProjectSecondPageWizard.getProperties().doubleClick( 1, 1 );
                        sleep();
                        createModuleProjectSecondPageWizard.setPropertiesText( 3, "d" );
                    }
                    else
                    {
                        createModuleProjectSecondPageWizard.setPropertiesText( 2, "c" );
                        sleep( 500 );
                        createModuleProjectSecondPageWizard.getProperties().doubleClick( 1, 1 );
                        sleep();
                        createModuleProjectSecondPageWizard.setPropertiesText( 2, "d" );

                    }

                    sleep();
                    createModuleProjectSecondPageWizard.getProperties().setFocus();
                    sleep();

                    assertTrue( createModuleProjectSecondPageWizard.getMoveUpButton().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveDownButton().isEnabled() );
                    createModuleProjectSecondPageWizard.getMoveUpButton().click();
                    assertFalse( createModuleProjectSecondPageWizard.getMoveUpButton().isEnabled() );
                    assertTrue( createModuleProjectSecondPageWizard.getMoveDownButton().isEnabled() );
                    createModuleProjectSecondPageWizard.getMoveDownButton().click();

                    createModuleProjectSecondPageWizard.getDeleteButton().click();

                }
            }

        }
        assertFalse( createModuleProjectWizard.nextButton().isEnabled() );
        createModuleProjectWizard.finish();
        createModuleProjectWizard.waitForPageToClose();
        sleep( 2000 );
    }

    public void openEditorAndCheck( String content, String projectName, String... nodes )
    {
        String fileName = nodes[nodes.length - 1];
        String pomFileName = "pom.xml";
        String fileNameForPom = projectName + "/pom.xml";

        String[] expandNodes = new String[nodes.length - 1];

        for( int i = 0; i < nodes.length - 1; i++ )
        {
            expandNodes[i] = nodes[i];
        }

        projectTree.setFocus();

        projectTree.expandNode( expandNodes ).doubleClick( fileName );

        if( fileName.trim().equals( pomFileName ) )
        {
            CTabItemPO switchCTabItem = new CTabItemPO( bot, pomFileName );

            switchCTabItem.click();

            TextEditorPO fileEditorForPom = eclipse.getTextEditor( fileNameForPom );

            assertContains( content, fileEditorForPom.getText() );

            fileEditorForPom.close();

        }
        else
        {
            TextEditorPO fileEditor = eclipse.getTextEditor( fileName );

            assertContains( content, fileEditor.getText() );

            fileEditor.close();
        }

    }

}
