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

package com.liferay.ide.portlet.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.portlet.core.jsf.INewJSFPortletClassDataModelProperties;
import com.liferay.ide.portlet.ui.jsf.NewJSFPortletWizard;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.ui.IvyUtil;

/**
 * @author Li Lu
 */
@SuppressWarnings( "restriction" )
public class NewJSFPortletWizardTests extends PortletUITestBase implements INewJSFPortletClassDataModelProperties
{

    private IProject project;
    IDataModel dataModel;
    NewJSFPortletWizard wizard;

    @Before
    public void createPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        project = createProject( "jsf-portlet-project-test", PluginType.portlet, "jsf" );

        boolean ivyNature = project.hasNature( "org.apache.ivyde.eclipse.ivynature" );
        if( ivyNature == false )
        {
            IvyUtil.configureIvyProject( project, new NullProgressMonitor() );
        }
        
        wizard = new NewJSFPortletWizard();
        dataModel = wizard.getDataModel();
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( TARGET_PROJECT, project.getName() );
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values page 1
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        assertEquals( "\\" + project.getName() + "\\docroot\\WEB-INF\\src", dataModel.getDefaultProperty( SOURCE_FOLDER ) );
        assertEquals( "javax.portlet.faces.GenericFacesPortlet", dataModel.getDefaultProperty( JSF_PORTLET_CLASS ) );

        // page2
        assertEquals( "new-jsf", dataModel.getDefaultProperty( PORTLET_NAME ) );
        assertEquals( "New Jsf", dataModel.getDefaultProperty( DISPLAY_NAME ) );
        assertEquals( "New Jsf", dataModel.getDefaultProperty( TITLE ) );
        assertEquals( true, dataModel.getDefaultProperty( VIEW_MODE ) );
        assertNotEquals( false, dataModel.getDefaultProperty( EDIT_MODE ) );
        assertNotEquals( false, dataModel.getDefaultProperty( HELP_MODE ) );
        assertEquals( true, dataModel.getDefaultProperty( CREATE_JSPS ) );
        assertEquals( "/WEB-INF/views/new-jsf", dataModel.getDefaultProperty( CREATE_JSPS_FOLDER ) );
        assertEquals( true, dataModel.getDefaultProperty( STANDARD_JSF ) );
        assertEquals( false, dataModel.getDefaultProperty( ICE_FACES ) );
        assertEquals( false, dataModel.getDefaultProperty( LIFERAY_FACES_ALLOY ) );
        assertEquals( false, dataModel.getDefaultProperty( PRIME_FACES ) );
        assertEquals( false, dataModel.getDefaultProperty( RICH_FACES ) );

        // page3
        assertEquals( "/icon.png", dataModel.getDefaultProperty( ICON_FILE ) );
        assertEquals( false, dataModel.getDefaultProperty( ALLOW_MULTIPLE ) );
        assertEquals( "/css/main.css", dataModel.getDefaultProperty( CSS_FILE ) );
        assertEquals( "/js/main.js", dataModel.getDefaultProperty( JAVASCRIPT_FILE ) );
        assertEquals( "new-jsf-portlet", dataModel.getDefaultProperty( CSS_CLASS_WRAPPER ) );
        assertEquals( "category.sample", dataModel.getDefaultProperty( CATEGORY ) );
        assertNotEquals( true, dataModel.getDefaultProperty( ADD_TO_CONTROL_PANEL ) );
        assertEquals( "category.my", dataModel.getDefaultProperty( ENTRY_CATEGORY ) );
        assertEquals( "1.5", dataModel.getDefaultProperty( ENTRY_WEIGHT ) );
        assertNotEquals( true, dataModel.getDefaultProperty( CREATE_ENTRY_CLASS ) );
        assertEquals( "NewJSFPortletControlPanelEntry", dataModel.getDefaultProperty( ENTRY_CLASS_NAME ) );
    }

    @Test
    public void testCreateJSPGroup() throws Exception
    {
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_JSPS ) );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_JSPS_FOLDER ) );

        dataModel.setProperty( CREATE_JSPS, false );
        assertEquals( false, dataModel.isPropertyEnabled( CREATE_JSPS_FOLDER ) );

        dataModel.setProperty( CREATE_JSPS, true );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_JSPS_FOLDER ) );

        dataModel.setProperty( PORTLET_NAME, "jspfolderchanged" );
        assertEquals( "/WEB-INF/views/jspfolderchanged", dataModel.getProperty( CREATE_JSPS_FOLDER ) );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertTrue( checkFileExist( project, "view.xhtml", "/WEB-INF/views/jspfolderchanged" ) );

        dataModel.setProperty( CREATE_JSPS_FOLDER, "" );
        String message = dataModel.validateProperty( CREATE_JSPS_FOLDER ).getMessage();
        assertEquals( "JSP folder cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );
        
        dataModel.setProperty( CREATE_JSPS_FOLDER, "/views/" );
        message = dataModel.validateProperty( CREATE_JSPS_FOLDER ).getMessage();
        assertEquals( "The views should be generated in the WEB-INF folder.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( PORTLET_NAME, "new-jsf" );
        dataModel.setProperty( CREATE_JSPS, false );
        message = dataModel.validate().getMessage();
        assertEquals( "OK", message );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( CREATE_JSPS, true );
        dataModel.setProperty( CREATE_JSPS_FOLDER, "/WEB-INF/views/jspfolderchanged" );
        message = dataModel.validateProperty( CREATE_JSPS_FOLDER ).getMessage();
        assertEquals( "View file already exists and will be overwritten.", message );
    }

    @Test
    public void testLiferayDisplayGroup() throws Exception
    {
        assertEquals( true, dataModel.isPropertyEnabled( CATEGORY ) );
        DataModelPropertyDescriptor[] categories = dataModel.getValidPropertyDescriptors( CATEGORY );
        assertNotNull( categories );
        assertEquals( true, containPropertyDescriptor( categories, "Sample" ) );
        assertEquals( true, containPropertyDescriptor( categories, "Marketplace" ) );
        assertEquals( true, containPropertyDescriptor( categories, "Wiki" ) );

        assertEquals( true, dataModel.isPropertyEnabled( ADD_TO_CONTROL_PANEL ) );

        // assertEquals( false, dataModel.isPropertyEnabled( ENTRY_CATEGORY ) );
        DataModelPropertyDescriptor[] entryCategories = dataModel.getValidPropertyDescriptors( ENTRY_CATEGORY );
        assertNotNull( entryCategories );
        assertEquals( true, containPropertyDescriptor( entryCategories, "My Account Administration" ) );
        assertEquals( true, containPropertyDescriptor( entryCategories, "Control Panel - Apps" ) );
        assertEquals( true, containPropertyDescriptor( entryCategories, "Control Panel - Configuration" ) );
        assertEquals( true, containPropertyDescriptor( entryCategories, "Site Administration - Configuration" ) );
        assertEquals( true, containPropertyDescriptor( entryCategories, "Site Administration - Content" ) );
        assertEquals( true, containPropertyDescriptor( entryCategories, "Site Administration - Pages" ) );
        assertEquals( true, containPropertyDescriptor( entryCategories, "Site Administration - Users" ) );
        assertEquals( true, containPropertyDescriptor( entryCategories, "Control Panel - Sites" ) );
        assertEquals( true, containPropertyDescriptor( entryCategories, "Control Panel - Users" ) );

        dataModel.setProperty( ADD_TO_CONTROL_PANEL, true );
        assertEquals( true, dataModel.isPropertyEnabled( ENTRY_CATEGORY ) );
        assertEquals( true, dataModel.isPropertyEnabled( ENTRY_WEIGHT ) );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_ENTRY_CLASS ) );

        dataModel.setProperty( CREATE_ENTRY_CLASS, true );
        assertEquals( true, dataModel.isPropertyEnabled( ENTRY_CLASS_NAME ) );

        dataModel.setProperty( ENTRY_WEIGHT, "" );
        String message = dataModel.validate().getMessage();
        assertEquals( "Must specify a valid double for entry weight.", message );
        dataModel.setProperty( ENTRY_WEIGHT, "1.5" );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( ENTRY_CLASS_NAME, "" );
        message = dataModel.validateProperty( ENTRY_CLASS_NAME ).getMessage();
        assertEquals( "The class name cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );
        dataModel.setProperty( ENTRY_CLASS_NAME, "test" );
        message = dataModel.validateProperty( ENTRY_CLASS_NAME ).getMessage();
        assertEquals( "Warning: By convention, Java type names usually start with an uppercase letter", message );
        assertEquals( true, wizard.canFinish() );
        dataModel.setProperty( ENTRY_CLASS_NAME, "test test" );
        message = dataModel.validateProperty( ENTRY_CLASS_NAME ).getMessage();
        assertEquals( "Invalid Java class name: The type name 'test test' is not a valid identifier", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( ENTRY_CLASS_NAME, "MyNewJSFPortletControlPanelEntry" );
        dataModel.setProperty( PORTLET_NAME, "LiferayDisplay" );

        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "MyNewJSFPortletControlPanelEntry.java", "WEB-INF/src/com/test" ) );

        dataModel.setProperty( ENTRY_CLASS_NAME, "MyNewJSFPortletControlPanelEntry" );
        message = dataModel.validateProperty( ENTRY_CLASS_NAME ).getMessage();
        assertEquals( "Type 'com.test.MyNewJSFPortletControlPanelEntry' already exists.", message );
        assertEquals( false, wizard.canFinish() );
    }

    @Test
    public void testLiferayPortletInfoGroup() throws Exception
    {
        assertEquals( true, dataModel.isPropertyEnabled( ICON_FILE ) );
        dataModel.setProperty( ICON_FILE, "" );
        assertEquals( true, wizard.canFinish() );

        assertEquals( true, dataModel.isPropertyEnabled( ALLOW_MULTIPLE ) );
        dataModel.setProperty( ALLOW_MULTIPLE, true );

        assertEquals( true, dataModel.isPropertyEnabled( ALLOW_MULTIPLE ) );
        dataModel.setProperty( ALLOW_MULTIPLE, true );

        assertEquals( true, dataModel.isPropertyEnabled( CSS_FILE ) );
        dataModel.setProperty( CSS_FILE, "" );
        assertEquals( true, wizard.canFinish() );

        assertEquals( true, dataModel.isPropertyEnabled( JAVASCRIPT_FILE ) );
        dataModel.setProperty( JAVASCRIPT_FILE, "" );
        assertEquals( true, wizard.canFinish() );

        assertEquals( true, dataModel.isPropertyEnabled( CSS_CLASS_WRAPPER ) );
        dataModel.setProperty( CSS_CLASS_WRAPPER, "" );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( PORTLET_NAME, "LiferayPortletInfo" );
        dataModel.setProperty( CSS_FILE, "/css/new_main.css" );
        dataModel.setProperty( JAVASCRIPT_FILE, "/js/new_main.js" );

        dataModel.setProperty( PROJECT, project );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "new_main.css", "css" ) );
        assertEquals( true, checkFileExist( project, "new_main.js", "js" ) );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        dataModel.setProperty( JSF_PORTLET_CLASS, "" );
        String message = dataModel.validateProperty( JSF_PORTLET_CLASS ).getMessage();
        assertEquals( "Must specify a JSF portlet class.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( JSF_PORTLET_CLASS, "NewJSFPortlet1" );
        message = dataModel.validateProperty( JSF_PORTLET_CLASS ).getMessage();
        assertEquals( "JSF portlet class must be a valid portlet class.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( JSF_PORTLET_CLASS, "javax.portlet.faces.GenericFacesPortlet" );
        assertEquals( true, wizard.canFinish() );
    }

    @Test
    public void testPortletInfoGroup() throws Exception
    {        
        dataModel.setProperty( PORTLET_NAME, "my-new" );
        assertEquals( "My New", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "My New", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "ABC-MY" );
        assertEquals( "ABC MY", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "ABC MY", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "aa12-m334y-bb-ccc" );
        assertEquals( "Aa12 M334y Bb Ccc", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Aa12 M334y Bb Ccc", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "Aa 12-M334yB_bCC2C" );
        assertEquals( "Aa 12 M334yB BCC2C", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Aa 12 M334yB BCC2C", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "my-n ew-POrtLet" );
        assertEquals( "My N Ew POrtLet", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "My N Ew POrtLet", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "my-^abcd-tao%*tao ---liferay_PORTlet*^-_Display-_ PORTLET" );
        assertEquals( "My ^abcd Tao%*tao Liferay PORTlet*^ Display PORTLET", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "My ^abcd Tao%*tao Liferay PORTlet*^ Display PORTLET", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "ABC^&&&d----DD()--[]AA___{portlet}-my new" );
        assertEquals( "ABC^&&&d DD() []AA {portlet} My New", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "ABC^&&&d DD() []AA {portlet} My New", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "" );
        assertEquals( "", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "", dataModel.getProperty( TITLE ) );
        String message = dataModel.validateProperty( PORTLET_NAME ).getMessage();
        assertEquals( "Portlet name is empty.", message );

        dataModel.setProperty( PORTLET_NAME, " \" \" " );
        assertEquals( "\" \"", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "\" \"", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " \" " );
        assertEquals( "\"", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "\"", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "\"\"       \" " );
        assertEquals( "\"\" \"", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "\"\" \"", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " Test1 Test2 \"Test1\" " );
        assertEquals( "Test1 Test2 \"Test1\"", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Test1 Test2 \"Test1\"", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " Test1 Test2\" \"Te\"st1 " );
        assertEquals( "Test1 Test2\" \"Te\"st1", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Test1 Test2\" \"Te\"st1", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " Tes \t3 Te\" \" Test2\" \"Te\"st1 " );
        assertEquals( "Tes 3 Te\" \" Test2\" \"Te\"st1", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Tes 3 Te\" \" Test2\" \"Te\"st1", dataModel.getProperty( TITLE ) );
        
        dataModel.setProperty( PORTLET_NAME, "portletexist" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        
        dataModel=wizard.getDataModel();
        dataModel.setProperty( PORTLET_NAME, "portletexist" );
        message = dataModel.validateProperty( PORTLET_NAME ).getMessage();
        assertEquals( "Portlet name already exists.", message );
    }

    @Test
    public void testPortletModesGroup() throws Exception
    {
        assertEquals( false, dataModel.isPropertyEnabled( VIEW_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( EDIT_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( HELP_MODE ) );

        dataModel.setProperty( EDIT_MODE, true );
        dataModel.setProperty( HELP_MODE, true );

        dataModel.setProperty( PORTLET_NAME, "PortletModes" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        assertTrue( checkFileExist( project, "view.xhtml", "WEB-INF/views/portletmodes" ) );
        assertTrue( checkFileExist( project, "edit.xhtml", "WEB-INF/views/portletmodes" ) );
        assertTrue( checkFileExist( project, "help.xhtml", "WEB-INF/views/portletmodes" ) );
    }

    @Test
    public void testSourceFolder() throws Exception
    {
        String contentName = SOURCE_FOLDER;
        Object contentDefaultValue = dataModel.getProperty( contentName );

        dataModel.setProperty( contentName, "" );
        String message = dataModel.validateProperty( contentName ).getMessage();
        assertEquals( "The source folder cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( contentName, "src" );
        assertEquals( false, wizard.canFinish() );
        message = dataModel.validateProperty( contentName ).getMessage();

        assertEquals( "Source folder path must be a workspace relative absolute path.", message );

        dataModel.setProperty( contentName, contentDefaultValue );
        assertEquals( true, wizard.canFinish() );
    }

    @Test
    public void testTargetProject() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, null );
        String message = wizard.getDataModel().validateProperty( PROJECT_NAME ).getMessage();
        assertEquals( "Enter a project name.", message );
    }

    @Test
    public void testViewTemplateGroup() throws Exception
    {
        assertEquals( true, dataModel.isProperty( STANDARD_JSF ) );
        assertEquals( true, dataModel.isProperty( ICE_FACES ) );
        assertEquals( true, dataModel.isProperty( LIFERAY_FACES_ALLOY ) );
        assertEquals( true, dataModel.isProperty( PRIME_FACES ) );
        assertEquals( true, dataModel.isProperty( RICH_FACES ) );

        dataModel.setProperty( ICE_FACES, true );
        dataModel.setProperty( PORTLET_NAME, "icefacestemplate" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "view.xhtml", "WEB-INF/views/icefacestemplate" ) );

        dataModel.setProperty( LIFERAY_FACES_ALLOY, true );
        dataModel.setProperty( PORTLET_NAME, "liferayfacesalloytemplate" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "view.xhtml", "WEB-INF/views/liferayfacesalloytemplate" ) );

        dataModel.setProperty( PRIME_FACES, true );
        dataModel.setProperty( PORTLET_NAME, "primefacestemplate" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "view.xhtml", "WEB-INF/views/primefacestemplate" ) );

        dataModel.setProperty( RICH_FACES, true );
        dataModel.setProperty( PORTLET_NAME, "richfacestemplate" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "view.xhtml", "WEB-INF/views/richfacestemplate" ) );
    }
}
