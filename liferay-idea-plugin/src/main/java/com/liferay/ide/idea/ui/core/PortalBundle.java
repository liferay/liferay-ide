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

package com.liferay.ide.idea.ui.core;

import java.nio.file.Path;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 *@author Terry Jia
 */
public interface PortalBundle extends ILiferayPortal
{

    Path getAutoDeployPath();

    Path getAppServerDeployDir();

    Path getAppServerLibGlobalDir();

    int getJmxRemotePort();

    Path getLiferayHome();

    Path getAppServerDir();

    String getMainClass();

    Path getModulesPath();

    Path getOSGiBundlesDir();

    Path[] getRuntimeClasspath();

    String[] getRuntimeStartVMArgs();

    String[] getRuntimeStopVMArgs();

    String[] getRuntimeStartProgArgs();

    String[] getRuntimeStopProgArgs();

    String getType();

    String getDisplayName();

    Path[] getBundleDependencyJars();

    Path[] getUserLibs();

    String getHttpPort();

    void setHttpPort(String port);

}
