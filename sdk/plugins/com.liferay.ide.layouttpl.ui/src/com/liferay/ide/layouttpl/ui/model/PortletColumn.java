/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.ui.model;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.ui.util.LayoutTplUtil;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class PortletColumn extends ModelElement
{
    public static final int DEFAULT_WEIGHT = -1;
    public static final String WEIGHT_PROP = "PortletColumn.weight";

    // public static final String SIZE_PROP = "PortletColumn.size";
    // public static final String LOCATION_PROP = "PortletColumn.location";

    protected static IPropertyDescriptor[] descriptors;

    static
    {
        descriptors = new IPropertyDescriptor[] {
        // id and description pair
        new TextPropertyDescriptor( WEIGHT_PROP, "Weight" ), };

        // use a custom cell editor validator for all four array entries
        for( int i = 0; i < descriptors.length; i++ )
        {
            ( (PropertyDescriptor) descriptors[i] ).setValidator( new ICellEditorValidator()
            {

                public String isValid( Object value )
                {
                    int intValue = -1;
                    try
                    {
                        intValue = Integer.parseInt( (String) value );
                    }
                    catch( NumberFormatException exc )
                    {
                        return "Not a number";
                    }
                    return ( intValue >= 0 ) ? null : "Value must be >=  0";
                }
            } );
        }
    }

    public static PortletColumn createFromElement( IDOMElement portletColumnElement )
    {
        if( portletColumnElement == null )
        {
            return null;
        }

        PortletColumn newPortletColumn = new PortletColumn();

        String existingClassName = portletColumnElement.getAttribute( "class" );
        if( ( !CoreUtil.isNullOrEmpty( existingClassName ) ) && existingClassName.contains( "portlet-column" ) )
        {
            newPortletColumn.setClassName( existingClassName );
        }
        else
        {
            newPortletColumn.setClassName( "portlet-column" );
        }

        newPortletColumn.setWeight( LayoutTplUtil.getWeightValue( portletColumnElement, -1 ) );

        return newPortletColumn;
    }

    protected String className;
    protected boolean first = false;
    protected boolean last = false;
    protected int numId = 0;
    protected int weight;

    public PortletColumn()
    {
        this( DEFAULT_WEIGHT, "portlet-column" );
    }

    public PortletColumn( int weight )
    {
        this( weight, "portlet-column" );
    }

    public PortletColumn( int weight, String className )
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

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        return descriptors;
    }

    public Object getPropertyValue( Object propertyId )
    {
        if( WEIGHT_PROP.equals( propertyId ) )
        {
            if( getWeight() == DEFAULT_WEIGHT )
            {
                return "100%";
            }
            else
            {
                return Integer.toString( getWeight() ) + "%";
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
            String val = value.toString().replaceAll( "%", "" );
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
