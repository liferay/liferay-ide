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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getHyperLinksForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposalsForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getTextHoverForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlViewerConfiguration;
import com.liferay.ide.xml.search.ui.validators.LiferayDisplayDescriptorValidator;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.junit.Test;


/**
 * @author Kuo Zhang
 */
public class LiferayDisplayXmlTests extends XmlSearchTestsBase
{

    private final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptorFile;
    private IProject project;

    private IFile getDescriptorFile() throws Exception
    {
        return descriptorFile != null ? descriptorFile :
            LiferayCore.create(  getProject() ).getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );
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

    // TODO
    public void testCategoryAtName()
    {
    }

    // example of testing attribute
    @Test
    public void testPortletAtId() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testPortletAtIdContentAssist();
        testPortletAtIdHyperlink();
        testPortletAtIdValidtion();
        testPortletAtIdTextHover();
    }

    protected void testPortletAtIdContentAssist() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet";
        final String attrName = "id";

        String attrValue = "";
        setAttrValue( descriptorFile, elementName, attrName, attrValue );

        final ICompletionProposal[] proposals = getProposalsForAttr( descriptorFile, elementName, attrName );
        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String exceptedProposalString = "Portlet-Xml-Test";
        assertEquals( true, containsProposal( proposals, exceptedProposalString, true ) );

        attrValue = "Portlet-Xml-Test";
        setAttrValue( descriptorFile, elementName, attrName, attrValue );
        buildAndValidate( descriptorFile );
    }

    protected void testPortletAtIdHyperlink() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet";
        final String attrName = "id";

        IHyperlink[] hyperlinks = getHyperLinksForAttr( descriptorFile, elementName, attrName );
        final String exceptedHyperlinkText = "/Portlet-Xml-Test-portlet/docroot/WEB-INF/portlet.xml";

        containHyperlink( hyperlinks, exceptedHyperlinkText, false );
    }

    // a better way to test text hover ?
    protected void testPortletAtIdTextHover() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet";
        final String attrName = "id";

        setAttrValue( descriptorFile, elementName, attrName, "Portlet-Xml-Test" );
        buildAndValidate( descriptorFile );

        final String[] displayTexts = getTextHoverForAttr( descriptorFile, elementName, attrName );
        assertNotNull( displayTexts );
        assertEquals( true, displayTexts.length > 0 );

        boolean flag = false;

        for( String text : displayTexts )
        {
            if( text.contains( "Portlet name" ) &&
                text.contains( "Display name" ) &&
                text.contains( "Portlet class" ) &&
                text.contains( "File" ) )
            {
                flag = true;
                break;
            }
        }

        assertEquals( true, flag );
    }

    protected void testPortletAtIdValidtion() throws Exception
    {
        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet";
        final String attrName = "id";

        final String wrongAttrValue = "Wrong-Xml-Reference";
        setAttrValue( descriptorFile, elementName, attrName, wrongAttrValue );

        String markerMessage = MessageFormat. format( LiferayDisplayDescriptorValidator.MESSAGE_REFERENCE_NOT_FOUND,
                                                      new Object[] { wrongAttrValue, "portlet.xml" } );

        buildAndValidate( descriptorFile );
        assertEquals( true, checkMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessage, true ) );

        final String correctAttrValue = "Portlet-Xml-Test";
        setAttrValue( descriptorFile, elementName, attrName, correctAttrValue );
        buildAndValidate( descriptorFile );
        assertEquals( true, checkNoMarker( descriptorFile, MARKER_TYPE ) );
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
