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

package com.liferay.ide.ui.pref;

/**
 * @author Greg Amerson
 */
public class ComboData
{

    private int fIndex;

    private String fKey;

    private int[] fSeverities;

    int originalSeverity = -2;

    public ComboData( String key, int[] severities, int index )
    {
        fKey = key;
        fSeverities = severities;
        fIndex = index;
    }

    public int getIndex()
    {
        return fIndex;
    }

    public String getKey()
    {
        return fKey;
    }

    public int getSeverity()
    {
        return ( fIndex >= 0 && fSeverities != null && fIndex < fSeverities.length ) ? fSeverities[fIndex] : -1;
    }

    public void setIndex( int index )
    {
        fIndex = index;
    }

    /**
     * Sets the severity index based on <code>severity</code>. If the severity doesn't exist, the index is set to -1.
     * 
     * @param severity
     *            the severity level
     */
    public void setSeverity( int severity )
    {
        for( int i = 0; fSeverities != null && i < fSeverities.length; i++ )
        {
            if( fSeverities[i] == severity )
            {
                fIndex = i;
                return;
            }
        }

        fIndex = -1;
    }

    boolean isChanged()
    {
        return fSeverities[fIndex] != originalSeverity;
    }
}
