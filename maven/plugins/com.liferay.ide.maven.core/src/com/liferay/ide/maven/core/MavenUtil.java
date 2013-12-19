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

import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenConfiguration;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.embedder.MavenRuntimeManager;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.wtp.ProjectUtils;
import org.eclipse.wst.xml.core.internal.provisional.format.NodeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class MavenUtil
{

    public static IStatus executeMojoGoal( final IMavenProjectFacade projectFacade,
                                           final IMavenExecutionContext context,
                                           final String goal,
                                           final IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;
        final IMaven maven = MavenPlugin.getMaven();

        final List<String> goals = Collections.singletonList( goal );
        final MavenExecutionPlan plan = maven.calculateExecutionPlan( projectFacade.getMavenProject(), goals, true, monitor );

//        context.getExecutionRequest().setOffline( true );
//        context.getExecutionRequest().setRecursive( false );

        final MojoExecution liferayMojoExecution =
            getExecution( plan, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID );

        if( liferayMojoExecution != null )
        {
            ResolverConfiguration configuration = projectFacade.getResolverConfiguration();
            configuration.setResolveWorkspaceProjects( true );

            maven.execute( projectFacade.getMavenProject(), liferayMojoExecution, monitor );
        }

        List<Throwable> exceptions = context.getSession().getResult().getExceptions();

        if( exceptions.size() == 1 )
        {
            retval = LiferayMavenCore.createErrorStatus( exceptions.get( 0 ) );
        }
        else if( exceptions.size() > 1 )
        {
            List<IStatus> statues = new ArrayList<IStatus>();

            for( Throwable t : exceptions )
            {
                statues.add( LiferayMavenCore.createErrorStatus( t ) );
            }

            final IStatus firstStatus = statues.get( 0 );
            retval =
                new MultiStatus(
                    LiferayMavenCore.PLUGIN_ID, IStatus.ERROR, statues.toArray( new IStatus[0] ),
                    firstStatus.getMessage(), firstStatus.getException() );
        }

        return retval == null ? Status.OK_STATUS : retval;
    }


    public static MojoExecution getExecution( MavenExecutionPlan plan, String artifactId )
    {
        if( plan != null )
        {
            for( MojoExecution execution : plan.getMojoExecutions() )
            {
                if( artifactId.equals( execution.getArtifactId() ) )
                {
                    return execution;
                }
            }
        }

        return null;
    }

    public static IFolder getGeneratedThemeResourcesFolder( MavenProject mavenProject, IProject project )
    {
        IPath m2eLiferayFolder = getM2eLiferayFolder( mavenProject, project );

        return project.getFolder( m2eLiferayFolder ).getFolder( ILiferayMavenConstants.THEME_RESOURCES_FOLDER );
    }

    public static Plugin getLiferayMavenPlugin( MavenProject mavenProject )
    {
        Plugin retval = null;

        if( mavenProject != null )
        {
            retval = mavenProject.getPlugin( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY );
        }

        if( retval == null )
        {
            retval =
                mavenProject.getPluginManagement().getPluginsAsMap().get(
                    ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY );
        }

        return retval;
    }

    public static Xpp3Dom getLiferayMavenPluginConfig( MavenProject mavenProject )
    {
        Xpp3Dom retval = null;

        if( mavenProject != null )
        {
            final Plugin plugin = mavenProject.getPlugin( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY );

            if( plugin != null )
            {
                retval = (Xpp3Dom) plugin.getConfiguration();
            }
        }

        return retval;
    }

    public static String getLiferayMavenPluginConfig( MavenProject mavenProject, String childElement )
    {
        String retval = null;

        Xpp3Dom liferayMavenPluginConfig = getLiferayMavenPluginConfig( mavenProject );

        if( liferayMavenPluginConfig != null )
        {
            final Xpp3Dom childNode = liferayMavenPluginConfig.getChild( childElement );

            if( childNode != null )
            {
                retval = childNode.getValue();
            }
        }

        return retval;
    }

    public static String getLiferayMavenPluginType( MavenProject mavenProject )
    {
        String pluginType = getLiferayMavenPluginConfig( mavenProject,
                                                                   ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_TYPE );

        if( pluginType == null )
        {
            // check for EXT
            pluginType = ILiferayMavenConstants.DEFAULT_PLUGIN_TYPE;
        }

        return pluginType;
    }

    public static String getLocalRepositoryDir()
    {
        String retval = null;

        final IMavenConfiguration mavenConfiguration = MavenPlugin.getMavenConfiguration();
        String userSettings = mavenConfiguration.getUserSettingsFile();

        if( userSettings == null || userSettings.length() == 0 )
        {
            userSettings = MavenCli.DEFAULT_USER_SETTINGS_FILE.getAbsolutePath();
        }

        final MavenRuntimeManager runtimeManager = MavenPlugin.getMavenRuntimeManager();

        final String globalSettings = runtimeManager.getGlobalSettingsFile();

        final IMaven maven = MavenPlugin.getMaven();

        try
        {
            final Settings settings = maven.buildSettings( globalSettings, userSettings );
            retval = settings.getLocalRepository();
        }
        catch( CoreException e )
        {
            LiferayMavenCore.logError( "Unable to get local repository dir.", e );
        }

        if( retval == null )
        {
            retval = org.apache.maven.repository.RepositorySystem.defaultUserLocalRepository.getAbsolutePath();
        }

        return retval;
    }

    public static IPath getM2eLiferayFolder( MavenProject mavenProject, IProject project )
    {
        String buildOutputDir = mavenProject.getBuild().getDirectory();
        String relativeBuildOutputDir = ProjectUtils.getRelativePath( project, buildOutputDir );

        return new Path( relativeBuildOutputDir ).append( ILiferayMavenConstants.M2E_LIFERAY_FOLDER );
    }

    public static IMavenProjectFacade getProjectFacade( final IProject project )
    {
        return getProjectFacade( project, new NullProgressMonitor() );
    }

    public static IMavenProjectFacade getProjectFacade( final IProject project, final IProgressMonitor monitor )
    {
        final IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
        final IFile pomResource = project.getFile( IMavenConstants.POM_FILE_NAME );

        IMavenProjectFacade projectFacade = projectManager.create( project, monitor );

        if( projectFacade == null || projectFacade.isStale() )
        {
            try
            {
                projectManager.refresh( Collections.singleton( pomResource ), monitor );
            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError( e );
            }

            projectFacade = projectManager.create( project, monitor );

            if( projectFacade == null )
            {
                // error marker should have been created
            }
        }

        return projectFacade;
    }

    public static String getVersion( String version )
    {
        DefaultArtifactVersion v = new DefaultArtifactVersion( version );

        return v.getMajorVersion() + "." + v.getMinorVersion() + "." + v.getIncrementalVersion(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static boolean isMavenProject( IProject project ) throws CoreException
    {
        return project != null && project.exists() &&
            ( project.hasNature( IMavenConstants.NATURE_ID ) || project.getFile( IMavenConstants.POM_FILE_NAME ).exists() );
    }

    public static boolean isPomFile( IFile pomFile )
    {
        return pomFile != null && pomFile.exists() && IMavenConstants.POM_FILE_NAME.equals( pomFile.getName() ) &&
            pomFile.getParent() instanceof IProject;
    }

    public static boolean loadParentHierarchy( final IMaven maven,
                                               final IMavenProjectFacade facade,
                                               final IMavenExecutionContext context,
                                               final IProgressMonitor monitor ) throws CoreException
    {
        boolean loadedParent = false;
        MavenProject mavenProject = facade.getMavenProject();

        try
        {
            if( mavenProject.getModel().getParent() == null || mavenProject.getParent() != null )
            {
                // If the method is called without error, we can assume the project has been fully loaded
                // No need to continue.
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
                request = context.getExecutionRequest();
            }

            MavenProject parentProject = maven.resolveParentProject( mavenProject, monitor );

            if( parentProject != null )
            {
                mavenProject.setParent( parentProject );
                loadedParent = true;
            }

            mavenProject = parentProject;
        }

        return loadedParent;
    }

    public static void setConfigValue( Xpp3Dom configuration, String childName, Object value )
    {
        Xpp3Dom childNode = configuration.getChild( childName );

        if( childNode == null )
        {
            childNode = new Xpp3Dom( childName );
            configuration.addChild( childNode );
        }

        childNode.setValue( ( value == null ) ? null : value.toString() );
    }


    public static Node createNewLiferayProfileNode(
        Document pomDocument, NewLiferayProfile newLiferayProfile, final String pluginVersion )
    {
        Node newNode = null;

        final String liferayVersion = newLiferayProfile.getLiferayVersion().content();

        try
        {
            final String runtimeName = newLiferayProfile.getRuntimeName().content();
            final ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( ServerUtil.getRuntime( runtimeName ) );

            final Element root = pomDocument.getDocumentElement();

            Element profiles = NodeUtil.findChildElement( root, "profiles" );

            if( profiles == null )
            {
                newNode = profiles = NodeUtil.appendChildElement( root, "profiles" );
            }

            Element newProfile = null;

            if( profiles != null )
            {
                NodeUtil.appendTextNode( profiles, "\n" );
                newProfile = NodeUtil.appendChildElement( profiles, "profile" );
                NodeUtil.appendTextNode( profiles, "\n" );

                if( newNode == null )
                {
                    newNode = newProfile;
                }
            }

            if( newProfile != null )
            {
                final IPath autoDeployDir =
                    liferayRuntime.getAppServerDir().removeLastSegments( 1 ).append( "deploy" );

                NodeUtil.appendTextNode( newProfile, "\n\t" );

                NodeUtil.appendChildElement( newProfile, "id", newLiferayProfile.getId().content() );
                NodeUtil.appendTextNode( newProfile, "\n\t" );

                final Element propertiesElement = NodeUtil.appendChildElement( newProfile, "properties" );

                NodeUtil.appendTextNode( newProfile, "\n\t" );
                NodeUtil.appendTextNode( propertiesElement, "\n\t\t" );
                NodeUtil.appendChildElement( propertiesElement, "liferay.version", liferayVersion );
                NodeUtil.appendTextNode( propertiesElement, "\n\t\t" );
                NodeUtil.appendChildElement( propertiesElement, "liferay.maven.plugin.version", pluginVersion );
                NodeUtil.appendTextNode( propertiesElement, "\n\t\t" );
                NodeUtil.appendChildElement(
                    propertiesElement, "liferay.auto.deploy.dir", autoDeployDir.toOSString() );
                NodeUtil.appendTextNode( propertiesElement, "\n\t\t");
                NodeUtil.appendChildElement(
                    propertiesElement, "liferay.app.server.deploy.dir",
                    liferayRuntime.getAppServerDeployDir().toOSString() );
                NodeUtil.appendTextNode( propertiesElement, "\n\t\t" );
                NodeUtil.appendChildElement(
                    propertiesElement, "liferay.app.server.lib.global.dir",
                    liferayRuntime.getAppServerLibGlobalDir().toOSString() );
                NodeUtil.appendTextNode( propertiesElement, "\n\t\t" );
                NodeUtil.appendChildElement(
                    propertiesElement, "liferay.app.server.portal.dir",
                    liferayRuntime.getAppServerPortalDir().toOSString() );
                NodeUtil.appendTextNode( propertiesElement, "\n\t" );
                
                NodeFormatter formatter = new NodeFormatter();
                formatter.format( newNode );
            }
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( "Unable to add new liferay profile.", e );
        }

        return newNode;
    }

}
