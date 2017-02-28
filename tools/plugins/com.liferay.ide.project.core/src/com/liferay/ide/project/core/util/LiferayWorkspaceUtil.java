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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @author Andy Wu
 */
public class LiferayWorkspaceUtil
{

    public static String multiWorkspaceErrorMsg = "More than one Liferay workspace build in current Eclipse workspace.";

    public static String hasLiferayWorkspaceMsg =
        "A Liferay Workspace project already exists in this Eclipse instance.";

    private final static Pattern PATTERN_WORKSPACE_PLUGIN = Pattern.compile(
        ".*apply.*plugin.*:.*[\'\"]com\\.liferay\\.workspace[\'\"].*", Pattern.MULTILINE | Pattern.DOTALL );

    public static void clearWorkspace( String location )
    {
        File projectFile = new File( location, ".project" );

        if( projectFile.exists() )
        {
            projectFile.delete();
        }

        File classpathFile = new File( location, ".classpath" );

        if( classpathFile.exists() )
            classpathFile.delete();

        File settings = new File( location, ".settings" );

        if( settings.exists() && settings.isDirectory() )
        {
            FileUtil.deleteDir( settings, true );
        }
    }

    private static boolean isValidGradleWorkspace( IProject project )
    {
        return project != null && project.getLocation() != null &&
            isValidGradleWorkspaceLocation( project.getLocation().toOSString() );
    }

    private static boolean isValidMavenWorkspace( IProject project )
    {
        return project != null && project.getLocation() != null &&
            isValidMavenWorkspaceLocation( project.getLocation().toOSString() );
    }

    private static boolean isValidGradleWorkspaceLocation( String location )
    {
        File workspaceDir = new File( location );

        File buildGradle = new File( workspaceDir, "build.gradle" );
        File settingsGradle = new File( workspaceDir, "settings.gradle" );
        File gradleProperties = new File( workspaceDir, "gradle.properties" );

        if( !( buildGradle.exists() && settingsGradle.exists() && gradleProperties.exists() ) )
        {
            return false;
        }

        final String settingsContent = FileUtil.readContents( settingsGradle, true );

        return settingsContent != null && PATTERN_WORKSPACE_PLUGIN.matcher( settingsContent ).matches();
    }

    private static boolean isValidMavenWorkspaceLocation( String location )
    {
        File workspaceDir = new File( location );

        File pomFile = new File( workspaceDir, "pom.xml" );

        if( pomFile.exists() )
        {
            String content = FileUtil.readContents( pomFile );

            if( content.contains( "com.liferay.portal.tools.bundle.support" ) )
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isValidWorkspaceLocation( String location )
    {
        if( isValidMavenWorkspaceLocation( location ) || isValidGradleWorkspaceLocation( location ) )
        {
            return true;
        }

        return false;
    }

    public static boolean isValidWorkspace( IProject project )
    {
        return project != null &&
            project.getLocation() != null &&
            isValidWorkspaceLocation( project.getLocation().toOSString() );
    }

    public static boolean hasBundlesDir( String location )
    {
        File bundles = new File( location, getHomeDir( location ) );

        return bundles.exists() && bundles.isDirectory();
    }

    public static boolean hasGradleWorkspace() throws CoreException
    {
        IProject[] projects = CoreUtil.getAllProjects();

        int count = 0;

        for( IProject project : projects )
        {
            if( isValidGradleWorkspace( project ) )
            {
                ++count;
            }
        }

        if( count == 1 )
        {
            return true;
        }
        else if( count > 1 )
        {
            throw new CoreException( ProjectCore.createErrorStatus( multiWorkspaceErrorMsg ) );
        }

        return false;
    }

    public static boolean hasMavenWorkspace() throws CoreException
    {
        IProject[] projects = CoreUtil.getAllProjects();

        int count = 0;

        for( IProject project : projects )
        {
            if( isValidMavenWorkspace( project ) )
            {
                ++count;
            }
        }

        if( count == 1 )
        {
            return true;
        }
        else if( count > 1 )
        {
            throw new CoreException( ProjectCore.createErrorStatus( multiWorkspaceErrorMsg ) );
        }

        return false;
    }

    public static boolean hasWorkspace() throws CoreException
    {
        IProject[] projects = CoreUtil.getAllProjects();

        int count = 0;

        for( IProject project : projects )
        {
            if( isValidWorkspace( project ) )
            {
                ++count;
            }
        }

        if( count == 1 )
        {
            return true;
        }
        else if( count > 1 )
        {
            throw new CoreException( ProjectCore.createErrorStatus( multiWorkspaceErrorMsg ) );
        }

        return false;
    }

    private static String getGradleProperty( String projectLocation, String key, String defaultValue )
    {
        File gradleProperties = new File( projectLocation, "gradle.properties" );

        String retVal = null;

        if( gradleProperties.exists() )
        {
            Properties properties = PropertiesUtil.loadProperties( gradleProperties );

            retVal = properties.getProperty( key, defaultValue );
        }

        return retVal;
    }

    public static IProject getWorkspaceProject()
    {
        IProject[] projects = CoreUtil.getAllProjects();

        for( IProject project : projects )
        {
            if( isValidWorkspace( project ) )
            {
                return project;
            }
        }
        return null;
    }

    public static String getModulesDir( final IProject project )
    {
        String retval = null;

        if( project != null )
        {
            final IPath projectLocation = project.getLocation();

            if( projectLocation != null )
            {
                retval = getGradleProperty( projectLocation.toPortableString(),
                    "liferay.workspace.modules.dir", "modules" );
            }
        }

        return retval == null ? "modules" : retval;
    }

    public static String getThemesDir( final IProject project )
    {
        String retval = null;

        if( project != null )
        {
            final IPath projectLocation = project.getLocation();

            if( projectLocation != null )
            {
                retval = getGradleProperty( projectLocation.toPortableString(),
                    "liferay.workspace.themes.dir", "themes" );
            }
        }

        return retval == null ? "themes" : retval;
    }

    public static String[] getWarsDirs( final IProject project )
    {
        String[] retval = null;

        if( project != null )
        {
            final IPath projectLocation = project.getLocation();

            if( projectLocation != null )
            {
                String val = getGradleProperty( projectLocation.toPortableString(),
                    "liferay.workspace.wars.dir", "wars" );

                val = ( val == null ? "wars" : val );

                retval = val.split( "," );
            }
        }

        return retval;
    }

    public static String getWorkspaceType( String location )
    {
        if( isValidWorkspaceLocation( location ) )
        {
            File pomFile = new File( location, "pom.xml" );

            if( pomFile.exists() )
            {
                return "maven-liferay-workspace";
            }
            else
            {
                return "gradle-liferay-workspace";
            }
        }

        return null;
    }

    public static String getHomeDir( String location )
    {
        String result = getGradleProperty( location, "liferay.workspace.home.dir", "bundles" );

        return result == null ? "bundles" : result;
    }
}
