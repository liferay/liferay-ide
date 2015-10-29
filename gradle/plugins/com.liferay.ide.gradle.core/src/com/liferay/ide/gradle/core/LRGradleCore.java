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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.osgi.framework.BundleContext;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class LRGradleCore extends Plugin
{

    // The shared instance
    private static LRGradleCore plugin;

    // The plugin ID
    public static final String PLUGIN_ID = "com.liferay.ide.gradle.core";

    private static GradleProjectConfigurator gradleProjectConfigurator ;

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
    public static LRGradleCore getDefault()
    {
        return plugin;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T getToolingModel( Class<?> modelClass, IProject gradleProject )
    {
        T retval = null;

        try
        {
            GradleConnector connector = GradleConnector.newConnector();
            connector.forProjectDirectory( gradleProject.getLocation().toFile() );
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
    public LRGradleCore()
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

        if( gradleProjectConfigurator == null )
        {
            gradleProjectConfigurator = new GradleProjectConfigurator();

            ResourcesPlugin.getWorkspace().addResourceChangeListener(
                gradleProjectConfigurator, IResourceChangeEvent.POST_CHANGE );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop( context );

        if( gradleProjectConfigurator != null )
        {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener( gradleProjectConfigurator );
            gradleProjectConfigurator = null;
        }
    }
}
