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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.AbstractLiferayProjectImporter;
import com.liferay.ide.core.util.CoreUtil;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * @author Andy Wu
 */
public class GradleModuleProjectImporter extends AbstractLiferayProjectImporter
{
    private IProject refreshProject = null;

    @Override
    public IStatus canImport( String location )
    {
        IStatus retval = null;

        File file = new File( location );

        if( findGradleFile( file ) )
        {
            if( findSettingsFile( file ) )
            {
                return Status.OK_STATUS;
            }
            else
            {
                File parent = file.getParentFile();

                while( parent != null )
                {
                    if( findGradleFile( parent ) )
                    {
                        File gradleFile = new File( file, "build.gradle" );
                        IPath gradleFilelocation = Path.fromOSString( gradleFile.getAbsolutePath() );
                        IFile ifile = CoreUtil.getWorkspaceRoot().getFileForLocation( gradleFilelocation );

                        if( ifile != null && ifile.getProject() != null )
                        {
                            refreshProject = ifile.getProject();

                            retval = new Status(
                                IStatus.WARNING, GradleCore.PLUGIN_ID,
                                "Project is inside \"" + refreshProject.getName() + "\" project. we will just refresh to import" );
                        }
                        else
                        {
                            retval = new Status(
                                IStatus.ERROR, GradleCore.PLUGIN_ID,
                                "Location is not the root location of a multi-module project." );
                        }

                        return retval;
                    }

                    parent = parent.getParentFile();
                }

                if( retval == null )
                {
                    return Status.OK_STATUS;
                }
            }
        }

        return retval;
    }

    private boolean findFile( File dir, String name )
    {
        boolean retval = false;

        if( dir.exists() )
        {
            File[] files = dir.listFiles();

            for( File file : files )
            {
                if( !file.isDirectory() && file.getName().equals( name ) )
                {
                    retval = true;
                }
            }
        }

        return retval;
    }

    private boolean findGradleFile( File dir )
    {
        return findFile( dir, "build.gradle" );
    }

    private boolean findSettingsFile( File dir )
    {
        return findFile( dir, "settings.gradle" );
    }

    @Override
    public void importProject( String location, IProgressMonitor monitor ) throws CoreException
    {
        if( refreshProject != null )
        {
            GradleUtil.refreshGradleProject( refreshProject );
        }
        else
        {
            GradleUtil.importGradleProject( new File( location ), monitor );
        }
        
    }

}
