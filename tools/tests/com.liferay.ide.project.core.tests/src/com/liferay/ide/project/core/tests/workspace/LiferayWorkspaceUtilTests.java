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

package com.liferay.ide.project.core.tests.workspace;

import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.junit.Test;

/**
 * @author Andy Wu
 */
public class LiferayWorkspaceUtilTests
{
    @Test
    public void testLiferayWorkspaceUtil() throws Exception
    {
        NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

        op.setWorkspaceName( "test-liferay-workspace" );

        if( op.validation().ok() )
        {
            op.execute( new ProgressMonitor() );
        }

        IProject workspaceProject = CoreUtil.getProject( "test-liferay-workspace" );

        assertTrue(workspaceProject != null);
        assertTrue(workspaceProject.exists());

        String workspaceLocation = workspaceProject.getLocation().toPortableString();

        String homeValue = LiferayWorkspaceUtil.getHomeDir( workspaceLocation );

        assertTrue( homeValue.equals( "bundles" ) );

        String modulesValue = LiferayWorkspaceUtil.getModulesDir( workspaceProject );

        assertTrue( modulesValue.equals( "modules" ) );

        String pluginSdkValue = LiferayWorkspaceUtil.getPluginsSDKDir( workspaceLocation );

        assertTrue( pluginSdkValue.equals( "plugins-sdk" ) );

        String themesValue = LiferayWorkspaceUtil.getThemesDir( workspaceProject );

        assertTrue( themesValue.equals( "themes" ) );

        String warsValue = LiferayWorkspaceUtil.getWarsDirs( workspaceProject )[0];

        assertTrue( warsValue.equals( "wars" ) );

        File propertiesFile = new File(workspaceLocation+"/gradle.properties");

        Properties prop = PropertiesUtil.loadProperties( propertiesFile);
        prop.setProperty( LiferayWorkspaceUtil.LIFERAY_WORKSPACE_HOME_DIR, "bundles1" );
        prop.setProperty( LiferayWorkspaceUtil.LIFERAY_WORKSPACE_MODULES_DIR, "tests,modules" );
        prop.setProperty( LiferayWorkspaceUtil.LIFERAY_WORKSPACE_PLUGINS_SDK_DIR, "plugins-sdk1" );
        prop.setProperty( LiferayWorkspaceUtil.LIFERAY_WORKSPACE_THEMES_DIR, "themes1" );
        prop.setProperty( LiferayWorkspaceUtil.LIFERAY_WORKSPACE_WARS_DIR, "test1,wars1," );
        PropertiesUtil.saveProperties( prop, propertiesFile );

        workspaceProject.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );

        homeValue = LiferayWorkspaceUtil.getHomeDir( workspaceLocation );


        assertTrue( homeValue.equals( "bundles1" ) );

        modulesValue = LiferayWorkspaceUtil.getModulesDir( workspaceProject );

        assertTrue( modulesValue.equals( "tests" ) );

        pluginSdkValue = LiferayWorkspaceUtil.getPluginsSDKDir( workspaceLocation );

        assertTrue( pluginSdkValue.equals( "plugins-sdk1" ) );

        themesValue = LiferayWorkspaceUtil.getThemesDir( workspaceProject );

        assertTrue( themesValue.equals( "themes1" ) );

        warsValue = LiferayWorkspaceUtil.getWarsDirs( workspaceProject )[0];

        assertTrue( warsValue.equals( "test1" ) );

        workspaceProject.delete(true,true,new NullProgressMonitor());
    }

}
