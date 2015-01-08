/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All riproghts reserved.
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

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.server.core.LiferayServerCore;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishOperation;

/**
 * @author Gregory Amerson
 */
public class BundlePublishFullRemove extends PublishOperation
{

    private final IServer server;
    private final PortalRuntime portalRuntime;
    private final List<IModule> modules;
    private final PortalServerBehavior portalServerBehavior;

    public BundlePublishFullRemove( IServer s, IModule[] modules )
    {
        this.server = s;
        this.modules = Arrays.asList( modules );
        this.portalRuntime = (PortalRuntime) this.server.getRuntime().loadAdapter( PortalRuntime.class, null );

        if( this.portalRuntime == null )
        {
            throw new IllegalArgumentException( "Could not get portal runtime from server " + s.getName() );
        }

        this.portalServerBehavior = (PortalServerBehavior) this.server.loadAdapter( PortalServerBehavior.class, null );

        if( this.portalServerBehavior == null )
        {
            throw new IllegalArgumentException( "Could not get portal server behavior from server " + s.getName() );
        }
    }

    public int getKind()
    {
        return REQUIRED;
    }

    public void execute( IProgressMonitor monitor, IAdaptable info ) throws CoreException
    {
        for( IModule module : modules )
        {
            IStatus status = null;

            final ModulePublisher publisher = LiferayCore.create( ModulePublisher.class, module.getProject() );

            if( publisher != null )
            {
                status = publisher.remove( server, module );
                this.portalServerBehavior.setModulePublishState2( new IModule[] { module }, IServer.PUBLISH_STATE_NONE );
            }
            else
            {
                status =
                    LiferayServerCore.error( "Could not get module publisher for project " +
                        module.getProject().getName() );
            }

            if( !status.isOK() )
            {
                throw new CoreException( status );
            }
        }
    }

    @Override
    public int getOrder()
    {
        return 0;
    }
}
