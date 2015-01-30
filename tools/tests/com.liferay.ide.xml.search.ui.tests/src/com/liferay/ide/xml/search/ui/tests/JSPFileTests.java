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
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.deleteOtherProjects;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.setAttrValue;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.verifyQuickFix;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.xml.search.ui.AddResourceKeyMarkerResolution;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.junit.Test;

/**
 * @author Kuo Zhang
 * @author Terry Jia
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
        final IFile viewJspFile = getJspFile("view.jsp");
        assertNotNull( viewJspFile );

        final String elementName = "liferay-ui:message";
        final String attrName = "key";

        setAttrValue( viewJspFile, elementName, attrName, "Foo" );
        buildAndValidate( viewJspFile );

        final String markerType = XMLSearchConstants.LIFERAY_JSP_MARKER_ID;
        final String exceptedMessageRegex = "Property.*not found in.*";

        verifyQuickFix( viewJspFile, markerType, exceptedMessageRegex, AddResourceKeyMarkerResolution.class );
    }

    @Test
    public void testTagLiferayPortletParams() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile jspFile = getJspFile( "test-liferay-portlet-param.jsp" );
        assertNotNull( jspFile );

        final String liferayPortletParamElementName = "liferay-portlet:param";

        String[][] paramsArray =
        {
            { "<%= ActionRequest.ACTION_NAME %>", "sayHello1", "Type \"sayHello1\" not found.", "true", "0" },
            { "javax.portlet.action", "sayHello2", "Type \"sayHello2\" not found.", "true", "0" },
            { "bookName", "liferay-in-action", "", "false", "25" }
        };

        for( String[] params : paramsArray )
        {
            testTagParamValidation(
                jspFile, liferayPortletParamElementName, params[0], params[1], params[2], Boolean.valueOf( params[3] ),
                Integer.valueOf( params[4] ) );
        }
    }

    @Test
    public void testTagParams() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile jspFile = getJspFile( "test-param.jsp" );

        assertNotNull( jspFile );

        final String paramElementName = "param";

        String[][] paramsArray =
        {
            { "<%= ActionRequest.ACTION_NAME %>", "sayHello3", "", "false", "24" },
            { "javax.portlet.action", "sayHello4", "", "false", "24" },
            { "bookName", "liferay-in-action", "", "false", "24" }
        };

        for( String[] params : paramsArray )
        {
            testTagParamValidation(
                jspFile, paramElementName, params[0], params[1], params[2], Boolean.valueOf( params[3] ),
                Integer.valueOf( params[4] ) );
        }
    }

    @Test
    public void testTagPortletParams() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final IFile jspFile = getJspFile( "test-portlet-param.jsp" );

        assertNotNull( jspFile );

        final String portletParamElementName = "portlet:param";

        String[][] paramsArray =
        {
            { "<%= ActionRequest.ACTION_NAME %>", "sayHello5", "Type \"sayHello5\" not found.", "true", "0" },
            { "javax.portlet.action", "sayHello6", "Type \"sayHello6\" not found.", "true", "0" },
            { "bookName", "liferay-in-action", "", "false", "24" }
        };

        for( String[] params : paramsArray )
        {
            testTagParamValidation(
                jspFile, portletParamElementName, params[0], params[1], params[2], Boolean.valueOf( params[3] ),
                Integer.valueOf( params[4] ) );
        }
    }

    protected void testTagParamValidation(
        IFile file, String tagName, String nameAttr, String valueAttr, String exceptedMessageRegex,
        boolean exceptedFindWarning, int lineNumber ) throws Exception
    {
        final String markerType = XMLSearchConstants.LIFERAY_JSP_MARKER_ID;

        setAttrValue( file, tagName, "name", nameAttr );
        setAttrValue( file, tagName, "value", valueAttr );

        buildAndValidate( file );

        if( exceptedFindWarning )
        {
            assertEquals( true, checkMarkerByMessage( file, markerType, exceptedMessageRegex, true ) );
        }
        else
        {
            final IMarker[] markers = file.findMarkers( markerType, false, IResource.DEPTH_ZERO );

            if( markers.length > 0 )
            {
                for( IMarker marker : markers )
                {
                    assertNotEquals( lineNumber, marker.getAttribute( IMarker.LINE_NUMBER ) );
                }
            }
        }
    }

}
