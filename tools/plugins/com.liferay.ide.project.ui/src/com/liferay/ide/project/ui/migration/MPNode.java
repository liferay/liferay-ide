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

package com.liferay.ide.project.ui.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public class MPNode
{

    List<MPNode> childs;
    String data;
    String incrementalPath;
    List<MPNode> leafs;

    public MPNode( String nodeValue, String incrementalPath )
    {
        childs = new ArrayList<MPNode>();
        leafs = new ArrayList<MPNode>();
        data = nodeValue;
        this.incrementalPath = incrementalPath;
    }

    public void addElement( String currentPath, String[] list )
    {
        // Avoid first element that can be an empty string if you split a string that has a starting slash as /sd/card/
        while( list[0] == null || list[0].equals( "" ) )
        {
            list = Arrays.copyOfRange( list, 1, list.length );
        }

        MPNode currentChild = new MPNode( list[0], currentPath + "/" + list[0] );

        if( list.length == 1 )
        {
            leafs.add( currentChild );

            return;
        }
        else
        {
            int index = childs.indexOf( currentChild );

            if( index == -1 )
            {
                childs.add( currentChild );
                currentChild.addElement( currentChild.incrementalPath, Arrays.copyOfRange( list, 1, list.length ) );
            }
            else
            {
                MPNode nextChild = childs.get( index );
                nextChild.addElement( currentChild.incrementalPath, Arrays.copyOfRange( list, 1, list.length ) );
            }
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj instanceof MPNode )
        {
            MPNode cmpObj = (MPNode) obj;
            return incrementalPath.equals( cmpObj.incrementalPath ) && data.equals( cmpObj.data );
        }
        else
        {
            return false;
        }
    }

    public boolean isLeaf()
    {
        return childs.isEmpty() && leafs.isEmpty();
    }

    @Override
    public String toString()
    {
        return data;
    }

}
