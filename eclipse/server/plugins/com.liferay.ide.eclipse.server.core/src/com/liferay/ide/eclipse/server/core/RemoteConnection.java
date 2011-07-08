package com.liferay.ide.eclipse.server.core;

import com.liferay.ide.eclipse.server.remote.IRemoteConnection;

import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public class RemoteConnection implements IRemoteConnection {

	private String host;
	private HttpClient httpClient;
	private String httpPort;
	private GetMethod isAliveMethod;
	private GetMethod debugPortMethod;
	private String managerContextPath;

	public RemoteConnection() {
		this( null, null, null );
	}

	public RemoteConnection( String host, String httpPort, String managerContextPath ) {
		this.host = host;
		this.httpPort = httpPort;
		this.managerContextPath = managerContextPath;
	}

	public int getDebugPort() {
		if ( isAlive() ) {
			try {
				GetMethod method = getDebugPortMethod();
				int statusCode = getHttpClient().executeMethod( method );

				if ( statusCode != HttpStatus.SC_OK ) {
					System.err.println( "Method failed: " + method.getStatusLine() );
				}
				else {
					// Read the response body.
					byte[] body = method.getResponseBody();

					return Integer.parseInt( new String( body ) );
				}
			}
			catch ( Exception e ) {

			}

		}

		return -1;
	}

	public List<String> getLiferayPlugins() {
		return Collections.emptyList();
	}

	public String getServerState() {
		if ( isAlive() ) {
			return "STARTED";
		}
		else {
			return "STOPPED";
		}
	}

	public boolean isAlive() {
		try {
			GetMethod method = getIsAliveMethod();
			int statusCode = getHttpClient().executeMethod( method );

			if ( statusCode != HttpStatus.SC_OK ) {
				System.err.println( "Method failed: " + method.getStatusLine() );
			}
			else {
				// Read the response body.
				byte[] body = method.getResponseBody();
				int isAliveInt = Integer.parseInt( new String( body ) );

				if ( isAliveInt == 1 ) {
					return true;
				}
			}
		}
		catch ( Exception e ) {
		}

		return false;
	}

	public boolean isLiferayPluginStarted( String name ) {
		return false;
	}

	public void setHost( String host ) {
		this.host = host;
		this.isAliveMethod = null;
		this.debugPortMethod = null;
	}

	public void setHttpPort( String httpPort ) {
		this.httpPort = httpPort;
		this.isAliveMethod = null;
		this.debugPortMethod = null;
	}

	public void setManagerContextPath( String managerContextPath ) {
		this.managerContextPath = managerContextPath;
		this.isAliveMethod = null;
		this.debugPortMethod = null;
	}

	private HttpClient getHttpClient() {
		if ( httpClient == null ) {
			httpClient = new HttpClient();
		}

		return httpClient;
	}

	private GetMethod getIsAliveMethod() {
		if ( isAliveMethod == null ) {
			isAliveMethod = new GetMethod( getIsAliveURI() );
		}

		return isAliveMethod;
	}

	private GetMethod getDebugPortMethod() {
		if ( debugPortMethod == null ) {
			debugPortMethod = new GetMethod( getDebugPortURI() );
		}

		return debugPortMethod;
	}

	private String getManagerURI() {
		return "http://" + host + ":" + httpPort + managerContextPath;
	}

	private String getIsAliveURI() {
		return getManagerURI() + "/is-alive";
	}

	private String getDebugPortURI() {
		return getManagerURI() + "/debug-port";
	}

	public boolean isAppInstalled( String appName ) {
		return false;
	}

	public Object uninstallApplication( String appName, IProgressMonitor monitor ) {
		return null;
	}

	public Object updateApplication( String appName, String absolutePath, IProgressMonitor monitor ) {
		return null;
	}

	public Object installApplication( String absolutePath, String appName, IProgressMonitor submon ) {
		return null;
	}

}
