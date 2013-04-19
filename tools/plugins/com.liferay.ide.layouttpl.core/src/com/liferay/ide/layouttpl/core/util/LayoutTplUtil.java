/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramElement;
import com.liferay.ide.templates.core.ITemplateContext;
import com.liferay.ide.templates.core.ITemplateOperation;
import com.liferay.ide.templates.core.TemplatesCore;

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
 */
@SuppressWarnings( "restriction" )
public class LayoutTplUtil
{

    public static void configContext( ITemplateContext ctx, LayoutTplDiagramElement tplDiagramElement, String templateName )
    {
        ctx.put( "root", tplDiagramElement ); //$NON-NLS-1$
        ctx.put( "templateName", templateName ); //$NON-NLS-1$
        ctx.put( "stack", new ArrayStack() ); //$NON-NLS-1$
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

    public static String getTemplateSource( LayoutTplDiagramElement diagram, String templateName )
    {
        ITemplateOperation templateOperation = TemplatesCore.getTemplateOperation( "layouttpl.tpl" ); //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer();
        templateOperation.setOutputBuffer( buffer );
        ITemplateContext ctx = templateOperation.getContext();
        configContext( ctx, diagram, templateName );

        try
        {
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

        Pattern pattern = Pattern.compile( ".*aui-w([-\\d]+).*" ); //$NON-NLS-1$
        Matcher matcher = pattern.matcher( classAttr );

        if( matcher.matches() )
        {
            String weightString = matcher.group( 1 );

            if( !CoreUtil.isNullOrEmpty( weightString ) )
            {
                try
                {
                    weightValue = Integer.parseInt( weightString );
                }
                catch( NumberFormatException e )
                {
                    // if we have a 1-2 then we have a fraction
                    int index = weightString.indexOf( '-' );

                    if( index > 0 )
                    {
                        try
                        {
                            int numerator = Integer.parseInt( weightString.substring( 0, index ) );
                            int denominator =
                                Integer.parseInt( weightString.substring( index + 1, weightString.length() ) );
                            weightValue = (int) ( (float) numerator / denominator * 100 );
                        }
                        catch( NumberFormatException ex )
                        {
                            // best effort
                        }
                    }
                }
            }

            int remainder = weightValue % 5;

            if( remainder != 0 )
            {
                if( weightValue != 33 && weightValue != 66 )
                {
                    if( remainder < 3 )
                    {
                        weightValue -= remainder;
                    }
                    else
                    {
                        weightValue += remainder;
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

    public static void saveToFile( LayoutTplDiagramElement diagram, IFile file, IProgressMonitor monitor )
    {
        ITemplateOperation templateOperation = TemplatesCore.getTemplateOperation( "layouttpl.tpl" ); //$NON-NLS-1$
        templateOperation.setOutputFile( file );

        try
        {
            ITemplateContext ctx = templateOperation.getContext();
            String name = file.getFullPath().removeFileExtension().lastSegment();
            configContext( ctx, diagram, name );
            templateOperation.execute( monitor );
        }
        catch( Exception e )
        {
            LayoutTplCore.logError( e );
        }
    }

}
