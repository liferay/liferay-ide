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
package com.liferay.ide.maven.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.server.remote.AbstractRemoteServerPublisher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;

/**
 * @author Simon Jiang
 */
public class MavenProjectRemoteServerPublisher extends AbstractRemoteServerPublisher
{
    private final String ATTR_GOALS = "M2_GOALS";
    private final String ATTR_POM_DIR = IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
    private final String ATTR_PROFILES = "M2_PROFILES";
    private final String ATTR_SKIP_TESTS = "M2_SKIP_TESTS";
    private final String ATTR_UPDATE_SNAPSHOTS = "M2_UPDATE_SNAPSHOTS";
    private final String ATTR_WORKSPACE_RESOLUTION = "M2_WORKSPACE_RESOLUTION";
    private final String LAUNCH_CONFIGURATION_TYPE_ID = "org.eclipse.m2e.Maven2LaunchConfigurationType";

    public MavenProjectRemoteServerPublisher( IProject project )
    {
        super( project );
    }

    private String getMavenDeployGoals()
    {
        return "package war:war";
    }

    @Override
    public void processResourceDeltas(
        final IModuleResourceDelta[] deltas, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries,
        final String deletePrefix, final String deltaPrefix, final boolean adjustGMTOffset )
        throws IOException, CoreException
    {
        for( final IModuleResourceDelta delta : deltas )
        {
            final int deltaKind = delta.getKind();
            final IResource deltaResource = (IResource) delta.getModuleResource().getAdapter( IResource.class );
            final IProject deltaProject = deltaResource.getProject();
            final IVirtualFolder webappRoot = CoreUtil.getDocroot( deltaProject );
            final IPath deltaFullPath = deltaResource.getFullPath();

            boolean deltaZip = false;
            IPath deltaPath = null;

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
                final IFolder[] folders = CoreUtil.getSrcFolders( deltaProject );

                for( IFolder folder : folders )
                {
                    final IPath folderPath = folder.getFullPath();

                    if ( folderPath.isPrefixOf( deltaFullPath ) )
                    {
                        deltaZip = true;
                        break;
                    }
                }
            }

            if( deltaZip == false && ( deltaKind == IModuleResourceDelta.ADDED ||
                                       deltaKind == IModuleResourceDelta.CHANGED ||
                                       deltaKind == IModuleResourceDelta.REMOVED ) )
            {
                final IPath targetPath = JavaCore.create( deltaProject ).getOutputLocation();

                deltaZip = true;
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
                    final IModuleResourceDelta[] children = delta.getAffectedChildren();
                    processResourceDeltas( children, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset );
                }
            }
        }
    }

    public IPath publishModuleFull( IProgressMonitor monitor ) throws CoreException
    {
        IPath retval = null;

        if( runMavenGoal( getProject(), monitor ) )
        {
            final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );
            final MavenProject mavenProject = projectFacade.getMavenProject( monitor );
            final String targetFolder = mavenProject.getBuild().getDirectory();
            final String targetWar = mavenProject.getBuild().getFinalName() + "." + mavenProject.getPackaging();

            retval = new Path( targetFolder ).append( targetWar );
        }

        return retval;
    }

    private boolean runMavenGoal(
        final IProject project, final IProgressMonitor monitor )
        throws CoreException
    {
        IProject execProject = project;

        IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( project, monitor );

        String pluginType = MavenUtil.getLiferayMavenPluginType( projectFacade.getMavenProject( monitor ) );
        
        List<IFile> serviceXmls = ( new SearchFilesVisitor() ).searchFiles( project, "service.xml" );

        final MavenProject parentProject = projectFacade.getMavenProject( monitor ).getParent();
        String goal = getMavenDeployGoals();

        if( serviceXmls != null && serviceXmls.size() > 0 &&
            pluginType.equalsIgnoreCase( ILiferayMavenConstants.DEFAULT_PLUGIN_TYPE ) && parentProject != null )
        {
            execProject = ProjectUtil.getProject( parentProject.getName() );
            projectFacade = MavenUtil.getProjectFacade( execProject, monitor );
            goal = " package -am -pl " + project.getName();
        }

        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        ILaunchConfigurationType launchConfigurationType =
            launchManager.getLaunchConfigurationType( LAUNCH_CONFIGURATION_TYPE_ID );

        IPath basedirLocation = execProject.getLocation();

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
}
