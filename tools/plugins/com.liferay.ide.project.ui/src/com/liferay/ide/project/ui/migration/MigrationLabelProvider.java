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

import blade.migrate.api.Problem;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

/**
 * @author Gregory Amerson
 */
public class MigrationLabelProvider extends StyledCellLabelProvider
{

    @Override
    public void update( ViewerCell cell )
    {
        Object element = cell.getElement();
        StyledString text = new StyledString();

        if( element instanceof String )
        {
            text.append( element.toString() );
        }
        if( element instanceof MigrationTask )
        {
            final MigrationTask task = (MigrationTask) element;

            text.append( "Migration Task - " + new SimpleDateFormat().format( new Date(task.getTimestamp() ) ) );
        }
        else if( element instanceof Problem )
        {
            text.append( (String) ( (Problem) element ).title );
        }

        cell.setText( text.toString() );
        cell.setStyleRanges( text.getStyleRanges() );

        super.update( cell );
    }
}
