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
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class LocationValidationService extends ValidationService
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

        if( !op().getUseDefaultLocation().content( true ) )
        {
            final Path currentProjectLocation = op().getLocation().content( false );
            final String currentProjectName = op().getProjectName().content();

            /*
             * IDE-1150, instead of using annotation "@Required",use this service to validate the custom project
             * location must be specified, let the wizard display the error of project name when project name and
             * location are both null.
             */
            if( currentProjectName != null )
            {
                if( currentProjectLocation != null )
                {
                    String currentPath = currentProjectLocation.toOSString();

                    final IProject handle = CoreUtil.getWorkspaceRoot().getProject( currentProjectName );

                    if( !org.eclipse.core.runtime.Path.EMPTY.isValidPath( currentPath ) )
                    {
                        retval = Status.createErrorStatus( "\"" + currentPath + "\" is not a valid path." );
                    }
                    else
                    {
                        IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

                        if( !osPath.toFile().isAbsolute() )
                        {
                            retval = Status.createErrorStatus( "\"" + currentPath + "\" is not an absolute path." );
                        }
                        else
                        {
                            if( !osPath.toFile().exists() )
                            {
                                // check non-existing external location
                                if( !canCreate( osPath.toFile() ) )
                                {
                                    retval =
                                        Status.createErrorStatus( "Cannot create project content at \"" + 
                                             currentPath + "\"" );
                                }
                            }

                            // validate the location
                            final IStatus locationStatus =
                                CoreUtil.getWorkspace().validateProjectLocation( handle, osPath );

                            if( !locationStatus.isOK() )
                            {
                                retval =
                                    Status.createErrorStatus( "\"" + currentPath +
                                        "\" is not a valid project location." );
                            }
                        }
                    }
                }
                else
                {
                    retval = Status.createErrorStatus( "Location must be specified." );
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
            op().getProjectName().detach( this.listener );

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

        op().getProjectName().attach( this.listener );
    }

    private NewLiferayAndroidProjectOp op()
    {
        return context( NewLiferayAndroidProjectOp.class );
    }

}
