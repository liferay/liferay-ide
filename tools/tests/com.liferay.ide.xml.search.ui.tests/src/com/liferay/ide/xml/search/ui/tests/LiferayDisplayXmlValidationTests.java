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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.validators.LiferayDisplayDescriptorValidator;

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

/**
 * @author Li Lu
 */
public class LiferayDisplayXmlValidationTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private static IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create(IWebProject.class, getProject() ).getDescriptorFile(
            ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );
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
        ZipEntry entry = projectFile.getEntry( "Portlet-Xml-Test-portlet/docroot/WEB-INF/liferay-display.xml" );

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

    @Test
    public void testPortletAtId() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        final String elementName = "portlet";
        final String attrName = "id";

        final String wrongAttrValue = "Wrong-Xml-Reference";
        setAttrValue( descriptorFile, elementName, attrName, wrongAttrValue );

        String markerMessage =
            MessageFormat.format( LiferayDisplayDescriptorValidator.MESSAGE_REFERENCE_NOT_FOUND, new Object[] {
                wrongAttrValue, "portlet.xml" } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        final String correctAttrValue = "Portlet-Xml-Test";
        setAttrValue( descriptorFile, elementName, attrName, correctAttrValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testCategoryAtName() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        final String elementName = "category";
        final String attrName = "name";

        setAttrValue( descriptorFile, elementName, attrName, "   " );

        String markerMessage = LiferayDisplayDescriptorValidator.MESSAGE_CATEGORY_NAME_CANNOT_BE_EMPTY;

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        final String correctAttrValue = "category.sample";
        setAttrValue( descriptorFile, elementName, attrName, correctAttrValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }
}
