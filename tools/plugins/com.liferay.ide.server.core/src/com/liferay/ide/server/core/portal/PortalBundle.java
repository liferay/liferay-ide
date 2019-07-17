/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.ILiferayPortal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IVMInstall;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Charles Wu
 */
public interface PortalBundle extends ILiferayPortal {

	public IPath getAppServerDeployDir();

	public IPath getAppServerDir();

	public IPath getAppServerLibGlobalDir();

	public IPath getAutoDeployPath();

	public IPath[] getBundleDependencyJars();

	public String getDisplayName();

	public String getHttpPort();

	public int getJmxRemotePort();

	public IPath getLiferayHome();

	public IPath getLogPath();

	public String getMainClass();

	public IPath getModulesPath();

	public IPath getOSGiBundlesDir();

	public IPath[] getRuntimeClasspath();

	public String[] getRuntimeStartProgArgs();

	public String[] getRuntimeStartVMArgs(IVMInstall vmInstall);

	public String[] getRuntimeStopProgArgs();

	public String[] getRuntimeStopVMArgs(IVMInstall vmInstall);

	public String getServerReleaseInfo();

	public String getType();

	public IPath[] getUserLibs();

	public void setHttpPort(String port);

}