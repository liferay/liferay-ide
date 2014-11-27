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
 *******************************************************************************/

package com.liferay.ide.xml.search.ui.editor;

import org.eclipse.jface.text.Region;


/**
 * @author Kuo Zhang
 */
public class InfoRegion extends Region
{
    private String info;

    public InfoRegion( int offset, int length )
    {
        this( offset, length, null );
    }

    public InfoRegion( int offset, int length, String info )
    {
        super( offset, length );
        this.info = info;
    }

    public void setInfo( String info )
    {
        this.info = info;
    }

    public String getInfo()
    {
        return info;
    }

}
