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
package com.liferay.mobile.sdk.core;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.core.util.RuntimeClasspathModel;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Gregory Amerson
 */
public class SDKBuilderHelper extends LaunchHelper
{

    public SDKBuilderHelper( String platform, String url, String context, String packageName, String filter, String dest )
    {
        super( IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION );

        setLaunchSync( true );
        setLaunchInBackground( true );
        setLaunchCaptureInConsole( true );
        setLaunchIsPrivate( true );

        setMainClass( "com.liferay.mobile.sdk.SDKBuilder" );
        setLaunchArgs( new String[] { "platform=" + platform, "url=" + url, "context=" + context,
            "packageName=" + packageName, "filter=" + filter, "destination=" + dest } );
    }

    protected void addUserEntries( RuntimeClasspathModel model ) throws CoreException
    {
        final String[] coreLibs =
        {
            "lib/org.apache.httpcomponents.httpclient_4.2.1.jar",
            "lib/org.apache.httpcomponents.httpcore_4.2.1.jar",
            "lib/org.apache.httpcomponents.httpmime_4.2.1.jar",
        };

        final String[] mobileLibs =
        {
            "lib/liferay-sdk-builder-6.2.0.1.jar",
            "lib/velocity-1.7.jar",
            "lib/velocity-tools-2.0.jar",
        };

        final String[] bundles =
        {
            "org.apache.commons.collections",
            "org.apache.commons.lang",
            "org.apache.commons.logging",
            "org.json",
        };

        try
        {
            for( String bundle : bundles )
            {
                final File file = FileLocator.getBundleFile( Platform.getBundle( bundle ) );

                model.addEntry(
                    RuntimeClasspathModel.USER,
                    JavaRuntime.newArchiveRuntimeClasspathEntry( new Path( file.getAbsolutePath() ) ) );
            }

            for( String lib : coreLibs )
            {
                final Path path = new Path( FileLocator.toFileURL(
                    LiferayCore.getDefault().getBundle().getEntry( lib ) ).getFile() );

                model.addEntry( RuntimeClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry( path ) );
            }

            for( String lib : mobileLibs )
            {
                final Path path = new Path( FileLocator.toFileURL(
                    MobileSDKCore.getDefault().getBundle().getEntry( lib ) ).getFile() );

                model.addEntry( RuntimeClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry( path ) );
            }
        }
        catch( Exception e )
        {
        }
    }

}
