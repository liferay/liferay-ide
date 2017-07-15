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

import com.liferay.ide.swtbot.liferay.ui.WizardUI;
import com.liferay.ide.swtbot.liferay.ui.LiferayPortletWizardUI;
import com.liferay.ide.swtbot.liferay.ui.SWTBotBase;
import com.liferay.ide.swtbot.liferay.ui.VaadinPortletWizardUI;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ChoosePortletFrameworkWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.CreateVaadinPortletWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.LiferayPortletDeploymentDescriptorWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewSdkProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.PortletDeploymentDescriptorWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.SetSDKLocationWizard;
import com.liferay.ide.swtbot.ui.page.Dialog;

/**
 * @author Li Lu
 */
public class NewVaadinPortletWizardTests extends SWTBotBase
    implements VaadinPortletWizardUI, LiferayPortletWizardUI, WizardUI
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    static CreateVaadinPortletWizard newVaadinPortletPage =
        new CreateVaadinPortletWizard( bot, INDEX_VAADIN_VALIDATION_MESSAGE1 );

    static ChoosePortletFrameworkWizard choosePortletFrameworkPage = new ChoosePortletFrameworkWizard( bot );

    PortletDeploymentDescriptorWizard portletDeploymentDescriptorPage = new PortletDeploymentDescriptorWizard( bot );

    LiferayPortletDeploymentDescriptorWizard liferayPortletDeploymentDescriptorPage =
        new LiferayPortletDeploymentDescriptorWizard( bot );

    static SetSDKLocationWizard setSDKLocationPage = new SetSDKLocationWizard( bot, "" );

    static NewSdkProjectWizard newLiferayProjectPage =
        new NewSdkProjectWizard( bot, LABEL_NEW_LIFERAY_PLUGIN_PROJECT, INDEX_NEW_LIFERAY_PLUGIN_PROJECT_WIZARD );

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
        ide.getPackageExporerView().deleteResouceByName( projectName, true );

        // click new liferay vaadin portlet wizard without projects

        ide.getCreateLiferayProjectToolbar().getNewLiferayVaadinPortlet().click();

        Dialog dialogPage1 = new Dialog( bot, TITLE_NEW_LIFERAY_VAADIN_PORTLET, NO, YES );

        dialogPage1.cancelBtn().click();

        assertEquals( TEXT_ENTER_A_PROJECT_NAME, newVaadinPortletPage.getValidationMsg() );
        assertFalse( newVaadinPortletPage.nextBtn().isEnabled() );

        newVaadinPortletPage.cancel();

        ide.getCreateLiferayProjectToolbar().getNewLiferayVaadinPortlet().click();

        dialogPage1.confirm();

        assertEquals( TEXT_PLEASE_ENTER_A_PROJECT_NAME, newLiferayProjectPage.getValidationMsg() );
        assertFalse( newLiferayProjectPage.nextBtn().isEnabled() );

        newLiferayProjectPage.createSDKPortletProject( projectName );
        assertTrue( newLiferayProjectPage.nextBtn().isEnabled() );

        newLiferayProjectPage.next();

        choosePortletFrameworkPage.selectPortletFramework( LABEL_VAADIN_FRAMEWORK );

        if( newVaadinPortletPage.finishBtn().isEnabled() )
        {
            newVaadinPortletPage.finish();
        }

        else
        {
            newVaadinPortletPage.next();
            setSDKLocationPage.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );
            setSDKLocationPage.finish();
            setSDKLocationPage.waitForPageToClose();
            sleep( 50000 );
        }

    }

    @AfterClass
    public static void deleteProject()
    {
        ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
    }

    @After
    public void closeWizard()
    {
        ide.closeShell( LABEL_NEW_LIFERAY_VAADIN_PORTLET );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayVaadinPortlet().click();
    }

    @Test
    public void testApplicationClass() throws Exception
    {
        newVaadinPortletPage.getApplicationClass().setText( "" );
        sleep();
        assertEquals( TEXT_CLASS_NAME_CANNOT_BE_EMPTY, newVaadinPortletPage.getValidationMsg() );

        newVaadinPortletPage.getApplicationClass().setText( ".." );
        sleep();
        assertEquals( TEXT_DONOT_USE_QUALIDIED_CLASS_NAME, newVaadinPortletPage.getValidationMsg() );

        newVaadinPortletPage.getApplicationClass().setText( "22" );
        sleep();
        assertEquals(
            TEXT_INVALID_JAVA_CLASS_NAME + "\'" + "22" + "\'" + TEXT_NOT_A_VALID_IDENTIFIER,
            newVaadinPortletPage.getValidationMsg() );

        newVaadinPortletPage.getApplicationClass().setText( "m" );
        sleep();
        assertEquals( TEXT_JAVA_TYPE_START_WITH_AN_UPPERCASE_LETTER, newVaadinPortletPage.getValidationMsg() );

        newVaadinPortletPage.getApplicationClass().setText( "NewVaadinPortletApplication" );
        sleep();
        assertEquals( TEXT_CREATE_VAADIN_PORTLET_APPLICATION_CLASS, newVaadinPortletPage.getValidationMsg() );

        assertTrue( newVaadinPortletPage.finishBtn().isEnabled() );
        newVaadinPortletPage.cancel();
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values page 1
        assertEquals( projectName + "-portlet", newVaadinPortletPage.getPortletPluginProjects().getText() );
        assertTrue( newVaadinPortletPage.getSourceFolder().getText().contains( "docroot/WEB-INF/src" ) );
        assertEquals( "NewVaadinPortletApplication", newVaadinPortletPage.getApplicationClass().getText() );
        assertEquals( "com.test", newVaadinPortletPage.getJavaPackage().getText() );
        assertEquals( "com.vaadin.Application", newVaadinPortletPage.getSuperClasses().getText() );
        assertEquals(
            "com.vaadin.terminal.gwt.server.ApplicationPortlet2",
            newVaadinPortletPage.getVaadinPortletClasses().getText() );
        // portletDeploymentDescriptorPage
        newVaadinPortletPage.next();
        assertEquals( "newvaadinportlet", portletDeploymentDescriptorPage.getPortletName() );
        assertEquals( "NewVaadinPortlet", portletDeploymentDescriptorPage.getDisplayName() );
        assertEquals( "NewVaadinPortlet", portletDeploymentDescriptorPage.getPortletTitle() );
        assertFalse( portletDeploymentDescriptorPage.getCreateResourceBundleFile().isChecked() );
        assertEquals( "content/Language.properties", portletDeploymentDescriptorPage.getResourceBundleFilePath() );
        // liferayPortletDeploymentDescriptorPage
        newVaadinPortletPage.next();

        assertEquals( "/icon.png", liferayPortletDeploymentDescriptorPage.getIcon() );
        assertEquals( "/css/main.css", liferayPortletDeploymentDescriptorPage.getCss() );
        assertEquals( "/js/main.js", liferayPortletDeploymentDescriptorPage.getJavaScript() );
        assertEquals( "1.5", liferayPortletDeploymentDescriptorPage.getEntryWeight() );
        assertEquals(
            "NewVaadinPortletApplicationControlPanelEntry", liferayPortletDeploymentDescriptorPage.getEntryClass() );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        newVaadinPortletPage.getPortletClass().setText( "" );
        sleep();
        assertEquals( TEXT_MUST_SPECIFY_VAADIN_PORTLET_CLASS, newVaadinPortletPage.getValidationMsg() );

        newVaadinPortletPage.getPortletClass().setText( ".." );
        sleep();
        assertEquals( TEXT_A_PACKAGE_NAME_CANNOT_START_WITH_A_DOT, newVaadinPortletPage.getValidationMsg() );

        newVaadinPortletPage.getPortletClass().setText( "22" );
        sleep();
        assertEquals(
            TEXT_THE_TYPE_NAME + "\'" + "22" + "\'" + TEXT_NOT_A_VALID_IDENTIFIER,
            newVaadinPortletPage.getValidationMsg() );

        assertFalse( newVaadinPortletPage.finishBtn().isEnabled() );
        newVaadinPortletPage.cancel();
    }

}
