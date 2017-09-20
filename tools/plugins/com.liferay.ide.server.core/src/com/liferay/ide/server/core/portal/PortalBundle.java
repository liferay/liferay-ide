/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.server.core.portal;

import org.eclipse.core.runtime.IPath;

import com.liferay.ide.core.ILiferayPortal;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public interface PortalBundle extends ILiferayPortal
{

    IPath getAutoDeployPath();

    IPath getAppServerDeployDir();

    IPath getAppServerLibGlobalDir();

    IPath getLiferayHome();

    IPath getAppServerDir();

    String getMainClass();

    IPath getModulesPath();

    IPath getOSGiBundlesDir();

    IPath[] getRuntimeClasspath();

    String[] getRuntimeStartVMArgs();

    String[] getRuntimeStopVMArgs();

    String[] getRuntimeStartProgArgs();

    String[] getRuntimeStopProgArgs();

    String getType();

    String getDisplayName();

    IPath[] getBundleDependencyJars();

    IPath[] getUserLibs();

    PortalBundleConfiguration initBundleConfiguration();
}
