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

package com.liferay.ide.adt.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;
import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;
import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
public class MobileSDKLibrariesOpTests extends BaseTests
{

    protected File getProjectFile( Bundle bundle, String file ) throws IOException
    {
        return new File( FileLocator.toFileURL( bundle.getEntry( "projects/" + file ) ).getFile() );
    }

    protected IProject importAndroidProject( String projectName, String fileName ) throws Exception
    {
        final IPath projectPath = Activator.getDefault().getStateLocation().append( projectName );

        final File projectDir = projectPath.toFile();

        if( projectDir.exists() )
        {
            projectDir.delete();
        }

        assertTrue( !projectDir.exists() );

        final File projectZip = getProjectFile( Activator.getDefault().getBundle(), fileName );

        ZipUtil.unzip( projectZip, projectDir.getParentFile() );

        final IProjectDescription projectDescription = CoreUtil.getWorkspace().newProjectDescription( projectName );

        projectDescription.setLocation( projectPath );

        final IProject project = CoreUtil.getProject( projectName );

        final NullProgressMonitor npm = new NullProgressMonitor();

        project.create( projectDescription, npm );

        project.open( npm );

        return project;
    }

    @Test
    public void testMobileLibrariesOpAddDefault() throws Exception
    {
        final String projectName = "SampleAndroidApp";
        final String propertiesFileName = "libs/liferay-android-sdk-1.1.jar.properties";

        final IProject sampleProject = importAndroidProject( projectName, projectName + ".zip" );

        assertNotNull( sampleProject );

        assertTrue( sampleProject.exists() );

        final MobileSDKLibrariesOp op = MobileSDKLibrariesOp.TYPE.instantiate();

        op.setProjectName( projectName );

        op.getLibraries().insert().setContext( "Liferay core" );

        op.execute( npm() );

        assertTrue( sampleProject.getFile( "libs/liferay-android-sdk-1.1.jar" ).exists() );

        assertTrue( sampleProject.getFile( propertiesFileName ).exists() );

        assertTrue( sampleProject.getFile( "libs/src/liferay-android-sdk-1.1-sources.jar" ).exists() );

        final String propertiesContent =
            CoreUtil.readStreamToString( sampleProject.getFile( propertiesFileName ).getContents(
                true ) );

        assertEquals(
            stripCarriageReturns( "src=src/liferay-android-sdk-1.1-sources.jar" ),
            stripCarriageReturns( propertiesContent ) );
    }

    private ProgressMonitor npm()
    {
        return ProgressMonitorBridge.create( new NullProgressMonitor() );
    }

}
