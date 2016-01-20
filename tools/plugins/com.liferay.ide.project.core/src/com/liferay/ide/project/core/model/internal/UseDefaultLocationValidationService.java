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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
/**
 * @author Kuo Zhang
 */
public class UseDefaultLocationValidationService extends ValidationService
{
    private Listener listener = null;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.listener = new FilteredListener<Event>()
        {
            @Override
            protected void handleTypedEvent( Event event )
            {
                refresh();
            }
        };

        final NewLiferayPluginProjectOp op = op();

        op.getProjectProvider().attach( this.listener );
        op.getProjectName().attach( this.listener );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayPluginProjectOp op = op();

        if( op.getProjectName().content() != null &&
            "ant".equals( op.getProjectProvider().content().getShortName() ) &&
            ! op.getUseDefaultLocation().content() )
        {
            if( ! NewLiferayPluginProjectOpMethods.canUseCustomLocation( op ) )
            {
                retval = Status.createErrorStatus( "The specified SDK version is not allowed to use custom location." );
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        final NewLiferayPluginProjectOp op = op();

        if ( op() != null && !op().disposed() )
        {
            op.getProjectProvider().detach( this.listener );
            op.getProjectName().detach( this.listener );
        }
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}
