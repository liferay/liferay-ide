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
package com.liferay.ide.gradle.core;

import com.liferay.ide.gradle.toolingapi.custom.CustomModel;

import java.io.File;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class GradleProjectMethods
{

    public static IPath getOutputJar( IProject gradleProject )
    {
        final CustomModel model = LRGradleCore.getToolingModel( CustomModel.class, gradleProject );

        Set<File> outputFiles = model.getOutputFiles();

        if( outputFiles.size() > 0 )
        {
            return new Path( outputFiles.iterator().next().getAbsolutePath() );
        }

        return null;
    }

}
