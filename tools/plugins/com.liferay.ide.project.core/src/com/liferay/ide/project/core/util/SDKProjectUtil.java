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
package com.liferay.ide.project.core.util;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Simon Jiang
 */

public class SDKProjectUtil
{
    public static IStatus validateProjectPath(final String currentPath)
    {
        IStatus retVal = Status.OK_STATUS;

        if( !org.eclipse.core.runtime.Path.EMPTY.isValidPath( currentPath ) )
        {
            retVal = ProjectCore.createErrorStatus( "\"" + currentPath + "\" is not a valid path." ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            IPath osPath = org.eclipse.core.runtime.Path.fromOSString( currentPath );

            if( !osPath.toFile().isAbsolute() )
            {
                retVal = ProjectCore.createErrorStatus( "\"" + currentPath + "\" is not an absolute path." ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                if( !osPath.toFile().exists() )
                {
                    retVal = ProjectCore.createErrorStatus( "Project isn't exist at \"" + currentPath + "\"" ); //$NON-NLS-1$
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
                            retVal = ProjectCore.createErrorStatus( "Project name already exists." );
                        }
                        else
                        {
                            File projectDir = record.getProjectLocation().toFile();

                            SDK sdk = SDKUtil.getSDKFromProjectDir( projectDir );

                            if( sdk != null )
                            {
                                try
                                {
                                    IProject workspaceSdkProject = SDKUtil.getWorkspaceSDKProject();

                                    if( workspaceSdkProject != null )
                                    {
                                        if ( !workspaceSdkProject.getLocation().equals( sdk.getLocation() ) )
                                        {
                                            return ProjectCore.createErrorStatus("This project has different sdk than current workspace sdk");
                                        }
                                    }
                                }
                                catch( CoreException e )
                                {
                                    return ProjectCore.createErrorStatus("Can't find sdk in workspace");
                                }
                                retVal = sdk.validate();
                            }
                            else
                            {
                                retVal = ProjectCore.createErrorStatus( "SDK is not exist" );
                            }
                        }
                    }
                    else
                    {
                        retVal = ProjectCore.createErrorStatus( "Invalid project location" );
                    }
                }
            }
        }
        return retVal;
    }

}
