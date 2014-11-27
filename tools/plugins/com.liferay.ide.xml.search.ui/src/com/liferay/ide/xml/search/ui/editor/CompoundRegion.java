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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

/**
 * @author Kuo Zhang
 */
public class CompoundRegion implements IRegion
{

    private int length = Integer.MIN_VALUE;

    private int offset = Integer.MAX_VALUE;

    private List<IRegion> regions = new ArrayList<IRegion>();

    public final ITextViewer textViewer;

    public final int textOffset;

    public CompoundRegion( ITextViewer textViewer, int textOffset )
    {
        this.textViewer = textViewer;
        this.textOffset = textOffset;
    }

    public int getLength()
    {
        return length;
    }

    public int getOffset()
    {
        return offset;
    }

    public void addRegion( IRegion region )
    {
        if( regions.contains( region ) )
        {
            return;
        }

        regions.add( region );
        int start = Math.min( region.getOffset(), offset );
        int end = Math.max( region.getOffset() + region.getLength(), offset + length );
        offset = start;
        length = end - start;
    }

    public List<IRegion> getRegions()
    {
        return regions;
    }
}
