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

import com.liferay.ide.core.util.CoreUtil;

import org.apache.maven.model.Plugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.framework.Version;

/**
 * @author Joye Luo
 */
public class MavenGoalUtil
{

    public static String getMavenBuildServiceGoal( IProject project )
    {
        try
        {
            Plugin plugin = MavenUtil.getPlugin(
                MavenUtil.getProjectFacade( project ), ILiferayMavenConstants._LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" +
                    ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID,
                new NullProgressMonitor() );

            if( plugin == null )
            {
                plugin = MavenUtil.getPlugin(
                    MavenUtil.getProjectFacade( project ), ILiferayMavenConstants.NEW_LIFERAY_MAVEN_PLUGINS_GROUP_ID +
                        ":" + ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_SERVICE_BUILDER_KEY,
                    new NullProgressMonitor() );
            }

            return getMavenBuildServiceGoal( plugin );
        }
        catch( CoreException e )
        {
            LiferayMavenCore.logError( e );
        }

        return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_SERVICE;
    }

    public static String getMavenBuildServiceGoal( Plugin plugin )
    {
        if( plugin == null )
        {
            return "build-service";
        }

        if( CoreUtil.compareVersions( new Version( plugin.getVersion() ), new Version( "1.0.145" ) ) >= 0 &&
            plugin.getArtifactId().equals( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_SERVICE_BUILDER_KEY ) )
        {
            return "service-builder:build";
        }

        return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_SERVICE;
    }

    public static String getMavenBuildWSDDGoal( IProject project )
    {
        try
        {
            Plugin plugin = MavenUtil.getPlugin(
                MavenUtil.getProjectFacade( project ), ILiferayMavenConstants._LIFERAY_MAVEN_PLUGINS_GROUP_ID + ":" +
                    ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_ARTIFACT_ID,
                new NullProgressMonitor() );

            if( plugin == null )
            {
                plugin = MavenUtil.getPlugin(
                    MavenUtil.getProjectFacade( project ), ILiferayMavenConstants.NEW_LIFERAY_MAVEN_PLUGINS_GROUP_ID +
                        ":" + ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_WSDD_BUILDER_KEY,
                    new NullProgressMonitor() );
            }

            return getMavenBuildWSDDGoal( plugin );
        }
        catch( CoreException e )
        {
            LiferayMavenCore.logError( e );
        }

        return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_WSDD;
    }

    public static String getMavenBuildWSDDGoal( Plugin plugin )
    {
        if( plugin == null )
        {
            return "build-wsdd";
        }

        if( CoreUtil.compareVersions( new Version( plugin.getVersion() ), new Version( "1.0.7" ) ) >= 0 &&
            plugin.getArtifactId().equals( ILiferayMavenConstants.LIFERAY_MAVEN_PLUGINS_WSDD_BUILDER_KEY ) )
        {
            return "wsdd-builder:build";
        }

        return ILiferayMavenConstants.PLUGIN_GOAL_BUILD_WSDD;
    }

}
