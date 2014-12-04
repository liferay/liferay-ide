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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.verifyQuickFix;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.xml.search.ui.AddResourceKeyMarkerResolution;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;


/**
 * @author Kuo Zhang
 */
public class JSPFileTests extends XmlSearchTestsBase
{

    private IProject project;

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "portlets", "Portlet-Xml-Test-portlet" );
            deleteOtherProjects( project );
        }

        return project;
    }

    private IFile getViewJspFile() throws Exception
    {
        final IFile file =  CoreUtil.getDefaultDocrootFolder( getProject() ).getFile( "view.jsp" );

        if( file != null && file.exists() )
        {
            return file;
        }

        return null;
    }

    @Test
    public void testMessageKey() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        testMessageKeyValidation();
        testMessageKeyContentAssist();
        testMessageKeyQuickFix();
    }

    // TODO
    public void testMessageKeyValidation()
    {
    }

    // TODO
    public void testMessageKeyContentAssist()
    {
    }

    // an example of testing quick fix
    protected void testMessageKeyQuickFix() throws Exception
    {
        final IFile viewJspFile = getViewJspFile();
        assertNotNull( viewJspFile );

        final String elementName = "liferay-ui:message";
        final String attrName = "key";

        setAttrValue( viewJspFile, elementName, attrName, "Foo" );
        buildAndValidate( viewJspFile );

        final String markerType = XMLSearchConstants.LIFERAY_JSP_MARKER_ID;
        final String exceptedMessageRegex = "Property.*not found in.*";

        verifyQuickFix( viewJspFile, markerType, exceptedMessageRegex, AddResourceKeyMarkerResolution.class );
    }
}
