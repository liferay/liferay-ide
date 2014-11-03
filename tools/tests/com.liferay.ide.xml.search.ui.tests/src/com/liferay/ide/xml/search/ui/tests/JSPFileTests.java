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

import static org.junit.Assert.assertNotNull;
import static com.liferay.ide.xml.search.ui.tests.XmlSearchTestsUtils.*;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.xml.search.ui.AddResourceKeyMarkerResolution;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;


/**
 * @author Kuo Zhang
 * 
 */
public class JSPFileTests extends XmlSearchTestsBase
{

    @Test
    public void testMessageKey() throws Exception
    {
        testMessageKeyQuickFix();
    }

    // an example of testing quick fix
    protected void testMessageKeyQuickFix() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        IProject project = getProject( "portlets", "Portlet-Xml-Test-portlet" );
        IFile viewJspFile = getViewJspFile( project );
        assertNotNull( viewJspFile );

        openEditor( viewJspFile );

        final String markerType = XMLSearchConstants.LIFERAY_JSP_MARKER_ID;
        final String exceptedMessageRegex = "Property.*not found in.*";

        Class< ? extends IMarkerResolution > resolutionClazz = AddResourceKeyMarkerResolution.class;
        verifyQuickFix( viewJspFile, markerType, exceptedMessageRegex, resolutionClazz );
    }

    private IFile getViewJspFile( IProject project ) throws Exception
    {
        final IFile file =  CoreUtil.getDefaultDocrootFolder( project ).getFile( "view.jsp" );

        if( file != null && file.exists() )
        {
            return file;
        }

        return null;
    }
}
