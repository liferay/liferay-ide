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

package com.liferay.ide.layouttpl.core.model;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class PortletColumnElement extends PortletRowLayoutElement
{
    public static final int DEFAULT_WEIGHT = -1;
    public static final String WEIGHT_PROP = "PortletColumn.weight"; //$NON-NLS-1$

    // public static final String SIZE_PROP = "PortletColumn.size";
    // public static final String LOCATION_PROP = "PortletColumn.location";

    public static PortletColumnElement createFromElement( IDOMElement portletColumnElement, ILayoutTplDiagramFactory factory )
    {
        if( portletColumnElement == null )
        {
            return null;
        }

        PortletColumnElement newPortletColumn = factory.newPortletColumn();

        String existingClassName = portletColumnElement.getAttribute( "class" ); //$NON-NLS-1$

        if( ( !CoreUtil.isNullOrEmpty( existingClassName ) ) && existingClassName.contains( "portlet-column" ) ) //$NON-NLS-1$
        {
            newPortletColumn.setClassName( existingClassName );
        }
        else
        {
            newPortletColumn.setClassName( "portlet-column" ); //$NON-NLS-1$
        }

        newPortletColumn.setWeight( LayoutTplUtil.getWeightValue( portletColumnElement, -1 ) );

        IDOMElement[] portletLayoutElements =
            LayoutTplUtil.findChildElementsByClassName( portletColumnElement, "div", "portlet-layout" ); //$NON-NLS-1$ //$NON-NLS-2$

        if( !CoreUtil.isNullOrEmpty( portletLayoutElements ) )
        {
            for( IDOMElement portletLayoutElement : portletLayoutElements )
            {
                PortletLayoutElement newPortletLayout = factory.newPortletLayoutFromElement( portletLayoutElement );
                newPortletColumn.addRow( newPortletLayout );
            }
        }

        return newPortletColumn;
    }

    protected String className;
    protected boolean first = false;
    protected boolean last = false;
    protected int numId = 0;
    protected int weight;

    public PortletColumnElement()
    {
        this( DEFAULT_WEIGHT, "portlet-column" ); //$NON-NLS-1$
    }

    public PortletColumnElement( int weight )
    {
        this( weight, "portlet-column" ); //$NON-NLS-1$
    }

    public PortletColumnElement( int weight, String className )
    {
        super();

        this.weight = weight;
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }

    public int getNumId()
    {
        return numId;
    }

    public Object getPropertyValue( Object propertyId )
    {
        if( WEIGHT_PROP.equals( propertyId ) )
        {
            if( getWeight() == DEFAULT_WEIGHT )
            {
                return "100%"; //$NON-NLS-1$
            }
            else
            {
                return Integer.toString( getWeight() ) + "%"; //$NON-NLS-1$
            }
        }

        return super.getPropertyValue( propertyId );
    }

    public int getWeight()
    {
        return weight;
    }

    public boolean isFirst()
    {
        return first;
    }

    public boolean isLast()
    {
        return last;
    }

    @Override
    public void removeChild( ModelElement child )
    {
    }

    public void setClassName( String className )
    {
        this.className = className;
    }

    public void setFirst( boolean first )
    {
        this.first = first;
    }

    public void setLast( boolean last )
    {
        this.last = last;
    }

    public void setNumId( int numId )
    {
        this.numId = numId;
    }

    public void setPropertyValue( Object propertyId, Object value )
    {
        if( WEIGHT_PROP.equals( propertyId ) )
        {
            String val = value.toString().replaceAll( "%", StringPool.EMPTY ); //$NON-NLS-1$
            int weight = Integer.parseInt( val );
            setWeight( weight );
        }
        else
        {
            super.setPropertyValue( propertyId, value );
        }
    }

    public void setWeight( int weight )
    {
        int oldValue = this.weight;
        this.weight = weight;
        firePropertyChange( WEIGHT_PROP, oldValue, this.weight );
    }

}
