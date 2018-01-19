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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.containHyperlink;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.findMarkerByMessage;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.findMarkerResolutionByClass;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getHyperLinksForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposalsForElement;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setElementContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlViewerConfiguration;
import com.liferay.ide.xml.search.ui.markerResolutions.DecreaseInstanceScopeXmlValidationLevel;
import com.liferay.ide.xml.search.ui.markerResolutions.DecreaseProjectScopeXmlValidationLevel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * This test can only run in ui thread, and run as "org.eclipse.ui.ide.workbench" application.
 *
 * @author Kuo Zhang
 * @author Li Lu
 */
public class PortletXmlTests extends XmlSearchTestsBase
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

    public void validateContentAssistForElement(
        String elementName, String elementContent, String expectedProposalString ) throws Exception
    {
        if( shouldSkipBundleTests() ) return;
        descriptorFile = getDescriptorFile();
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );
        final ICompletionProposal[] proposals = getProposalsForElement( descriptorFile, elementName );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        assertEquals( true, containsProposal( proposals, expectedProposalString, true ) );
    }

    public void validateHyperLinksForElement( String elementName, String expectedHyperlinkText ) throws Exception
    {
        descriptorFile = getDescriptorFile();
        IHyperlink[] hyperLinks = getHyperLinksForElement( descriptorFile, elementName );
        assertTrue( containHyperlink( hyperLinks, expectedHyperlinkText, true ) );
    }

    public void validateQuickFixForElements( String elementName , String elementContent ) throws Exception
    {
        descriptorFile = getDescriptorFile();
        String markerMessageRegex = ".*" + elementContent + ".*";
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );

        IMarker expectedMarker = findMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false );
        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseProjectScopeXmlValidationLevel.class ) );
        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseInstanceScopeXmlValidationLevel.class ) );
    }

    @Test
    public void testPortletClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "portlet-class";
        String expectedHyperlinkText = "Open 'MVCPortlet - com.liferay.util.bridges.mvc'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String elementContent = "";
        String expectedProposalString = "GenericPortletImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, expectedProposalString );

        elementContent = "GenericPortletI";
        validateContentAssistForElement( elementName, elementContent, expectedProposalString );
        validateQuickFixForElements( elementName, elementContent );
    }

    @Test
    public void testListenerClass() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String elementName = "listener-class";

        String expectedHyperlinkText = "Open 'PortletURLGenerationListenerImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String elementContent = "";
        String expectedProposalString = "PortletURLGenerationListenerImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, expectedProposalString );

        elementContent = "PortletURL";
        validateContentAssistForElement( elementName, elementContent, expectedProposalString );
        validateQuickFixForElements( elementName, elementContent );
    }

    @Test
    public void testFilterClass() throws Exception
    {
        final String elementName = "filter-class";

        String expectedHyperlinkText = "Open 'ResourceFilterImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String elementContent = "";
        String expectedProposalString = "ResourceFilterImpl - com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, expectedProposalString );

        elementContent = "ResourceFilter";
        validateContentAssistForElement( elementName, elementContent, expectedProposalString );
        validateQuickFixForElements( elementName, elementContent );
    }

    @Test
    public void testResourceBundle() throws Exception
    {
        final String elementName = "resource-bundle";
        String expectedHyperlinkText = "Open '/Portlet-Xml-Test-portlet/docroot/WEB-INF/src/content/Language.properties'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String elementContent = "";
        String expectedProposalString = "content.Language";
        validateContentAssistForElement( elementName, elementContent, expectedProposalString );

        elementContent = "content.";
        validateContentAssistForElement( elementName, elementContent, expectedProposalString );
        validateQuickFixForElements( elementName, elementContent );
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        Object sourceViewerConfiguration =
            XmlSearchTestsUtils.getSourceViewerConfiguraionFromOpenedEditor( descriptorFile );

        assertEquals( true, sourceViewerConfiguration instanceof LiferayCustomXmlViewerConfiguration );
    }
}
