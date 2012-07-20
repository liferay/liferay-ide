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

/**
 * @author Gregory Amerson
 */
public class PortletColumnDeleteCommand extends Command
{
    protected final PortletColumn child;
    protected final PortletLayout parent;
    protected LayoutTplDiagram diagram = null;
    protected boolean wasRemoved;

    public PortletColumnDeleteCommand( PortletLayout parent, PortletColumn child )
    {
        if( parent == null || child == null )
        {
            throw new IllegalArgumentException();
        }

        setLabel( "Portlet Column deleted" );

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

        if( parent.getColumns().size() == 0 )
        {
            diagram = (LayoutTplDiagram) parent.getParent();
            diagram.removeChild( parent );
        }
    }

    public void undo()
    {
        parent.addColumn( child );

        if( diagram != null )
        {
            diagram.addRow( parent );
        }
    }
}
