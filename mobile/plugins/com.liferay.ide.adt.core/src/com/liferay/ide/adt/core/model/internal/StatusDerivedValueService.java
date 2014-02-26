/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;
import com.liferay.mobile.sdk.core.MobileSDKCore;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;


/**
 * @author Gregory Amerson
 */
public class StatusDerivedValueService extends DerivedValueService
{
    private String status = "unknown";
    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initDerivedValueService()
    {
        super.initDerivedValueService();

        final MobileSDKLibrariesOp op = op();

        listener = new FilteredListener<PropertyContentEvent>()
        {
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                restoreStatus();
            };
        };

        op.attach( listener, "Url" );
        op.attach( listener, "OmniUsername" );
        op.attach( listener, "OmniPassword" );
    }

    @Override
    public void dispose()
    {
        final MobileSDKLibrariesOp op = op();

        op.detach( listener, "Url" );
        op.detach( listener, "OmniUsername" );
        op.detach( listener, "OmniPassword" );
    }

    protected String checkValue()
    {
        String retval = null;

        final MobileSDKLibrariesOp op = op();

        final Object serverStatus = MobileSDKCore.checkServerStatus(
            op.getUrl().content(), op.getOmniUsername().content(), op.getOmniPassword().content() );

        if( serverStatus instanceof String )
        {
            retval = (String) serverStatus;
        }
        else if( serverStatus instanceof Integer )
        {
            retval = "OK";
        }

        return retval;
    }

    @Override
    protected String compute()
    {
        return this.status;
    }

    protected MobileSDKLibrariesOp op()
    {
        return context( MobileSDKLibrariesOp.class );
    }

    protected void restoreStatus()
    {
        this.status = "unknown";

        refresh();
    }

    protected void updateStatus()
    {
        final String newStatus = checkValue();

        if( ! StatusDerivedValueService.this.status.equals( newStatus ) )
        {
            StatusDerivedValueService.this.status = newStatus;

            if( ! op().disposed() )
            {
                refresh();
            }
        }
    }

}
