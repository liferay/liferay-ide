/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
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
		if (layoutConstraint.equals(LayoutConstraint.EMPTY)) {
			PortletLayout portletLayout = new PortletLayout();
			portletLayout.addColumn(newColumn);
			diagram.addRow(portletLayout);
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
