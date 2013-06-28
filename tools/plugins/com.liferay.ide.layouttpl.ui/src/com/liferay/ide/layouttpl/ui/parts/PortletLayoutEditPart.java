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
import com.liferay.ide.layouttpl.ui.draw2d.PortletLayoutPanel;
import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.layouttpl.ui.policies.PortletLayoutLayoutEditPolicy;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
public class PortletLayoutEditPart extends BaseGraphicalEditPart
{

    protected class PortletLayoutLayoutListener implements LayoutListener
    {

        public void invalidate( IFigure container )
        {
        }

        public boolean layout( IFigure container )
        {
            return false;
        }

        public void postLayout( IFigure container )
        {
            if( needsRefreshPostLayout )
            {
                needsRefreshPostLayout = false;
                refreshVisuals();
            }

        }

        public void remove( IFigure child )
        {
        }

        public void setConstraint( IFigure child, Object constraint )
        {
        }

    }
    public static final int COLUMN_SPACING = 10;

    public static final int LAYOUT_MARGIN = 10;

    public static GridData createGridData()
    {
        return new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 );
    }

    protected LayoutListener layoutListener = new PortletLayoutLayoutListener();

    protected PortletLayoutPanel layoutPanel;

    protected boolean needsRefreshPostLayout;

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

    public void propertyChange( PropertyChangeEvent evt )
    {
        String prop = evt.getPropertyName();

        if( PortletLayout.COLUMN_ADDED_PROP.equals( prop ) || PortletLayout.COLUMN_REMOVED_PROP.equals( prop ) ||
            PortletLayout.CHILD_COLUMN_WEIGHT_CHANGED_PROP.equals( prop ) )
        {
            refreshChildren();
            refreshVisuals();
        }
    }

    private PortletRowLayoutEditPart getCastedParent()
    {
        return (PortletRowLayoutEditPart) getParent();
    }

    @Override
    protected void createEditPolicies()
    {
        installEditPolicy( EditPolicy.LAYOUT_ROLE, new PortletLayoutLayoutEditPolicy() );
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

        layoutPanel.addLayoutListener( layoutListener );

        return layoutPanel;
    }

    protected PortletLayoutPanel getCastedFigure()
    {
        return (PortletLayoutPanel) getFigure();
    }

    protected PortletLayout getCastedModel()
    {
        return (PortletLayout) getModel();
    }

    protected List<ModelElement> getModelChildren()
    {
        return getCastedModel().getColumns(); // return a list of columns
    }

    protected LayoutTplDiagram getParentModel()
    {
        return (LayoutTplDiagram) getParent().getModel();
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
        if( numColumns > 1 )
        {
            // get width of our own part to calculate new width
            int rowWidth = this.getFigure().getSize().width - ( PortletLayoutEditPart.LAYOUT_MARGIN * 2 );

            if( rowWidth > 0 )
            {
                for( Object col : columns )
                {
                    PortletColumnEditPart portletColumnPart = (PortletColumnEditPart) col;
                    PortletColumn column = (PortletColumn) portletColumnPart.getModel();
                    // if (column.getWeight() == PortletColumn.DEFAULT_WEIGHT) {
                    // column.setWeight(100);
                    // }
                    GridData rowData = portletColumnPart.createGridData();

                    double percent = column.getWeight() / 100d;
                    rowData.widthHint = (int) ( percent * rowWidth ) - ( COLUMN_SPACING * 2 );
                    this.setLayoutConstraint( portletColumnPart, portletColumnPart.getFigure(), rowData );
                }
            }
            else
            {
                this.needsRefreshPostLayout = true;
            }
        }

        gridLayout.numColumns = numColumns;
        gridLayout.invalidate();

        this.getFigure().repaint();
    }

}
