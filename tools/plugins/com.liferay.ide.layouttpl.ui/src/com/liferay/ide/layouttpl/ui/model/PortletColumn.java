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

package com.liferay.ide.layouttpl.ui.model;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class PortletColumn extends PortletColumnElement implements IPropertySource
{
    protected static IPropertyDescriptor[] descriptors;

    static
    {
        descriptors = new IPropertyDescriptor[] {
        // id and description pair
        new TextPropertyDescriptor( WEIGHT_PROP, "Weight" ), }; //$NON-NLS-1$

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
                        intValue = Integer.parseInt( ((String) value).replaceAll( "%", StringPool.EMPTY ) ); //$NON-NLS-1$
                    }
                    catch( NumberFormatException exc )
                    {
                        return "Not a number"; //$NON-NLS-1$
                    }
                    return ( intValue >= 0 ) ? null : "Value must be >=  0"; //$NON-NLS-1$
                }
            } );
        }
    }

    public PortletColumn( int i )
    {
        super( i );
    }

    public PortletColumn()
    {
        super();
    }

    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        return descriptors;
    }


}
