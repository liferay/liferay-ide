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

package com.liferay.ide.project.core.library;

import com.liferay.ide.project.core.PluginClasspathDependencyContainerInitializer;
import com.liferay.ide.project.core.WebClasspathContainer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class WebPluginLibraryInstallOperation extends PluginLibraryInstallOperation
{

    protected IPath getClasspathContainerPath()
    {
        return new Path( PluginClasspathDependencyContainerInitializer.ID + "/" + WebClasspathContainer.SEGMENT_PATH ); //$NON-NLS-1$
    }

}
