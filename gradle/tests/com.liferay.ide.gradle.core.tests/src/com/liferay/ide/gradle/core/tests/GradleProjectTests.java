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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleBundleProject;
import com.liferay.ide.gradle.core.LRGradleCore;
import com.liferay.ide.gradle.toolingapi.custom.CustomModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.eclipse.HierarchicalEclipseProject;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class GradleProjectTests
{

    public static GradleProject fullImportGradleProject( String projectPath ) throws Exception
    {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = ws.getRoot();

        File src = new File( projectPath );
        File dst = new File( root.getLocation().toFile(), src.getName() );

        TestUtil.copyDir( src, dst );

        final List<HierarchicalEclipseProject> projects = GradleImportOperation.allProjects( dst );
        HierarchicalEclipseProject projectModel = projects.get( 0 );

        FlatPrecomputedProjectMapper mapper = new FlatPrecomputedProjectMapper( projects );

        String projectName = mapper.get( projectModel ).getName();
        IProjectDescription projectDescription = ws.newProjectDescription( projectName );

        IProject project = ws.getRoot().getProject( projectName );
        final NullProgressMonitor npm = new NullProgressMonitor();
        project.create( projectDescription, new SubProgressMonitor( npm, 1 ) );
        project.open( npm );
        NatureUtils.ensure( project, npm, GradleNature.NATURE_ID, JavaCore.NATURE_ID );

        return GradleCore.create( project );
    }

    public static GradleProject shallowImportGradleProject( String projectPath ) throws Exception
    {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        File src = new File( projectPath );
        File dst = new File( root.getLocation().toFile(), src.getName() );

        TestUtil.copyDir( src, dst );

        return GradleCore.create( dst );
    }

    @Test
    public void getSymbolicName() throws Exception
    {
        GradleProject gradleProject = fullImportGradleProject( "projects/getSymbolicName" );

        assertNotNull( gradleProject );

        IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        NullProgressMonitor monitor = new NullProgressMonitor();

        IPath outputJar = bundleProject.getOutputJar( false, monitor );

        if( outputJar != null && outputJar.toFile().exists() )
        {
            outputJar = bundleProject.getOutputJar( true, monitor );
        }

        assertTrue( outputJar.toFile().exists() );

        assertEquals( "com.liferay.test.bsn", bundleProject.getSymbolicName() );
    }

    @Test
    public void getOutputJar() throws Exception
    {
        GradleProject gradleProject = fullImportGradleProject( "projects/getOutputJar" );

        assertNotNull( gradleProject );

        IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        NullProgressMonitor monitor = new NullProgressMonitor();

        IPath outputJar = bundleProject.getOutputJar( false, monitor );

        if( outputJar != null && outputJar.toFile().exists() )
        {
            outputJar.toFile().delete();
        }

        assertTrue( !outputJar.toFile().exists() );

        outputJar = bundleProject.getOutputJar( true, monitor );

        assertNotNull( outputJar );

        assertTrue( outputJar.toFile().exists() );
    }

    @Test
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

        GradleProject gradleProject = fullImportGradleProject( "projects/cacheTest" );

        assertNotNull( gradleProject );

        IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        assertEquals( GradleBundleProject.class, bundleProject.getClass() );

        assertEquals( 1, consolesAdded[0] );

        bundleProject = LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        assertEquals( GradleBundleProject.class, bundleProject.getClass() );

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
    }

    @Test
    public void hasGradleBundlePluginDetection() throws Exception
    {
        GradleProject gradleProject = fullImportGradleProject( "projects/org.dm.bundle" );

        assertNotNull( gradleProject );

        final IBundleProject bundleProject =
            LiferayCore.create( IBundleProject.class, gradleProject.getProject() );

        assertNotNull( bundleProject );

        assertEquals( GradleBundleProject.class, bundleProject.getClass() );
    }

    @Test
    public void toolingApiCustomModel() throws Exception
    {
        GradleProject gradleProject = shallowImportGradleProject( "projects/customModel" );

        assertNotNull( gradleProject );

        CustomModel customModel = LRGradleCore.getToolingModel( CustomModel.class, gradleProject );

        assertNotNull( customModel );

        assertFalse( customModel.hasPlugin( "not.a.plugin" ) );

        assertTrue( customModel.hasPlugin( "org.dm.gradle.plugins.bundle.BundlePlugin" ) );
    }

}
