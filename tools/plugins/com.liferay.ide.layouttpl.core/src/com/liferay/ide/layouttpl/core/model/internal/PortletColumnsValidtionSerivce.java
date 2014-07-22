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

package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import java.util.HashSet;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Kuo Zhang
 *
 */
public class PortletColumnsValidtionSerivce extends ValidationService
{
    private FilteredListener<PropertyContentEvent> listener;

    // store PortletColumns who have attached the listener
    private HashSet<PortletColumnElement> columnsAttachedListener;

    @Override
    protected void initValidationService()
    {
        columnsAttachedListener = new HashSet<PortletColumnElement>();

        listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        super.initValidationService();
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final PortletLayoutElement portletLayout = context( PortletLayoutElement.class );
        final LayoutTplElement layoutTpl = portletLayout.nearest( LayoutTplElement.class );

        int actualWeightSum = 0;
        int exceptedweightSum = layoutTpl.getBootstrapStyle().content() ? 12 : 100;

        for( PortletColumnElement col : portletLayout.getPortletColumns() )
        {
            // attach listener for the newly added PortletColumn
            // there should be a better way to do this which makes more sense
            if( ! columnsAttachedListener.contains( col ) )
            {
                col.getWeight().attach( this.listener );
                columnsAttachedListener.add( col );
            }

            actualWeightSum += col.getWeight().content().intValue();
        }

        // we need allow 99% ?
        if( !( actualWeightSum == exceptedweightSum || ( exceptedweightSum == 100 && actualWeightSum == 99 ) ) )
        {
            retval = Status.createErrorStatus( " The sum of weight of columns should be: " + exceptedweightSum );
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        for( PortletColumnElement column: columnsAttachedListener )
        {
            if( column != null && ! column.disposed() )
            {
                column.detach( this.listener );
            }
        }

        this.listener = null;

        super.dispose();
    }

}
