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

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Andy Wu
 */
public class ResourceFilterUtil
{

    public static void addResourceFilter( IFolder folder, String filteredSubFolderName, IProgressMonitor monitor )
    {
        FileInfoMatcherDescription fmd = new FileInfoMatcherDescription(
            "org.eclipse.ui.ide.multiFilter", "1.0-name-matches-true-false-" + filteredSubFolderName );

        try
        {
            folder.createFilter( 10, fmd, IResource.BACKGROUND_REFRESH, monitor );
        }
        catch( CoreException e )
        {
            ProjectCore.logError( "add filter error", e );
        }
    }

    public static void deleteResourceFilter( IFolder parentFolder, String filteredSubFolderName )
    {
        try
        {
            IResourceFilterDescription[] resourceFilterDescriptions = parentFolder.getFilters();

            for( IResourceFilterDescription resourceFilterDescription : resourceFilterDescriptions )
            {
                Object argument = resourceFilterDescription.getFileInfoMatcherDescription().getArguments();

                if( argument.toString().contains( filteredSubFolderName ) )
                {
                    Job job = new WorkspaceJob( "delete project resource filter" )
                    {

                        @Override
                        public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                        {
                            resourceFilterDescription.delete( IResource.BACKGROUND_REFRESH, monitor );

                            return Status.OK_STATUS;
                        }
                    };

                    job.schedule();
                }
            }
        }
        catch( CoreException e )
        {
            ProjectCore.logError( "delete filter error", e );
        }
    }
}
