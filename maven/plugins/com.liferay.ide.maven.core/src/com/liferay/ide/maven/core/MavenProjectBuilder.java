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

import com.liferay.ide.project.core.AbstractProjectBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ICallable;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class MavenProjectBuilder extends AbstractProjectBuilder
{

    protected final IMaven maven = MavenPlugin.getMaven();
    protected final IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();

    public MavenProjectBuilder( IProject project )
    {
        super( project );
    }

    public IStatus buildService( final IFile serviceXmlFile, final IProgressMonitor monitor ) throws CoreException
    {
        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject(), monitor );

        final IStatus retval = this.maven.execute
        (
            new ICallable<IStatus>()
            {
                public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor ) throws CoreException
                {
                    return projectManager.execute
                    (
                        projectFacade,
                        new ICallable<IStatus>()
                        {
                            public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor )
                                throws CoreException
                            {
                                return executeBuildServiceMojo( projectFacade, context, monitor );
                            }
                        },
                        monitor
                    );
                }
            },
            monitor
        );

        refreshSiblingProject( projectFacade, monitor );

        getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );

        return retval;
    }

    protected IStatus executeBuildServiceMojo( final IMavenProjectFacade projectFacade,
                                               final IMavenExecutionContext context,
                                               final IProgressMonitor monitor ) throws CoreException
    {
        IStatus retval = null;

        final List<String> goals = Collections.singletonList( ILiferayMavenConstants.SERVICE_BUILDER_GOAL );
        final MavenExecutionPlan plan =
            this.maven.calculateExecutionPlan( projectFacade.getMavenProject(), goals, true, monitor );

        final MojoExecution serviceBuilderMojoExecution =
            getExecution( plan, ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID );

//        Object serviceBuilderMojo =
//            this.maven.getConfiguredMojo( context.getSession(), serviceBuilderMojoExecution, Object.class );
//        this.maven.releaseMojo( serviceBuilderMojo, serviceBuilderMojoExecution );

        if( serviceBuilderMojoExecution != null )
        {
            ResolverConfiguration configuration = projectFacade.getResolverConfiguration();
            configuration.setResolveWorkspaceProjects( true );
            this.maven.execute( projectFacade.getMavenProject(), serviceBuilderMojoExecution, monitor );
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

    private MojoExecution getExecution( MavenExecutionPlan plan, String artifactId )
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

    public void refreshSiblingProject( IMavenProjectFacade projectFacade, IProgressMonitor monitor ) throws CoreException
    {
        // need to look up project configuration and refresh the *-service project associated with this project
        try
        {
            // not doing any null checks since this is in large try/catch
            final Plugin liferayMavenPlugin = MavenUtil.getLiferayMavenPlugin( projectFacade.getMavenProject() );
            final Xpp3Dom config = (Xpp3Dom) liferayMavenPlugin.getConfiguration();
            final Xpp3Dom apiBaseDir = config.getChild( ILiferayMavenConstants.PLUGIN_CONFIG_API_BASE_DIR );
            // this should be the name path of a project that should be in user's workspace that we can refresh
            final String apiBaseDirValue = apiBaseDir.getValue();

            final IFile apiBasePomFile =
                ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(
                    new Path( apiBaseDirValue ).append( IMavenConstants.POM_FILE_NAME ) );
            final IMavenProjectFacade apiBaseFacade = this.projectManager.create( apiBasePomFile, true, monitor );

            apiBaseFacade.getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( "Could not refresh sibling service project.", e ); //$NON-NLS-1$
        }
    }

}
