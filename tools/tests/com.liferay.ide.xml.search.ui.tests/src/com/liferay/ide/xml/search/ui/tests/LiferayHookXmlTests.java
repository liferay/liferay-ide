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

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kuo Zhang
 */
public class LiferayHookXmlTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private static IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile : LiferayCore.create(IWebProject.class, getProject() ).getDescriptorFile(
            ILiferayConstants.LIFERAY_HOOK_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "hooks", "Liferay-Hook-Xml-Test-hook" );
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

    @Before
    public void cleanupMarkers() throws Exception
    {
        descriptorFile = getDescriptorFile();
        ZipFile projectFile = new ZipFile( getProjectZip( getBundleId(), "Liferay-Hook-Xml-Test-hook" ) );
        ZipEntry entry = projectFile.getEntry( "Liferay-Hook-Xml-Test-hook/docroot/WEB-INF/liferay-hook.xml" );

        descriptorFile.setContents( projectFile.getInputStream( entry ), IResource.FORCE, new NullProgressMonitor() );
        projectFile.close();
    }

    public void validateContentAssistForElement( String elementName, String elementContent, String[] expectedProposal )
        throws Exception
    {
        descriptorFile = getDescriptorFile();
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );

        final ICompletionProposal[] proposals = getProposalsForElement( descriptorFile, elementName );
        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        for( String proposal : expectedProposal )
        {
            assertTrue(
                "can't get proposal " + proposal + " in " + elementName, containsProposal( proposals, proposal, true ) );
        }
    }

    public void validateHyperLinksForElement( String elementName, String expectedHyperlinkText ) throws Exception
    {
        descriptorFile = getDescriptorFile();
        IHyperlink[] hyperLinks = getHyperLinksForElement( descriptorFile, elementName );
        assertTrue( containHyperlink( hyperLinks, expectedHyperlinkText, true ) );
    }

    public void validateQuickFixForElements( String elementName, String elementContent, String markerMessageRegex )
        throws Exception
    {
        //check all types of elements has quick fix for decrease validation level in current project and all project
        descriptorFile = getDescriptorFile();
        setElementContent( descriptorFile, elementName, elementContent );
        buildAndValidate( descriptorFile );

        IMarker expectedMarker = findMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false );
        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseProjectScopeXmlValidationLevel.class ) );
        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseInstanceScopeXmlValidationLevel.class ) );
    }

    @Test
    public void testIndexerClassName() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "indexer-class-name";
        String elementContent = "";

        String expectedHyperlinkText = "Open 'Indexer - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "Indexer - com.liferay.ide.tests" };
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        String markerMessageRegex = ".*" + elementContent + ".*";
        validateQuickFixForElements( elementName, elementContent, markerMessageRegex );
    }

    @Test
    public void testIndexerPostProcesserImpl() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "indexer-post-processor-impl";
        String elementContent = "";

        String expectedHyperlinkText = "Open 'IndexerPostProcessorImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "IndexerPostProcessorImpl - com.liferay.ide.tests" };
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        String markerMessageRegex = ".*" + elementContent + ".*";
        validateQuickFixForElements( elementName, elementContent, markerMessageRegex );
    }

    @Test
    public void testLanguageProperties() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "language-properties";
        String elementContent = "";

        String expectedHyperlinkText =
            "Open '/Liferay-Hook-Xml-Test-hook/docroot/WEB-INF/src/content/Language.properties'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "content/Language.properties" };
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "content/";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        String markerMessageRegex = ".*" + elementContent + ".*";
        validateQuickFixForElements( elementName, elementContent, markerMessageRegex );
    }

    @Test
    public void testPortalProperties() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "portal-properties";
        String elementContent = "";

        String expectedHyperlinkText = "Open '/Liferay-Hook-Xml-Test-hook/docroot/WEB-INF/src/content/portal.properties'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "content/portal.properties" };
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "content/";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        String markerMessageRegex = ".*" + elementContent + ".*";
        validateQuickFixForElements( elementName, elementContent, markerMessageRegex );

    }

    @Test
    public void testCustomJspDir() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "custom-jsp-dir";
        String elementContent = "";

        String[] expectedProposal = { "/WEB-INF/src" };
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "/WEB-INF";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "/custom_jspsNotExist";
        String markerMessageRegex = ".*" + elementContent + ".*";
        validateQuickFixForElements( elementName, elementContent, markerMessageRegex );
    }

    @Test
    public void testServiceType() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "service-type";

        String expectedHyperlinkText = "Open 'AccountService - com.liferay.portal.service'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

    }

    @Test
    public void testServiceImpl() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "service-impl";
        String expectedHyperlinkText = "Open 'AccountServiceWrapperImpl - com.liferay.ide.tests'";

        validateHyperLinksForElement( elementName, expectedHyperlinkText );
    }

    @Test
    public void testServletFilterImpl() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "servlet-filter-impl";
        String elementContent = "";

        String expectedHyperlinkText = "Open 'ServletFilterImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "ServletFilterImpl - com.liferay.ide.tests" };
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        String markerMessageRegex = ".*" + elementContent + ".*";
        validateQuickFixForElements( elementName, elementContent, markerMessageRegex );
    }

    @Test
    public void testStrutsActionImpl() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "struts-action-impl";
        String elementContent = "";

        String expectedHyperlinkText = "Open 'StrutsActionImpl - com.liferay.ide.tests'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "StrutsActionImpl - com.liferay.ide.tests" };
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "com.liferay.ide.tests";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        String markerMessageRegex = ".*" + elementContent + ".*";
        validateQuickFixForElements( elementName, elementContent, markerMessageRegex );
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        final IFile descriptorFile = getDescriptorFile();
        Object sourceViewerConfiguration =
            XmlSearchTestsUtils.getSourceViewerConfiguraionFromOpenedEditor( descriptorFile );

        assertEquals( true, sourceViewerConfiguration instanceof LiferayCustomXmlViewerConfiguration );
    }

}
