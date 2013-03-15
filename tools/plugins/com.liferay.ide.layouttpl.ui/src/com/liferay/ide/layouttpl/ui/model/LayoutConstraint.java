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

/**
 * @author Gregory Amerson
 */
public class LayoutConstraint
{
    public static final LayoutConstraint EMPTY = new LayoutConstraint();

    public int rowIndex = -1;
    public int newRowIndex = -1;
    public int newColumnIndex = -1;
    public int weight = PortletColumn.DEFAULT_WEIGHT;
    public PortletColumn refColumn = null;

    @Override
    public boolean equals( Object obj )
    {
        if( !( obj instanceof LayoutConstraint ) )
        {
            return false;
        }

        LayoutConstraint constraint = (LayoutConstraint) obj;

        return this.rowIndex == constraint.rowIndex && this.newRowIndex == constraint.newRowIndex &&
            this.newColumnIndex == constraint.newColumnIndex && this.weight == constraint.weight &&
            this.refColumn == constraint.refColumn;
    }

    @Override
    public String toString()
    {
        if( this.equals( EMPTY ) )
        {
            return "LayoutConstraint { EMPTY }"; //$NON-NLS-1$
        }
        else
        {
            return "LayoutConstraint { rowIndex = " + rowIndex + ", newRowIndex = " + newRowIndex + //$NON-NLS-1$ //$NON-NLS-2$
                ", newColumnIndex = " + newColumnIndex + ", weight = " + weight + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

}
