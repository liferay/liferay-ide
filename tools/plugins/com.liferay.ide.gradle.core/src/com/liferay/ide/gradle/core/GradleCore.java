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

package com.liferay.ide.gradle.core;

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.util.Set;

import org.eclipse.buildship.core.configuration.GradleProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class GradleCore extends Plugin
{

    // The shared instance
    private static GradleCore plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.gradle.core";

    public static IStatus createErrorStatus( Exception ex )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, ex.getMessage(), ex );
    }

    public static IStatus createErrorStatus( String msg )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg );
    }

    public static IStatus createErrorStatus( String msg, Exception e )
    {
        return new Status( IStatus.ERROR, PLUGIN_ID, msg, e );
    }

    public static IStatus createWarningStatus( String msg )
    {
        return new Status( IStatus.WARNING, PLUGIN_ID, msg );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static GradleCore getDefault()
    {
        return plugin;
    }

    public static Object getProjectInfoFromDir( GradleCore gradleCore, File file )
    {
        CustomModel model = getToolingModel( gradleCore, CustomModel.class, file );

        Set<File> files = model.getOutputFiles();

        return files;
    }

    public static <T> T getToolingModel( GradleCore gradleCore, Class<T> modelClass, File projectDir )
    {
        T retval = null;

        try
        {
            retval =
                GradleTooling.getModel(
                    modelClass, gradleCore.getStateLocation().append( "cache" ).toFile(), projectDir );
        }
        catch( Exception e )
        {
            logError( "Error getting tooling model", e );
        }

        return retval;
    }

    public static <T> T getToolingModel( GradleCore gradleCore, Class<T> modelClass, IProject gradleProject )
    {
        return getToolingModel( gradleCore, modelClass, gradleProject.getLocation().toFile() );
    }

    public static void logError( Exception ex )
    {
        getDefault().getLog().log( createErrorStatus( ex ) );
    }

    public static void logError( String msg )
    {
        getDefault().getLog().log( createErrorStatus( msg ) );
    }

    public static void logError( String msg, Exception e )
    {
        getDefault().getLog().log( createErrorStatus( msg, e ) );
    }

    /**
     * The constructor
     */
    public GradleCore()
    {
    }

    private static void configureIfLiferayProject( final IProject project, final GradleCore gradleCore ) throws CoreException
    {
        if( project.hasNature( GradleProjectNature.ID ) && !LiferayNature.hasNature( project ) )
        {
            new WorkspaceJob( "Checking gradle configuration" )
            {
                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    try
                    {
                        final CustomModel customModel = getToolingModel( gradleCore, CustomModel.class, project );

                        if( customModel.hasPlugin( "aQute.bnd.gradle.BndBuilderPlugin" ) ||
                            customModel.hasPlugin( "com.liferay.gradle.plugins.LiferayPlugin" ) ||
                            customModel.hasPlugin( "com.liferay.gradle.plugins.gulp.GulpPlugin" ) )
                        {
                            LiferayNature.addLiferayNature( project, new NullProgressMonitor() );
                        }
                    }
                    catch( Exception e )
                    {
                        logError( "Unable to get tooling model", e );
                    }

                    return Status.OK_STATUS;
                }
            }.schedule();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;

        CoreUtil.getWorkspace().addResourceChangeListener( new IResourceChangeListener()
        {
            @Override
            public void resourceChanged( IResourceChangeEvent event )
            {
                try
                {
                    event.getDelta().accept( new IResourceDeltaVisitor()
                    {
                        @Override
                        public boolean visit( IResourceDelta delta ) throws CoreException
                        {
                            IResourceDelta dotProject = delta.findMember( new Path( ".project" ) );

                            if( dotProject == null )
                            {
                                final IResourceDelta[] children = delta.getAffectedChildren();

                                if( CoreUtil.isNotNullOrEmpty( children) )
                                {
                                    final IPath projectPath = children[0].getFullPath();

                                    dotProject = delta.findMember( projectPath.append( ".project" ) );
                                }
                            }

                            if( dotProject != null )
                            {
                                final IProject project = dotProject.getResource().getProject();

                                configureIfLiferayProject( project, GradleCore.this );
                            }

                            return true;
                        }
                    });
                }
                catch( CoreException e )
                {
                }
            }
        }, IResourceChangeEvent.POST_CHANGE );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );
    }
}
