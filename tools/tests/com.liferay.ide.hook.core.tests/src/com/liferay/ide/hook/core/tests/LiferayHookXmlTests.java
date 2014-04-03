/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.hook.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.descriptor.LiferayHookDescriptorValidator;
import com.liferay.ide.project.core.tests.XmlTestsBase;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class LiferayHookXmlTests extends XmlTestsBase
{

    private IProject getProject( String projectName ) throws Exception
    {
        IProject project = CoreUtil.getWorkspaceRoot().getProject( projectName );

        if( project != null && project.exists() )
        {
            return project;
        }

        return importProject( "hooks", "com.liferay.ide.hook.core.tests", projectName );
    }

    private void setPropertiesValue( IFile descriptorFile, String elementName, String value ) throws Exception
    {
        final IDOMModel domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( descriptorFile );
        final IDOMDocument document = domModel.getDocument();
        final NodeList elements = document.getElementsByTagName( elementName );

        assertEquals( true, elements.getLength() > 0 );

        final Element element = (Element) elements.item( 0 );

        final NodeList childNodes = element.getChildNodes();

        for( int i = 0; i < childNodes.getLength(); i++ )
        {
            element.removeChild( childNodes.item( i ) );
        }

        element.appendChild( document.createTextNode( value ) );

        domModel.save();
        domModel.releaseFromEdit();

        descriptorFile.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
    }

    @Test
    public void testLanguagePropertiesELementValidation() throws Exception
    {
        final IProject project = getProject( "Hook-Properties-Validation-Test-hook" );

        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.LIFERAY_HOOK_XML_FILE );
        final String markerType = LiferayHookDescriptorValidator.MARKER_TYPE;

        String elementName = "language-properties";
        String elementValue = null;
        String markerMessage = null;

        // language-properties value doesn't end with ".properties"
        elementValue = "LanguagePropertiesNotEndProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_PRORETIES_VALUE_END_WITH_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // language-properties value ends with ".properties"
        elementValue = "LanguagePropertiesEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // language properties file doesn't exist
        elementValue = "LanguagePropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // language properties file exists
        elementValue = "LanguagePropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // language properties file doesn't exist
        elementValue = "content/LanguagePropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // language properties file exists
        elementValue = "content/LanguagePropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // language properties file with "*" doesn't exist
        elementValue = "LanguagePropertiesNotExist*.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // language properties file with "*" exists
        elementValue = "LanguagePropertiesExist*.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );
    }

    @Test
    public void testPortalPropertiesELementValidation() throws Exception
    {
        final IProject project = getProject( "Hook-Properties-Validation-Test-hook" );

        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, ILiferayConstants.LIFERAY_HOOK_XML_FILE );
        final String markerType = LiferayHookDescriptorValidator.MARKER_TYPE;

        String elementName = "portal-properties";
        String elementValue = null;
        String markerMessage = null;

        // portal-properties value donesn't end with ".properties"
        elementValue = "PortalPropertiesNotEndProperties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_PRORETIES_VALUE_END_WITH_PROPERTIES, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // portal-properties value ends with ".properties"
        elementValue = "PortalPropertiesEndWithProperties.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // portal properties file doesn't exist
        elementValue = "PortalPropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) ); 

        // portal properties file exists
        elementValue = "PortalPropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );

        // portal properties file doesn't exist
        elementValue = "content/PortalPropertiesNotExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );
        markerMessage = MessageFormat.format(
            LiferayHookDescriptorValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        waitForBuildAndValidation( project );
        assertEquals( true, checkMarker( descriptorFile, markerType, markerMessage ) );

        // portal properties file exists
        elementValue = "content/PortalPropertiesExist.properties";
        setPropertiesValue( descriptorFile, elementName, elementValue );

        waitForBuildAndValidation( project );
        assertEquals( false, checkMarker( descriptorFile, markerType, null ) );
    }
}
