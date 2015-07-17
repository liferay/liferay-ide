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

package com.liferay.ide.alloy.ui.tests;

import static com.liferay.ide.alloy.ui.tests.AlloyTestsUtils.getWebSevicesProposals;
import static com.liferay.ide.ui.tests.UITestsUtils.containsProposal;
import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.alloy.ui.editor.PortletJSPSourceViewerConfiguration;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.tests.ProjectCoreBase;
import com.liferay.ide.ui.tests.UITestsUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;

/**
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class TemplatesTests extends ProjectCoreBase
{

    private static final String bundleId = "com.liferay.ide.alloy.ui.tests";
    private IProject project;

    protected String getBundleId()
    {
        return bundleId;
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

    private IFile getJspFile(String fileName) throws Exception
    {
        final IFile file =  CoreUtil.getDefaultDocrootFolder( getProject() ).getFile( fileName );

        if( file != null && file.exists() )
        {
            return file;
        }

        return null;
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        final IFile viewJspFile = getJspFile( "view.jsp" );

        Object sourceViewerConfiguration =
            UITestsUtils.getSourceViewerConfiguraionFromOpenedEditor( viewJspFile );

        assertEquals( true, sourceViewerConfiguration instanceof PortletJSPSourceViewerConfiguration );
    }

    @Test
    public void testTemplatesPropsals() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        IFile file = getJspFile( "view.jsp" );

        String[] testNames =
        {
            "jsonws-DDMStructure-delete-structure",
            "jsonws-ExpandoColumn-update-type-settings",
            "jsonws-JournalArticle-unsubscribe"
        };

        ICompletionProposal[] proposals = getWebSevicesProposals( file, "aui:script" );

        assertEquals( "the proposals' length should be greater than 1000", true, proposals.length > 1000 );

        assertNotNull( proposals );

        int i = 0;

        for( ICompletionProposal proposal : proposals )
        {
            for( String testName : testNames )
            {
                if( proposal.getDisplayString().startsWith( testName ) )
                {
                    i++;
                }
            }
        }

        assertEquals( "No liferay webservices templates been found. ", 3, i );
    }
    
    @Test
    public void testJSPTagTemplates() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        IFile file = getJspFile( "view.jsp" );

        ICompletionProposal[] proposals = getWebSevicesProposals( file, "aui:form" );
        assertNotNull( proposals );
        assertEquals( true, proposals.length > 0 );
        String[] expectedProposalStrings =
            { "actionURL - A portlet:actionURL tag with its most commonly used attributes",
                "actionURLForm - A portlet:actionURL tag and a aui:form",
                "aui-form - An aui:form tag with all of the most commonly used attributes",
                "currentURL - Create a jsp variable for currentURL" };

        for( String expectedProposalString : expectedProposalStrings )
        {
            assertTrue( "can't get proposal at "+ expectedProposalString , containsProposal( proposals, expectedProposalString, true ) );
        }
    }
}
