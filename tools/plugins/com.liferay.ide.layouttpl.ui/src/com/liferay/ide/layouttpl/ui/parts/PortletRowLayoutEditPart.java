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

package com.liferay.ide.layouttpl.ui.parts;

import com.liferay.ide.layouttpl.core.model.ModelElement;
import com.liferay.ide.layouttpl.core.model.PortletRowLayoutElement;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.swt.SWT;

/**
 * @author Cindy Li
 */
public abstract class PortletRowLayoutEditPart extends BaseGraphicalEditPart
{
    public static final int DEFAULT_COLUMN_HEIGHT = -1;

    protected Panel panel;

    protected boolean shouldUpdateConstraints = false;

    protected void configurePanel( Panel panel )
    {
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;

        panel.setLayoutManager( gridLayout );
        panel.setBorder( new MarginBorder( getMargin() ) );

        panel.addLayoutListener( new LayoutListener()
        {

            public void invalidate( IFigure container )
            {
                shouldUpdateConstraints = true;
            }

            public boolean layout( IFigure container )
            {
                return false;
            }

            public void postLayout( IFigure container )
            {
                if( shouldUpdateConstraints )
                {
                    updateColumnConstraints();
                }
            }

            public void remove( IFigure child )
            {
            }

            public void setConstraint( IFigure child, Object constraint )
            {
            }
        } );
    }

    @Override
    protected IFigure createFigure()
    {
        panel = createPanel();
        configurePanel( panel );

        return panel;
    }

    protected Panel createPanel()
    {
        return new Panel();
    }

    protected PortletRowLayoutElement getCastedModel()
    {
        return (PortletRowLayoutElement) getModel();
    }

    public int getContainerWidth()
    {
        if( panel != null ) //XXX to be continued, temporarily fix NullPointerException for parent column
        {
            return panel.getSize().width - ( getMargin() * 2 );
        }

        return 0;
    }

    public abstract int getMargin();

    protected List<ModelElement> getModelChildren()
    {
        return getCastedModel().getRows(); // return a list of rows
    }

    public int getPreferredColumnHeight()
    {
        int retval = DEFAULT_COLUMN_HEIGHT;

        int numRows = getRowPartsCount();

        if( numRows > 1 )
        {
            Rectangle partBounds = getFigure().getBounds();

            if( partBounds.height > 0 )
            {
                int partHeight = partBounds.height;
                int rowsHeight = partHeight - ( getMargin() * 2 );
                int totalColumnsHeight = rowsHeight - ( getRowPartsCount() * PortletLayoutEditPart.COLUMN_SPACING * 2 );
                int computedColumnHeight = totalColumnsHeight / numRows;

                retval = computedColumnHeight;
            }
        }

        return retval;
    }

    protected int getRowPartsCount()
    {
        return getChildren().size();
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        String prop = evt.getPropertyName();

        if( PortletRowLayoutElement.ROW_ADDED_PROP.equals( prop ) || PortletRowLayoutElement.ROW_REMOVED_PROP.equals( prop ) )
        {
            refreshChildren();
            List rows = getChildren();

            if( rows.size() > 0 )
            {
                for( Object row : rows )
                {
                    AbstractEditPart rowPart = (AbstractEditPart) row;
                    List cols = rowPart.getChildren();

                    if( cols.size() > 0 )
                    {
                        for( Object col : cols )
                        {
                            ( (AbstractEditPart) col ).refresh();
                        }
                    }

                    ( (AbstractEditPart) row ).refresh();
                }
            }
            refreshVisuals();
        }
    }

    @Override
    public void refresh()
    {
        super.refresh();

        if( getChildren() != null )
        {
            refreshVisuals();
        }
    }

    @Override
    protected void refreshVisuals()
    {
        super.refreshVisuals();
        List children = getChildren();

        for( Object child : children )
        {
            if( child instanceof AbstractEditPart )
            {
                ( (AbstractEditPart) child ).refresh();
            }
        }
    }

    protected void updateColumnConstraints()
    {
        try
        {
            for( Object row : getChildren() )
            {
                PortletLayoutEditPart rowPart = (PortletLayoutEditPart) row;

                for( Object col : ( (EditPart) row ).getChildren() )
                {
                    PortletColumnEditPart columnPart = (PortletColumnEditPart) col;
                    Object constraint = rowPart.getLayoutConstraint( columnPart, columnPart.getFigure() );

                    if( constraint instanceof GridData )
                    {
                        GridData gd = (GridData) constraint;
                        int columnHeight = getPreferredColumnHeight();

                        if( columnHeight > 0 )
                        {
                            gd.heightHint = columnHeight;
                        }
                        else
                        {
                            gd.heightHint = SWT.DEFAULT;
                            gd.grabExcessVerticalSpace = true;
                        }

                        rowPart.setLayoutConstraint( columnPart, columnPart.getFigure(), gd );
                    }
                }
            }
        }
        catch( Exception e )
        {
            // best effort don't log errors
        }
        finally
        {
            shouldUpdateConstraints = false;
        }
    }

}
