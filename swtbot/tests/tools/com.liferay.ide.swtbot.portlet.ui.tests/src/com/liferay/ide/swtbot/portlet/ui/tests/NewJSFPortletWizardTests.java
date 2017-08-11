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
import static org.junit.Assert.assertTrue;

import com.liferay.ide.swtbot.liferay.ui.SwtbotBase;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.ChoosePortletFrameworkWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.CreateJSFPortletWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.LiferayPortletDeploymentDescriptorWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.PortletDeploymentDescriptorWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.NewSdkProjectWizard;
import com.liferay.ide.swtbot.liferay.ui.page.wizard.project.SetSDKLocationWizard;
import com.liferay.ide.swtbot.ui.page.TreeItem;
import com.liferay.ide.swtbot.ui.util.StringPool;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Li Lu
 */
@Ignore
public class NewJSFPortletWizardTests extends SwtbotBase
{

    static String projectName = "test";

    static String fullClassname = new SecurityManager()
    {

        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }.getClassName();

    static String currentClassname = fullClassname.substring( fullClassname.lastIndexOf( '.' ) ).substring( 1 );

    @BeforeClass
    public static void createJSFPortletProject() throws Exception
    {
        Assume.assumeTrue( currentClassname.equals( runTest ) || runAllTests() );

        unzipPluginsSDK();
        unzipServer();

        ide.getCreateLiferayProjectToolbar().getNewLiferayPlugin().click();

        NewSdkProjectWizard page1 = new NewSdkProjectWizard( bot );
        page1.createSDKPortletProject( projectName );
        page1.next();

        ChoosePortletFrameworkWizard page2 = new ChoosePortletFrameworkWizard( bot );
        page2.selectPortletFramework( JSF_2_X );

        if( page1.finishBtn().isEnabled() )
        {
            page1.finish();
        }

        else
        {
            page1.next();
            page1.next();
            SetSDKLocationWizard page3 = new SetSDKLocationWizard( bot );
            page3.getSdkLocation().setText( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @AfterClass
    public static void deleteProject()
    {
        viewAction.deleteProjectsExcludeNames( getLiferayPluginsSdkName() );
    }

    CreateJSFPortletWizard page = new CreateJSFPortletWizard( bot );
    PortletDeploymentDescriptorWizard page2 = new PortletDeploymentDescriptorWizard( bot );
    LiferayPortletDeploymentDescriptorWizard page3 = new LiferayPortletDeploymentDescriptorWizard( bot );

    @After
    public void closeWizard()
    {
        ide.closeShell( NEW_LIFERAY_JSF_PORTLET );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        ide.getCreateLiferayProjectToolbar().getNewLiferayJSFPortlet().click();
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values page 1
        assertEquals( projectName + "-portlet", page.getPortletPluginProjects().getText() );
        assertEquals( "/" + projectName + "-portlet" + "/docroot/WEB-INF/src", page.getSourceFolder().getText() );
        assertEquals( "javax.portlet.faces.GenericFacesPortlet", page.getPortletClass().getText() );

        // page2
        page.next();

        assertEquals( "new-jsf", page2.getPortletName() );
        assertEquals( "New Jsf", page2.getDisplayName() );
        assertEquals( "New Jsf", page2.getPortletTitle() );
        assertEquals( true, page2.getView().isChecked() );
        assertEquals( false, page2.getEdit().isChecked() );
        assertEquals( false, page2.getHelp().isChecked() );
        assertEquals( true, page.isCreateViewFilesChecked() );
        assertEquals( "/WEB-INF/views/new-jsf", page.getViewFolderText() );
        assertEquals( true, page._isStandardJSFSelected() );
        assertEquals( false, page.isICEfacesSelected() );
        assertEquals( false, page._isLiferayFacesAlloySelected() );
        assertEquals( false, page.isPrimeFacesSelected() );
        assertEquals( false, page.isRichFacesSelected() );

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
        assertEquals( false, page3.getEntryCategory().isEnabled() );
        assertEquals( "1.5", page3.getEntryWeight().getText() );
        assertEquals( false, page3.getEntryCategory().isEnabled() );
        assertEquals( false, page3.getCreateEntryClass().isChecked() );
        assertEquals( "NewJSFPortletControlPanelEntry", page3.getEntryClass().getText() );
    }

    @Test
    public void testCreateResourcesGroup() throws Exception
    {
        page.next();
        page = new CreateJSFPortletWizard( bot );

        assertEquals( true, page.isViewFolderEnabled() );

        page.deSelectCreateViewFiles();
        assertEquals( false, page.isViewFolderEnabled() );

        page.selectCreateViewFiles();
        assertEquals( true, page.isViewFolderEnabled() );

        page2.getPortletName().setText( "jspfolderchanged" );
        assertEquals( "/WEB-INF/views/jspfolderchanged", page.getViewFolderText() );
        page.finish();

        sleep( 2000 );
        TreeItem fileTree = new TreeItem(
            bot, ide.getProjectTree(), projectName + "-portlet", "docroot", "WEB-INF", "views", "jspfolderchanged",
            "view.xhtml" );

        assertTrue( fileTree.isVisible() );

        openWizard();
        page.next();

        page.setViewFolderText( StringPool.BLANK );;
        String message = page.getValidationMsg();
        assertEquals( JSP_FOLDER_CANOT_BE_EMPTY, message );
        assertEquals( false, page.finishBtn().isEnabled() );

        page.setViewFolderText( "/views/" );
        message = page.getValidationMsg();
        assertEquals( VIEWS_SHOULD_IN_WEB_INF_FOLDER, message );
        assertEquals( false, page.finishBtn().isEnabled() );

        page.deSelectCreateViewFiles();
        assertEquals( true, page.finishBtn().isEnabled() );

        page.selectCreateViewFiles();
        page.setViewFolderText( "/WEB-INF/views/jspfolderchanged" );

        assertEquals( VIEW_FILE_OEVERWRITTEN, page.getValidationMsg() );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        page.getPortletClass().setText( StringPool.BLANK );
        String message = page.getValidationMsg();
        assertEquals( MUST_SPECIFY_JSF_PORTLET_CLASS, message );
        assertEquals( false, page.finishBtn().isEnabled() );

        page.getPortletClass().setText( "NewJSFPortlet1" );
        message = page.getValidationMsg();
        assertEquals( JSF_PORTLET_CLASS_MUST_BE_A_VALID_PORTLET_CLASS, message );
        assertEquals( false, page.finishBtn().isEnabled() );

        page.getPortletClass().setText( JAVAX_PORTLET_FACES_GENERICFACESPORTLET );
        assertEquals( true, page.finishBtn().isEnabled() );
    }

    @Test
    public void testPortletModesGroup() throws Exception
    {
        page.next();
        assertEquals( false, page2.getView().isEnabled() );

        page2.speficyPortletModes( true, true );

        page2.getPortletName().setText( "PortletModes" );
        page.finish();

        TreeItem folderTree = new TreeItem(
            bot, ide.getProjectTree(), projectName + "-portlet", "docroot", "WEB-INF", "views", "portletmodes" );
        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "edit.xhtml" ).isVisible() );
        assertTrue( folderTree.getTreeItem( "edit.xhtml" ).isVisible() );
    }

    @Test
    public void testViewTemplateGroup() throws Exception
    {
        page.next();

        assertEquals( true, page.isStandardJSFEnabled() );
        assertEquals( true, page.isICEfacesEnabled() );
        assertEquals( true, page.isliferayFacesAlloyEnbled() );
        assertEquals( true, page.isRichFacesEnbled() );
        assertEquals( true, page.isPrimeFacesEnbled() );

        page.selectViewTemplate( ICEFACES );
        page2.getPortletName().setText( "icefacestemplate" );
        page.finish();

        TreeItem folderTree =
            new TreeItem( bot, ide.getProjectTree(), projectName + "-portlet", "docroot", "WEB-INF", "views" );

        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "icefacestemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.selectViewTemplate( LIFERAY_FACES_ALLOY );
        page2.getPortletName().setText( "liferayfacesalloytemplate" );
        page.finish();
        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "liferayfacesalloytemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.selectViewTemplate( PRIMEFACES );
        page2.getPortletName().setText( "primefacestemplate" );
        page.finish();
        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "primefacestemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.selectViewTemplate( RICHFACES );
        page2.getPortletName().setText( "richfacestemplate" );
        page.finish();
        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "richfacestemplate", "view.xhtml" ).isVisible() );
    }

}
