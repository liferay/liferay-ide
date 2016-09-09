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

package com.liferay.ide.project.ui.upgrade.animated;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Andy Wu
 */
public class ConvertedProjectLocationValidationService extends ValidationService
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

        final Path currentProjectLocation = op().getConvertedProjectLocation().content( true );

        if( currentProjectLocation != null )
        {
            final String currentPath = currentProjectLocation.toPortableString();

            if( !org.eclipse.core.runtime.Path.EMPTY.isValidPath( currentPath ) )
            {
                retval = Status.createErrorStatus( "\"" + currentPath + "\" is not a valid path." ); 
            }
            else
            {
                IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

                if( ! osPath.toFile().isAbsolute() )
                {
                    retval = Status.createErrorStatus( "\"" + currentPath + "\" is not an absolute path." );
                }
                else
                {
                    if( ! osPath.toFile().exists() )
                    {
                        if( !canCreate( osPath.toFile() ) )
                        {
                            retval =
                                Status.createErrorStatus( "Cannot create project content at \"" + //$NON-NLS-1$
                                     currentPath + "\"" ); //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        else
        {
            retval = Status.createErrorStatus( "Converted Project Location must be specified." ); //$NON-NLS-1$
        }

        return retval;
    }

    private LiferayUpgradeDataModel op()
    {
        return context( LiferayUpgradeDataModel.class );
    }
}
