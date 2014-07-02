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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ICallable;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;


/**
 * @author Gregory Amerson
 */
public abstract class ThemePluginBuildParticipant extends AbstractBuildParticipant
{
    protected final IMaven maven = MavenPlugin.getMaven();
    protected final IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();

    @Override
    public Set<IProject> build( int kind, IProgressMonitor monitor ) throws Exception
    {
        final IMavenProjectFacade facade = getMavenProjectFacade();

        if( ! shouldBuild( kind, facade ) )
        {
            return null;
        }

        final ICallable<IStatus> callable = new ICallable<IStatus>()
        {
            public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor ) throws CoreException
            {
                return executeThemeMojo( facade, context, monitor );
            }
        };

        IStatus retval = null;

        try
        {
            retval = executeMaven( facade, callable, monitor );
        }
        catch( Exception e )
        {
            retval = LiferayMavenCore.createErrorStatus( getGoal() + " build error", e ); //$NON-NLS-1$
        }

        if( retval != null && ! retval.isOK() )
        {
            LiferayMavenCore.log( retval );
        }

        try
        {
            facade.getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( CoreException e )
        {
        }

        monitor.worked( 10 );

        return null;
    }

    protected void configureExecution( IMavenProjectFacade facade, Xpp3Dom config )
    {
        final IPath m2eLiferayFolder =
                        MavenUtil.getM2eLiferayFolder( facade.getMavenProject(), facade.getProject() );
        final IPath themeResourcesFolder =
                        m2eLiferayFolder.append( ILiferayMavenConstants.THEME_RESOURCES_FOLDER );
        final String targetFolderValue = themeResourcesFolder.toPortableString();

        MavenUtil.setConfigValue( config, ILiferayMavenConstants.PLUGIN_CONFIG_WEBAPP_DIR, targetFolderValue );
    }

    protected IStatus executeMaven( final IMavenProjectFacade projectFacade,
                                    final ICallable<IStatus> callable,
                                    final IProgressMonitor monitor ) throws CoreException
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


    protected IStatus executeThemeMojo( final IMavenProjectFacade facade,
                                        final IMavenExecutionContext context,
                                        final IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        final List<String> goals = Collections.singletonList( getGoal() );

        final MavenProject mavenProject = facade.getMavenProject( monitor );
        final MavenExecutionPlan plan = maven.calculateExecutionPlan( mavenProject, goals, true, monitor );

        monitor.worked( 10 );

        final MojoExecution liferayMojoExecution =
            MavenUtil.getExecution( plan, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID );

        final Xpp3Dom originalConfig = liferayMojoExecution.getConfiguration();

        final Xpp3Dom config = Xpp3DomUtils.mergeXpp3Dom( new Xpp3Dom( "configuration" ), originalConfig ); //$NON-NLS-1$

        configureExecution( facade, config );

        boolean parentHierarchyLoaded = false;

        try
        {
            parentHierarchyLoaded = MavenUtil.loadParentHierarchy( facade, monitor );

            monitor.worked( 10 );

            final ResolverConfiguration configuration = facade.getResolverConfiguration();
            configuration.setResolveWorkspaceProjects( true );

            liferayMojoExecution.setConfiguration( config );

            maven.execute( mavenProject, liferayMojoExecution, monitor );

            monitor.worked( 50 );

            List<Throwable> exceptions = context.getSession().getResult().getExceptions();

            if( exceptions.size() == 1 )
            {
                retval = LiferayMavenCore.createErrorStatus( exceptions.get( 0 ) );
            }
            else if( exceptions.size() > 1 )
            {
                List<IStatus> statuses = new ArrayList<IStatus>();

                for( Throwable t : exceptions )
                {
                    statuses.add( LiferayMavenCore.createErrorStatus( t ) );
                }

                retval = LiferayMavenCore.createMultiStatus( IStatus.ERROR, statuses.toArray( new IStatus[0] ) );
            }

            retval = retval == null ? Status.OK_STATUS : retval;
        }
        catch( CoreException e )
        {
            retval = LiferayMavenCore.createErrorStatus( e );
        }
        finally
        {
            liferayMojoExecution.setConfiguration( originalConfig );

            if( parentHierarchyLoaded )
            {
                mavenProject.setParent( null );
            }
        }

        return retval;
    }

    protected abstract String getGoal();

    protected abstract boolean shouldBuild( int kind, IMavenProjectFacade facade );
}
