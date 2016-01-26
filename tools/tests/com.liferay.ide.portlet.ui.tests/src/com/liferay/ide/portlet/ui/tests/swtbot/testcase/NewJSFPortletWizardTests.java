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
import static org.junit.Assert.assertTrue;

import com.liferay.ide.portlet.ui.tests.swtbot.JSFPortletWizard;
import com.liferay.ide.portlet.ui.tests.swtbot.pages.CreateJSFPortletWizardPageObject;
import com.liferay.ide.portlet.ui.tests.swtbot.pages.SpecifyPortletDetailsOnePageObject;
import com.liferay.ide.portlet.ui.tests.swtbot.pages.SpecifyPortletDetailsTwoPageObject;
import com.liferay.ide.project.ui.tests.page.ChoosePortletFrameworkPageObject;
import com.liferay.ide.project.ui.tests.page.CreateProjectWizardPageObject;
import com.liferay.ide.project.ui.tests.page.SetSDKLocationPageObject;
import com.liferay.ide.ui.tests.SWTBotBase;
import com.liferay.ide.ui.tests.WizardBase;
import com.liferay.ide.ui.tests.swtbot.page.TreeItemPageObject;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class NewJSFPortletWizardTests extends SWTBotBase implements JSFPortletWizard, WizardBase
{

    static String projectName = "test";

    @BeforeClass
    public static void createJSFPortletProject() throws Exception
    {
        openWizard( MENU_NEW_LIFERAY_PLUGIN_PROJECT );

        CreateProjectWizardPageObject<SWTWorkbenchBot> page1 = new CreateProjectWizardPageObject<SWTWorkbenchBot>( bot );
        page1.getProjectNameText().setText( projectName );
        page1.next();

        ChoosePortletFrameworkPageObject<SWTWorkbenchBot> page2 =
            new ChoosePortletFrameworkPageObject<SWTWorkbenchBot>( bot );
        page2.getJsf2x().click();

        if( page1.finishButton().isEnabled() )
        {
            page1.finish();
        }

        else
        {
            page1.next();
            page1.next();
            SetSDKLocationPageObject<SWTWorkbenchBot> page3 = new SetSDKLocationPageObject<>( bot, "" );
            page3.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @AfterClass
    public static void deleteProject()
    {
        deleteALLWSProjects();
    }

    CreateJSFPortletWizardPageObject<SWTWorkbenchBot> page =
        new CreateJSFPortletWizardPageObject<SWTWorkbenchBot>( bot );
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
        openWizard( MENU_NEW_LIFERAY_JSF_PORTLET );
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values page 1
        assertEquals( "/" + projectName + "-portlet" + "/docroot/WEB-INF/src", page.getSourceFolder().getText() );
        assertEquals( "javax.portlet.faces.GenericFacesPortlet", page.getPortletClass().getText() );

        // page2
        page.next();

        assertEquals( "new-jsf", page2.getName().getText() );
        assertEquals( "New Jsf", page2.getPortletDisplayName().getText() );
        assertEquals( "New Jsf", page2.getPortletTitle().getText() );
        assertEquals( true, page2.getView().isChecked() );
        assertEquals( false, page2.getEdit().isChecked() );
        assertEquals( false, page2.getHelp().isChecked() );
        assertEquals( true, page.getCreateViewFiles().isChecked() );
        assertEquals( "/WEB-INF/views/new-jsf", page.getViewFolder().getText() );
        assertEquals( true, page.getStandardJSF().isSelected() );
        assertEquals( false, page.getICEfaces().isSelected() );
        assertEquals( false, page.getLiferayFacesAlloy().isSelected() );
        assertEquals( false, page.getPrimeFaces().isSelected() );
        assertEquals( false, page.getRichFaces().isSelected() );

        // page3
        page.next();
        assertEquals( "/icon.png", page3.getIcon().getText() );
        assertEquals( false, page3.getAllowMultipleInstances().isChecked() );
        assertEquals( "/css/main.css", page3.getCss().getText() );
        assertEquals( "/js/main.js", page3.getJavaScript().getText() );
        assertEquals( "new-jsf-portlet", page3.getCssClassWrapper().getText() );
        assertEquals( "Sample", page3.getDisplayCategory().getText() );
        assertEquals( false, page3.getAddToControlPanel().isChecked() );
        assertEquals( "My Account Administration", page3.getEntryCategory().getText() );
        assertEquals( "1.5", page3.getEntryWeight().getText() );
        assertEquals( false, page3.getCreateEntryClass().isChecked() );
        assertEquals( "NewJSFPortletControlPanelEntry", page3.getEntryClass().getText() );
    }

    @Test
    public void testCreateResourcesGroup() throws Exception
    {
        page.next();
        page = new CreateJSFPortletWizardPageObject<SWTWorkbenchBot>( bot, INDEX_JSF_VALIDATION_MESSAGE2 );

        assertEquals( true, page.getCreateViewFiles().isEnabled() );
        assertEquals( true, page.getViewFolder().isEnabled() );

        page.getCreateViewFiles().deselect();
        assertEquals( false, page.getViewFolder().isEnabled() );

        page.getCreateViewFiles().select();
        assertEquals( true, page.getViewFolder().isEnabled() );

        page2.getName().setText( "jspfolderchanged" );
        assertEquals( "/WEB-INF/views/jspfolderchanged", page.getViewFolder().getText() );
        page.finish();

        TreeItemPageObject<SWTWorkbenchBot> fileTree =
            new TreeItemPageObject<SWTWorkbenchBot>(
                bot, projectName + "-portlet", "docroot", "WEB-INF", "views", "jspfolderchanged", "view.xhtml" );

        assertTrue( fileTree.isVisible() );

        openWizard();
        page.next();

        page.getViewFolder().setText( "" );
        String message = page.getValidationMessage();
        assertEquals( TEXT_JSP_FOLDER_CANOT_BE_EMPTY, message );
        assertEquals( false, page.finishButton().isEnabled() );

        page.getViewFolder().setText( "/views/" );
        message = page.getValidationMessage();
        assertEquals( TEXT_VIEWS_SHOULD_IN_WEB_INF_FOLDER, message );
        assertEquals( false, page.finishButton().isEnabled() );

        page.getCreateViewFiles().deselect();
        assertEquals( true, page.finishButton().isEnabled() );

        page.getCreateViewFiles().select();
        page.getViewFolder().setText( "/WEB-INF/views/jspfolderchanged" );
        message = page.getValidationMessage();
        assertEquals( TEXT_VIEW_FILE_OEVERWRITTEN, message );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        page.getPortletClass().setText( "" );;
        String message = page.getValidationMessage();
        assertEquals( TEXT_MUST_SPECIFY_JSF_PORTLET_CLASS, message );
        assertEquals( false, page.finishButton().isEnabled() );

        page.getPortletClass().setText( "NewJSFPortlet1" );
        message = page.getValidationMessage();
        assertEquals( TEXT_MUST_BE_VALID_PORTLET_CLASS, message );
        assertEquals( false, page.finishButton().isEnabled() );

        page.getPortletClass().setText( PORTLET_CLASS_DEFAULT_VALUE );
        assertEquals( true, page.finishButton().isEnabled() );
    }

    @Test
    public void testPortletModesGroup() throws Exception
    {
        page.next();
        assertEquals( false, page2.getView().isEnabled() );
        assertEquals( true, page2.getEdit().isEnabled() );
        assertEquals( true, page2.getHelp().isEnabled() );

        page2.getEdit().select();
        page2.getHelp().select();

        page2.getName().setText( "PortletModes" );
        page.finish();

        TreeItemPageObject<SWTWorkbenchBot> folderTree =
            new TreeItemPageObject<SWTWorkbenchBot>(
                bot, projectName + "-portlet", "docroot", "WEB-INF", "views", "portletmodes" );
        assertTrue( folderTree.getTreeItem( "edit.xhtml" ).isVisible() );
        assertTrue( folderTree.getTreeItem( "edit.xhtml" ).isVisible() );
    }

    @Test
    public void testViewTemplateGroup() throws Exception
    {
        page.next();
        assertEquals( true, page.getStandardJSF().isEnabled() );
        assertEquals( true, page.getICEfaces().isEnabled() );
        assertEquals( true, page.getLiferayFacesAlloy().isEnabled() );
        assertEquals( true, page.getPrimeFaces().isEnabled() );
        assertEquals( true, page.getRichFaces().isEnabled() );

        page.getICEfaces().click();
        page2.getName().setText( "icefacestemplate" );
        page.finish();
        sleep( 1000 );

        TreeItemPageObject<SWTWorkbenchBot> folderTree =
            new TreeItemPageObject<SWTWorkbenchBot>( bot, projectName + "-portlet", "docroot", "WEB-INF", "views" );

        assertTrue( folderTree.getTreeItem( "icefacestemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.getLiferayFacesAlloy().click();
        page2.getName().setText( "liferayfacesalloytemplate" );
        page.finish();
        sleep( 500 );
        assertTrue( folderTree.getTreeItem( "liferayfacesalloytemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.getPrimeFaces().click();
        page2.getName().setText( "primefacestemplate" );
        page.finish();
        sleep( 500 );
        assertTrue( folderTree.getTreeItem( "primefacestemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.getRichFaces().click();
        page2.getName().setText( "richfacestemplate" );
        page.finish();
        sleep( 500 );
        assertTrue( folderTree.getTreeItem( "richfacestemplate", "view.xhtml" ).isVisible() );
    }

}
