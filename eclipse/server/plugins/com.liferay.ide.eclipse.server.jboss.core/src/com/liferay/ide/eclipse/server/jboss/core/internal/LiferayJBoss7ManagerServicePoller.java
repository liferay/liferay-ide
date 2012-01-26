/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.

 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.

 * Contributors:
 * Kamesh Sampath - initial implementation
 * Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.server.jboss.core.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.JBossServerCorePlugin;
import org.jboss.ide.eclipse.as.core.server.IServerStatePoller;
import org.jboss.ide.eclipse.as.core.server.IServerStatePoller2;
import org.jboss.ide.eclipse.as.core.server.internal.ServerStatePollerType;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.jboss.ide.eclipse.as.core.server.v7.management.AS7ManagementDetails;
import org.jboss.ide.eclipse.as.management.core.AS7ManagementActivator;
import org.jboss.ide.eclipse.as.management.core.IJBoss7ManagerService;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManagerServiceProxy;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManagerUtil;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManangerConnectException;
import org.jboss.ide.eclipse.as.management.core.JBoss7ServerState;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class LiferayJBoss7ManagerServicePoller implements IServerStatePoller2 {

	public static final String POLLER_ID =
		"com.liferay.ide.eclipse.server.jboss.core.internal.LiferayJBoss7ManagerServicePoller"; //$NON-NLS-1$

	static final IConfigurationElement[] RUNTIME_TYPES_CONFIG_ELEMENTS;
	static {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		RUNTIME_TYPES_CONFIG_ELEMENTS =
			registry.getConfigurationElementsFor( JBossServerCorePlugin.PLUGIN_ID, "runtimeTypesExtension" ); //$NON-NLS-1$
	}

	private IServer server;

	private AS7ManagementDetails managementDetails;

	private ServerStatePollerType type;
	private boolean expectedState;
	private IJBoss7ManagerService service;
	private boolean done = false;
	private boolean canceled = false;
	private PollingException pollingException = null;
	private RequiresInfoException requiresInfoException = null;
	private List<String> requiredProperties = null;
	private Properties requiredPropertiesReturned = null;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#getPollerType()
	 */
	public ServerStatePollerType getPollerType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jboss.ide.eclipse.as.core.server.IServerStatePoller#setPollerType(org.jboss.ide.eclipse.as.core.server.internal
	 * .ServerStatePollerType)
	 */
	public void setPollerType( ServerStatePollerType type ) {
		this.type = type;

	}

	/**
	 * 
	 */
	public void beginPolling( IServer server, boolean expectedState ) throws PollingException {
		this.server = server;
		this.managementDetails = createManagementDetails();
		this.expectedState = expectedState;
		launchPollingThread();

	}

	private void launchPollingThread() {
		new Thread() {

			@Override
			public void run() {
				runLoop();
			}
		}.start();

	}

	public void runLoop() {
		try {
			while ( !done && !canceled ) {
				if ( expectedState == SERVER_DOWN ) {
					done = checkShutdown( service );
				}
				else {
					done = checkRunning( service );
				}
				if ( !done ) {
					try {
						Thread.sleep( 300 );
					}
					catch ( InterruptedException ie ) {
						// Ignore
					}
				}
			}
		}
		catch ( Exception e ) {
			pollingException = new PollingException( e.getMessage() );
		}
	}

	private boolean checkRunning( IJBoss7ManagerService service ) {
		try {
			JBoss7ServerState serverState = null;
			serverState = service.getServerState( managementDetails );
			return serverState == JBoss7ServerState.RUNNING;
		}
		catch ( Exception e ) {
			return false;
		}
	}

	/**
	 * @param server
	 * @return
	 * @throws InvalidSyntaxException
	 */
	public static IJBoss7ManagerService getService( IServer server ) throws InvalidSyntaxException {
		BundleContext context = AS7ManagementActivator.getContext();
		JBoss7ManagerServiceProxy proxy = new JBoss7ManagerServiceProxy( context, getRequiredVersion( server ) );
		proxy.open();
		return proxy;
	}

	private static String getRequiredVersion( IServer server ) {
		String serverId = server.getRuntime().getRuntimeType().getId();
		for ( IConfigurationElement iConfigurationElement : RUNTIME_TYPES_CONFIG_ELEMENTS ) {
			String baseRuntimeTypeId = iConfigurationElement.getAttribute( "baseRuntimeTypeId" );//$NON-NLS-1$
			String extnRuntimeTypeId = iConfigurationElement.getAttribute( "runtimeTypeIds" );//$NON-NLS-1$
			String[] extnRuntimeTypeIds = extnRuntimeTypeId.split( "," );;//$NON-NLS-1$
			for ( String rtTypeId : extnRuntimeTypeIds ) {
				if ( serverId.equalsIgnoreCase( rtTypeId ) ) {
					return baseRuntimeTypeId;
				}
			}

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#isComplete()
	 */
	public boolean isComplete() throws PollingException, RequiresInfoException {
		if ( pollingException != null )
			throw pollingException;
		if ( requiresInfoException != null )
			throw requiresInfoException;
		return done;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.core.server.IServerStatePoller#getState()
	 */
	public boolean getState() throws PollingException, RequiresInfoException {
		if ( done )
			return expectedState;
		return !expectedState;
	}

	public void cleanup() {
		JBoss7ManagerUtil.dispose( service );

	}

	public void cancel( int type ) {
		canceled = true;

	}

	public int getTimeoutBehavior() {
		return TIMEOUT_BEHAVIOR_FAIL;
	}

	public List<String> getRequiredProperties() {
		return requiredProperties == null ? new ArrayList<String>() : requiredProperties;
	}

	public void provideCredentials( Properties credentials ) {
		requiredPropertiesReturned = credentials;

	}

	public IServer getServer() {
		return server;
	}

	private AS7ManagementDetails createManagementDetails() {
		return new AS7ManagementDetails( server ) {

			@Override
			public String[] handleCallbacks( String[] prompts ) {
				return handleAsynchCallbacks( prompts );
			}
		};
	}

	private String[] handleAsynchCallbacks( String[] prompts ) {
		List<String> tmp = new ArrayList<String>();
		tmp.addAll( Arrays.asList( prompts ) );
		requiredProperties = tmp;
		requiresInfoException = new RequiresInfoException( "Requires proper credentials" ); //$NON-NLS-1$
		while ( !done && !canceled && requiredPropertiesReturned == null ) {
			try {
				Thread.sleep( 500 );
			}
			catch ( InterruptedException ie ) {/* Do nothing */
			}
		}

		if ( done || canceled )
			return new String[0];
		String[] retPrompts = new String[prompts.length];
		for ( int i = 0; i < retPrompts.length; i++ ) {
			retPrompts[i] = (String) requiredPropertiesReturned.get( prompts[i] );
		}

		// If not cleared then it will keep asking for username/password
		requiresInfoException = null;

		return retPrompts;
	}

	/* Code related to synchronous state checking */
	private boolean callbacksCalled = false;

	public IStatus getCurrentStateSynchronous( final IServer server ) {
		try {
			Boolean result = JBoss7ManagerUtil.executeWithService( new JBoss7ManagerUtil.IServiceAware<Boolean>() {

				public Boolean execute( IJBoss7ManagerService service ) throws Exception {
					try {
						JBoss7ServerState state = service.getServerState( createSynchronousManagementDetails( server ) );
						return state == JBoss7ServerState.RUNNING
							? IServerStatePoller.SERVER_UP : IServerStatePoller.SERVER_DOWN;
					}
					catch ( Exception e ) {
						/* Should be JBoss7ManagerException, but cannot compile against since it is in jboss-as jars */
						return callbacksCalled ? IServerStatePoller.SERVER_UP : IServerStatePoller.SERVER_DOWN;
					}
				}
			}, server );
			if ( result.booleanValue() ) {
				Status s =
					new Status(
						IStatus.OK,
						JBossServerCorePlugin.PLUGIN_ID,
						"A JBoss 7 Management Service on " + server.getHost() //$NON-NLS-1$
							+
							", port " + getManagementPort( server ) + " has responded that the server is completely started." ); //$NON-NLS-1$ //$NON-NLS-2$
				return s;
			}
			Status s =
				new Status(
					IStatus.INFO,
					JBossServerCorePlugin.PLUGIN_ID,
					"A JBoss 7 Management Service on " + server.getHost() //$NON-NLS-1$
						+
						", port " + getManagementPort( server ) + " has responded that the server is not completely started." ); //$NON-NLS-1$ //$NON-NLS-2$
			return s;
		}
		catch ( Exception e ) {
			Status s =
				new Status(
					IStatus.INFO, JBossServerCorePlugin.PLUGIN_ID,
					"An attempt to reach the JBoss 7 Management Service on host " + server.getHost() //$NON-NLS-1$
						+ " and port " + getManagementPort( server ) + " has resulted in an exception", e ); //$NON-NLS-1$ //$NON-NLS-2$
			return s;
		}
	}

	private AS7ManagementDetails createSynchronousManagementDetails( IServer server ) {
		return new AS7ManagementDetails( server ) {

			@Override
			public String[] handleCallbacks( String[] prompts ) throws UnsupportedOperationException {
				// No need to do verification here... simply know that a server responded requesting callbacks
				// This means a server is up already
				callbacksCalled = true;
				throw new UnsupportedOperationException();
			}
		};
	}

	private int getManagementPort( IServer server ) {
		if ( server != null ) {
			JBoss7Server jbossServer =
				(JBoss7Server) server.loadAdapter( JBoss7Server.class, new NullProgressMonitor() );
			return jbossServer.getManagementPort();
		}
		return IJBoss7ManagerService.MGMT_PORT;
	}

	private boolean checkShutdown( IJBoss7ManagerService service ) {
		try {
			service.getServerState( managementDetails );
			return false;
		}
		catch ( JBoss7ManangerConnectException e ) {
			return true;
		}
		catch ( Exception e ) {
			return false;
		}
	}

}
