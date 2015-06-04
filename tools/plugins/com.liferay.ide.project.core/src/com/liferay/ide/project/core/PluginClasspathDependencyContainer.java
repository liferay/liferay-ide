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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.osgi.util.NLS;

/**
 * @author Simon.Jiang
 */
public class PluginClasspathDependencyContainer extends AbstractLiferayClasspathContainer implements IClasspathContainer
{
    public static final String ID = "com.liferay.ide.eclipse.plugin.dependency.container";

    protected static final String[] portalJars =
    {
        "commons-logging.jar",  //$NON-NLS-1$
        "log4j.jar",  //$NON-NLS-1$
        "util-bridges.jar", //$NON-NLS-1$
        "util-java.jar",  //$NON-NLS-1$
        "util-taglib.jar",  //$NON-NLS-1$
    };

    protected IPath portalGlobalDir;
    
    protected IPath bundleDir;
    
    protected IPath[] bundleLibDependencyPath;

    public PluginClasspathDependencyContainer(
        IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourceURL, IPath portalGlobalDir, IPath bundleDir, IPath[] bundleLibDependencyPath )
    {
        super(containerPath,project,portalDir,javadocURL,sourceURL);
        this.portalGlobalDir = portalGlobalDir;
        this.bundleDir = bundleDir;
        this.bundleLibDependencyPath = bundleLibDependencyPath;
    }

    @Override
    public IClasspathEntry[] getClasspathEntries()
    {
        if( this.classpathEntries == null )
        {
            List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

            for( IPath pluginJarPath : bundleLibDependencyPath )
            {
                IPath sourcePath = null;
                
                if( portalSourceJars.contains( pluginJarPath.lastSegment() ) )
                {
                    sourcePath = getSourceLocation();
                }
                entries.add( createClasspathEntry( pluginJarPath, sourcePath, this.javadocURL ) );
            }
                
            if( this.portalDir != null )
            {
                for( String pluginJar : getPortalJars() )
                {
                    entries.add( createPortalJarClasspathEntry( pluginJar ) );
                }

                for( String pluginPackageJar : getPortalDependencyJars() )
                {
                    entries.add( createPortalJarClasspathEntry( pluginPackageJar ) );
                }
            }

            this.classpathEntries = entries.toArray( new IClasspathEntry[entries.size()] );
        }

        return this.classpathEntries;
    }

    public String getDescription()
    {
        return Msgs.liferayPluginDependency;
    }
    
    public IPath getPortalGlobalDir()
    {
        return portalGlobalDir;
    }

    public IPath getBundleDir()
    {
        return bundleDir;
    }
    
    public IPath[] getBundleLibDependencyPath()
    {
        return this.bundleLibDependencyPath;
    }

    protected String[] getPortalJars()
    {
        return portalJars;
    }

    public static boolean isPluginContainerEntry(IClasspathEntry e) {
        return e!=null && e.getEntryKind()==IClasspathEntry.CPE_CONTAINER && e.getPath().segment(0).equals(ID);
    }

    private static class Msgs extends NLS
    {
        public static String liferayPluginDependency;

        static
        {
            initializeMessages( PluginClasspathDependencyContainer.class.getName(), Msgs.class );
        }
    }

}
