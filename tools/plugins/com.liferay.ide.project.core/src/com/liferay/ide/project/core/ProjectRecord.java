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
/**
 *
 */

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.StringPool;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class ProjectRecord
{

    public IProjectDescription description;

    public File liferayProjectDir;

    public File projectSystemFile;

    boolean hasConflicts;

    int level;

    Object parent;

    IProject project;

    String projectName;

    /**
     * Create a record for a project based on the info in the file.
     *
     * @param file
     */
    public ProjectRecord( File file )
    {
        if( file.isDirectory() )
        {
            liferayProjectDir = file;
        }
        else
        {
            projectSystemFile = file;
        }

        setProjectName();
    }

    public ProjectRecord( IProject preSelectedProject )
    {
        this.project = preSelectedProject;

        setProjectName();
    }

    /**
     * @param file
     *            The Object representing the .project file
     * @param parent
     *            The parent folder of the .project file
     * @param level
     *            The number of levels deep in the provider the file is
     */
    ProjectRecord( Object file, Object parent, int level )
    {
        this.parent = parent;

        this.level = level;

        setProjectName();
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj instanceof ProjectRecord )
        {
            if( this.project != null )
            {
                return this.project.equals( ( (ProjectRecord) obj ).project );
            }
        }

        return super.equals( obj );
    }

    /**
     * Gets the label to be used when rendering this project record in the UI.
     *
     * @return String the label
     * @since 3.4
     */
    public String getProjectLabel()
    {
        if( description == null )
            return projectName;

        String path =
            projectSystemFile != null ? projectSystemFile.getParent() : ( liferayProjectDir != null
                ? liferayProjectDir.getPath() : ( project != null
                    ? new Path( project.getLocationURI().getPath() ).toOSString() : StringPool.EMPTY ) );

        return NLS.bind( "{0} ({1})", projectName, path ); //$NON-NLS-1$
    }

    public IPath getProjectLocation()
    {
        if( this.projectSystemFile != null )
        {
            return new Path( this.projectSystemFile.getParent() );
        }
        else if( this.liferayProjectDir != null )
        {
            return new Path( this.liferayProjectDir.getPath() );
        }
        else if( this.project != null )
        {
            return this.project.getRawLocation();
        }

        return null;
    }

    /**
     * Get the name of the project
     *
     * @return String
     */
    public String getProjectName()
    {
        return projectName;
    }

    /**
     * @return Returns the hasConflicts.
     */
    public boolean hasConflicts()
    {
        return hasConflicts;
    }

    public void setHasConflicts( boolean b )
    {
        this.hasConflicts = b;
    }

    /**
     * Returns whether the given project description file path is in the default location for a project
     *
     * @param path
     *            The path to examine
     * @return Whether the given path is the default location for a project
     */
    private boolean isDefaultLocation( IPath path )
    {
        // The project description file must at least be within the project,
        // which is within the workspace location
        if( path.segmentCount() < 2 )
        {
            return false;
        }

        return path.removeLastSegments( 2 ).toFile().equals( Platform.getLocation().toFile() );
    }

    /**
     * Set the name of the project based on the projectFile.
     */
    private void setProjectName()
    {
        try
        {
            // If we don't have the project name try again
            if( projectName == null )
            {
                if( projectSystemFile != null )
                {
                    IPath path = new Path( projectSystemFile.getPath() );

                    // if the file is in the default location, use the directory
                    // name as the project name
                    if( isDefaultLocation( path ) )
                    {
                        projectName = path.segment( path.segmentCount() - 2 );

                        description = ResourcesPlugin.getWorkspace().newProjectDescription( projectName );
                    }
                    else
                    {
                        description = ResourcesPlugin.getWorkspace().loadProjectDescription( path );

                        projectName = description.getName();
                    }
                }
                else if( liferayProjectDir != null )
                {
                    IPath path = new Path( liferayProjectDir.getPath() );

                    projectName = path.lastSegment();

                    description = ResourcesPlugin.getWorkspace().newProjectDescription( projectName );
                }
                else if( project != null )
                {
                    projectName = project.getName();

                    description = project.getDescription();
                }

            }
        }
        catch( CoreException e )
        {
            // no good couldn't get the name
        }
    }

}
