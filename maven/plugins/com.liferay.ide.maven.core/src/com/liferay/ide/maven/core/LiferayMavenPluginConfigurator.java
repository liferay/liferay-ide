package com.liferay.ide.maven.core;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;


public class LiferayMavenPluginConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator
{

    public LiferayMavenPluginConfigurator()
    {
        super();
    }

    @Override
    public void configure( ProjectConfigurationRequest request, IProgressMonitor monitor ) throws CoreException
    {

       System.out.println(request); 
    }
    
    @Override
    protected List<MojoExecution> getMojoExecutions( ProjectConfigurationRequest request, IProgressMonitor monitor )
        throws CoreException
    {
        // TODO Auto-generated method stub
        return super.getMojoExecutions( request, monitor );
    }
    
    @Override
    public AbstractBuildParticipant getBuildParticipant(
        IMavenProjectFacade projectFacade, MojoExecution execution, IPluginExecutionMetadata executionMetadata )
    {
        // TODO Auto-generated method stub
        return super.getBuildParticipant( projectFacade, execution, executionMetadata );
    }
    
    
    @Override
    protected <T> T getParameterValue(
        String parameter, Class<T> asType, MavenSession session, MojoExecution mojoExecution ) throws CoreException
    {
        // TODO Auto-generated method stub
        return super.getParameterValue( parameter, asType, session, mojoExecution );
    }
    
    @Override
    public void mavenProjectChanged( MavenProjectChangedEvent event, IProgressMonitor monitor ) throws CoreException
    {
        // TODO Auto-generated method stub
        super.mavenProjectChanged( event, monitor );
    }

    public void configureClasspath( IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor )
        throws CoreException
    {
        System.out.println(facade);
    }

    public void configureRawClasspath(
        ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor )
        throws CoreException
    {
        System.out.println(request);
    }
    
    

}
