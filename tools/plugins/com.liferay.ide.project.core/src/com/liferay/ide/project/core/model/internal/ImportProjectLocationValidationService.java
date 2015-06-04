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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.model.SDKProjectsImportOp30;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class ImportProjectLocationValidationService extends ValidationService
{
    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final Path currentProjectLocation = op().getLocation().content( true );

        if( currentProjectLocation != null && !currentProjectLocation.isEmpty())
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
                        retval = Status.createErrorStatus( "Project isn't exist at \"" + currentPath + "\"" ); //$NON-NLS-1$
                    }
                    else
                    {
                        ProjectRecord record = ProjectUtil.getProjectRecordForDir( currentPath );

                        if( record != null )
                        {
                            String projectName = record.getProjectName();
                            IProject existingProject =
                                ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );

                            if( existingProject != null && existingProject.exists() )
                            {
                                retval = Status.createErrorStatus( "Project name already exists." );
                            }

                            File projectDir = record.getProjectLocation().toFile();

                            SDK sdk = SDKUtil.getSDKFromProjectDir( projectDir );

                            if( sdk != null )
                            {
                                retval = Status.createOkStatus();
                            }
                            else
                            {
                                retval = Status.createErrorStatus( "Project is not located inside Liferay Plugins SDK" );
                            }

                            final SDK workspaceSdk = getSdkInWorkspace();

                            if( workspaceSdk != null )
                            {
                                if ( !workspaceSdk.getLocation().equals( sdk.getLocation() ) )
                                {
                                    retval = Status.createErrorStatus("The import project has different sdk with current workspace");
                                }
                            }
                        }
                        else
                        {
                            retval = Status.createErrorStatus( "Invalid project location" );
                        }
                    }
                }
            }
        }
        else
        {
            retval = Status.createErrorStatus( "Location must be specified." ); //$NON-NLS-1$
        }

        return retval;
    }

    private SDK getSdkInWorkspace()
    {
        SDK retVal = null;
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for( IProject project : projects )
        {
            IPath projectLocation = project.getLocation();

            if ( SDKUtil.isValidSDKLocation( projectLocation.toPortableString() ) )
            {
                retVal = SDKUtil.createSDKFromLocation( projectLocation );
                break;
            }
        }
        return retVal; 
    }

    private SDKProjectsImportOp30 op()
    {
        return context( SDKProjectsImportOp30.class );
    }
}