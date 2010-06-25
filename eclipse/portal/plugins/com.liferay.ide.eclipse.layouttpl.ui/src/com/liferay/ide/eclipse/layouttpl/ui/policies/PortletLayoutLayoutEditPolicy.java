package com.liferay.ide.eclipse.layouttpl.ui.policies;

import com.liferay.ide.eclipse.layouttpl.ui.cmd.PortletColumnCreateCommand;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.parts.LayoutTplDiagramEditPart;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;


public class PortletLayoutLayoutEditPolicy extends ConstrainedLayoutEditPolicy {

	public static final int DEFAULT_FEEDBACK_HEIGHT = 50;

	protected IFigure layoutFeedbackFigure;

	public PortletLayoutLayoutEditPolicy() {
		super();
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		System.out.println("ColumnLayoutEditPolicy.createChangeConstraintCommand");
		return null;
	}

	protected IFigure createLayoutFeedbackFigure(Request request) {

		if (request instanceof CreateRequest) {
			LayoutConstraint constraint = (LayoutConstraint) getConstraintFor((CreateRequest) request);

			RoundedRectangle rRect = new RoundedRectangle();
			rRect.setBackgroundColor(ColorConstants.white);

			if (constraint.equals(LayoutConstraint.EMPTY)) {
				rRect.setSize(getContainerWidth(), DEFAULT_FEEDBACK_HEIGHT);
				rRect.setLocation(new Point(
					LayoutTplDiagramEditPart.DIAGRAM_MARGIN, LayoutTplDiagramEditPart.DIAGRAM_MARGIN));
				rRect.setAlpha(128);
			}

			return rRect;
		}

		return null;
	}

	protected int getContainerWidth() {
		return getDiagramPart().getContainerWidth();
	}

	protected LayoutTplDiagramEditPart getDiagramPart() {
		return (LayoutTplDiagramEditPart) getHost();
	}

	@Override
	protected void eraseLayoutTargetFeedback(Request request) {
		super.eraseLayoutTargetFeedback(request);

		if (layoutFeedbackFigure != null) {
			System.out.println(getFeedbackLayer().getChildren().size());
			removeFeedback(layoutFeedbackFigure);
			getFeedbackLayer().repaint();
			System.out.println(getFeedbackLayer().getChildren().size());
			layoutFeedbackFigure = null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object getConstraintFor(Point point) {
		LayoutConstraint constraint = new LayoutConstraint();

		LayoutTplDiagramEditPart diagramPart = (LayoutTplDiagramEditPart) getHost();
		List parts = diagramPart.getChildren();
		int numParts = parts.size();

		if (numParts > 0) {

		}

		return constraint;
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
				(LayoutConstraint) getConstraintFor(request));
		}

		return null;
	}

	@Override
	protected void showLayoutTargetFeedback(Request request) {
		// ColumnLayoutEditPolicy
		System.out.println("ColumnLayoutEditPolicy.showLayoutTargetFeedback");
		super.showLayoutTargetFeedback(request);

		if (layoutFeedbackFigure == null) {
			layoutFeedbackFigure = createLayoutFeedbackFigure(request);
			if (layoutFeedbackFigure != null) {
				addFeedback(layoutFeedbackFigure);
			}
		}
	}

}
