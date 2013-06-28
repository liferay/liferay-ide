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

import com.liferay.ide.layouttpl.core.model.PortletRowLayoutElement;
import com.liferay.ide.layouttpl.ui.model.LayoutConstraint;
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
    protected PortletRowLayoutElement rowLayout;
    protected LayoutConstraint layoutConstraint;
    protected PortletLayout newLayout;

    public PortletLayoutCreateCommand( PortletLayout newLayout, PortletRowLayoutElement rowLayout, LayoutConstraint constraint )
    {
        this.newLayout = newLayout;
        this.rowLayout = rowLayout;
        this.layoutConstraint = constraint;
        setLabel( Msgs.portletRowAdded );
    }

    public boolean canExecute()
    {
        return newLayout != null && rowLayout != null && layoutConstraint != null;
    }

    public void execute()
    {
        redo();
    }

    public void redo()
    {
        if( newLayout.getColumns().size() == 0 )
        {
            PortletColumn newColumn = new PortletColumn( 100 );
            newLayout.addColumn( newColumn );
        }

        rowLayout.addRow( newLayout, layoutConstraint.newRowIndex );
    }

    public void undo()
    {
        rowLayout.removeRow( newLayout );
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
