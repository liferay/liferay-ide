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

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;
import com.liferay.ide.eclipse.server.util.ServerUtil;
import com.liferay.ide.eclipse.server.util.SocketUtil;

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
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * @author Greg Amerson
 */
public class RemoteServer extends ServerDelegate implements IRemoteServerWorkingCopy {

	public static final String ATTR_REMOTE_SERVER_MODULE_IDS_LIST = "remote-server-module-ids-list";

	public RemoteServer() {
		super();
	}

	public String getId() {
		return getServer().getId();
	}

	public URL getModuleRootURL( IModule module ) {
		return getPortalHomeUrl();
	}

	@Override
	public void setDefaults( IProgressMonitor monitor ) {
		super.setDefaults( monitor );
		// getServerWorkingCopy().setAttribute( Server.PROP_AUTO_PUBLISH_SETTING, Server.AUTO_PUBLISH_DISABLE );
	}

	@Override
	public IStatus canModifyModules( IModule[] add, IModule[] remove ) {
		// check to see if we can add this to remote server
		if ( !( CoreUtil.isNullOrEmpty( add ) ) ) {
			for ( IModule addModule : add ) {
				IProject addModuleProject = addModule.getProject();

				if ( !ServerUtil.isLiferayProject( addModuleProject ) ) {
					return LiferayServerCorePlugin.createErrorStatus( "Cannot add non Liferay plugin project module." );
				}

				IStatus facetStatus = FacetUtil.verifyFacets( addModuleProject, getServer() );

				if ( facetStatus != null && !facetStatus.isOK() ) {
					return facetStatus;
				}

				// make sure that EXT is disabled for now for remote deployment
				if ( ServerUtil.isExtProject( addModuleProject ) ) {
					return LiferayServerCorePlugin.createErrorStatus( "Ext plugin deployment to remote server is not supported at this time." );
				}
			}

		}
		else if ( !( CoreUtil.isNullOrEmpty( remove ) ) ) {
			for ( IModule removeModule : remove ) {
				IProject removeModuleProject = removeModule.getProject();

				if ( removeModuleProject == null ) {
					// something has happened to the project lets just remove it
					return Status.OK_STATUS;
				}

				// if (!ProjectUtil.isLiferayProject(removeModuleProject)) {
				// return WebsphereCore.createErrorStatus("Cannot remove non Liferay plugin project module.");
				// }

				IStatus facetStatus = FacetUtil.verifyFacets( removeModuleProject, getServer() );

				if ( facetStatus != null && !facetStatus.isOK() ) {
					return facetStatus;
				}
			}
		}

		// ignore remove we can always try to remove;

		return Status.OK_STATUS;
	}

	@Override
	public IModule[] getChildModules( IModule[] module ) {
		if ( CoreUtil.isNullOrEmpty( module ) ) {
			return null;
		}

		List<IModule> childModules = new ArrayList<IModule>();

		for ( IModule m : module ) {
			IProject project = m.getProject();

			if ( ServerUtil.isLiferayProject( project ) ) {
				IWebModule webModule = (IWebModule) m.loadAdapter( IWebModule.class, null );
				if ( webModule != null ) {
					IModule[] webModules = webModule.getModules();

					if ( !( CoreUtil.isNullOrEmpty( webModules ) ) ) {
						for ( IModule childWebModule : webModules ) {
							childModules.add( childWebModule );
						}
					}
				}
			}
		}

		return childModules.toArray( new IModule[0] );
	}

	@Override
	public IModule[] getRootModules( IModule module ) throws CoreException {

		IModule[] modules = new IModule[] { module };

		IStatus status = canModifyModules( modules, null );

		if ( status == null || !status.isOK() ) {
			throw new CoreException( status );
		}

		return modules;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public void modifyModules( IModule[] add, IModule[] remove, IProgressMonitor monitor ) throws CoreException {

		List<String> moduleIds = (List<String>) this.getAttribute( getModuleListAttr(), (List<String>) null );

		if ( !( CoreUtil.isNullOrEmpty( add ) ) ) {
			if ( moduleIds == null ) {
				moduleIds = new ArrayList<String>( add.length );
			}

			for ( IModule addModule : add ) {
				if ( moduleIds.contains( addModule.getId() ) == false ) {
					moduleIds.add( addModule.getId() );
				}
			}
		}

		if ( !( CoreUtil.isNullOrEmpty( remove ) ) && !( CoreUtil.isNullOrEmpty( moduleIds ) ) ) {
			for ( IModule removeModule : remove ) {
				moduleIds.remove( removeModule.getId() );
			}
		}

		if ( moduleIds != null ) {
			setAttribute( getModuleListAttr(), moduleIds );
		}
	}

	protected String getModuleListAttr() {
		return ATTR_REMOTE_SERVER_MODULE_IDS_LIST;
	}

	public URL getPortalHomeUrl() {
		String httpPort = getHTTPPort();
		String contextUrl = getLiferayPortalContextPath();

		if ( httpPort != null && ( !CoreUtil.isNullOrEmpty( contextUrl ) ) ) {
			try {
				return new URL( "http://" + getServer().getHost() + ":" + httpPort + contextUrl );
			}
			catch ( MalformedURLException e ) {
			}
		}

		return null;
	}

	public URL getWebServicesListURL() {
		try {
			return new URL( "http://" + getServer().getHost() + ":" + getHTTPPort() + "/tunnel-web/axis" );
		}
		catch ( MalformedURLException e ) {
			LiferayServerCorePlugin.logError( "Unable to get web services list URL", e );
		}

		return null;
	}

	public IStatus validate( IProgressMonitor monitor ) {
		// TODO Implement validate method on class IRemoteServerWorkingCopy
		return Status.OK_STATUS;
	}

	public boolean canMakeHttpConnection() {
		String host = getServer().getHost();
		String http = getHTTPPort();

		final Socket socket = new Socket();

		new Thread() {

			@Override
			public void run() {
				try {
					sleep( 3000 );
					socket.close();
				}
				catch ( Exception e ) {
				}
			}

		}.start();

		IStatus status = null;

		try {
			status = SocketUtil.canConnect( socket, host, http );
		}
		catch ( Exception e ) {
		}

		return status != null && status.isOK();
	}

	public String getHost() {
		return getServer().getHost();
	}

	public String getHTTPPort() {
		return getAttribute( ATTR_HTTP_PORT, DEFAULT_HTTP_PORT );
	}

	public String getServerManagerContextPath() {
		return getAttribute( ATTR_SERVER_MANAGER_CONTEXT_PATH, DEFAULT_SERVER_MANAGER_CONTEXT_PATH );
	}

	public void setHTTPPort( String httpPort ) {
		setAttribute( ATTR_HTTP_PORT, httpPort );
	}

	public void setServerManagerContextPath( String path ) {
		setAttribute( ATTR_SERVER_MANAGER_CONTEXT_PATH, path );
	}

	public String getLiferayPortalContextPath() {
		return getAttribute( ATTR_LIFERAY_PORTAL_CONTEXT_PATH, DEFAULT_LIFERAY_PORTAL_CONTEXT_PATH );
	}

	public void setLiferayPortalContextPath( String path ) {
		setAttribute( ATTR_LIFERAY_PORTAL_CONTEXT_PATH, path );
	}

}
