/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.cmd;

import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public class PortletColumnDeleteCommand extends Command
{
    protected PortletColumn adjustedColumn = null;
    protected int adjustedColumnWeight = 0;
    protected final PortletColumn child;
    protected LayoutTplDiagram diagram = null;
    protected final PortletLayout parent;
    protected int parentIndex = 0;
    protected boolean wasRemoved;

    public PortletColumnDeleteCommand( PortletLayout parent, PortletColumn child )
    {
        if( parent == null || child == null )
        {
            throw new IllegalArgumentException();
        }

        setLabel( Msgs.portletColumnDeleted );

        this.parent = parent;
        this.child = child;
    }

    public boolean canUndo()
    {
        return wasRemoved;
    }

    public void execute()
    {
        redo();
    }

    public void redo()
    {
        wasRemoved = parent.removeColumn( child );
        final int columnsNum = parent.getColumns().size();

        if( columnsNum == 0 )
        {
            diagram = (LayoutTplDiagram) parent.getParent();
            parentIndex = diagram.getRows().indexOf( parent );
            diagram.removeChild( parent );
        }
        else if( columnsNum == 1 )
        {
            adjustedColumn = ((PortletColumn) parent.getColumns().get( 0 ));
            adjustedColumnWeight = adjustedColumn.getWeight() + child.getWeight();
            adjustedColumn.setWeight( 100 );
        }
        else
        {
            //if there are 2 or more columns left, pick the right adjacent one only when there are more right remaining ones than the left,
            //otherwise pick the left one.
            final int childIndex = child.getNumId() - ( (PortletColumn) parent.getColumns().get( 0 ) ).getNumId();
            int adjustedColumnIndex = 0;

            if( childIndex < ( ( columnsNum + 1 ) / 2 ) )
            {
                adjustedColumnIndex = childIndex;
            }
            else
            {
                adjustedColumnIndex = childIndex - 1;
            }

            adjustedColumn = (PortletColumn) parent.getColumns().get( adjustedColumnIndex );
            adjustedColumn.setWeight( adjustedColumn.getWeight() + child.getWeight() );
            adjustedColumnWeight = adjustedColumn.getWeight();
        }
    }

    public void undo()
    {
        if( adjustedColumn != null )
        {
            adjustedColumn.setWeight( adjustedColumnWeight - child.getWeight() );
        }

        if( ! parent.getColumns().isEmpty() )
        {
            final int childIndex = child.getNumId() - ( (PortletColumn) parent.getColumns().get( 0 ) ).getNumId();
            parent.addColumn( child, childIndex );
        }
        else
        {
            parent.addColumn( child );
        }

        if( diagram != null )
        {
            diagram.addRow( parent, parentIndex );
        }
    }

    private static class Msgs extends NLS
    {
        public static String portletColumnDeleted;

        static
        {
            initializeMessages( PortletColumnDeleteCommand.class.getName(), Msgs.class );
        }
    }
}
