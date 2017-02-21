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

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class WorkspaceLocationValidationService extends ValidationService
{
    private boolean canCreate( File file )
    {
        while( !file.exists() )
        {
            file = file.getParentFile();

            if( file == null )
            {
                return false;
            }
        }

        return file.canWrite();
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final Path currentProjectLocation = op().getLocation().content( true );
        final String currentWorkspaceName = op().getWorkspaceName().content();

        // Location won't be validated if the UseDefaultLocation has an error. Get the validation of the property might
        // not work as excepted, let's use call the validation service manually.
        if( !op().getUseDefaultLocation().content( true ) )
        {
            /*
             * IDE-1150, instead of using annotation "@Required",use this service to validate the custom project
             * location must be specified, let the wizard display the error of project name when project name and
             * location are both null.
             */
            if( currentWorkspaceName != null )
            {
                if( currentProjectLocation != null )
                {
                    final String currentPath = currentProjectLocation.append( currentWorkspaceName ).toOSString();

                    IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

                    if( !osPath.toFile().isAbsolute() )
                    {
                        return Status.createErrorStatus( "\"" + currentPath + "\" is not an absolute path." );
                    }

                    if( !canCreate( osPath.toFile() ) )
                    {
                        return Status.createErrorStatus( "Cannot create project content at \"" + currentPath + "\"." );
                    }
                }
                else
                {
                    return Status.createErrorStatus( "Location must be specified." );
                }
            }
        }

        return retval;
    }

    private NewLiferayWorkspaceOp op()
    {
        return context( NewLiferayWorkspaceOp.class );
    }
}
