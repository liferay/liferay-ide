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

import com.liferay.ide.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.layouttpl.ui.util.LayoutTplUtil;

import org.eclipse.gef.commands.Command;

/**
 * @author Greg Amerson
 */
public class PortletColumnChangeConstraintCommand extends Command
{

    protected PortletColumn column;
    protected PortletLayout currentParent;
    protected PortletLayout newParent;
    protected LayoutConstraint layoutConstraint;

    public PortletColumnChangeConstraintCommand(
        PortletColumn column, PortletLayout currentParent, PortletLayout newParent, LayoutConstraint constraint )
    {
        this.column = column;
        this.currentParent = currentParent;
        this.newParent = newParent;
        this.layoutConstraint = constraint;
        setLabel( "Portlet column changed" );
    }

    public boolean canExecute()
    {
        return column != null && currentParent != null && newParent != null && layoutConstraint != null;
    }

    public void execute()
    {
        redo();
    }

    public void redo()
    {
        if( currentParent.equals( newParent ) )
        {
            int currentColumnIndex = LayoutTplUtil.getColumnIndex( currentParent, column );

            if( currentColumnIndex != layoutConstraint.newColumnIndex )
            {
            }

            int existingWeight = column.getWeight();
            column.setWeight( layoutConstraint.weight );
            int diffWeight = existingWeight - layoutConstraint.weight;

            PortletColumn refColumn = layoutConstraint.refColumn;
            int newWeight = refColumn.getWeight() + diffWeight;

            newWeight = LayoutTplUtil.adjustWeight( newWeight );

            refColumn.setWeight( newWeight );
        }
    }

    public void undo()
    {
        System.out.println( "UNDO" );
    }

}
