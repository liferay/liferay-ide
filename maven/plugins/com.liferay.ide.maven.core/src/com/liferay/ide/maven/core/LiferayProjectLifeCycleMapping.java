package com.liferay.ide.maven.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractCustomizableLifecycleMapping;
import org.eclipse.m2e.core.project.configurator.ILifecycleMappingConfiguration;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;


public class LiferayProjectLifeCycleMapping extends AbstractCustomizableLifecycleMapping
{

    public LiferayProjectLifeCycleMapping()
    {
        super();
    }
    
    @Override
    public void configure( ProjectConfigurationRequest request, IProgressMonitor mon ) throws CoreException
    {
        // TODO Auto-generated method stub
        super.configure( request, mon );
    }
    
    @Override
    public boolean hasLifecycleMappingChanged(
        IMavenProjectFacade newFacade, ILifecycleMappingConfiguration oldConfiguration, IProgressMonitor monitor )
    {
        // TODO Auto-generated method stub
        return super.hasLifecycleMappingChanged( newFacade, oldConfiguration, monitor );
    }

}
