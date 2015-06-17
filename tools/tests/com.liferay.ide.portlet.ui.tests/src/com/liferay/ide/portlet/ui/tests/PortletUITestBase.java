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

package com.liferay.ide.portlet.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.getDOMModel;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Li Lu
 */
@SuppressWarnings( { "restriction" } )
public class PortletUITestBase extends ProjectCoreBase
{

    public boolean checkFileExist( IProject project, String fileName, String path )
    {
        IFolder docroot = CoreUtil.getDefaultDocrootFolder( project );

        if( docroot.getFolder( path ).exists() )
        {
            return docroot.getFolder( path ).getFile( fileName ).exists();
        }

        return docroot.getFile( fileName ).exists();
    }

    public boolean containPropertyDescriptor( DataModelPropertyDescriptor[] properties, String expectedValue )
    {
        boolean flag = false;
        for( DataModelPropertyDescriptor property : properties )
        {
            if( property.getPropertyDescription().equals( expectedValue ) )
            {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public IProject createProject( String projectName, PluginType pluginType, String portletFramework )
        throws Exception
    {
        IProject[] projects = CoreUtil.getWorkspaceRoot().getProjects();
        if( projects != null )
        {
            for( IProject project : projects )
            {
                if( project.exists() && project.getName().contains( projectName ) )
                {
                    return project;
                }
            }
        }

        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( pluginType );
        op.setPortletFramework( portletFramework );
        op.setIncludeSampleCode( false );
        return createAntProject( op );
    }

    public boolean checkFileHasContent( IFile file, String elementName, String attrName, String matchString )
        throws Exception
    {
        String contents[] = getElementContent( file, elementName, attrName );
        for( String content : contents )
        {
            if( content.equals( matchString ) )
            {
                return true;
            }
        }
        return false;

    }

    public static String[] getElementContent( IFile file, String elementName, String attrName ) throws Exception
    {
        final IDOMModel domModel = getDOMModel( file, true );
        final IDOMDocument document = domModel.getDocument();
        final NodeList elements = document.getElementsByTagName( elementName );
        assertNotNull( elements );

        String[] contents = new String[elements.getLength()];
        for( int i = 0; i < elements.getLength(); i++ )
        {
            Element element = (Element) elements.item( i );

            if( attrName != null && element.hasAttribute( attrName ) )
                contents[i] = element.getAttribute( attrName );
            else
                contents[i] = element.getFirstChild().getNodeValue();
        }
        return contents;
    }
}
