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

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.ICallable;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.embedder.IMavenExecutionContext;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.osgi.util.NLS;


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

    public IStatus buildService( final IFile serviceXmlFile, final IProgressMonitor monitor ) throws CoreException
    {
        final IProgressMonitor sub = new SubProgressMonitor( monitor, 100 );

        sub.beginTask( Msgs.buildingServices, 100 );

        final IMavenProjectFacade facade = MavenUtil.getProjectFacade( getProject(), monitor );

        sub.worked( 10 );

        final ICallable<IStatus> callable = new ICallable<IStatus>()
        {
            public IStatus call( IMavenExecutionContext context, IProgressMonitor monitor ) throws CoreException
            {
                return MavenUtil.executeMojoGoal( facade, context, ILiferayMavenConstants.PLUGIN_GOAL_BUILD_SERVICE, monitor );
            }
        };

        final IStatus retval = executeMaven( facade, callable, monitor );

        sub.worked( 70 );

        refreshSiblingProject( facade, monitor );

        sub.worked( 10 );

        getProject().refreshLocal( IResource.DEPTH_INFINITE, monitor );

        sub.worked( 10 );
        sub.done();

        return retval;
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

    protected static class Msgs extends NLS
    {
        public static String buildingServices;
        public static String buildingLanguages;

        static
        {
            initializeMessages( MavenProjectBuilder.class.getName(), Msgs.class );
        }
    }

}
