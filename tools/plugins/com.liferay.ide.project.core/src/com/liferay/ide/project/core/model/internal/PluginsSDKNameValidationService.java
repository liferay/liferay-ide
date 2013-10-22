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
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class PluginsSDKNameValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayPluginProjectOp op = context( NewLiferayPluginProjectOp.class );

        if( "ant".equals( op.getProjectProvider().content().getShortName() ) ) //$NON-NLS-1$
        {
            final String sdkName = op.getPluginsSDKName().content();

            final SDK sdk = SDKManager.getInstance().getSDK( sdkName );

            if( sdk == null )
            {
                retval = Status.createErrorStatus( "Plugins SDK must be configured." ); //$NON-NLS-1$
            }
            else if( ! sdk.isValid() )
            {
                retval = Status.createErrorStatus( "Plugins SDK " + sdkName + " is invalid." ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        return retval;
    }

}
