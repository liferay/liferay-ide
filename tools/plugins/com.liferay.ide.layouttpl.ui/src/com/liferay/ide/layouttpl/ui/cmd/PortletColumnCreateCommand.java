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
import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.ModelElement;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
public class PortletColumnCreateCommand extends Command
{
    protected LayoutTplDiagram diagram;
    protected LayoutConstraint layoutConstraint;
    protected PortletColumn newColumn;
    protected int refColumnOldWeight = 0;

    public PortletColumnCreateCommand( PortletColumn newColumn, LayoutTplDiagram diagram, LayoutConstraint constraint )
    {
        this.newColumn = newColumn;
        this.diagram = diagram;
        this.layoutConstraint = constraint;
        setLabel( Msgs.portletColumnAdded );
    }

    public boolean canExecute()
    {
        return newColumn != null && diagram != null && layoutConstraint != null;
    }

    public void execute()
    {
        redo();
    }

    public void redo()
    {
        if( layoutConstraint.equals( LayoutConstraint.EMPTY ) || layoutConstraint.newColumnIndex == -1 )
        {
            PortletLayout portletLayout = new PortletLayout();
            portletLayout.addColumn( newColumn );

            diagram.addRow( portletLayout, layoutConstraint.newRowIndex );
        }
        else if( layoutConstraint.rowIndex > -1 && layoutConstraint.newColumnIndex > -1 )
        {
            /* layoutConstraint.newRowIndex > -1 */

            if( layoutConstraint.refColumn != null )
            {
                refColumnOldWeight = layoutConstraint.refColumn.getWeight();
                layoutConstraint.refColumn.setWeight( layoutConstraint.weight );
            }

            newColumn.setWeight( layoutConstraint.weight );

            // get the row that the column will be inserted into
            ModelElement row = diagram.getRows().get( layoutConstraint.rowIndex );
            PortletLayout portletLayout = (PortletLayout) row;

            if( row != null )
            {
                portletLayout.addColumn( newColumn, layoutConstraint.newColumnIndex );
            }
        }
    }

    public void undo()
    {
        if( layoutConstraint.equals( LayoutConstraint.EMPTY ) || layoutConstraint.newColumnIndex == -1  )
        {
            for( ModelElement row : diagram.getRows() )
            {
                PortletLayout portletLayout = (PortletLayout) row;

                if( portletLayout.getColumns().size() == 1 && portletLayout.getColumns().get( 0 ).equals( newColumn ) )
                {
                    diagram.removeRow( portletLayout );
                    break;
                }
            }
        }
        else if( layoutConstraint.rowIndex > -1 && layoutConstraint.newColumnIndex > -1 )
        {
            if( layoutConstraint.refColumn != null )
            {
                layoutConstraint.refColumn.setWeight( refColumnOldWeight );
            }

            ModelElement row = diagram.getRows().get( layoutConstraint.rowIndex );
            PortletLayout portletLayout = (PortletLayout) row;

            if( row != null )
            {
                portletLayout.removeColumn( newColumn );
            }
        }
    }

    private static class Msgs extends NLS
    {
        public static String portletColumnAdded;

        static
        {
            initializeMessages( PortletColumnCreateCommand.class.getName(), Msgs.class );
        }
    }
}
