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
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.commands.Command;

public class PortletLayoutCreateCommand extends Command {

	protected PortletLayout newLayout;

	protected LayoutTplDiagram diagram;
	
	protected LayoutConstraint layoutConstraint;

	public PortletLayoutCreateCommand(PortletLayout newLayout, LayoutTplDiagram diagram, LayoutConstraint constraint) {
		this.newLayout = newLayout;
		this.diagram = diagram;
		this.layoutConstraint = constraint;
		setLabel("Portlet row added");
	}

	public boolean canExecute() {
		return newLayout != null && diagram != null && layoutConstraint != null;
	}

	public void execute() {
		redo();
	}

	public void redo() {
		if (layoutConstraint.equals(LayoutConstraint.EMPTY)) {
			newLayout.setParent(diagram);
			PortletColumn newColumn = new PortletColumn();
			newLayout.addColumn(newColumn);
			newLayout.setParent(diagram);
			diagram.addRow(newLayout, layoutConstraint.rowIndex);
		}
		else if (layoutConstraint.rowIndex > -1 && layoutConstraint.columnIndex > -1) {
			newLayout.setParent(diagram);
			PortletColumn newColumn = new PortletColumn();
			newLayout.addColumn(newColumn);
			newLayout.setParent(diagram);
			diagram.addRow(newLayout, layoutConstraint.rowIndex);
		}
	}

	public void undo() {
		System.out.println("UNDO");
	}

}
