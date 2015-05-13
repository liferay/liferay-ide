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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getHyperLinksForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposalsForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getTextHoverForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 * @author Li Lu
 */
public class JSPFileTests extends XmlSearchTestsBase
{

    private static IProject project;
    private IFile jspFile;

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    private IFile getJspFile( String fileName ) throws Exception
    {
        final IFile file = CoreUtil.getDefaultDocrootFolder( getProject() ).getFile( fileName );

        if( file != null && file.exists() )
        {
            return file;
        }

        return null;
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

    public void validateContentAssistForEmptyAttr( String elementName, String attrName ) throws Exception
    {
        jspFile = getJspFile( "test-jsp-validation.jsp" );
        setAttrValue( jspFile, elementName, attrName, "" );
        buildAndValidate( jspFile );

        final ICompletionProposal[] proposals = getProposalsForAttr( jspFile, elementName, attrName );
        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );
        final String[] expectedProposalString =
            { "javax.portlet.title - [Language.properties]", "MessageKeyHoverTest - [Language.properties]",
                "Test - [Language.properties]" };
        for( String proposal : expectedProposalString )
        {
            assertTrue(
                "can't get proposal " + proposal + " in " + elementName + attrName,
                containsProposal( proposals, proposal, true ) );

        }
    }

    public void validateContentAssistForAttr( String elementName, String attrName ) throws Exception
    {
        jspFile = getJspFile( "test-jsp-validation.jsp" );
        setAttrValue( jspFile, elementName, attrName, "MessageKeyHove" );
        buildAndValidate( jspFile );
        final ICompletionProposal[] proposals = getProposalsForAttr( jspFile, elementName, attrName );

        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );
        final String expectedProposalString = "MessageKeyHoverTest - [Language.properties]";

        assertEquals( true, containsProposal( proposals, expectedProposalString, true ) );

    }

    public void validateTextHoverForAttr( String elementName, String attrName ) throws Exception
    {
        jspFile = getJspFile( "test-jsp-validation.jsp" );
        String[] hover = getTextHoverForAttr( jspFile, elementName, attrName );
        String expectMessageRegex =
            ".*this is the test for message key text hover.* in /Portlet-Xml-Test-portlet/docroot/WEB-INF/src/content/Language.properties.*";

        hover = getTextHoverForAttr( jspFile, elementName, attrName );
        if( hover.length == 0 || !hover[0].toString().matches( expectMessageRegex ) )
        {
            buildAndValidate( jspFile );
            hover = getTextHoverForAttr( jspFile, elementName, attrName );
        }
        assertTrue( hover[0].toString().matches( expectMessageRegex ) );

    }

    public void validateHyperLinksForAttr( String elementName, String attrName ) throws Exception
    {
        jspFile = getJspFile( "test-jsp-validation.jsp" );
        String expectedHyperlinkText1 = "Open 'MessageKeyHoverTest' in Language.properties";
        String expectedHyperlinkText2 = "Open";

        IHyperlink[] hyperLinks = getHyperLinksForAttr( jspFile, elementName, attrName );

        boolean haslink =
            containHyperlink( hyperLinks, expectedHyperlinkText1, true ) ||
                containHyperlink( hyperLinks, expectedHyperlinkText2, false );

        hyperLinks = getHyperLinksForAttr( jspFile, elementName, attrName );
        assertTrue( "can'get hyper link at <" + elementName + "  " + attrName + ">", haslink );
    }

    @Test
    public void testMessageKey() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:message";
        String attrName = "key";
        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:error";
        String attrName = "message";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testLable() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "aui:input";
        String attrName = "label";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testHelpMessage() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "aui:input";
        String attrName = "helpMessage";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testSuffix() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "aui:input";
        String attrName = "suffix";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testATitle() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "aui:a";
        String attrName = "title";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testHeaderTitle() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:header";
        String attrName = "title";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testPanelTitle() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:panel";
        String attrName = "title";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testALable() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "aui:a";
        String attrName = "label";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testButtonValue() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "aui:button";
        String attrName = "value";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testPlaceholder() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "aui:input";
        String attrName = "placeholder";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testWorkflowstatusStatusMessage() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "aui:workflow-status";
        String attrName = "statusMessage";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testIcondeleteConfirmation() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:icon-delete";
        String attrName = "confirmation";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testInputmoveboxesRighgtitle() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:input-move-boxes";
        String attrName = "rightTitle";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testInputmoveboxesLefttitle() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:input-move-boxes";
        String attrName = "leftTitle";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testSocialActivitiesFeedLinkMessage() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:social-activities";
        String attrName = "feedLinkMessage";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testSearchContainerEmptyResulsMessage() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-ui:search-container";
        String attrName = "emptyResultsMessage";

        validateHyperLinksForAttr( elementName, attrName );
        validateTextHoverForAttr( elementName, attrName );
        validateContentAssistForEmptyAttr( elementName, attrName );
        validateContentAssistForAttr( elementName, attrName );
    }

    @Test
    public void testActionURLName() throws Exception
    {

        if( shouldSkipBundleTests() )return;

        jspFile = getJspFile( "test-jsp-validation.jsp" );

        String elementName = "liferay-portlet:actionURL";
        String attrName = "name";

        Thread.sleep( 10000 );
        validateHyperLinksForAttr( elementName, attrName );
        setAttrValue( jspFile, elementName, attrName, "" );
        buildAndValidate( jspFile );

        ICompletionProposal[] proposals = getProposalsForAttr( jspFile, elementName, attrName );
        if( !containsProposal( proposals, "actionTest - NewPortlet", true ) )
        {
            buildAndValidate( jspFile );
            proposals=getProposalsForAttr( jspFile, elementName, attrName );
        }
        assertEquals( true, containsProposal( proposals, "actionTest - NewPortlet", true ) );
    }

    @Test
    public void testLiferayPortletParamValueMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "liferay-portlet:param";
        String attrName = "value";

        validateHyperLinksForAttr( elementName, attrName );
    }

    @Test
    public void testPortletParamValueMessage() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        String elementName = "portlet:param";
        String attrName = "value";

        validateHyperLinksForAttr( elementName, attrName );
    }

}
