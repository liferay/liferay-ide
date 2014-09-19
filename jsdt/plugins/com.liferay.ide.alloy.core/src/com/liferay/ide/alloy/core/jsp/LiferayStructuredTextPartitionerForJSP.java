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

package com.liferay.ide.alloy.core.jsp;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayStructuredTextPartitionerForJSP extends StructuredTextPartitionerForJSP
{

    @Override
    public String getPartitionType( ITextRegion region, int offset )
    {
        String retval = super.getPartitionType( region, offset );

        final IStructuredDocumentRegion sdRegion = this.fStructuredDocument.getRegionAtCharacterOffset( offset );

        if( sdRegion != null && sdRegion.getPrevious() != null &&
            sdRegion.getPrevious().toString().contains( "aui:script" ) )
        {
            retval = "org.eclipse.wst.html.SCRIPT";
        }

        return retval;
    }

    @Override
    public String getPartitionTypeBetween( IStructuredDocumentRegion previousNode, IStructuredDocumentRegion nextNode )
    {
        String retval = super.getPartitionTypeBetween( previousNode, nextNode );

        if( previousNode.toString().contains( "aui:script" ) )
        {
            retval = "org.eclipse.wst.html.SCRIPT";
        }

        return retval;
    }

    @Override
    public ITypedRegion getPartition( int offset )
    {
        // TODO Auto-generated method stub
        return super.getPartition( offset );
    }
}
