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

import static com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods.supportsExtOrWebTypePlugin;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PluginTypeValidationService extends ValidationService
{

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getProjectProvider().attach( listener );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        try
        {
            SDK sdk = SDKUtil.getWorkspaceSDK();

            final NewLiferayPluginProjectOp op = op();

            if( sdk != null )
            {
                if( op.getPluginType().content().equals( PluginType.web ) && !supportsExtOrWebTypePlugin( op, "web" ) )
                {
                    retval = Status.createErrorStatus(
                        "The selected Plugins SDK does not support creating new web type plugins.  " +
                            "Please configure version 7.0 or greater." );
                }
                else if( op.getPluginType().content().equals( PluginType.ext ) &&
                    !supportsExtOrWebTypePlugin( op, "ext" ) )
                {
                    retval = Status.createErrorStatus(
                        "The selected Plugins SDK does not support creating ext type plugins.  " +
                            "Please configure version 6.2 or less." );
                }
            }
            else if( op.getPluginType().content().equals( PluginType.ext ) && !supportsExtOrWebTypePlugin( op, "ext" ) )
            {
                retval = Status.createErrorStatus( "The Maven does not support creating ext type plugins." );
            }
        }
        catch( CoreException e )
        {
        }

        return retval;
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

}