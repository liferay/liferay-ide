/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class PortletLayoutCreateCommand extends Command
{
    protected LayoutTplDiagram diagram;
    protected LayoutConstraint layoutConstraint;
    protected PortletLayout newLayout;

    public PortletLayoutCreateCommand( PortletLayout newLayout, LayoutTplDiagram diagram, LayoutConstraint constraint )
    {
        this.newLayout = newLayout;
        this.diagram = diagram;
        this.layoutConstraint = constraint;
        setLabel( Msgs.portletRowAdded );
    }

    public boolean canExecute()
    {
        return newLayout != null && diagram != null && layoutConstraint != null;
    }

    public void execute()
    {
        redo();
    }

    public void redo()
    {
        if( newLayout.getColumns().size() == 0 )
        {
            PortletColumn newColumn = new PortletColumn();
            newLayout.addColumn( newColumn );
        }

        diagram.addRow( newLayout, layoutConstraint.newRowIndex );
    }

    public void undo()
    {
        diagram.removeRow( newLayout );
    }

    private static class Msgs extends NLS
    {
        public static String portletRowAdded;

        static
        {
            initializeMessages( PortletLayoutCreateCommand.class.getName(), Msgs.class );
        }
    }
}
