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

import aQute.bnd.osgi.Jar;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.gradleware.tooling.toolingclient.GradleDistribution;
import com.gradleware.tooling.toolingmodel.repository.FixedRequestAttributes;
import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.gradle.toolingapi.custom.CustomModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.configuration.ProjectConfiguration;
import org.eclipse.buildship.core.launch.GradleRunConfigurationAttributes;
import org.eclipse.buildship.core.util.file.FileUtils;
import org.eclipse.buildship.core.util.variable.ExpressionUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class LiferayGradleProject extends BaseLiferayProject implements IBundleProject
{

    private static final String[] ignorePaths = new String[] { ".gradle", "build" };

    public LiferayGradleProject( IProject project )
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
    public IPath getOutputBundle( boolean build, IProgressMonitor monitor ) throws CoreException
    {
        IPath outputBundle = getOutputBundle( getProject() );

        if( !build && outputBundle != null && outputBundle.toFile().exists() )
        {
            return outputBundle;
        }
        else
        {
            final String task = isThemeProject( getProject() ) ? "build" : "jar";

            ILaunchConfiguration launchConfiguration =
                CorePlugin.gradleLaunchConfigurationManager().getOrCreateRunConfiguration(
                    getRunConfigurationAttributes( task ) );

            final ILaunchConfigurationWorkingCopy launchConfigurationWC = launchConfiguration.getWorkingCopy();

            launchConfigurationWC.setAttribute( "org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", true );
            launchConfigurationWC.setAttribute( "org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", false );
            launchConfigurationWC.setAttribute( "org.eclipse.debug.ui.ATTR_PRIVATE", true );

            final String jobTitle = "Building " + getProject().getName() + " output...";

            final Job job = new Job( jobTitle )
            {
                protected IStatus run( IProgressMonitor monitor )
                {
                    try
                    {
                        launchConfigurationWC.launch( ILaunchManager.RUN_MODE, monitor );
                    }
                    catch( Exception e )
                    {
                    }

                    return Status.OK_STATUS;
                }
            };

            monitor.subTask( jobTitle );

            job.schedule();

            try
            {
                job.join();
            }
            catch( InterruptedException e )
            {
            }

            outputBundle = getOutputBundle( getProject() );
        }

        if( outputBundle.toFile().exists() )
        {
            return outputBundle;
        }

        return null;
    }

    private boolean isThemeProject( IProject project )
    {
        return project.getFile( "gulpfile.js" ).exists() &&
            project.getFile( "package.json" ).exists() &&
            project.getFile( "src/WEB-INF/liferay-plugin-package.properties" ).exists();
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

        final IPath outputBundle = getOutputBundle( getProject() );

        if( outputBundle == null || outputBundle.lastSegment().endsWith( ".war" ) )
        {
            return getProject().getName();
        }
        else if( outputBundle != null && outputBundle.toFile().exists() )
        {
            try( final Jar jar = new Jar( outputBundle.toFile() ) )
            {
                retval = jar.getBsn();
            }
            catch( Exception e )
            {
            }
        }

        return retval;
    }

    public static IPath getOutputBundle( IProject gradleProject )
    {
        final CustomModel model = GradleCore.getToolingModel( CustomModel.class, gradleProject );

        Set<File> outputFiles = model.getOutputFiles();

        if( outputFiles.size() > 0 )
        {
            return new Path( outputFiles.iterator().next().getAbsolutePath() );
        }
        else if( model.hasPlugin( "com.liferay.gradle.plugins.gulp.GulpPlugin" ) )
        {
            return gradleProject.getLocation().append( "dist/" + gradleProject.getName() + ".war" );
        }

        return null;
    }

    private GradleRunConfigurationAttributes getRunConfigurationAttributes( String task )
    {
        ProjectConfiguration projectConfiguration =
            CorePlugin.projectConfigurationManager().readProjectConfiguration( getProject() );

        Optional<FixedRequestAttributes> requestAttributes = Optional.of( projectConfiguration.getRequestAttributes() );

        List<String> tasks = new ArrayList<String>();
        tasks.add( task );

        String projectDirectoryExpression = ExpressionUtils.encodeWorkspaceLocation( getProject() );

        boolean isPresent = requestAttributes.isPresent();

        GradleDistribution gradleDistribution =
            isPresent ? requestAttributes.get().getGradleDistribution() : GradleDistribution.fromBuild();

        String gradleUserHome =
            isPresent ? FileUtils.getAbsolutePath( requestAttributes.get().getGradleUserHome() ).orNull() : null;

        String javaHome =
            isPresent ? FileUtils.getAbsolutePath( requestAttributes.get().getJavaHome() ).orNull() : null;

        List<String> jvmArguments = isPresent ? requestAttributes.get().getJvmArguments() : ImmutableList.<String> of();
        List<String> arguments = isPresent ? requestAttributes.get().getArguments() : ImmutableList.<String> of();

        boolean showExecutionView = true;
        boolean showConsoleView = true;

        return GradleRunConfigurationAttributes.with(
            tasks, projectDirectoryExpression, gradleDistribution, gradleUserHome, javaHome, jvmArguments, arguments,
            showExecutionView, showConsoleView );
    }

}