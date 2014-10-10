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
import com.liferay.ide.xml.search.ui.validators.LiferayHookDescriptorValidator;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

/**
 * @author Kuo Zhang
 */
public class LiferayHookXmlValidationTests extends XmlSearchTestsBase
{

    /**
     * Only test in Eclipse workbench, cannot be tested on headless thread
     */
    @Test
    public void testLanguagePropertiesELementValidation() throws Exception
    {
        final IProject project = getProject( "hooks", "Hook-Properties-Validation-Test-hook" );

        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.LIFERAY_HOOK_XML_FILE );
        final String markerType = LiferayHookDescriptorValidator.MARKER_TYPE;

        String elementName = "language-properties";
        String elementValue = null;
        String markerMessage = null;

        // language-properties value doesn't end with ".properties"
        elementValue = "LanguagePropertiesNotEndProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // language-properties value ends with ".properties"
        elementValue = "LanguagePropertiesEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // language properties file doesn't exist
        elementValue = "LanguagePropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // language properties file exists
        elementValue = "LanguagePropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // language properties file doesn't exist
        elementValue = "content/LanguagePropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage,true ) );

        // language properties file exists
        elementValue = "content/LanguagePropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // language properties file with "*" doesn't exist
        elementValue = "LanguagePropertiesNotExist*.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // language properties file with "*" exists
        elementValue = "LanguagePropertiesExist*.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );
    }

    /**
     * Only test in Eclipse workbench, cannot be tested on headless thread
     */
    @Test
    public void testPortalPropertiesElementValidation() throws Exception
    {
        final IProject project = getProject( "hooks", "Hook-Properties-Validation-Test-hook" );

        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.LIFERAY_HOOK_XML_FILE );
        final String markerType = LiferayHookDescriptorValidator.MARKER_TYPE;

        String elementName = "portal-properties";
        String elementValue = null;
        String markerMessage = null;

        // portal-properties value donesn't end with ".properties"
        elementValue = "PortalPropertiesNotEndProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_PROPERTIES_NOT_END_WITH_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // portal-properties value ends with ".properties"
        elementValue = "PortalPropertiesEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // portal properties file doesn't exist
        elementValue = "PortalPropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage, true ) );

        // portal properties file exists
        elementValue = "PortalPropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );

        // portal properties file doesn't exist
        elementValue = "content/PortalPropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage , true ) );

        // portal properties file exists
        elementValue = "content/PortalPropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( true, checkNoMarker( descriptorFile, markerType ) );
    }

}