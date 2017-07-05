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

import com.liferay.ide.swtbot.hook.ui.tests.page.AddEventActionPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.AddJSPFilePathPO;
//import com.liferay.ide.swtbot.swtbot.hook.ui.tests.page.AddLanguagePropertyPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.AddPortalPropertiesOverridePO;
import com.liferay.ide.swtbot.hook.ui.tests.page.AddServiceWarningPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.AddServiceWrapperPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.ContainerSelectionPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.CreateCustomJSPsPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.EventSelectionPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.CreateLiferayHookConfigurationPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.LanguagePropertiesPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.LiferayCustomJSPPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.NewClassPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.NewImplClassPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.PortalPropertiesFilePO;
import com.liferay.ide.swtbot.hook.ui.tests.page.PortalPropertiesPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.PropertySelectionPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.ServicesPO;
import com.liferay.ide.swtbot.hook.ui.tests.page.SuperclassSelectionPO;
import com.liferay.ide.swtbot.project.ui.tests.page.CreateProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SetSDKLocationPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TextEditorPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class HookConfigurationWizardTests extends SWTBotBase implements HookConfigurationWizard
{

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    CreateLiferayHookConfigurationPO createLiferayHookConfiguration =
        new CreateLiferayHookConfigurationPO( bot, TITLE_NEW_LIFERAY_HOOK, INDEX_NEW_LIFERAY_HOOK_VALIDATION_MESSAGE );
    CreateProjectWizardPO createProjectWizard = new CreateProjectWizardPO( bot, INDEX_VALIDATION_MESSAGE );
    String projectHookName = "hook-configuration-wizard";

    @AfterClass
    public static void cleanAll()
    {
        try
        {
            eclipse.closeShell( TITLE_NEW_LIFERAY_HOOK );
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

        unzipPluginsSDK();
        unzipServer();
    }

    @Test
    public void hookConfigurationAllHookTypes()
    {
        CreateCustomJSPsPO customJSPpage = new CreateCustomJSPsPO( bot, INDEX_CUSTOM_JSPS_VALIDATION_MESSAGE );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();

        createLiferayHookConfiguration.getCustomJSPs().select();
        createLiferayHookConfiguration.getPortalProperties().select();
        createLiferayHookConfiguration.getServices().select();
        createLiferayHookConfiguration.getLanguageProperties().select();

        // check it doesn't support new language properties with sdk 7
        LanguagePropertiesPO languageProperties = new LanguagePropertiesPO(
            bot, TITLE_NEW_LIFERAY_HOOK, INDEX_LANGUAGE_PROPERTIES_WITH_SDK7_VALIDATION_MESSAGE );
        assertEquals( LABLE_LANGUAGE_PROPERTIES_IN_SDK7_IS_NOT_SUPPORTED, languageProperties.getValidationMessage() );
        assertFalse( languageProperties.nextButton().isEnabled() );

        createLiferayHookConfiguration.getLanguageProperties().deselect();
        createLiferayHookConfiguration.next();

        // Custom JSPs
        AddJSPFilePathPO jspFile = new AddJSPFilePathPO( bot );

        customJSPpage.getAddButton().click();
        jspFile.setJSPFilePathText( "CustomJsps.jsp" );
        jspFile.confirm();
        customJSPpage.next();

        // Portal Properties
        PortalPropertiesPO portalPropertiesPage =
            new PortalPropertiesPO( bot, INDEX_PORTAL_PROPERTIES_VALIDATION_MESSAGE );
        AddEventActionPO eventActionPage = new AddEventActionPO( bot );

        portalPropertiesPage.getEventAddButton().click();
        eventActionPage.setEvent( "portalProperties" );
        eventActionPage.setEventActionclass( "portalPropertiesClass" );
        eventActionPage.confirm();
        sleep();

        portalPropertiesPage.next();

        // Service
        ServicesPO servicesPage = new ServicesPO( bot, INDEX_SERVICES_MESSAGE );
        AddServiceWrapperPO serviceWrapperPage = new AddServiceWrapperPO( bot );

        servicesPage.getAddButton().click();
        serviceWrapperPage.setServiceTypeText( "com.liferay.portal.service.AddressService" );
        serviceWrapperPage.setImplClassText( "com.liferay.portal.service.AddressServiceWrapper" );
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
        // languagePropertiesPage.getAddButton().click();
        // languageProperty.setLanguagePropertyFileText(
        // "languageTest.properties" );
        // languageProperty.confirm();
        // languagePropertiesPage.finish();

        servicesPage.finish();

        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();

        String fileName = "CustomJsps.jsp";

        projectTree.expandNode(
            new String[] { projectHookName + "-hook", "docroot", "META-INF", "custom_jsps" } ).doubleClick( fileName );

        fileName = "portal.properties";

        projectTree.expandNode( new String[] { projectHookName + "-hook", "docroot/WEB-INF/src" } ).doubleClick(
            fileName );

        TextEditorPO textEditor = eclipse.getTextEditor( fileName );

        assertTrue( textEditor.isActive() );
        assertContains( "portalProperties=portalPropertiesClass", textEditor.getText() );

        // projectTree = eclipse.showPackageExporerView().getProjectTree();
        //
        // fileName = "languageTest.properties";
        //
        // projectTree.expandNode(
        // new String[] { projectHookName + "-hook", "docroot/WEB-INF/src",
        // "content" } ).doubleClick( fileName );

    }

    @Test
    public void hookConfigurationCustomJSPs()
    {
        String defaultMessage = "Create customs JSP folder and select JSPs to override.";
        String errorMessage01 = " Custom JSPs folder not configured.";
        String errorMessage02 = " Need to specify at least one JSP to override.";
        String warningMessage = " Shouldn't add same jsp file.";
        CreateCustomJSPsPO customJSPpage = new CreateCustomJSPsPO( bot, INDEX_CUSTOM_JSPS_VALIDATION_MESSAGE );

        assertEquals(
            projectHookName + "-hook", createLiferayHookConfiguration.getHookPluginProjectComboBox().getText() );

        createLiferayHookConfiguration.getCustomJSPs().select();
        createLiferayHookConfiguration.next();

        // Custom JSPs page
        assertEquals( defaultMessage, customJSPpage.getValidationMessage() );
        assertEquals( "hook-configuration-wizard-hook", customJSPpage.getSelectedProject().getText() );
        assertEquals( "docroot", customJSPpage.getWebRootFolder().getText() );
        assertEquals( "/META-INF/custom_jsps", customJSPpage.getCustomJSPfolder().getText() );

        customJSPpage.setCustomJSPfolder( "" );
        assertEquals( errorMessage01, customJSPpage.getValidationMessage() );

        customJSPpage.setCustomJSPfolder( "22" );
        assertEquals( errorMessage02, customJSPpage.getValidationMessage() );

        customJSPpage.getBrowseButton().click();

        ContainerSelectionPO chooseFolder = new ContainerSelectionPO( bot );

        chooseFolder.select( "hook-configuration-wizard-hook", "docroot", "META-INF" );
        chooseFolder.confirm();
        customJSPpage.setCustomJSPfolder( "/META-INF/custom_jsps" );

        // JSP files to override
        LiferayCustomJSPPO chooseCustomJSP = new LiferayCustomJSPPO( bot );
        customJSPpage.getAddFromLiferayButton().click();

        chooseCustomJSP.select( "html", "common", "themes", "bottom.jsp" );
        chooseCustomJSP.cancel();

        customJSPpage.getAddFromLiferayButton().click();
        chooseCustomJSP.getSelectJspToCustomize().setText( "bottom.jsp" );
        chooseCustomJSP.select( "html", "common", "themes", "bottom.jsp" );
        chooseCustomJSP.confirm();

        customJSPpage.getAddFromLiferayButton().click();
        chooseCustomJSP.select( "html", "common", "themes", "bottom.jsp" );
        chooseCustomJSP.confirm();

        assertEquals( warningMessage, customJSPpage.getValidationMessage() );
        customJSPpage.getJspFilesToOverride().click( 1, 0 );
        customJSPpage.getRemoveButton().click();

        AddJSPFilePathPO jspFile = new AddJSPFilePathPO( bot );
        customJSPpage.getAddButton().click();
        jspFile.setJSPFilePathText( "test.jsp" );
        jspFile.cancel();

        customJSPpage.getAddButton().click();
        jspFile.setJSPFilePathText( "test.jsp" );
        jspFile.confirm();

        customJSPpage.getJspFilesToOverride().click( 1, 0 );
        customJSPpage.getEditButton().click();

        jspFile.setJSPFilePathText( "hooktest.jsp" );
        jspFile.confirm();

        customJSPpage.getJspFilesToOverride().click( 1, 0 );
        customJSPpage.getRemoveButton().click();
        assertTrue( customJSPpage.getDisableJspSyntaxValidation().isChecked() );
        customJSPpage.finish();

        TreePO projectTree = eclipse.showPackageExporerView().getProjectTree();

        projectTree.expandNode(
            new String[] { projectHookName + "-hook", "docroot", "META-INF", "custom_jsps", "html", "common",
                "themes" } ).doubleClick( "bottom.jsp" );
    }

    @Test
    public void hookConfigurationLanguageProperties()
    {
        // String defaultMessage = "Create new Language properties files.";
        // String errorMessage = " Content folder not configured.";
        eclipse.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();
        createLiferayHookConfiguration.getLanguageProperties().select();

        LanguagePropertiesPO languageProperties = new LanguagePropertiesPO(
            bot, TITLE_NEW_LIFERAY_HOOK, INDEX_LANGUAGE_PROPERTIES_WITH_SDK7_VALIDATION_MESSAGE );
        sleep( 3000 );
        assertEquals( LABLE_LANGUAGE_PROPERTIES_IN_SDK7_IS_NOT_SUPPORTED, languageProperties.getValidationMessage() );
        sleep( 3000 );
        assertFalse( languageProperties.nextButton().isEnabled() );
        sleep( 3000 );
        createLiferayHookConfiguration.cancel();

        // should test with sdk 6.2
        // newHookTypesPage.next();
        //
        // LanguagePropertiesPO languagePropertiesPage = new
        // LanguagePropertiesPO(
        // bot, "New Liferay Hook Configuration",
        // INDEX_LANGUAGE_PROPERTIES_VALIDATION_MESSAGE );
        //
        // assertEquals( defaultMessage,
        // languagePropertiesPage.getValidationMessage() );
        // assertEquals(
        // "/hook-configuration-wizard-hook/docroot/WEB-INF/src/content",
        // languagePropertiesPage.getContentFolderText() );
        //
        // languagePropertiesPage.setContentFolderText( "" );
        // sleep( 500 );
        //
        // assertEquals( errorMessage,
        // languagePropertiesPage.getValidationMessage() );
        //
        // languagePropertiesPage.getBrowseButton().click();
        // sleep( 500 );
        // ContainerSelectionPO chooseFolder = new ContainerSelectionPO( bot );
        //
        // chooseFolder.select( "hook-configuration-wizard-hook", "docroot",
        // "WEB-INF", "src" );
        //
        // chooseFolder.confirm();
        //
        // sleep( 500 );
        //
        // // Language property files
        // languagePropertiesPage.getAddButton().click();
        //
        // AddLanguagePropertyPO languageProperty = new AddLanguagePropertyPO(
        // bot );
        // languageProperty.setLanguagePropertyFileText( "test.properties" );
        // languageProperty.confirm();
        //
        // languagePropertiesPage.getAddButton().click();
        //
        // languageProperty.setLanguagePropertyFileText( "test-hook" );
        // languageProperty.confirm();
        //
        // languagePropertiesPage.setFocus();
        // languagePropertiesPage.clickLanguagePropertyFiles( 1 );
        //
        // languagePropertiesPage.getEditButton().click();
        //
        // languageProperty.setLanguagePropertyFileText( "hook" );
        // languageProperty.confirm();
        //
        // languagePropertiesPage.setFocus();
        // languagePropertiesPage.clickLanguagePropertyFiles( 1 );
        //
        // languagePropertiesPage.getRemoveButton().click();
        //
        // languagePropertiesPage.finish();
        //
        // TreePO projectTree =
        // eclipse.showPackageExporerView().getProjectTree();
        //
        // projectTree.expandNode(
        // new String[] { projectHookName + "-hook", "docroot/WEB-INF/src",
        // "content" } ).doubleClick(
        // "test.properties" );
    }

    @Test
    public void hookConfigurationPortalProperties()
    {
        String defaultMessage = "Specify which portal properties to override.";
        String errorMessage1 = " portal.properties file not configured.";
        String errorMessage2 = " Need to specify at least one Event Action or Property to override.";

        PortalPropertiesPO portalPropertiesPage =
            new PortalPropertiesPO( bot, INDEX_PORTAL_PROPERTIES_VALIDATION_MESSAGE );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();

        assertEquals(
            projectHookName + "-hook", createLiferayHookConfiguration.getHookPluginProjectComboBox().getText() );

        createLiferayHookConfiguration.getPortalProperties().select();
        createLiferayHookConfiguration.next();

        assertEquals( defaultMessage, portalPropertiesPage.getValidationMessage() );
        assertEquals(
            "/hook-configuration-wizard-hook/docroot/WEB-INF/src/portal.properties",
            portalPropertiesPage.getPortalPropertiesFile().getText() );

        portalPropertiesPage.setPortalPropertiesFile( " " );
        assertEquals( errorMessage1, portalPropertiesPage.getValidationMessage() );

        portalPropertiesPage.setPortalPropertiesFile( "123" );
        assertEquals( errorMessage2, portalPropertiesPage.getValidationMessage() );

        portalPropertiesPage.getBrowseButton().click();
        PortalPropertiesFilePO propertiesPage = new PortalPropertiesFilePO( bot );
        propertiesPage.select( "hook-configuration-wizard-hook", "docroot", "WEB-INF", "src" );
        propertiesPage.cancel();

        portalPropertiesPage.getBrowseButton().click();
        propertiesPage.select( "hook-configuration-wizard-hook", "docroot", "WEB-INF", "src" );
        propertiesPage.confirm();

        // Define actions to be executed on portal events
        AddEventActionPO eventActionPage = new AddEventActionPO( bot );

        portalPropertiesPage.getEventAddButton().click();
        portalPropertiesPage.cancel();
        portalPropertiesPage.getEventAddButton().click();

        eventActionPage.getNewButton().click();
        eventActionPage.cancel();
        eventActionPage.getNewButton().click();

        NewClassPO newClassPage = new NewClassPO( bot );

        newClassPage.setClassName( "test" );
        newClassPage.getBrowseButton().click();
        newClassPage.cancel();
        newClassPage.setJavaPackage( "hook" );

        assertEquals( "com.liferay.portal.kernel.events.SimpleAction", newClassPage.getSuperClass().getText() );
        newClassPage.getCreateButton().click();

        PropertySelectionPO propertySelectionPage = new PropertySelectionPO( bot );

        eventActionPage.getSelectEventButton().click();
        propertySelectionPage.setPleaseSelectProperty( "*startup.events" );
        propertySelectionPage.select( "application.startup.events" );
        propertySelectionPage.confirm();

        portalPropertiesPage.getAddEventAction().setFocus();
        eventActionPage.confirm();

        portalPropertiesPage.getEventAddButton().click();
        eventActionPage.getSelectEventButton().click();
        propertySelectionPage.select( "application.startup.events" );
        propertySelectionPage.confirm();
        portalPropertiesPage.getAddEventAction().setFocus();

        EventSelectionPO eventSelectionPage = new EventSelectionPO( bot );
        eventActionPage.getSelectClass().click();
        eventSelectionPage.setEventAction( " " );
        sleep( 3000 );
        eventSelectionPage.cancel();

        eventActionPage.getSelectClass().click();
        sleep( 30000 );
        eventSelectionPage.setEventAction( "wwww" );
        sleep( 3000 );
        assertFalse( eventActionPage.confirmButton().isEnabled() );
        eventSelectionPage.cancel();

        eventActionPage.getSelectClass().click();
        sleep( 30000 );
        eventSelectionPage.setEventAction( "ObjectAction" );
        eventSelectionPage.confirm();
        sleep( 2000 );
        eventActionPage.confirm();

        portalPropertiesPage.getDefineActionsOnPortalEvents().click( 1, 1 );
        portalPropertiesPage.getEventRemoveButton().click();

        portalPropertiesPage.getDefineActionsOnPortalEvents().click( 0, 1 );
        portalPropertiesPage.getEventEditButton().click();

        eventActionPage.setEventActionclass( "test_hook" );
        eventActionPage.confirm();

        // Specify properties to override
        AddPortalPropertiesOverridePO propertyOverridePage = new AddPortalPropertiesOverridePO( bot );

        portalPropertiesPage.getPropertyAddButton().click();
        portalPropertiesPage.cancel();
        portalPropertiesPage.getPropertyAddButton().click();

        propertyOverridePage.getSelectProperty().click();

        propertySelectionPage.select( "admin.default.group.names" );
        propertySelectionPage.confirm();

        portalPropertiesPage.getAddPropertyOverride().setFocus();
        propertyOverridePage.setValue( "1" );
        propertyOverridePage.confirm();

        portalPropertiesPage.getPropertyAddButton().click();
        propertyOverridePage.setProperty( "test" );
        propertyOverridePage.setValue( "2" );
        propertyOverridePage.confirm();

        portalPropertiesPage.getNewLiferayHookConfiguration().setFocus();
        portalPropertiesPage.getSpecifyPropertiesToOverride().click( 1, 1 );
        portalPropertiesPage.getPropertyEditButton().click();

        propertyOverridePage.setProperty( "test_hook" );
        propertyOverridePage.setValue( "3" );
        propertyOverridePage.confirm();

        portalPropertiesPage.getNewLiferayHookConfiguration().setFocus();
        portalPropertiesPage.getSpecifyPropertiesToOverride().click( 1, 1 );

        portalPropertiesPage.getPropertyRemoveButton().click();
        portalPropertiesPage.finish();

        TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

        String fileName = "portal.properties";

        projectTree.expandNode( new String[] { projectHookName + "-hook", "docroot/WEB-INF/src" } ).doubleClick(
            fileName );

        TextEditorPO textEditor = eclipse.getTextEditor( fileName );

        assertTrue( textEditor.isActive() );

        assertContains( "application.startup.events=test_hook", textEditor.getText() );
        assertContains( "admin.default.group.names=1", textEditor.getText() );

        projectTree = eclipse.showPackageExporerView().getProjectTree();

        fileName = "test.java";

        projectTree.expandNode( new String[] { projectHookName + "-hook", "docroot/WEB-INF/src", "hook" } ).doubleClick(
            fileName );

        textEditor = eclipse.getTextEditor( fileName );

        assertTrue( textEditor.isActive() );

        assertContains( "SimpleAction", textEditor.getText() );
    }

    @Test
    public void hookConfigurationServices()
    {
        String defaultMessage = "Specify which Liferay services to extend.";
        String errorMessage = " Need to specify at least one Service to override.";

        ServicesPO servicesPage = new ServicesPO( bot, INDEX_SERVICES_MESSAGE );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();

        createLiferayHookConfiguration.getServices().select();

        createLiferayHookConfiguration.next();

        assertEquals( defaultMessage, servicesPage.getValidationMessage() );

        servicesPage.getAddButton().click();
        servicesPage.cancel();
        servicesPage.getAddButton().click();

        AddServiceWrapperPO serviceWrapperPage = new AddServiceWrapperPO( bot );
        AddServiceWarningPO serviceWarningPage = new AddServiceWarningPO( bot );

        serviceWrapperPage.getNewButton().click();
        servicesPage.getAddService().setFocus();
        serviceWarningPage.getOkButton().click();

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.getSelectImplClass( 1 ).click();

        serviceWarningPage.getOkButton().click();

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.setServiceTypeText( "test" );
        serviceWrapperPage.setImplClassText( "test" );

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.confirm();
        servicesPage.getDefinePortalServices().click( 0, 1 );
        servicesPage.getRemoveButton().click();

        assertEquals( errorMessage, servicesPage.getValidationMessage() );

        servicesPage.getAddButton().click();
        serviceWrapperPage.getSelectServiceType().click();
        serviceWrapperPage.cancel();
        serviceWrapperPage.getSelectServiceType().click();
        sleep( 15000 );

        SuperclassSelectionPO superclassPage = new SuperclassSelectionPO( bot );

        NewImplClassPO newImplClassPage = new NewImplClassPO( bot );

        superclassPage.setSuperclass( "AccountService" );
        superclassPage.confirm();

        serviceWrapperPage.getSelectImplClass( 1 ).click();
        superclassPage.cancel();
        serviceWrapperPage.getSelectImplClass( 1 ).click();
        serviceWrapperPage.confirm();

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.getNewButton().click();
        newImplClassPage.getClassname().setText( " " );

        assertFalse( newImplClassPage.getCreateButton().isEnabled() );

        newImplClassPage.getClassname().setText( "ExtAccountLocalService" );

        newImplClassPage.getBrowseButton().click();
        newImplClassPage.cancel();
        newImplClassPage.getJavaPackage().setText( "hookservice" );

        newImplClassPage.getCreateButton().click();

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.confirm();
        servicesPage.getDefinePortalServices().click( 0, 1 );
        servicesPage.getEditButton().click();

        serviceWrapperPage.getImplClass().setText( "hookservice.ExtAccountService" );
        serviceWrapperPage.confirm();

        servicesPage.finish();

        TreePO projectTree = eclipse.getPackageExporerView().getProjectTree();

        String fileName = "ExtAccountLocalService.java";
        projectTree.expandNode(
            new String[] { projectHookName + "-hook", "docroot/WEB-INF/src", "hookservice" } ).doubleClick( fileName );

        TextEditorPO textEditor = eclipse.getTextEditor( fileName );

        assertTrue( textEditor.isActive() );

        assertContains( "ExtAccountLocalService", textEditor.getText() );
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

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();
        sleep( 3000 );

        DialogPO dialogPage = new DialogPO( bot, LABEL_NEW_LIFERAY_HOOK_CONFIGURATION, BUTTON_NO, BUTTON_YES );
        dialogPage.cancel();
        sleep( 3000 );

        assertEquals( TEXT_BLANK, createLiferayHookConfiguration.getHookPluginProjectComboBox().getText() );

        createLiferayHookConfiguration.getCustomJSPs().select();
        createLiferayHookConfiguration.getPortalProperties().select();
        createLiferayHookConfiguration.getServices().select();
        createLiferayHookConfiguration.getLanguageProperties().select();
        sleep( 5000 );

        assertEquals( TEXT_ENTER_A_PROJECT_NAME, createLiferayHookConfiguration.getValidationMessage() );

        createLiferayHookConfiguration.cancel();

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayHookConfiguration().click();
        dialogPage.confirm();

        createProjectWizard.createSDKProject( projectHookName, MENU_HOOK );

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

        sleep( 10000 );

    }
}
