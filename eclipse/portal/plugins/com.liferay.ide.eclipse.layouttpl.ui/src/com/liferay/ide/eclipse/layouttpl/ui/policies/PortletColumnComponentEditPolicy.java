
package com.liferay.ide.eclipse.layouttpl.ui.policies;

import com.liferay.ide.eclipse.layouttpl.ui.cmd.PortletColumnDeleteCommand;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;


public class PortletColumnComponentEditPolicy extends ComponentEditPolicy {


	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object parent = getHost().getParent().getModel();
		Object child = getHost().getModel();
		if (parent instanceof LayoutTplDiagram && child instanceof PortletColumn) {
			return new PortletColumnDeleteCommand(null, (PortletColumn) child);
		}

		return super.createDeleteCommand(deleteRequest);
	}

	@Override
	public void eraseSourceFeedback(Request request) {
		// PortletColumnComponentEditPolicy
		System.out.println("PortletColumnComponentEditPolicy.eraseSourceFeedback");
		super.eraseSourceFeedback(request);
	}

	@Override
	public void eraseTargetFeedback(Request request) {
		// PortletColumnComponentEditPolicy
		System.out.println("PortletColumnComponentEditPolicy.eraseTargetFeedback");
		super.eraseTargetFeedback(request);
	}

	@Override
	public void showSourceFeedback(Request request) {
		// PortletColumnComponentEditPolicy
		System.out.println("PortletColumnComponentEditPolicy.showSourceFeedback");
		super.showSourceFeedback(request);
	}

	@Override
	public void showTargetFeedback(Request request) {
		// PortletColumnComponentEditPolicy
		System.out.println("PortletColumnComponentEditPolicy.showTargetFeedback");
		super.showTargetFeedback(request);
	}
}
