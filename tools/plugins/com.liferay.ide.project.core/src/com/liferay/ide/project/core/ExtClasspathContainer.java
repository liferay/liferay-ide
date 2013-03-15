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

package com.liferay.ide.project.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.osgi.util.NLS;

/**
 * @author Greg Amerson
 */
public class ExtClasspathContainer extends PluginClasspathContainer 
{
	public static final String SEGMENT_PATH = "ext"; //$NON-NLS-1$

	protected static final String[] portalJars =
	{
		"commons-logging.jar", //$NON-NLS-1$
		"log4j.jar",  //$NON-NLS-1$
		"util-bridges.jar",  //$NON-NLS-1$
		"util-java.jar",  //$NON-NLS-1$
		"util-taglib.jar", //$NON-NLS-1$
		"portal-impl.jar",  //$NON-NLS-1$
		"struts.jar",  //$NON-NLS-1$
		"struts-el.jar" //$NON-NLS-1$
	};
    
	public ExtClasspathContainer( IPath containerPath, IJavaProject project, IPath portalDir, String javadoc, IPath sourceURL )
	{
		super( containerPath, project, portalDir, javadoc, sourceURL );
	}

    public String getDescription()
    {
        return Msgs.liferayExtPluginAPI;
    }

    @Override
    protected String[] getPortalJars()
    {
        return portalJars;
    }

    private static class Msgs extends NLS
    {
        public static String liferayExtPluginAPI;

        static
        {
            initializeMessages( ExtClasspathContainer.class.getName(), Msgs.class );
        }
    }
}
