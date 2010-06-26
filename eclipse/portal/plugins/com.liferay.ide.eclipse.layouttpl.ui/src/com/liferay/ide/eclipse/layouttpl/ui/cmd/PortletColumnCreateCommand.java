/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.layouttpl.ui.cmd;

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.commands.Command;

public class PortletColumnCreateCommand extends Command {

	protected PortletColumn newColumn;

	protected LayoutTplDiagram diagram;
	
	protected LayoutConstraint layoutConstraint;

	public PortletColumnCreateCommand(PortletColumn newColumn, LayoutTplDiagram diagram, LayoutConstraint constraint) {
		this.newColumn = newColumn;
		this.diagram = diagram;
		this.layoutConstraint = constraint;
		setLabel("Portlet column added");
	}

	public boolean canExecute() {
		return newColumn != null && diagram != null && layoutConstraint != null;
	}

	public void execute() {
		// newColumn.setLocation(bounds.getLocation());

		// Dimension size = bounds.getSize();
		// if (size.width > 0 && size.height > 0)
		// newColumn.setSize(size);
		redo();
	}

	public void redo() {
		if (layoutConstraint.equals(LayoutConstraint.EMPTY) || layoutConstraint.rowIndex > -1) {
			PortletLayout portletLayout = new PortletLayout();
			newColumn.setParent(portletLayout);
			portletLayout.addColumn(newColumn);
			portletLayout.setParent(diagram);
			diagram.addRow(portletLayout, layoutConstraint.rowIndex);
		}
		else if (layoutConstraint.columnIndex > -1) {
			ModelElement portletLayout = diagram.getRows().get(layoutConstraint.columnIndex);
			if (portletLayout instanceof PortletLayout) {
				PortletLayout existingPortletLayout = (PortletLayout) portletLayout;
				newColumn.setParent(existingPortletLayout);
				existingPortletLayout.addColumn(newColumn, layoutConstraint.columnIndex);
			}
		}
		// parent.addColumn(newColumn);
	}

	public void undo() {
		if (layoutConstraint.equals(LayoutConstraint.EMPTY)) {
			for (ModelElement row : diagram.getRows()) {
				PortletLayout portletLayout = (PortletLayout) row;
				if (portletLayout.getColumns().size() == 1 && portletLayout.getColumns().get(0).equals(newColumn)) {
					diagram.removeRow(portletLayout);
				}
			}
		}
	}

}
