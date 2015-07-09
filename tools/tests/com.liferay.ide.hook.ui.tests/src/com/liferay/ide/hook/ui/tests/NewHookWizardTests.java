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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.hook.ui.wizard.NewHookWizard;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.jface.wizard.IWizardPage;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Li Lu
 */
@SuppressWarnings( "restriction" )
public class NewHookWizardTests extends HookUITestBase implements INewHookDataModelProperties
{

    private IProject project;
    private final NewHookWizard wizard = new NewHookWizard();
    private final IDataModel dataModel = wizard.getDataModel();
    private final String MARKER_TYPE = "org.eclipse.jst.jsp.core.validationMarker";

    public IProject getProject( String projectName ) throws Exception
    {
        IProject[] projects = CoreUtil.getWorkspaceRoot().getProjects();
        if( projects != null )
        {
            for( IProject project : projects )
            {
                if( project.exists() && project.getName().startsWith( projectName ) )
                    return project;
            }
        }

        NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( PluginType.hook );
        project = createProject( op );

        return project;
    }

    @Before
    public void createPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        project = getProject( "hook-project-test" );
    }

    @SuppressWarnings( "unused" )
    @Test
    public void testContentDefaults() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );

        // page1
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_CUSTOM_JSPS ) );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_PORTAL_PROPERTIES ) );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_SERVICES ) );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_LANGUAGE_PROPERTIES ) );

        assertNotEquals( true, dataModel.getDefaultProperty( CREATE_CUSTOM_JSPS ) );
        assertNotEquals( true, dataModel.getDefaultProperty( CREATE_PORTAL_PROPERTIES ) );
        assertNotEquals( true, dataModel.getDefaultProperty( CREATE_SERVICES ) );
        assertNotEquals( true, dataModel.getDefaultProperty( CREATE_LANGUAGE_PROPERTIES ) );
        IWizardPage[] pages = wizard.getPages();

        // custom jsps page
        assertEquals( true, dataModel.isPropertyEnabled( SELECTED_PROJECT ) );
        assertEquals( true, dataModel.isPropertyEnabled( WEB_ROOT_FOLDER ) );
        assertEquals( true, dataModel.isPropertyEnabled( CUSTOM_JSPS_FOLDER ) );
        assertEquals( true, dataModel.isPropertyEnabled( CUSTOM_JSPS_ITEMS ) );
        assertEquals( true, dataModel.isPropertyEnabled( DISABLE_CUSTOM_JSP_FOLDER_VALIDATION ) );

        assertEquals( project.getName(), dataModel.getDefaultProperty( SELECTED_PROJECT ) );
        assertEquals( "docroot", dataModel.getDefaultProperty( WEB_ROOT_FOLDER ) );
        assertEquals( "/META-INF/custom_jsps", dataModel.getDefaultProperty( CUSTOM_JSPS_FOLDER ) );
        DataModelPropertyDescriptor customJSPFolders = dataModel.getPropertyDescriptor( CUSTOM_JSPS_FOLDER );
        assertEquals( null, dataModel.getDefaultProperty( CUSTOM_JSPS_ITEMS ) );
        assertEquals( true, dataModel.getDefaultProperty( DISABLE_CUSTOM_JSP_FOLDER_VALIDATION ) );

        // portal properties page
        assertEquals( true, dataModel.isPropertyEnabled( PORTAL_PROPERTIES_FILE ) );
        assertEquals( true, dataModel.isPropertyEnabled( PORTAL_PROPERTIES_ACTION_ITEMS ) );
        assertEquals( true, dataModel.isPropertyEnabled( PORTAL_PROPERTIES_ACTION_ITEMS ) );

        assertEquals(
            "/" + project.getName() + "/docroot/WEB-INF/src/portal.properties",
            dataModel.getDefaultProperty( PORTAL_PROPERTIES_FILE ) );
        assertEquals( null, dataModel.getDefaultProperty( PORTAL_PROPERTIES_ACTION_ITEMS ) );
        assertEquals( null, dataModel.getDefaultProperty( PORTAL_PROPERTIES_OVERRIDE_ITEMS ) );

        // service properties page
        assertEquals( true, dataModel.isPropertyEnabled( SERVICES_ITEMS ) );
        assertEquals( null, dataModel.getDefaultProperty( SERVICES_ITEMS ) );

        // language properties page
        assertEquals( true, dataModel.isPropertyEnabled( LANGUAGE_PROPERTIES_ITEMS ) );
        assertEquals(
            "/" + project.getName() + "/docroot/WEB-INF/src/content", dataModel.getDefaultProperty( CONTENT_FOLDER ) );
        assertEquals( null, dataModel.getDefaultProperty( LANGUAGE_PROPERTIES_ITEMS ) );
    }

    @Test
    public void testTargetProject() throws Exception
    {
        String message = wizard.getDataModel().validate().getMessage();
        assertEquals( "Enter a project name.", message );
    }

    @SuppressWarnings( "static-access" )
    @Test
    public void testNextPage() throws Exception
    {
        wizard.addPages();

        dataModel.setBooleanProperty( CREATE_CUSTOM_JSPS, true );
        IWizardPage currentPage = wizard.getStartingPage();
        IWizardPage nextPage = wizard.getNextPage( currentPage );
        assertEquals( wizard.CUSTOM_JSPS_PAGE, nextPage.getName() );

        dataModel.setBooleanProperty( CREATE_CUSTOM_JSPS, false );
        dataModel.setBooleanProperty( CREATE_LANGUAGE_PROPERTIES, true );
        currentPage = wizard.getStartingPage();
        nextPage = wizard.getNextPage( currentPage );
        assertEquals( wizard.LANGUAGE_PROPERTIES_PAGE, nextPage.getName() );

        dataModel.setBooleanProperty( CREATE_CUSTOM_JSPS, false );
        dataModel.setBooleanProperty( CREATE_LANGUAGE_PROPERTIES, false );
        dataModel.setBooleanProperty( CREATE_PORTAL_PROPERTIES, true );
        currentPage = wizard.getStartingPage();
        nextPage = wizard.getNextPage( currentPage );
        assertEquals( wizard.PORTAL_PROPERTIES_PAGE, nextPage.getName() );

        dataModel.setBooleanProperty( CREATE_CUSTOM_JSPS, false );
        dataModel.setBooleanProperty( CREATE_LANGUAGE_PROPERTIES, false );
        dataModel.setBooleanProperty( CREATE_PORTAL_PROPERTIES, false );
        dataModel.setBooleanProperty( CREATE_SERVICES, true );
        currentPage = wizard.getStartingPage();
        nextPage = wizard.getNextPage( currentPage );
        assertEquals( wizard.SERVICES_PAGE, nextPage.getName() );

        // select all
        dataModel.setBooleanProperty( CREATE_CUSTOM_JSPS, true );
        dataModel.setBooleanProperty( CREATE_LANGUAGE_PROPERTIES, true );
        dataModel.setBooleanProperty( CREATE_PORTAL_PROPERTIES, true );
        // check next page
        currentPage = wizard.getStartingPage();
        nextPage = wizard.getNextPage( currentPage );
        assertEquals( wizard.CUSTOM_JSPS_PAGE, nextPage.getName() );

        currentPage = nextPage;
        nextPage = wizard.getNextPage( currentPage );
        assertEquals( wizard.PORTAL_PROPERTIES_PAGE, nextPage.getName() );

        currentPage = nextPage;
        nextPage = wizard.getNextPage( currentPage );
        assertEquals( wizard.SERVICES_PAGE, nextPage.getName() );

        currentPage = nextPage;
        nextPage = wizard.getNextPage( currentPage );
        assertEquals( wizard.LANGUAGE_PROPERTIES_PAGE, nextPage.getName() );

        currentPage = nextPage;
        nextPage = wizard.getNextPage( currentPage );
        assertEquals( null, wizard.getNextPage( currentPage ) );

        // go back and check
        IWizardPage previousPage = wizard.getPreviousPage( currentPage );
        assertEquals( wizard.SERVICES_PAGE, previousPage.getName() );

        currentPage = previousPage;
        previousPage = wizard.getPreviousPage( currentPage );
        assertEquals( wizard.PORTAL_PROPERTIES_PAGE, previousPage.getName() );

        currentPage = previousPage;
        previousPage = wizard.getPreviousPage( currentPage );
        assertEquals( wizard.CUSTOM_JSPS_PAGE, previousPage.getName() );

        currentPage = previousPage;
        previousPage = wizard.getPreviousPage( currentPage );
        assertEquals( wizard.getStartingPage(), previousPage );

        currentPage = previousPage;
        previousPage = wizard.getPreviousPage( currentPage );
        assertEquals( null, previousPage );
    }

    // custom jsps page
    @Test
    public void testCustomJSPFolder() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( CREATE_CUSTOM_JSPS, true );

        dataModel.setProperty( CUSTOM_JSPS_FOLDER, "" );
        String message = dataModel.validateProperty( CUSTOM_JSPS_FOLDER ).getMessage();
        assertEquals( "Custom JSPs folder not configured.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( CUSTOM_JSPS_FOLDER, "/META-INF/my_custom_jsps" );
        dataModel.setProperty( CUSTOM_JSPS_ITEMS, null );
        message = dataModel.validate().getMessage();
        assertEquals( "Need to specify at least one JSP to override.", message );
        assertEquals( false, wizard.canFinish() );

        List<String[]> list = new ArrayList<String[]>();
        list.add( new String[] { "/html/portal/api/jsonws/action.jsp" } );
        list.add( new String[] { "/myhtml/view.jsp" } );
        dataModel.setProperty( CUSTOM_JSPS_ITEMS, list );
        assertEquals( true, wizard.canFinish() );

        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "/META-INF/my_custom_jsps/html/portal/api/jsonws/", "action.jsp" ) );
        assertEquals( true, checkFileExist( project, "/META-INF/my_custom_jsps/myhtml/", "view.jsp" ) );
    }

    @Test
    public void testAddJSPFromLiferay() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );

        List<String[]> list = new ArrayList<String[]>();

        String[][] items =
            { { "/html/common/themes/body_bottom-ext.jsp" }, { "/html/portal/api/jsonws/action.jsp" },
                { "/html/portlet/activities/view.jsp" } };

        for( String[] item : items )
            list.add( item );

        dataModel.setProperty( CREATE_CUSTOM_JSPS, true );
        dataModel.setProperty( CUSTOM_JSPS_ITEMS, list );

        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        String customJSPsFolder = dataModel.getProperty( CUSTOM_JSPS_FOLDER ).toString();
        assertEquals( true, checkFileExist( project, customJSPsFolder + "/html/common/themes/", "body_bottom-ext.jsp" ) );
        assertEquals( true, checkFileExist( project, customJSPsFolder + "/html/portal/api/jsonws/", "action.jsp" ) );
        assertEquals( true, checkFileExist( project, customJSPsFolder + "/html/portlet/activities/", "view.jsp" ) );
    }

    @Test
    public void testAddCustomJSP() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );

        List<String[]> list = new ArrayList<String[]>();

        String[][] items = { { "/myhtml/view1.jsp" }, { "./view1.jsp" }, { "../../view1" } };

        for( String[] item : items )
            list.add( item );

        dataModel.setProperty( CREATE_CUSTOM_JSPS, true );
        dataModel.setProperty( CUSTOM_JSPS_ITEMS, list );
        assertEquals( true, wizard.canFinish() );

        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        String customJSPsFolder = dataModel.getProperty( CUSTOM_JSPS_FOLDER ).toString();
        assertEquals( true, checkFileExist( project, customJSPsFolder + "/myhtml/", "view1.jsp" ) );
        assertEquals( true, checkFileExist( project, customJSPsFolder, "view1.jsp" ) );
        assertEquals( true, checkFileExist( project, "./", "view1" ) );
    }

    @Test
    public void testDisableCustomJSPValidation() throws Exception
    {
        IProject project2 = getProject( "hook-project-test2" );

        dataModel.setProperty( PROJECT_NAME, project2.getName() );
        dataModel.setProperty( CREATE_CUSTOM_JSPS, true );

        List<String[]> list = new ArrayList<String[]>();
        list.add( new String[] { "/html/portal/api/jsonws/action.jsp" } );
        dataModel.setProperty( CUSTOM_JSPS_ITEMS, list );

        dataModel.setProperty( DISABLE_CUSTOM_JSP_FOLDER_VALIDATION, false );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        IFile jspFile = getFile( project2, "/META-INF/custom_jsps/html/portal/api/jsonws/", "action.jsp" );
        buildAndValidate( jspFile );
        String markerMessageRegex =
            ".*" + "/html/portal/api/jsonws/init.jsp" + ".*" + "was not found at expected path" + ".*";
        IMarker marker = findMarkerByMessage( jspFile, MARKER_TYPE, markerMessageRegex, false );
        assertNotNull( marker );

        // disable jsp validation for custom jsp folder
        dataModel.setProperty( DISABLE_CUSTOM_JSP_FOLDER_VALIDATION, true );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        buildAndValidate( jspFile );
        Thread.sleep( 5000 );
        marker = findMarkerByMessage( jspFile, MARKER_TYPE, markerMessageRegex, false );
        assertNull( marker );
    }

    // portal properties page
    @Test
    public void testPortalPropertiesFile() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( CREATE_PORTAL_PROPERTIES, true );

        dataModel.setProperty( PORTAL_PROPERTIES_FILE, "" );
        String message = dataModel.validateProperty( PORTAL_PROPERTIES_FILE ).getMessage();
        assertEquals( "portal.properties file not configured.", message );
        assertEquals( false, wizard.canFinish() );
    }

    @Test
    public void testPortalPropertiesActionItems() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( CREATE_PORTAL_PROPERTIES, true );

        dataModel.setProperty( PORTAL_PROPERTIES_ACTION_ITEMS, null );
        dataModel.setProperty( PORTAL_PROPERTIES_OVERRIDE_ITEMS, null );
        String message = dataModel.validate().getMessage();
        assertEquals( "Need to specify at least one Event Action or Property to override.", message );

        List<String[]> list = new ArrayList<String[]>();
        String[][] items =
            { { "servlet.service.events.post", "com.liferay.portal.kernel.servlet.DynamicServletRequest" },
                { "login.events.post", "ExtLoginAction" }, { "MyEvent", "MyTestEvent" },
                { "logout.events.post", "com.test.ExtLogoutAction" } };
        for( String[] item : items )
            list.add( item );

        mockCreateNewEventClass( dataModel, "", "ExtLoginAction" );
        mockCreateNewEventClass( dataModel, "com.test", "ExtLogoutAction" );
        dataModel.setProperty( PORTAL_PROPERTIES_ACTION_ITEMS, list );
        assertEquals( true, wizard.canFinish() );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        assertEquals( true, checkFileExist( project, "/WEB-INF/src/", "ExtLoginAction.java" ) );
        assertEquals( true, checkFileExist( project, "/WEB-INF/src/com/test/", "ExtLogoutAction.java" ) );

        IFile propertiesFile = getFile( project, "/WEB-INF/src/", "portal.properties" );
        assertEquals( true, checkFileHasContent( propertiesFile, "login.events.post=ExtLoginAction" ) );
        assertEquals(
            true,
            checkFileHasContent(
                propertiesFile, "servlet.service.events.post=com.liferay.portal.kernel.servlet.DynamicServletRequest" ) );
        assertEquals( true, checkFileHasContent( propertiesFile, "logout.events.post=com.test.ExtLogoutAction" ) );
        assertEquals( true, checkFileHasContent( propertiesFile, "login.events.post=ExtLoginAction" ) );
        assertEquals( true, checkFileHasContent( propertiesFile, "MyEvent=MyTestEvent" ) );
    }

    @Test
    public void testPortalPropertiesOverrideItems() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( CREATE_PORTAL_PROPERTIES, true );

        List<String[]> list = new ArrayList<String[]>();
        list.add( new String[] { "admin.default.group.names", "test test" } );
        list.add( new String[] { "Foo", "foo1" } );
        list.add( new String[] { "Foo", "foo2" } );
        dataModel.setProperty( PORTAL_PROPERTIES_OVERRIDE_ITEMS, list );

        dataModel.setProperty( PORTAL_PROPERTIES_FILE, project.getName() + "/docroot/portal.properties" );
        assertEquals( true, wizard.canFinish() );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        IFile propertyFile = getFile( project, "portal.properties", "." );
        assertEquals( true, propertyFile.exists() );

        assertEquals( true, checkFileHasContent( propertyFile, "admin.default.group.names=test test" ) );
        assertEquals( false, checkFileHasContent( propertyFile, "Foo=foo1" ) );
        assertEquals( true, checkFileHasContent( propertyFile, "Foo=foo2" ) );
    }

    // create service hook page
    @Test
    public void testPortalServiceItems() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( CREATE_SERVICES, true );

        assertEquals( false, wizard.canFinish() );

        ArrayList<String[]> list = new ArrayList<String[]>();
        String items[][] =
            {
                { "com.liferay.portal.service.AccountLocalService",
                    "com.liferay.portal.service.AccountLocalServiceWrapper" },
                { "com.liferay.portlet.announcements.service.AnnouncementsEntryService", "ExtAnnouncementsEntryService" } };

        for( String[] item : items )
            list.add( item );

        mockCreateServiceImplClass(
            dataModel, "com.liferay.portlet.announcements.service.AnnouncementsEntryService", "com.test",
            "ExtAnnouncementsEntryService" );
        dataModel.setProperty( SERVICES_ITEMS, list );

        assertEquals( true, wizard.canFinish() );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        IFile classFile = getFile( project, "/WEB-INF/src/com/test", "ExtAnnouncementsEntryService.java" );
        assertEquals( true, checkFileExist( project, "/WEB-INF/src/com/test", "ExtAnnouncementsEntryService.java" ) );
        assertEquals( true, checkFileHasContent( classFile, "extends AnnouncementsEntryServiceWrapper" ) );
    }

    // language properties page
    @Test
    public void testContentFolder() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( CREATE_LANGUAGE_PROPERTIES, true );

        dataModel.setProperty( CONTENT_FOLDER, "" );
        String message = dataModel.validateProperty( CONTENT_FOLDER ).getMessage();
        assertEquals( "Content folder not configured.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( CONTENT_FOLDER, project.getName() + "/docroot/WEB-INF/content" );
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add( new String[] { "Language.properties" } );

        dataModel.setProperty( LANGUAGE_PROPERTIES_ITEMS, list );
        assertEquals( true, wizard.canFinish() );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        assertEquals( true, checkFileExist( project, "/WEB-INF/content/", "Language.properties" ) );
    }

    @Test
    public void testLanguagePropertyFilesItems() throws Exception
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( CREATE_LANGUAGE_PROPERTIES, true );

        dataModel.setProperty( LANGUAGE_PROPERTIES_ITEMS, null );
        String message = dataModel.validate().getMessage();
        assertEquals( "Need to specify at least one language property file.", message );
        assertEquals( false, wizard.canFinish() );

        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add( new String[] { "Language.properties" } );
        list.add( new String[] { "Language_ca.properties" } );

        dataModel.setProperty( LANGUAGE_PROPERTIES_ITEMS, list );
        assertEquals( true, wizard.canFinish() );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        assertEquals( true, checkFileExist( project, "/WEB-INF/src/content", "Language.properties" ) );
        assertEquals( true, checkFileExist( project, "/WEB-INF/src/content", "Language_ca.properties" ) );
    }

}
