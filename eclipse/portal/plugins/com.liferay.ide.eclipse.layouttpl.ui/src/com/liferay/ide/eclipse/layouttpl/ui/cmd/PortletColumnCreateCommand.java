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

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;

import org.eclipse.draw2d.GridData;
import org.eclipse.gef.commands.Command;

public class PortletColumnCreateCommand extends Command {

	protected PortletColumn newColumn;

	private final LayoutTplDiagram parent;
	
	private GridData gridData;

	public PortletColumnCreateCommand(PortletColumn newColumn, LayoutTplDiagram parent, GridData data) {
		this.newColumn = newColumn;
		this.parent = parent;
		this.gridData = data;
		setLabel("Portlet column added");
	}

	public boolean canExecute() {
		return newColumn != null && parent != null && gridData != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		// newColumn.setLocation(bounds.getLocation());

		// Dimension size = bounds.getSize();
		// if (size.width > 0 && size.height > 0)
		// newColumn.setSize(size);
		redo();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		parent.addChild(newColumn);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		parent.removeChild(newColumn);
	}

}
