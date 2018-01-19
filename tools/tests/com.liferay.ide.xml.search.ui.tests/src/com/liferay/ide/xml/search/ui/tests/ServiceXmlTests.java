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

import static com.liferay.ide.ui.tests.UITestsUtils.containsProposal;
import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkNoMarker;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposalsForText;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;

import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class ServiceXmlTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    public static final String SERVICE_XML_SYNTAX_INVALID = "Syntax of \"{0}\" is invalid.";
    private IFile descriptorFile;
    private static IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create(IWebProject.class, getProject() ).getDescriptorFile(
            ILiferayConstants.SERVICE_XML_FILE );
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
        ZipEntry entry = projectFile.getEntry( "Portlet-Xml-Test-portlet/docroot/WEB-INF/service.xml" );

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
    public void testNamespaceValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        String elementName = "namespace";
        String elementValue = "namespace1";
        setElementContent( descriptorFile, elementName, elementValue );

        String markerMessage = MessageFormat.format( SERVICE_XML_SYNTAX_INVALID, new Object[] { elementValue } );

        buildAndValidate( descriptorFile );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "namespace";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );

        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testPackagePathValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        String elementName = "service-builder";
        String attrName = "package-path";
        String invalidAttrValue = "com.liferay test";

        setAttrValue( descriptorFile, elementName, attrName, invalidAttrValue );

        String markerMessage = "Invalid Java package name: 'liferay test' is not a valid Java identifier";

        buildAndValidate( descriptorFile );

        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        String defaultAttrValue = "com.example.plugins";
        setAttrValue( descriptorFile, elementName, attrName, defaultAttrValue );
        buildAndValidate( descriptorFile );

        assertTrue( checkNoMarker( descriptorFile, MARKER_TYPE ) );

        setAttrValue( descriptorFile, elementName, attrName, "" );
        buildAndValidate( descriptorFile );
        markerMessage = "Invalid Java package name: A package name must not be empty";
        assertTrue( checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );
    }

    @Test
    public void testServiceXMLTemplates() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        descriptorFile = getDescriptorFile();
        ICompletionProposal[] proposals = getProposalsForText( descriptorFile, "entity" );
        final String[] expectedProposalStrings =
            { "column", "finder", "order", "reference", "tx-required", "sb-column - a column for an entity",
                "sb-columnp-primary - a primary column for an entity", "sb-entity - an entity element",
                "sb-entity-sample - a simple entity element", "sb-exceptions - an exceptions element",
                "sb-finder - a finder element", "sb-finder-column - a finder column for a finder",
                "sb-order - an order element", "sb-order-column - an order column for an order" };

        for( String expectedProposalString : expectedProposalStrings )
        {
            assertTrue( "can't get proposal at :" + expectedProposalString, containsProposal( proposals, expectedProposalString, true ) );
        }

        //test on IDE-1683
        descriptorFile = LiferayCore.create( IWebProject.class, project ).getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        proposals = getProposalsForText( descriptorFile, "portlet" );

        for( String expectedProposalString : expectedProposalStrings )
        {
            assertFalse( "service builder proposal at :" + descriptorFile, containsProposal( proposals, expectedProposalString, true ) );
        }

        descriptorFile = LiferayCore.create( IWebProject.class, project ).getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        proposals = getProposalsForText( descriptorFile, "portlet" );

        for( String expectedProposalString : expectedProposalStrings )
        {
            assertFalse( "service builder proposal at :" + descriptorFile, containsProposal( proposals, expectedProposalString, true ) );
        }
    }
}
