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

package com.liferay.ide.hook.ui.tests;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.hook.ui.tests.page.AddEventActionPageObject;
import com.liferay.ide.hook.ui.tests.page.AddJSPFilePathPageObject;
import com.liferay.ide.hook.ui.tests.page.AddLanguagePropertyPageObject;
import com.liferay.ide.hook.ui.tests.page.AddPortalPropertiesOverridePageObject;
import com.liferay.ide.hook.ui.tests.page.AddServiceWarningPageObject;
import com.liferay.ide.hook.ui.tests.page.AddServiceWrapperPageObject;
import com.liferay.ide.hook.ui.tests.page.ContainerSelectionPageObject;
import com.liferay.ide.hook.ui.tests.page.CreateCustomJSPsPageObject;
import com.liferay.ide.hook.ui.tests.page.EventSelectionPageObject;
import com.liferay.ide.hook.ui.tests.page.HookTypesToCreatePageObject;
import com.liferay.ide.hook.ui.tests.page.LanguagePropertiesPageObject;
import com.liferay.ide.hook.ui.tests.page.LiferayCustomJSPPageObject;
import com.liferay.ide.hook.ui.tests.page.NewClassPageObject;
import com.liferay.ide.hook.ui.tests.page.NewImplClassPageObject;
import com.liferay.ide.hook.ui.tests.page.PortalPropertiesFilePageObject;
import com.liferay.ide.hook.ui.tests.page.PortalPropertiesPageObject;
import com.liferay.ide.hook.ui.tests.page.PropertySelectionPageObject;
import com.liferay.ide.hook.ui.tests.page.ServicesPageObject;
import com.liferay.ide.hook.ui.tests.page.SuperclassSelectionPageObject;
import com.liferay.ide.project.ui.tests.page.CreateProjectWizardPageObject;
import com.liferay.ide.project.ui.tests.page.SetSDKLocationPageObject;
import com.liferay.ide.ui.tests.SWTBotBase;
import com.liferay.ide.ui.tests.swtbot.page.EditorPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextEditorPageObject;

/**
 * @author Vicky Wang
 */
public class HookConfigurationWizardTests extends SWTBotBase implements HookConfigurationWizard
{

    public static boolean added = false;

    @AfterClass
    public static void cleanAll()
    {
        SWTBotTreeItem[] items = treeBot.getItems();

        try
        {
            for( SWTBotTreeItem item : items )
            {
                if( !item.getText().equals( getLiferayPluginsSdkName() ) )
                {
                    item.contextMenu( BUTTON_DELETE ).click();

                    checkBoxBot.click();

                    buttonBot.click( BUTTON_OK );

                    if( buttonBot.isEnabled( "Continue" ) )
                    {
                        buttonBot.click( "Continue" );
                    }

                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    HookTypesToCreatePageObject<SWTWorkbenchBot> newHookTypesPage =
        new HookTypesToCreatePageObject<SWTWorkbenchBot>( bot, "New Liferay Hook Configuration" );

    String projectHookName = "hook-configuration-wizard";

    private boolean addedProjects()
    {
        viewBot.show( VIEW_PACKAGE_EXPLORER );

        return treeBot.hasItems();
    }

    private SetSDKLocationPageObject<SWTWorkbenchBot> getSetSDKLoactionPage()
    {
        SetSDKLocationPageObject<SWTWorkbenchBot> page = new SetSDKLocationPageObject<SWTWorkbenchBot>( bot, "" );
        page.setSdkLocation( getLiferayPluginsSdkDir().toString() );

        return page;
    }

    @Test
    public void hookConfigurationAllHookTypes()
    {
        CreateCustomJSPsPageObject<SWTWorkbenchBot> customJSPpage =
            new CreateCustomJSPsPageObject<SWTWorkbenchBot>( bot, "", INDEX_CUSTOM_JSPS_VALIDATION_MESSAGE );

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_HOOK_CONFIGURATION );

        newHookTypesPage.getCustomJSPs().select();
        newHookTypesPage.getPortalProperties().select();
        newHookTypesPage.getServices().select();
        newHookTypesPage.getLanguageProperties().select();

        newHookTypesPage.next();
        sleep( 1000 );

        // Custom JSPs
        AddJSPFilePathPageObject<SWTWorkbenchBot> jspFile = new AddJSPFilePathPageObject<SWTWorkbenchBot>( bot, "" );

        customJSPpage.getAdd().click();
        jspFile.setJSPFilePathText( "CustomJsps.jsp" );
        jspFile.confirm();
        customJSPpage.next();
        sleep( 500 );

        // Portal Properties
        PortalPropertiesPageObject<SWTWorkbenchBot> portalPropertiesPage =
            new PortalPropertiesPageObject<SWTWorkbenchBot>( bot, "", INDEX_PORTAL_PROPERTIES_VALIDATION_MESSAGE );

        AddEventActionPageObject<SWTWorkbenchBot> eventActionPage =
            new AddEventActionPageObject<SWTWorkbenchBot>( bot );

        portalPropertiesPage.getEventAdd().click();
        eventActionPage.setEvent( "portalProperties" );
        eventActionPage.setEventActionclass( "portalPropertiesClass" );
        eventActionPage.confirm();

        portalPropertiesPage.next();
        sleep( 500 );

        // Service
        ServicesPageObject<SWTWorkbenchBot> servicesPage =
            new ServicesPageObject<SWTWorkbenchBot>( bot, "", INDEX_SERVICES_MESSAGE );

        AddServiceWrapperPageObject<SWTWorkbenchBot> serviceWrapperPage =
            new AddServiceWrapperPageObject<SWTWorkbenchBot>( bot );

        servicesPage.getAdd().click();
        serviceWrapperPage.setServiceTypeText( "com.liferay.portal.service.AddressService" );
        serviceWrapperPage.setImplClassText( "com.liferay.portal.service.AddressServiceWrapper" );
        serviceWrapperPage.confirm();

        servicesPage.next();
        sleep( 500 );

        // Language Properties
        LanguagePropertiesPageObject<SWTWorkbenchBot> languagePropertiesPage =
            new LanguagePropertiesPageObject<SWTWorkbenchBot>( bot, INDEX_LANGUAGE_PROPERTIES_VALIDATION_MESSAGE );

        AddLanguagePropertyPageObject<SWTWorkbenchBot> languageProperty =
            new AddLanguagePropertyPageObject<SWTWorkbenchBot>( bot );

        languagePropertiesPage.getAdd().click();
        languageProperty.setLanguagePropertyFileText( "languageTest.properties" );
        languageProperty.confirm();
        languagePropertiesPage.finish();
        sleep( 1000 );

        // check files
        treeBot.doubleClick( "CustomJsps.jsp", projectHookName + "-hook", "docroot", "META-INF", "custom_jsps" );
        treeBot.doubleClick( "portal.properties", projectHookName + "-hook", "docroot/WEB-INF/src" );
        TextEditorPageObject textEditorPage = new TextEditorPageObject( bot, "portal.properties" );
        assertContains( "portalProperties=portalPropertiesClass", textEditorPage.getText() );
        treeBot.doubleClick( "languageTest.properties", projectHookName + "-hook", "docroot/WEB-INF/src", "content" );
    }

    @Test
    public void hookConfigurationCustomJSPs()
    {
        String defaultMessage = "Create customs JSP folder and select JSPs to override.";
        String errorMessage = " Custom JSPs folder not configured.";

        CreateCustomJSPsPageObject<SWTWorkbenchBot> customJSPpage =
            new CreateCustomJSPsPageObject<SWTWorkbenchBot>( bot, "", INDEX_CUSTOM_JSPS_VALIDATION_MESSAGE );

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_HOOK_CONFIGURATION );

        newHookTypesPage.getCustomJSPs().select();
        newHookTypesPage.next();
        sleep( 1000 );

        // Custom JSPs page
        assertEquals( defaultMessage, customJSPpage.getValidationMessage() );
        assertEquals( "hook-configuration-wizard-hook", customJSPpage.getSelectedProject().getText() );
        assertEquals( "docroot", customJSPpage.getWebRootFolder().getText() );
        assertEquals( "/META-INF/custom_jsps", customJSPpage.getCustomJSPfolder().getText() );

        customJSPpage.setCustomJSPfolder( "" );
        sleep( 500 );
        assertEquals( errorMessage, customJSPpage.getValidationMessage() );

        customJSPpage.getBrowse().click();
        sleep( 500 );

        ContainerSelectionPageObject<SWTWorkbenchBot> chooseFolder =
            new ContainerSelectionPageObject<SWTWorkbenchBot>( bot );

        chooseFolder.select( "hook-configuration-wizard-hook", "docroot", "META-INF" );
        chooseFolder.confirm();

        customJSPpage.setCustomJSPfolder( "/META-INF/custom_jsps" );

        // JSP files to override
        LiferayCustomJSPPageObject<SWTWorkbenchBot> chooseCustomJSP =
            new LiferayCustomJSPPageObject<SWTWorkbenchBot>( bot );

        customJSPpage.getAddFromLiferay().click();
        chooseCustomJSP.select( "html", "common", "themes", "body_bottom.jsp" );
        chooseCustomJSP.confirm();
        sleep( 500 );

        AddJSPFilePathPageObject<SWTWorkbenchBot> jspFile = new AddJSPFilePathPageObject<SWTWorkbenchBot>( bot, "" );
        customJSPpage.getAdd().click();
        jspFile.setJSPFilePathText( "test.jsp" );
        jspFile.confirm();

        customJSPpage.getJspFilesToOverride().click( 1, 0 );
        customJSPpage.getEdit().click();

        jspFile.setJSPFilePathText( "hooktest.jsp" );
        jspFile.confirm();

        customJSPpage.getJspFilesToOverride().click( 1, 0 );
        customJSPpage.getRemove().click();

        customJSPpage.finish();
        sleep( 1000 );

        treeBot.doubleClick( "body_bottom.jsp", projectHookName + "-hook", "docroot", "META-INF", "custom_jsps", "html",
            "common", "themes" );
    }

    @Test
    public void hookConfigurationLanguageProperties()
    {
        String defaultMessage = "Create new Language properties files.";
        String errorMessage = " Content folder not configured.";

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_HOOK_CONFIGURATION );

        newHookTypesPage.getLanguageProperties().select();

        newHookTypesPage.next();

        sleep( 1000 );

        LanguagePropertiesPageObject<SWTWorkbenchBot> languagePropertiesPage =
            new LanguagePropertiesPageObject<SWTWorkbenchBot>(
                bot, "New Liferay Hook Configuration", INDEX_LANGUAGE_PROPERTIES_VALIDATION_MESSAGE );

        assertEquals( defaultMessage, languagePropertiesPage.getValidationMessage() );
        assertEquals(
            "/hook-configuration-wizard-hook/docroot/WEB-INF/src/content",
            languagePropertiesPage.getContentFolderText() );

        languagePropertiesPage.setContentFolderText( "" );
        sleep( 500 );

        assertEquals( errorMessage, languagePropertiesPage.getValidationMessage() );

        languagePropertiesPage.getBrowse().click();
        sleep( 500 );
        ContainerSelectionPageObject<SWTWorkbenchBot> chooseFolder =
            new ContainerSelectionPageObject<SWTWorkbenchBot>( bot );

        chooseFolder.select( "hook-configuration-wizard-hook", "docroot", "WEB-INF", "src" );

        chooseFolder.confirm();

        sleep( 500 );

        // Language property files
        languagePropertiesPage.getAdd().click();

        AddLanguagePropertyPageObject<SWTWorkbenchBot> languageProperty =
            new AddLanguagePropertyPageObject<SWTWorkbenchBot>( bot );
        languageProperty.setLanguagePropertyFileText( "test.properties" );
        languageProperty.confirm();

        languagePropertiesPage.getAdd().click();

        languageProperty.setLanguagePropertyFileText( "test-hook" );
        languageProperty.confirm();

        languagePropertiesPage.setFocus();
        languagePropertiesPage.clickLanguagePropertyFiles( 1 );

        languagePropertiesPage.getEdit().click();

        languageProperty.setLanguagePropertyFileText( "hook" );
        languageProperty.confirm();

        languagePropertiesPage.setFocus();
        languagePropertiesPage.clickLanguagePropertyFiles( 1 );

        languagePropertiesPage.getRemove().click();

        languagePropertiesPage.finish();

        // check language properties file exist in the project
        treeBot.doubleClick( "test.properties", projectHookName + "-hook", "docroot/WEB-INF/src", "content" );
        sleep( 1000 );
    }

    @Test
    public void hookConfigurationPortalProperties()
    {
        String defaultMessage = "Specify which portal properties to override.";
        String errorMessage = " portal.properties file not configured.";

        PortalPropertiesPageObject<SWTWorkbenchBot> portalPropertiesPage =
            new PortalPropertiesPageObject<SWTWorkbenchBot>( bot, "", INDEX_PORTAL_PROPERTIES_VALIDATION_MESSAGE );

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_HOOK_CONFIGURATION );

        newHookTypesPage.getPortalProperties().select();
        newHookTypesPage.next();
        sleep( 1000 );

        assertEquals( defaultMessage, portalPropertiesPage.getValidationMessage() );
        assertEquals(
            "/hook-configuration-wizard-hook/docroot/WEB-INF/src/portal.properties",
            portalPropertiesPage.getPortalPropertiesFile().getText() );
        portalPropertiesPage.setPortalPropertiesFile( "" );
        sleep( 500 );

        assertEquals( errorMessage, portalPropertiesPage.getValidationMessage() );
        portalPropertiesPage.getBrowse().click();
        sleep( 500 );

        PortalPropertiesFilePageObject<SWTWorkbenchBot> propertiesPage =
            new PortalPropertiesFilePageObject<SWTWorkbenchBot>( bot );

        propertiesPage.select( "hook-configuration-wizard-hook", "docroot", "WEB-INF", "src" );
        propertiesPage.confirm();

        // Define actions to be executed on portal events
        AddEventActionPageObject<SWTWorkbenchBot> eventActionPage =
            new AddEventActionPageObject<SWTWorkbenchBot>( bot );

        portalPropertiesPage.getEventAdd().click();
        eventActionPage.getNew().click();
        sleep( 500 );

        NewClassPageObject<SWTWorkbenchBot> newClassPage = new NewClassPageObject<SWTWorkbenchBot>( bot );

        newClassPage.setClassName( "test" );
        newClassPage.setJavaPackage( "hook" );
        newClassPage.getCreate().click();
        sleep( 500 );

        PropertySelectionPageObject<SWTWorkbenchBot> propertySelectionPage =
            new PropertySelectionPageObject<SWTWorkbenchBot>( bot );

        eventActionPage.getSelectEvent().click();
        propertySelectionPage.select( "application.startup.events" );
        propertySelectionPage.confirm();
        sleep( 500 );

        portalPropertiesPage.getAddEventAction().setFocus();
        eventActionPage.confirm();
        sleep( 500 );

        portalPropertiesPage.getEventAdd().click();
        eventActionPage.getSelectEvent().click();
        propertySelectionPage.select( "application.startup.events" );
        propertySelectionPage.confirm();
        sleep( 500 );
        portalPropertiesPage.getAddEventAction().setFocus();

        EventSelectionPageObject<SWTWorkbenchBot> eventSelectionPage =
            new EventSelectionPageObject<SWTWorkbenchBot>( bot );
        eventActionPage.getSelectClass().click();
        sleep( 500 );

        eventSelectionPage.setEventAction( "ObjectAction" );
        sleep( 2000 );
        eventSelectionPage.confirm();

        eventActionPage.confirm();
        sleep( 1000 );

        portalPropertiesPage.getDefineActionsOnPortalEvents().click( 1, 1 );
        portalPropertiesPage.getEventRemove().click();

        portalPropertiesPage.getDefineActionsOnPortalEvents().click( 0, 1 );
        portalPropertiesPage.getEventEdit().click();

        eventActionPage.setEventActionclass( "test_hook" );
        eventActionPage.confirm();

        // Specify properties to override
        AddPortalPropertiesOverridePageObject<SWTWorkbenchBot> propertyOverridePage =
            new AddPortalPropertiesOverridePageObject<SWTWorkbenchBot>( bot );

        portalPropertiesPage.getPropertyAdd().click();
        sleep( 500 );
        propertyOverridePage.getSelectProperty().click();
        propertySelectionPage.select( "admin.default.group.names" );

        propertySelectionPage.confirm();
        sleep( 1000 );
        portalPropertiesPage.getAddPropertyOverride().setFocus();
        propertyOverridePage.setValue( "1" );
        propertyOverridePage.confirm();

        portalPropertiesPage.getPropertyAdd().click();
        propertyOverridePage.setProperty( "test" );
        propertyOverridePage.setValue( "2" );
        propertyOverridePage.confirm();
        sleep( 500 );

        portalPropertiesPage.getNewLiferayHookConfiguration().setFocus();
        portalPropertiesPage.getSpecifyPropertiesToOverride().click( 1, 1 );
        portalPropertiesPage.getPropertyEdit().click();

        propertyOverridePage.setProperty( "test_hook" );
        propertyOverridePage.setValue( "3" );
        propertyOverridePage.confirm();

        portalPropertiesPage.getNewLiferayHookConfiguration().setFocus();
        portalPropertiesPage.getSpecifyPropertiesToOverride().click( 1, 1 );

        portalPropertiesPage.getPropertyRemove().click();
        portalPropertiesPage.finish();
        sleep( 1000 );

        // check files exist in the project
        treeBot.doubleClick( "portal.properties", projectHookName + "-hook", "docroot/WEB-INF/src" );
        EditorPageObject editorPage = new EditorPageObject( bot, "portal.properties" );
        assertTrue( editorPage.isActive() );
        TextEditorPageObject textEditorPage = new TextEditorPageObject( bot, "portal.properties" );
        assertContains( "application.startup.events=test_hook", textEditorPage.getText() );
        assertContains( "admin.default.group.names=1", textEditorPage.getText() );
        sleep( 500 );

        treeBot.doubleClick( "test.java", projectHookName + "-hook", "docroot/WEB-INF/src", "hook" );
        EditorPageObject editorPagejava = new EditorPageObject( bot, "test.java" );
        assertTrue( editorPagejava.isActive() );

        TextEditorPageObject textEditorPagejava = new TextEditorPageObject( bot, "test.java" );
        assertContains( "SimpleAction", textEditorPagejava.getText() );
    }

    @Test
    public void hookConfigurationServices()
    {
        String defaultMessage = "Specify which Liferay services to extend.";
        String errorMessage = " Need to specify at least one Service to override.";

        ServicesPageObject<SWTWorkbenchBot> servicesPage =
            new ServicesPageObject<SWTWorkbenchBot>( bot, "", INDEX_SERVICES_MESSAGE );

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_HOOK_CONFIGURATION );

        newHookTypesPage.getServices().select();

        newHookTypesPage.next();

        sleep( 1000 );

        assertEquals( defaultMessage, servicesPage.getValidationMessage() );

        servicesPage.getAdd().click();
        sleep( 500 );

        AddServiceWrapperPageObject<SWTWorkbenchBot> serviceWrapperPage =
            new AddServiceWrapperPageObject<SWTWorkbenchBot>( bot );

        AddServiceWarningPageObject<SWTWorkbenchBot> serviceWarningPage =
            new AddServiceWarningPageObject<SWTWorkbenchBot>( bot );

        serviceWrapperPage.getNew().click();
        servicesPage.getAddService().setFocus();
        serviceWarningPage.getOk().click();
        sleep( 500 );

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.getSelectImplClass( 1 ).click();
        serviceWarningPage.getOk().click();

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.setServiceTypeText( "test" );
        serviceWrapperPage.setImplClassText( "test" );

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.confirm();
        servicesPage.getDefinePortalServices().click( 0, 1 );
        servicesPage.getRemove().click();
        sleep( 500 );
        assertEquals( errorMessage, servicesPage.getValidationMessage() );

        servicesPage.getAdd().click();
        serviceWrapperPage.getSelectServiceType().click();
        sleep( 500 );

        SuperclassSelectionPageObject<SWTWorkbenchBot> superclassPage =
            new SuperclassSelectionPageObject<SWTWorkbenchBot>( bot );

        NewImplClassPageObject<SWTWorkbenchBot> newImplClassPage = new NewImplClassPageObject<SWTWorkbenchBot>( bot );

        superclassPage.setSuperclass( "AccountService" );
        superclassPage.confirm();
        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.getNew().click();
        newImplClassPage.getJavaPackage().setText( "hookservice" );
        newImplClassPage.getCreate().click();

        servicesPage.getAddServiceWrapper().setFocus();
        serviceWrapperPage.confirm();
        servicesPage.getDefinePortalServices().click( 0, 1 );
        servicesPage.getEdit().click();

        serviceWrapperPage.getImplClass().setText( "hookservice.ExtAccountService" );
        serviceWrapperPage.confirm();
        servicesPage.finish();
        sleep( 1000 );

        // check file exist in the project
        treeBot.doubleClick( "ExtAccountService.java", projectHookName + "-hook", "docroot/WEB-INF/src",
            "hookservice" );
        EditorPageObject editorPagejava = new EditorPageObject( bot, "ExtAccountService.java" );
        assertTrue( editorPagejava.isActive() );
        TextEditorPageObject textEditorPagejava = new TextEditorPageObject( bot, "ExtAccountService.java" );
        assertContains( "AccountServiceWrapper", textEditorPagejava.getText() );
    }

    @Before
    public void openWizardCreateProject()
    {
        added = addedProjects();

        if( added )
        {
            return;
        }

        toolbarBot.menuClick( TOOLTIP_CREATE_LIFERAY_PROJECT, TOOLTIP_MENU_ITEM_NEW_LIFERAY_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 =
            new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );

        String projectHookName = "hook-configuration-wizard";

        page1.createSDKProject( projectHookName, MENU_HOOK );

        if( added )
        {
            page1.finish();
        }
        else
        {
            page1.next();

            SetSDKLocationPageObject<SWTWorkbenchBot> page2 = getSetSDKLoactionPage();

            page2.finish();
        }

        sleep( 10000 );
    }

    @After
    public void waitForCreate()
    {
        sleep( 5000 );
    }

}
