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
package com.liferay.ide.maven.ui;

import com.liferay.ide.core.adapter.LaunchAdapter;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.ILiferayMavenConstants;
import com.liferay.ide.maven.core.IMavenProject;
import com.liferay.ide.maven.core.MavenProjectBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 *
 * Maven project builder for maven projects less then 6.2.0 version, 6.2 or greater uses MavenProjectBuilder
 */
@SuppressWarnings( "restriction" )
public class MavenUIProjectBuilder extends MavenProjectBuilder
{

    public MavenUIProjectBuilder( IProject project )
    {
        super( project );
    }

    public MavenUIProjectBuilder( IMavenProject liferayMavenProject )
    {
        super( liferayMavenProject.getProject() );
    }

    @Override
    public IStatus buildLang( IFile langFile, IProgressMonitor monitor ) throws CoreException
    {
        final IProgressMonitor sub = CoreUtil.newSubMonitor( monitor, 100 );

        sub.beginTask( Msgs.buildingLanguages, 100 );

        final IFile pomFile = getProject().getFile( new Path( IMavenConstants.POM_FILE_NAME ) );
        final IMavenProjectFacade projectFacade = projectManager.create( pomFile, false, new NullProgressMonitor() );

        sub.worked( 10 );

        final IStatus retval = runMavenGoal( projectFacade, ILiferayMavenConstants.PLUGIN_GOAL_BUILD_LANG, "run", monitor );

        sub.done();

        return retval;
    }

    @Override
    public IStatus buildSB( IFile serviceXmlFile, final String goal, IProgressMonitor monitor ) throws CoreException
    {
        final IFile pomFile = getProject().getFile( new Path( IMavenConstants.POM_FILE_NAME ) );
        final IMavenProjectFacade projectFacade = projectManager.create( pomFile, false, new NullProgressMonitor() );

        monitor.worked( 10 );

        IStatus status = runMavenGoal( projectFacade, goal, "run", monitor );

        refreshSiblingProject( projectFacade, monitor );

        monitor.worked( 10 );
        monitor.done();

        return status;
    }

    public IStatus runMavenGoal( final IMavenProjectFacade projectFacade,
                                 final String goal,
                                 final String mode,
                                 final IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        ILaunchConfigurationType launchConfigurationType =
            launchManager.getLaunchConfigurationType( MavenLaunchConstants.LAUNCH_CONFIGURATION_TYPE_ID );

        IPath basedirLocation = getProject().getLocation();

        String newName = launchManager.generateLaunchConfigurationName( basedirLocation.lastSegment() );

        final ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance( null, newName );

        workingCopy.setAttribute( MavenLaunchConstants.ATTR_POM_DIR, basedirLocation.toString() );
        workingCopy.setAttribute( MavenLaunchConstants.ATTR_GOALS, goal );
        workingCopy.setAttribute( MavenLaunchConstants.ATTR_UPDATE_SNAPSHOTS, true );
        workingCopy.setAttribute( MavenLaunchConstants.ATTR_WORKSPACE_RESOLUTION, true );
        workingCopy.setAttribute( MavenLaunchConstants.ATTR_SKIP_TESTS, true );

        if( projectFacade != null )
        {
            final ResolverConfiguration configuration = projectFacade.getResolverConfiguration();

            final String selectedProfiles = configuration.getSelectedProfiles();

            if( selectedProfiles != null && selectedProfiles.length() > 0 )
            {
                workingCopy.setAttribute( MavenLaunchConstants.ATTR_PROFILES, selectedProfiles );
            }

            /*
             * <?xml version="1.0" encoding="UTF-8" standalone="no"?> <launchConfiguration
             * type="org.eclipse.m2e.Maven2LaunchConfigurationType"> <booleanAttribute key="M2_DEBUG_OUTPUT"
             * value="false"/> <booleanAttribute key="M2_NON_RECURSIVE" value="false"/> <booleanAttribute
             * key="M2_OFFLINE" value="false"/> <stringAttribute key="M2_PROFILES" value="v6.2.0"/> <listAttribute
             * key="M2_PROPERTIES"/> <stringAttribute key="M2_RUNTIME" value="EMBEDDED"/> <intAttribute key="M2_THREADS"
             * value="1"/> <stringAttribute key="org.eclipse.jdt.launching.WORKING_DIRECTORY"
             * value="D:/dev java/workspaces/runtime-eclipse-ide-juno-sr2/WorldDatabase/WorldDatabase-portlet"/>
             * </launchConfiguration>
             */
            UIJob launchJob = new UIJob( "maven launch" )
            {
                @Override
                public IStatus runInUIThread( IProgressMonitor monitor )
                {
                    DebugUITools.launch( workingCopy, mode );

                    return Status.OK_STATUS;
                }
            };

            final boolean[] launchTerminated = new boolean[1];
            final ILaunchListener[] listener = new ILaunchListener[1];

            listener[0] = new LaunchAdapter()
            {
                public void launchChanged( final ILaunch launch )
                {
                    if( launch.getLaunchConfiguration().equals( workingCopy ) )
                    {
                        Thread t = new Thread()
                        {
                            @Override
                            public void run()
                            {
                                while( launch.getProcesses().length > 0 && ! launch.getProcesses()[0].isTerminated() )
                                {
                                    try
                                    {
                                        sleep(100);
                                    }
                                    catch( InterruptedException e )
                                    {
                                    }
                                }

                                launchTerminated[0] = true;
                                DebugPlugin.getDefault().getLaunchManager().removeLaunchListener( listener[0] );
                            }
                        };

                        t.start();
                    }
                }
            };

            DebugPlugin.getDefault().getLaunchManager().addLaunchListener( listener[0] );

            launchJob.schedule();

            // make sure that we aren't on display thread before sleeping
            while( Display.getCurrent() == null && ! launchTerminated[0] )
            {
                try
                {
                    Thread.sleep( 100 );
                }
                catch( InterruptedException e )
                {
                }
            }
        }

        return retval;
    }

}
