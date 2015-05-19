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

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewLiferayWebPluginProjectTests extends ProjectCoreBase
{

    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-7.0" );
    }

    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-7.0-ce-m4-20150224120313668.zip" );
    }

    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-7.0/";
    }

    @Test
    public void testNewWebAntProjectValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

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

        final NewLiferayPluginProjectOp op = newProjectOp( projectName );

        op.setPluginsSDKName( sdk.getName() );
        op.setPluginType( PluginType.web );

        assertEquals(
            "The selected Plugins SDK does not support creating new web type plugins.  Please configure version 7.0.0 or greater.",
            op.getPluginType().validation().message() );
    }

    @Test
    public void testNewWebAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String projectName = "test-web-project-sdk";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );

        op.setPluginType( PluginType.web );

        final IProject webProject = createAntProject( op );

        assertNotNull( LiferayCore.create( IWebProject.class, webProject ).getDefaultDocrootFolder() );
    }

}
