/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.remote;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.util.SDKUtil;
import com.liferay.ide.eclipse.server.core.ILiferayServerBehavior;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;
import com.liferay.ide.eclipse.server.util.LiferayPublishHelper;
import com.liferay.ide.eclipse.server.util.ServerUtil;
import com.liferay.ide.eclipse.server.util.SocketUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Greg Amerson
 */
public class RemoteServerBehavior extends ServerBehaviourDelegate
	implements ILiferayServerBehavior, IServerLifecycleListener {

	protected ILaunch currentLaunch;
	protected IRemoteConnection remoteConnection;
	protected Job remoteServerUpdateJob;

	public RemoteServerBehavior() {
		super();
	}

	public boolean canConnect() {
		return SocketUtil.canConnect( getServer().getHost(), getRemoteServer().getHTTPPort() ).isOK();
	}

	@Override
	public IStatus canPublish() {
		if ( currentLaunch != null ) {
			return Status.OK_STATUS;
		}
		else {
			return Status.CANCEL_STATUS;
		}
	}

	@Override
	public IStatus canRestart( String mode ) {
		return LiferayServerCorePlugin.createWarningStatus( "Restarting remote server is not supported." );
	}

	@Override
	public boolean canRestartModule( IModule[] module ) {
		IStatus status = getServer().canModifyModules( module, null, null );

		return status.isOK();
	}

	@Override
	public IStatus canStart( String launchMode ) {
		return LiferayServerCorePlugin.createErrorStatus( "The Liferay server instance is remote and can not be started locally." );
	}

	@Override
	public IStatus canStop() {
		return LiferayServerCorePlugin.createWarningStatus( "Stopping a remote Liferay server is not supported." );
	}

	@Override
	public void dispose() {
		super.dispose();

		remoteServerUpdateJob.cancel();
		remoteServerUpdateJob = null;
	}

	public int getRemoteServerState( int currentServerState, IProgressMonitor monitor ) {
		try {
			if ( currentServerState == IServer.STATE_STOPPED ) {
				monitor.beginTask( "Updating server state for " + getServer().getName(), 100 );
			}

			Object retval = null;

			IRemoteConnection remoteConnection = getRemoteConnection();

			try {
				retval = remoteConnection.getServerState();
			}
			catch ( Exception e ) {
				e.printStackTrace();
			}

			if ( retval == null ) {
				setServerStatus( LiferayServerCorePlugin.createErrorStatus( "Check connection settings." ) );
				return IServer.STATE_UNKNOWN;
			}

			String serverState = retval.toString();

			if ( "STARTED".equals( serverState ) ) {
				return IServer.STATE_STARTED;
			}
			else if ( "STOPPED".equals( serverState ) ) {
				return IServer.STATE_STOPPED;
			}
		}
		catch ( Exception e ) {
			LiferayServerCorePlugin.logError( "Could not get server state.", e );
			return IServer.STATE_UNKNOWN;
		}

		return IServer.STATE_UNKNOWN;
	}

	public void redeployModule( IModule[] module ) {
		setModulePublishState( module, IServer.PUBLISH_STATE_FULL );

		IAdaptable info = new IAdaptable() {

			@SuppressWarnings( "rawtypes" )
			public Object getAdapter( Class adapter ) {
				if ( String.class.equals( adapter ) )
					return "user";
				return null;
			}
		};

		getServer().publish( IServer.PUBLISH_FULL, null, info, null );
	}

	public void serverAdded( IServer server ) {
	}

	public void serverChanged( IServer server ) {
	}

	public void serverRemoved( IServer server ) {
		if ( server.equals( getServer() ) ) {
			if ( currentLaunch != null && !currentLaunch.isTerminated() ) {
				try {
					currentLaunch.terminate();
				}
				catch ( DebugException e ) {
				}
			}
		}

		ServerCore.removeServerLifecycleListener( this );
	}

	@Override
	public void stop( boolean force ) {
		setServerState( IServer.STATE_STOPPED );
	}

	protected Job checkRemoteServerState( IProgressMonitor monitor ) {
		if ( monitor == null ) {
			monitor = new NullProgressMonitor();
		}

		// make sure the server has not been deleted
		if ( !( getServer().equals( ServerCore.findServer( getServer().getId() ) ) ) ) {
			remoteServerUpdateJob = null;
			return null;
		}

		final int state = getServer().getServerState();

		Job updateServerJob = null;

		switch ( state ) {
		case IServer.STATE_UNKNOWN:
			if ( canConnect() ) {
				updateServerJob = new Job( "Updating status for " + getServer().getName() ) {

					@Override
					protected IStatus run( IProgressMonitor monitor ) {
						try {
							return updateServerState( state, monitor );
						}
						catch ( Exception e ) {
							LiferayServerCorePlugin.logError( e );
						}

						return Status.OK_STATUS;
					}

				};
			}

			break;

		case IServer.STATE_STOPPED:
			if ( canConnect() ) {
				updateServerJob = new Job( "Connecting to " + getServer().getName() ) {

					@Override
					protected IStatus run( IProgressMonitor monitor ) {
						int wasState = getRemoteServerState( state, monitor );

						if ( wasState == IServer.STATE_STARTED ) {
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

			try {
				isAlive = getRemoteConnection().isAlive();
			}
			catch ( Exception ex ) {
				// no error, this could because server is down
			}

			if ( isAlive && ( this.currentLaunch == null || this.currentLaunch.isTerminated() ) ) {
				updateServerJob = new Job( "Connecting to server: " + getServer().getName() ) {

					@Override
					protected IStatus run( IProgressMonitor monitor ) {
						launchServer( monitor );
						return Status.OK_STATUS;
					}

				};
			}
			else {
				// check on the current launch to make sure it is still valid
				if ( !isAlive ) {
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

	protected Job createRemoteServerUpdateJob() {
		return new Job( "Remote server update." ) {

			@Override
			protected IStatus run( IProgressMonitor monitor ) {
				Job updateServerJob = checkRemoteServerState( monitor );

				if ( updateServerJob != null ) {
					updateServerJob.schedule();

					try {
						updateServerJob.join();
					}
					catch ( InterruptedException e ) {
					}
				}

				if ( remoteServerUpdateJob != null ) {
					remoteServerUpdateJob.schedule( getRemoteServerUpdateDelay() );
				}

				return Status.OK_STATUS;
			}
		};
	}

	protected IRemoteConnection getRemoteConnection() {
		if ( remoteConnection == null ) {
			remoteConnection = LiferayServerCorePlugin.getRemoteConnection( getRemoteServer() );
		}

		return remoteConnection;
	}

	protected IRemoteServer getRemoteServer() {
		return RemoteUtil.getRemoteServer( getServer() );
	}

	protected long getRemoteServerUpdateDelay() {
		return 5000;
	}

	@Override
	protected void initialize( IProgressMonitor monitor ) {
		ServerCore.addServerLifecycleListener( this );
		remoteServerUpdateJob = createRemoteServerUpdateJob();

		remoteServerUpdateJob.setSystem( true );
		remoteServerUpdateJob.schedule();
	}

	protected boolean isModuleInstalled( IModule[] module ) {
		for ( IModule m : module ) {
			String appName = m.getProject().getName();

			if ( getRemoteConnection().isAppInstalled( appName ) ) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	protected void launchServer( IProgressMonitor monitor ) {
		if ( currentLaunch != null && ( !currentLaunch.isTerminated() ) ) {
			terminateLaunch();
		}

		ILaunchConfigurationWorkingCopy config = null;
		String launchMode = null;

		try {
			config = getServer().getLaunchConfiguration( true, null ).getWorkingCopy();
			IRemoteConnection remoteConnection = getRemoteConnection();
			Integer debugPort = remoteConnection.getDebugPort();

			if ( debugPort > 0 ) {
				Map connectMap = config.getAttribute( IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null );

				if ( connectMap == null ) {
					connectMap = new HashMap();
					config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, connectMap );
				}

				connectMap.put( "hostname", getServer().getHost() );
				connectMap.put( "port", debugPort.toString() );

				launchMode = ILaunchManager.DEBUG_MODE;
			}
			else {
				launchMode = ILaunchManager.RUN_MODE;
			}

			try {
				currentLaunch = config.launch( launchMode, null );
			}
			catch ( CoreException ce ) {
				if ( debugPort > 0 ) {
					// if debug launch failed just try to launch one in run mode
					ServerUtil.terminateLaunchesForConfig( config );
				}

				try {
					currentLaunch = config.launch( ILaunchManager.RUN_MODE, null );
				}
				catch ( CoreException ce1 ) {
					ServerUtil.terminateLaunchesForConfig( config );
				}
			}

		}
		catch ( Exception e ) {
			LiferayServerCorePlugin.logError( "Could not create new server launch configuration.", e );
		}
	}

	@Override
	protected void publishFinish( IProgressMonitor monitor ) throws CoreException {
		super.publishFinish( monitor );

		setServerPublishState( IServer.PUBLISH_STATE_NONE );
	}

	@Override
	protected void publishModule( int kind, int deltaKind, IModule[] module, IProgressMonitor monitor )
		throws CoreException {

		boolean shouldPublishModule =
			LiferayPublishHelper.prePublishModule(
				this, kind, deltaKind, module, getPublishedResourceDelta( module ), monitor );

		if ( !shouldPublishModule ) {
			return;
		}

		int modulePublishState = -1;

		if ( kind == IServer.PUBLISH_FULL && ( deltaKind == ADDED || deltaKind == CHANGED ) ) {
			modulePublishState = publishModuleFull( module, deltaKind, monitor );
		}
		else if ( kind == IServer.PUBLISH_FULL && deltaKind == NO_CHANGE ) {
			// first check to see if this module is even in the list of applications, if it is then we don't need to
			// actually update it
			if ( !isModuleInstalled( module ) ) {
				modulePublishState = publishModuleFull( module, deltaKind, monitor );
			}
		}
		else if ( kind == IServer.PUBLISH_FULL && deltaKind == REMOVED ) {
			modulePublishState = removeModule( module, monitor );
		}
		else if ( ( kind == IServer.PUBLISH_AUTO || kind == IServer.PUBLISH_INCREMENTAL ) && deltaKind == CHANGED ) {
			modulePublishState = publishModuleDelta( module, monitor );
		}

		if ( modulePublishState == -1 ) {
			modulePublishState = IServer.PUBLISH_STATE_UNKNOWN;
		}

		// by default, assume the module has published successfully.
		// this will update the publish state and delta correctly
		setModulePublishState( module, modulePublishState );
	}

	protected int publishModuleDelta( IModule[] module, IProgressMonitor monitor ) throws CoreException {
		if ( monitor == null ) {
			monitor = new NullProgressMonitor();
		}

		IProject moduleProject = module[0].getProject();

		IModuleResourceDelta[] delta = getPublishedResourceDelta( module );

		if ( shouldPublishModuleFull( delta ) ) {
			return publishModuleFull( module, CHANGED, monitor );
		}

		String appName = moduleProject.getName();

		monitor.subTask( "Creating partial " + moduleProject.getName() + " update archive..." );

		File partialWar = ServerUtil.createPartialWAR( appName + ".war", delta, "liferay", true );

		monitor.worked( 25 );

		if ( monitor != null && monitor.isCanceled() ) {
			return IServer.PUBLISH_STATE_UNKNOWN;
		}

		monitor.subTask( "Getting Liferay connection..." );

		IRemoteConnection connection = getRemoteConnection();

		monitor.subTask( "Updating " + moduleProject.getName() + " on Liferay..." );

		Object error = connection.updateApplication( appName, partialWar.getAbsolutePath(), monitor );

		monitor.worked( 90 );

		if ( error != null ) {
			throw new CoreException( LiferayServerCorePlugin.createErrorStatus( error.toString() ) );
		}

		monitor.done();

		return IServer.PUBLISH_STATE_NONE;
	}

	protected int publishModuleFull( IModule[] module, int deltaKind, IProgressMonitor monitor ) throws CoreException {

		if ( module == null || module.length != 1 ) {
			throw new CoreException( LiferayServerCorePlugin.createErrorStatus( "Cannot publish module with length " +
				( module != null ? module.length : 0 ) ) );
		}

		if ( monitor == null ) {
			monitor = new NullProgressMonitor();
		}

		IModule publishModule = module[0];

		IProject moduleProject = publishModule.getProject();

		IProgressMonitor submon = CoreUtil.newSubMonitor( monitor, 100 );
		submon.subTask( "Deploying " + moduleProject.getName() + "  to Liferay..." );

		SDK sdk = SDKUtil.getSDK( moduleProject );

		Map<String, String> properties = new HashMap<String, String>();
		properties.put( ISDKConstants.PROPERTY_AUTO_DEPLOY_UNPACK_WAR, "false" );

		IPath deployPath = LiferayServerCorePlugin.getTempLocation( "direct-deploy", "" );

		properties.put( ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, deployPath.toOSString() );

		File warFile = deployPath.append( moduleProject.getName() + ".war" ).toFile();
		warFile.getParentFile().mkdirs();

		properties.put( ISDKConstants.PROPERTY_PLUGIN_FILE, warFile.getAbsolutePath() );

		submon.worked( 10 ); // 10% complete

		if ( monitor.isCanceled() ) {
			return IServer.PUBLISH_STATE_FULL;
		}

		submon.subTask( "Deploying " + moduleProject.getName() + "..." );

		Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( moduleProject );

		IStatus directDeployStatus =
			sdk.war( moduleProject, properties, true, appServerProperties, new String[] { "-Duser.timezone=GMT" } );

		if ( !directDeployStatus.isOK() || ( !warFile.exists() ) ) {
			throw new CoreException( directDeployStatus );
		}

		submon.worked( 15 ); // 25% complete

		if ( monitor.isCanceled() ) {
			return IServer.PUBLISH_STATE_FULL;
		}

		String appName = moduleProject.getName();

		IRemoteConnection remoteConnection = getRemoteConnection();

		setModuleStatus( module, LiferayServerCorePlugin.createInfoStatus( "Installing..." ) );

		submon.worked( 15 ); // 50% complete

		if ( monitor.isCanceled() ) {
			return IServer.PUBLISH_STATE_FULL;
		}

		submon.subTask( "Publishing " + moduleProject.getName() + " to Liferay..." );

		Object error = null;

		try {
			if ( remoteConnection.isAppInstalled( appName ) ) {
				error = remoteConnection.updateApplication( appName, warFile.getAbsolutePath(), submon );
			}
			else {
				error = remoteConnection.installApplication( warFile.getAbsolutePath(), appName, submon );
			}
		}
		catch ( Exception ex ) {
			setModuleStatus( module, null );
			setModuleState( module, IServer.STATE_UNKNOWN );
			throw new CoreException( LiferayServerCorePlugin.createErrorStatus( ex ) );
		}

		if ( error != null ) {
			setModuleStatus( module, null );
			setModuleState( module, IServer.STATE_UNKNOWN );
			throw new CoreException( LiferayServerCorePlugin.createErrorStatus( error.toString() ) );
		}

		submon.worked( 40 ); // 90%

		setModuleStatus( module, LiferayServerCorePlugin.createInfoStatus( "Starting..." ) );

		// scriptFile = getScriptFile("publish/startApplicationScript.groovy");

		if ( monitor.isCanceled() ) {
			setModuleStatus( module, null );
			return IServer.PUBLISH_STATE_UNKNOWN;
		}

		setModuleStatus( module, null );
		setModuleState( module, IServer.STATE_STARTED );

		submon.worked( 10 ); // 100%
		monitor.done();

		return IServer.PUBLISH_STATE_NONE;
	}

	@Override
	protected void publishStart( IProgressMonitor monitor ) throws CoreException {

		int state = getServer().getServerState();

		if ( state != IServer.STATE_STARTED ) {
			throw new CoreException(
				LiferayServerCorePlugin.createErrorStatus( "Cannot publish to remote server that is not started." ) );
		}
	}

	protected int removeModule( IModule[] module, IProgressMonitor monitor ) throws CoreException {

		if ( module == null || module.length != 1 ) {
			throw new CoreException( LiferayServerCorePlugin.createErrorStatus( "Cannot publish module with length " +
				( module != null ? module.length : 0 ) ) );
		}

		if ( monitor == null ) {
			monitor = new NullProgressMonitor();
		}

		IModule publishModule = module[0];

		IProject moduleProject = publishModule.getProject();

		if ( moduleProject == null ) {
			// just return this is a dead module
			setModuleStatus( module, null );
			return IServer.PUBLISH_STATE_UNKNOWN;
		}

		/*
		 * First look to see if this module (ear plugin) is actually installed on liferay, and if it is then uninstall
		 * it
		 */

		monitor.beginTask( "Undeploying " + moduleProject.getName() + " from Liferay...", 100 );

		String appName = moduleProject.getName();

		setModuleStatus( module, LiferayServerCorePlugin.createInfoStatus( "Uninstalling..." ) );

		monitor.subTask( "Getting remote connection..." );

		IRemoteConnection remoteConnection = getRemoteConnection();

		monitor.worked( 25 ); // 25%

		// File scriptFile = getScriptFile( "publish/uninstallApplicationScript.groovy" );

		monitor.subTask( "Uninstalling " + moduleProject.getName() + " from Liferay..." );

		Object error = remoteConnection.uninstallApplication( appName, monitor );

		monitor.worked( 75 ); // 100%

		if ( error != null ) {
			throw new CoreException( LiferayServerCorePlugin.createErrorStatus( error.toString() ) );
		}

		setModuleStatus( module, null );

		return IServer.PUBLISH_STATE_NONE;
	}

	protected boolean shouldPublishModuleFull( IModuleResourceDelta[] deltas ) {
		boolean retval = false;

		if ( !CoreUtil.isNullOrEmpty( deltas ) ) {
			for ( IModuleResourceDelta delta : deltas ) {
				if ( shouldPublishModuleFull( delta.getAffectedChildren() ) ) {
					retval = true;
					break;
				}
				else {
					IModuleResource resource = delta.getModuleResource();

					if ( resource.getName().equals( "web.xml" ) ||
						resource.getName().equals( ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE ) ) {

						retval = CoreUtil.isResourceInDocroot( resource );

						if ( retval ) {
							break;
						}
					}
					else if ( resource.getName().equals( "portlet.xml" ) ) {
						// if portlet-custom.xml is used we need to redeploy on this change.
						retval = CoreUtil.isResourceInDocroot( resource );

						if ( retval ) {
							break;
						}
					}
				}
			}
		}

		return retval;
	}

	protected void terminateLaunch() {
		if ( currentLaunch != null ) {
			try {
				currentLaunch.terminate();
			}
			catch ( DebugException e ) {
			}

			currentLaunch = null;
		}
	}

	protected IStatus updateModuleState( IModule module ) {
		String appName = module.getProject().getName();

		boolean appStarted = getRemoteConnection().isLiferayPluginStarted( appName );

		IModule[] module2 = new IModule[] { module };

		setModuleState( module2, appStarted ? IServer.STATE_STARTED : IServer.STATE_STOPPED );

		return Status.OK_STATUS;
	}

	protected IStatus updateServerState( int currentServerState, IProgressMonitor monitor ) {
		if ( getServer() == null ) {
			return Status.OK_STATUS;
		}

		monitor.beginTask( "Updating server status...", 100 );

		int remoteState = getRemoteServerState( currentServerState, monitor );

		if ( remoteState == IServer.STATE_STARTED ) {
			setServerState( IServer.STATE_STARTED );
			launchServer( monitor );
		}
		else if ( remoteState == IServer.STATE_STOPPED ) {
			terminateLaunch();
			setServerState( IServer.STATE_STOPPED );
		}

		// check modules
		IModule[] modules = getServer().getModules();

		if ( !CoreUtil.isNullOrEmpty( modules ) ) {
			List<String> plugins = getRemoteConnection().getLiferayPlugins();

			for ( IModule module : modules ) {
				if ( ServerUtil.isLiferayProject( module.getProject() ) ) {
					String appName = module.getProject().getName();

					if ( plugins.contains( appName ) ) {
						updateModuleState( module );
					}
					else {
						setModuleState( new IModule[] { module }, IServer.STATE_UNKNOWN );
					}
				}
			}
		}

		return Status.OK_STATUS;
	}

}
