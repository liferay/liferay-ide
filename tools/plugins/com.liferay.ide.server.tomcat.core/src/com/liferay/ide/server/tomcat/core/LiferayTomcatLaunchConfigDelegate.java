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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.debug.core.LiferayDebugCore;
import com.liferay.ide.debug.core.fm.FMDebugTarget;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.PortalSourceLookupDirector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatLaunchConfigurationDelegate;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.ServerUtil;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcatLaunchConfigDelegate extends TomcatLaunchConfigurationDelegate
{
    private static final String STOP_SERVER = "stop-server"; //$NON-NLS-1$
    private static final String FALSE = "false"; //$NON-NLS-1$
    private static final String FM_PARAMS = " -Dfreemarker.debug.password={0} -Dfreemarker.debug.port={1}"; //$NON-NLS-1$

    private String saveLaunchMode;

    @Override
    public String getVMArguments( ILaunchConfiguration configuration ) throws CoreException
    {
        String retval = super.getVMArguments( configuration );

        String stopServer = configuration.getAttribute( STOP_SERVER, FALSE );

        if( ILaunchManager.DEBUG_MODE.equals( saveLaunchMode ) && FALSE.equals( stopServer ) )
        {
            try
            {
                final IServer server = ServerUtil.getServer( configuration );

                final ILiferayRuntime liferayRuntime =
                    (ILiferayRuntime) server.getRuntime().loadAdapter( ILiferayRuntime.class, null );

                final Version version = new Version( liferayRuntime.getPortalVersion() );

                if( CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
                {
                    retval +=
                        NLS.bind(
                            FM_PARAMS, LiferayDebugCore.getPreference( LiferayDebugCore.PREF_FM_DEBUG_PASSWORD ),
                            LiferayDebugCore.getPreference( LiferayDebugCore.PREF_FM_DEBUG_PORT ) );
                }
            }
            catch( CoreException e )
            {
            }
        }

        return retval;
    }

    @Override
    public void launch( final ILaunchConfiguration configuration, String mode, final ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {
        final IServer server = ServerUtil.getServer( configuration );

        if( ILaunchManager.DEBUG_MODE.equals( mode ) )
        {
            final PortalSourceLookupDirector sourceLocator = new PortalSourceLookupDirector();

            server.addServerListener
            (
                new IServerListener()
                {
                    IModule[] modules = server.getModules();

                    public void serverChanged( ServerEvent event )
                    {
                        if( ( event.getKind() & ServerEvent.MODULE_CHANGE ) > 0 )
                        {
                            IModule[] newModules = event.getServer().getModules();

                            if( modulesChanged( modules, newModules ) )
                            {
                                try
                                {
                                    final PortalSourceLookupDirector director =
                                        (PortalSourceLookupDirector) launch.getSourceLocator();
                                    director.initializeDefaults( configuration );
                                }
                                catch( Exception e )
                                {
                                    LiferayTomcatPlugin.logError( "Unable to update source containers for server", e ); //$NON-NLS-1$
                                }

                                modules = newModules;
                            }
                        }
                    }

                    private boolean modulesChanged( IModule[] modules, IModule[] modules2 )
                    {
                        if( CoreUtil.isNullOrEmpty( modules ) && CoreUtil.isNullOrEmpty( modules2 )  )
                        {
                            return true;
                        }

                        if( CoreUtil.isNullOrEmpty( modules ) || CoreUtil.isNullOrEmpty( modules2 ) )
                        {
                            return true;
                        }

                        if( modules.length != modules2.length )
                        {
                            return true;
                        }

                        for( int i = 0; i < modules.length; i++ )
                        {
                            if( ! modules[i].equals( modules2[i] ) )
                            {
                                return true;
                            }
                        }

                        return false;
                    }
                }
            );

            final ISourcePathComputer sourcePathComputer =
                getLaunchManager().getSourcePathComputer( LiferayTomcatSourcePathComputer.ID );
            sourceLocator.setSourcePathComputer( sourcePathComputer );
            sourceLocator.initializeDefaults( configuration );
            launch.setSourceLocator( sourceLocator );
        }

        this.saveLaunchMode = mode;
        super.launch( configuration, mode, launch, monitor );
        this.saveLaunchMode = null;

        final String stopServer = configuration.getAttribute( STOP_SERVER, FALSE );

        if( ILaunchManager.DEBUG_MODE.equals( mode ) && FALSE.equals( stopServer ) )
        {
            final IDebugTarget target = new FMDebugTarget( server.getHost(), launch, launch.getProcesses()[0] );
            launch.addDebugTarget( target );
        }
    }
}
