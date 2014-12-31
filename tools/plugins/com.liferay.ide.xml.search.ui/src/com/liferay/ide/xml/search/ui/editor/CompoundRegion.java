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
import java.util.Iterator;
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

    public final int textOffset;
    public final ITextViewer textViewer;

    public CompoundRegion( ITextViewer textViewer, int textOffset )
    {
        this.textViewer = textViewer;
        this.textOffset = textOffset;
    }

    public void addRegion( IRegion region )
    {
        Iterator<IRegion> it = regions.iterator();

        IRegion r;

        while( it.hasNext() )
        {
            r = it.next();

            if( r.equals( region ) )
            {
                return;
            }

            // In jsp editor, TempMarker and TempRegion are used,
            // but MarkerRegion still have a higher priority,
            // if MarkerRegion and TemporaryRegion represent the same problem, keep the MarkerRegion
            if( region instanceof TemporaryRegion && r instanceof MarkerRegion )
            {
                if( compareRegions( (MarkerRegion) r, (TemporaryRegion) region ) )
                {
                    return;
                }
            }

            if( region instanceof MarkerRegion && r instanceof TemporaryRegion )
            {
                if( compareRegions( (MarkerRegion) region, (TemporaryRegion) r ) )
                {
                    it.remove();
                }
            }
        }

        regions.add( region );
        int start = Math.min( region.getOffset(), offset );
        int end = Math.max( region.getOffset() + region.getLength(), offset + length );
        offset = start;
        length = end - start;
    }

    // Compare MarkerRegion and TemporayRegion
    private boolean compareRegions( MarkerRegion m, TemporaryRegion t )
    {
        try
        {
            if( m.getLength() == t.getLength() && m.getOffset() == t.getOffset() &&
                m.getAnnotation().getText().equals( t.getAnnotation().getText() ) )
            {
                return true;
            }
        }
        catch( NullPointerException e )
        {
            return false;
        }

        return false;
    }

    public int getLength()
    {
        return length;
    }

    public int getOffset()
    {
        return offset;
    }

    public List<IRegion> getRegions()
    {
        return regions;
    }
}
