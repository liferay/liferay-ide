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

import com.liferay.ide.swtbot.liferay.ui.ModuleWizardUI;
import com.liferay.ide.swtbot.liferay.ui.SWTBotBase;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.SelectModuleServiceNameDialog;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayModuleProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayModuleProjectWizardSecondPageWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewLiferayWorkspaceProjectWizard;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.page.CTabItem;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Tree;

/**
 * @author Ashley Yuan
 * @author Sunny Shi
 */
public class BaseNewLiferayModuleProjectWizard extends SWTBotBase implements ModuleWizardUI
{

    static NewLiferayWorkspaceProjectWizard newLiferayWorkspace = new NewLiferayWorkspaceProjectWizard( bot );

    static Tree projectTree = ide.getPackageExporerView().getProjectTree();

    static DeleteResourcesDialog deleteResources = new DeleteResourcesDialog( bot );

    @AfterClass
    public static void cleanAll()
    {
        ide.closeShell( LABEL_NEW_LIFERAY_MODULE_PROJECT );
        ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
    }

    @BeforeClass
    public static void createLiferayWorkspace()
    {
        ide.getLiferayWorkspacePerspective().activate();

        ide.getProjectExplorerView().show();
    }

    public static void newLiferayWorkspace( String liferayWorkspaceName, String buildType )
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayWorkspaceProject().click();
        sleep( 2000 );

        newLiferayWorkspace.getWorkspaceName().setText( liferayWorkspaceName );

        newLiferayWorkspace.getBuildTypes().setSelection( buildType );

        newLiferayWorkspace.finish();
        sleep( 20000 );
    }

    NewLiferayModuleProjectWizard createModuleProjectWizard =
        new NewLiferayModuleProjectWizard( bot, INDEX_NEW_LIFERAY_MODULE_PROJECT_VALIDATION_MESSAGE );

    NewLiferayModuleProjectWizardSecondPageWizard createModuleProjectSecondPageWizard =
        new NewLiferayModuleProjectWizardSecondPageWizard( bot, INDEX_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE );

    NewLiferayModuleProjectWizardSecondPageWizard createServiceModuleProjectSecondPage =
        new NewLiferayModuleProjectWizardSecondPageWizard(
            bot, INDEX_SERVICE_CONFIGURE_COMPONENT_CLASS_VALIDATION_MESSAGE );

    public void checkBuildTypes()
    {
        String[] liferayWorkspaceBuildTypeItems = createModuleProjectWizard.getBuildTypes().items();

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
        ide.getCreateLiferayProjectToolbar().getNewLiferayModuleProject().click();

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, createModuleProjectWizard.getValidationMsg() );
        assertEquals( MODULE_MVC_PORTLET, createModuleProjectWizard.getProjectTemplateNames().getText() );
        assertTrue( createModuleProjectWizard.getUseDefaultLocation().isChecked() );

        createModuleProjectWizard.getUseDefaultLocation().deselect();

        assertEquals( eclipseWorkspace, createModuleProjectWizard.getLocation().getText() );

        createModuleProjectWizard.createModuleProject( projectName, projectTemplate, buildType );

        assertEquals( buildType, createModuleProjectWizard.getBuildTypes().getText() );

        createModuleProjectWizard.getLocation().setText( TEXT_BLANK );

        assertEquals( LOCATION_MUST_BE_SPECIFIED, createModuleProjectWizard.getValidationMsg() );

        if( isCustomizeLocation )
        {

            File customizeDir = new File( customizeLocation );

            if( !customizeDir.exists() )
            {
                customizeDir.mkdir();
            }

            createModuleProjectWizard.getLocation().setText( customizeDir.toString() );

            assertEquals( NEW_LIFERAY_MODULE_MESSAGE, createModuleProjectWizard.getValidationMsg() );
        }
        else
        {
            createModuleProjectWizard.getUseDefaultLocation().select();

            createModuleProjectWizard.getUseDefaultLocation().deselect();

            // assertEquals( defaultLocation, createModuleProjectWizard.getLocation().getText() );

            createModuleProjectWizard.getUseDefaultLocation().select();
        }

        assertEquals( NEW_LIFERAY_MODULE_MESSAGE, createModuleProjectWizard.getValidationMsg() );

        String[] moduleProjectTemplateItems = createModuleProjectWizard.getProjectTemplateNames().items();

        for( int i = 0; i < moduleProjectTemplateItems.length; i++ )
        {
            assertTrue( moduleProjectTemplateItems[i].equals( expectedModuleProjectTemplateItems[i] ) );
        }

        if( !projectTemplate.equals( MODULE_THEME ) )
        {
            createModuleProjectWizard.next();
            sleep();

            if( addProperties )
            {
                if( projectTemplate.equals( MODULE_SERVICE ) || projectTemplate.equals( MODULE_SERVICE_WRAPPER ) )
                {
                    assertEquals( CONFIGURE_COMPONENT_CLASS, createServiceModuleProjectSecondPage.getValidationMsg() );
                }
                else
                {
                    assertEquals( CONFIGURE_COMPONENT_CLASS, createModuleProjectSecondPageWizard.getValidationMsg() );
                }
                assertEquals( "", createModuleProjectSecondPageWizard.getComponentClassName().getText() );
                assertEquals( "", createModuleProjectSecondPageWizard.getPackageName().getText() );

                if( projectTemplate.equals( MODULE_SERVICE ) || projectTemplate.equals( MODULE_SERVICE_WRAPPER ) )
                {

                    assertEquals( "", createModuleProjectSecondPageWizard.getServiceName().getText() );

                    createModuleProjectSecondPageWizard.getBrowseBtn().click();

                    SelectModuleServiceNameDialog selectOneServiceName = new SelectModuleServiceNameDialog( bot );
                    selectOneServiceName.cancel();

                    if( !serviceName.equals( TEXT_BLANK ) )
                    {
                        createModuleProjectSecondPageWizard.getBrowseBtn().click();
                        sleep();

                        selectOneServiceName.getServiceName().setText( "gg" );
                        sleep( 2000 );

                        assertFalse( selectOneServiceName.confirmBtn().isEnabled() );

                        selectOneServiceName.getServiceName().setText( serviceName );
                        sleep();

                        assertTrue( selectOneServiceName.confirmBtn().isEnabled() );
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

                    // add properties then check toolbarBtn state
                    assertTrue( createModuleProjectSecondPageWizard.getAddPropertyKeyBtn().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getDeleteBtn().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveUpBtn().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveDownBtn().isEnabled() );

                    createModuleProjectSecondPageWizard.getAddPropertyKeyBtn().click();
                    sleep();

                    if( projectTemplate.equals( MODULE_SERVICE ) || projectTemplate.equals( MODULE_SERVICE_WRAPPER ) )
                    {
                        createModuleProjectSecondPageWizard.getProperties().setText( 3, "a" );
                        sleep( 500 );
                        createModuleProjectSecondPageWizard.getProperties().doubleClick( 0, 1 );
                        sleep();
                        createModuleProjectSecondPageWizard.getProperties().setText( 3, "b" );
                        sleep( 500 );
                    }
                    else
                    {
                        createModuleProjectSecondPageWizard.getProperties().setText( 2, "a" );
                        sleep( 500 );
                        createModuleProjectSecondPageWizard.getProperties().doubleClick( 0, 1 );
                        sleep();
                        createModuleProjectSecondPageWizard.getProperties().setText( 2, "b" );

                    }
                    sleep();
                    createModuleProjectSecondPageWizard.getProperties().setFocus();
                    sleep();

                    assertTrue( createModuleProjectSecondPageWizard.getDeleteBtn().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveUpBtn().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveDownBtn().isEnabled() );

                    createModuleProjectSecondPageWizard.getAddPropertyKeyBtn().click();
                    sleep();

                    if( projectTemplate.equals( MODULE_SERVICE ) || projectTemplate.equals( MODULE_SERVICE_WRAPPER ) )
                    {
                        createModuleProjectSecondPageWizard.getProperties().setText( 3, "c" );
                        sleep( 500 );
                        createModuleProjectSecondPageWizard.getProperties().doubleClick( 1, 1 );
                        sleep();
                        createModuleProjectSecondPageWizard.getProperties().setText( 3, "d" );
                    }
                    else
                    {
                        createModuleProjectSecondPageWizard.getProperties().setText( 2, "c" );
                        sleep( 500 );
                        createModuleProjectSecondPageWizard.getProperties().doubleClick( 1, 1 );
                        sleep();
                        createModuleProjectSecondPageWizard.getProperties().setText( 2, "d" );

                    }

                    sleep();
                    createModuleProjectSecondPageWizard.getProperties().setFocus();
                    sleep();

                    assertTrue( createModuleProjectSecondPageWizard.getMoveUpBtn().isEnabled() );
                    assertFalse( createModuleProjectSecondPageWizard.getMoveDownBtn().isEnabled() );
                    createModuleProjectSecondPageWizard.getMoveUpBtn().click();
                    assertFalse( createModuleProjectSecondPageWizard.getMoveUpBtn().isEnabled() );
                    assertTrue( createModuleProjectSecondPageWizard.getMoveDownBtn().isEnabled() );
                    createModuleProjectSecondPageWizard.getMoveDownBtn().click();

                    createModuleProjectSecondPageWizard.getDeleteBtn().click();

                }
            }

        }
        assertFalse( createModuleProjectWizard.nextBtn().isEnabled() );
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
            CTabItem switchCTabItem = new CTabItem( bot, pomFileName );

            switchCTabItem.click();

            Editor fileEditorForPom = ide.getEditor( fileNameForPom );

            assertContains( content, fileEditorForPom.getText() );

            fileEditorForPom.close();

        }
        else
        {
            Editor fileEditor = ide.getEditor( fileName );

            assertContains( content, fileEditor.getText() );

            fileEditor.close();
        }

    }

}
