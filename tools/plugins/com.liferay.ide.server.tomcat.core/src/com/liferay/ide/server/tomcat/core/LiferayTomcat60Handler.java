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

import com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.tomcat.core.internal.Tomcat60Handler;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayTomcat60Handler extends Tomcat60Handler implements ILiferayTomcatHandler
{
    protected IServer currentServer;
    protected ILiferayTomcatServer portalServer;

    @Override
    public IStatus canAddModule( IModule module )
    {
        IStatus status = LiferayTomcatUtil.canAddModule( module, currentServer );

        if( !status.isOK() )
        {
            return status;
        }

        return super.canAddModule( module );
    }

    @Override
    public String[] getRuntimeVMArguments( IPath installPath, IPath configPath, IPath deployPath, boolean isTestEnv )
    {
        List<String> runtimeVMArgs = new ArrayList<String>();

        LiferayTomcatUtil.addRuntimeVMArgments(
            runtimeVMArgs, installPath, configPath, deployPath, isTestEnv, currentServer, getPortalServer() );

        Collections.addAll( runtimeVMArgs, super.getRuntimeVMArguments( installPath, configPath, deployPath, isTestEnv ) );

        return runtimeVMArgs.toArray( new String[runtimeVMArgs.size()] );
    }

    public void setCurrentServer( IServer server )
    {
        this.currentServer = server;
    }

    protected ILiferayTomcatServer getPortalServer()
    {
        if( this.portalServer == null )
        {
            this.portalServer = (ILiferayTomcatServer) getServer().loadAdapter( ILiferayTomcatServer.class, null );
        }

        return this.portalServer;
    }

    protected IServer getServer()
    {
        return this.currentServer;
    }

}
