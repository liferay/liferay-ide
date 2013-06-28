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

import com.liferay.ide.layouttpl.core.model.PortletRowLayoutElement;
import com.liferay.ide.layouttpl.ui.cmd.PortletColumnCreateCommand;
import com.liferay.ide.layouttpl.ui.cmd.PortletLayoutCreateCommand;
import com.liferay.ide.layouttpl.ui.draw2d.FeedbackRoundedRectangle;
import com.liferay.ide.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.layouttpl.ui.parts.PortletLayoutEditPart;
import com.liferay.ide.layouttpl.ui.parts.PortletRowLayoutEditPart;
import com.liferay.ide.layouttpl.ui.util.LayoutTplUIUtil;

import java.util.List;

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
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Cindy Li
 */
@SuppressWarnings( "rawtypes" )
public class PortletRowLayoutLayoutEditPolicy extends ConstrainedLayoutEditPolicy
{
    public static final int DEFAULT_FEEDBACK_HEIGHT = 20;

    protected IFigure layoutFeedbackFigure;

    public PortletRowLayoutLayoutEditPolicy()
    {
        super();
    }

    @Override
    protected Command createChangeConstraintCommand( EditPart child, Object constraint )
    {
        return null;
    }

    @Override
    protected EditPolicy createChildEditPolicy( EditPart child )
    {
        return null;// don't create edit policy for child
    }

    protected IFigure createLayoutFeedbackFigure( Request request )
    {
        LayoutConstraint constraint = (LayoutConstraint) getConstraintFor( (CreateRequest) request );

        if( constraint == null )//XXX non-parent column should go here
        {
            return null;
        }

        boolean isRowRequest = LayoutTplUIUtil.isCreateRequest( PortletLayout.class, request );
        boolean isColumnRequest = LayoutTplUIUtil.isCreateRequest( PortletColumn.class, request );
        RoundedRectangle feedback = new FeedbackRoundedRectangle();

        if( isRowRequest || isColumnRequest )
        {
            feedback.setSize( getContainerWidth(), DEFAULT_FEEDBACK_HEIGHT );
            PortletRowLayoutEditPart rowLayoutPart = getRowLayoutPart();
            List parts = rowLayoutPart.getChildren();

            if( constraint.equals( LayoutConstraint.EMPTY ) )
            {
                Rectangle r = new Rectangle();

                if( parts.size() > 0 )
                {
                    for( Object part : parts )
                    {
                        GraphicalEditPart editPart = (GraphicalEditPart) part;
                        r.union( editPart.getFigure().getBounds() );
                    }
                }

                Point point = new Point( r.x, r.y + r.height );

                if( point.x < rowLayoutPart.getMargin() )
                {
                    point.x = rowLayoutPart.getMargin();
                }

                if( point.y < rowLayoutPart.getMargin() )
                {
                    point.y = rowLayoutPart.getMargin();
                }

                if( parts.size() > 0 )
                {
                    point.y -= ( feedback.getSize().height / 2 );
                }

                feedback.setLocation( point );
            }
            else if( constraint.newRowIndex == 0 )
            {
                Rectangle r = new Rectangle();

                if( parts.size() > 0 )
                {
                    GraphicalEditPart editPart = (GraphicalEditPart) parts.get( 0 );
                    r = editPart.getFigure().getBounds().getCopy();
                    r.y -= ( feedback.getSize().height / 2 );
                }

                Point point = new Point( r.x, r.y );

                feedback.setLocation( point );
            }

        }
        else
        {
            feedback = null;
        }

        return feedback;
    }

    @Override
    protected void eraseLayoutTargetFeedback( Request request )
    {
        super.eraseLayoutTargetFeedback( request );

        if( layoutFeedbackFigure != null )
        {
            removeFeedback( layoutFeedbackFigure );
            getFeedbackLayer().repaint();
            layoutFeedbackFigure = null;
        }
    }

    @Override
    protected Object getConstraintFor( Point point )
    {
        LayoutConstraint constraint = null;
        PortletRowLayoutEditPart rowLayoutPart = getRowLayoutPart();

        if( rowLayoutPart.getChildren().size() == 0 )
        {
            constraint = new LayoutConstraint();//XXX for non-parent column too?(return null;)
        }
        else if( point.y < ( rowLayoutPart.getMargin() + PortletLayoutEditPart.LAYOUT_MARGIN ) )
        {
            constraint = new LayoutConstraint();
            List parts = rowLayoutPart.getChildren();
            int numParts = parts.size();

            if( numParts > 0 )
            {
                constraint.newRowIndex = 0;
            }
        }
        else
        {
            List parts = rowLayoutPart.getChildren();
            Rectangle r = new Rectangle();
            Dimension d = new Dimension();
            for( Object part : parts )
            {
                GraphicalEditPart editPart = (GraphicalEditPart) part;
                r.union( editPart.getFigure().getBounds().getSize() );
                d.union( editPart.getFigure().getBounds().getSize() );
            }

            if( point.y > r.height )
            {
                constraint = new LayoutConstraint();
                constraint.newRowIndex = -1;
            }
        }

        return constraint;
    }

    @Override
    protected Object getConstraintFor( Rectangle rect )
    {
        return null;
    }

    protected int getContainerWidth()
    {
        return getRowLayoutPart().getContainerWidth();
    }

    @Override
    protected Command getCreateCommand( CreateRequest request )//XXX non-parent column used to be return null;
    {
        if( getConstraintFor( request ) == null )
        {
            return UnexecutableCommand.INSTANCE;
        }

        Object childClass = request.getNewObjectType();

        if( childClass == PortletColumn.class )
        {
            return new PortletColumnCreateCommand(
                (PortletColumn) request.getNewObject(), (PortletRowLayoutElement) getHost().getModel(),
                (LayoutConstraint) getConstraintFor( request ) );
        }

        if( childClass == PortletLayout.class )
        {
            return new PortletLayoutCreateCommand(
                (PortletLayout) request.getNewObject(), (PortletRowLayoutElement) getHost().getModel(),
                (LayoutConstraint) getConstraintFor( request ) );
        }

        return null;
    }

    protected PortletRowLayoutEditPart getRowLayoutPart()
    {
        return (PortletRowLayoutEditPart) getHost();
    }

    @Override
    protected void showLayoutTargetFeedback( Request request )
    {
        super.showLayoutTargetFeedback( request );

        // if (shouldCreateLayoutFeedbackFigure(request)) {
        IFigure feedback = createLayoutFeedbackFigure( request );

        if( feedback != null && !feedback.equals( layoutFeedbackFigure ) )
        {
            if( layoutFeedbackFigure != null )
            {
                removeFeedback( layoutFeedbackFigure );
            }

            layoutFeedbackFigure = feedback;
            addFeedback( layoutFeedbackFigure );
        }
        // }
    }

}
