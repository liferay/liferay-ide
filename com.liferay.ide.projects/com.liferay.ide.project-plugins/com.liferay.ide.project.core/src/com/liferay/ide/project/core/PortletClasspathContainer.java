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

package com.liferay.ide.project.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

/**
 * @author Greg Amerson
 */
public class PortletClasspathContainer extends PluginClasspathContainer
{
    public final static String SEGMENT_PATH = "portlet";

    protected static final String[] portalJars = 
    { 
        "commons-logging.jar", 
        "log4j.jar", 
        "util-bridges.jar",
        "util-java.jar", 
        "util-taglib.jar", 
    };

    public PortletClasspathContainer(
        IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourcePath )
    {
        super( containerPath, project, portalDir, javadocURL, sourcePath );
    }

    public String getDescription()
    {
        return "Liferay Portlet Plugin API";
    }

    @Override
    protected String[] getPortalJars()
    {
        return portalJars;
    }

}
