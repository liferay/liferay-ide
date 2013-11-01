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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class RuntimeNameValidationService extends ValidationService
{

    private Listener listener = null;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.listener = new FilteredListener<Event>()
        {
            protected void handleTypedEvent( Event event )
            {
                refresh();
            }
        };

        context( NewLiferayPluginProjectOp.class ).getProjectProvider().attach( this.listener );;
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayPluginProjectOp op = context( NewLiferayPluginProjectOp.class );

        if( "ant".equals( op.getProjectProvider().content( true ).getShortName() ) ) //$NON-NLS-1$
        {
            final String runtimeName = op.getRuntimeName().content( true );

            final IRuntime runtime = ServerUtil.getRuntime( runtimeName );

            if( runtime == null )
            {
                retval = Status.createErrorStatus( "Liferay runtime must be configured." ); //$NON-NLS-1$
            }
            else
            {
                retval = StatusBridge.create( runtime.validate( new NullProgressMonitor() ) );
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        context( NewLiferayPluginProjectOp.class ).getProjectProvider().detach( this.listener );
    }
}
