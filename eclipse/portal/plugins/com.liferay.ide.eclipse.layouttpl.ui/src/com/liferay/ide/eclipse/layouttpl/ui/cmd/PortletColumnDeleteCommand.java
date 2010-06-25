package com.liferay.ide.eclipse.layouttpl.ui.cmd;

import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.commands.Command;

public class PortletColumnDeleteCommand extends Command {

	protected final PortletColumn child;

	protected final PortletLayout parent;

	protected boolean wasRemoved;

	public PortletColumnDeleteCommand(PortletLayout parent, PortletColumn child) {
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
		wasRemoved = parent.removeColumn(child);
	}

	public void undo() {
		parent.addColumn(child);
	}
}
