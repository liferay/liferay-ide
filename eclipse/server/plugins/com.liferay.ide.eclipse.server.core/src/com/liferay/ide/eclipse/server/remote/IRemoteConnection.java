package com.liferay.ide.eclipse.server.remote;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IRemoteConnection {

	int getDebugPort();

	List<String> getLiferayPlugins();

	String getServerState();

	boolean isAlive();

	boolean isLiferayPluginStarted( String name );

	void setHost( String host );

	void setHttpPort( String port );

	void setManagerContextPath( String path );

	boolean isAppInstalled( String appName );

	Object uninstallApplication( String appName, IProgressMonitor monitor );

	Object updateApplication( String appName, String absolutePath, IProgressMonitor monitor );

	Object installApplication( String absolutePath, String appName, IProgressMonitor submon );

}
