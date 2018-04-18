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
package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.ServerUtil;


/**
 * @author Gregory Amerson
 * @author Charles Wu
 */
public class PortalServerLaunchConfigDelegate extends AbstractJavaLaunchConfigurationDelegate
{

    public static final String ID = "com.liferay.ide.server.portal.launch";

    @Override
    public void launch( ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {
        final IServer server = ServerUtil.getServer( config );

        if( server != null )
        {
            IRuntime runtime = server.getRuntime();

            if ( runtime == null ) {
                throw new CoreException( LiferayServerCore.createErrorStatus("Server runtime is invalid.") );
            }

            PortalRuntime portalRuntime = (PortalRuntime) runtime.loadAdapter( PortalRuntime.class, monitor );

            if ( portalRuntime == null ) {
                throw new CoreException( LiferayServerCore.createErrorStatus("Server portal runtime is invalid.") );
            }

            IStatus status = portalRuntime.validate();

            if( !status.isOK() )
            {
                throw new CoreException( status );
            }

            launchServer( server, config, mode, launch, monitor );
        }
    }

    private void launchServer(
        final IServer server, final ILaunchConfiguration config, final String mode, final ILaunch launch,
        final IProgressMonitor monitor ) throws CoreException
    {
        final IVMInstall vm = verifyVMInstall( config );

        final IVMRunner runner =
            vm.getVMRunner( mode ) != null ? vm.getVMRunner( mode ) : vm.getVMRunner( ILaunchManager.RUN_MODE );

        final File workingDir = verifyWorkingDirectory( config );
        final String workingDirPath = workingDir != null ? workingDir.getAbsolutePath() : null;

        final String progArgs = getProgramArguments( config );
        final String vmArgs = getVMArguments( config );
        final String[] envp = getEnvironment( config );

        final ExecutionArguments execArgs = new ExecutionArguments( vmArgs, progArgs );

        final Map<String, Object> vmAttributesMap = getVMSpecificAttributesMap( config );

        final PortalServerBehavior portalServer =
            (PortalServerBehavior) server.loadAdapter ( PortalServerBehavior.class, monitor );

        final String classToLaunch = portalServer.getClassToLaunch();
        final String[] classpath = getClasspath( config );

        final VMRunnerConfiguration runConfig = new VMRunnerConfiguration( classToLaunch, classpath );
        runConfig.setProgramArguments( execArgs.getProgramArgumentsArray() );
        runConfig.setVMArguments( execArgs.getVMArgumentsArray() );
        runConfig.setWorkingDirectory( workingDirPath );
        runConfig.setEnvironment( envp );
        runConfig.setVMSpecificAttributesMap( vmAttributesMap );

        final String[] bootpath = getBootpath( config );

        if( ListUtil.isNotEmpty(bootpath) )
        {
            runConfig.setBootClassPath( bootpath );
        }

        portalServer.launchServer( launch, mode, monitor );

        server.addServerListener(new IServerListener()
        {
            @Override
            public void serverChanged( ServerEvent event )
            {
                if( ( event.getKind() & ServerEvent.MODULE_CHANGE ) > 0 )
                {
                    AbstractSourceLookupDirector sourceLocator = (AbstractSourceLookupDirector) launch.getSourceLocator();

                    try
                    {
                        final String memento =
                            config.getAttribute( ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String) null );

                        if( memento != null )
                        {
                            sourceLocator.initializeFromMemento( memento );
                        }
                        else
                        {
                            sourceLocator.initializeDefaults( config );
                        }
                    }
                    catch( CoreException e )
                    {
                        LiferayServerCore.logError( "Could not reinitialize source lookup director", e );
                    }
                }
                else if((event.getKind() & ServerEvent.SERVER_CHANGE)>0 && event.getState() == IServer.STATE_STOPPED)
                {
                    server.removeServerListener( this );
                }
            }
        });

        try
        {
            runner.run( runConfig, launch, monitor );
            portalServer.addProcessListener( launch.getProcesses()[0] );
        }
        catch( Exception e )
        {
            portalServer.cleanup();
        }
    }

}
