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

import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.checkNoMarker;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.containHyperlink;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.containProposal;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getHyperLinksForElementContent;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposals;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.openEditor;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.xml.search.ui.validators.PortletDescriptorValidator;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.wst.validation.ReporterHelper;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.search.editor.XMLReferencesStructuredTextViewerConfiguration;
import org.eclipse.wst.xml.search.editor.validation.XMLReferencesBatchValidator;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 * This test can only run in ui thread, and run as "org.eclipse.ui.ide.workbench" application.
 *
 * @author Kuo Zhang
 */
public class PortletXmlTests extends XmlSearchTestsBase
{
    private final static String MARKER_TYPE = PortletDescriptorValidator.MARKER_TYPE;
    private IProject project;
    private IFile descriptorFile;

    @Test
    public void testResourceBundle() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        openEditor( descriptorFile );

        testResourceBundleValidation( descriptorFile );
        testResourceBundleContentAssist( descriptorFile );
        testResourceBundleHyperlink( descriptorFile );
    }

    protected void testResourceBundleValidation( IFile descriptorFile ) throws Exception
    {
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
        markerMessage = MessageFormat.format(
            PortletDescriptorValidator.MESSAGE_RESOURCE_BUNDLE_CONTAIN_PATH_SEPARATOR, new Object[] { elementContent } );
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

    protected void testResourceBundleContentAssist( IFile descriptorFile ) throws Exception
    {
        final String elementName = "resource-bundle";
        String elementContent = "";
        setElementContent( descriptorFile, elementName, "" );

        final SourceViewerConfiguration conf = new XMLReferencesStructuredTextViewerConfiguration();
        final ICompletionProposal[] proposals = getProposals( descriptorFile, conf, elementName, Node.ELEMENT_NODE );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "content.Language";
        assertEquals( true, containProposal( proposals, exceptedProposalString, true ) );

        // set element conent back to a right content
        elementContent = "content.Language";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );

    }

    protected void testResourceBundleHyperlink( IFile descriptorFile ) throws Exception
    {
        final String elementName = "resource-bundle";
        String elementContent = "content.Language";
        setElementContent( descriptorFile, elementName, elementContent );

        final SourceViewerConfiguration conf = new XMLReferencesStructuredTextViewerConfiguration();
        IHyperlink[] hyperlinks = getHyperLinksForElementContent( descriptorFile, conf, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "content/Language.properties";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        openEditor( descriptorFile );

        testPortletClassValidation( descriptorFile );
        testPortletClassContentAssist( descriptorFile );
        testPortletClassHyperlink( descriptorFile );
    }

    protected void testPortletClassValidation( IFile descriptorFile ) throws Exception
    {
        final String elementName = "portlet-class";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );

        buildAndValidate( descriptorFile );

        String markerMessage = MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessageRegex = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false ) );

        elementValue = "com.liferay.ide.tests.GenericPortletImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    protected IReporter testTest( IFile file ) throws Exception
    {
        XMLReferencesBatchValidator batchValidator = new XMLReferencesBatchValidator();
        IReporter reporter = new ReporterHelper( new NullProgressMonitor() );

        Method validateMethod = batchValidator.getClass().getDeclaredMethod( "validateFile", IFile.class, IReporter.class );
        validateMethod.setAccessible( true );
        validateMethod.invoke( batchValidator, descriptorFile, reporter );
        return reporter;
    }

    protected void testPortletClassContentAssist( IFile descriptorFile ) throws Exception
    {
        final String elementName = "portlet-class";
        String elementContent = "";
        setElementContent( descriptorFile, elementName, elementContent );

        final SourceViewerConfiguration conf = new XMLReferencesStructuredTextViewerConfiguration();
        final ICompletionProposal[] proposals = getProposals( descriptorFile, conf, elementName, Node.ELEMENT_NODE );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "GenericPortletImpl - com.liferay.ide.tests";
        assertEquals( true, containProposal( proposals, exceptedProposalString, true ) );

        elementContent = "com.liferay.ide.tests.GenericPortletImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    protected void testPortletClassHyperlink( IFile descriptorFile ) throws Exception
    {
        final String elementName = "portlet-class";
        String elementContent = "com.liferay.ide.tests.GenericPortletImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        final SourceViewerConfiguration conf = new XMLReferencesStructuredTextViewerConfiguration();
        IHyperlink[] hyperlinks = getHyperLinksForElementContent( descriptorFile, conf, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "GenericPortletImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    @Test
    public void testListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        openEditor( descriptorFile );

        testListenerClassValidation( descriptorFile );
        testListenerClassContentAssist( descriptorFile );
        testListenerClassHyperLink( descriptorFile );
    }

    protected void testListenerClassValidation( IFile descriptorFile ) throws Exception
    {
        final String elementName = "listener-class";

        String elementValue = "Foo";
        setElementContent( descriptorFile, elementName, elementValue );
        String markerMessage = MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementValue } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementValue = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementValue );
        final String markerMessageRegex = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementValue } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false ) );

        elementValue = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    protected void testListenerClassContentAssist( IFile descriptorFile ) throws Exception
    {
        final String elementName = "listener-class";
        String elementContent = "";

        setElementContent( descriptorFile, elementName, elementContent );

        final SourceViewerConfiguration conf = new XMLReferencesStructuredTextViewerConfiguration();
        final ICompletionProposal[] proposals = getProposals( descriptorFile, conf, elementName, Node.ELEMENT_NODE );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "PortletURLGenerationListenerImpl - com.liferay.ide.tests";
        assertEquals( true, containProposal( proposals, exceptedProposalString, true ) );

        elementContent = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    protected void testListenerClassHyperLink( IFile descriptorFile ) throws Exception
    {
        final String elementName = "listener-class";
        String elementContent = "com.liferay.ide.tests.PortletURLGenerationListenerImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        final SourceViewerConfiguration conf = new XMLReferencesStructuredTextViewerConfiguration();
        IHyperlink[] hyperlinks = getHyperLinksForElementContent( descriptorFile, conf, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "PortletURLGenerationListenerImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    @Test
    public void testFilterClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        openEditor( descriptorFile );

        testFilterClassValidation( descriptorFile );
        testFilterClassContentAssist( descriptorFile );
        testFilterClassHyperlink( descriptorFile );
    }

    protected void testFilterClassValidation( IFile descriptorFile ) throws Exception
    {
        final String elementName = "filter-class";

        String elementContent = "Foo";
        setElementContent( descriptorFile, elementName, elementContent );
        String markerMessage = MessageFormat.format( PortletDescriptorValidator.MESSAGE_TYPE_NOT_FOUND, new Object[] { elementContent } );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        elementContent = "com.liferay.ide.tests.Orphan";
        setElementContent( descriptorFile, elementName, elementContent );
        final String markerMessageRegex = MessageFormat.format( MESSAGE_TYPE_HIERARCHY_INCORRECT, new Object[] { elementContent } ) + ".*";
        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false ) );

        elementContent = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
    }

    protected void testFilterClassContentAssist( IFile descriptorFile ) throws Exception
    {
        final String elementName = "filter-class";
        String elementContent = "";
        setElementContent( descriptorFile, elementName, elementContent );

        final SourceViewerConfiguration conf = new XMLReferencesStructuredTextViewerConfiguration();
        final ICompletionProposal[] proposals = getProposals( descriptorFile, conf, elementName, Node.ELEMENT_NODE );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "ResourceFilterImpl - com.liferay.ide.tests";
        assertEquals( true, containProposal( proposals, exceptedProposalString, true ) );

        elementContent = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
    }

    protected void testFilterClassHyperlink( IFile descriptorFile ) throws Exception
    {
        final String elementName = "listener-class";
        String elementContent = "com.liferay.ide.tests.ResourceFilterImpl";
        setElementContent( descriptorFile, elementName, elementContent );

        final SourceViewerConfiguration conf = new XMLReferencesStructuredTextViewerConfiguration();
        IHyperlink[] hyperlinks = getHyperLinksForElementContent( descriptorFile, conf, elementName );

        assertNotNull( hyperlinks );
        assertEquals( true, hyperlinks.length > 0 );

        final String exceptedHyperlink = "ResourceFilterImpl - com.liferay.ide.tests";
        assertEquals( true, containHyperlink( hyperlinks, exceptedHyperlink, false ) );
    }

    private IFile getDescriptorFile() throws Exception
    {
        if( descriptorFile == null )
        {
            descriptorFile = CoreUtil.getDescriptorFile( getProject(), ILiferayConstants.PORTLET_XML_FILE );
        }

        return descriptorFile;
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
        }

        return project;
    }
}