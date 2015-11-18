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
package com.liferay.ide.project.ui.upgrade;


import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;

/**
 * @author Lovett Li
 */
public class PreviousLiferayLocationValidationService extends ValidationService
{
    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final Path sourceLiferayLocation = op().getSourceLiferayLocation().content( true );

        if( sourceLiferayLocation != null && !sourceLiferayLocation.isEmpty() )
        {
            final String sourcePath = sourceLiferayLocation.toOSString();
            PortalBundle portalBundle =
                LiferayServerCore.getPortalBundle( new org.eclipse.core.runtime.Path( sourcePath ) );

            if( portalBundle == null || !portalBundle.getVersion().startsWith( "6.2" ) )
            {
                retval = Status.createErrorStatus( "This is not protal 6.2 project." );
            }

        }

        return retval;
    }

    private CopyPortalSettingsOp op()
    {
        return context( CopyPortalSettingsOp.class );
    }

}
