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

import com.liferay.blade.api.Problem;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * @author Lovett li
 */
public class MigratorComparator extends ViewerComparator
{

    private int columnIndex;
    private static final int DESCENDING = 1;
    private int direction = DESCENDING;

    public MigratorComparator()
    {
        this.columnIndex = 1;
        direction = DESCENDING;
    }

    public int getDirection()
    {
        return direction == 1 ? SWT.DOWN : SWT.UP;
    }

    public void setColumn( int column )
    {
        if( column == this.columnIndex )
        {
            direction = 1 - direction;
        }
        else
        {
            this.columnIndex = column;
            direction = DESCENDING;
        }
    }

    @Override
    public int compare( Viewer viewer, Object e1, Object e2 )
    {
        final Problem t1 = (Problem) e1;
        final Problem t2 = (Problem) e2;
        int flag = 0;

        switch( columnIndex )
        {
        case 0:
            flag = ( t1.getStatus() == Problem.STATUS_RESOLVED ) ? 1 : -1;
            break;

        case 1:
            if( t1.getLineNumber() == t2.getLineNumber() )
            {
                flag = 0;
            }
            else
            {
                flag = t1.getLineNumber() > t2.getLineNumber() ? 1 : -1;
            }
            break;

        case 2:
            flag = t1.getTitle().compareTo( t2.getTitle() );
            break;

        default:
            flag = 0;
        }

        if( direction != DESCENDING )
        {
            flag = -flag;
        }

        return flag;
    }
}
