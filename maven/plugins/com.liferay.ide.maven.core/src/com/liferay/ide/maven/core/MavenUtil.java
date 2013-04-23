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

import java.util.Collections;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class MavenUtil
{

    public static Plugin getLiferayMavenPlugin( MavenProject mavenProject )
    {
        Plugin retval = null;

        if( mavenProject != null )
        {
            retval = mavenProject.getPlugin( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY );
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

    public static boolean isMavenProject( IProject project ) throws CoreException
    {
        return project != null &&
            ( project.hasNature( IMavenConstants.NATURE_ID ) || project.getFile( IMavenConstants.POM_FILE_NAME ).exists() );
    }

    public static boolean isPomFile( IFile pomFile )
    {
        return pomFile != null && pomFile.exists() && IMavenConstants.POM_FILE_NAME.equals( pomFile.getName() ) &&
            pomFile.getParent() instanceof IProject;
    }

}
