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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class ImportWorkspaceLocationValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        try
        {
            if( LiferayWorkspaceUtil.hasLiferayWorkspace() )
            {
                retval = Status.createErrorStatus( LiferayWorkspaceUtil.hasLiferayWorkspaceMsg );

                return retval;
            }
        }
        catch( CoreException e )
        {
            retval = Status.createErrorStatus( LiferayWorkspaceUtil.multiWorkspaceErrorMsg );

            return retval;
        }

        final Path currentProjectLocation = op().getWorkspaceLocation().content( true );

        if( currentProjectLocation != null && !currentProjectLocation.isEmpty() )
        {
            final String currentPath = currentProjectLocation.toOSString();

            retval = StatusBridge.create( LiferayWorkspaceUtil.validateWorkspacePath( currentPath ) );

            if( !retval.ok() )
                return retval;

            String projectName = currentProjectLocation.lastSegment();

            if( CoreUtil.getProject( projectName ).exists() )
            {
                retval = Status.createErrorStatus( "A project with that name already exists." );

                return retval;
            }
        }

        return retval;
    }

    private ImportLiferayWorkspaceOp op()
    {
        return context( ImportLiferayWorkspaceOp.class );
    }

}
