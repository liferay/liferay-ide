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

import com.liferay.ide.eclipse.layouttpl.ui.cmd.PortletColumnChangeConstraintCommand;
import com.liferay.ide.eclipse.layouttpl.ui.cmd.PortletColumnCreateCommand;
import com.liferay.ide.eclipse.layouttpl.ui.cmd.PortletLayoutCreateCommand;
import com.liferay.ide.eclipse.layouttpl.ui.draw2d.FeedbackRoundedRectangle;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.eclipse.layouttpl.ui.parts.PortletColumnEditPart;
import com.liferay.ide.eclipse.layouttpl.ui.parts.PortletLayoutEditPart;
import com.liferay.ide.eclipse.layouttpl.ui.util.LayoutTplUtil;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("unchecked")
public class PortletLayoutLayoutEditPolicy extends ConstrainedLayoutEditPolicy {

	public static final int DEFAULT_FEEDBACK_HEIGHT = 100;

	protected IFigure layoutFeedbackFigure;

	public PortletLayoutLayoutEditPolicy() {
		super();
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		if (constraint instanceof LayoutConstraint && child instanceof PortletColumnEditPart) {
			PortletColumnEditPart portletColumnPart = (PortletColumnEditPart) child;
			PortletLayoutEditPart parentPart = (PortletLayoutEditPart) portletColumnPart.getParent();
			return new PortletColumnChangeConstraintCommand(
				portletColumnPart.getCastedModel(), (PortletLayout) parentPart.getModel(),
				(PortletLayout) parentPart.getModel(), (LayoutConstraint) constraint);
		}

		return null;
	}

	protected IFigure createLayoutFeedbackFigure(Request request) {

		if (LayoutTplUtil.isCreateRequest(PortletColumn.class, request)) {
			LayoutConstraint constraint = (LayoutConstraint) getConstraintFor((CreateRequest) request);

			RoundedRectangle rRect = new FeedbackRoundedRectangle();
			rRect.setSize(PortletLayoutEditPart.COLUMN_SPACING, DEFAULT_FEEDBACK_HEIGHT);

			List children = getPart().getChildren();
			Point rectLocation = null;
			if (constraint.columnIndex >= children.size()) {
				PortletColumnEditPart insertColumnPart =
					(PortletColumnEditPart) getPart().getChildren().get(constraint.columnIndex - 1);
				Rectangle insertColumnRect = insertColumnPart.getFigure().getBounds();
				rectLocation = new Point(insertColumnRect.x + insertColumnRect.width, insertColumnRect.y);
			}
			else {
				PortletColumnEditPart insertColumnPart =
					(PortletColumnEditPart) getPart().getChildren().get(constraint.columnIndex);
				Rectangle insertColumnRect = insertColumnPart.getFigure().getBounds();
				rectLocation = new Point(insertColumnRect.x - PortletLayoutEditPart.COLUMN_SPACING, insertColumnRect.y);
			}

			rRect.setLocation(rectLocation);

			return rRect;
		}
		else if (LayoutTplUtil.isCreateRequest(PortletLayout.class, request)) {
			// LayoutConstraint constraint = (LayoutConstraint)
			// getConstraintFor((CreateRequest) request);

			RoundedRectangle rRect = new FeedbackRoundedRectangle();
			rRect.setSize(getContainerWidth(), 10);
			Rectangle partBounds = getPart().getFigure().getBounds();
			rRect.setLocation(new Point(partBounds.x, partBounds.y - 10));
			return rRect;
		}

		return null;
	}

	protected int getContainerWidth() {
		return getPart().getFigure().getSize().width;
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

	@Override
	protected void showLayoutTargetFeedback(Request request) {
		// ColumnLayoutEditPolicy
		super.showLayoutTargetFeedback(request);

		IFigure feedback = createLayoutFeedbackFigure(request);

		if (feedback != null && !feedback.equals(layoutFeedbackFigure)) {
			if (layoutFeedbackFigure != null) {
				removeFeedback(layoutFeedbackFigure);
			}

			layoutFeedbackFigure = feedback;
			addFeedback(layoutFeedbackFigure);
		}
	}

	protected PortletLayoutEditPart getPart() {
		return (PortletLayoutEditPart) getHost();
	}


	@Override
	protected Object getConstraintFor(Point point) {
		LayoutConstraint constraint = new LayoutConstraint();

		PortletLayoutEditPart layoutEditPart = (PortletLayoutEditPart) getHost();
		constraint.rowIndex = LayoutTplUtil.getRowIndex(layoutEditPart);

		List columns = layoutEditPart.getChildren();
		int numColumns = columns.size();

		int topColumnY = ((PortletColumnEditPart) columns.get(0)).getFigure().getBounds().y;

		if (point.y > topColumnY) {
			constraint.rowIndex = numColumns > 1 ? constraint.rowIndex - 1 : 0;
		}
		else {
			// either need to insert this column at the first or the end
			if (point.x < 0) {
				constraint.columnIndex = 0;
			}
			else {
				for (int i = 0; i < columns.size(); i++) {
					int xCoord = ((PortletColumnEditPart) columns.get(i)).getFigure().getBounds().x;

					if (point.x < xCoord) {
						constraint.columnIndex = i;
						break;
					}
				}

				if (constraint.columnIndex == -1) {
					constraint.columnIndex = numColumns;
				}
			}

			// for the weight lets cut in half the column just inserted next two
			PortletColumnEditPart refColumnPart = null;
			if (constraint.columnIndex < numColumns) {
				refColumnPart = (PortletColumnEditPart) columns.get(constraint.columnIndex);
			}
			else {
				refColumnPart = (PortletColumnEditPart) columns.get(constraint.columnIndex - 1);
			}

			int refWeight = refColumnPart.getCastedModel().getWeight();
			int newWeight = -1;
			if (refWeight != PortletColumn.DEFAULT_WEIGHT) {
				newWeight = refWeight / 2;
			}
			else {
				newWeight = 50; // 50%
			}

			constraint.weight = newWeight;
			constraint.refColumn = refColumnPart.getCastedModel();
		}



		return constraint;
	}

	@Override
	protected Object getConstraintFor(Rectangle rect) {
		PortletLayoutEditPart layoutEditPart = (PortletLayoutEditPart) getHost();
		List columns = layoutEditPart.getChildren();
		int numColumns = columns.size();

		LayoutConstraint constraint = new LayoutConstraint();
		constraint.rowIndex = LayoutTplUtil.getRowIndex(layoutEditPart);


		// either need to insert this column at the first or the end
		if (rect.x < 0) {
			constraint.columnIndex = 0;
		}
		else {
			for (int i = 0; i < columns.size(); i++) {
				int xCoord = ((PortletColumnEditPart) columns.get(i)).getFigure().getBounds().x;

				if (rect.x < xCoord) {
					constraint.columnIndex = i;
					break;
				}
			}

			if (constraint.columnIndex == -1) {
				constraint.columnIndex = numColumns;
			}
		}

		PortletColumnEditPart refColumnPart = null;
		if (constraint.columnIndex > 0) {
			refColumnPart = (PortletColumnEditPart) columns.get(constraint.columnIndex - 1);
		}
		else if (constraint.columnIndex == 0) {
			refColumnPart = (PortletColumnEditPart) columns.get(1);
		}

		constraint.refColumn = refColumnPart.getCastedModel();

		// get new weight based on resize
		int rowWidth = getHostFigure().getSize().width - (PortletLayoutEditPart.LAYOUT_MARGIN * 2);
		constraint.weight = (int) ((double) rect.width / (double) rowWidth * 100d);

		return constraint;
	}

	protected LayoutTplDiagram getDiagram() {
		return (LayoutTplDiagram) getHost().getParent().getModel();
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Object childClass = request.getNewObjectType();

		if (childClass == PortletColumn.class) {
			return new PortletColumnCreateCommand(
				(PortletColumn) request.getNewObject(), getDiagram(), (LayoutConstraint) getConstraintFor(request));
		}

		if (childClass == PortletLayout.class) {
			return new PortletLayoutCreateCommand(
				(PortletLayout) request.getNewObject(), (LayoutTplDiagram) getHost().getParent().getModel(),
				(LayoutConstraint) getConstraintFor(request));
		}

		return null;
	}

}
