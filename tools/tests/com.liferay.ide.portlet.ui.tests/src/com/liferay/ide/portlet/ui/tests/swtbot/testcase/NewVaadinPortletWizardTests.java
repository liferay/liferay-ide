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

package com.liferay.ide.portlet.ui.tests.swtbot.testcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.portlet.ui.tests.swtbot.PortletWizard;
import com.liferay.ide.portlet.ui.tests.swtbot.VaadinPortletWizard;
import com.liferay.ide.portlet.ui.tests.swtbot.pages.CreateVaadinPortletWizardPageObject;
import com.liferay.ide.portlet.ui.tests.swtbot.pages.SpecifyPortletDetailsOnePageObject;
import com.liferay.ide.portlet.ui.tests.swtbot.pages.SpecifyPortletDetailsTwoPageObject;
import com.liferay.ide.project.ui.tests.page.ChoosePortletFrameworkPageObject;
import com.liferay.ide.project.ui.tests.page.CreateProjectWizardPageObject;
import com.liferay.ide.project.ui.tests.page.SetSDKLocationPageObject;
import com.liferay.ide.ui.tests.SWTBotBase;
import com.liferay.ide.ui.tests.WizardBase;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class NewVaadinPortletWizardTests extends SWTBotBase implements VaadinPortletWizard, WizardBase, PortletWizard
{

    static String projectName = "vaadin-test";

    @BeforeClass
    public static void createJSFPortletProject() throws Exception
    {

        openWizard( MENU_NEW_LIFERAY_PLUGIN_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );
        page1.getProjectNameText().setText( projectName );
        page1.next();

        ChoosePortletFrameworkPageObject<SWTWorkbenchBot> page2 =
            new ChoosePortletFrameworkPageObject<SWTWorkbenchBot>( bot );

        page2.getVaadin().click();

        if( page1.finishButton().isEnabled() )
        {
            page1.finish();
        }

        else
        {
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page3 = new SetSDKLocationPageObject<>( bot, "" );
            page3.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page3.finish();
        }
    }

    @AfterClass
    public static void deleteProject()
    {
        deleteALLWSProjects();
    }

    CreateVaadinPortletWizardPageObject<SWTWorkbenchBot> page =
        new CreateVaadinPortletWizardPageObject<SWTWorkbenchBot>( bot, INDEX__VAADIN_VALIDATION_MESSAGE1 );

    SpecifyPortletDetailsOnePageObject<SWTWorkbenchBot> page2 =
        new SpecifyPortletDetailsOnePageObject<SWTWorkbenchBot>( bot );

    SpecifyPortletDetailsTwoPageObject<SWTWorkbenchBot> page3 =
        new SpecifyPortletDetailsTwoPageObject<SWTWorkbenchBot>( bot );

    @After
    public void closeWizard()
    {
        try
        {
            page.cancel();
        }
        catch( Exception e )
        {
        }
    }

    @Before
    public void openWizard()
    {
        openWizard( MENU_NEW_LIFERAY_VAADIN_PORTLET );
    }

    @Test
    public void testApplicationClass() throws Exception
    {
        page.getApplicationClass().setText( "" );

        String message = page.getValidationMessage();
        assertEquals( TEXT_CLASS_NAME_CANNOT_BE_EMPTY, message );
        assertEquals( false, page.finishButton().isEnabled() );
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values page 1
        assertTrue( page.getPortletPluginProject().isEnabled() );
        assertTrue( page.getSourceFolder().isEnabled() );
        assertTrue( page.getApplicationClass().isEnabled() );
        assertTrue( page.getJavaPackage().isEnabled() );
        assertTrue( page.getSuperClass().isEnabled() );
        assertTrue( page.getPortletClass().isEnabled() );

        assertEquals( projectName + "-portlet", page.getPortletPluginProject().getText() );
        assertTrue( page.getSourceFolder().getText().contains( "docroot/WEB-INF/src" ) );
        assertEquals( "NewVaadinPortletApplication", page.getApplicationClass().getText() );
        assertEquals( "com.test", page.getJavaPackage().getText() );
        assertEquals( "com.vaadin.Application", page.getSuperClass().getText() );
        assertEquals( "com.vaadin.terminal.gwt.server.ApplicationPortlet2", page.getVaadinPortletClass().getText() );
        // page2
        page.next();
        assertTrue( page2.getName().isEnabled() );
        assertTrue( page2.getPortletDisplayName().isEnabled() );
        assertTrue( page2.getPortletTitle().isEnabled() );
        assertTrue( page2.getCreateResourceBundleFile().isEnabled() );
        assertFalse( page2.getResourceBundleFilePath().isEnabled() );

        assertEquals( "newvaadinportlet", page2.getName().getText() );
        assertEquals( "NewVaadinPortlet", page2.getPortletDisplayName().getText() );
        assertEquals( "NewVaadinPortlet", page2.getPortletTitle().getText() );
        assertEquals( false, page2.getCreateResourceBundleFile().isChecked() );
        assertEquals( "content/Language.properties", page2.getResourceBundleFilePath().getText() );
        // page3
        page.next();
        assertTrue( page3.getIcon().isEnabled() );
        assertTrue( page3.getAllowMultipleInstances().isEnabled() );
        assertTrue( page3.getCss().isEnabled() );
        assertTrue( page3.getJavaScript().isEnabled() );
        assertTrue( page3.getCssClassWrapper().isEnabled() );
        assertTrue( page3.getDisplayCategory().isEnabled() );
        assertTrue( page3.getAddToControlPanel().isEnabled() );
        assertFalse( page3.getEntryWeight().isEnabled() );
        assertFalse( page3.getEntryCategory().isEnabled() );
        assertFalse( page3.getEntryClass().isEnabled() );
        assertFalse( page3.getCreateEntryClass().isEnabled() );

        assertEquals( "/icon.png", page3.getIcon().getText() );
        assertEquals( false, page3.getAllowMultipleInstances().isChecked() );
        assertEquals( "/css/main.css", page3.getCss().getText() );
        assertEquals( "/js/main.js", page3.getJavaScript().getText() );
        assertEquals( "newvaadinportlet-portlet", page3.getCssClassWrapper().getText() );
        assertEquals( "Sample", page3.getDisplayCategory().getText() );
        assertEquals( true, page3.getAddToControlPanel().isEnabled() );
        assertEquals( "My Account Administration", page3.getEntryCategory().getText() );
        assertEquals( "1.5", page3.getEntryWeight().getText() );
        assertEquals( false, page3.getCreateEntryClass().isEnabled() );
        assertEquals( "NewVaadinPortletApplicationControlPanelEntry", page3.getEntryClass().getText() );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        page.getVaadinPortletClass().setText( "" );

        String message = page.getValidationMessage();
        assertEquals( TEXT_MUST_SPECIFY_VAADIN_PORTLET_CLASS, message );
        assertEquals( false, page.finishButton().isEnabled() );
    }

}
