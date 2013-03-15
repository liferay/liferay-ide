/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

    IPath getAppServerDeployDir();

    IPath getAppServerDir();

    IPath getAppServerLibGlobalDir();

    IPath getAppServerPortalDir();

    String getAppServerType();

    String[] getHookSupportedProperties();

    String getJavadocURL();

    String getPortalVersion();

    Properties getPortletCategories();

    Properties getPortletEntryCategories();

    IRuntime getRuntime();

    IPath getRuntimeLocation();

    IPath getSourceLocation();

    IPath[] getUserLibs();

}
