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

package com.liferay.ide.project.core.workspace;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.internal.ServerPlugin;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class ServerNameValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        String serverName = op().getServerName().content();

        if( ServerPlugin.isNameInUse( null, serverName ) )
        {
            retval = Status.createErrorStatus(
                "The server or runtime name is already in use. Specify a different name." );
        }

        return retval;
    }

    private BaseLiferayWorkspaceOp op()
    {
        return context( BaseLiferayWorkspaceOp.class );
    }

}
