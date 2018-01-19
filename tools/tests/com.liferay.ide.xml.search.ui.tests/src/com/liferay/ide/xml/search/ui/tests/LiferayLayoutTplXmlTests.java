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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.buildAndValidate;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.containHyperlink;
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.AfterClass;
import org.junit.Test;


/**
 * @author Kuo Zhang
 */
public class LiferayLayoutTplXmlTests extends XmlSearchTestsBase
{

    private IFile descriptor;
    private static IProject project;

    private IFile getDescriptorFile() throws Exception
    {
        return descriptor != null ? descriptor : LiferayCore.create(IWebProject.class, getProject() ).getDescriptorFile(
            ILiferayConstants.LIFERAY_LAYOUTTPL_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "layouttpl", "Liferay-Layout-Templates-Xml-Test-layouttpl" );
            XmlSearchTestsUtils.deleteOtherProjects( project );
        }

        return project;
    }

    @AfterClass
    public static void deleteProject() throws Exception
    {
        project.delete( true, null );
    }

    public void validateHyperLinksForElement( String elementName, String expectedHyperlinkText ) throws Exception
    {
        descriptor = getDescriptorFile();
        IHyperlink[] hyperLinks = getHyperLinksForElement( descriptor, elementName );
        assertTrue( containHyperlink( hyperLinks, expectedHyperlinkText, true ) );
    }

    public void validateContentAssistForElement( String elementName, String elementContent, String[] expectedProposal )
        throws Exception
    {
        descriptor = getDescriptorFile();
        setElementContent( descriptor, elementName, elementContent );
        buildAndValidate( descriptor );

        final ICompletionProposal[] proposals = getProposalsForElement( descriptor, elementName );
        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        for( String proposal : expectedProposal )
        {
            assertTrue(
                "can't get proposal " + proposal + " in " + elementName, containsProposal( proposals, proposal, true ) );
        }
    }

    @Test
    public void testTemplatePath() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "template-path";
        String expectedHyperlinkText = "Open '/Liferay-Layout-Templates-Xml-Test-layouttpl/docroot/Liferay_Layout_Templates_Xml_Test.tpl'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "/Liferay_Layout_Templates_Xml_Test.tpl" };
        String elementContent = "";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "/Liferay_Layout_Template";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );
    }

    @Test
    public void testThumbnailPath() throws Exception
    {
        if( shouldSkipBundleTests() )return;
        String elementName = "thumbnail-path";
        String expectedHyperlinkText = "Open '/Liferay-Layout-Templates-Xml-Test-layouttpl/docroot/Liferay_Layout_Templates_Xml_Test.png'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "/Liferay_Layout_Templates_Xml_Test.png" };
        String elementContent = "";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "/Liferay_Layout_Template";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );
    }

    @Test
    public void testWapTemplatePath() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "wap-template-path";
        String expectedHyperlinkText = "Open '/Liferay-Layout-Templates-Xml-Test-layouttpl/docroot/Liferay_Layout_Templates_Xml_Test.wap.tpl'";
        validateHyperLinksForElement( elementName, expectedHyperlinkText );

        String[] expectedProposal = { "/Liferay_Layout_Templates_Xml_Test.wap.tpl" };
        String elementContent = "";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );

        elementContent = "/Liferay_Layout_Template";
        validateContentAssistForElement( elementName, elementContent, expectedProposal );
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
