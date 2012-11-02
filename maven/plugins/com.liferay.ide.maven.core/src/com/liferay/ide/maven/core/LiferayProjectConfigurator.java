/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.execution.MavenExecutionRequest;
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
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;


/**
 * @author Gregory Amerson
 */
public class LiferayProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator
{

    private static final String DEFAULT_PLUGIN_TYPE = "portlet";
    private static final String PORTLET_PLUGIN_TYPE = DEFAULT_PLUGIN_TYPE;
    private static final String HOOK_PLUGIN_TYPE = "hook";
    private static final String EXT_PLUGIN_TYPE = "ext";
    private static final String THEME_PLUGIN_TYPE = "theme";
    private static final String LAYOUTTPL_PLUGIN_TYPE = "layouttpl";
    private static final String LIFERAY_MAVEN_PLUGIN_KEY = "com.liferay.maven.plugins:liferay-maven-plugin";

    @Override
    public void configure( ProjectConfigurationRequest request, IProgressMonitor monitor ) throws CoreException
    {
        final MavenProject mavenProject = request.getMavenProject();

        final Xpp3Dom liferayMavenPluginConfig = getLiferayMavenPluginConfig( mavenProject );

        if( liferayMavenPluginConfig == null )
        {
            return;
        }

        String pluginType = DEFAULT_PLUGIN_TYPE;

        final Xpp3Dom pluginTypeNode = liferayMavenPluginConfig.getChild( "pluginType" );

        if( pluginTypeNode != null )
        {
            pluginType = pluginTypeNode.getValue();
        }

        final IFacetedProject facetedProject = ProjectFacetsManager.create( request.getProject(), false, monitor );

        final IProjectFacetVersion liferayPortletFV = getLiferayProjectFacet( facetedProject );

        if( liferayPortletFV == null )
        {
            IProjectFacetVersion newLiferayFacetToInstall = null;
            IDataModelProvider liferayFacetInstallProvider = null;

            if( PORTLET_PLUGIN_TYPE.equals( pluginType ) )
            {
                newLiferayFacetToInstall = IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET.getDefaultVersion();
                liferayFacetInstallProvider = new MavenPortletPluginFacetInstallProvider();
            }
            else if( HOOK_PLUGIN_TYPE.equals( pluginType ) )
            {
                newLiferayFacetToInstall = IPluginFacetConstants.LIFERAY_HOOK_PROJECT_FACET.getDefaultVersion();
                liferayFacetInstallProvider = new MavenHookPluginFacetInstallProvider();
            }
//            else if( EXT_PLUGIN_TYPE.equals( pluginType ) )
//            {
//                newLiferayFacetToInstall = IPluginFacetConstants.LIFERAY_EXT_PROJECT_FACET.getDefaultVersion();
//                liferayFacetInstallProvider = new MavenExtPluginFacetInstallProvider();
//            }
            else if( LAYOUTTPL_PLUGIN_TYPE.equals( pluginType ) )
            {
                newLiferayFacetToInstall = IPluginFacetConstants.LIFERAY_LAYOUTTPL_PROJECT_FACET.getDefaultVersion();
                liferayFacetInstallProvider = new MavenLayoutTplPluginFacetInstallProvider();
            }
            else if( THEME_PLUGIN_TYPE.equals( pluginType ) )
            {
                newLiferayFacetToInstall = IPluginFacetConstants.LIFERAY_THEME_PROJECT_FACET.getDefaultVersion();
                liferayFacetInstallProvider = new MavenThemePluginFacetInstallProvider();
            }

            if( newLiferayFacetToInstall != null )
            {
                final IDataModel config = DataModelFactory.createDataModel( liferayFacetInstallProvider );
                final Set<Action> actions = new LinkedHashSet<Action>();

                actions.add( new IFacetedProject.Action(
                    IFacetedProject.Action.Type.INSTALL, newLiferayFacetToInstall, config ) );

                facetedProject.modify( actions, monitor );
            }
        }
    }

    private IProjectFacetVersion getLiferayProjectFacet( IFacetedProject facetedProject )
    {
        IProjectFacetVersion retval = null;

        if( facetedProject != null )
        {
            for( IProjectFacetVersion fv : facetedProject.getProjectFacets() )
            {
                if( fv.getProjectFacet().getId().contains( "liferay." ) )
                {
                    retval = fv;
                    break;
                }
            }
        }

        return retval;
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

    private Plugin findLiferayMavenPlugin( MavenProject mavenProject )
    {
        Plugin retval = null;

        if( mavenProject != null )
        {
            retval = mavenProject.getPlugin( LIFERAY_MAVEN_PLUGIN_KEY );

            if( retval == null )
            {
                retval = findLiferayMavenPlugin( mavenProject.getParent() );
            }
        }

        return retval;
    }

    private Xpp3Dom getLiferayMavenPluginConfig( MavenProject mavenProject )
    {
        Xpp3Dom retval = null;

        if( mavenProject != null )
        {
            final Plugin plugin = mavenProject.getPlugin( LIFERAY_MAVEN_PLUGIN_KEY );

            if( plugin != null )
            {
                retval = (Xpp3Dom) plugin.getConfiguration();
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

}
