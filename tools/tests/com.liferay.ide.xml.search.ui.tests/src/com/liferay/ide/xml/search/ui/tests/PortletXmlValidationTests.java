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

package com.liferay.ide.xml.search.ui.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.xml.search.ui.validators.PortletDescriptorValidator;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;


/**
 * This test cannot run in headless mode, run it as a product "org.eclipse.platform.ide"
 *
 * @author Kuo Zhang
 */
public class PortletXmlValidationTests extends XmlSearchTestsBase
{
    /**
     * Only test in Eclipse workbench, cannot be tested on headless thread
     */
    @Test
    public void testResourceBundleValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IProject project = getProject( "portlets", "Portlet-Xml-Validation-Test-portlet" );
        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.PORTLET_XML_FILE );
        final String markerType = PortletDescriptorValidator.MARKER_TYPE;

        final String elementName = "resource-bundle";
        String elementValue = null;
        String markerMessage = null;

        // resource-bundle value ends with ".properties"
        elementValue = "ResourceBundleEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_END_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // resource-bundle doesn't end with ".properties"
        elementValue = "ResourceBundleNotEndWithProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // resource-bundle values contains "/"
        elementValue = "ResourceBundle/WithSlash";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_CONTAIN_PATH_SEPARATOR, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // resource-bundle values doesn't contain "/"
        elementValue = "ResourceBundleWithoutSlash";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // resource bundle file doesn't exist
        elementValue = "ResourceBundleNotExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // resource bundle file exists
        elementValue = "ResourceBundleExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // resource bundle file doesn't exist
        elementValue = "content.ResourceBundleNotExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // resource bundle file exists
        elementValue = "ResourceBundleExist";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );
    }

    @Test
    public void testTypeValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IProject project = getProject( "portlets", "Portlet-Xml-Validation-Test-portlet" );
        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.PORTLET_XML_FILE );
        final String markerType = PortletDescriptorValidator.MARKER_TYPE;

        // *** Element <portlet-class> ***

        final String portletClass = "portlet-class";

        String elementValue = "Foo";
        setPropertiesValue( descriptorFile, portletClass, elementValue );
        String markerMessage = MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Empty";
        setPropertiesValue( descriptorFile, portletClass, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } );
        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.GenericPortletImpl";
        setPropertiesValue( descriptorFile, portletClass, elementValue );
        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // *** Element <listener-class> ***

        final String listenerClass = "listener-class";

        elementValue = "Foo";
        setPropertiesValue( descriptorFile, listenerClass, elementValue );
        markerMessage = MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Empty";
        setPropertiesValue( descriptorFile, listenerClass, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } );
        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setPropertiesValue( descriptorFile, listenerClass, elementValue );
        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // *** Element <filter-class> ***

        final String filterClass = "filter-class";

        elementValue = "Foo";
        setPropertiesValue( descriptorFile, filterClass, elementValue );
        markerMessage = MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Empty";
        setPropertiesValue( descriptorFile, filterClass, elementValue );
        markerMessage = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, false ) );

        elementValue = "com.liferay.ide.tests.ResourceFilterImpl";
        setPropertiesValue( descriptorFile, filterClass, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );
    }

}