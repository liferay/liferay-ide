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
import com.liferay.ide.layouttpl.ui.cmd.PortletColumnChangeConstraintCommand;
import com.liferay.ide.layouttpl.ui.cmd.PortletColumnCreateCommand;
import com.liferay.ide.layouttpl.ui.cmd.PortletLayoutCreateCommand;
import com.liferay.ide.layouttpl.ui.draw2d.FeedbackRoundedRectangle;
import com.liferay.ide.layouttpl.ui.model.LayoutConstraint;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.layouttpl.ui.parts.PortletColumnEditPart;
import com.liferay.ide.layouttpl.ui.parts.PortletLayoutEditPart;
import com.liferay.ide.layouttpl.ui.util.LayoutTplUIUtil;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "rawtypes" )
public class PortletLayoutLayoutEditPolicy extends ConstrainedLayoutEditPolicy
{

    // public static final int DEFAULT_FEEDBACK_HEIGHT = 100;

    protected IFigure feedbackFigure;

    public PortletLayoutLayoutEditPolicy()
    {
        super();
    }

    @Override
    protected Command createChangeConstraintCommand( EditPart child, Object constraint )
    {
        if( constraint instanceof LayoutConstraint && child instanceof PortletColumnEditPart )
        {
            PortletColumnEditPart portletColumnPart = (PortletColumnEditPart) child;
            PortletLayoutEditPart parentPart = (PortletLayoutEditPart) portletColumnPart.getParent();
            return new PortletColumnChangeConstraintCommand(
                portletColumnPart.getCastedModel(), (PortletLayout) parentPart.getModel(),
                (PortletLayout) parentPart.getModel(), (LayoutConstraint) constraint );
        }

        return null;
    }

    protected IFigure createLayoutFeedbackFigure( Request request )
    {
        Figure feedback = null;

        if( request instanceof CreateRequest )
        {
            boolean isRowRequest = LayoutTplUIUtil.isCreateRequest( PortletLayout.class, request );
            boolean isColumnRequest = LayoutTplUIUtil.isCreateRequest( PortletColumn.class, request );
            LayoutConstraint constraint = (LayoutConstraint) getConstraintFor( (CreateRequest) request );

            if( constraint != null )
            {
                feedback = new FeedbackRoundedRectangle();

                Rectangle partBounds = getPart().getFigure().getBounds().getCopy();

                if( isRowRequest )
                {
                    feedback.setSize( getContainerWidth(), LayoutTplDiagramLayoutEditPolicy.DEFAULT_FEEDBACK_HEIGHT );

                    PortletLayoutEditPart layoutEditPart = (PortletLayoutEditPart) getHost();
                    int currentRowIndex = LayoutTplUIUtil.getRowIndex( layoutEditPart );
                    if( constraint.newRowIndex == currentRowIndex )
                    {
                        partBounds.y -= ( feedback.getSize().height / 2 );
                    }
                    else if( constraint.newRowIndex > currentRowIndex || constraint.newRowIndex == -1 )
                    {
                        partBounds.y = partBounds.y + partBounds.height - ( feedback.getSize().height / 2 );
                    }

                    feedback.setLocation( new Point( partBounds.x, partBounds.y ) );
                }
                else if( isColumnRequest )
                {
                    feedback.setSize( (int) ( PortletLayoutEditPart.COLUMN_SPACING * 1.2 ), partBounds.height -
                        ( PortletLayoutEditPart.LAYOUT_MARGIN * 2 ) );
                    Point rectLocation = null;

                    List children = getPart().getChildren();

                    if( constraint.newColumnIndex >= children.size() )
                    {
                        PortletColumnEditPart insertColumnPart =
                            (PortletColumnEditPart) getPart().getChildren().get( constraint.newColumnIndex - 1 );
                        Rectangle insertColumnRect = insertColumnPart.getFigure().getBounds();
                        rectLocation = new Point( insertColumnRect.x + insertColumnRect.width, insertColumnRect.y );
                    }
                    else
                    {
                        int index = 0;
                        if( constraint.newColumnIndex > -1 )
                        {
                            index = constraint.newColumnIndex;
                        }
                        else
                        {
                            index = getPart().getChildren().size() - 1;
                        }

                        PortletColumnEditPart insertColumnPart =
                            (PortletColumnEditPart) getPart().getChildren().get( index );
                        Rectangle insertColumnRect = insertColumnPart.getFigure().getBounds();
                        rectLocation = new Point( insertColumnRect.x - feedback.getSize().width, insertColumnRect.y );
                    }

                    feedback.setLocation( rectLocation );
                }
            }
        }
        else if( request instanceof ChangeBoundsRequest )
        {

        }

        return feedback;
    }

    protected int getContainerWidth()
    {
        return getPart().getFigure().getSize().width;
    }

    @Override
    protected void eraseLayoutTargetFeedback( Request request )
    {
        super.eraseLayoutTargetFeedback( request );

        if( feedbackFigure != null )
        {
            removeFeedback( feedbackFigure );
            getFeedbackLayer().repaint();
            feedbackFigure = null;
        }
    }

    @Override
    protected void showLayoutTargetFeedback( Request request )
    {
        // ColumnLayoutEditPolicy
        super.showLayoutTargetFeedback( request );

        IFigure feedback = createLayoutFeedbackFigure( request );

        if( feedback != null && !feedback.equals( feedbackFigure ) )
        {
            if( feedbackFigure != null )
            {
                removeFeedback( feedbackFigure );
            }

            feedbackFigure = feedback;
            addFeedback( feedbackFigure );
        }
    }

    protected PortletLayoutEditPart getPart()
    {
        return (PortletLayoutEditPart) getHost();
    }

    @Override
    protected Object getConstraintFor( ChangeBoundsRequest request, GraphicalEditPart child )
    {
        Rectangle column = child.getFigure().getBounds() ;

        //to disable crossing the line where width = 0
        //using equals because request.getSizeDelta().width doesn't change any more after crossing the line
        if( ( - request.getSizeDelta().width ) == column.width - 5 )
        {
            return null;
        }

        return super.getConstraintFor( request, child );
    }

    @Override
    protected Object getConstraintFor( Point orgPoint )
    {
        LayoutConstraint constraint = new LayoutConstraint();

        PortletLayoutEditPart layoutEditPart = (PortletLayoutEditPart) getHost();
        int currentRowIndex = LayoutTplUIUtil.getRowIndex( layoutEditPart );
        constraint.rowIndex = currentRowIndex;

        List columns = layoutEditPart.getChildren();
        int numColumns = columns.size();

        Rectangle columnBounds = ( (PortletColumnEditPart) columns.get( 0 ) ).getFigure().getBounds().getCopy();
        Point copyPoint = orgPoint.getCopy();
        copyPoint.translate( layoutEditPart.getFigure().getBounds().getLocation() );

        int topColumnY = columnBounds.y;
        int bottomColumnY = topColumnY + ( (PortletColumnEditPart) columns.get( 0 ) ).getFigure().getBounds().height;

        if( copyPoint.y > bottomColumnY - PortletLayoutEditPart.LAYOUT_MARGIN )
        {
            constraint.newRowIndex = currentRowIndex + 1;
        }
        else if( copyPoint.y < topColumnY - PortletLayoutEditPart.LAYOUT_MARGIN )
        {
            constraint.newRowIndex = currentRowIndex;
        }
        else
        {
            // if (copyPoint.y > topColumnY && copyPoint.y < (topColumnY +
            // (columnBounds.height / 2))) {
            // constraint.newRowIndex = numColumns > 1 ? constraint.rowIndex - 1
            // : 0;
            // }
            // else {
            // constraint.newRowIndex = currentRowIndex + 1;
            // }

            // either need to insert this column at the first or the end
            if( orgPoint.x < 0 )
            {
                constraint.newColumnIndex = 0;
            }
            else
            {
                for( int i = 0; i < columns.size(); i++ )
                {
                    int xCoord = ( (PortletColumnEditPart) columns.get( i ) ).getFigure().getBounds().x;

                    if( orgPoint.x < xCoord )
                    {
                        constraint.newColumnIndex = i;
                        break;
                    }
                }

                if( constraint.newColumnIndex == -1 )
                {
                    constraint.newColumnIndex = numColumns;
                }
            }

            // find the nearest(right first) >5% column as ref column; if all columns are 5%, make command invalid
            PortletColumnEditPart refColumnPart = null;

            for( int i = 0; i < numColumns; i++ )
            {
                if( constraint.newColumnIndex + i < numColumns )
                {
                    PortletColumnEditPart ref = (PortletColumnEditPart) columns.get( constraint.newColumnIndex + i );

                    if( ref.getCastedModel().getWeight() > 5 )
                    {
                        refColumnPart = ref;
                        break;
                    }
                }

                if( constraint.newColumnIndex - i > 0 )
                {
                    PortletColumnEditPart ref = (PortletColumnEditPart) columns.get( constraint.newColumnIndex - 1 - i );

                    if( ref.getCastedModel().getWeight() > 5 )
                    {
                        refColumnPart = ref;
                        break;
                    }
                }
            }

            if( refColumnPart == null )
            {
                return null;
            }

            int refWeight = refColumnPart.getCastedModel().getWeight();
            int newWeight = -1;
            if( refWeight != PortletColumn.DEFAULT_WEIGHT )
            {
                newWeight = refWeight / 2;
            }
            else
            {
                newWeight = 50; // 50%
            }

            constraint.weight = LayoutTplUIUtil.adjustWeight( newWeight );
            constraint.refColumn = refColumnPart.getCastedModel();
        }

        return constraint;
    }

    @Override
    protected Object getConstraintFor( Rectangle rect )
    {
        PortletLayoutEditPart layoutEditPart = (PortletLayoutEditPart) getHost();
        List columns = layoutEditPart.getChildren();
        int numColumns = columns.size();

        LayoutConstraint constraint = new LayoutConstraint();
        constraint.rowIndex = LayoutTplUIUtil.getRowIndex( layoutEditPart );

        int refColumnIndex = 0;

        // either need to insert this column at the first or the end
        if( rect.x < 0 )
        {
            return null; // make the command unexecutable
        }
        else
        {
            for( int i = 0; i < columns.size(); i++ )
            {
                Rectangle bounds = ( (PortletColumnEditPart) columns.get( i ) ).getFigure().getBounds();
                Rectangle copyBounds = bounds.getCopy();
                translateFromAbsoluteToLayoutRelative( copyBounds );
                int xCoord = copyBounds.x;
                int yCoord = copyBounds.y;

                if( rect.y != yCoord || (rect.y + rect.height) != yCoord + bounds.height )
                {
                    return null; // make the command unexecutable
                }

                if( rect.x == xCoord ) // right side of the column is draged
                {
                    constraint.newColumnIndex = i;
                    refColumnIndex  = i + 1;
                    break;
                }
                else if( (rect.x + rect.width) == xCoord + bounds.width ) // left side is draged
                {
                    constraint.newColumnIndex = i;
                    refColumnIndex = i - 1;
                    break;
                }
            }

            if( constraint.newColumnIndex == -1 )
            {
                constraint.newColumnIndex = numColumns;
            }
        }

        PortletColumnEditPart refColumnPart = null;

        if( constraint.newColumnIndex >= 0 )
        {
            if( refColumnIndex >= 0 && refColumnIndex < numColumns )
            {
                refColumnPart = (PortletColumnEditPart) columns.get( refColumnIndex );
                constraint.refColumn = refColumnPart.getCastedModel();
            }
            else
            {
                return null; // make the command unexecutable
            }
        }

        // get new weight based on resize
        int rowWidth = getHostFigure().getSize().width - ( PortletLayoutEditPart.LAYOUT_MARGIN * 2 );
        constraint.weight = LayoutTplUIUtil.adjustWeight( (int) ( (double) rect.width / (double) rowWidth * 100d ) );

        return constraint;
    }

    protected PortletRowLayoutElement getRowLayout()
    {
        return (PortletRowLayoutElement) getHost().getParent().getModel();
    }

    @Override
    protected Command getCreateCommand( CreateRequest request )
    {
        if( getConstraintFor( request ) == null )
        {
            return UnexecutableCommand.INSTANCE;
        }

        Object childClass = request.getNewObjectType();

        if( childClass == PortletColumn.class )
        {
            return new PortletColumnCreateCommand(
                (PortletColumn) request.getNewObject(), getRowLayout(), (LayoutConstraint) getConstraintFor( request ) );
        }

        if( childClass == PortletLayout.class )
        {
            return new PortletLayoutCreateCommand(
                (PortletLayout) request.getNewObject(), getRowLayout(), (LayoutConstraint) getConstraintFor( request ) );
        }

        return null;
    }

    @Override
    protected Command getMoveChildrenCommand( Request request )
    {
        return null;
    }

    @Override
    protected EditPolicy createChildEditPolicy( EditPart child )
    {
        // return new RoundedRectangleEditPolicy();
        return super.createChildEditPolicy( child );
    }

}
