package com.liferay.ide.maven.core;

import com.liferay.ide.project.core.facet.IPluginFacetConstants;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;


/**
 * @author Gregory Amerson
 */
public class LiferayProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator
{

    @Override
    public void configure( ProjectConfigurationRequest request, IProgressMonitor monitor ) throws CoreException
    {
        final MavenProject mavenProject = request.getMavenProject();

        final Plugin liferayMavenPlugin = mavenProject.getPlugin( "com.liferay.maven.plugins:liferay-maven-plugin" );
 
        if( liferayMavenPlugin != null )
        {
            final Xpp3Dom dom = (Xpp3Dom) liferayMavenPlugin.getConfiguration();
 
            if( dom != null )
            {
                final Xpp3Dom pluginTypeNode = dom.getChild( "pluginType" );

                if( pluginTypeNode != null )
                {
                    final String pluginType = pluginTypeNode.getValue();
 
                    if( "portlet".equals( pluginType ) )
                    {
                        final IFacetedProject facetedProject = ProjectFacetsManager.create( request.getProject(), true, monitor );

                        Set<Action> actions = new LinkedHashSet<Action>();
                        IDataModel liferayProjectConfig = getLiferayProjectConfig();
                        IProjectFacetVersion fv = IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET.getDefaultVersion();
                        actions.add(new IFacetedProject.Action( IFacetedProject.Action.Type.INSTALL, fv, liferayProjectConfig ));

                        facetedProject.modify( actions, monitor );
                    }
                }
            }
        }

        System.out.println(request);
    }

    private IDataModel getLiferayProjectConfig()
    {
        IDataModel liferayProjectConfig = DataModelFactory.createDataModel( new MavenPortletPluginFacetInstallDataModelProvider() );

        return liferayProjectConfig;
    }

    public void configureClasspath( IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor )
        throws CoreException
    {
    }

    public void configureRawClasspath(
        ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor )
        throws CoreException
    {
    }

}
