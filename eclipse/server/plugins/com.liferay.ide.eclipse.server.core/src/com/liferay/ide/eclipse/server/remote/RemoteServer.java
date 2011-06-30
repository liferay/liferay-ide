package com.liferay.ide.eclipse.server.remote;

import com.liferay.ide.eclipse.server.core.ILiferayServer;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;


public class RemoteServer extends ServerDelegate implements ILiferayServer {

	@Override
	public IStatus canModifyModules( IModule[] add, IModule[] remove ) {
		// TODO Implement canModifyModules method on class RemoteServer
		return null;
	}

	@Override
	public IModule[] getChildModules( IModule[] module ) {
		// TODO Implement getChildModules method on class RemoteServer
		return null;
	}

	@Override
	public IModule[] getRootModules( IModule module ) throws CoreException {
		// TODO Implement getRootModules method on class RemoteServer
		return null;
	}

	@Override
	public void modifyModules( IModule[] add, IModule[] remove, IProgressMonitor monitor ) throws CoreException {
		// TODO Implement modifyModules method on class RemoteServer

	}

	public URL getPortalHomeUrl() {
		// TODO Implement getPortalHomeUrl method on class ILiferayServer
		return null;
	}

	public URL getWebServicesListURL() {
		// TODO Implement getWebServicesListURL method on class ILiferayServer
		return null;
	}

}
