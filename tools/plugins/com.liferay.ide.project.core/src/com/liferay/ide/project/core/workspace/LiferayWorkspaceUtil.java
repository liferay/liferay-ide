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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Andy Wu
 */
public class LiferayWorkspaceUtil
{

    public static String multiWorkspaceError = "more than one Liferay Workspace in workspace";

    public static boolean isValidWorkspaceLocation( String location )
    {
        File workspaceDir = new File( location );

        File buildGradle = new File( workspaceDir, "build.gradle" );
        File settingsGradle = new File( workspaceDir, "settings.gradle" );
        File gradleProperties = new File( workspaceDir, "gradle.properties" );

        if( !( buildGradle.exists() && settingsGradle.exists() && gradleProperties.exists() ) )
        {
            return false;
        }

        try
        {
            //TODO Waiting for new release about changing
            //String modulesDir = CoreUtil.readPropertyFileValue( gradleProperties, "liferay.workspace.modules.dir" );
            //String themesDir = CoreUtil.readPropertyFileValue( gradleProperties, "liferay.workspace.themes.dir" );

            String modulesDir = CoreUtil.readPropertyFileValue( gradleProperties, "modules.dir" );
            String themesDir = CoreUtil.readPropertyFileValue( gradleProperties, "themes.dir" );

            if( CoreUtil.empty( modulesDir ) || CoreUtil.empty( themesDir ) )
            {
                return false;
            }

            File modules = new File( workspaceDir, modulesDir );
            File themes = new File( workspaceDir, themesDir );

            if( !( modules.exists() && themes.exists() ) )
            {
                return false;
            }
        }
        catch( Exception e )
        {
            // ignore exception
        }

        return true;
    }

    public static boolean isValidWorkspace( IProject project )
    {
        String location = project.getLocation().toOSString();

        return isValidWorkspaceLocation( location );
    }

    public static boolean hasLiferayWorkspace() throws CoreException
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
            throw new CoreException( ProjectCore.createErrorStatus( multiWorkspaceError ) );
        }

        return false;
    }

}
