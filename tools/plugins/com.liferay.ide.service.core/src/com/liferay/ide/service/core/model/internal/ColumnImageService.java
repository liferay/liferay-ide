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

package com.liferay.ide.service.core.model.internal;

import com.liferay.ide.service.core.model.Column;

import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.services.ImageService;
import org.eclipse.sapphire.services.ImageServiceData;

/**
 * @author Gregory Amerson
 */
public class ColumnImageService extends ImageService
{

    private static final ImageData IMG_COLUMN = ImageData.readFromClassLoader(
        ColumnImageService.class, "images/column_16x16.gif" ).required(); //$NON-NLS-1$

    private static final ImageData IMG_COLUMN_PRIMARY = ImageData.readFromClassLoader(
        ColumnImageService.class, "images/column_primary_16x16.png" ).required(); //$NON-NLS-1$

    private Listener listener;

    @Override
    protected void initImageService()
    {

        this.listener = new FilteredListener<PropertyEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyEvent event )
            {
                refresh();
            }
        };

        context( Element.class ).attach( this.listener, Column.PROP_PRIMARY.name() );

        attach
        (
            new Listener()
            {
                @Override
                public void handle( Event event )
                {
                    if( event instanceof DisposeEvent )
                    {
                        context( Element.class ).detach( listener, Column.PROP_PRIMARY.name() );
                    }
                }
            }
        );
    }

    @Override
    public ImageServiceData compute()
    {
        ImageData imageData = null;

        if( ( context( Column.class ) ).isPrimary().content() )
        {
            imageData = IMG_COLUMN_PRIMARY;
        }
        else
        {
            imageData = IMG_COLUMN;
        }

        return new ImageServiceData( imageData );
    }

}
