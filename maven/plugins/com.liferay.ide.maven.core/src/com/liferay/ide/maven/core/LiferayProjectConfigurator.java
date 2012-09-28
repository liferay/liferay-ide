package com.liferay.ide.maven.core;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ILifecycleMappingConfiguration;
import org.eclipse.m2e.core.project.configurator.MojoExecutionKey;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;


public class LiferayProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator
{

    @Override
    public void configure( ProjectConfigurationRequest request, IProgressMonitor monitor ) throws CoreException
    {
        MavenProject mavenProject = request.getMavenProject();
        Plugin plugin = mavenProject.getPlugin( "com.liferay.maven.plugins:liferay-maven-plugin" );
        Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
        dom.getChild( "appServerDeployDir" ).setValue( "xxx" );
        System.out.println(dom);
        
        IMavenProjectFacade mavenProjectFacade = request.getMavenProjectFacade();
        
        MavenSession session = request.getMavenSession();
        
        ResolverConfiguration config = request.getResolverConfiguration();
        
        System.out.println(request);
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
    
    @Override
    protected void assertHasNature( IProject project, String natureId ) throws CoreException
    {
        // TODO Auto-generated method stub
        super.assertHasNature( project, natureId );
    }
    
    
    @Override
    public void unconfigure( ProjectConfigurationRequest request, IProgressMonitor monitor ) throws CoreException
    {
        // TODO Auto-generated method stub
        super.unconfigure( request, monitor );
    }
    
    @Override
    public AbstractBuildParticipant getBuildParticipant(
        IMavenProjectFacade projectFacade, MojoExecution execution, IPluginExecutionMetadata executionMetadata )
    {
        return new LiferayProjectBuildParticipant();
    }
    
    @Override
    public boolean hasConfigurationChanged(
        IMavenProjectFacade newFacade, ILifecycleMappingConfiguration oldProjectConfiguration, MojoExecutionKey key,
        IProgressMonitor monitor )
    {
        // TODO Auto-generated method stub
        return super.hasConfigurationChanged( newFacade, oldProjectConfiguration, key, monitor );
    }
    
    @Override
    public void mavenProjectChanged( MavenProjectChangedEvent event, IProgressMonitor monitor ) throws CoreException
    {
        // TODO Auto-generated method stub
        super.mavenProjectChanged( event, monitor );
    }

}
