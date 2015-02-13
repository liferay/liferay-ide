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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.containHyperlink;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getHyperLinksForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposalsForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlViewerConfiguration;
import com.liferay.ide.xml.search.ui.validators.PortletDescriptorValidator;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.Test;

/**
 * This test can only run in ui thread, and run as "org.eclipse.ui.ide.workbench" application.
 *
 * @author Kuo Zhang
 */
public class PortletXmlTests extends XmlSearchTestsBase
{
    private final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private IProject project;

    private IFile getDescriptorFile() throws Exception
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
    public void testFilterClass() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testFilterClassValidation();
        testFilterClassContentAssist();
        testFilterClassHyperlink();
    }

    protected void testFilterClassContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "filter-class";
        String elementContent = "";
        setElementContent( descriptorFile, elementName, elementContent );

        final ICompletionProposal[] proposals = getProposalsForElement( descriptorFile, elementName );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "ResourceFilterImpl - com.liferay.ide.tests";
        assertEquals( true, containsProposal( proposals, exceptedProposalString, true ) );

        elementContent = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    protected void testFilterClassHyperlink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "listener-class";
        String elementContent = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        IHyperlink[] hyperlinks = getHyperLinksForElement( descriptorFile, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "ResourceFilterImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    protected void testFilterClassValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "filter-class";

        String elementContent = "Foo";
        setElementContent( descriptorFile, elementName, elementContent );
        String markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementContent } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementContent = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementContent );
        final String markerMessageRegex =
            MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementContent } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false ) );

        elementContent = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementContent );
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

        testListenerClassValidation();
        testListenerClassContentAssist();
        testListenerClassHyperLink();
    }

    protected void testListenerClassContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "listener-class";
        String elementContent = "";

        setElementContent( descriptorFile, elementName, elementContent );

        final ICompletionProposal[] proposals = getProposalsForElement( descriptorFile, elementName );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "PortletURLGenerationListenerImpl - com.liferay.ide.tests";
        assertEquals( true, containsProposal( proposals, exceptedProposalString, true ) );

        elementContent = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    protected void testListenerClassHyperLink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "listener-class";
        String elementContent = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        IHyperlink[] hyperlinks = getHyperLinksForElement( descriptorFile, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "PortletURLGenerationListenerImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    protected void testListenerClassValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "listener-class";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        final String markerMessageRegex =
            MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false ) );

        elementValue = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testPortletClassValidation();
        testPortletClassContentAssist();
        testPortletClassHyperlink();
    }

    protected void testPortletClassContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-class";
        String elementContent = "";
        setElementContent( descriptorFile, elementName, elementContent );

        final ICompletionProposal[] proposals = getProposalsForElement( descriptorFile, elementName );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "GenericPortletImpl - com.liferay.ide.tests";
        assertEquals( true, containsProposal( proposals, exceptedProposalString, true ) );

        elementContent = "com.liferay.ide.tests.GenericPortletImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    protected void testPortletClassHyperlink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-class";
        String elementContent = "com.liferay.ide.tests.GenericPortletImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        IHyperlink[] hyperlinks = getHyperLinksForElement( descriptorFile, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "GenericPortletImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    protected void testPortletClassValidation() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet-class";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );

        String markerMessage =
            MessageFormat.format(
                PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessageRegex =
            MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false ) );

        elementValue = "com.liferay.ide.tests.GenericPortletImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    @Test
    public void testResourceBundle() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testResourceBundleValidation();
        testResourceBundleContentAssist();
        testResourceBundleHyperlink();
    }

    protected void testResourceBundleContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "resource-bundle";
        String elementContent = "";
        setElementContent( descriptorFile, elementName, "" );

        final ICompletionProposal[] proposals = getProposalsForElement( descriptorFile, elementName );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "content.Language";
        assertEquals( true, containsProposal( proposals, exceptedProposalString, true ) );

        elementContent = "content.Language";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    protected void testResourceBundleHyperlink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "resource-bundle";
        String elementContent = "content.Language";
        setElementContent( descriptorFile, elementName, elementContent );

        IHyperlink[] hyperlinks = getHyperLinksForElement( descriptorFile, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "content/Language.properties";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    protected void testResourceBundleValidation() throws Exception
    {
        IFile descriptorFile = getDescriptorFile();
        final String elementName = "resource-bundle";
        String elementContent = null;
        String markerMessage = null;

        // resource-bundle value ends with ".properties"
        elementContent = "ResourceBundleEndWithProperties.properties";
        setElementContent( descriptorFile, elementName, elementContent );
        markerMessage = MessageFormat.format(
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
        markerMessage = MessageFormat.format(
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
        markerMessage = MessageFormat.format(
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
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() ) { return; }

        final IFile descriptorFile = getDescriptorFile();
        Object sourceViewerConfiguration =
            XmlSearchTestsUtils.getSourceViewerConfiguraionFromOpenedEditor( descriptorFile );

        assertEquals( true, sourceViewerConfiguration instanceof LiferayCustomXmlViewerConfiguration );
    }
}