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

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * @author Kuo Zhang
 */
public class MarkerRegion implements IRegion
{

    private final MarkerAnnotation annotation;

    private final int offset;

    private final int length;

    public MarkerRegion( int offset, int length, MarkerAnnotation applicable )
    {
        this.offset = offset;
        this.length = length;
        this.annotation = applicable;
    }

    public int getLength()
    {
        return length;
    }

    public int getOffset()
    {
        return offset;
    }

    public MarkerAnnotation getAnnotation()
    {
        return annotation;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( !( obj instanceof MarkerRegion ) )
        {
            return false;
        }

        MarkerRegion compared = (MarkerRegion) obj;

        if( this.length == compared.length &&
            this.offset == compared.offset &&
            this.annotation.getText() != null &&
            compared.annotation.getText() != null &&
            this.annotation.getText().equals( compared.annotation.getText() ) )
        {
            return true;
        }

        return super.equals( obj );
    }
}
