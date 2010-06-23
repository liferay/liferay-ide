package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.cmd.PortletColumnCreateCommand;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;


public class ColumnLayoutEditPolicy extends ConstrainedLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		// TODO Implement createChangeConstraintCommand method on class ColumnLayoutEditPolicy
		System.out.println("ColumnLayoutEditPolicy.createChangeConstraintCommand");
		return null;
	}

	@Override
	protected Object getConstraintFor(Point point) {
		return new GridData();
	}

	@Override
	protected Object getConstraintFor(Rectangle rect) {
		System.out.println("ColumnLayoutEditPolicy.getConstraintFor " + rect);
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Object childClass = request.getNewObjectType();
		if (childClass == PortletColumn.class) {
			return new PortletColumnCreateCommand(
				(PortletColumn) request.getNewObject(), (LayoutTplDiagram) getHost().getModel(),
				(GridData) getConstraintFor(request));
		}

		return null;
	}

}
