/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.util.SDKUtil;
import com.liferay.ide.server.core.AbstractPluginPublisher;
import com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Greg Amerson
 */
public class TomcatExtPluginPublisher extends AbstractPluginPublisher
{

    public TomcatExtPluginPublisher()
    {
        super();
    }

    public TomcatExtPluginPublisher( String facetId )
    {
        super( facetId );
    }

    public IStatus canPublishModule( IServer server, IModule module )
    {
        // check to make sure that the user isn't trying to add multiple
        // ext-plugins to server
        if( ILiferayTomcatConstants.PREVENT_MULTI_EXT_PLUGINS_DEPLOY && module != null && server != null )
        {
            if( ProjectUtil.isExtProject( module.getProject() ) )
            {
                for( IModule currentModule : server.getModules() )
                {
                    if( ProjectUtil.isExtProject( currentModule.getProject() ) )
                    {
                        return LiferayTomcatPlugin.createErrorStatus( "Portal can only have on Ext-plugin deployed at a time." );
                    }
                }
            }
        }

        return Status.OK_STATUS;
    }

    public boolean prePublishModule(
        ServerBehaviourDelegate delegate, int kind, int deltaKind, IModule[] moduleTree, IModuleResourceDelta[] delta,
        IProgressMonitor monitor )
    {

        if( kind == IServer.PUBLISH_AUTO )
        {
            LiferayTomcatUtil.displayToggleMessage(
                "The Ext plugin does not support auto-publishing.  To redeploy changes from this plugin you will need to manually publish the server from the Servers view.",
                LiferayTomcatPlugin.PREFERENCES_ADDED_EXT_PLUGIN_TOGGLE_KEY );

            return false;
        }

        if( kind == IServer.PUBLISH_CLEAN || moduleTree == null )
        {
            return false;
        }

        try
        {
            if( deltaKind == ServerBehaviourDelegate.ADDED || deltaKind == ServerBehaviourDelegate.CHANGED )
            {
                addExtModule( delegate, moduleTree[0], monitor );
            }
            else if( deltaKind == ServerBehaviourDelegate.REMOVED )
            {
                // nothing to do right now
                // removeExtModule(delegate, moduleTree[0], monitor);
            }
        }
        catch( Exception e )
        {
            LiferayTomcatPlugin.logError( "Failed pre-publishing ext module.", e );
            return false;
        }

        return true;
    }

    protected void addExtModule( ServerBehaviourDelegate delegate, IModule module, IProgressMonitor monitor )
        throws CoreException
    {

        SDK sdk = null;
        IProject project = module.getProject();

        sdk = SDKUtil.getSDK( project );

        if( sdk == null )
        {
            throw new CoreException(
                LiferayTomcatPlugin.createErrorStatus( "No SDK for project configured. Could not deploy ext module" ) );
        }

        String mode =
            delegate.getServer().getServerState() == IServer.STATE_STARTED ? delegate.getServer().getMode() : null;

        if( mode != null )
        {
            LiferayTomcatUtil.syncStopServer( delegate.getServer() );
        }

        Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( project );

        IStatus status = sdk.directDeploy( project, null, true, appServerProperties );

        assertStatus( status );

        if( mode != null )
        {
            delegate.getServer().start( mode, monitor );
        }
    }

    protected void assertStatus( IStatus status ) throws CoreException
    {
        if( status == null )
        {
            throw new CoreException( LiferayTomcatPlugin.createErrorStatus( "null status" ) );
        }

        if( !status.isOK() )
        {
            throw new CoreException( status );
        }
    }

    protected void removeExtModule( ServerBehaviourDelegate delegate, IModule module, IProgressMonitor monitor )
        throws CoreException
    {
    }

}
