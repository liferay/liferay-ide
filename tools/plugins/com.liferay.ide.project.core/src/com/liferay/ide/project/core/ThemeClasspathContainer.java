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
package com.liferay.ide.project.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.osgi.util.NLS;


/**
 * @author Cindy Li
 */
public class ThemeClasspathContainer extends PluginClasspathContainer
{
    public final static String SEGMENT_PATH = "theme"; //$NON-NLS-1$

    protected static final String[] portalJars =
    {
        "commons-logging.jar",  //$NON-NLS-1$
        "log4j.jar",  //$NON-NLS-1$
        "util-bridges.jar", //$NON-NLS-1$
        "util-java.jar",  //$NON-NLS-1$
        "util-taglib.jar",  //$NON-NLS-1$
    };

    public ThemeClasspathContainer(
        IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourceURL )
    {
        super( containerPath, project, portalDir, javadocURL, sourceURL );
    }

    @Override
    public String getDescription()
    {
        return Msgs.liferayThemePluginAPI;
    }

    @Override
    protected String[] getPortalJars()
    {
        return portalJars;
    }

    private static class Msgs extends NLS
    {
        public static String liferayThemePluginAPI;

        static
        {
            initializeMessages( ThemeClasspathContainer.class.getName(), Msgs.class );
        }
    }
}
