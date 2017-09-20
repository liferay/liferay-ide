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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.server.util.SocketUtil;

import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * @author Greg Amerson
 * @author Tao Tao
 */
@SuppressWarnings( "restriction" )
public class RemoteServer extends ServerDelegate implements IRemoteServerWorkingCopy
{

    private static final String CONNECT_ERROR_MSG = Msgs.notConnectRemoteIDEConnectorPlugin;

    public static final String ATTR_REMOTE_SERVER_MODULE_IDS_LIST = "remote-server-module-ids-list"; //$NON-NLS-1$

    public RemoteServer()
    {
        super();
    }

    public boolean canMakeHttpConnection()
    {
        String host = getServer().getHost();
        int http = getHttpPort();

        final Socket socket = new Socket();

        new Thread()
        {

            @Override
            public void run()
            {
                try
                {
                    sleep( 3000 );
                    socket.close();
                }
                catch( Exception e )
                {
                }
            }

        }.start();

        IStatus status = SocketUtil.canConnect( socket, host, http );

        if( status != null && status.isOK() )
        {
            return true;
        }
        else
        {
            final Socket socket1 = new Socket();
            status = SocketUtil.canConnectProxy( socket1, host, http );

            if( status != null && status.isOK() )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public IStatus canModifyModules( IModule[] add, IModule[] remove )
    {
        // check to see if we can add this to remote server
        if( !( CoreUtil.isNullOrEmpty( add ) ) )
        {
            for( IModule addModule : add )
            {
                IProject addModuleProject = addModule.getProject();

                if( ! CoreUtil.isLiferayProject( addModuleProject ) )
                {
                    return LiferayServerCore.error( Msgs.notAddNonLiferayPluginProjectModule );
                }

                IStatus facetStatus = FacetUtil.verifyFacets( addModuleProject, getServer() );

                if( facetStatus != null && !facetStatus.isOK() )
                {
                    return facetStatus;
                }

                // make sure that EXT is disabled for now for remote deployment
                if( ServerUtil.isExtProject( addModuleProject ) )
                {
                    return LiferayServerCore.error( Msgs.extPluginDeployment );
                }
            }

        }
        else if( !( CoreUtil.isNullOrEmpty( remove ) ) )
        {
            for( IModule removeModule : remove )
            {
                IProject removeModuleProject = removeModule.getProject();

                if( removeModuleProject == null )
                {
                    // something has happened to the project lets just remove it
                    return Status.OK_STATUS;
                }

                // if (!ProjectUtil.isLiferayProject(removeModuleProject)) {
                // return WebsphereCore.createErrorStatus("Cannot remove non Liferay plugin project.");
                // }

                IStatus facetStatus = FacetUtil.verifyFacets( removeModuleProject, getServer() );

                if( facetStatus != null && !facetStatus.isOK() )
                {
                    return facetStatus;
                }
            }
        }

        // ignore remove we can always try to remove;

        return Status.OK_STATUS;
    }

    public boolean getAdjustDeploymentTimestamp()
    {
        return getAttribute( ATTR_ADJUST_DEPLOYMENT_TIMESTAMP, DEFAULT_ADJUST_DEPLOYMENT_TIMESTAMP );
    }

    @Override
    public IModule[] getChildModules( IModule[] module )
    {
        if( CoreUtil.isNullOrEmpty( module ) )
        {
            return null;
        }

        List<IModule> childModules = new ArrayList<IModule>();

        for( IModule m : module )
        {
            IProject project = m.getProject();

            if( CoreUtil.isLiferayProject( project ) )
            {
                IWebModule webModule = (IWebModule) m.loadAdapter( IWebModule.class, null );
                if( webModule != null )
                {
                    IModule[] webModules = webModule.getModules();

                    if( !( CoreUtil.isNullOrEmpty( webModules ) ) )
                    {
                        for( IModule childWebModule : webModules )
                        {
                            childModules.add( childWebModule );
                        }
                    }
                }
            }
        }

        return childModules.toArray( new IModule[0] );
    }

    public String getHost()
    {
        return getServer().getHost();
    }

    public int getHttpPort()
    {
        return getAttribute( ATTR_HTTP_PORT, DEFAULT_HTTP_PORT );
    }

    public String getId()
    {
        return getServer().getId();
    }

    public String getLiferayPortalContextPath()
    {
        return getAttribute( ATTR_LIFERAY_PORTAL_CONTEXT_PATH, DEFAULT_LIFERAY_PORTAL_CONTEXT_PATH );
    }

    public URL getModuleRootURL( IModule module )
    {
        return getPortalHomeUrl();
    }

    public String getPassword()
    {
        return getAttribute( ATTR_PASSWORD, DEFAULT_PASSWORD );
    }

    public URL getPluginContextURL( String context )
    {
        int httpPort = getHttpPort();

        try
        {
            return new URL( "http://" + getServer().getHost() + ":" + httpPort + context ); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch( MalformedURLException e )
        {
        }

        return null;
    }

    public URL getPortalHomeUrl()
    {
        int httpPort = getHttpPort();
        String contextUrl = getLiferayPortalContextPath();

        if( !CoreUtil.isNullOrEmpty( contextUrl ) )
        {
            try
            {
                return new URL( "http://" + getServer().getHost() + ":" + httpPort + contextUrl ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch( MalformedURLException e )
            {
            }
        }

        return null;
    }

    @Override
    public IModule[] getRootModules( IModule module ) throws CoreException
    {

        IModule[] modules = new IModule[] { module };

        IStatus status = canModifyModules( modules, null );

        if( status == null || !status.isOK() )
        {
            throw new CoreException( status );
        }

        return modules;
    }

    public String getServerManagerContextPath()
    {
        return getAttribute( ATTR_SERVER_MANAGER_CONTEXT_PATH, DEFAULT_SERVER_MANAGER_CONTEXT_PATH );
    }

    public String getUsername()
    {
        return getAttribute( ATTR_USERNAME, DEFAULT_USERNAME );
    }

    public URL getWebServicesListURL()
    {
        try
        {
            return new URL( "http://" + getServer().getHost() + ":" + getHttpPort() + "/tunnel-web/axis" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        catch( MalformedURLException e )
        {
            LiferayServerCore.logError( "Unable to get web services list URL", e ); //$NON-NLS-1$
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void modifyModules( IModule[] add, IModule[] remove, IProgressMonitor monitor ) throws CoreException
    {
        List<String> moduleIds = (List<String>) this.getAttribute( getModuleListAttr(), (List<String>) null );

        if( !( CoreUtil.isNullOrEmpty( add ) ) )
        {
            if( moduleIds == null )
            {
                moduleIds = new ArrayList<String>( add.length );
            }

            for( IModule addModule : add )
            {
                if( moduleIds.contains( addModule.getId() ) == false )
                {
                    moduleIds.add( addModule.getId() );
                }
            }
        }

        if( !( CoreUtil.isNullOrEmpty( remove ) ) && !( CoreUtil.isNullOrEmpty( moduleIds ) ) )
        {
            for( IModule removeModule : remove )
            {
                moduleIds.remove( removeModule.getId() );
            }
        }

        if( moduleIds != null )
        {
            setAttribute( getModuleListAttr(), moduleIds );
        }
    }

    public void setAdjustDeploymentTimestamp( boolean adjustTimestamp )
    {
        setAttribute( ATTR_ADJUST_DEPLOYMENT_TIMESTAMP, adjustTimestamp );
    }

    @Override
    public void setDefaults( IProgressMonitor monitor )
    {
        super.setDefaults( monitor );
        // getServerWorkingCopy().setAttribute( Server.PROP_AUTO_PUBLISH_SETTING, Server.AUTO_PUBLISH_DISABLE );
        String baseName = "Remote Liferay Server at " + getServer().getHost(); //$NON-NLS-1$
        String defaultName = baseName;

        int collision = 1;
        while( ServerPlugin.isNameInUse( getServer(), defaultName ) )
        {
            defaultName = baseName + " (" + ( collision++ ) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        getServerWorkingCopy().setName( defaultName );
    }

    public void setHttpPort( int httpPort )
    {
        setAttribute( ATTR_HTTP_PORT, httpPort );
    }

    public void setLiferayPortalContextPath( String path )
    {
        setAttribute( ATTR_LIFERAY_PORTAL_CONTEXT_PATH, path );
    }

    public void setPassword( String pw )
    {
        setAttribute( ATTR_PASSWORD, pw );
    }

    public void setServerManagerContextPath( String path )
    {
        setAttribute( ATTR_SERVER_MANAGER_CONTEXT_PATH, path );
    }

    public void setUsername( String username )
    {
        setAttribute( ATTR_USERNAME, username );
    }

    public IStatus validate( IProgressMonitor monitor )
    {
        IStatus status = null;

        try
        {
            String host = getServerWorkingCopy().getHost();

            if( monitor != null )
            {
                monitor.beginTask( NLS.bind( Msgs.validatingConnection, host, getHttpPort() ), IProgressMonitor.UNKNOWN );
            }

            if( !canMakeHttpConnection() )
            {
                return LiferayServerCore.createWarningStatus( NLS.bind(
                    Msgs.serverNotAvailable, host, getHttpPort() ) );
            }

            IServerManagerConnection connection = LiferayServerCore.getRemoteConnection( this );

            final IStatus createErrorStatus = LiferayServerCore.error( CONNECT_ERROR_MSG );

            status = connection.isAlive() ? Status.OK_STATUS : createErrorStatus;

            if( status.isOK() )
            {
                URL url = getPortalHomeUrl();

                if( url == null )
                {
                    status = createErrorStatus;
                }
            }

            if( monitor != null )
            {
                monitor.done();
            }
        }
        catch( Exception e )
        {
            status = LiferayServerCore.createErrorStatus( e );
        }

        return status;
    }

    protected String getModuleListAttr()
    {
        return ATTR_REMOTE_SERVER_MODULE_IDS_LIST;
    }

    private static class Msgs extends NLS
    {
        public static String extPluginDeployment;
        public static String notAddNonLiferayPluginProjectModule;
        public static String notConnectRemoteIDEConnectorPlugin;
        public static String serverNotAvailable;
        public static String validatingConnection;

        static
        {
            initializeMessages( RemoteServer.class.getName(), Msgs.class );
        }
    }
}
