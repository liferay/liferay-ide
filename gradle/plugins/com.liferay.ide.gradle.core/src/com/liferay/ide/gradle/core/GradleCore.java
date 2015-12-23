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

import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.toolingapi.custom.CustomModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.buildship.core.event.Event;
import org.eclipse.buildship.core.event.EventListener;
import org.eclipse.buildship.core.workspace.ProjectCreatedEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class GradleCore extends Plugin implements EventListener
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

    public static <T> T getToolingModel( Class<T> modelClass, IProject gradleProject )
    {
        return getToolingModel( modelClass, gradleProject.getLocation().toFile() );
    }

    public static <T> T getToolingModel( Class<T> modelClass, File projectDir )
    {
        T retval = null;

        try
        {
            GradleConnector connector = GradleConnector.newConnector();
            connector.forProjectDirectory( projectDir );
            ProjectConnection connection = null;

            try
            {
                connection = connector.connect();
                ModelBuilder<T> modelBuilder = (ModelBuilder<T>) connection.model( modelClass );

                final File localRepo =
                    new File( FileLocator.toFileURL( getDefault().getBundle().getEntry( "repo" ) ).getFile() );

                final String initScriptTemplate =
                    CoreUtil.readStreamToString( new FileInputStream( new File( localRepo, "init.gradle" ) ) );

                String path = localRepo.getAbsolutePath();

                path = path.replaceAll( "\\\\", "/" );
                final String initScriptContents = initScriptTemplate.replaceFirst( "%repo%", path );

                final IPath scriptPath = getDefault().getStateLocation().append( "init.gradle" );

                final File scriptFile = scriptPath.toFile();

                FileUtil.writeFileFromStream( scriptFile, new ByteArrayInputStream( initScriptContents.getBytes() ) );

                modelBuilder.withArguments( "--init-script", scriptFile.getAbsolutePath() );

                retval = modelBuilder.get();
            }
            finally
            {
                if( connection != null )
                {
                    connection.close();
                }
            }
        }
        catch( Exception e )
        {
            logError( "Error getting tooling model", e );
        }

        return retval;
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

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
        plugin = this;

        CorePlugin.listenerRegistry().addEventListener( this );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );

        CorePlugin.listenerRegistry().removeEventListener( this );
    }

    @Override
    public void onEvent( Event event )
    {
        if( event instanceof ProjectCreatedEvent )
        {
            final IProgressMonitor npm = new NullProgressMonitor();
            final ProjectCreatedEvent projectCreatedEvent = (ProjectCreatedEvent) event;

            final IProject project = projectCreatedEvent.getProject();

            if( !LiferayNature.hasNature( project ) )
            {
                try
                {
                    final CustomModel customModel = getToolingModel( CustomModel.class, project );

                    if( customModel.hasPlugin( "aQute.bnd.gradle.BndBuilderPlugin" ) ||
                        customModel.hasPlugin( "com.liferay.gradle.plugins.LiferayPlugin" ) ||
                        customModel.hasPlugin( "com.liferay.gradle.plugins.gulp.GulpPlugin" ) )
                    {
                        LiferayNature.addLiferayNature( project, npm );
                    }
                }
                catch( Exception e )
                {
                    logError( "Unable to get tooling model", e );
                }
            }
        }
    }

    public static Object getProjectInfoFromDir( File file )
    {
        CustomModel model = getToolingModel( CustomModel.class, file );

        Set<File> files = model.getOutputFiles();

        return files;
    }
}
