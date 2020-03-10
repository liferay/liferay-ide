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
package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Lovett Li
 */
public class SDKUtilTests extends ProjectCoreBase
{
    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    @Override
    @Before
    public void setupPluginsSDK() throws Exception
    {
    }

    @Test
    public void nullWorkSpaceSDKProject() throws Exception
    {
        deleteAllWorkspaceProjects();

        IProject project = SDKUtil.getWorkspaceSDKProject();

        assertNull( project );
    }

    @Test
    public void singleWorkSpaceProject() throws Exception
    {

        if( shouldSkipBundleTests() ) return;

        final File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

        if( ! liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSdkZipFile = getLiferayPluginsSDKZip().toFile();

            assertEquals(
                "Expected file to exist: " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
                liferayPluginsSdkZipFile.exists() );

            liferayPluginsSdkDirFile.mkdirs();

            final String liferayPluginsSdkZipFolder = getLiferayPluginsSdkZipFolder();

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

        SDK sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );

        SDKUtil.openAsProject( sdk );

        IProject project = SDKUtil.getWorkspaceSDKProject();

        assertNotNull( project );
    }

}
