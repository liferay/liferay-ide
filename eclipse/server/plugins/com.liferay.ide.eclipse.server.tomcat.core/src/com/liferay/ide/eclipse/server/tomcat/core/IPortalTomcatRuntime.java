package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.server.core.IPortalRuntime;

import org.eclipse.core.runtime.IPath;


public interface IPortalTomcatRuntime extends IPortalRuntime {

	public IPath getBundleZipLocation();

	public void setBundleZipLocation(IPath path);

}
