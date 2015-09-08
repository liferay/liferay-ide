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
package com.liferay.ide.maven.core;

import com.liferay.ide.core.IBundleProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author Gregory Amerson
 */
public class FacetedMavenBundleProject extends FacetedMavenProject implements IBundleProject
{

    private final MavenBundlePluginProject bundleProject;

    public FacetedMavenBundleProject( IProject project )
    {
        super( project );

        this.bundleProject = new MavenBundlePluginProject( project );
    }

    @Override
    public boolean filterResource( IPath resourcePath )
    {
        return this.bundleProject.filterResource( resourcePath );
    }

    @Override
    public IPath getOutputJar( boolean buildIfNeeded, IProgressMonitor monitor ) throws CoreException
    {
        return this.bundleProject.getOutputJar( buildIfNeeded, monitor );
    }

    @Override
    public String getSymbolicName() throws CoreException
    {
        return this.bundleProject.getSymbolicName();
    }

}
