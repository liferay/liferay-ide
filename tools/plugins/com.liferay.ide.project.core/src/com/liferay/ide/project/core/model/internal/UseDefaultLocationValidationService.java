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
import com.liferay.ide.project.core.util.ProjectUtil;

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
            protected void handleTypedEvent( Event event )
            {
                refresh();
            }
        };

        context( NewLiferayPluginProjectOp.class ).getProjectProvider().attach( this.listener );
        context( NewLiferayPluginProjectOp.class ).getPluginsSDKName().attach( this.listener );
        context( NewLiferayPluginProjectOp.class ).getProjectName().attach( this.listener );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayPluginProjectOp op = context( NewLiferayPluginProjectOp.class );

        if( op.getProjectName().content() != null && 
            "ant".equals( op.getProjectProvider().content().getShortName() ) &&
            ! op.getUseDefaultLocation().content() )
        {
            if( ! ProjectUtil.canUseCustomLocation( op ) )
            {
                retval = Status.createErrorStatus( "The sdk of lower version is not allowed to use custom location." );
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        context( NewLiferayPluginProjectOp.class ).getProjectProvider().detach( this.listener );
        context( NewLiferayPluginProjectOp.class ).getPluginsSDKName().detach( this.listener );
        context( NewLiferayPluginProjectOp.class ).getProjectName().detach( this.listener );
        
    }

}
