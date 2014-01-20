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

import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.core.IRuntime;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class LiferayLanguageFileEncodingTests extends ProjectCoreBase
{
    private IRuntime runtime;

    private IRuntime getRuntime() throws Exception
    {
        if( runtime == null )
        {
            runtime = createNewRuntime( "runtime" );

            assertNotNull( runtime );
        }

        return runtime;
    }

    private IProject importProject( String path, String name ) throws Exception
    {
        final IPath sdkLocation = SDKManager.getInstance().getDefaultSDK().getLocation();
        final IPath hooksFolder = sdkLocation.append( path );

        final URL hookZipUrl =
            Platform.getBundle( "com.liferay.ide.project.core.tests" ).getEntry( "projects/" + name + ".zip" );

        final File hookZipFile = new File( FileLocator.toFileURL( hookZipUrl ).getFile() );

        ZipUtil.unzip( hookZipFile, hooksFolder.toFile() );

        final IPath projectFolder = hooksFolder.append( name );

        assertEquals( true, projectFolder.toFile().exists() );

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectFolder.toOSString() );

        assertNotNull( projectRecord );

        final IProject project =
            ProjectUtil.importProject(
                projectRecord, ServerUtil.getFacetRuntime( getRuntime() ), sdkLocation.toOSString(),
                new NullProgressMonitor() );

        assertNotNull( project );

        assertEquals( "Expected new project to exist.", true, project.exists() );

        return project;
    }

    @Test
    public void testHookProjectEncoding() throws Exception
    {
        final IProject hookProject = importProject( "hooks", "Hook-Encoding-Test-hook" );

        assertEquals( true, ProjectUtil.isHookProject( hookProject ) );

        // TODO test the hook project language properties file encoding
    }

    @Test
    public void testPortletProjectEncoding() throws Exception
    {
        final IProject portletProject = importProject( "portlets", "Portlet-Encoding-Test-portlet" );

        assertEquals( true, ProjectUtil.isPortletProject( portletProject ) );

        // TODO test the portlet project language properties file encoding
    }
}
