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
package com.liferay.ide.layouttpl.core.util;

import com.liferay.ide.core.templates.ITemplateContext;
import com.liferay.ide.core.templates.ITemplateOperation;
import com.liferay.ide.core.templates.TemplatesCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.ArrayStack;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class LayoutTplUtil
{

    private static void createLayoutTplContext( ITemplateOperation op, LayoutTplElement layouttpl )
    {
        final ITemplateContext ctx = op.getContext();

        ctx.put( "root", layouttpl );
        ctx.put( "stack", new ArrayStack() );
    }

    public static IDOMElement[] findChildElementsByClassName( IDOMElement parentElement,
                                                              String childElementTag,
                                                              String className )
    {
        if( parentElement == null || !( parentElement.hasChildNodes() ) )
        {
            return null;
        }

        List<IDOMElement> childElements = new ArrayList<IDOMElement>();

        List<Element> divChildren = getChildElementsByTagName( parentElement, childElementTag );

        for( int i = 0; i < divChildren.size(); i++ )
        {
            IDOMElement childDivElement = (IDOMElement) divChildren.get( i );

            if( hasClassName( childDivElement, className ) )
            {
                childElements.add( childDivElement );
            }
        }

        return childElements.toArray( new IDOMElement[0] );
    }

    public static IDOMElement findMainContentElement( IDOMDocument rootDocument )
    {
        if( rootDocument == null || !( rootDocument.hasChildNodes() ) )
        {
            return null;
        }

        IDOMElement mainContentElement = null;

        mainContentElement = (IDOMElement) rootDocument.getElementById( "main-content" ); //$NON-NLS-1$

        return mainContentElement;
    }

    public static List<Element> getChildElementsByTagName( IDOMElement parentElement, String childElementTag )
    {
        final NodeList childNodes = ( (Node) parentElement ).getChildNodes();

        List<Element> childElements = new ArrayList<Element>();

        for( int i = 0; i < childNodes.getLength(); i++)
        {
            Node childNode = childNodes.item( i );

            if( childNode.getNodeType() == 1 && childElementTag != null )
            {
                Element element = (Element) childNode;

                if( element.getTagName().equals( childElementTag ) )
                {
                    childElements.add( element );
                }
            }
        }

        return childElements;
    }

    public static String getRoleValue( IDOMElement mainContentElement, String defaultValue )
    {
        String retval = defaultValue;
        String currentRoleValue = mainContentElement.getAttribute( "role" ); //$NON-NLS-1$

        if( !CoreUtil.isNullOrEmpty( currentRoleValue ) )
        {
            retval = currentRoleValue;
        }

        return retval;
    }

    public static String getTemplateSource( LayoutTplElement layouttpl )
    {
        final StringBuffer buffer = new StringBuffer();

        try
        {
            ITemplateOperation templateOperation = null;

            if( layouttpl.getBootstrapStyle().content() )
            {
                templateOperation =
                    TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.layoutTemplate.bootstrap" );
            }
            else
            {
                templateOperation =
                    TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.layoutTemplate.legacy" );
            }

            createLayoutTplContext( templateOperation, layouttpl );

            templateOperation.setOutputBuffer( buffer );
            templateOperation.execute( new NullProgressMonitor() );
        }
        catch( Exception ex )
        {
            LayoutTplCore.logError( "Error getting template source.", ex ); //$NON-NLS-1$
        }

        return buffer.toString();
    }

    public static int getWeightValue( IDOMElement portletColumnElement, int defaultValue )
    {
        int weightValue = defaultValue;

        if( portletColumnElement == null )
        {
            return weightValue;
        }

        String classAttr = portletColumnElement.getAttribute( "class" ); //$NON-NLS-1$

        if( CoreUtil.isNullOrEmpty( classAttr ) )
        {
            return weightValue;
        }

        // resolve column weight of bootstrap style, portal version equal to 62
        Matcher matcher = Pattern.compile( "(.*span)(\\d+)" ).matcher( classAttr );

        if( matcher.matches() )
        {
            String weightString = matcher.group( 2 );

            if( !CoreUtil.isNullOrEmpty( weightString ) )
            {
                try
                {
                    weightValue = Integer.parseInt( weightString );
                }
                catch( NumberFormatException ex )
                {
                    weightValue = 0;
                }
            }
        }
        else
        {
            matcher = Pattern.compile( ".*col-md-(\\d+).*" ).matcher( classAttr );

            if( matcher.matches() )
            {
                String weightString = matcher.group( 1 );

                if( !CoreUtil.isNullOrEmpty( weightString ) )
                {
                    try
                    {
                        weightValue = Integer.parseInt( weightString );
                    }
                    catch( NumberFormatException ex )
                    {
                        weightValue = 0;
                    }
                }
            }
        }

        return weightValue;
    }

    public static boolean hasClassName( IDOMElement domElement, String className )
    {
        boolean retval = false;

        if( domElement != null )
        {
            String classAttr = domElement.getAttribute( "class" ); //$NON-NLS-1$

            if( !CoreUtil.isNullOrEmpty( classAttr ) )
            {
                retval = classAttr.contains( className );
            }
        }

        return retval;
    }

    public static void saveToFile( LayoutTplElement diagramElement, IFile file, IProgressMonitor monitor )
    {
        try
        {
            ITemplateOperation op = null;

            if( diagramElement.getBootstrapStyle().content() )
            {
                op = TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.layoutTemplate.bootstrap" );
            }
            else
            {
                op = TemplatesCore.getTemplateOperation( "com.liferay.ide.layouttpl.core.layoutTemplate.legacy" );
            }

            createLayoutTplContext( op, diagramElement );

            op.setOutputFile( file );
            op.execute( monitor );
        }
        catch( Exception e )
        {
            LayoutTplCore.logError( e );
        }
    }
}
