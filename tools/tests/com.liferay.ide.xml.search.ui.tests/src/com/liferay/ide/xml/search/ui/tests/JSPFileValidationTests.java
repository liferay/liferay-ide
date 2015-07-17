
package com.liferay.ide.xml.search.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.findMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.findMarkerResolutionByClass;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.verifyQuickFix;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.xml.search.ui.AddResourceKeyMarkerResolution;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;
import com.liferay.ide.xml.search.ui.markerResolutions.DecreaseInstanceScopeXmlValidationLevel;
import com.liferay.ide.xml.search.ui.markerResolutions.DecreaseProjectScopeXmlValidationLevel;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;
import com.liferay.ide.xml.search.ui.validators.LiferayJspValidator;

import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class JSPFileValidationTests extends XmlSearchTestsBase
{

    private static IProject project;
    private IFile jspFile;
    final String markerType = XMLSearchConstants.LIFERAY_JSP_MARKER_ID;

    public IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    public IFile getFile( String fileName ) throws Exception
    {

        jspFile = CoreUtil.getDefaultDocrootFolder( getProject() ).getFile( fileName );

        if( jspFile != null && jspFile.exists() )
        {
            return jspFile;
        }

        return null;
    }

    @Before
    public void cleanupMarkers() throws Exception
    {
        jspFile = getFile( "test-jsp-validation.jsp" );
        assertNotNull( jspFile );

        ZipFile projectFile = new ZipFile( getProjectZip( getBundleId(), "Portlet-Xml-Test-portlet" ) );
        ZipEntry entry = projectFile.getEntry( "Portlet-Xml-Test-portlet/docroot/test-jsp-validation.jsp" );

        jspFile.setContents( projectFile.getInputStream( entry ), IResource.FORCE, new NullProgressMonitor() );
        projectFile.close();
    }

    @AfterClass
    public static void deleteProject() throws Exception
    {
        try
        {
            project.close( null );
            project.delete( true, null );
        }
        catch( Exception e )
        {
        }
    }

    void ValidateAttrPropertyNotFound( String elementName, String attrName, String attrValue ) throws Exception
    {
        setAttrValue( jspFile, elementName, attrName, attrValue );
        buildAndValidate( jspFile );
        String errorMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_PROPERTY_NOT_FOUND, new Object[] { attrValue,
                "Language.properties" } );
        boolean findExpectedMarker = checkMarkerByMessage( jspFile, markerType, errorMessage, true );
        if( !findExpectedMarker )
        {
            buildAndValidate( jspFile );
            findExpectedMarker = checkMarkerByMessage( jspFile, markerType, errorMessage, true );
        }
        assertTrue( findExpectedMarker );
    }

    void ValidateAttrPropertyCorrect( String elementName, String attrName ) throws Exception
    {
        int random = (int) ( Math.random() * 100 );
        String attrValue = attrName + "Test" + random;
        setAttrValue( jspFile, elementName, attrName, attrValue );
        buildAndValidate( jspFile );

        String markerMessageRegex =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_PROPERTY_NOT_FOUND, new Object[] { attrValue,
                "Language.properties" } );
        
        quickFixChangeValidationLevel( markerMessageRegex );
        buildAndValidate( jspFile );
        verifyQuickFix( jspFile, markerType, markerMessageRegex, AddResourceKeyMarkerResolution.class );

    }

    void ValidateAttrMethodNotFound( String elementName, String attrName, String attrValue ) throws Exception
    {
        setAttrValue( jspFile, elementName, attrName, attrValue );
        buildAndValidate( jspFile );

        final String expectedMessage = "Method " + '"' + attrValue + '"' + " not found.";
        assertEquals( true, checkMarkerByMessage( jspFile, markerType, expectedMessage, true ) );
        quickFixChangeValidationLevel( expectedMessage+".*" );
    }

    public void quickFixChangeValidationLevel( String markerMessageRegex ) throws Exception
    {
        IMarker expectedMarker = findMarkerByMessage( jspFile, markerType, markerMessageRegex + ".*", false );
        String liferayPluginValidationType =
            expectedMarker.getAttribute( XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE, null );

        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseInstanceScopeXmlValidationLevel.class ) );

        verifyQuickFix( jspFile, markerType, markerMessageRegex, DecreaseProjectScopeXmlValidationLevel.class );

        ValidationPreferences.setProjectScopeValidationLevel(
            expectedMarker.getResource().getProject(), liferayPluginValidationType, 2 );
    }

    @Test
    public void testMessageKey() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:message";
        String attrName = "key";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:error";
        String attrName = "message";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testLable() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:input";
        String attrName = "label";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testHelpMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:input";
        String attrName = "helpMessage";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testSuffix() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:input";
        String attrName = "suffix";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testATitle() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:a";
        String attrName = "title";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testHeaderTitle() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:header";
        String attrName = "title";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testPanelTitle() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:panel";
        String attrName = "title";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testALable() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:a";
        String attrName = "label";
        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testButtonValue() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:button";
        String attrName = "value";
        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testPlaceholder() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:input";
        String attrName = "placeholder";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testWorkflowstatusStatusMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:workflow-status";
        String attrName = "statusMessage";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testIcondeleteConfirmation() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:icon-delete";
        String attrName = "confirmation";
        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testInputmoveboxesRighgtitle() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:input-move-boxes";
        String attrName = "rightTitle";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testInputmoveboxesLefttitle() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:input-move-boxes";
        String attrName = "leftTitle";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testSocialActivitiesFeedLinkMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        String elementName = "liferay-ui:social-activities";
        String attrName = "feedLinkMessage";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testSearchContainerEmptyResulsMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:search-container";
        String attrName = "emptyResultsMessage";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }

    @Test
    public void testActionURLName() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-portlet:actionURL";
        String attrName = "name";
        buildAndValidate( jspFile );
        Thread.sleep( 15000 );
        ValidateAttrMethodNotFound( elementName, attrName, "foo" );
        ValidateAttrMethodNotFound( elementName, attrName, "" );
        
        setAttrValue( jspFile, elementName, attrName, "beamMe" );
        buildAndValidate( jspFile );
        
        String markerMessage = ".*" + "beamMe" + ".*";
        assertEquals( false, checkMarkerByMessage( jspFile, elementName, markerMessage, false ) );
    }

    @Test
    public void testLiferayPortletParam() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-portlet:param";
        testTagParamValidation( elementName );
    }

    @Test
    public void testPortletParam() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "portlet:param";
        testTagParamValidation( elementName );
    }

    @Test
    public void testAuiClass() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        assertEquals(
            true,
            checkMarkerByMessage( jspFile, markerType, LiferayJspValidator.MESSAGE_CLASS_ATTRIBUTE_NOT_WORK, true ) );

    }

    public void testTagParamValidation( String elementName ) throws Exception
    {
        if( shouldSkipBundleTests() )return;
        
        String nameAttr = "foo";
        String valueAttr = "foo";

        String markerMessage = "Type " + '"' + valueAttr + '"' + " not found.";

        // name is not action type
        setAttrValue( jspFile, elementName, "name", nameAttr );
        setAttrValue( jspFile, elementName, "value", valueAttr );
        buildAndValidate( jspFile );
        assertEquals( false, checkMarkerByMessage( jspFile, markerType, markerMessage, true ) );

        // name is variable
        nameAttr = "<%= ActionRequest.ACTION_NAME %>";

        setAttrValue( jspFile, elementName, "name", nameAttr );
        buildAndValidate( jspFile );
        assertEquals( true, checkMarkerByMessage( jspFile, markerType, markerMessage, true ) );

        valueAttr = "com.liferay.ide.tests.Orphan";
        setAttrValue( jspFile, elementName, "value", valueAttr );
        buildAndValidate( jspFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { valueAttr,
                "com.liferay.util.bridges.mvc.ActionCommand" } );

        assertEquals( true, checkMarkerByMessage( jspFile, markerType, markerMessage, true ) );
        valueAttr = "com.liferat.ide.tests.PortletActionImpl";
        setAttrValue( jspFile, elementName, "value", valueAttr );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { valueAttr,
                "com.liferay.util.bridges.mvc.ActionCommand" } );
        assertEquals( false, checkMarkerByMessage( jspFile, markerType, markerMessage, true ) );

        // name is action
        nameAttr = "javax.portlet.action";
        valueAttr = "foo";

        setAttrValue( jspFile, elementName, "name", nameAttr );
        setAttrValue( jspFile, elementName, "value", valueAttr );
        markerMessage = "Type " + '"' + valueAttr + '"' + " not found.";
        buildAndValidate( jspFile );

        assertEquals( true, checkMarkerByMessage( jspFile, markerType, markerMessage, true ) );
        valueAttr = "com.liferay.ide.tests.Orphan";
        setAttrValue( jspFile, elementName, "value", valueAttr );
        buildAndValidate( jspFile );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { valueAttr,
                "com.liferay.util.bridges.mvc.ActionCommand" } );

        assertEquals( true, checkMarkerByMessage( jspFile, markerType, markerMessage, true ) );
        valueAttr = "com.liferat.ide.tests.PortletActionImpl";
        setAttrValue( jspFile, elementName, "value", valueAttr );
        markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { valueAttr,
                "com.liferay.util.bridges.mvc.ActionCommand" } );
        assertEquals( false, checkMarkerByMessage( jspFile, markerType, markerMessage, true ) );
    }
    
    @Test
    public void testValidatorErrorMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:validator";
        String attrName = "errorMessage";

        ValidateAttrPropertyNotFound( elementName, attrName, "foo" );
        ValidateAttrPropertyNotFound( elementName, attrName, "" );
        ValidateAttrPropertyCorrect( elementName, attrName );
    }
}
