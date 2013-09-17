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
package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;
import com.liferay.ide.core.util.CoreUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class LocationValidationService extends ValidationService
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

        if( ! op().getUseDefaultLocation().content( true ) )
        {
            final Path currentProjectLocation = op().getLocation().content( false );
            final String currentProjectName = op().getProjectName().content();

            if( currentProjectLocation != null )
            {
                String currentPath = currentProjectLocation.toOSString();

                final IProject handle= CoreUtil.getWorkspaceRoot().getProject( currentProjectName );

                if( ! org.eclipse.core.runtime.Path.EMPTY.isValidPath( currentPath ) )
                {
                    retval = Status.createErrorStatus( "error 1" );
                }
                else
                {
                    IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

                    if ( ! osPath.toFile().exists() )
                    {
                        // check non-existing external location
                        if ( ! canCreate( osPath.toFile() ) )
                        {
                            retval = Status.createErrorStatus( "error 2" );
                        }
                    }

                    // validate the location
                    final IStatus locationStatus= CoreUtil.getWorkspace().validateProjectLocation(handle, osPath);

                    if (!locationStatus.isOK())
                    {
                        retval = Status.createErrorStatus( "error 3" );
                    }
                }
            }
        }

        return retval;
    }

    private NewLiferayAndroidProjectOp op()
    {
        return context( NewLiferayAndroidProjectOp.class );
    }

}
