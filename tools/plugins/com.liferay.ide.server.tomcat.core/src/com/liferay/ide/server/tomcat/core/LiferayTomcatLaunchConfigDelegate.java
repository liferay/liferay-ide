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
import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.debug.core.fm.FMDebugTarget;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.PortalSourceLookupDirector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatLaunchConfigurationDelegate;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
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
                    retval += NLS.bind( FM_PARAMS, ILRDebugConstants.FM_DEBUG_PASSWORD, ILRDebugConstants.FM_DEBUG_PORT );
                }
            }
            catch( CoreException e )
            {
            }
        }

        return retval;
    }

    @Override
    public void launch( ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor )
        throws CoreException
    {
        final ISourceLookupDirector sourceLocator = new PortalSourceLookupDirector();
        final ISourcePathComputer sourcePathComputer =
            getLaunchManager().getSourcePathComputer( LiferayTomcatSourcePathComputer.ID );
        sourceLocator.setSourcePathComputer( sourcePathComputer );
        sourceLocator.initializeDefaults( configuration );
        launch.setSourceLocator( sourceLocator );

        this.saveLaunchMode = mode;
        super.launch( configuration, mode, launch, monitor );
        this.saveLaunchMode = null;

        final String stopServer = configuration.getAttribute(STOP_SERVER, FALSE);

        if( ILaunchManager.DEBUG_MODE.equals( mode ) && FALSE.equals( stopServer ) )
        {
            final IServer server = ServerUtil.getServer( configuration );

            final IDebugTarget target = new FMDebugTarget( server.getHost(), launch, launch.getProcesses()[0] );
            launch.addDebugTarget( target );
        }
    }
}
