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

import static org.springsource.ide.eclipse.gradle.core.util.JobUtil.NO_RULE;
import aQute.bnd.osgi.Jar;

import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IBundleProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.springsource.ide.eclipse.gradle.core.GradleCore;
import org.springsource.ide.eclipse.gradle.core.GradleProject;
import org.springsource.ide.eclipse.gradle.core.launch.GradleLaunchConfigurationDelegate;
import org.springsource.ide.eclipse.gradle.core.util.GradleRunnable;
import org.springsource.ide.eclipse.gradle.core.util.JobUtil;

/**
 * @author Gregory Amerson
 */
public class GradleBundlePluginProject extends BaseLiferayProject implements IBundleProject
{
    private static final String[] ignorePaths = new String[] { ".gradle", "build" };

    public GradleBundlePluginProject( IProject project )
    {
        super( project );
    }

    @Override
    public boolean filterResource( IPath resourcePath )
    {
        if( filterResource( resourcePath, ignorePaths ) )
        {
            return true;
        }

        return false;
    }

    @Override
    public IFile getDescriptorFile( String name )
    {
        return null;
    }

    @Override
    public IPath getLibraryPath( String filename )
    {
        return null;
    }

    @Override
    public IPath getOutputJar( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
    {
        IPath retval = null;

        final GradleProject gradleProject = GradleCore.create( getProject() );

        // need to rerun the jar task to make sure the jar is correct

        final String task = "jar";
        final ILaunchConfiguration conf =
            GradleLaunchConfigurationDelegate.createDefault( gradleProject, task, false );

        final GradleRunnable gradleRunnable = new GradleRunnable( task )
        {

            @Override
            public void doit( IProgressMonitor mon ) throws Exception
            {
                conf.launch( "run", mon, false, false );
            }
        };

        try
        {
            JobUtil.schedule( NO_RULE, gradleRunnable ).join();

            retval = GradleProjectMethods.getOutputJar( gradleProject );
        }
        catch( InterruptedException e )
        {
        };

        final IPath outputJar = GradleProjectMethods.getOutputJar( gradleProject );

        if( outputJar.toFile().exists() )
        {
            retval = outputJar;
        }

        return retval;
    }

    @Override
    public String getProperty( String key, String defaultValue )
    {
        return null;
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        String retval = null;

        final IPath outputJar = getOutputJar( false, new NullProgressMonitor() );

        if( outputJar != null && outputJar.toFile().exists() )
        {
            try( final Jar jar = new Jar( outputJar.toFile() ) )
            {
                retval = jar.getBsn();
            }
            catch( Exception e )
            {
            }
        }

        return retval;
    }

}
