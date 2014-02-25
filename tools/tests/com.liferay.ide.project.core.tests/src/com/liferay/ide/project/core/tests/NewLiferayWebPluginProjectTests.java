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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewLiferayWebPluginProjectTests extends ProjectCoreBase
{

    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-7.0.0" );
    }

    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-7.0.0.zip" );
    }

    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-7.0.0/";
    }

    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-rc6/tomcat-7.0.42" );
    }

    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-rc6-20131028112536986.zip" );
    }

    @Test
    public void testNewWebAntProjectValidation() throws Exception
    {
        IPath liferayPluginsSdkDir = super.getLiferayPluginsSdkDir();

        final File liferayPluginsSdkDirFile = liferayPluginsSdkDir.toFile();

        if( !liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSdkZipFile = super.getLiferayPluginsSDKZip().toFile();

            assertEquals(
                "Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
                liferayPluginsSdkZipFile.exists() );

            liferayPluginsSdkDirFile.mkdirs();

            final String liferayPluginsSdkZipFolder = super.getLiferayPluginsSdkZipFolder();

            if( CoreUtil.isNullOrEmpty( liferayPluginsSdkZipFolder ) )
            {
                ZipUtil.unzip( liferayPluginsSdkZipFile, liferayPluginsSdkDirFile );
            }
            else
            {
                ZipUtil.unzip(
                    liferayPluginsSdkZipFile, liferayPluginsSdkZipFolder, liferayPluginsSdkDirFile,
                    new NullProgressMonitor() );
            }
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        SDK sdk = null;

        final SDK existingSdk = SDKManager.getInstance().getSDK( liferayPluginsSdkDir );

        if( existingSdk == null )
        {
            sdk = SDKUtil.createSDKFromLocation( liferayPluginsSdkDir );
        }
        else
        {
            sdk = existingSdk;
        }

        final String projectName = "test-web-project-sdk";

        final NewLiferayPluginProjectOp op = newProjectOp();

        op.setPluginsSDKName( sdk.getName() );
        op.setProjectName( projectName );
        op.setPluginType( PluginType.web );

        assertEquals(
            "The selected Plugins SDK does not support the web plugin type.  Please configure a higher version greater than 700",
            op.getProjectName().validation().message() );
    }

    @Test
    public void testNewWebAntProject() throws Exception
    {
        final String projectName = "test-web-project-sdk";
        final NewLiferayPluginProjectOp op = newProjectOp();

        op.setProjectName( projectName );
        op.setPluginType( PluginType.web );

        final IProject webProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( webProject );

        assertNotNull( webappRoot );
    }

}
