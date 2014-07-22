/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;
import com.liferay.ide.layouttpl.ui.draw2d.PortletLayoutPanel;

import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.sapphire.ElementList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
public class PortletLayoutEditPart extends BaseGraphicalEditPart
{

    public static final int COLUMN_SPACING = 10;

    public static final int LAYOUT_MARGIN = 10;

    public static GridData createGridData()
    {
        return new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 );
    }

    protected PortletLayoutPanel layoutPanel;

    public int getDefaultColumnHeight()
    {
        return getCastedParent().getPreferredColumnHeight();
    }

    public Object getLayoutConstraint( PortletColumnEditPart columnPart, IFigure figure )
    {
        if( getChildren().contains( columnPart ) )
        {
            return getFigure().getLayoutManager().getConstraint( figure );
        }

        return null;
    }

    private PortletRowLayoutEditPart getCastedParent()
    {
        return (PortletRowLayoutEditPart) getParent();
    }

    @Override
    protected void createEditPolicies()
    {
    }

    @Override
    protected IFigure createFigure()
    {
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.horizontalSpacing = COLUMN_SPACING;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;

        layoutPanel = new PortletLayoutPanel();
        layoutPanel.setOpaque( true );
        layoutPanel.setBorder( new MarginBorder( LAYOUT_MARGIN ) );
        layoutPanel.setBackgroundColor( new Color( null, 171, 171, 171 ) );
        layoutPanel.setLayoutManager( gridLayout );

        return layoutPanel;
    }

    protected PortletLayoutPanel getCastedFigure()
    {
        return (PortletLayoutPanel) getFigure();
    }

    protected PortletLayoutElement getCastedModel()
    {
        return (PortletLayoutElement) getModel();
    }

    protected ElementList<PortletColumnElement> getModelChildren()
    {
        return getCastedModel().getPortletColumns();
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    protected void refreshVisuals()
    {
        super.refreshVisuals();

        GridData gd = createGridData();
        ( (GraphicalEditPart) getParent() ).setLayoutConstraint( this, layoutPanel, gd );

        List rows = getParent().getChildren();

        if( rows.size() == 1 )
        {
            layoutPanel.setTop( true );
            layoutPanel.setBottom( true );
        }
        else if( this.equals( rows.get( 0 ) ) )
        {
            layoutPanel.setTop( true );
            layoutPanel.setBottom( false );
        }
        else if( this.equals( rows.get( rows.size() - 1 ) ) )
        {
            layoutPanel.setTop( false );
            layoutPanel.setBottom( true );
        }
        else
        {
            layoutPanel.setTop( false );
            layoutPanel.setBottom( false );
        }

        PortletLayoutPanel panel = getCastedFigure();
        GridLayout gridLayout = (GridLayout) panel.getLayoutManager();
        List columns = getChildren();

        int numColumns = columns.size();

        // need to rebuild column widths based on weight
        if( numColumns > 0 )
        {
            // get width of our own part to calculate new width
            int rowWidth = this.getFigure().getSize().width - ( PortletLayoutEditPart.LAYOUT_MARGIN * 2 );

            if( rowWidth > 0 )
            {
                for( Object col : columns )
                {
                    PortletColumnEditPart portletColumnPart = (PortletColumnEditPart) col;
                    PortletColumnElement column = (PortletColumnElement) portletColumnPart.getModel();
                    GridData rowData = portletColumnPart.createGridData();

                    double percent = column.getWeight().content().doubleValue() /
                                     column.getFullWeight().content().doubleValue();
                    rowData.widthHint = (int) ( percent * rowWidth ) - ( COLUMN_SPACING * 2 );
                    this.setLayoutConstraint( portletColumnPart, portletColumnPart.getFigure(), rowData );
                    portletColumnPart.refresh();
                }
            }
        }

        gridLayout.numColumns = numColumns;

        this.getFigure().repaint();
    }

}
