package com.liferay.ide.eclipse.server.remote;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;


public interface IRemoteServerWorkingCopy extends IRemoteServer {

	void setHTTPPort( String httpPort );

	void setServerManagerContextPath( String path );

	void setLiferayPortalContextPath( String path );

	IStatus validate( IProgressMonitor monitor );

}
