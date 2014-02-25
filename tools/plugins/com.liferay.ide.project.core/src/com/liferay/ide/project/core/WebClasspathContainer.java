/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
import org.eclipse.osgi.util.NLS;

/**
 * @author Terry Jia
 */
public class WebClasspathContainer extends PluginClasspathContainer
{
    public static final String SEGMENT_PATH = "web"; //$NON-NLS-1$

    protected static final String[] portalJars = 
    { 
        "commons-logging.jar",  //$NON-NLS-1$
        "log4j.jar",  //$NON-NLS-1$
        "util-bridges.jar", //$NON-NLS-1$
        "util-java.jar",  //$NON-NLS-1$
        "util-taglib.jar",  //$NON-NLS-1$
    };

    public WebClasspathContainer(
        IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourceURL )
    {
        super( containerPath, project, portalDir, javadocURL, sourceURL );
    }

    public String getDescription()
    {
        return Msgs.liferayWebPluginAPI;
    }

    @Override
    protected String[] getPortalJars()
    {
        return portalJars;
    }

    private static class Msgs extends NLS
    {

        public static String liferayWebPluginAPI;

        static
        {
            initializeMessages( WebClasspathContainer.class.getName(), Msgs.class );
        }
    }

}
