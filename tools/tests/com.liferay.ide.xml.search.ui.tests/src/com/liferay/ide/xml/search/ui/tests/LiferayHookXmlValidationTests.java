
package com.liferay.ide.xml.search.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkNoMarker;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.validators.LiferayHookDescriptorValidator;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class LiferayHookXmlValidationTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptor;
    private IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptor != null ? descriptor : LiferayCore.create( getProject() ).getDescriptorFile(
            ILiferayConstants.LIFERAY_HOOK_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "hooks", "Liferay-Hook-Xml-Test-hook" );
            deleteOtherProjects( project );
        }

        return project;
    }

    @Test
    public void testPortalProperties() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portal-properties";
        String elementValue = null;
        String markerMessage = null;

        // portal-properties value doesn't end with ".properties"
        elementValue = "PortalPropertiesNotEndProperties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES,
                new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // portal properties file doesn't exist
        elementValue = "PortalPropertiesNotExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // portal properties file exists
        elementValue = "PortalPropertiesExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        checkMarkerByMessage( descriptorFile, MARKER_TYPE, "", true );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    @Test
    public void testLanguageProperties() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "language-properties";
        String elementValue = null;
        String markerMessage = null;

        // language-properties value doesn't end with ".properties"
        elementValue = "LanguagePropertiesNotEndProperties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES,
                new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // language-properties value ends with ".properties"
        elementValue = "LanguagePropertiesEndWithProperties.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // language properties file doesn't exist
        elementValue = "LanguagePropertiesNotExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // language properties file exists
        elementValue = "LanguagePropertiesExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // language properties file doesn't exist
        elementValue = "content/LanguagePropertiesNotExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // language properties file exists
        elementValue = "content/LanguagePropertiesExist.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // language properties file with "*" doesn't exist
        elementValue = "LanguagePropertiesNotExist*.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // language properties file with "*" exists
        elementValue = "LanguagePropertiesExist*.properties";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // set to a correct value
        elementValue = "content/Language.properties";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testCustomJspDir() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "custom-jsp-dir";

        String elementValue = "/custom_jspsNotExist";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        // path incorrect test
        elementValue = "/custom_jsps";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        // set to correct jsp dir
        elementValue = "/WEB-INF/src";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testServiceTypeAndServiceImpl() throws Exception
    {

        final IFile descriptorFile = getDescriptorFile();
        String elementName = "service-type";

        // type not exist
        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // type is not an interface
        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format(
                LiferayHookDescriptorValidator.MESSAGE_SERVICE_TYPE_NOT_INTERFACE, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        // validate type hierarchy
        elementValue = "com.liferay.ide.tests.InterfaceTest";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = LiferayHookDescriptorValidator.MESSAGE_SERVICE_TYPE_INVALID;
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        // correct
        elementValue = "com.liferay.portal.service.AccountService";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // service-impl test
        elementName = "service-impl";
        elementValue = "Foo";

        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_SERVICE_IMPL_TYPE_INCORRECT, new Object[] {
                elementValue, "com.liferay.portal.service.AccountServiceWrapper" } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.AccountServiceWrapperImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    @Test
    public void testIndexerClassName() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "indexer-class-name";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );

        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );

        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.Indexer";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testIndexerPostProcesserImpl() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "indexer-post-processor-impl";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.IndexerPostProcessorImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testServletFilterImpl() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "servlet-filter-impl";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.ServletFilterImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testStrutsActionImpl() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "struts-action-impl";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage =
            MessageFormat.format( LiferayHookDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.StrutsActionImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

}
