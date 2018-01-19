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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getHyperLinksForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getProposalsForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.getTextHoverForAttr;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
 * @author Kuo Zhang
 * @author Li Lu
 */
public class LiferayDisplayXmlTests extends XmlSearchTestsBase
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
    public void testPortletAtIdContentAssist() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet";
        final String attrName = "id";

        String attrValue = "";
        setAttrValue( descriptorFile, elementName, attrName, attrValue );

        final ICompletionProposal[] proposals = getProposalsForAttr( descriptorFile, elementName, attrName );
        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );

        final String expectedProposalString = "Portlet-Xml-Test";
        assertEquals( true, containsProposal( proposals, expectedProposalString, true ) );

        attrValue = "Portlet-Xml-Test";
        setAttrValue( descriptorFile, elementName, attrName, attrValue );
        buildAndValidate( descriptorFile );
    }

    @Test
    public void testPortletAtIdHyperlink() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet";
        final String attrName = "id";

        IHyperlink[] hyperlinks = getHyperLinksForAttr( descriptorFile, elementName, attrName );
        final String expectedHyperlinkText = "/Portlet-Xml-Test-portlet/docroot/WEB-INF/portlet.xml";

        containHyperlink( hyperlinks, expectedHyperlinkText, false );
    }

    // a better way to test text hover ?

    @Test
    public void testPortletAtIdTextHover() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

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
            if( text.contains( "Portlet name" ) && text.contains( "Display name" ) &&
                text.contains( "Portlet class" ) && text.contains( "File" ) )
            {
                flag = true;
                break;
            }
        }

        assertEquals( true, flag );
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

    @Test
    public void testPortletAtIdQuickFix() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        final IFile descriptorFile = getDescriptorFile();
        final String elementName = "portlet";
        final String attrName = "id";

        setAttrValue( descriptorFile, elementName, attrName, "Wrong-Xml-Reference" );
        buildAndValidate( descriptorFile );

        String markerMessageRegex = ".*" + "Wrong-Xml-Reference" + ".*";
        IMarker expectedMarker = findMarkerByMessage( descriptorFile, MARKER_TYPE, markerMessageRegex, false );
        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseProjectScopeXmlValidationLevel.class ) );
        assertNotNull( findMarkerResolutionByClass( expectedMarker, DecreaseInstanceScopeXmlValidationLevel.class ) );
    }

}
