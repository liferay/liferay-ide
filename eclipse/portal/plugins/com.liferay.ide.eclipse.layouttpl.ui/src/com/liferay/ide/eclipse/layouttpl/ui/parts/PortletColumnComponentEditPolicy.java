
package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.cmd.PortletColumnDeleteCommand;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;


class PortletColumnComponentEditPolicy extends ComponentEditPolicy {


	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object parent = getHost().getParent().getModel();
		Object child = getHost().getModel();
		if (parent instanceof LayoutTplDiagram && child instanceof PortletColumn) {
			return new PortletColumnDeleteCommand((LayoutTplDiagram) parent, (PortletColumn) child);
		}

		return super.createDeleteCommand(deleteRequest);
	}
}
