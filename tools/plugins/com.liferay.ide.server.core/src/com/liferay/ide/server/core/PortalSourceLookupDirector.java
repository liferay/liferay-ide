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
package com.liferay.ide.server.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.ServerUtil;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class PortalSourceLookupDirector extends JavaSourceLookupDirector
{

    private ILaunchConfiguration configuration;
    private String sourceComputerId;

    public PortalSourceLookupDirector( ILaunchConfiguration configuration, String sourceComputerId )
    {
        super();

        this.configuration = configuration;
        this.sourceComputerId = sourceComputerId;
    }

    public void configureLaunch( final ILaunch launch ) throws CoreException
    {
        final IServer server = ServerUtil.getServer( configuration );

        server.addServerListener
        (
            new IServerListener()
            {
                IModule[] modules = server.getModules();

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

                public synchronized void serverChanged( ServerEvent event )
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
                                LiferayServerCore.logError( "Unable to update source containers for server", e ); //$NON-NLS-1$
                            }

                            modules = newModules;
                        }
                    }
                }
            }
        );

        final ISourcePathComputer sourcePathComputer = getLaunchManager().getSourcePathComputer( this.sourceComputerId );
        this.setSourcePathComputer( sourcePathComputer );
        this.initializeDefaults( configuration );

        final String memento = configuration.getAttribute( ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, (String) null);

        if( memento != null )
        {
            this.initializeFromMemento( memento, configuration );
        }

        launch.setSourceLocator( this );
    }

    private ILaunchManager getLaunchManager()
    {
        return DebugPlugin.getDefault().getLaunchManager();
    }

    @Override
    public void initializeParticipants()
    {
        super.initializeParticipants();

        for( PortalLaunchParticipant participant : LiferayServerCore.getPortalLaunchParticipants() )
        {
            addParticipants( participant.getPortalSourceLookupParticipants() );
        }
    }

    @Override
    public boolean isFindDuplicates()
    {
        return true;
    }

}
