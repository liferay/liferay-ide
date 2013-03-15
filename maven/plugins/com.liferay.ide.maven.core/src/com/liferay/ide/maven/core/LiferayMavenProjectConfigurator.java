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

import com.liferay.ide.project.core.facet.IPluginFacetConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.internal.markers.MavenProblemInfo;
import org.eclipse.m2e.core.internal.markers.SourceLocation;
import org.eclipse.m2e.core.internal.markers.SourceLocationHelper;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayMavenProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator
{
    private IMavenMarkerManager mavenMarkerManager;

    public LiferayMavenProjectConfigurator()
    {
        super();

        this.mavenMarkerManager = MavenPluginActivator.getDefault().getMavenMarkerManager();
    }

    private MavenProblemInfo checkValidConfigDir( Plugin liferayMavenPlugin, Xpp3Dom config, String configParam )
    {
        MavenProblemInfo retval = null;

        if( configParam != null && config != null )
        {
            final Xpp3Dom configNode = config.getChild( configParam );

            if( configNode != null )
            {
                final String value = configNode.getValue();

                if( ! new File( value ).exists() )
                {
                    SourceLocation location = SourceLocationHelper.findLocation( liferayMavenPlugin, configParam );
                    retval = new MavenProblemInfo( NLS.bind( Msgs.invalidConfigValue, configParam, value ),
                                                   IMarker.SEVERITY_ERROR,
                                                   location );
                }
            }
        }

        return retval;
    }

    private MavenProblemInfo checkValidLiferayVersion( Plugin liferayMavenPlugin, Xpp3Dom config )
    {
        MavenProblemInfo retval = null;
        Version liferayVersion = null;
        String liferayVersionValue = null;

        if( config != null )
        {
         // check for liferayVersion
            final Xpp3Dom liferayVersionNode = config.getChild( ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION );

            if( liferayVersionNode != null )
            {
                liferayVersionValue = liferayVersionNode.getValue();

                // handle the case where user specifies SNAPSHOT version
                liferayVersionValue = liferayVersionValue.replaceAll( "-SNAPSHOT$", "" ); //$NON-NLS-1$ //$NON-NLS-2$

                try
                {
                    liferayVersion = new Version( liferayVersionValue );
                }
                catch( IllegalArgumentException e )
                {
                    // bad version
                }
            }
        }

        if( liferayVersion == null )
        {
            // could not get valid liferayVersion
            final SourceLocation location = SourceLocationHelper.findLocation( liferayMavenPlugin, null );
            final String problemMsg = NLS.bind( Msgs.invalidConfigValue,
                                                ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION,
                                                liferayVersionValue );
            retval = new MavenProblemInfo( problemMsg, IMarker.SEVERITY_ERROR, location );
        }

        return retval;
    }

    @Override
    public void configure( ProjectConfigurationRequest request, IProgressMonitor monitor ) throws CoreException
    {
        final MavenProject mavenProject = request.getMavenProject();
        final Plugin liferayMavenPlugin = LiferayMavenUtil.getLiferayMavenPlugin( mavenProject );

        if( ! shouldConfigure( liferayMavenPlugin ) )
        {
            return;
        }

        final IProject project = request.getProject();
        final IFile pomFile = project.getFile( IMavenConstants.POM_FILE_NAME );
        final IFacetedProject facetedProject = ProjectFacetsManager.create( project, false, monitor );

        removeLiferayMavenMarkers( project );

        final List<MavenProblemInfo> errors = findLiferayMavenPluginProblems( project, mavenProject );

        if( errors.size() > 0 )
        {
            try
            {
                this.markerManager.addErrorMarkers( pomFile,
                                                    ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_ERROR_ID,
                                                    errors );
            }
            catch( CoreException e )
            {
                // no need to log this error its just best effort
            }

            return;
        }

        if( shouldInstallNewLiferayFacet( facetedProject ) )
        {
            final MavenProblemInfo installProblem = installNewLiferayFacet( facetedProject, mavenProject, monitor );

            if( installProblem != null )
            {
                this.markerManager.addMarker( pomFile,
                                              ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_ERROR_ID,
                                              installProblem.getMessage(),
                                              installProblem.getLocation().getLineNumber(),
                                              IMarker.SEVERITY_WARNING );
            }


        }
    }

    public void configureClasspath( IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor )
        throws CoreException
    {
    }

    // Copied from org.eclipse.m2e.wtp.AbstractProjectConfiguratorDelegate#configureDeployedName()
    protected void configureDeployedName(IProject project, String deployedFileName) {
        //We need to remove the file extension from deployedFileName
        int extSeparatorPos  = deployedFileName.lastIndexOf('.');
        String deployedName = extSeparatorPos > -1? deployedFileName.substring(0, extSeparatorPos): deployedFileName;
        //From jerr's patch in MNGECLIPSE-965
        IVirtualComponent projectComponent = ComponentCore.createComponent(project);
        if(projectComponent != null && !deployedName.equals(projectComponent.getDeployedName())){//MNGECLIPSE-2331 : Seems projectComponent.getDeployedName() can be null
          StructureEdit moduleCore = null;
          try {
            moduleCore = StructureEdit.getStructureEditForWrite(project);
            if (moduleCore != null){
              WorkbenchComponent component = moduleCore.getComponent();
              if (component != null) {
                component.setName(deployedName);
                moduleCore.saveIfNecessary(null);
              }
            }
          } finally {
            if (moduleCore != null) {
              moduleCore.dispose();
            }
          }
        }
      }

    public void configureRawClasspath(
        ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor )
        throws CoreException
    {
    }

    private Plugin findLiferayMavenPlugin( MavenProject mavenProject )
    {
        Plugin retval = null;

        if( mavenProject != null )
        {
            retval = mavenProject.getPlugin( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY );

            if( retval == null )
            {
                retval = findLiferayMavenPlugin( mavenProject.getParent() );
            }
        }

        return retval;
    }

    private List<MavenProblemInfo> findLiferayMavenPluginProblems( IProject project, MavenProject mavenProject )
    {
        final List<MavenProblemInfo> errors = new ArrayList<MavenProblemInfo>();

        // first check to make sure that the AppServer* properties are available and pointed to valid location
        final Plugin liferayMavenPlugin = LiferayMavenUtil.getLiferayMavenPlugin( mavenProject );

        if( liferayMavenPlugin != null )
        {
            final Xpp3Dom config = (Xpp3Dom) liferayMavenPlugin.getConfiguration();

            final MavenProblemInfo valueProblemInfo = checkValidLiferayVersion( liferayMavenPlugin, config );

            if( valueProblemInfo != null )
            {
                errors.add( valueProblemInfo );
            }

            final String[] configDirParams = new String[]
            {
                ILiferayMavenConstants.PLUGIN_CONFIG_APP_AUTO_DEPLOY_DIR,
                ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_CLASSES_PORTAL_DIR,
                ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR,
                ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR,
                ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_LIB_PORTAL_DIR,
                ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR,
                ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_TLD_PORTAL_DIR,
            };

            for( final String configParam : configDirParams )
            {
                final MavenProblemInfo configProblemInfo = checkValidConfigDir( liferayMavenPlugin, config, configParam );

                if( configProblemInfo != null )
                {
                    errors.add( configProblemInfo );
                }
            }
        }

        return errors;
    }

    private String getLiferayMavenPluginType( MavenProject mavenProject )
    {
        String pluginType = LiferayMavenUtil.getLiferayMavenPluginConfig( mavenProject,
                                                                          ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE );

        if( pluginType == null )
        {
            // check for EXT
            pluginType = ILiferayMavenConstants.DEFAULT_PLUGIN_TYPE;
        }

        return pluginType;
    }

    private IProjectFacetVersion getLiferayProjectFacet( IFacetedProject facetedProject )
    {
        IProjectFacetVersion retval = null;

        if( facetedProject != null )
        {
            for( IProjectFacetVersion fv : facetedProject.getProjectFacets() )
            {
                if( fv.getProjectFacet().getId().contains( "liferay." ) ) //$NON-NLS-1$
                {
                    retval = fv;
                    break;
                }
            }
        }

        return retval;
    }

    private Action getNewLiferayFacetInstallAction( String pluginType )
    {
        Action retval = null;
        IProjectFacetVersion newFacet = null;
        IDataModelProvider dataModel = null;

        if( ILiferayMavenConstants.PORTLET_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenPortletPluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.HOOK_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_HOOK_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenHookPluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.EXT_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_EXT_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenExtPluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.LAYOUTTPL_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_LAYOUTTPL_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenLayoutTplPluginFacetInstallProvider();
        }
        else if( ILiferayMavenConstants.THEME_PLUGIN_TYPE.equals( pluginType ) )
        {
            newFacet = IPluginFacetConstants.LIFERAY_THEME_PROJECT_FACET.getDefaultVersion();
            dataModel = new MavenThemePluginFacetInstallProvider();
        }

        if( newFacet != null )
        {
            final IDataModel config = DataModelFactory.createDataModel( dataModel );
            retval = new Action( Action.Type.INSTALL, newFacet, config );
        }

        return retval;
    }

    private MavenProblemInfo installNewLiferayFacet( IFacetedProject facetedProject,
                                                     MavenProject mavenProject,
                                                     IProgressMonitor monitor )
    {
        MavenProblemInfo retval = null;

        final String pluginType = getLiferayMavenPluginType( mavenProject );
        final Plugin liferayMavenPlugin = LiferayMavenUtil.getLiferayMavenPlugin( mavenProject );
        final Action action = getNewLiferayFacetInstallAction( pluginType );

        if( action != null )
        {
            try
            {
                facetedProject.modify( Collections.singleton( action ), monitor );
            }
            catch( Exception e )
            {
                final SourceLocation location = SourceLocationHelper.findLocation( liferayMavenPlugin, null );
                final String problemMsg = NLS.bind( Msgs.facetInstallError,
                                                    pluginType,
                                                    e.getCause() != null ? e.getCause().getMessage() : e.getMessage() );

                retval = new MavenProblemInfo( location, e );
                retval.setMessage( problemMsg );

                LiferayMavenCore.logError(
                    "Unable to install liferay facet " + action.getProjectFacetVersion(), e.getCause() ); //$NON-NLS-1$
            }

            final IProject project = facetedProject.getProject();

            try
            {
                // IDE-817 we need to mak sure that on deployment it will have the correct suffix for project name
                final IVirtualComponent projectComponent = ComponentCore.createComponent( project );

                if( projectComponent != null )
                {
                    final String deployedName = projectComponent.getDeployedName();

                    if( deployedName == null || ( deployedName != null && !deployedName.endsWith( pluginType ) ) )
                    {
                        final String deployedFileName = project.getName() + "-" + pluginType; //$NON-NLS-1$

                        configureDeployedName( project, deployedFileName );
                        projectComponent.setMetaProperty( "context-root", deployedFileName ); //$NON-NLS-1$
                    }
                }
            }
            catch( Exception e )
            {
                LiferayMavenCore.logError( "Unable to configure component for liferay deployment." + project.getName(), e ); //$NON-NLS-1$
            }
        }

        return retval;
    }

    private boolean loadParentHierarchy( IMavenProjectFacade facade, IProgressMonitor monitor ) throws CoreException
    {
        boolean loadedParent = false;
        MavenProject mavenProject = facade.getMavenProject();

        try
        {
            if( mavenProject.getModel().getParent() == null || mavenProject.getParent() != null )
            {
                // If the getParent() method is called without error,
                // we can assume the project has been fully loaded, no need to continue.
                return false;
            }
        }
        catch( IllegalStateException e )
        {
            // The parent can not be loaded properly
        }

        MavenExecutionRequest request = null;

        while( mavenProject != null && mavenProject.getModel().getParent() != null )
        {
            if( monitor.isCanceled() )
            {
                break;
            }

            if( request == null )
            {
                request = projectManager.createExecutionRequest( facade, monitor );
            }

            MavenProject parentProject = maven.resolveParentProject( request, mavenProject, monitor );

            if( parentProject != null )
           {
                mavenProject.setParent( parentProject );
                loadedParent = true;
            }

            mavenProject = parentProject;
        }

        return loadedParent;
    }

    private void removeLiferayMavenMarkers( IProject project ) throws CoreException
    {
        this.mavenMarkerManager.deleteMarkers( project,
                                               ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_ERROR_ID );
    }

    private boolean shouldConfigure( Plugin liferayMavenPlugin )
    {
        return liferayMavenPlugin != null;
    }

    private boolean shouldInstallNewLiferayFacet( IFacetedProject facetedProject )
    {
        return getLiferayProjectFacet( facetedProject ) == null;
    }

    private static class Msgs extends NLS
    {
        public static String facetInstallError;
        public static String invalidConfigValue;

        static
        {
            initializeMessages( LiferayMavenProjectConfigurator.class.getName(), Msgs.class );
        }
    }

}
