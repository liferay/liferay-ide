/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.maven.core;

import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.server.remote.AbstractRemoteServerPublisher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class MavenProjectRemoteServerPublisher extends AbstractRemoteServerPublisher
{
    private final String LAUNCH_CONFIGURATION_TYPE_ID = "org.eclipse.m2e.Maven2LaunchConfigurationType";
    private final String ATTR_POM_DIR = IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
    private final String ATTR_GOALS = "M2_GOALS";
    private final String ATTR_UPDATE_SNAPSHOTS = "M2_UPDATE_SNAPSHOTS";
    private final String ATTR_WORKSPACE_RESOLUTION = "M2_WORKSPACE_RESOLUTION";
    private final String ATTR_SKIP_TESTS = "M2_SKIP_TESTS";
    private final String ATTR_PROFILES = "M2_PROFILES";

    public MavenProjectRemoteServerPublisher( IProject project )
    {
        super( project );
    }

    private String getMavenDeployGoals()
    {
        return "package war:war";
    }

    public IPath publishModuleFull( IProject project, IProgressMonitor monitor ) throws CoreException
    {
        final IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
        IFile pomFile = project.getFile( IMavenConstants.POM_FILE_NAME );
        final IMavenProjectFacade projectFacade = projectManager.create( pomFile, false, new NullProgressMonitor() );
        return runMavenGoal( projectFacade, getMavenDeployGoals(), "run", monitor );
    }

    private IPath runMavenGoal(
        final IMavenProjectFacade projectFacade, final String goal, final String mode, final IProgressMonitor monitor )
        throws CoreException
    {

        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        ILaunchConfigurationType launchConfigurationType =
            launchManager.getLaunchConfigurationType( LAUNCH_CONFIGURATION_TYPE_ID );

        IPath basedirLocation = getProject().getLocation();

        String newName = launchManager.generateLaunchConfigurationName( basedirLocation.lastSegment() );

        final ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance( null, newName );

        workingCopy.setAttribute( ATTR_POM_DIR, basedirLocation.toString() );
        workingCopy.setAttribute( ATTR_GOALS, goal );
        workingCopy.setAttribute( ATTR_UPDATE_SNAPSHOTS, true );
        workingCopy.setAttribute( ATTR_WORKSPACE_RESOLUTION, true );
        workingCopy.setAttribute( ATTR_SKIP_TESTS, true );

        if( projectFacade != null )
        {
            final ResolverConfiguration configuration = projectFacade.getResolverConfiguration();

            final String selectedProfiles = configuration.getSelectedProfiles();

            if( selectedProfiles != null && selectedProfiles.length() > 0 )
            {
                workingCopy.setAttribute( ATTR_PROFILES, selectedProfiles );
            }

            new LaunchHelper().launch( workingCopy, mode, monitor );
        }
        final String targetFolder = projectFacade.getMavenProject().getBuild().getDirectory();
        final String targetWar = projectFacade.getMavenProject().getBuild().getFinalName() + "." +
                projectFacade.getMavenProject().getPackaging();

        return new Path( targetFolder ).append( targetWar );
    }

}
