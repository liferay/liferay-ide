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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.project.core.NewLiferayProjectProvider;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public abstract class AbstractProjectLocationValidationService<T extends ExecutableElement> extends ValidationService
{

    private Listener listener;

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

        final String currentProjectName = op().getProjectName().content();
        final Path currentProjectLocation = op().getLocation().content();
        final Boolean userDefaultLocation = op().getUseDefaultLocation().content();

        // Location won't be validated if the UseDefaultLocation has an error. Get the validation of the property might
        // not work as excepted, let's use call the validation service manually.
        if( !userDefaultLocation )
        {
            /*
             * IDE-1150, instead of using annotation "@Required",use this service to validate the custom project
             * location must be specified, let the wizard display the error of project name when project name and
             * location are both null.
             */
            if( currentProjectName != null )
            {
                if( currentProjectLocation != null )
                {
                    final String currentPath = currentProjectLocation.toOSString();

                    if( !org.eclipse.core.runtime.Path.EMPTY.isValidPath( currentPath ) )
                    {
                        retval = Status.createErrorStatus( "\"" + currentPath + "\" is not a valid path." ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

                        if( !osPath.toFile().isAbsolute() )
                        {
                            retval = Status.createErrorStatus( "\"" + currentPath + "\" is not an absolute path." ); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        else
                        {
                            if( !osPath.toFile().exists() )
                            {
                                // check non-existing external location
                                if( !canCreate( osPath.toFile() ) )
                                {
                                    retval = Status.createErrorStatus( "Cannot create project content at \"" + //$NON-NLS-1$
                                        currentPath + "\"" ); //$NON-NLS-1$
                                }
                            }

                            final IStatus locationStatus =
                                getProjectProvider().validateProjectLocation( currentProjectName, osPath );

                            if( !locationStatus.isOK() )
                            {
                                retval = Status.createErrorStatus( locationStatus.getMessage() ); // $NON-NLS-1$
                            }
                        }
                    }
                }
                else
                {
                    retval = Status.createErrorStatus( "Location must be specified." ); //$NON-NLS-1$
                }
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        super.dispose();

        if( this.listener != null )
        {
            op().detach( this.listener );
            this.listener = null;
        }
    }

    @Override
    protected void initValidationService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        attachListener( listener );
    }

    protected abstract NewLiferayProjectProvider<T> getProjectProvider();

    protected abstract void attachListener( final Listener listener );

    protected abstract BaseModuleOp op();
}
