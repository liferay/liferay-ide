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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.vaadin.core.operation.INewVaadinPortletClassDataModelProperties;
import com.liferay.ide.portlet.vaadin.ui.wizard.NewVaadinPortletWizard;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Li Lu
 */
@SuppressWarnings( "restriction" )
public class NewVaadinPortletWizardTests extends PortletUITestBase
    implements INewVaadinPortletClassDataModelProperties, INewPortletClassDataModelProperties
{

    private IProject project;
    private final NewVaadinPortletWizard wizard = new NewVaadinPortletWizard();
    private final IDataModel dataModel = wizard.getDataModel();

    @Before
    public void createPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        project = createProject( "vaadin-portlet-project-test", PluginType.portlet, "vaadin" );
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        // check default values page 1
        assertTrue( dataModel.isPropertyEnabled( PROJECT_NAME ) );
        assertTrue( dataModel.isPropertyEnabled( SOURCE_FOLDER ) );
        assertTrue( dataModel.isPropertyEnabled( CLASS_NAME ) );
        assertTrue( dataModel.isPropertyEnabled( JAVA_PACKAGE ) );
        assertTrue( dataModel.isPropertyEnabled( SUPERCLASS ) );
        assertTrue( dataModel.isPropertyEnabled( VAADIN_PORTLET_CLASS ) );

        dataModel.setProperty( PROJECT_NAME, project.getName() );
        assertTrue( dataModel.getDefaultProperty( SOURCE_FOLDER ).toString().contains( "docroot\\WEB-INF\\src" ) );
        assertEquals( "NewVaadinPortletApplication", dataModel.getDefaultProperty( CLASS_NAME ) );
        assertEquals( "com.test", dataModel.getDefaultProperty( JAVA_PACKAGE ) );
        assertEquals( "com.vaadin.Application", dataModel.getDefaultProperty( SUPERCLASS ) );
        assertEquals( "com.vaadin.terminal.gwt.server.ApplicationPortlet2", dataModel.getDefaultProperty( VAADIN_PORTLET_CLASS ) );
        // page2
        assertTrue( dataModel.isPropertyEnabled( PORTLET_NAME ) );
        assertTrue( dataModel.isPropertyEnabled( DISPLAY_NAME ) );
        assertTrue( dataModel.isPropertyEnabled( TITLE ) );
        assertTrue( dataModel.isPropertyEnabled( CREATE_RESOURCE_BUNDLE_FILE ) );
        assertFalse( dataModel.isPropertyEnabled( CREATE_RESOURCE_BUNDLE_FILE_PATH ) );

        assertEquals( "newvaadinportlet", dataModel.getDefaultProperty( PORTLET_NAME ) );
        assertEquals( "NewVaadinPortlet", dataModel.getDefaultProperty( DISPLAY_NAME ) );
        assertEquals( "NewVaadinPortlet", dataModel.getDefaultProperty( TITLE ) );
        assertEquals( false, dataModel.getDefaultProperty( CREATE_RESOURCE_BUNDLE_FILE ) );
        assertEquals( "content/Language.properties", dataModel.getDefaultProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH ) );
        // page3
        assertTrue( dataModel.isPropertyEnabled( ICON_FILE ) );
        assertTrue( dataModel.isPropertyEnabled( ALLOW_MULTIPLE ) );
        assertTrue( dataModel.isPropertyEnabled( CSS_FILE ) );
        assertTrue( dataModel.isPropertyEnabled( JAVASCRIPT_FILE ) );
        assertTrue( dataModel.isPropertyEnabled( CSS_CLASS_WRAPPER ) );
        assertTrue( dataModel.isPropertyEnabled( CATEGORY ) );
        assertTrue( dataModel.isPropertyEnabled( ADD_TO_CONTROL_PANEL ) );
        assertTrue( dataModel.isPropertyEnabled( ENTRY_CATEGORY ) );
        assertTrue( dataModel.isPropertyEnabled( ENTRY_WEIGHT ) );
        assertTrue( dataModel.isPropertyEnabled( CREATE_ENTRY_CLASS ) );
        assertTrue( dataModel.isPropertyEnabled( ENTRY_CLASS_NAME ) );

        assertEquals( "/icon.png", dataModel.getDefaultProperty( ICON_FILE ) );
        assertEquals( false, dataModel.getDefaultProperty( ALLOW_MULTIPLE ) );
        assertEquals( "/css/main.css", dataModel.getDefaultProperty( CSS_FILE ) );
        assertEquals( "/js/main.js", dataModel.getDefaultProperty( JAVASCRIPT_FILE ) );
        assertEquals( "newvaadinportlet-portlet", dataModel.getDefaultProperty( CSS_CLASS_WRAPPER ) );
        assertEquals( "category.sample", dataModel.getDefaultProperty( CATEGORY ) );
        assertNotEquals( true, dataModel.getDefaultProperty( ADD_TO_CONTROL_PANEL ) );
        assertEquals( "category.my", dataModel.getDefaultProperty( ENTRY_CATEGORY ) );
        assertEquals( "1.5", dataModel.getDefaultProperty( ENTRY_WEIGHT ) );
        assertNotEquals( true, dataModel.getProperty( CREATE_ENTRY_CLASS ) );
        assertEquals( "NewVaadinPortletApplicationControlPanelEntry", dataModel.getDefaultProperty( ENTRY_CLASS_NAME ) );
    }

    @Test
    public void testTargetProject() throws Exception
    {
        String message = wizard.getDataModel().validate().getMessage();
        assertEquals( "Enter a project name.", message );
    }

    @Test
    public void testSourceFolder() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );

        dataModel.setProperty( SOURCE_FOLDER, "" );
        String message = dataModel.validateProperty( SOURCE_FOLDER ).getMessage();
        assertEquals( "The source folder cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( SOURCE_FOLDER, "src" );
        assertEquals( false, wizard.canFinish() );
        message = dataModel.validateProperty( SOURCE_FOLDER ).getMessage();

        assertEquals( "Source folder path must be a workspace relative absolute path.", message );

        dataModel.setProperty( SOURCE_FOLDER, "/" + project.getName() + "/docroot/WEB-INF/src" );
        message=dataModel.validate().getMessage();

        assertEquals( true, wizard.canFinish() );
    }

    @Test
    public void testApplicationClass() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( CLASS_NAME, "" );
        
        String message = dataModel.validateProperty( CLASS_NAME ).getMessage();
        assertEquals( "The class name cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );
        // todo:add class validation
    }

    @Test
    public void testSuperClass() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( SUPERCLASS, "" );
        String message = dataModel.validate().getMessage();
        assertEquals( "Must specify a portlet superclass.", message );
        assertEquals( false, wizard.canFinish() );
        // todo:add class validation
    }

    @Test
    public void testPortletClass() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( VAADIN_PORTLET_CLASS, "" );
        String message = dataModel.validate().getMessage();
        assertEquals( "Must specify a vaadin portlet class.", message );
        assertEquals( false, wizard.canFinish() );
        // todo:add class validation
    }

    @Test
    public void testPortletInfoGroup() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        
        dataModel.setProperty( PORTLET_NAME, "" );
        String message = dataModel.validate().getMessage();

        assertEquals( "Portlet name is empty.", message );
        assertEquals( false, wizard.canFinish() );
        // todo:add test for display name and title
    }

    @Test
    public void testResourcesGroup() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );

        dataModel.setProperty( CREATE_RESOURCE_BUNDLE_FILE, true );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_RESOURCE_BUNDLE_FILE_PATH ) );

        dataModel.setProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH, "" );
        String message = dataModel.validate().getMessage();
        assertEquals( "Resource bundle file path must be a valid path.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH, "test" );
        message = dataModel.validateProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH ).getMessage();
        assertEquals( "Resource bundle file path should end with .properties", message );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH, "content/TestLanguage.properties" );
        dataModel.setProperty( CLASS_NAME, "ResourceBundle" );
        assertEquals( true, wizard.canFinish() );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "TestLanguage.properties", "WEB-INF/src/content" ) );
    }

    // page 3
    @Test
    public void testLiferayPortletInfoGroup() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );

        dataModel.setProperty( ICON_FILE, "" );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( ALLOW_MULTIPLE, true );

        dataModel.setProperty( CSS_FILE, "" );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( JAVASCRIPT_FILE, "" );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( CSS_CLASS_WRAPPER, "" );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( CLASS_NAME, "LiferayPortletInfo" );
        dataModel.setProperty( CSS_FILE, "/css/new_main.css" );
        dataModel.setProperty( JAVASCRIPT_FILE, "/js/new_main.js" );

        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "new_main.css", "css" ) );
        assertEquals( true, checkFileExist( project, "new_main.js", "js" ) );
    }

    @Test
    public void testLiferayDisplayGroup() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );

        DataModelPropertyDescriptor[] categories = dataModel.getValidPropertyDescriptors( CATEGORY );
        assertNotNull( categories );
        assertEquals( true, containPropertyDescriptor( categories, "Sample" ) );
        assertEquals( true, containPropertyDescriptor( categories, "Marketplace" ) );
        assertEquals( true, containPropertyDescriptor( categories, "Wiki" ) );

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

        dataModel.setProperty( ENTRY_CATEGORY, "" );
        assertEquals( true, wizard.canFinish() );

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

        dataModel.setProperty( ENTRY_CLASS_NAME, "MyNewVaadinPortletApplicationControlPanelEntry" );
        dataModel.setProperty( CLASS_NAME, "LiferayDisplay" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true,checkFileExist( project, "MyNewVaadinPortletApplicationControlPanelEntry.java", "WEB-INF/src/com/test" ) );

        dataModel.setProperty( ENTRY_CLASS_NAME, "LiferayDisplay" );
        message = dataModel.validateProperty( ENTRY_CLASS_NAME ).getMessage();
        assertEquals( "Type 'com.test.LiferayDisplay' already exists.", message );
    }

}
