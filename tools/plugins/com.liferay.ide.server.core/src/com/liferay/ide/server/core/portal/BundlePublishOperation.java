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

import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishOperation;

/**
 * @author Gregory Amerson
 */
public class BundlePublishOperation extends PublishOperation
{

    protected final List<IModule> modules;
    protected final PortalRuntime portalRuntime;
    protected final PortalServerBehavior portalServerBehavior;
    protected final IServer server;

    public BundlePublishOperation( IServer s, IModule[] modules )
    {
        this.server = s;
        this.modules = new ArrayList<IModule>( Arrays.asList( modules ) );
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

    public void addModule( IModule[] module )
    {
        for( IModule m : module )
        {
            this.modules.add( m );
        }
    }

    @Override
    public void execute( IProgressMonitor monitor, IAdaptable info ) throws CoreException
    {
    }

    @Override
	public int getKind()
    {
        return REQUIRED;
    }

    @Override
    public int getOrder()
    {
        return 0;
    }

    protected GogoBundleDeployer createBundleDeployer() throws Exception
    {
        return ServerUtil.createBundleDeployer( portalRuntime, server );
    }

}
