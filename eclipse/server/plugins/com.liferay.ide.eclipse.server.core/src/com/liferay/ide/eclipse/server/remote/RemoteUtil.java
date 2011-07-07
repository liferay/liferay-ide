package com.liferay.ide.eclipse.server.remote;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;

public class RemoteUtil {

	public static String detectServerManagerContextPath( RemoteServer remoteServer, IProgressMonitor monitor ) {
		// TODO
		return null;
	}

	public static IRemoteServer getRemoteServer( IServer server ) {
		if ( server != null ) {
			return (IRemoteServer) server.loadAdapter( IRemoteServer.class, null );
		}

		return null;
	}
}
