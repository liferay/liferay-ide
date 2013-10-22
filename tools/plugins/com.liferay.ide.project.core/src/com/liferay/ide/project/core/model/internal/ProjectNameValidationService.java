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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class ProjectNameValidationService extends ValidationService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        listener = new FilteredListener<PropertyContentEvent>()
        {
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().attach( listener, "*" ); //$NON-NLS-1$
    }

    @Override
    public void dispose()
    {
        super.dispose();

        op().detach( listener, "*" );
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final String currentProjectName = op().getProjectName().content();

        if( currentProjectName != null )
        {
            final IStatus nameStatus = CoreUtil.getWorkspace().validateName( currentProjectName, IResource.PROJECT );
            final IProject handle = CoreUtil.getProject( currentProjectName );

            if( ! nameStatus.isOK() )
            {
                retval = StatusBridge.create( nameStatus );
            }
            else if( handle.exists() )
            {
                retval = Status.createErrorStatus( "A project with that name already exists." );  //$NON-NLS-1$
            }
            else
            {
                final Path currentProjectLocation = op().getLocation().content( true );

                // double check to make sure this project wont overlap with existing dir
                if( currentProjectName != null && currentProjectLocation != null )
                {
                    final String currentPath = currentProjectLocation.toOSString();
                    final IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

                    if( osPath.append(".project").toFile().exists() ) //$NON-NLS-1$
                    {
                        retval = Status.createErrorStatus( "\"" + currentPath + //$NON-NLS-1$
                                "\" is not a valid because a project already exists at that location." ); //$NON-NLS-1$
                    }
                    else
                    {
                        final File pathFile = osPath.toFile();

                        if( pathFile.exists() && pathFile.isDirectory() && pathFile.listFiles().length > 0 )
                        {
                            retval = Status.createErrorStatus( "\"" + currentPath + //$NON-NLS-1$
                                    "\" is not a valid because it already contains files." ); //$NON-NLS-1$
                        }
                    }
                }
            }
        }

        return retval;
    }

}
