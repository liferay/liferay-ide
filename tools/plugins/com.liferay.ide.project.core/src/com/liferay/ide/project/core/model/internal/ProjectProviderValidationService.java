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
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Kuo Zhang
 * @author Tao Tao
 */
public class ProjectProviderValidationService extends ValidationService
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

      final NewLiferayPluginProjectOp op = op();

      op.getPluginsSDKName().attach( this.listener);
      op.getRuntimeName().attach( this.listener );
      op.getUseSdkLocation().attach( this.listener);
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayPluginProjectOp op = op();

        if( "ant".equals( op.getProjectProvider().content().getShortName() ) && !op.getUseSdkLocation().content() ) //$NON-NLS-1$
        {
            if( !NewLiferayPluginProjectOpMethods.canUseCustomLocation( op ) )
            {
                retval =
                    Status.createErrorStatus( "The selected Plugins SDK does not support using Eclipse workspace as base for project location.  Please configure a higher version." );
            }
        }

        final Status sdkNameStatus = op.getPluginsSDKName().validation();

        if( retval.ok() && !sdkNameStatus.ok() )
        {
            retval = sdkNameStatus;
        }
        else
        {
            final Status runtimeNameStatus = op.getRuntimeName().validation();

            if( retval.ok() && !runtimeNameStatus.ok() )
            {
                retval = runtimeNameStatus;
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        final NewLiferayPluginProjectOp op = op();

        op.getUseSdkLocation().detach( this.listener );
        op.getRuntimeName().detach( this.listener );
        op.getPluginsSDKName().detach( this.listener);
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

}
