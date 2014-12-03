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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;


/**
 * @author Gregory Amerson
 */
public class BundleProjectNature implements IProjectNature
{
    public static final String ID = "com.liferay.ide.project.core.bundleNature";

    private IProject project;

    @Override
    public void configure() throws CoreException
    {
    }

    @Override
    public void deconfigure() throws CoreException
    {
    }

    @Override
    public IProject getProject()
    {
        return this.project;
    }

    @Override
    public void setProject( IProject project )
    {
        this.project = project;
    }

}
