/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.ui.policies;

import com.liferay.ide.layouttpl.ui.parts.PortletColumnEditPart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Gregory Amerson
 */
public class PortletColumnLayoutEditPolicy extends ConstrainedLayoutEditPolicy
{

    public static final int DEFAULT_FEEDBACK_HEIGHT = 50;

    protected IFigure layoutFeedbackFigure;

    public PortletColumnLayoutEditPolicy()
    {
        super();
    }

    @Override
    protected Command createChangeConstraintCommand( EditPart child, Object constraint )
    {
        return null;
    }

    protected IFigure createLayoutFeedbackFigure( Request request )
    {
        // if (request instanceof CreateRequest) {
        // LayoutConstraint constraint = (LayoutConstraint)
        // getConstraintFor((CreateRequest) request);
        //
        // RoundedRectangle rRect = new RoundedRectangle();
        // rRect.setBackgroundColor(ColorConstants.white);
        //
        // if (constraint.equals(LayoutConstraint.EMPTY)) {
        // rRect.setSize(getContainerWidth(), DEFAULT_FEEDBACK_HEIGHT);
        // rRect.setLocation(new Point(
        // LayoutTplDiagramEditPart.DIAGRAM_MARGIN,
        // LayoutTplDiagramEditPart.DIAGRAM_MARGIN));
        // rRect.setAlpha(128);
        // }
        //
        // return rRect;
        // }

        return null;
    }

    // protected int getContainerWidth() {
    // return getDiagramPart().getContainerWidth();
    // }

    protected PortletColumnEditPart getDiagramPart()
    {
        return (PortletColumnEditPart) getHost();
    }

    @Override
    protected void eraseLayoutTargetFeedback( Request request )
    {
        super.eraseLayoutTargetFeedback( request );

        // if (layoutFeedbackFigure != null) {
        // System.out.println(getFeedbackLayer().getChildren().size());
        // removeFeedback(layoutFeedbackFigure);
        // getFeedbackLayer().repaint();
        // System.out.println(getFeedbackLayer().getChildren().size());
        // layoutFeedbackFigure = null;
        // }
    }

    @Override
    protected Object getConstraintFor( Point point )
    {
        return null;
    }

    @Override
    protected Object getConstraintFor( Rectangle rect )
    {
        return null;
    }

    @Override
    protected Command getCreateCommand( CreateRequest request )
    {
        // Object childClass = request.getNewObjectType();

        // if (childClass == PortletColumn.class) {
        // return new PortletColumnCreateCommand(
        // (PortletColumn) request.getNewObject(), (LayoutTplDiagram)
        // getHost().getModel(),
        // (LayoutConstraint) getConstraintFor(request));
        // }

        return null;
    }

    @Override
    protected void showLayoutTargetFeedback( Request request )
    {
        // ColumnLayoutEditPolicy
        super.showLayoutTargetFeedback( request );
        //
        // if (layoutFeedbackFigure == null) {
        // layoutFeedbackFigure = createLayoutFeedbackFigure(request);
        // if (layoutFeedbackFigure != null) {
        // addFeedback(layoutFeedbackFigure);
        // }
        // }
    }

    @Override
    protected Command getMoveChildrenCommand( Request request )
    {
        return null;
    }

    @Override
    protected Command getAddCommand( Request generic )
    {
        return null;
    }

}
