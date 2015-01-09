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
 *
 *******************************************************************************/

package com.liferay.ide.xml.search.ui.editor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;

/**
 * @author Kuo Zhang
 *
 * the region used to locate TemporaryAnnotation
 */
@SuppressWarnings( "restriction" )
public class TemporaryRegion implements IRegion
{

    private final TemporaryAnnotation annotation;
    private final int length;
    private final int offset;

    public TemporaryRegion( int offset, int length, TemporaryAnnotation applicable )
    {
        this.offset = offset;
        this.length = length;
        this.annotation = applicable;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( !( obj instanceof TemporaryRegion ) )
        {
            return false;
        }

        TemporaryRegion compared = (TemporaryRegion) obj;

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

    public TemporaryAnnotation getAnnotation()
    {
        return annotation;
    }

    public int getLength()
    {
        return length;
    }

    public int getOffset()
    {
        return offset;
    }
}
