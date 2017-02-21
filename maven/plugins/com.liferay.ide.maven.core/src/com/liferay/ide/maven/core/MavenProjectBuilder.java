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
package com.liferay.ide.maven.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.project.core.AbstractProjectBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ICallable;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.osgi.util.NLS;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class MavenProjectBuilder extends AbstractProjectBuilder
{
    private final String ATTR_GOALS = "M2_GOALS";
    private final String ATTR_POM_DIR = IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
    private final String ATTR_PROFILES = "M2_PROFILES";
    private final String ATTR_SKIP_TESTS = "M2_SKIP_TESTS";
    private final String ATTR_WORKSPACE_RESOLUTION = "M2_WORKSPACE_RESOLUTION";

    private final String LAUNCH_CONFIGURATION_TYPE_ID = "org.eclipse.m2e.Maven2LaunchConfigurationType";

    protected final IMaven maven = MavenPlugin.getMaven();

    protected final IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();

    public MavenProjectBuilder( IProject project )
    {
        super( project );
    }

    public IStatus buildLang( IFile langFile, IProgressMonitor monitor ) throws CoreException
    {
        final IProgressMonitor sub = new SubProgressMonitor( monitor, 100 );

        sub.beginTask( Msgs.buildingLanguages, 100 );

        final IMavenProjectFacade facade = MavenUtil.getProjectFacade( getProject(), sub );

        sub.worked( 10 );

        final ICallable<IStatus> callable = new ICallable<IStatus>()
        {
            public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor ) throws CoreException
            {
                return MavenUtil.executeMojoGoal( facade, context, ILiferayMavenConstants.PLUGIN_GOAL_BUILD_LANG, monitor );
            }
        };

        final IStatus retval = executeMaven( facade, callable, sub );

        sub.worked( 80 );

        getProject().refreshLocal( IResource.DEPTH_INFINITE, sub  );

        sub.worked( 10 );
        sub.done();

        return retval;
    }

    public IStatus buildSB( final IFile serviceXmlFile, final String goal, final IProgressMonitor monitor ) throws CoreException
    {
        final IProject serviceProject = serviceXmlFile.getProject();

        final IMavenProjectFacade facade = MavenUtil.getProjectFacade( serviceProject , monitor );

        monitor.worked( 10 );

        final ICallable<IStatus> callable = new ICallable<IStatus>()
        {
            public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor ) throws CoreException
            {
                return MavenUtil.executeMojoGoal( facade, context, goal, monitor );
            }
        };

        IStatus retval = null;

        final IStatus executeStatus = executeMaven( facade, callable, monitor );

        if( !executeStatus.isOK() && executeStatus.getException() instanceof MojoExecutionException )
        {
             MojoExecutionException mojoException = (MojoExecutionException) executeStatus.getException();

             if( mojoException.getCause() instanceof InvocationTargetException )
             {
                 InvocationTargetException ex = (InvocationTargetException) mojoException.getCause();

                 retval = LiferayMavenCore.createErrorStatus( ex.getTargetException() );
             }
             else
             {
                 retval = LiferayMavenCore.createErrorStatus( mojoException );
             }
        }
        else
        {
            retval = Status.OK_STATUS;
        }

        monitor.worked( 70 );

        refreshSiblingProject( facade, monitor );

        monitor.worked( 10 );

        serviceProject.refreshLocal( IResource.DEPTH_INFINITE, monitor );

        monitor.worked( 10 );
        monitor.done();

        return retval;
    }

    @Override
    public IStatus buildService( IProgressMonitor monitor ) throws CoreException
    {
        final IFile serviceFile = preBuildService( monitor );

        final IProgressMonitor sub = new SubProgressMonitor( monitor, 100 );

        sub.beginTask( Msgs.buildingServices, 100 );

        return buildSB( serviceFile, ILiferayMavenConstants.PLUGIN_GOAL_BUILD_SERVICE, sub );
    }

    @Override
    public IStatus buildWSDD( IProgressMonitor monitor ) throws CoreException
    {
        final IFile serviceFile = preBuildService( monitor );

        final IProgressMonitor sub = new SubProgressMonitor( monitor, 100 );

        sub.beginTask( Msgs.buildingServices, 100 );

        return buildSB( serviceFile, ILiferayMavenConstants.PLUGIN_GOAL_BUILD_WSDD, sub );
    }

    public IStatus execGoals( final List<String> goals, final IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        final IMavenProjectFacade facade = MavenUtil.getProjectFacade( getProject(), monitor );

        final ICallable<IStatus> callable = new ICallable<IStatus>()
        {
            public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor ) throws CoreException
            {
                final IStatus execStatus = MavenUtil.executeGoals( facade, context, goals, monitor );

                final List<Throwable> exceptions = context.getSession().getResult().getExceptions();

                return LiferayMavenCore.newMultiStatus().add( execStatus ).addAll( exceptions ).retval();
            }
        };

        retval = executeMaven( facade, callable, monitor );

        return retval;
    }

    public IStatus execJarMojo( IMavenProjectFacade projectFacade, IProgressMonitor monitor ) throws CoreException
    {

        IStatus retval = null;

        final ICallable<IStatus> callable = new ICallable<IStatus>()
        {

            public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor ) throws CoreException
            {
                MavenProject mavenProject = projectFacade.getMavenProject();

                if( mavenProject == null )
                {
                    mavenProject = projectFacade.getMavenProject( monitor );
                }

                final IMaven maven = MavenPlugin.getMaven();

                final MavenExecutionPlan plan = maven.calculateExecutionPlan( mavenProject, Arrays.asList( "jar:jar" ), true, monitor );
                final List<MojoExecution> mojoExecutions = plan.getMojoExecutions();

                if( mojoExecutions != null )
                {
                    for( MojoExecution mojoExecution : mojoExecutions )
                    {
                        MavenPlugin.getMaven().execute( mavenProject, mojoExecution, monitor );
                    }
                }

                return Status.OK_STATUS;
            }
        };

        retval = executeMaven( projectFacade, callable, monitor );

        return retval;
    }

    private boolean execMavenLaunch(
        final IProject project, final String goal, final IMavenProjectFacade facade, IProgressMonitor monitor )
        throws CoreException
    {
        final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        final ILaunchConfigurationType launchConfigurationType =
            launchManager.getLaunchConfigurationType( LAUNCH_CONFIGURATION_TYPE_ID );
        final IPath basedirLocation = project.getLocation();
        final String newName = launchManager.generateLaunchConfigurationName( basedirLocation.lastSegment() );

        final ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance( null, newName );
        workingCopy.setAttribute(
            IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-Dmaven.multiModuleProjectDirectory" );
        workingCopy.setAttribute( ATTR_POM_DIR, basedirLocation.toString() );
        workingCopy.setAttribute( ATTR_GOALS, goal );
//        workingCopy.setAttribute( ATTR_UPDATE_SNAPSHOTS, true );
        workingCopy.setAttribute( ATTR_WORKSPACE_RESOLUTION, true );
        workingCopy.setAttribute( ATTR_SKIP_TESTS, true );

        if( facade != null )
        {
            final ResolverConfiguration configuration = facade.getResolverConfiguration();

            final String selectedProfiles = configuration.getSelectedProfiles();

            if( selectedProfiles != null && selectedProfiles.length() > 0 )
            {
                workingCopy.setAttribute( ATTR_PROFILES, selectedProfiles );
            }

            new LaunchHelper().launch( workingCopy, "run", monitor );

            return true;
        }
        else
        {
            return false;
        }
    }

    protected IStatus executeMaven( final IMavenProjectFacade projectFacade,
                                    final ICallable<IStatus> callable,
                                    IProgressMonitor monitor ) throws CoreException
    {
        return this.maven.execute
        (
            new ICallable<IStatus>()
            {
                public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor ) throws CoreException
                {
                    return projectManager.execute( projectFacade, callable, monitor );
                }
            },
            monitor
        );
    }

    public IProject getPortletProject( IMavenProjectFacade projectFacade, IProgressMonitor monitor )
        throws CoreException
    {
        IProject retVal = null;
        try
        {
            final Xpp3Dom config = (Xpp3Dom) MavenUtil.getLiferayMavenPluginConfig( projectFacade.getMavenProject() );
            final Xpp3Dom webAppDir = config.getChild( ILiferayMavenConstants.PLUGIN_CONFIG_WEBAPPBASE_DIR );
            final Xpp3Dom pluginName = config.getChild( ILiferayMavenConstants.PLUGIN_CONFIG_PLUGIN_NAME );
            // this should be the name path of a project that should be in user's workspace that we can refresh

            if( webAppDir != null )
            {
                final String webAppDirValue = webAppDir.getValue();
                String projectPath = Path.fromOSString( webAppDirValue ).lastSegment();
                retVal = ResourcesPlugin.getWorkspace().getRoot().getProject( projectPath );
            }
            else if( pluginName != null )
            {
                final String pluginNameValue = pluginName.getValue();
                retVal = CoreUtil.getProject( pluginNameValue );
            }
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( "Could not refresh sibling service project.", e ); //$NON-NLS-1$
        }

        return retVal;
    }

    public IFile preBuildService( IProgressMonitor monitor ) throws CoreException
    {
        IProject project = getProject();

        IFile retval = getDocrootFile( "WEB-INF/" + ILiferayConstants.SERVICE_XML_FILE );

        if( retval == null )
        {
            final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( project );

            if( projectFacade != null )
            {
                final IProject portletProject = getPortletProject( projectFacade, monitor );

                if( portletProject != null )
                {
                    retval =
                        new MavenProjectBuilder( portletProject ).getDocrootFile( "WEB-INF/" +
                            ILiferayConstants.SERVICE_XML_FILE );
                }
            }
        }

        // add support for 7.0 service builder templates

        if( retval == null )
        {
            retval = project.getFile( "service.xml" );
        }

        return retval;
    }

    public void refreshSiblingProject( IMavenProjectFacade projectFacade, IProgressMonitor monitor ) throws CoreException
    {
        // need to look up project configuration and refresh the *-service project associated with this project
        try
        {
            final Plugin plugin6x =
                MavenUtil.getPlugin( projectFacade, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor );

            if( plugin6x != null )
            {
                final Xpp3Dom config = (Xpp3Dom) plugin6x.getConfiguration();
                final Xpp3Dom apiBaseDir = config.getChild( ILiferayMavenConstants.PLUGIN_CONFIG_API_BASE_DIR );
                final String apiBaseDirValue = apiBaseDir.getValue();

                final IFile apiBasePomFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
                    new Path( apiBaseDirValue ).append( IMavenConstants.POM_FILE_NAME ) );
                final IMavenProjectFacade apiBaseFacade = this.projectManager.create( apiBasePomFile, true, monitor );

                apiBaseFacade.getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );
            }
            else
            {
                Plugin plugin7x =
                    MavenUtil.getPlugin( projectFacade, ILiferayMavenConstants.SERVICE_BUILDER_PLUGIN_KEY, monitor );

                if( plugin7x != null )
                {
                    final Xpp3Dom config = (Xpp3Dom) plugin7x.getConfiguration();
                    final Xpp3Dom apiDirName = config.getChild( "apiDirName" );
                    final String apiDirNameValue = apiDirName.getValue();

                    int startIndex = apiDirNameValue.indexOf( "../" );
                    int endIndex = apiDirNameValue.indexOf( "/src/main/java" );
                    String projectName = apiDirNameValue.substring( startIndex + 3, endIndex );

                    IProject project = CoreUtil.getProject( projectName );

                    if( project != null )
                    {
                        project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                    }
                }
            }
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( "Could not refresh sibling service project.", e ); //$NON-NLS-1$
        }
    }

    public boolean runMavenGoal( final IProject project, final String goal, final IProgressMonitor monitor )
        throws CoreException
    {
        final IMavenProjectFacade facade = MavenUtil.getProjectFacade( project, monitor );

        return execMavenLaunch( project, goal, facade, monitor );
    }

    protected static class Msgs extends NLS
    {
        public static String buildingLanguages;
        public static String buildingServices;
        public static String buildingWSDD;

        static
        {
            initializeMessages( MavenProjectBuilder.class.getName(), Msgs.class );
        }
    }

    @Override
    public IStatus execInitBundle( IProject project, String taskName, String bundleUrl, IProgressMonitor monitor )
        throws CoreException
    {
        final IMavenProjectFacade facade = MavenUtil.getProjectFacade( project, monitor );
        execMavenLaunch( project, "liferay:init-bundle", facade, monitor );
        return Status.OK_STATUS;
    }

    @Override
    public IStatus updateProjectDependency( IProject project, List<String[]> dependencies ) throws CoreException
    {
        IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( project, new NullProgressMonitor() );

        if( projectFacade != null )
        {
            MavenProject mavenProject = projectFacade.getMavenProject( new NullProgressMonitor() );
            List<Dependency> existedDependencies = mavenProject.getDependencies();

            final IMaven maven = MavenPlugin.getMaven();
            File pomFile = new File( project.getLocation().toOSString(), IMavenConstants.POM_FILE_NAME );
            Model model = maven.readModel( pomFile );

            for( String[] dependency : dependencies )
            {
                Dependency de = new Dependency();
                de.setGroupId( dependency[0] );
                de.setArtifactId( dependency[1] );
                de.setVersion( dependency[2] );
                String newKey = de.getManagementKey();

                boolean existed = false;

                for( Dependency existedDependency : existedDependencies )
                {
                    String existedKey = existedDependency.getManagementKey();
                    if( existedKey.equals( newKey ) )
                    {
                        existed = true;
                        break;
                    }
                }

                if( existed == false && model != null )
                {
                    model.addDependency( de );
                }
            }

            try(FileOutputStream out = new FileOutputStream( pomFile ))
            {
                maven.writeModel( model, out );
                out.flush();
                out.close();

                final WorkspaceJob job = new WorkspaceJob( "Updating project " + project.getName())
                {

                    public IStatus runInWorkspace( IProgressMonitor monitor )
                    {
                        try
                        {
                            project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
                            MavenPlugin.getProjectConfigurationManager().updateProjectConfiguration( project, monitor );
                        }
                        catch( CoreException ex )
                        {
                            return ex.getStatus( );
                        }

                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
            catch( Exception e )
            {
                return LiferayMavenCore.createErrorStatus( "Error updating maven project dependency", e );
            }
        }

        return Status.OK_STATUS;
    }
}
