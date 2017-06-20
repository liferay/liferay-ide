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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.portlet.ui.tests.page.CreateVaadinPortletWizardPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.LiferayPortletDeploymentDescriptorPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.PortletDeploymentDescriptorPO;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.project.ui.tests.page.ChoosePortletFrameworkPO;
import com.liferay.ide.swtbot.project.ui.tests.page.CreateProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SetSDKLocationPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;

/**
 * @author Li Lu
 */
public class NewVaadinPortletWizardTests extends SWTBotBase
    implements VaadinPortletWizard, LiferayPortletWizard, ProjectWizard
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    static CreateVaadinPortletWizardPO newVaadinPortletPage =
        new CreateVaadinPortletWizardPO( bot, INDEX_VAADIN_VALIDATION_MESSAGE1 );

    static ChoosePortletFrameworkPO choosePortletFrameworkPage = new ChoosePortletFrameworkPO( bot );

    PortletDeploymentDescriptorPO portletDeploymentDescriptorPage = new PortletDeploymentDescriptorPO( bot );

    LiferayPortletDeploymentDescriptorPO liferayPortletDeploymentDescriptorPage =
        new LiferayPortletDeploymentDescriptorPO( bot );

    static SetSDKLocationPO setSDKLocationPage = new SetSDKLocationPO( bot, "" );

    static CreateProjectWizardPO newLiferayProjectPage =
        new CreateProjectWizardPO( bot, LABEL_NEW_LIFERAY_PLUGIN_PROJECT, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_WIZARD );

    static String projectName = "vaadin-test";

    @BeforeClass
    public static void unzipServerAndSdk() throws Exception
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipPluginsSDK();
        unzipServer();

        createVaddinPorltetWithoutLiferayProjects();
        sleep( 3000 );
        newVaadinPortletPage.cancel();
    }

    public static void createVaddinPorltetWithoutLiferayProjects()
    {
        eclipse.getPackageExporerView().deleteResouceByName( projectName, true );

        // click new liferay vaadin portlet wizard without projects

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayVaadinPortlet().click();

        DialogPO dialogPage1 = new DialogPO( bot, TITLE_NEW_LIFERAY_VAADIN_PORTLET, BUTTON_NO, BUTTON_YES );

        dialogPage1.cancelButton().click();

        assertEquals( TEXT_ENTER_A_PROJECT_NAME, newVaadinPortletPage.getValidationMessage() );
        assertFalse( newVaadinPortletPage.nextButton().isEnabled() );

        newVaadinPortletPage.cancel();

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayVaadinPortlet().click();

        dialogPage1.confirm();

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayProjectPage.getValidationMessage() );
        assertFalse( newLiferayProjectPage.nextButton().isEnabled() );

        newLiferayProjectPage.createSDKPortletProject( projectName );
        assertTrue( newLiferayProjectPage.nextButton().isEnabled() );

        newLiferayProjectPage.next();

        choosePortletFrameworkPage.selectPortletFramework( LABEL_VAADIN_FRAMEWORK );

        if( newVaadinPortletPage.finishButton().isEnabled() )
        {
            newVaadinPortletPage.finish();
        }

        else
        {
            newVaadinPortletPage.next();
            setSDKLocationPage.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            setSDKLocationPage.finish();
            setSDKLocationPage.waitForPageToClose();
            sleep( 50000 );
        }

    }

    @AfterClass
    public static void deleteProject()
    {
        eclipse.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
    }

    @After
    public void closeWizard()
    {
        eclipse.closeShell( LABEL_NEW_LIFERAY_VAADIN_PORTLET );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayVaadinPortlet().click();
    }

    @Test
    public void testApplicationClass() throws Exception
    {
        newVaadinPortletPage.setApplicationClassText( "" );
        sleep();
        assertEquals( TEXT_CLASS_NAME_CANNOT_BE_EMPTY, newVaadinPortletPage.getValidationMessage() );

        newVaadinPortletPage.setApplicationClassText( ".." );
        sleep();
        assertEquals( TEXT_DONOT_USE_QUALIDIED_CLASS_NAME, newVaadinPortletPage.getValidationMessage() );

        newVaadinPortletPage.setApplicationClassText( "22" );
        sleep();
        assertEquals(
            TEXT_INVALID_JAVA_CLASS_NAME + "\'" + "22" + "\'" + TEXT_NOT_A_VALID_IDENTIFIER,
            newVaadinPortletPage.getValidationMessage() );

        newVaadinPortletPage.setApplicationClassText( "m" );
        sleep();
        assertEquals( TEXT_JAVA_TYPE_START_WITH_AN_UPPERCASE_LETTER, newVaadinPortletPage.getValidationMessage() );

        newVaadinPortletPage.setApplicationClassText( "NewVaadinPortletApplication" );
        sleep();
        assertEquals( TEXT_CREATE_VAADIN_PORTLET_APPLICATION_CLASS, newVaadinPortletPage.getValidationMessage() );

        assertTrue( newVaadinPortletPage.finishButton().isEnabled() );
        newVaadinPortletPage.cancel();
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values page 1
        assertEquals( projectName + "-portlet", newVaadinPortletPage.getPortletPluginProject() );
        assertTrue( newVaadinPortletPage.getSourceFolderText().contains( "docroot/WEB-INF/src" ) );
        assertEquals( "NewVaadinPortletApplication", newVaadinPortletPage.getApplicationClassText() );
        assertEquals( "com.test", newVaadinPortletPage.getJavaPackageText() );
        assertEquals( "com.vaadin.Application", newVaadinPortletPage.getSuperClassCombobox() );
        assertEquals(
            "com.vaadin.terminal.gwt.server.ApplicationPortlet2", newVaadinPortletPage.getVaadinPortletClassText() );
        // portletDeploymentDescriptorPage
        newVaadinPortletPage.next();
        assertEquals( "newvaadinportlet", portletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "NewVaadinPortlet", portletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "NewVaadinPortlet", portletDeploymentDescriptorPage.getPortletTitle() );
        assertFalse( portletDeploymentDescriptorPage.get_createResourceBundleFileCheckbox().isChecked() );
        assertEquals( "content/Language.properties", portletDeploymentDescriptorPage.getResourceBundleFilePath() );
        // liferayPortletDeploymentDescriptorPage
        newVaadinPortletPage.next();
        assertFalse( liferayPortletDeploymentDescriptorPage.isEntryCategoryEnabled() );
        assertFalse( liferayPortletDeploymentDescriptorPage.isEntryWeightEnabled() );
        assertFalse( liferayPortletDeploymentDescriptorPage.isCreateEntryClassEnabled() );
        assertFalse( liferayPortletDeploymentDescriptorPage.isEntryClassEnabled() );

        assertEquals( "/icon.png", liferayPortletDeploymentDescriptorPage.getIconText() );
        assertEquals( false, liferayPortletDeploymentDescriptorPage.isAllowMultipleInstancesChecked() );
        assertEquals( "/css/main.css", liferayPortletDeploymentDescriptorPage.getCssText() );
        assertEquals( "/js/main.js", liferayPortletDeploymentDescriptorPage.getJavaScriptText() );
        assertEquals( "newvaadinportlet-portlet", liferayPortletDeploymentDescriptorPage.getCssClassWrapperText() );
        assertEquals( "Sample", liferayPortletDeploymentDescriptorPage.getDisplayCategoryCombobox() );
        assertEquals( false, liferayPortletDeploymentDescriptorPage.isAddToControlPanelChecked() );
        assertEquals( "My Account Administration", liferayPortletDeploymentDescriptorPage.getEntryCategoryCombobox() );
        assertEquals( "1.5", liferayPortletDeploymentDescriptorPage.getEntryWeightText() );
        assertEquals( false, liferayPortletDeploymentDescriptorPage.isCreateEntryClassChecked() );
        assertEquals(
            "NewVaadinPortletApplicationControlPanelEntry",
            liferayPortletDeploymentDescriptorPage.getEntryClassText() );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        newVaadinPortletPage.setPortletClassText( "" );
        sleep();
        assertEquals( TEXT_MUST_SPECIFY_VAADIN_PORTLET_CLASS, newVaadinPortletPage.getValidationMessage() );

        newVaadinPortletPage.setPortletClassText( ".." );
        sleep();
        assertEquals( TEXT_A_PACKAGE_NAME_CANNOT_START_WITH_A_DOT, newVaadinPortletPage.getValidationMessage() );

        newVaadinPortletPage.setPortletClassText( "22" );
        sleep();
        assertEquals(
            TEXT_THE_TYPE_NAME + "\'" + "22" + "\'" + TEXT_NOT_A_VALID_IDENTIFIER,
            newVaadinPortletPage.getValidationMessage() );

        assertFalse( newVaadinPortletPage.finishButton().isEnabled() );
        newVaadinPortletPage.cancel();
    }

}
