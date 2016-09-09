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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.gradleware.tooling.toolingclient.GradleDistribution;
import com.gradleware.tooling.toolingmodel.repository.FixedRequestAttributes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.buildship.core.configuration.ProjectConfiguration;
import org.eclipse.buildship.core.launch.GradleRunConfigurationAttributes;
import org.eclipse.buildship.core.projectimport.ProjectImportConfiguration;
import org.eclipse.buildship.core.util.file.FileUtils;
import org.eclipse.buildship.core.util.gradle.GradleDistributionWrapper;
import org.eclipse.buildship.core.util.progress.AsyncHandler;
import org.eclipse.buildship.core.util.variable.ExpressionUtils;
import org.eclipse.buildship.core.workspace.NewProjectHandler;
import org.eclipse.buildship.core.workspace.SynchronizeGradleProjectJob;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

/**
 * @author Andy Wu
 * @author Lovett Li
 */
public class GradleUtil
{

    private static GradleRunConfigurationAttributes getRunConfigurationAttributes( IProject project, String task )
    {
        ProjectConfiguration projectConfiguration =
            CorePlugin.projectConfigurationManager().readProjectConfiguration( project );

        FixedRequestAttributes attrs = projectConfiguration.getRequestAttributes();

        Optional<FixedRequestAttributes> requestAttributes = Optional.of( attrs );

        List<String> tasks = new ArrayList<String>();
        tasks.add( task );

        String projectDirectoryExpression = ExpressionUtils.encodeWorkspaceLocation( project );

        boolean isPresent = requestAttributes.isPresent();

        GradleDistribution gradleDistribution =
            isPresent ? requestAttributes.get().getGradleDistribution() : GradleDistribution.fromBuild();

        //String gradleUserHome =
        //    isPresent ? FileUtils.getAbsolutePath( requestAttributes.get().getGradleUserHome() ).orNull() : null;

        String javaHome =
            isPresent ? FileUtils.getAbsolutePath( requestAttributes.get().getJavaHome() ).orNull() : null;

        List<String> jvmArguments = isPresent ? requestAttributes.get().getJvmArguments() : ImmutableList.<String> of();
        List<String> arguments = isPresent ? requestAttributes.get().getArguments() : ImmutableList.<String> of();

        boolean showExecutionView = true;
        boolean showConsoleView = true;

        return GradleRunConfigurationAttributes.with(
            tasks, projectDirectoryExpression, gradleDistribution, javaHome, jvmArguments, arguments,
            showExecutionView, showConsoleView );
    }

    public static IStatus importGradleProject( File dir, IProgressMonitor monitor ) throws CoreException
    {
        ProjectImportConfiguration configuration = new ProjectImportConfiguration();
        GradleDistributionWrapper from = GradleDistributionWrapper.from( GradleDistribution.fromBuild() );

        configuration.setGradleDistribution( from );
        configuration.setProjectDir( dir );
        configuration.setApplyWorkingSets( false );

        SynchronizeGradleProjectJob synchronizeGradleProjectJob = new SynchronizeGradleProjectJob(
            configuration.toFixedAttributes(), null, AsyncHandler.NO_OP );

        synchronizeGradleProjectJob.setUser( true );

        return synchronizeGradleProjectJob.run( monitor );
    }

    public static boolean isBuildFile( IFile buildFile )
    {
        return buildFile != null && buildFile.exists() && "build.gradle".equals( buildFile.getName() ) &&
            buildFile.getParent() instanceof IProject;
    }

    public static boolean isGradleProject( Object resource ) throws CoreException
    {
        IProject project = null;

        if( resource instanceof IFile )
        {
            project = ( (IFile) resource ).getProject();

        }
        else if( resource instanceof IProject )
        {
            project = (IProject) resource;
        }
        return project != null && project.exists() && project.isAccessible() &&
            ( project.hasNature( GradleProjectNature.ID ) );
    }

    public static void refreshGradleProject( IProject project )
    {
        Set<IProject> projects = new HashSet<>();

        projects.add( project );

        CorePlugin.gradleWorkspaceManager().getCompositeBuild( projects ).synchronize(
            NewProjectHandler.IMPORT_AND_MERGE );
    }

    public static void runGradleTask( IProject project, String task, IProgressMonitor monitor ) throws CoreException
    {
        ILaunchConfiguration launchConfiguration =
            CorePlugin.gradleLaunchConfigurationManager().getOrCreateRunConfiguration(
                getRunConfigurationAttributes( project, task ) );

        final ILaunchConfigurationWorkingCopy launchConfigurationWC = launchConfiguration.getWorkingCopy();

        launchConfigurationWC.setAttribute( "org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", true );
        launchConfigurationWC.setAttribute( "org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", true );
        launchConfigurationWC.setAttribute( "org.eclipse.debug.ui.ATTR_PRIVATE", true );

        launchConfigurationWC.doSave();

        launchConfigurationWC.launch( ILaunchManager.RUN_MODE, monitor );
    }
}
