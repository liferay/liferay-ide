/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.core;

import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Greg Amerson
 */
public interface ILiferayRuntime extends IJavaRuntime
{

    IPath[] getAllUserClasspathLibraries();

    IPath getAppServerDir();

    String getAppServerType();

    IPath getDeployDir();

    String getJavadocURL();

    IPath getLibGlobalDir();

    IPath getPortalDir();

    String getPortalVersion();

    Properties getPortletCategories();

    IRuntime getRuntime();

    IPath getRuntimeLocation();

    String[] getServletFilterNames();

    IPath getSourceLocation();

    String[] getSupportedHookProperties();

}
