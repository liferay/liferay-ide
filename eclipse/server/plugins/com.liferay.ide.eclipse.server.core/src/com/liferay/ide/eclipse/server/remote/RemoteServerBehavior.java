package com.liferay.ide.eclipse.server.remote;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;
import com.liferay.ide.eclipse.server.util.ServerUtil;
import com.liferay.ide.eclipse.server.util.SocketUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
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
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;


public class RemoteServerBehavior extends ServerBehaviourDelegate implements IServerLifecycleListener {

	protected ILaunch currentLaunch;
	protected Job remoteServerUpdateJob;
	protected IRemoteConnection remoteConnection;

	public RemoteServerBehavior() {
		super();
	}

	public boolean canConnect() {
		return SocketUtil.canConnect( getServer().getHost(), getRemoteServer().getHTTPPort() ).isOK();
	}

	protected IRemoteServer getRemoteServer() {
		return RemoteUtil.getRemoteServer( getServer() );
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
	protected void publishFinish( IProgressMonitor monitor ) throws CoreException {
		super.publishFinish( monitor );

		setServerPublishState( IServer.PUBLISH_STATE_NONE );
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

	@Override
	protected void initialize( IProgressMonitor monitor ) {
		ServerCore.addServerLifecycleListener( this );
		remoteServerUpdateJob = createRemoteServerUpdateJob();

		remoteServerUpdateJob.setSystem( true );
		remoteServerUpdateJob.schedule();
	}

	@Override
	public void stop( boolean force ) {
		setServerState( IServer.STATE_STOPPED );
	}

	protected IRemoteConnection getRemoteConnection() {
		if ( remoteConnection == null ) {
			remoteConnection = LiferayServerCorePlugin.getRemoteConnection( getRemoteServer() );
		}

		return remoteConnection;
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
						return updateServerState( state, monitor );
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

	@SuppressWarnings( "unchecked" )
	protected void launchServer( IProgressMonitor monitor ) {
		if ( currentLaunch != null && ( !currentLaunch.isTerminated() ) ) {
			terminateLaunch();
		}

		ILaunchConfigurationWorkingCopy config = null;
		String launchMode = null;

		try {
			config = getServer().getLaunchConfiguration( true, null ).getWorkingCopy();
			IRemoteConnection adminProxy = getRemoteConnection();
			Map debugOptions = adminProxy.getDebugOptions();

			Object debugMode = debugOptions.get( "debugMode" );
			Object debugArgs = debugOptions.get( "debugArgs" );

			if ( Boolean.parseBoolean( debugMode.toString() ) ) {
				Pattern pattern = Pattern.compile( ".*address=([0-9]+).*", Pattern.DOTALL );
				Matcher matcher = pattern.matcher( debugArgs.toString() );
				matcher.matches();
				matcher.group( 1 );

				String debugPort = matcher.group( 1 );

				Map connectMap = config.getAttribute( IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null );

				if ( connectMap == null ) {
					connectMap = new HashMap();
					config.setAttribute( IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, connectMap );
				}

				connectMap.put( "hostname", getServer().getHost() );
				connectMap.put( "port", debugPort );

				launchMode = ILaunchManager.DEBUG_MODE;
			}
			else {
				launchMode = ILaunchManager.RUN_MODE;
			}

			try {
				currentLaunch = config.launch( launchMode, null );
			}
			catch ( CoreException ce ) {
				if ( Boolean.parseBoolean( debugMode.toString() ) ) {
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

	protected long getRemoteServerUpdateDelay() {
		return 5000;
	}

	protected IStatus updateServerState( int currentServerState, IProgressMonitor monitor ) {
		if ( getServer() == null ) {
			return Status.OK_STATUS;
		}

		monitor.beginTask( "Updating server status...", 100 );

		int websphereState = getRemoteServerState( currentServerState, monitor );

		if ( websphereState == IServer.STATE_STARTED ) {
			setServerState( IServer.STATE_STARTED );
			launchServer( monitor );
		}
		else if ( websphereState == IServer.STATE_STOPPED ) {
			terminateLaunch();
			setServerState( IServer.STATE_STOPPED );
		}

		// check modules
		IModule[] modules = getServer().getModules();

		if ( !CoreUtil.isNullOrEmpty( modules ) ) {
			Vector apps = getRemoteConnection().listApplications();

			for ( IModule module : modules ) {
				if ( ServerUtil.isLiferayProject( module.getProject() ) ) {
					String appName = WebsphereUtil.getAppName( module.getProject() );

					if ( apps.contains( appName ) ) {
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

	protected IStatus updateModuleState( IModule module ) {
		String appName = WebsphereUtil.getAppName( module.getProject() );

		boolean appStarted = getRemoteConnection().isAppStarted( appName );

		IModule[] module2 = new IModule[] { module };

		setModuleState( module2, appStarted ? IServer.STATE_STARTED : IServer.STATE_STOPPED );

		return Status.OK_STATUS;
	}

	public int getRemoteServerState( int currentServerState, IProgressMonitor monitor ) {
		try {
			if ( currentServerState == IServer.STATE_STOPPED ) {
				monitor.beginTask( "Updating server state for " + getServer().getName(), 100 );
			}

			Object retval = null;

			IRemoteConnection adminConnection = getRemoteConnection();

			try {
				retval = adminConnection.getServerState();
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

}
