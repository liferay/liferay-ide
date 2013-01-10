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
        if( "liferay-maven-plugin".equals( execution.getArtifactId() ) && "build-css".equals( execution.getGoal() ) ||
            "liferay-maven-plugin".equals( execution.getArtifactId() ) && "build-ext".equals( execution.getGoal() ) ||
            "liferay-maven-plugin".equals( execution.getArtifactId() ) && "build-thumbnail".equals( execution.getGoal() ) )
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
