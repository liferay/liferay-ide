
package com.liferay.ide.portlet.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.operation.PortletSupertypesValidator;
import com.liferay.ide.portlet.ui.wizard.NewPortletWizard;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings( "restriction" )
public class NewPortletWizardTests extends PortletUITestBase implements INewPortletClassDataModelProperties
{

    private IProject project;

    @Before
    public void createPortletProject() throws Exception
    {
        project = createProject( "mvc-portlet-project-test", PluginType.portlet, "mvc" );
    }

    public boolean containPropertyDescriptor( DataModelPropertyDescriptor[] properties, String expectedValue )
    {
        boolean flag = false;
        for( DataModelPropertyDescriptor property : properties )
        {
            if( property.getPropertyDescription().equals( expectedValue ) )
            {
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Test
    public void testContentDefaultValues() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();

        // check default values page 1
        assertTrue( dataModel.getProperty( SOURCE_FOLDER ).toString().contains( "docroot\\WEB-INF\\src" ) );
        assertEquals( true, dataModel.getProperty( CREATE_NEW_PORTLET_CLASS ) );
        assertEquals( false, dataModel.getProperty( USE_DEFAULT_PORTLET_CLASS ) );
        assertEquals( "NewPortlet", dataModel.getProperty( CLASS_NAME ) );
        assertEquals( "com.test", dataModel.getProperty( JAVA_PACKAGE ) );
        assertEquals( "com.liferay.util.bridges.mvc.MVCPortlet", dataModel.getProperty( SUPERCLASS ) );
        // page2
        assertEquals( "new", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "New", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "New", dataModel.getProperty( TITLE ) );
        assertEquals( true, dataModel.getProperty( VIEW_MODE ) );
        assertNotEquals( true, dataModel.getProperty( EDIT_MODE ) );
        assertNotEquals( true, dataModel.getProperty( HELP_MODE ) );
        assertNotEquals( true, dataModel.getProperty( ABOUT_MODE ) );
        assertNotEquals( true, dataModel.getProperty( CONFIG_MODE ) );
        assertNotEquals( true, dataModel.getProperty( EDITDEFAULTS_MODE ) );
        assertNotEquals( true, dataModel.getProperty( EDITGUEST_MODE ) );
        assertNotEquals( true, dataModel.getProperty( PREVIEW_MODE ) );
        assertNotEquals( true, dataModel.getProperty( PRINT_MODE ) );
        assertEquals( true, dataModel.getProperty( CREATE_JSPS ) );

        assertEquals( "/html/new", dataModel.getProperty( CREATE_JSPS_FOLDER ) );
        assertEquals( false, dataModel.getProperty( CREATE_RESOURCE_BUNDLE_FILE ) );
        assertEquals( "content/Language.properties", dataModel.getProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH ) );
        // page3
        assertEquals( "/icon.png", dataModel.getProperty( ICON_FILE ) );
        assertEquals( false, dataModel.getProperty( ALLOW_MULTIPLE ) );
        assertEquals( "/css/main.css", dataModel.getProperty( CSS_FILE ) );
        assertEquals( "/js/main.js", dataModel.getProperty( JAVASCRIPT_FILE ) );
        assertEquals( "new-portlet", dataModel.getProperty( CSS_CLASS_WRAPPER ) );
        assertEquals( "category.sample", dataModel.getProperty( CATEGORY ) );
        assertNotEquals( true, dataModel.getProperty( ADD_TO_CONTROL_PANEL ) );
        assertEquals( "category.my", dataModel.getProperty( ENTRY_CATEGORY ) );
        assertEquals( "1.5", dataModel.getProperty( ENTRY_WEIGHT ) );
        assertNotEquals( true, dataModel.getProperty( CREATE_ENTRY_CLASS ) );
        assertEquals( "NewPortletControlPanelEntry", dataModel.getProperty( ENTRY_CLASS_NAME ) );
        // page4
        assertEquals( true, dataModel.getProperty( MODIFIER_PUBLIC ) );
        assertNotEquals( true, dataModel.getProperty( MODIFIER_ABSTRACT ) );
        assertNotEquals( true, dataModel.getProperty( MODIFIER_FINAL ) );
        assertNotEquals( true, dataModel.getProperty( INTERFACES ) );
        assertEquals( false, dataModel.getProperty( CONSTRUCTOR ) );
        assertEquals( true, dataModel.getProperty( ABSTRACT_METHODS ) );
        assertEquals( true, dataModel.getProperty( INIT_OVERRIDE ) );
        assertEquals( false, dataModel.getProperty( DESTROY_OVERRIDE ) );
        assertEquals( true, dataModel.getProperty( DOVIEW_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOEDIT_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOHELP_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOHELP_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOABOUT_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOCONFIG_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOEDITDEFAULTS_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOEDITGUEST_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOPREVIEW_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( DOPRINT_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( PROCESSACTION_OVERRIDE ) );
        assertNotEquals( true, dataModel.getProperty( SERVERESOURCE_OVERRIDE ) );

        dataModel.setProperty( CLASS_NAME, "DefaultPortlet" );
        IStatus status = dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertNotNull( status );
        assertEquals( "OK", status.getMessage() );
        assertTrue( checkFileExist( project, "DefaultPortlet.java", "WEB-INF/src/com/test" ) );
        assertTrue( checkFileExist( project, "main.js", "js" ) );
        assertTrue( checkFileExist( project, "main.css", "css" ) );
        assertTrue( checkFileExist( project, "icon.png", "." ) );
    }

    @Test
    public void testTargetProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        NewPortletWizard wizard = new NewPortletWizard();
        String message = wizard.getDataModel().validate().getMessage();
        assertEquals( "Enter a project name.", message );
    }

    @Test
    public void testSourceFolder() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();
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
    public void testCreateNewPortletAndUseDefaultPortlet() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();
        String contentName = USE_DEFAULT_PORTLET_CLASS;

        dataModel.setProperty( CREATE_NEW_PORTLET_CLASS, false );
        dataModel.setProperty( contentName, true );
        assertEquals( false, dataModel.isPropertyEnabled( CLASS_NAME ) );
        assertEquals( false, dataModel.isPropertyEnabled( JAVA_PACKAGE ) );
        assertEquals( false, dataModel.isPropertyEnabled( SUPERCLASS ) );

        dataModel.setProperty( CREATE_NEW_PORTLET_CLASS, true );
        dataModel.setProperty( contentName, false );
        assertEquals( true, dataModel.isPropertyEnabled( CLASS_NAME ) );
        assertEquals( true, dataModel.isPropertyEnabled( JAVA_PACKAGE ) );
        assertEquals( true, dataModel.isPropertyEnabled( SUPERCLASS ) );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String contentName = CLASS_NAME;
        NewPortletWizard wizard = new NewPortletWizard( project );

        IDataModel dataModel = wizard.getDataModel();
        dataModel.setProperty( contentName, "" );
        String message = dataModel.validateProperty( contentName ).getMessage();
        assertEquals( "The class name cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( contentName, "New Portlet" );
        message = dataModel.validateProperty( contentName ).getMessage();
        assertEquals( "Invalid Java class name: The type name 'New Portlet' is not a valid identifier", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( contentName, "NewPortlet1" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        dataModel = wizard.getDataModel();
        dataModel.setProperty( contentName, "NewPortlet1" );
        message = dataModel.validateProperty( contentName ).getMessage();
        assertEquals( "Type 'com.test.NewPortlet1' already exists.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( USE_DEFAULT_PORTLET_CLASS, true );
        assertEquals( false, wizard.canFinish() );
        dataModel.setProperty( CLASS_NAME, "UseDefaultPortlet" );
        // dataModel.setProperty( USE_DEFAULT_PORTLET_CLASS, true );
        assertEquals( true, wizard.canFinish() );
    }

    @Test
    public void testJavaPackage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();
        String contentName = JAVA_PACKAGE;

        dataModel.setProperty( contentName, "" );
        assertEquals( true, wizard.canFinish() );
        dataModel.setProperty( CLASS_NAME, "EmptyPackageTest" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertTrue( checkFileExist( project, "EmptyPackageTest.java", "WEB-INF/src" ) );

        dataModel.setProperty( contentName, "com.test" );
        String message = dataModel.validate().getMessage();
        assertEquals( "Portlet name already exists.", message );
    }

    @Test
    public void testSuperClass() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();
        String contentName = SUPERCLASS;

        dataModel.setProperty( contentName, "" );
        String message = dataModel.validateProperty( contentName ).getMessage();
        assertEquals( "Must specify a portlet superclass.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( contentName, "invalidSuperClass" );
        message = dataModel.validateProperty( contentName ).getMessage();
        assertEquals( "Portlet superclass must be a valid portlet class.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( CLASS_NAME, "ExistPortlet" );
        dataModel.setProperty( contentName, "com.liferay.util.bridges.mvc.MVCPortlet" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        dataModel.setProperty( CLASS_NAME, "NewPortlet" );
        dataModel.setProperty( contentName, "com.test.ExistPortlet" );
        assertEquals( true, wizard.canFinish() );

        DataModelPropertyDescriptor[] superClasses = dataModel.getValidPropertyDescriptors( SUPERCLASS );
        assertNotNull( superClasses );

        assertEquals( true, containPropertyDescriptor( superClasses, "com.liferay.util.bridges.mvc.MVCPortlet" ) );
        assertEquals(
            true, containPropertyDescriptor( superClasses, "com.liferay.portal.kernel.portlet.LiferayPortlet" ) );
        assertEquals( true, containPropertyDescriptor( superClasses, "javax.portlet.GenericPortlet" ) );

        // test superclass not in the default list
        dataModel.setProperty( contentName, "com.liferay.util.bridges.freemarker.FreeMarkerPortlet" );
        dataModel.setProperty( CLASS_NAME, "SuperClass" );
        assertEquals( true, wizard.canFinish() );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "SuperClass.java", "WEB-INF/src/com/test" ) );
    }

    @Test
    public void testPortletInfoGroup() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewPortletWizard wizard = new NewPortletWizard();
        IDataModel dataModel = wizard.getDataModel();

        // test the defaults for classname, portletname, displayname
        assertEquals( "NewPortlet", dataModel.getProperty( CLASS_NAME ) );
        assertEquals( "new", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "New", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "New", dataModel.getProperty( TITLE ) );

        // check all other defaults
        dataModel.setProperty( CLASS_NAME, "MyNewPortlet" );
        assertEquals( "my-new", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "My New", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "My New", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "ABCNewPortlet" );
        assertEquals( "ABCNewPortlet", dataModel.getProperty( CLASS_NAME ) );
        assertEquals( "abc-new", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "Abc New", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Abc New", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "abcdMyPORTLET" );
        assertEquals( "abcd-my", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "Abcd My", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Abcd My", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "NewPortletD" );
        assertEquals( "new-portlet-d", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "New Portlet D", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "New Portlet D", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "MyAABcPortletPortlet" );
        assertEquals( "my-aa-bc-portlet", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "My Aa Bc Portlet", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "My Aa Bc Portlet", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "Aa12M334yBbCCC" );
        assertEquals( "aa12-m334y-bb-ccc", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "Aa12 M334y Bb Ccc", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Aa12 M334y Bb Ccc", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "Aa12M334yB2bCCC2C" );
        assertEquals( "aa12-m334y-b2b-cc-c2-c", dataModel.getProperty( PORTLET_NAME ) );
        assertEquals( "Aa12 M334y B2b Cc C2 C", dataModel.getProperty( DISPLAY_NAME ) );
        assertEquals( "Aa12 M334y B2b Cc C2 C", dataModel.getProperty( TITLE ) );

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
    }

    @Test
    public void testPortletModesGroup() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();
        assertEquals( false, dataModel.isPropertyEnabled( VIEW_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( EDIT_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( HELP_MODE ) );

        dataModel.setProperty( EDIT_MODE, true );
        dataModel.setProperty( HELP_MODE, true );

        dataModel.setProperty( CLASS_NAME, "PortletModes" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        assertTrue( checkFileExist( project, "view.jsp", "html/portletmodes" ) );
        assertTrue( checkFileExist( project, "edit.jsp", "html/portletmodes" ) );
        assertTrue( checkFileExist( project, "help.jsp", "html/portletmodes" ) );
    }

    @Test
    public void testLiferayPortletModesGroup() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();

        assertEquals( true, dataModel.isPropertyEnabled( ABOUT_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( CONFIG_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( EDITDEFAULTS_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( EDITGUEST_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( PREVIEW_MODE ) );
        assertEquals( true, dataModel.isPropertyEnabled( PRINT_MODE ) );

        dataModel.setProperty( ABOUT_MODE, true );
        dataModel.setProperty( CONFIG_MODE, true );
        dataModel.setProperty( EDITDEFAULTS_MODE, true );
        dataModel.setProperty( EDITGUEST_MODE, true );
        dataModel.setProperty( PREVIEW_MODE, true );
        dataModel.setProperty( PRINT_MODE, true );

        dataModel.setProperty( CLASS_NAME, "LiferayPortletModes" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );

        assertTrue( checkFileExist( project, "about.jsp", "html/liferayportletmodes" ) );
        assertTrue( checkFileExist( project, "config.jsp", "html/liferayportletmodes" ) );
        assertTrue( checkFileExist( project, "edit-defaults.jsp", "html/liferayportletmodes" ) );
        assertTrue( checkFileExist( project, "edit-guest.jsp", "html/liferayportletmodes" ) );
        assertTrue( checkFileExist( project, "preview.jsp", "html/liferayportletmodes" ) );
        assertTrue( checkFileExist( project, "print.jsp", "html/liferayportletmodes" ) );
    }

    @Test
    public void testResourcesGroup() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        // create jsp files group
        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_JSPS ) );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_JSPS_FOLDER ) );

        dataModel.setProperty( CREATE_JSPS, false );
        assertEquals( false, dataModel.isPropertyEnabled( CREATE_JSPS_FOLDER ) );

        dataModel.setProperty( CREATE_JSPS, true );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_JSPS_FOLDER ) );
        dataModel.setProperty( CREATE_JSPS_FOLDER, "" );
        String message = dataModel.validateProperty( CREATE_JSPS_FOLDER ).getMessage();
        assertEquals( "JSP folder cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );
        dataModel.setProperty( CREATE_JSPS, false );
        assertEquals( true, wizard.canFinish() );

        // set jsp folder to same as portlet name
        dataModel.setProperty( CREATE_JSPS, true );
        dataModel.setProperty( CREATE_JSPS_FOLDER, dataModel.getProperty( PORTLET_NAME ) );
        message = dataModel.validateProperty( CREATE_JSPS_FOLDER ).getMessage();
        assertEquals( "JSP folder cannot match portlet name", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( CLASS_NAME, "JSPFolder" );
        assertEquals( true, wizard.canFinish() );

        dataModel.setProperty( CREATE_JSPS_FOLDER, "/html/jspfolderchanged" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertTrue( checkFileExist( project, "view.jsp", "html/jspfolderchanged" ) );

        // create resource bundle file group

        wizard = new NewPortletWizard( project );
        dataModel = wizard.getDataModel();
        assertEquals( true, dataModel.isProperty( CREATE_RESOURCE_BUNDLE_FILE ) );
        assertEquals( false, dataModel.isPropertyEnabled( CREATE_RESOURCE_BUNDLE_FILE_PATH ) );

        dataModel.setProperty( CREATE_RESOURCE_BUNDLE_FILE, true );
        assertEquals( true, dataModel.isPropertyEnabled( CREATE_RESOURCE_BUNDLE_FILE_PATH ) );

        dataModel.setProperty( CREATE_RESOURCE_BUNDLE_FILE_PATH, "" );
        message = dataModel.validate().getMessage();
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
        if( shouldSkipBundleTests() )return;

        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();

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
        if( shouldSkipBundleTests() )return;
        
        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();

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

        dataModel.setProperty( ENTRY_CLASS_NAME, "MyNewPortletControlPanelEntry" );
        dataModel.setProperty( CLASS_NAME, "LiferayDisplay" );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "MyNewPortletControlPanelEntry.java", "WEB-INF/src/com/test" ) );

        dataModel.setProperty( ENTRY_CLASS_NAME, "LiferayDisplay" );
        message = dataModel.validateProperty( ENTRY_CLASS_NAME ).getMessage();
        assertEquals( "Type 'com.test.LiferayDisplay' already exists.", message );
    }

    // page4
    @Test
    public void testModifiers() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();

        assertEquals( false, dataModel.isPropertyEnabled( MODIFIER_PUBLIC ) );
        assertEquals( false, dataModel.isPropertyEnabled( MODIFIER_ABSTRACT ) );
        assertEquals( true, dataModel.isPropertyEnabled( MODIFIER_FINAL ) );
    }

    @Test
    public void testInterfaces() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();

        assertEquals( true, dataModel.isPropertyEnabled( INTERFACES ) );
    }

    @Test
    public void testMethodStubs() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        NewPortletWizard wizard = new NewPortletWizard( project );
        IDataModel dataModel = wizard.getDataModel();

        assertEquals( true, dataModel.isPropertyEnabled( CONSTRUCTOR ) );
        dataModel.setProperty( CONSTRUCTOR, true );

        // change super class to "GenericPortlet" then methods checkbox can be edit
        dataModel.setProperty( SUPERCLASS, "javax.portlet.GenericPortlet" );
        boolean mvcPortlet = PortletSupertypesValidator.isMVCPortletSuperclass( dataModel );
        assertEquals( false, mvcPortlet );
        assertEquals( true, dataModel.isPropertyEnabled( ABSTRACT_METHODS ) );
        assertEquals( false, dataModel.isPropertyEnabled( INIT_OVERRIDE ) );
        assertEquals( true, dataModel.isPropertyEnabled( DESTROY_OVERRIDE ) );
        assertEquals( true, dataModel.getBooleanProperty( DOVIEW_OVERRIDE ) );

        assertEquals( true, dataModel.isPropertyEnabled( DOEDIT_OVERRIDE ) );
        assertEquals( true, dataModel.isPropertyEnabled( DOHELP_OVERRIDE ) );
        assertEquals( false, dataModel.getBooleanProperty( DOABOUT_OVERRIDE ) );
        assertEquals( false, dataModel.getBooleanProperty( DOCONFIG_OVERRIDE ) );
        assertEquals( false, dataModel.getBooleanProperty( DOEDITDEFAULTS_OVERRIDE ) );
        assertEquals( false, dataModel.getBooleanProperty( DOEDITGUEST_OVERRIDE ) );
        assertEquals( false, dataModel.getBooleanProperty( DOPREVIEW_OVERRIDE ) );
        assertEquals( false, dataModel.getBooleanProperty( DOPRINT_OVERRIDE ) );
        assertEquals( true, dataModel.isPropertyEnabled( PROCESSACTION_OVERRIDE ) );
        assertEquals( true, dataModel.isPropertyEnabled( SERVERESOURCE_OVERRIDE ) );
    }

}
