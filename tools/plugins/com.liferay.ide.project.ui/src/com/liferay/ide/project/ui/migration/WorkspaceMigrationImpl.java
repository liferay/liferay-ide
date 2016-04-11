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

package com.liferay.ide.project.ui.migration;

import com.liferay.blade.api.MigrationConstants;
import com.liferay.blade.api.MigrationListener;
import com.liferay.blade.api.Problem;
import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 */
public class WorkspaceMigrationImpl implements MigrationListener
{

    @Override
    public void problemsFound( List<Problem> problems )
    {
        final IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();

        for( Problem problem : problems )
        {
            IResource workspaceResource = null;

            final File file = problem.file;

            final IResource[] containers = ws.findContainersForLocationURI( file.toURI() );

            if( containers != null && containers.length > 0 )
            {
                // prefer project containers
                for( IResource container : containers )
                {
                    if( container.exists() )
                    {
                        if( container.getType() == IResource.PROJECT )
                        {
                            workspaceResource = container;
                            break;
                        }
                        else
                        {
                            final IProject project = container.getProject();

                            if( CoreUtil.isLiferayProject( project ) )
                            {
                                workspaceResource = container;
                                break;
                            }
                        }
                    }
                }

                if( workspaceResource == null )
                {
                    final IFile[] files = ws.findFilesForLocationURI( file.toURI() );

                    for( IFile ifile : files )
                    {
                        if( ifile.exists() )
                        {
                            if( CoreUtil.isLiferayProject( ifile.getProject() ) )
                            {
                                workspaceResource = ifile;
                                break;
                            }
                        }
                    }
                }


                if( workspaceResource == null )
                {
                    for( IResource container : containers )
                    {
                        if( workspaceResource == null )
                        {
                            workspaceResource = container;
                        }
                        else
                        {
                            // prefer the path that is shortest (to avoid a nested version)
                            if( container.getLocation().segmentCount() < workspaceResource.getLocation().segmentCount() )
                            {
                                workspaceResource = container;
                            }
                        }
                    }
                }
            }

            if( workspaceResource != null && workspaceResource.exists() )
            {
                try
                {
                    if( shouldAddMarker( workspaceResource ) )
                    {
                        final IMarker marker =
                            workspaceResource.createMarker( MigrationConstants.MARKER_TYPE );

                        problem.setMarkerId( marker.getId() );
                        MigrationUtil.problemToMarker( problem, marker );
                    }
                }
                catch( CoreException e )
                {
                }
            }
        }

    }

    private boolean shouldAddMarker( IResource resource )
    {
        //remove problems in WEB-INF/classes folder
        if( resource.getFullPath().toString().contains( "WEB-INF/classes" ) )
        {
            return false;
        }

        return true;

    }

}
