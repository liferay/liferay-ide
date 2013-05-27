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

package com.liferay.ide.layouttpl.core.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cindy Li
 */
public class PortletRowLayoutElement extends ModelElement implements PropertyChangeListener
{

    public static final String ROW_ADDED_PROP = "LayoutTplDiagram.RowAdded"; //$NON-NLS-1$
    public static final String ROW_REMOVED_PROP = "LayoutTplDiagram.RowRemoved"; //$NON-NLS-1$

    protected List<ModelElement> rows = new ArrayList<ModelElement>();

    public void addRow( PortletLayoutElement newRow )
    {
        addRow( newRow, -1 );
    }

    public boolean addRow( PortletLayoutElement newRow, int index )
    {
        if( newRow != null )
        {
            if( index < 0 )
            {
                rows.add( newRow );
            }
            else
            {
                rows.add( index, newRow );
            }

            newRow.setParent( this );
            newRow.addPropertyChangeListener( this );

            this.updateColumns();
            this.firePropertyChange( ROW_ADDED_PROP, null, newRow );

            return true;
        }

        return false;
    }

    public List<ModelElement> getRows()
    {
        return rows;
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        String prop = evt.getPropertyName();

        if( PortletLayoutElement.COLUMN_ADDED_PROP.equals( prop ) || PortletLayoutElement.COLUMN_REMOVED_PROP.equals( prop ) )
        {
            updateColumns();
        }
    }

    @Override
    public void removeChild( ModelElement child )
    {
        if( rows.contains( child ) )
        {
            removeRow( (PortletLayoutElement) child );
        }
    }

    public boolean removeRow( PortletLayoutElement existingRow )
    {
        if( existingRow != null && rows.remove( existingRow ) )
        {
            firePropertyChange( ROW_REMOVED_PROP, null, existingRow );

            return true;
        }

        return false;
    }

    protected void updateColumns()
    {
        ModelElement diagram = this;

        while( diagram != null && !( diagram instanceof LayoutTplDiagramElement ) )
        {
            diagram = diagram.getParent();
        }

        if( diagram instanceof LayoutTplDiagramElement )
        {
            ( (LayoutTplDiagramElement)diagram ).updateColumns( 1 );
        }
        else
        {
            this.updateColumns( 1 );
        }
    }

    protected int updateColumns( int numIdCount )
    {
        for( ModelElement row : ( this.getRows() ) )
        {
            List<ModelElement> cols = ( (PortletLayoutElement) row ).getColumns();

            for( int i = 0; i < cols.size(); i++ )
            {
                PortletColumnElement col = ( (PortletColumnElement) cols.get( i ) );

                if( col.getRows().isEmpty() )
                {
                    col.setNumId( numIdCount++ );
                }
                else
                {
                    numIdCount = col.updateColumns( numIdCount++ );
                }

                if( i == 0 && cols.size() > 1 )
                {
                    col.setFirst( true );
                    col.setLast( false );
                }
                else if( cols.size() > 1 && i == ( cols.size() - 1 ) )
                {
                    col.setLast( true );
                    col.setFirst( false );
                }
                else
                {
                    col.setFirst( false );
                    col.setLast( false );
                }
            }
        }

        return numIdCount;
    }
}
