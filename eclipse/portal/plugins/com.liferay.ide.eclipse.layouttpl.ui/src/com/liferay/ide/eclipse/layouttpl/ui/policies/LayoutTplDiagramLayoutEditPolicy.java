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
package com.liferay.ide.eclipse.layouttpl.ui.policies;

import com.liferay.ide.eclipse.layouttpl.ui.cmd.PortletColumnCreateCommand;
import com.liferay.ide.eclipse.layouttpl.ui.draw2d.FeedbackRoundedRectangle;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.parts.LayoutTplDiagramEditPart;
import com.liferay.ide.eclipse.layouttpl.ui.parts.PortletLayoutEditPart;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;


public class LayoutTplDiagramLayoutEditPolicy extends ConstrainedLayoutEditPolicy {

	public static final int DEFAULT_FEEDBACK_HEIGHT = 20;

	protected IFigure layoutFeedbackFigure;

	public LayoutTplDiagramLayoutEditPolicy() {
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

			if (constraint != null) {
				RoundedRectangle rRect = new FeedbackRoundedRectangle();
				rRect.setBackgroundColor(ColorConstants.white);
				rRect.setSize(getContainerWidth(), DEFAULT_FEEDBACK_HEIGHT);
				rRect.setAlpha(128);

				if (constraint.rowIndex == 0) {
					rRect.setLocation(new Point(
						LayoutTplDiagramEditPart.DIAGRAM_MARGIN, LayoutTplDiagramEditPart.DIAGRAM_MARGIN));
				}
				else if (constraint.equals(LayoutConstraint.EMPTY)) {
					List parts = getHost().getChildren();
					Rectangle r = new Rectangle();
					for (Object part : parts) {
						GraphicalEditPart editPart = (GraphicalEditPart) part;
						r.union(editPart.getFigure().getBounds());
					}

					rRect.setLocation(new Point(r.x, r.y + r.height));
				}

				return rRect;
			}
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
			removeFeedback(layoutFeedbackFigure);
			getFeedbackLayer().repaint();
			layoutFeedbackFigure = null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object getConstraintFor(Point point) {
		LayoutConstraint constraint = null;
		LayoutTplDiagramEditPart diagramPart = (LayoutTplDiagramEditPart) getHost();

		if (diagramPart.getChildren().size() == 0) {
			constraint = new LayoutConstraint();
		}
		else if (point.y < (LayoutTplDiagramEditPart.DIAGRAM_MARGIN + PortletLayoutEditPart.LAYOUT_MARGIN)) {
			constraint = new LayoutConstraint();
			List parts = diagramPart.getChildren();
			int numParts = parts.size();

			if (numParts > 0) {
				constraint.rowIndex = 0;
			}
		}
		else {
			List parts = diagramPart.getChildren();
			Rectangle r = new Rectangle();
			Dimension d = new Dimension();
			for (Object part : parts) {
				GraphicalEditPart editPart = (GraphicalEditPart) part;
				r.union(editPart.getFigure().getBounds());
				d.union(editPart.getFigure().getBounds().getSize());
			}

			if (point.y > r.height) {
				constraint = new LayoutConstraint();
				constraint.rowIndex = -1;
			}
		}

		return constraint;
	}

	@Override
	protected Object getConstraintFor(Rectangle rect) {
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
		super.showLayoutTargetFeedback(request);

		IFigure feedback = createLayoutFeedbackFigure(request);

		if (feedback != null && !feedback.equals(layoutFeedbackFigure)) {
			layoutFeedbackFigure = feedback;
			addFeedback(layoutFeedbackFigure);
		}
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		return null;// don't create edit policy for child
	}

}
