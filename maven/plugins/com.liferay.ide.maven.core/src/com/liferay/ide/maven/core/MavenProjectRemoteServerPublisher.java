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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.server.remote.AbstractRemoteServerPublisher;

import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;

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

    public IPath publishModuleFull( IProgressMonitor monitor ) throws CoreException
    {
        IPath retval = null;

        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );

        if( runMavenGoal( projectFacade, getMavenDeployGoals(), monitor ) )
        {
            final MavenProject mavenProject = projectFacade.getMavenProject( monitor );
            final String targetFolder = mavenProject.getBuild().getDirectory();
            final String targetWar = mavenProject.getBuild().getFinalName() + "." + mavenProject.getPackaging();

            retval = new Path( targetFolder ).append( targetWar );
        }

        return retval;
    }

    private boolean runMavenGoal(
        final IMavenProjectFacade projectFacade, final String goal, final IProgressMonitor monitor )
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

            new LaunchHelper().launch( workingCopy, "run", monitor );

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void processResourceDeltas(
        final IModuleResourceDelta[] deltas, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries, final String deletePrefix,
        final String deltaPrefix, final boolean adjustGMTOffset ) throws IOException, CoreException
    {
        for( final IModuleResourceDelta delta : deltas )
        {
            final int deltaKind = delta.getKind();
            final IResource deltaResource = (IResource) delta.getModuleResource().getAdapter( IResource.class );

            final IProject deltaProject = deltaResource.getProject();

            final IVirtualFolder webappRoot = CoreUtil.getDocroot( deltaProject );

            boolean deltaZip = false;
            IPath deltaPath = null;

            final IPath deltaFullPath = deltaResource.getFullPath();

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    if( container != null && container.exists()  )
                    {
                        final IPath containerFullPath = container.getFullPath();

                        if ( containerFullPath.isPrefixOf( deltaFullPath ))
                        {
                            deltaZip = true;
                            deltaPath = new Path( deltaPrefix + deltaFullPath.makeRelativeTo( containerFullPath ) );
                        }
                    }
                }
            }

            if ( deltaZip ==false && new Path("WEB-INF").isPrefixOf( delta.getModuleRelativePath() ))
            {
                IFolder[] folders = CoreUtil.getSrcFolders( deltaProject );
                for( IFolder folder : folders )
                {
                    IPath folderPath = folder.getFullPath();

                    if ( folderPath.isPrefixOf( deltaFullPath ) )
                    {
                        deltaZip = true;
                        break;
                    }
                }
            }

            if( deltaZip == false &&  ( deltaKind == IModuleResourceDelta.ADDED || deltaKind == IModuleResourceDelta.CHANGED ||
                            deltaKind == IModuleResourceDelta.REMOVED ) )
            {
                deltaZip = true;
                final JavaModelManager javaManager = JavaModelManager.getJavaModelManager();
                final IPath targetPath = javaManager.getJavaModel().getJavaProject( deltaProject.getName() ).getOutputLocation();
                deltaPath = new Path( "WEB-INF/classes" ).append( deltaFullPath.makeRelativeTo( targetPath ) );
            }

            if ( deltaZip )
            {
                if( deltaKind == IModuleResourceDelta.ADDED || deltaKind == IModuleResourceDelta.CHANGED )
                {
                    addToZip( deltaPath, deltaResource, zip, adjustGMTOffset );
                }
                else if( deltaKind == IModuleResourceDelta.REMOVED )
                {
                    addRemoveProps( deltaPath, deltaResource, zip, deleteEntries, deletePrefix );
                }
                else if( deltaKind == IModuleResourceDelta.NO_CHANGE )
                {
                    IModuleResourceDelta[] children = delta.getAffectedChildren();
                    processResourceDeltas( children, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset );
                }
            }

        }
    }
}
