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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Kuo Zhang
 */
public class XmlTestsBase extends ProjectCoreBase
{
    protected boolean checkMarker( IFile descriptorFile, String markerType, String markerMessage ) throws Exception
    {
        final IMarker[] markers = descriptorFile.findMarkers( markerType, false, IResource.DEPTH_ZERO );

        for( IMarker marker : markers )
        {
            if( markerType.equals( marker.getType() ) &&
                marker.getAttribute( IMarker.MESSAGE ).equals( markerMessage ) )
            {
                return true;
            }
        }

        return false;
    }

    protected IProject importProject( String path, String bundleId, String projectName ) throws Exception
    {
        final IPath sdkLocation = SDKManager.getInstance().getDefaultSDK().getLocation();
        final IPath hooksFolder = sdkLocation.append( path );

        final URL hookZipUrl =
            Platform.getBundle( bundleId ).getEntry( "projects/" + projectName + ".zip" );

        final File hookZipFile = new File( FileLocator.toFileURL( hookZipUrl ).getFile() );

        ZipUtil.unzip( hookZipFile, hooksFolder.toFile() );

        final IPath projectFolder = hooksFolder.append( projectName );
        assertEquals( true, projectFolder.toFile().exists() );

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectFolder.toOSString() );
        assertNotNull( projectRecord );

        final IRuntime runtime = ServerCore.findRuntime( getRuntimeVersion() );
        assertNotNull( runtime );

        final IProject project = ProjectUtil.importProject(
            projectRecord, ServerUtil.getFacetRuntime( runtime ), sdkLocation.toOSString(),new NullProgressMonitor() );

        assertNotNull( project );

        assertEquals( "Expected new project to exist.", true, project.exists() );

        return project;
    }
}
