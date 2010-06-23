package com.liferay.ide.eclipse.layouttpl.ui.cmd;

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;

import org.eclipse.gef.commands.Command;

public class PortletColumnDeleteCommand extends Command {

	protected final PortletColumn child;

	protected final LayoutTplDiagram parent;

	protected boolean wasRemoved;

	public PortletColumnDeleteCommand(LayoutTplDiagram parent, PortletColumn child) {
		if (parent == null || child == null) {
			throw new IllegalArgumentException();
		}

		setLabel("Portlet Column deleted");

		this.parent = parent;
		this.child = child;
	}


	public boolean canUndo() {
		return wasRemoved;
	}

	public void execute() {
		redo();
	}

	public void redo() {
		wasRemoved = parent.removeChild(child);
	}

	public void undo() {
		parent.addChild(child);
	}
}
