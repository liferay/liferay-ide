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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.remote.APIException;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.LiferayPublishHelper;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.server.util.SocketUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Greg Amerson
 * @author Tao Tao
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class RemoteServerBehavior extends ServerBehaviourDelegate
    implements ILiferayServerBehavior, IServerLifecycleListener
{
    protected ILaunch currentLaunch;
    private IServerManagerConnection remoteConnection;
    private List<IModule[]> redeployModules;
    protected Job remoteServerUpdateJob;

    public RemoteServerBehavior()
    {
        super();
    }

    public boolean canConnect()
    {
        IStatus status = SocketUtil.canConnect( getServer().getHost(), getRemoteServer().getHTTPPort() );

        if( status != null && status.isOK() )
        {
            return true;
        }
        else
        {
            status = SocketUtil.canConnectProxy( getServer().getHost(), getRemoteServer().getHTTPPort() );

            if( status != null && status.isOK() )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public IStatus canPublish()
    {
        if( currentLaunch != null )
        {
            return Status.OK_STATUS;
        }
        else
        {
            return Status.CANCEL_STATUS;
        }
    }

    @Override
    public IStatus canRestart( String mode )
    {
        return LiferayServerCore.createWarningStatus( Msgs.restartingRemoteServerNotSupported );
    }

    @Override
    public boolean canRestartModule( IModule[] module )
    {
        IStatus status = getServer().canModifyModules( module, null, null );

        return status.isOK();
    }

    @Override
    public IStatus canStart( String launchMode )
    {
        return LiferayServerCore.error( Msgs.liferayServerInstanceRemote );
    }

    @Override
    public IStatus canStop()
    {
        return LiferayServerCore.createWarningStatus( Msgs.stoppingRemoteLiferayServerNotSupported );
    }

    protected Job checkRemoteServerState( IProgressMonitor monitor )
    {
        if( monitor == null )
        {
            monitor = new NullProgressMonitor();
        }

        // make sure the server has not been deleted
        if( !( getServer().equals( ServerCore.findServer( getServer().getId() ) ) ) )
        {
            remoteServerUpdateJob = null;
            return null;
        }

        final int state = getServer().getServerState();

        Job updateServerJob = null;

        switch( state )
        {
            case IServer.STATE_UNKNOWN:
                if( canConnect() )
                {
                    updateServerJob = new Job( "Updating status for " + getServer().getName() ) //$NON-NLS-1$
                    {
                        @Override
                        protected IStatus run( IProgressMonitor monitor )
                        {
                            try
                            {
                                return updateServerState( state, monitor );
                            }
                            catch( Exception e )
                            {
                                LiferayServerCore.logError( e );
                            }

                            return Status.OK_STATUS;
                        }
                    };
                }

                break;

            case IServer.STATE_STOPPED:
                if( canConnect() )
                {
                    updateServerJob = new Job( "Connecting to " + getServer().getName() ) //$NON-NLS-1$
                    {
                        @Override
                        protected IStatus run( IProgressMonitor monitor )
                        {
                            int wasState = getRemoteServerState( state, monitor );

                            if( wasState == IServer.STATE_STARTED )
                            {
                                setServerState( IServer.STATE_STARTED );
                                launchServer( monitor );
                            }

                            return Status.OK_STATUS;
                        }
                    };
                }

                break;

            case IServer.STATE_STARTED:
                boolean isAlive = false;

                try
                {
                    isAlive = getServerManagerConnection().isAlive();
                }
                catch( Exception ex )
                {
                    // no error, this could because server is down
                }

                if( isAlive && ( this.currentLaunch == null || this.currentLaunch.isTerminated() ) )
                {
                    updateServerJob = new Job( "Connecting to server: " + getServer().getName() ) //$NON-NLS-1$
                    {
                        @Override
                        protected IStatus run( IProgressMonitor monitor )
                        {
                            launchServer( monitor );
                            return Status.OK_STATUS;
                        }
                    };
                }
                else
                {
                    // check on the current launch to make sure it is still valid
                    if( !isAlive )
                    {
                        terminateLaunch();
                        setServerState( IServer.STATE_STOPPED );
                    }
                }

                break;

            case IServer.STATE_STOPPING:
            case IServer.STATE_STARTING:
                // do nothing since server state will get updated automatically after start/stop
                break;

            default:
                break;
        }

        return updateServerJob;
    }

    protected Job createRemoteServerUpdateJob()
    {
        return new Job( "Remote server update." ) //$NON-NLS-1$
        {
            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                Job updateServerJob = checkRemoteServerState( monitor );

                if( updateServerJob != null )
                {
                    updateServerJob.schedule();

                    try
                    {
                        updateServerJob.join();
                    }
                    catch( InterruptedException e )
                    {
                    }
                }

                if( remoteServerUpdateJob != null )
                {
                    remoteServerUpdateJob.schedule( getRemoteServerUpdateDelay() );
                }

                return Status.OK_STATUS;
            }
        };
    }

    @Override
    public void dispose()
    {
        super.dispose();

        remoteServerUpdateJob.cancel();
        remoteServerUpdateJob = null;
    }

    @Override
    protected MultiStatus executePublishers(
        int kind, List<IModule[]> modules, List<Integer> deltaKinds, IProgressMonitor monitor, IAdaptable info )
        throws CoreException
    {
        return super.executePublishers(
            kind, ( redeployModules == null ) ? modules : redeployModules, deltaKinds, monitor, info );
    }

    public IPath getDeployedPath( IModule[] module )
    {
        return null;
    }

    protected IRemoteServer getRemoteServer()
    {
        return RemoteUtil.getRemoteServer( getServer() );
    }

    public int getRemoteServerState( int currentServerState, IProgressMonitor monitor )
    {
        try
        {
            if( currentServerState == IServer.STATE_STOPPED )
            {
                monitor.beginTask( NLS.bind( Msgs.updatingServerState, getServer().getName() ), 100 );
            }

            Object retval = null;

            IServerManagerConnection remoteConnection = getServerManagerConnection();

            try
            {
                retval = remoteConnection.getServerState();
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }

            if( retval == null )
            {
                return IServer.STATE_UNKNOWN;
            }

            String serverState = retval.toString();

            if( "STARTED".equals( serverState ) ) //$NON-NLS-1$
            {
                return IServer.STATE_STARTED;
            }
            else if( "STOPPED".equals( serverState ) ) //$NON-NLS-1$
            {
                return IServer.STATE_STOPPED;
            }
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Could not get server state.", e ); //$NON-NLS-1$
            return IServer.STATE_UNKNOWN;
        }

        return IServer.STATE_UNKNOWN;
    }

    protected long getRemoteServerUpdateDelay()
    {
        return 5000;
    }

    protected IServerManagerConnection getServerManagerConnection()
    {
        if( remoteConnection == null )
        {
            remoteConnection = LiferayServerCore.getRemoteConnection( getRemoteServer() );
        }

        return remoteConnection;
    }

    @Override
    protected void initialize( IProgressMonitor monitor )
    {
        ServerCore.addServerLifecycleListener( this );
        remoteServerUpdateJob = createRemoteServerUpdateJob();

        remoteServerUpdateJob.setSystem( true );
        remoteServerUpdateJob.schedule();
    }

    protected boolean isModuleInstalled( IModule[] module )
    {
        for( IModule m : module )
        {
            final String appName = ComponentUtilities.getServerContextRoot( m.getProject() );

            try
            {
                if( getServerManagerConnection().isAppInstalled( appName ) )
                {
                    return true;
                }
            }
            catch( APIException e )
            {
            }
        }

        return false;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    protected void launchServer( IProgressMonitor monitor )
    {
        if( currentLaunch != null && ( !currentLaunch.isTerminated() ) )
        {
            terminateLaunch();
        }

        ILaunchConfigurationWorkingCopy config = null;
        String launchMode = null;

        try
        {
            config = getServer().getLaunchConfiguration( true, null ).getWorkingCopy();
            IServerManagerConnection remoteConnection = getServerManagerConnection();
            Integer debugPort = remoteConnection.getDebugPort();

            if( debugPort > 0 )
            {
                Map connectMap = config.getAttribute( IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null );

                if( connectMap == null )
                {
                    connectMap = new HashMap();
                    config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, connectMap );
                }

                connectMap.put( "hostname", getServer().getHost() ); //$NON-NLS-1$
                connectMap.put( "port", debugPort.toString() ); //$NON-NLS-1$

                launchMode = ILaunchManager.DEBUG_MODE;
            }
            else
            {
                launchMode = ILaunchManager.RUN_MODE;
            }

            try
            {
                currentLaunch = config.launch( launchMode, null );
            }
            catch( CoreException ce )
            {
                if( debugPort > 0 )
                {
                    // if debug launch failed just try to launch one in run mode
                    ServerUtil.terminateLaunchesForConfig( config );
                }

                try
                {
                    currentLaunch = config.launch( ILaunchManager.RUN_MODE, null );
                }
                catch( CoreException ce1 )
                {
                    ServerUtil.terminateLaunchesForConfig( config );
                }
            }

        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Could not create new server launch configuration.", e ); //$NON-NLS-1$
        }
    }

    @Override
    protected void publishFinish( IProgressMonitor monitor ) throws CoreException
    {
        super.publishFinish( monitor );

        setServerPublishState( IServer.PUBLISH_STATE_NONE );

        this.redeployModules = null;
    }

    @Override
    protected void publishModule( int kind, int deltaKind, IModule[] module, IProgressMonitor monitor )
        throws CoreException
    {
        boolean shouldPublishModule =
            LiferayPublishHelper.prePublishModule(
                this, kind, deltaKind, module, getPublishedResourceDelta( module ), monitor );

        if( !shouldPublishModule )
        {
            return;
        }

        int modulePublishState = -1;

        if( kind == IServer.PUBLISH_FULL && ( deltaKind == ADDED || deltaKind == CHANGED ) )
        {
            modulePublishState = publishModuleFull( module, deltaKind, monitor );
        }
        else if( kind == IServer.PUBLISH_FULL && deltaKind == NO_CHANGE )
        {
            // first check to see if this module is even in the list of applications, if it is then we don't need to
            // actually update it
            modulePublishState = publishModuleFull( module, deltaKind, monitor );
        }
        else if( kind == IServer.PUBLISH_FULL && deltaKind == REMOVED )
        {
            modulePublishState = removeModule( module, monitor );
        }
        else if( ( kind == IServer.PUBLISH_AUTO || kind == IServer.PUBLISH_INCREMENTAL ) && deltaKind == CHANGED )
        {
            modulePublishState = publishModuleDelta( module, monitor );
        }

        if( modulePublishState == -1 )
        {
            modulePublishState = IServer.PUBLISH_STATE_UNKNOWN;
        }

        // by default, assume the module has published successfully.
        // this will update the publish state and delta correctly
        setModulePublishState( module, modulePublishState );
    }

    protected int publishModuleDelta( IModule[] module, IProgressMonitor monitor ) throws CoreException
    {
        if( monitor == null )
        {
            monitor = new NullProgressMonitor();
        }

        IProject moduleProject = module[0].getProject();

        IModuleResourceDelta[] delta = getPublishedResourceDelta( module );

        if( shouldPublishModuleFull( delta ) )
        {
            return publishModuleFull( module, CHANGED, monitor );
        }

        final String appName = ComponentUtilities.getServerContextRoot( moduleProject );

        monitor.subTask( "Creating partial " + moduleProject.getName() + " update archive..." ); //$NON-NLS-1$ //$NON-NLS-2$

        final ILiferayProject liferayProject = LiferayCore.create( moduleProject );
        final IRemoteServerPublisher publisher = liferayProject.adapt( IRemoteServerPublisher.class );

        if ( publisher == null )
        {
            setModuleStatus( module, null );
            throw new CoreException( LiferayServerCore.error( Msgs.publishingModuleProject ) );
        }

        final IPath warPath = publisher.publishModuleDelta( appName + ".war", delta, "liferay", true );

        monitor.worked( 25 );

        if( monitor != null && monitor.isCanceled() )
        {
            return IServer.PUBLISH_STATE_UNKNOWN;
        }

        monitor.subTask( Msgs.gettingLiferayConnection );

        IServerManagerConnection connection = getServerManagerConnection();

        monitor.subTask( NLS.bind( Msgs.updatingModuleProject, moduleProject.getName() ) );

        Object error = null;

        try
        {
            error = connection.updateApplication( appName, warPath.toOSString(), monitor );
        }
        catch( APIException e )
        {
            error = e.getMessage();
        }

        monitor.worked( 90 );

        if( error != null )
        {
            throw new CoreException( LiferayServerCore.error( error.toString() ) );
        }

        monitor.done();

        return IServer.PUBLISH_STATE_NONE;
    }

    protected int publishModuleFull( IModule[] module, int deltaKind, IProgressMonitor monitor ) throws CoreException
    {
        if( module == null )
        {
            throw new CoreException( LiferayServerCore.error( "Cannot publish module with length " + //$NON-NLS-1$
                ( module != null ? module.length : 0 ) ) );
        }

        if( monitor == null )
        {
            monitor = new NullProgressMonitor();
        }

        final IModule publishModule = module[0];

        final IProject moduleProject = publishModule.getProject();

        final IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 100 );
        submon.subTask( "Deploying " + moduleProject.getName() + "  to Liferay..." ); //$NON-NLS-1$ //$NON-NLS-2$

        final ILiferayProject liferayProject = LiferayCore.create( moduleProject );
        final IRemoteServerPublisher publisher = liferayProject.adapt( IRemoteServerPublisher.class );

        if ( publisher == null )
        {
            setModuleStatus( module, null );
            throw new CoreException( LiferayServerCore.error( Msgs.publishingModuleProject ) );
        }

        final IPath warPath = publisher.publishModuleFull( monitor );

        submon.worked( 25 ); // 25% complete

        if( monitor.isCanceled() )
        {
            return IServer.PUBLISH_STATE_FULL;
        }

        final String appName = ComponentUtilities.getServerContextRoot( moduleProject );

        IServerManagerConnection remoteConnection = getServerManagerConnection();

        setModuleStatus( module, LiferayServerCore.info( Msgs.installing ) );

        submon.worked( 25 ); // 50% complete

        if( monitor.isCanceled() )
        {
            return IServer.PUBLISH_STATE_FULL;
        }

        submon.subTask( NLS.bind( Msgs.publishingModuleProject, moduleProject.getName() ) );

        Object error = null;

        try
        {
            error = remoteConnection.installApplication( warPath.toOSString(), appName, submon );
        }
        catch( Exception ex )
        {
            setModuleStatus( module, null );
            setModuleState( module, IServer.STATE_UNKNOWN );
            throw new CoreException( LiferayServerCore.createErrorStatus( ex ) );
        }
        finally
        {
            if( warPath.toFile().exists() )
            {
                warPath.toFile().delete();
            }
        }

        if( error != null )
        {
            setModuleStatus( module, null );
            setModuleState( module, IServer.STATE_UNKNOWN );
            throw new CoreException( LiferayServerCore.error( error.toString() ) );
        }

        submon.worked( 40 ); // 90%

        setModuleStatus( module, LiferayServerCore.info( Msgs.starting ) );

        // scriptFile = getScriptFile("publish/startApplicationScript.groovy");

        if( monitor.isCanceled() )
        {
            setModuleStatus( module, null );
            return IServer.PUBLISH_STATE_UNKNOWN;
        }

        setModuleStatus( module, null );
        setModuleState( module, IServer.STATE_STARTED );

        submon.worked( 10 ); // 100%
        monitor.done();

        return IServer.PUBLISH_STATE_NONE;
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    protected void publishModules( int kind, List modules, List deltaKind2, MultiStatus multi, IProgressMonitor monitor )
    {
        super.publishModules( kind, ( redeployModules == null ) ? modules : redeployModules, deltaKind2, multi, monitor );
    }

    @Override
    protected void publishStart( IProgressMonitor monitor ) throws CoreException
    {
        int state = getServer().getServerState();

        if( state != IServer.STATE_STARTED )
        {
            throw new CoreException(
                LiferayServerCore.error( Msgs.notPublishRemoteServer ) );
        }
    }

    @SuppressWarnings( "rawtypes" )
    public void redeployModule( IModule[] module ) throws CoreException
    {
        setModulePublishState( module, IServer.PUBLISH_STATE_FULL );

        IAdaptable info = new IAdaptable()
        {
            public Object getAdapter( Class adapter )
            {
                if( String.class.equals( adapter ) )
                    return "user";
                return null;
            }
        };

        final List<IModule[]> modules = new ArrayList<IModule[]>();
        modules.add( module );

        try
        {
            redeployModules = modules;
            publish( IServer.PUBLISH_FULL, modules, null, info );
        }
        catch( CoreException e )
        {
            throw e;
        }
        finally
        {
            redeployModules = null;
        }
    }

    protected int removeModule( IModule[] module, IProgressMonitor monitor ) throws CoreException
    {
        if( module == null )
        {
            throw new CoreException( LiferayServerCore.error( "Cannot publish module with length " + //$NON-NLS-1$
                ( module != null ? module.length : 0 ) ) );
        }

        if( monitor == null )
        {
            monitor = new NullProgressMonitor();
        }

        IModule publishModule = module[0];

        IProject moduleProject = publishModule.getProject();

        if( moduleProject == null )
        {
            // just return this is a dead module
            setModuleStatus( module, null );
            return IServer.PUBLISH_STATE_UNKNOWN;
        }

        /*
         * First look to see if this module (ear plugin) is actually installed on liferay, and if it is then uninstall
         * it
         */

        monitor.beginTask( NLS.bind( Msgs.undeployingModuleProject, moduleProject.getName() ), 100 );

        final String appName = ComponentUtilities.getServerContextRoot( moduleProject );

        setModuleStatus( module, LiferayServerCore.info( Msgs.uninstalling ) );

        monitor.subTask( Msgs.gettingRemoteConnection );

        IServerManagerConnection remoteConnection = getServerManagerConnection();

        monitor.worked( 25 ); // 25%

        // File scriptFile = getScriptFile( "publish/uninstallApplicationScript.groovy" );

        monitor.subTask( NLS.bind( Msgs.uninstallingModuleProject, moduleProject.getName() ));

        Object error = null;

        try
        {
            error = remoteConnection.uninstallApplication( appName, monitor );
        }
        catch( APIException e )
        {
            error = e.getMessage();
        }

        monitor.worked( 75 ); // 100%

        if( error != null )
        {
            throw new CoreException( LiferayServerCore.error( error.toString() ) );
        }

        setModuleStatus( module, null );

        return IServer.PUBLISH_STATE_NONE;
    }


    public void serverAdded( IServer server )
    {
    }



    public void serverChanged( IServer server )
    {
    }

    public void serverRemoved( IServer server )
    {
        if( server.equals( getServer() ) )
        {
            if( currentLaunch != null && !currentLaunch.isTerminated() )
            {
                try
                {
                    currentLaunch.terminate();
                }
                catch( DebugException e )
                {
                }
            }
        }

        ServerCore.removeServerLifecycleListener( this );
    }

    protected boolean shouldPublishModuleFull( IModuleResourceDelta[] deltas )
    {
        boolean retval = false;

        if( ListUtil.isNotEmpty(deltas) )
        {
            for( IModuleResourceDelta delta : deltas )
            {
                if( shouldPublishModuleFull( delta.getAffectedChildren() ) )
                {
                    retval = true;
                    break;
                }
                else
                {
                    final IModuleResource resource = delta.getModuleResource();
                    final IFile resourceFile = (IFile) resource.getAdapter( IFile.class );

                    if( resourceFile != null )
                    {
                        final IWebProject lrproject = LiferayCore.create( IWebProject.class, resourceFile.getProject() );

                        if( lrproject != null )
                        {
                            final IPath docrootPath = lrproject.getDefaultDocrootFolder().getFullPath();

                            if( lrproject.findDocrootResource( resourceFile.getFullPath().makeRelativeTo(
                                docrootPath ) ) != null )
                            {
                                if( resource.getName().equals( "web.xml" ) ||
                                    resource.getName().equals(
                                        ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE ) )
                                {
                                    break;
                                }
                                else if( resource.getName().equals( "portlet.xml" ) )
                                {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return retval;
    }

    @Override
    public void stop( boolean force )
    {
        setServerState( IServer.STATE_STOPPED );
    }

    protected void terminateLaunch()
    {
        if( currentLaunch != null )
        {
            try
            {
                currentLaunch.terminate();
            }
            catch( DebugException e )
            {
            }

            currentLaunch = null;
        }
    }

    protected IStatus updateModuleState( IModule module )
    {

        final String appName = ComponentUtilities.getServerContextRoot( module.getProject() );

        boolean appStarted =false;

        try
        {
            appStarted = getServerManagerConnection().isLiferayPluginStarted( appName );
        }
        catch( APIException e )
        {
            LiferayServerCore.logError( e );
        }

        IModule[] module2 = new IModule[] { module };

        setModuleState( module2, appStarted ? IServer.STATE_STARTED : IServer.STATE_STOPPED );

        return Status.OK_STATUS;
    }

    protected IStatus updateServerState( int currentServerState, IProgressMonitor monitor )
    {
        if( getServer() == null )
        {
            return Status.OK_STATUS;
        }

        monitor.beginTask( Msgs.updatingServerStatus, 100 );

        int remoteState = getRemoteServerState( currentServerState, monitor );

        if( remoteState == IServer.STATE_STARTED )
        {
            setServerState( IServer.STATE_STARTED );
            launchServer( monitor );
        }
        else if( remoteState == IServer.STATE_STOPPED )
        {
            terminateLaunch();
            setServerState( IServer.STATE_STOPPED );
        }

        // check modules
        IModule[] modules = getServer().getModules();

        if( ListUtil.isNotEmpty(modules) )
        {
            List<String> plugins = getServerManagerConnection().getLiferayPlugins();

            for( IModule module : modules )
            {
                if( CoreUtil.isLiferayProject( module.getProject() ) )
                {

                    final String appName = ComponentUtilities.getServerContextRoot( module.getProject() );

                    if( plugins.contains( appName ) )
                    {
                        updateModuleState( module );
                    }
                    else
                    {
                        setModuleState( new IModule[] { module }, IServer.STATE_UNKNOWN );
                    }
                }
            }
        }

        return Status.OK_STATUS;
    }

    private static class Msgs extends NLS
    {

        public static String gettingLiferayConnection;
        public static String gettingRemoteConnection;
        public static String installing;
        public static String liferayServerInstanceRemote;
        public static String notPublishRemoteServer;
        public static String publishingModuleProject;
        public static String restartingRemoteServerNotSupported;
        public static String starting;
        public static String stoppingRemoteLiferayServerNotSupported;
        public static String undeployingModuleProject;
        public static String uninstalling;
        public static String uninstallingModuleProject;
        public static String updatingModuleProject;
        public static String updatingServerState;
        public static String updatingServerStatus;

        static
        {
            initializeMessages( RemoteServerBehavior.class.getName(), Msgs.class );
        }
    }
}
