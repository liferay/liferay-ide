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

/**
 * @author Simon.Jiang
 */
public class SDKClasspathContainer extends PluginClasspathContainer implements IClasspathContainer
{
    private final static String[] commonJars =
    {
        "commons-logging.jar",
        "log4j.jar",
        "util-bridges.jar",
        "util-java.jar",
        "util-taglib.jar",
    };

    public final static String ID = "com.liferay.ide.sdk.container";

    private final IPath bundleDir;

    private final IPath[] bundleLibDependencyPaths;

    private final IPath portalGlobalDir;

    private final IPath[] sdkDependencyPaths;

    public SDKClasspathContainer(
        IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourceURL,
        IPath portalGlobalDir, IPath bundleDir, IPath[] bundleLibDependencyPaths, IPath[] sdkDependencyPaths )
    {
        super( containerPath, project, portalDir, javadocURL, sourceURL );

        this.portalGlobalDir = portalGlobalDir;
        this.bundleDir = bundleDir;
        this.bundleLibDependencyPaths = bundleLibDependencyPaths;
        this.sdkDependencyPaths = sdkDependencyPaths;
    }

    public IPath getBundleDir()
    {
        return bundleDir;
    }

    public IPath[] getBundleLibDependencyPath()
    {
        return this.bundleLibDependencyPaths;
    }

    @Override
    public IClasspathEntry[] getClasspathEntries()
    {
        if( this.classpathEntries == null )
        {
            List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

            for( IPath pluginJarPath : bundleLibDependencyPaths )
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

            if ( sdkDependencyPaths != null )
            {
                for( IPath sdkDependencyJarPath : sdkDependencyPaths )
                {
                    entries.add( createClasspathEntry( sdkDependencyJarPath, null, null ) );
                }
            }

            this.classpathEntries = entries.toArray( new IClasspathEntry[entries.size()] );
        }

        return this.classpathEntries;
    }

    @Override
    public String getDescription()
    {
        return "Plugins SDK Dependencies";
    }

    public IPath getPortalGlobalDir()
    {
        return portalGlobalDir;
    }

    @Override
    protected String[] getPortalJars()
    {
        return commonJars;
    }

}
