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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.liferay.ide.swtbot.portlet.ui.tests.page.CreateJSFPortletWizardPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.LiferayPortletDeploymentDescriptorPO;
import com.liferay.ide.swtbot.portlet.ui.tests.page.PortletDeploymentDescriptorPO;
import com.liferay.ide.swtbot.project.ui.tests.ProjectWizard;
import com.liferay.ide.swtbot.project.ui.tests.page.ChoosePortletFrameworkPO;
import com.liferay.ide.swtbot.project.ui.tests.page.CreateProjectWizardPO;
import com.liferay.ide.swtbot.project.ui.tests.page.SetSDKLocationPO;
import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;

/**
 * @author Li Lu
 */
@Ignore
public class NewJSFPortletWizardTests extends SWTBotBase implements JSFPortletWizard, ProjectWizard
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

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayPluginProject().click();

        CreateProjectWizardPO page1 = new CreateProjectWizardPO( bot );
        page1.createSDKPortletProject( projectName );
        page1.next();

        ChoosePortletFrameworkPO page2 = new ChoosePortletFrameworkPO( bot );
        page2.selectPortletFramework( LABEL_JSF_FRAMEWORK );

        if( page1.finishButton().isEnabled() )
        {
            page1.finish();
        }

        else
        {
            page1.next();
            page1.next();
            SetSDKLocationPO page3 = new SetSDKLocationPO( bot, "" );
            page3.setSdkLocation( getLiferayPluginsSdkDir().toString() );
            page2.finish();
        }
    }

    @AfterClass
    public static void deleteProject()
    {
        eclipse.getPackageExporerView().deleteProjectExcludeNames( new String[] { getLiferayPluginsSdkName() }, true );
    }

    CreateJSFPortletWizardPO page = new CreateJSFPortletWizardPO( bot );
    PortletDeploymentDescriptorPO page2 = new PortletDeploymentDescriptorPO( bot );
    LiferayPortletDeploymentDescriptorPO page3 = new LiferayPortletDeploymentDescriptorPO( bot );

    @After
    public void closeWizard()
    {
        eclipse.closeShell( LABEL_NEW_LIFERAY_JSF_PORTLET );
    }

    @Before
    public void openWizard()
    {
        Assume.assumeTrue( runTest() || runAllTests() );

        eclipse.getCreateLiferayProjectToolbar().getNewLiferayJSFPortlet().click();
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values page 1
        assertEquals( projectName + "-portlet", page.getPortletPluginProject() );
        assertEquals( "/" + projectName + "-portlet" + "/docroot/WEB-INF/src", page.getSourceFolderText() );
        assertEquals( "javax.portlet.faces.GenericFacesPortlet", page.getPortletClassText() );

        // page2
        page.next();

        assertEquals( "new-jsf", page2.getPortletName() );
        assertEquals( "New Jsf", page2.getDisplayName() );
        assertEquals( "New Jsf", page2.getPortletTitle() );
        assertEquals( true, page2.get_viewPortletModeCheckbox().isChecked() );
        assertEquals( false, page2.isEditPortletModeChecked() );
        assertEquals( false, page2.isHelpPortletModeChecked() );
        assertEquals( true, page.isCreateViewFilesChecked() );
        assertEquals( "/WEB-INF/views/new-jsf", page.getViewFolderText() );
        assertEquals( true, page._isStandardJSFSelected() );
        assertEquals( false, page.isICEfacesSelected() );
        assertEquals( false, page._isLiferayFacesAlloySelected() );
        assertEquals( false, page.isPrimeFacesSelected() );
        assertEquals( false, page.isRichFacesSelected() );

        // page3
        page.next();
        assertEquals( "/icon.png", page3.getIconText() );
        assertEquals( false, page3.isAllowMultipleInstancesChecked() );
        assertEquals( "/css/main.css", page3.getCssText() );
        assertEquals( "/js/main.js", page3.getJavaScriptText() );
        assertEquals( "new-jsf-portlet", page3.getCssClassWrapperText() );
        assertEquals( "Sample", page3.getDisplayCategoryCombobox() );
        assertEquals( false, page3.isAddToControlPanelChecked() );
        assertEquals( "My Account Administration", page3.getEntryCategoryCombobox() );
        assertEquals( false, page3.isEntryCategoryEnabled() );
        assertEquals( "1.5", page3.getEntryWeightText() );
        assertEquals( false, page3.isEntryWeightEnabled() );
        assertEquals( false, page3.isCreateEntryClassChecked() );
        assertEquals( false, page3.isCreateEntryClassEnabled() );
        assertEquals( "NewJSFPortletControlPanelEntry", page3.getEntryClassText() );
        assertEquals( false, page3.isEntryClassEnabled() );
    }

    @Test
    public void testCreateResourcesGroup() throws Exception
    {
        page.next();
        page = new CreateJSFPortletWizardPO( bot, INDEX_JSF_VALIDATION_MESSAGE2 );

        assertEquals( true, page.isViewFolderEnabled() );

        page.deSelectCreateViewFiles();
        assertEquals( false, page.isViewFolderEnabled() );

        page.selectCreateViewFiles();
        assertEquals( true, page.isViewFolderEnabled() );

        page2.setPortletName( "jspfolderchanged" );
        assertEquals( "/WEB-INF/views/jspfolderchanged", page.getViewFolderText() );
        page.finish();

        sleep( 2000 );
        TreeItemPO fileTree = new TreeItemPO(
            bot, eclipse.getProjectTree(), projectName + "-portlet", "docroot", "WEB-INF", "views", "jspfolderchanged",
            "view.xhtml" );

        assertTrue( fileTree.isVisible() );

        openWizard();
        page.next();

        page.setViewFolderText( "" );;
        String message = page.getValidationMessage();
        assertEquals( TEXT_JSP_FOLDER_CANOT_BE_EMPTY, message );
        assertEquals( false, page.finishButton().isEnabled() );

        page.setViewFolderText( "/views/" );
        message = page.getValidationMessage();
        assertEquals( TEXT_VIEWS_SHOULD_IN_WEB_INF_FOLDER, message );
        assertEquals( false, page.finishButton().isEnabled() );

        page.deSelectCreateViewFiles();
        assertEquals( true, page.finishButton().isEnabled() );

        page.selectCreateViewFiles();
        page.setViewFolderText( "/WEB-INF/views/jspfolderchanged" );
        message = page.getValidationMessage();
        assertEquals( TEXT_VIEW_FILE_OEVERWRITTEN, message );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        page.setPortletClassText( "" );
        String message = page.getValidationMessage();
        assertEquals( TEXT_MUST_SPECIFY_JSF_PORTLET_CLASS, message );
        assertEquals( false, page.finishButton().isEnabled() );

        page.setPortletClassText( "NewJSFPortlet1" );
        message = page.getValidationMessage();
        assertEquals( TEXT_MUST_BE_VALID_PORTLET_CLASS, message );
        assertEquals( false, page.finishButton().isEnabled() );

        page.setPortletClassText( PORTLET_CLASS_DEFAULT_VALUE );
        assertEquals( true, page.finishButton().isEnabled() );
    }

    @Test
    public void testPortletModesGroup() throws Exception
    {
        page.next();
        assertEquals( false, page2.isViewPortletModeEnabled() );

        page2.speficyPortletModes( true, true );

        page2.setPortletName( "PortletModes" );
        page.finish();

        TreeItemPO folderTree = new TreeItemPO(
            bot, eclipse.getProjectTree(), projectName + "-portlet", "docroot", "WEB-INF", "views", "portletmodes" );
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

        page.selectViewTemplate( LABEL_ICE_FACES );
        page2.setPortletName( "icefacestemplate" );
        page.finish();

        TreeItemPO folderTree =
            new TreeItemPO( bot, eclipse.getProjectTree(), projectName + "-portlet", "docroot", "WEB-INF", "views" );

        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "icefacestemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.selectViewTemplate( LABEL_LIFERAY_FACES_ALLOY );
        page2.setPortletName( "liferayfacesalloytemplate" );
        page.finish();
        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "liferayfacesalloytemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.selectViewTemplate( LABEL_PRIME_FACES );
        page2.setPortletName( "primefacestemplate" );
        page.finish();
        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "primefacestemplate", "view.xhtml" ).isVisible() );

        openWizard();
        page.next();
        page.selectViewTemplate( LABEL_RICH_FACES );
        page2.setPortletName( "richfacestemplate" );
        page.finish();
        sleep( 2000 );
        assertTrue( folderTree.getTreeItem( "richfacestemplate", "view.xhtml" ).isVisible() );
    }

}
