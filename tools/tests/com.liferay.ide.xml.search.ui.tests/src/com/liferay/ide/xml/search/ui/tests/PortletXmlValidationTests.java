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

import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkNoMarker;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;
import com.liferay.ide.xml.search.ui.validators.PortletDescriptorValidator;

import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class PortletXmlValidationTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private static IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create(IWebProject.class, getProject() ).getDescriptorFile(
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

    @Before
    public void cleanupMarkers() throws Exception
    {
        descriptorFile = getDescriptorFile();
        ZipFile projectFile = new ZipFile( getProjectZip( getBundleId(), "Portlet-Xml-Test-portlet" ) );
        ZipEntry entry = projectFile.getEntry( "Portlet-Xml-Test-portlet/docroot/WEB-INF/portlet.xml" );

        descriptorFile.setContents( projectFile.getInputStream( entry ), IResource.FORCE, new NullProgressMonitor() );
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

    public void validateElementTypeNotFound( String elementName, String elementValue ) throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    public void validateElementTypeHierarchyInocorrect( String elementName, String extendType ) throws Exception
    {
        String elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue,
                extendType } );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    public void validateElementResourceNotFound( String elementName, String elementValue ) throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();

        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format( LiferayBaseValidator.MESSAGE_RESOURCE_NOT_FOUND, new Object[] { elementValue } );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    public void validateElementcorrectValue( String elementName, String elementValue ) throws Exception
    {
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "portlet-class";
        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "javax.portlet.GenericPortlet" );
        validateElementcorrectValue( elementName, "com.liferay.util.bridges.mvc.MVCPortlet" );
    }

    @Test
    public void testListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "listener-class";
        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect( elementName, "javax.portlet.PortletURLGenerationListener" );
        validateElementcorrectValue( elementName, "com.liferay.ide.tests.PortletURLGenerationListenerImpl" );
    }

    @Test
    public void testFilterClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "filter-class";

        validateElementTypeNotFound( elementName, "foo" );
        validateElementTypeNotFound( elementName, "" );
        validateElementTypeHierarchyInocorrect(
            elementName,
            "javax.portlet.filter.ResourceFilter, javax.portlet.filter.RenderFilter, javax.portlet.filter.ActionFilter, javax.portlet.filter.EventFilter" );
        validateElementcorrectValue( elementName, "com.liferay.ide.tests.ResourceFilterImpl" );
    }

    @Test
    public void testResourceBundle() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "resource-bundle";
        String elementValue = null;
        String markerMessage = null;

        // resource-bundle value ends with ".properties"
        elementValue = "ResourceBundleEndWithProperties.properties";
        markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_END_PROPERTIES, new Object[] { elementValue } );
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // resource-bundle doesn't end with ".properties"
        elementValue = "ResourceBundleNotEndWithProperties";
        validateElementcorrectValue( elementName, elementValue );

        // resource-bundle values contains "/"
        elementValue = "ResourceBundle/WithSlash";
        markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_CONTAIN_PATH_SEPARATOR,
                new Object[] { elementValue } );
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        // resource-bundle values doesn't contain "/"
        validateElementcorrectValue( elementName, "ResourceBundleWithoutSlash" );

        // resource bundle file doesn't exist
        validateElementResourceNotFound( elementName, "" );
        validateElementResourceNotFound( elementName, "ResourceBundleNotExist" );
        validateElementResourceNotFound( elementName, "content.ResourceBundleNotExist" );

        // resource bundle file exists
        validateElementcorrectValue( elementName, "ResourceBundleExist" );
        validateElementcorrectValue( elementName, "content.Language" );
    }

}
