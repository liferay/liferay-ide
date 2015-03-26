
package com.liferay.ide.xml.search.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkNoMarker;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.validators.PortletDescriptorValidator;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.junit.Test;

public class PortletXmlValidationTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create( getProject() ).getDescriptorFile(
            ILiferayConstants.PORTLET_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    @Test
    public void testPortletClass() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-class";
        String elementValue = "Foo";
        String markerMessage = null;

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            NLS.bind( PortletDescriptorValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT.toString(), new Object[] {
                elementValue, "javax.portlet.GenericPortlet" } );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.util.bridges.mvc.MVCPortlet";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    @Test
    public void testListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "listener-class";
        String elementValue = "Foo";
        String markerMessage = null;

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        markerMessage =
            MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        markerMessage =
            NLS.bind( PortletDescriptorValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT.toString(), new Object[] {
                elementValue, "javax.portlet.PortletURLGenerationListener" } );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

    }

    @Test
    public void testFilterClass() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "filter-class";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage =
            MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        markerMessage =
            NLS.bind(
                PortletDescriptorValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT.toString(),
                new Object[] {
                    elementValue,
                    "javax.portlet.filter.ResourceFilter, javax.portlet.filter.RenderFilter, javax.portlet.filter.ActionFilter, javax.portlet.filter.EventFilter" } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testResourceBundle() throws Exception
    {
        IFile descriptorFile = getDescriptorFile();
        final String elementName = "resource-bundle";
        String elementContent = null;
        String markerMessage = null;

        // resource-bundle value ends with ".properties"
        elementContent = "ResourceBundleEndWithProperties.properties";
        setElementContent( descriptorFile, elementName, elementContent );
        markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_END_PROPERTIES, new Object[] { elementContent } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // resource-bundle doesn't end with ".properties"
        elementContent = "ResourceBundleNotEndWithProperties";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // resource-bundle values contains "/"
        elementContent = "ResourceBundle/WithSlash";
        setElementContent( descriptorFile, elementName, elementContent );
        markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_CONTAIN_PATH_SEPARATOR,
                new Object[] { elementContent } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // resource-bundle values doesn't contain "/"
        elementContent = "ResourceBundleWithoutSlash";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // resource bundle file doesn't exist
        elementContent = "ResourceBundleNotExist";
        setElementContent( descriptorFile, elementName, elementContent );
        markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementContent } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // resource bundle file exists
        elementContent = "ResourceBundleExist";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // resource bundle file doesn't exist
        elementContent = "content.ResourceBundleNotExist";
        setElementContent( descriptorFile, elementName, elementContent );
        markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementContent } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // resource bundle file exists
        elementContent = "ResourceBundleExist";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );

        // set a right content
        elementContent = "content.Language";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

}
