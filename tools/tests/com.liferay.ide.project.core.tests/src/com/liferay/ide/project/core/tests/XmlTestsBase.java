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

import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.ProjectRecord;
import com.liferay.ide.project.core.util.ProjectImportUtil;
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
import org.junit.AfterClass;

/**
 * @author Kuo Zhang
 */
public class XmlTestsBase extends ProjectCoreBase
{
    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

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

    @Override
    protected IProject importProject( String path, String bundleId, String projectName ) throws Exception
    {
        final IPath sdkLocation = SDKManager.getInstance().getDefaultSDK().getLocation();
        final IPath projectFolder = sdkLocation.append( path );

        final URL projectZipUrl =
            Platform.getBundle( bundleId ).getEntry( "projects/" + projectName + ".zip" );

        final File projectZipFile = new File( FileLocator.toFileURL( projectZipUrl ).getFile() );

        ZipUtil.unzip( projectZipFile, projectFolder.toFile() );

        final IPath projectPath = projectFolder.append( projectName );
        assertEquals( true, projectPath.toFile().exists() );

        final ProjectRecord projectRecord = ProjectUtil.getProjectRecordForDir( projectPath.toOSString() );
        assertNotNull( projectRecord );

        final IRuntime runtime = ServerCore.findRuntime( getRuntimeVersion() );
        assertNotNull( runtime );

        final IProject project = ProjectImportUtil.importProject(
            projectRecord, ServerUtil.getFacetRuntime( runtime ), sdkLocation.toOSString(),new NullProgressMonitor() );

        assertNotNull( project );

        assertEquals( "Expected new project to exist.", true, project.exists() );

        return project;
    }
}
