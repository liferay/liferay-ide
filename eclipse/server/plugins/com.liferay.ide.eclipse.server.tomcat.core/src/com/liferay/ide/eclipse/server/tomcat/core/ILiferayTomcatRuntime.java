package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.server.core.ILiferayRuntime;

import org.eclipse.core.runtime.IPath;


public interface ILiferayTomcatRuntime extends ILiferayRuntime {

	public IPath getBundleZipLocation();

	public String getServerInfo();

	public void setBundleZipLocation(IPath path);

}
