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

package com.liferay.ide.swtbot.hook.ui.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.swtbot.liferay.ui.DialogUI;
import com.liferay.ide.swtbot.liferay.ui.SWTBotBase;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.AddEventActionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.AddJSPFilePathDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.AddPortalPropertiesOverrideDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.AddServiceWrapperDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.ContainerSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.EventSelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.LiferayCustomJSPDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.NewClassDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.NewImplClassDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.PortalPropertiesFileDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.PropertySelectionDialog;
import com.liferay.ide.swtbot.liferay.ui.page.dialog.SuperClassDialog;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.CreateCustomJSPsWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.CreateLiferayHookConfigurationWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.LanguagePropertiesWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.NewSdkProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.PortalPropertiesWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ServicesWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.SetSDKLocationWizard;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Editor;
import com.liferay.ide.swtbot.ui.page.Tree;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class HookConfigurationWizardTests extends SWTBotBase implements DialogUI
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    CreateLiferayHookConfigurationWizard createLiferayHookConfiguration = new CreateLiferayHookConfigurationWizard(
        bot, TITLE_NEW_LIFERAY_HOOK, INDEX_NEW_LIFERAY_HOOK_VALIDATION_MESSAGE );
    NewSdkProjectWizard createProjectWizard = new NewSdkProjectWizard( bot, INDEX_VALIDATION_MESSAGE );
    String projectHookName = "hook-configuration-wizard";

    @AfterClass
    public static void cleanAll()
    {
        try
        {
            ide.closeShell( TITLE_NEW_LIFERAY_HOOK );
            ide.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
        }
        catch( Exception e )
        {
        }
    }

    @BeforeClass
    public static void unzipServerAndSdk() throws IOException
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        ide.getLiferayPerspective().activate();

        unzipPluginsSDK();
        unzipServer();
    }

    @Test
    public void hookConfigurationAllHookTypes()
    {
        CreateCustomJSPsWizard customJSPpage = new CreateCustomJSPsWizard( bot, INDEX_CUSTOM_JSPS_VALIDATION_MESSAGE );

        ide.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();

        createLiferayHookConfiguration.getCustomJSPs().select();
        createLiferayHookConfiguration.getPortalProperties().select();
        createLiferayHookConfiguration.getServices().select();
        createLiferayHookConfiguration.getLanguageProperties().select();

        // check it doesn't support new language properties with sdk 7
        LanguagePropertiesWizard languageProperties = new LanguagePropertiesWizard(
            bot, TITLE_NEW_LIFERAY_HOOK, INDEX_LANGUAGE_PROPERTIES_WITH_SDK7_VALIDATION_MESSAGE );
        assertEquals( LABLE_LANGUAGE_PROPERTIES_IN_SDK7_IS_NOT_SUPPORTED, languageProperties.getValidationMsg() );
        assertFalse( languageProperties.nextBtn().isEnabled() );

        createLiferayHookConfiguration.getLanguageProperties().deselect();
        createLiferayHookConfiguration.next();

        // Custom JSPs
        AddJSPFilePathDialog jspFile = new AddJSPFilePathDialog( bot );

        customJSPpage.getAddBtn().click();
        jspFile.getJspFilePath().setText( "CustomJsps.jsp" );
        jspFile.confirm();
        customJSPpage.next();

        // Portal Properties
        PortalPropertiesWizard portalPropertiesPage =
            new PortalPropertiesWizard( bot, INDEX_PORTAL_PROPERTIES_VALIDATION_MESSAGE );
        AddEventActionDialog eventActionPage = new AddEventActionDialog( bot );

        portalPropertiesPage.getEventAddBtn().click();
        eventActionPage.getEvent().setText( "portalProperties" );
        eventActionPage.getEventActionClass().setText( "portalPropertiesClass" );
        eventActionPage.confirm();
        sleep();

        portalPropertiesPage.next();

        // Service
        ServicesWizard servicesPage = new ServicesWizard( bot, INDEX_SERVICES_MESSAGE );
        AddServiceWrapperDialog serviceWrapperPage = new AddServiceWrapperDialog( bot );

        servicesPage.getAddBtn().click();
        serviceWrapperPage.getServiceType().setText( "com.liferay.portal.service.AddressService" );
        serviceWrapperPage.getImplClass().setText( "com.liferay.portal.service.AddressServiceWrapper" );
        serviceWrapperPage.confirm();

        // servicesPage.next();

        // Language Properties should test with sdk 6.2
        // LanguagePropertiesPO languagePropertiesPage =
        // new LanguagePropertiesPO( bot,
        // INDEX_LANGUAGE_PROPERTIES_VALIDATION_MESSAGE );
        //
        // AddLanguagePropertyPO languageProperty = new AddLanguagePropertyPO(
        // bot );
        //
        // languagePropertiesPage.getAddBtn().click();
        // languageProperty.setLanguagePropertyFileText(
        // "languageTest.properties" );
        // languageProperty.confirm();
        // languagePropertiesPage.finish();

        servicesPage.finish();

        Tree projectTree = ide.showPackageExporerView().getProjectTree();

        String fileName = "CustomJsps.jsp";

        projectTree.expandNode(
            new String[] { projectHookName + "-hook", "docroot", "META-INF", "custom_jsps" } ).doubleClick( fileName );

        fileName = "portal.properties";

        projectTree.expandNode( new String[] { projectHookName + "-hook", "docroot/WEB-INF/src" } ).doubleClick(
            fileName );

        Editor editor = ide.getEditor( fileName );

        assertTrue( editor.isActive() );
        assertContains( "portalProperties=portalPropertiesClass", editor.getText() );
    }

    @Test
    public void hookConfigurationCustomJSPs()
    {
        String defaultMsg = "Create customs JSP folder and select JSPs to override.";
        String errorMsg01 = " Custom JSPs folder not configured.";
        String errorMsg02 = " Need to specify at least one JSP to override.";
        String warningMsg = " Shouldn't add same jsp file.";
        CreateCustomJSPsWizard customJSPpage = new CreateCustomJSPsWizard( bot, INDEX_CUSTOM_JSPS_VALIDATION_MESSAGE );

        assertEquals(
            projectHookName + "-hook", createLiferayHookConfiguration.getHookPluginProjectComboBox().getText() );

        createLiferayHookConfiguration.getCustomJSPs().select();
        createLiferayHookConfiguration.next();

        // Custom JSPs page
        assertEquals( defaultMsg, customJSPpage.getValidationMsg() );
        assertEquals( "hook-configuration-wizard-hook", customJSPpage.getSelectedProject().getText() );
        assertEquals( "docroot", customJSPpage.getWebRootFolder().getText() );
        assertEquals( "/META-INF/custom_jsps", customJSPpage.getCustomJSPfolder().getText() );

        customJSPpage.getCustomJSPfolder().setText( "" );
        assertEquals( errorMsg01, customJSPpage.getValidationMsg() );

        customJSPpage.getCustomJSPfolder().setText( "22" );
        assertEquals( errorMsg02, customJSPpage.getValidationMsg() );

        customJSPpage.getBrowseBtn().click();

        ContainerSelectionDialog chooseFolder = new ContainerSelectionDialog( bot );

        chooseFolder.select( "hook-configuration-wizard-hook", "docroot", "META-INF" );
        chooseFolder.confirm();
        customJSPpage.getCustomJSPfolder().setText( "/META-INF/custom_jsps" );

        // JSP files to override
        LiferayCustomJSPDialog chooseCustomJSP = new LiferayCustomJSPDialog( bot );
        customJSPpage.getAddFromLiferayBtn().click();

        chooseCustomJSP.select( "html", "common", "themes", "bottom.jsp" );
        chooseCustomJSP.cancel();

        customJSPpage.getAddFromLiferayBtn().click();
        chooseCustomJSP.getSelectJspToCustomize().setText( "bottom.jsp" );
        chooseCustomJSP.select( "html", "common", "themes", "bottom.jsp" );
        chooseCustomJSP.confirm();

        customJSPpage.getAddFromLiferayBtn().click();
        chooseCustomJSP.select( "html", "common", "themes", "bottom.jsp" );
        chooseCustomJSP.confirm();

        assertEquals( warningMsg, customJSPpage.getValidationMsg() );
        customJSPpage.getJspFilesToOverride().click( 1, 0 );
        customJSPpage.getRemoveBtn().click();

        AddJSPFilePathDialog jspFile = new AddJSPFilePathDialog( bot );
        customJSPpage.getAddBtn().click();
        jspFile.getJspFilePath().setText( "test.jsp" );
        jspFile.cancel();

        customJSPpage.getAddBtn().click();
        jspFile.getJspFilePath().setText( "test.jsp" );
        jspFile.confirm();

        customJSPpage.getJspFilesToOverride().click( 1, 0 );
        customJSPpage.getEditBtn().click();

        jspFile.getJspFilePath().setText( "hooktest.jsp" );
        jspFile.confirm();

        customJSPpage.getJspFilesToOverride().click( 1, 0 );
        customJSPpage.getRemoveBtn().click();
        assertTrue( customJSPpage.getDisableJspSyntaxValidation().isChecked() );
        customJSPpage.finish();

        Tree projectTree = ide.showPackageExporerView().getProjectTree();

        projectTree.expandNode(
            new String[] { projectHookName + "-hook", "docroot", "META-INF", "custom_jsps", "html", "common",
                "themes" } ).doubleClick( "bottom.jsp" );
    }

    @Test
    public void hookConfigurationLanguageProperties()
    {
        ide.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();
        createLiferayHookConfiguration.getLanguageProperties().select();

        LanguagePropertiesWizard languageProperties = new LanguagePropertiesWizard(
            bot, TITLE_NEW_LIFERAY_HOOK, INDEX_LANGUAGE_PROPERTIES_WITH_SDK7_VALIDATION_MESSAGE );
        sleep( 3000 );
        assertEquals( LABLE_LANGUAGE_PROPERTIES_IN_SDK7_IS_NOT_SUPPORTED, languageProperties.getValidationMsg() );
        sleep( 3000 );
        assertFalse( languageProperties.nextBtn().isEnabled() );
        sleep( 3000 );
        createLiferayHookConfiguration.cancel();

    }

    @Test
    public void hookConfigurationPortalProperties()
    {
        String defaultMsg = "Specify which portal properties to override.";
        String errorMsg1 = " portal.properties file not configured.";
        String errorMsg2 = " Need to specify at least one Event Action or Property to override.";

        PortalPropertiesWizard portalPropertiesPage =
            new PortalPropertiesWizard( bot, INDEX_PORTAL_PROPERTIES_VALIDATION_MESSAGE );

        ide.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();

        assertEquals(
            projectHookName + "-hook", createLiferayHookConfiguration.getHookPluginProjectComboBox().getText() );

        createLiferayHookConfiguration.getPortalProperties().select();
        createLiferayHookConfiguration.next();

        assertEquals( defaultMsg, portalPropertiesPage.getValidationMsg() );
        assertEquals(
            "/hook-configuration-wizard-hook/docroot/WEB-INF/src/portal.properties",
            portalPropertiesPage.getPortalPropertiesFile().getText() );

        portalPropertiesPage.getPortalPropertiesFile().setText( " " );
        assertEquals( errorMsg1, portalPropertiesPage.getValidationMsg() );

        portalPropertiesPage.getPortalPropertiesFile().setText( "123" );
        assertEquals( errorMsg2, portalPropertiesPage.getValidationMsg() );

        portalPropertiesPage.getBrowseBtn().click();
        PortalPropertiesFileDialog propertiesPage = new PortalPropertiesFileDialog( bot );
        propertiesPage.select( "hook-configuration-wizard-hook", "docroot", "WEB-INF", "src" );
        propertiesPage.cancel();

        portalPropertiesPage.getBrowseBtn().click();
        propertiesPage.select( "hook-configuration-wizard-hook", "docroot", "WEB-INF", "src" );
        propertiesPage.confirm();

        // Define actions to be executed on portal events
        AddEventActionDialog eventActionPage = new AddEventActionDialog( bot );

        portalPropertiesPage.getEventAddBtn().click();
        portalPropertiesPage.cancel();
        portalPropertiesPage.getEventAddBtn().click();

        eventActionPage.getNewBtn().click();
        eventActionPage.cancel();
        eventActionPage.getNewBtn().click();

        NewClassDialog newClassPage = new NewClassDialog( bot );

        newClassPage.getClassName().setText( "test" );
        newClassPage.getBrowseBtn().click();
        newClassPage.cancel();
        newClassPage.getJavaPackage().setText( "hook" );

        assertEquals( "com.liferay.portal.kernel.events.SimpleAction", newClassPage.getSuperClass().getText() );
        newClassPage.getCreateBtn().click();

        PropertySelectionDialog propertySelectionPage = new PropertySelectionDialog( bot );

        eventActionPage.getSelectEventBtn().click();
        propertySelectionPage.getSelectProperty().setText( "*startup.events" );
        propertySelectionPage.select( "application.startup.events" );
        propertySelectionPage.confirm();

        portalPropertiesPage.getAddEventActionDialog().setFocus();
        eventActionPage.confirm();

        portalPropertiesPage.getEventAddBtn().click();
        eventActionPage.getSelectEventBtn().click();
        propertySelectionPage.select( "application.startup.events" );
        propertySelectionPage.confirm();
        portalPropertiesPage.getAddEventActionDialog().setFocus();

        EventSelectionDialog eventSelectionPage = new EventSelectionDialog( bot );
        eventActionPage.getSelectClassBtn().click();
        eventSelectionPage.getEventAction().setText( " " );
        sleep( 3000 );
        eventSelectionPage.cancel();

        eventActionPage.getSelectClassBtn().click();
        sleep( 30000 );
        eventSelectionPage.getEventAction().setText( "wwww" );
        sleep( 3000 );
        assertFalse( eventActionPage.confirmBtn().isEnabled() );
        eventSelectionPage.cancel();

        eventActionPage.getSelectClassBtn().click();
        sleep( 30000 );
        eventSelectionPage.getEventAction().setText( "ObjectAction" );
        eventSelectionPage.confirm();
        sleep( 2000 );
        eventActionPage.confirm();

        portalPropertiesPage.getDefineActionsOnPortalEvents().click( 1, 1 );
        portalPropertiesPage.getEventRemoveBtn().click();

        portalPropertiesPage.getDefineActionsOnPortalEvents().click( 0, 1 );
        portalPropertiesPage.getEventEditBtn().click();

        eventActionPage.getEventActionClass().setText( "test_hook" );
        eventActionPage.confirm();

        // Specify properties to override
        AddPortalPropertiesOverrideDialog propertyOverridePage = new AddPortalPropertiesOverrideDialog( bot );

        portalPropertiesPage.getPropertyAddBtn().click();
        portalPropertiesPage.cancel();
        portalPropertiesPage.getPropertyAddBtn().click();

        propertyOverridePage.getSelectPropertyBtn().click();

        propertySelectionPage.select( "admin.default.group.names" );
        propertySelectionPage.confirm();

        portalPropertiesPage.getAddPropertyOverrideDialog().setFocus();
        propertyOverridePage.getValue().setText( "1" );
        propertyOverridePage.confirm();

        portalPropertiesPage.getPropertyAddBtn().click();
        propertyOverridePage.getProperty().setText( "test" );
        propertyOverridePage.getValue().setText( "2" );
        propertyOverridePage.confirm();

        portalPropertiesPage.getNewLiferayHookConfigurationDialog().setFocus();
        portalPropertiesPage.getSpecifyProperties().click( 1, 1 );
        portalPropertiesPage.getPropertyEditBtn().click();

        propertyOverridePage.getProperty().setText( "test_hook" );
        propertyOverridePage.getValue().setText( "3" );
        propertyOverridePage.confirm();

        portalPropertiesPage.getNewLiferayHookConfigurationDialog().setFocus();
        portalPropertiesPage.getSpecifyProperties().click( 1, 1 );

        portalPropertiesPage.getPropertyRemoveBtn().click();
        portalPropertiesPage.finish();
        sleep();

        Tree projectTree = ide.getPackageExporerView().getProjectTree();

        String fileName = "portal.properties";

        projectTree.expandNode( new String[] { projectHookName + "-hook", "docroot/WEB-INF/src" } ).doubleClick(
            fileName );

        Editor editor = ide.getEditor( fileName );

        assertTrue( editor.isActive() );

        assertContains( "application.startup.events=test_hook", editor.getText() );
        assertContains( "admin.default.group.names=1", editor.getText() );

        projectTree = ide.showPackageExporerView().getProjectTree();

        fileName = "test.java";

        projectTree.expandNode( new String[] { projectHookName + "-hook", "docroot/WEB-INF/src", "hook" } ).doubleClick(
            fileName );

        editor = ide.getEditor( fileName );

        assertTrue( editor.isActive() );

        assertContains( "SimpleAction", editor.getText() );
    }

    @Test
    public void hookConfigurationServices()
    {
        String defaultMsg = "Specify which Liferay services to extend.";
        String errorMsg = " Need to specify at least one Service to override.";

        ServicesWizard servicesPage = new ServicesWizard( bot, INDEX_SERVICES_MESSAGE );

        ide.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();

        createLiferayHookConfiguration.getServices().select();

        createLiferayHookConfiguration.next();

        assertEquals( defaultMsg, servicesPage.getValidationMsg() );

        servicesPage.getAddBtn().click();
        servicesPage.cancel();
        servicesPage.getAddBtn().click();

        AddServiceWrapperDialog serviceWrapperPage = new AddServiceWrapperDialog( bot );
        Dialog serviceWarningPage = new Dialog( bot );

        serviceWrapperPage.getNewBtn().click();
        servicesPage.getAddServiceDialog().setFocus();
        serviceWarningPage.confirm();

        servicesPage.getAddServiceWrapperDialog().setFocus();
        serviceWrapperPage.getSelectImplClass( 1 ).click();

        serviceWarningPage.confirm();

        servicesPage.getAddServiceWrapperDialog().setFocus();
        serviceWrapperPage.getServiceType().setText( "test" );
        serviceWrapperPage.getImplClass().setText( "test" );

        servicesPage.getAddServiceWrapperDialog().setFocus();
        serviceWrapperPage.confirm();
        servicesPage.getDefinePortalServices().click( 0, 1 );
        servicesPage.getRemoveBtn().click();

        assertEquals( errorMsg, servicesPage.getValidationMsg() );

        servicesPage.getAddBtn().click();
        serviceWrapperPage.getSelectServiceType().click();
        serviceWrapperPage.cancel();
        serviceWrapperPage.getSelectServiceType().click();
        sleep( 15000 );

        SuperClassDialog superclassPage = new SuperClassDialog( bot );

        NewImplClassDialog newImplClassPage = new NewImplClassDialog( bot );

        superclassPage.getSuperClass().setText( "AccountService" );
        superclassPage.confirm();

        serviceWrapperPage.getSelectImplClass( 1 ).click();
        superclassPage.cancel();
        serviceWrapperPage.getSelectImplClass( 1 ).click();
        serviceWrapperPage.confirm();

        servicesPage.getAddServiceWrapperDialog().setFocus();
        serviceWrapperPage.getNewBtn().click();
        newImplClassPage.getClassName().setText( " " );

        assertFalse( newImplClassPage.getCreateBtn().isEnabled() );

        newImplClassPage.getClassName().setText( "ExtAccountLocalService" );

        newImplClassPage.getBrowseBtn().click();
        newImplClassPage.cancel();
        newImplClassPage.getJavaPackage().setText( "hookservice" );

        newImplClassPage.getCreateBtn().click();

        servicesPage.getAddServiceWrapperDialog().setFocus();
        serviceWrapperPage.confirm();
        servicesPage.getDefinePortalServices().click( 0, 1 );
        servicesPage.getEditBtn().click();

        serviceWrapperPage.getImplClass().setText( "hookservice.ExtAccountService" );
        serviceWrapperPage.confirm();

        servicesPage.finish();

        Tree projectTree = ide.getPackageExporerView().getProjectTree();

        String fileName = "ExtAccountLocalService.java";
        projectTree.expandNode(
            new String[] { projectHookName + "-hook", "docroot/WEB-INF/src", "hookservice" } ).doubleClick( fileName );

        Editor editor = ide.getEditor( fileName );

        assertTrue( editor.isActive() );

        assertContains( "ExtAccountLocalService", editor.getText() );
    }

    @Before
    public void openWizardCreateProject()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        hasAddedProject = addedProjects();

        if( hasAddedProject )
        {
            return;
        }

        sleep( 3000 );

        ide.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();
        sleep( 3000 );

        Dialog dialogPage = new Dialog( bot, LABEL_NEW_LIFERAY_HOOK_CONFIGURATION, NO, YES );
        dialogPage.cancel();
        sleep( 3000 );

        assertEquals( TEXT_BLANK, createLiferayHookConfiguration.getHookPluginProjectComboBox().getText() );

        createLiferayHookConfiguration.getCustomJSPs().select();
        createLiferayHookConfiguration.getPortalProperties().select();
        createLiferayHookConfiguration.getServices().select();
        createLiferayHookConfiguration.getLanguageProperties().select();
        sleep( 5000 );

        assertEquals( TEXT_ENTER_A_PROJECT_NAME, createLiferayHookConfiguration.getValidationMsg() );

        createLiferayHookConfiguration.cancel();

        ide.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();
        dialogPage.confirm();

        createProjectWizard.createSDKProject( projectHookName, MENU_HOOK );

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

        sleep( 10000 );

    }
}
