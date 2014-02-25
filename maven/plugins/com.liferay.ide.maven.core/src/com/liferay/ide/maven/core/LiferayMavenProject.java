/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.BaseLiferayProject;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.util.LiferayPortalValueLoader;
import com.liferay.ide.server.remote.IRemoteServerPublisher;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.jdt.IClasspathManager;
import org.eclipse.m2e.jdt.MavenJdtPlugin;


/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
public class LiferayMavenProject extends BaseLiferayProject
{

    public LiferayMavenProject( IProject project )
    {
        super( project );
    }

    public <T> T adapt( Class<T> adapterType )
    {
        T adapter = super.adapt( adapterType );

        if( adapter != null )
        {
            return adapter;
        }

        if( IProjectBuilder.class.equals( adapterType ) && MavenUtil.getProjectFacade( getProject() ) != null )
        {
            final IProjectBuilder projectBuilder = new MavenProjectBuilder( getProject() );

            return adapterType.cast( projectBuilder );
        }

        if( IRemoteServerPublisher.class.equals( adapterType ) && MavenUtil.getProjectFacade( getProject() ) != null )
        {
            final IRemoteServerPublisher remoteServerPublisher = new MavenProjectRemoteServerPublisher( getProject() );

            return adapterType.cast( remoteServerPublisher );
        }
        
        return null;
    }

    public IPath getAppServerPortalDir()
    {
        IPath retval = null;

        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject() );

        if( projectFacade != null )
        {
            try
            {
                final MavenProject mavenProject = projectFacade.getMavenProject( new NullProgressMonitor() );

                final String appServerPortalDir =
                    MavenUtil.getLiferayMavenPluginConfig(
                        mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR );

                if( ! CoreUtil.isNullOrEmpty( appServerPortalDir ) )
                {
                    retval = new Path( appServerPortalDir );
                }
            }
            catch( CoreException ce )
            {
            }
        }

        return retval;
    }

    public String[] getHookSupportedProperties()
    {
        return new LiferayPortalValueLoader( getAppServerPortalDir(), getUserLibs() ).loadHookPropertiesFromClass();
    }

    public IPath getLibraryPath( String filename )
    {
        final IPath[] libs = getUserLibs();

        if( ! CoreUtil.isNullOrEmpty( libs ) )
        {
            for( IPath lib : libs )
            {
                if( lib.removeFileExtension().lastSegment().startsWith( filename ) )
                {
                    return lib;
                }
            }
        }

        return null;
    }

    public String getLiferayMavenPluginVersion()
    {
        String retval = null;

        final IMavenProjectFacade projectFacade = MavenPlugin.getMavenProjectRegistry().getProject( getProject() );

        if( projectFacade != null )
        {
            final MavenProject mavenProject = projectFacade.getMavenProject();

            if( mavenProject != null )
            {
                final Plugin liferayMavenPlugin = MavenUtil.getLiferayMavenPlugin( mavenProject );

                retval = liferayMavenPlugin.getVersion();
            }
        }

        return retval;
    }

    public String getPortalVersion()
    {
        String retval = null;
        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( getProject() );

        if( projectFacade != null )
        {
            try
            {
                MavenProject mavenProject = projectFacade.getMavenProject( new NullProgressMonitor() );

                String liferayVersion =
                    MavenUtil.getLiferayMavenPluginConfig(
                        mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION );

                retval = MavenUtil.getVersion( liferayVersion );
            }
            catch( CoreException ce )
            {
            }
        }

        return retval;
    }

    public Properties getPortletCategories()
    {
        Properties retval = null;

        final IPath appServerPortalDir = getAppServerPortalDir();

        if( appServerPortalDir != null && appServerPortalDir.toFile().exists() )
        {
            retval = ServerUtil.getPortletCategories( appServerPortalDir );
        }

        return retval;
    }

    public Properties getPortletEntryCategories()
    {
        Properties retval = null;

        final IPath appServerPortalDir = getAppServerPortalDir();

        if( appServerPortalDir != null && appServerPortalDir.toFile().exists() )
        {
            String portalVersion = null;
            ILiferayProject liferayProject = LiferayCore.create( getProject() );

            if( liferayProject != null )
            {
                portalVersion  = liferayProject.getPortalVersion();
            }

            retval = ServerUtil.getEntryCategories( appServerPortalDir, portalVersion );
        }

        return retval;
    }

    public IPath[] getUserLibs()
    {
        final List<IPath> libs = new ArrayList<IPath>();

        final IClasspathManager buildPathManager = MavenJdtPlugin.getDefault().getBuildpathManager();

        try
        {
            final IClasspathEntry[] classpath =
                buildPathManager.getClasspath(
                    getProject(), IClasspathManager.CLASSPATH_RUNTIME, true, new NullProgressMonitor() );

            for( IClasspathEntry entry : classpath )
            {
                libs.add( entry.getPath() );
            }
        }
        catch( CoreException e )
        {
            LiferayMavenCore.logError( "Unable to get maven classpath.", e ); //$NON-NLS-1$
        }

        return libs.toArray( new IPath[0] );
    }

}
