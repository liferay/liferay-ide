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

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.util.LiferayPortalValueLoader;
import com.liferay.ide.server.util.ServerUtil;

import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class LiferayPortalMaven implements ILiferayPortal
{
    private final IMavenProject lrMvnProject;

    public LiferayPortalMaven( IMavenProject project )
    {
        this.lrMvnProject = project;
    }

    @Override
    public IPath getAppServerPortalDir()
    {
        IPath retval = null;

        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( this.lrMvnProject.getProject() );

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

    @Override
    public String[] getHookSupportedProperties()
    {
        return new LiferayPortalValueLoader(
            getAppServerPortalDir(), this.lrMvnProject.getUserLibs() ).loadHookPropertiesFromClass();
    }

    @Override
    public Properties getPortletCategories()
    {
        Properties retval = null;

        final IPath appServerPortalDir = getAppServerPortalDir();

        if( appServerPortalDir != null && appServerPortalDir.toFile().exists() )
        {
            retval = ServerUtil.getPortletCategories( appServerPortalDir );
        }

        if( retval == null )
        {
            retval = new Properties();

            retval.put( "category.sample", "Sample" );
        }

        return retval;
    }

    @Override
    public Properties getPortletEntryCategories()
    {
        Properties retval = null;

        final IPath appServerPortalDir = getAppServerPortalDir();

        if( appServerPortalDir != null && appServerPortalDir.toFile().exists() )
        {
            String portalVersion = getVersion();

            if( portalVersion != null )
            {
                retval = ServerUtil.getEntryCategories( appServerPortalDir, portalVersion );
            }
        }

        return retval;
    }

    @Override
    public String getVersion()
    {
        String retval = null;
        final IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade( this.lrMvnProject.getProject() );

        if( projectFacade != null )
        {
            try
            {
                final MavenProject mavenProject = projectFacade.getMavenProject( new NullProgressMonitor() );

                String liferayVersion =
                    MavenUtil.getLiferayMavenPluginConfig(
                        mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION );

                if( liferayVersion == null )
                {
                    liferayVersion = mavenProject.getProperties().getProperty( "liferay.version" );

                    if( liferayVersion == null )
                    {
                        // look through dependencies for portal-service
                        final List<Dependency> deps = mavenProject.getDependencies();

                        if( deps != null )
                        {
                            for( Dependency dep : deps )
                            {
                                if( dep.getArtifactId().startsWith( "portal-" ) &&
                                    dep.getGroupId().startsWith( "com.liferay" ) )
                                {
                                    liferayVersion = dep.getVersion();
                                    break;
                                }
                            }
                        }
                    }
                }

                if( liferayVersion != null )
                {
                    retval = MavenUtil.getVersion( liferayVersion );
                }
            }
            catch( CoreException e )
            {
            }
        }

        return retval;
    }
}
