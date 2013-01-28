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
package com.liferay.ide.maven.core;

import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;


/**
 * @author Gregory Amerson
 */
public class LiferayProjectLifecycleConfigurator extends AbstractProjectConfigurator
{

    public LiferayProjectLifecycleConfigurator()
    {
        super();
    }

    @Override
    public void configure( ProjectConfigurationRequest request, IProgressMonitor monitor ) throws CoreException
    {

    }

    @Override
    public AbstractBuildParticipant getBuildParticipant(
        IMavenProjectFacade projectFacade, MojoExecution execution, IPluginExecutionMetadata executionMetadata )
    {
        final String artifactId = execution.getArtifactId();
        final String goal = execution.getGoal();

        if( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN.equals( artifactId ) &&
            ILiferayMavenConstants.BUILD_CSS.equals( goal ) ||
            ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN.equals( artifactId ) &&
            ILiferayMavenConstants.BUILD_EXT.equals( goal ) ||
            ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN.equals( artifactId ) &&
            ILiferayMavenConstants.BUILD_THUMBNAIL.equals( goal ) )
        {
            return new AbstractBuildParticipant()
            {
                @Override
                public Set<IProject> build( int kind, IProgressMonitor monitor ) throws Exception
                {
                    // TODO Auto-generated method stub
                    return null;
                }
            };
        }

        return null;
    }

}
