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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.NodeList;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LayoutTplUtil
{

    public static IDOMElement[] findChildElementsByClassName( IDOMElement parentElement,
                                                              String childElementTag,
                                                              String className )
    {
        if( parentElement == null || !( parentElement.hasChildNodes() ) )
        {
            return null;
        }

        List<IDOMElement> childElements = new ArrayList<IDOMElement>();

        NodeList divChildren = parentElement.getElementsByTagName( childElementTag );

        for( int i = 0; i < divChildren.getLength(); i++ )
        {
            IDOMElement childDivElement = (IDOMElement) divChildren.item( i );

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

}
