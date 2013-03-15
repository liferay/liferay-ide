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

package com.liferay.ide.layouttpl.ui.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.ui.model.ModelElement;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.layouttpl.ui.parts.LayoutTplDiagramEditPart;
import com.liferay.ide.layouttpl.ui.parts.PortletLayoutEditPart;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.NodeList;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class LayoutTplUtil
{

    public static IDOMElement[] findChildElementsByClassName(
        IDOMElement parentElement, String childElementTag, String className )
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

            if( LayoutTplUtil.hasClassName( childDivElement, className ) )
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

    public static int getColumnIndex( PortletLayout currentParent, PortletColumn column )
    {
        if( currentParent == null || column == null )
        {
            return -1;
        }

        List<ModelElement> cols = currentParent.getColumns();

        for( int i = 0; i < cols.size(); i++ )
        {
            if( column.equals( cols.get( i ) ) )
            {
                return i;
            }
        }

        return -1;
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

    public static int getRowIndex( PortletLayoutEditPart layoutEditPart )
    {
        if( layoutEditPart == null )
        {
            return -1;
        }

        LayoutTplDiagramEditPart diagramPart = (LayoutTplDiagramEditPart) layoutEditPart.getParent();
        Object[] rows = diagramPart.getChildren().toArray();

        for( int i = 0; i < rows.length; i++ )
        {
            if( layoutEditPart.equals( rows[i] ) )
            {
                return i;
            }
        }

        return -1;
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

    public static boolean isCreateRequest( Class<?> class1, Request request )
    {
        if( !( request instanceof CreateRequest ) )
        {
            return false;
        }

        if( !( ( (CreateRequest) request ).getNewObject().getClass() == class1 ) )
        {
            return false;
        }

        return true;
    }

    public static boolean isLayoutTplProject( IProject project )
    {
        return ProjectUtil.hasFacet( project, IPluginFacetConstants.LIFERAY_LAYOUTTPL_PROJECT_FACET );
    }

    public static int adjustWeight( int newWeight )
    {
        int retval = -1;

        // make sure that new weight is valid

        //use 35 instead of 34 because the 33 and 66 situations should be corresponding by a sum of 100
        //or when 66 is in 66, 34 is not in 33 but 35
        if( newWeight > 31 && newWeight < 35 )
        {
            retval = 33;
        }
        else if( newWeight > 65 && newWeight < 69 )
        {
            retval = 66;
        }
        else
        {
            retval = (int) Math.round( (double) newWeight / (double) 5 ) * 5;
        }

        return retval;
    }

}
