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

package com.liferay.ide.gradle.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.LiferayGradleProject;
import com.liferay.ide.gradle.toolingapi.custom.CustomModel;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class GradleProjectTests
{

    public static LiferayGradleProject fullImportGradleProject( String projectPath ) throws Exception
    {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = ws.getRoot();

        File src = new File( projectPath );
        File dst = new File( root.getLocation().toFile(), src.getName() );

        TestUtil.copyDir( src, dst );

        Job job = GradleUtil.importGradleProject( dst );

        try
        {
            job.join();
        }
        catch( InterruptedException e )
        {
        }

        IProject project = CoreUtil.getProject( dst.getName() );

        return new LiferayGradleProject( project );
    }

    @Test
    public void getSymbolicName() throws Exception
    {
        LiferayGradleProject gradleProject = fullImportGradleProject( "projects/getSymbolicName" );

        assertNotNull( gradleProject );

        IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        NullProgressMonitor monitor = new NullProgressMonitor();

        IPath outputJar = bundleProject.getOutputBundle( false, monitor );

        if( outputJar != null && outputJar.toFile().exists() )
        {
            outputJar = bundleProject.getOutputBundle( true, monitor );
        }

        assertTrue( outputJar.toFile().exists() );

        assertEquals( "com.liferay.test.bsn", bundleProject.getSymbolicName() );
    }

    @Test
    public void getOutputJar() throws Exception
    {
        LiferayGradleProject gradleProject = fullImportGradleProject( "projects/getOutputJar" );

        assertNotNull( gradleProject );

        IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        NullProgressMonitor monitor = new NullProgressMonitor();

        IPath outputJar = bundleProject.getOutputBundle( false, monitor );

        if( outputJar != null && outputJar.toFile().exists() )
        {
            outputJar.toFile().delete();
        }

        assertTrue( !outputJar.toFile().exists() );

        outputJar = bundleProject.getOutputBundle( true, monitor );

        assertNotNull( outputJar );

        assertTrue( outputJar.toFile().exists() );
    }

/*    @Test
    public void gradleProjectProviderCache() throws Exception
    {
        final int[] consolesAdded = new int[1];

        IConsoleListener consoleListener = new IConsoleListener()
        {
            @Override
            public void consolesRemoved( IConsole[] consoles )
            {
            }

            @Override
            public void consolesAdded( IConsole[] consoles )
            {
                consolesAdded[0]++;
            }
        };

        ConsolePlugin.getDefault().getConsoleManager().addConsoleListener( consoleListener );;

        LiferayGradleProject gradleProject = fullImportGradleProject( "projects/cacheTest" );

        assertNotNull( gradleProject );

        IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        assertEquals( LiferayGradleProject.class, bundleProject.getClass() );

        assertEquals( 1, consolesAdded[0] );

        bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        assertEquals( LiferayGradleProject.class, bundleProject.getClass() );

        assertEquals( 1, consolesAdded[0] );

        IFile buildFile = gradleProject.getProject().getFile( "build.gradle" );
        String buildFileContents = CoreUtil.readStreamToString( buildFile.getContents( true ), true );
        String updatedContents = buildFileContents.replaceAll( "apply plugin: 'org.dm.bundle'", "" );

        buildFile.setContents(
            new ByteArrayInputStream( updatedContents.getBytes() ), IResource.FORCE, new NullProgressMonitor() );

        final Object lock = new Object();

        IGradleModelListener gradleModelListener = new IGradleModelListener()
        {
            @Override
            public <T> void modelChanged( GradleProject project, Class<T> type, T model )
            {
                synchronized( lock )
                {
                    lock.notify();
                }
            }
        };

        gradleProject.addModelListener( gradleModelListener );
        gradleProject.requestGradleModelRefresh();

        synchronized( lock )
        {
            lock.wait();
        }

        bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNull( bundleProject );

        assertEquals( 2, consolesAdded[0] );

        buildFile.setContents(
            new ByteArrayInputStream( buildFileContents.getBytes() ), IResource.FORCE, new NullProgressMonitor() );

        gradleProject.requestGradleModelRefresh();

        synchronized( lock )
        {
            lock.wait();
        }

        bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        assertEquals( 3, consolesAdded[0] );
    }*/

    @Test
    public void hasGradleBundlePluginDetection() throws Exception
    {
        LiferayGradleProject gradleProject = fullImportGradleProject( "projects/biz.aQute.bundle" );

        assertNotNull( gradleProject );

        final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        assertEquals( LiferayGradleProject.class, bundleProject.getClass() );
    }

    @Test
    public void toolingApiCustomModel() throws Exception
    {
        LiferayGradleProject gradleProject = fullImportGradleProject( "projects/customModel" );

        assertNotNull( gradleProject );

        CustomModel customModel = GradleCore.getToolingModel( CustomModel.class, gradleProject.getProject() );

        assertNotNull( customModel );

        assertFalse( customModel.hasPlugin( "not.a.plugin" ) );

        assertTrue( customModel.hasPlugin( "org.dm.gradle.plugins.bundle.BundlePlugin" ) );
    }

}
