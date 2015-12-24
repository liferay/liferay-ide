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

import com.gradleware.tooling.toolingclient.GradleDistribution;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.buildship.core.projectimport.ProjectImportConfiguration;
import org.eclipse.buildship.core.util.gradle.GradleDistributionWrapper;
import org.eclipse.buildship.core.util.progress.AsyncHandler;
import org.eclipse.buildship.core.workspace.SynchronizeGradleProjectJob;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Andy Wu
 */
public class GradleUtil
{

    public static Job importGradleProject( File dir )
    {
        ProjectImportConfiguration configuration = new ProjectImportConfiguration();
        GradleDistributionWrapper from = GradleDistributionWrapper.from( GradleDistribution.fromBuild() );

        configuration.setGradleDistribution( from );
        configuration.setProjectDir( dir );
        configuration.setApplyWorkingSets( false );
        configuration.setWorkingSets( new ArrayList<String>() );

        Job job =
            new SynchronizeGradleProjectJob(
                configuration.toFixedAttributes(), configuration.getWorkingSets().getValue(), AsyncHandler.NO_OP );

        job.schedule();

        return job;
    }
}
