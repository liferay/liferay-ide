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

/**
 * @author Gregory Amerson
 */
public class MPTree
{
    MPNode root;
    MPNode commonRoot;

    public MPTree( MPNode root )
    {
        this.root = root;
        commonRoot = null;
    }

    public void addElement( String elementValue )
    {
        String[] list = elementValue.split( "/" );

        // latest element of the list is the filename.extrension
        root.addElement( root.incrementalPath, list );
    }

    public MPNode getCommonRoot()
    {
        if( commonRoot != null )
        {
            return commonRoot;
        }
        else
        {
            MPNode current = root;

            while( current.leafs.size() <= 0 && current.childs.size() == 1 )
            {
                current = current.childs.get( 0 );
            }

            commonRoot = current;

            return commonRoot;
        }

    }

}
