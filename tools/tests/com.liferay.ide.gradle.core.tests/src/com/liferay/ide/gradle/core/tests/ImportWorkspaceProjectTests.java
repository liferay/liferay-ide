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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.LiferayGradleProject;
import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;

import java.io.File;
import java.nio.file.Files;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ImportWorkspaceProjectTests
{
    @Before
    public void clearWorkspace() throws Exception
    {
        Util.deleteAllWorkspaceProjects();
    }

    @Test
    public void testImportLiferayWorkspace() throws Exception
    {
        LiferayGradleProject rootProject = Util.fullImportGradleProject( "projects/testWorkspace" );

        assertNotNull( rootProject );

        Util.waitForBuildAndValidation();

        assertLiferayProject( "jstl.test" );
        assertLiferayProject( "roster-api" );
        assertLiferayProject( "roster-service" );
        assertLiferayProject( "roster-web" );
        assertLiferayProject( "sample-portlet" );
        assertLiferayProject( "sample-model-listener" );
        assertLiferayProject( "sample-theme" );
        assertSourceFolders( "sample-theme", "src" );
    }

    @Test
    public void testImportLiferayWorkspaceEE() throws Exception
    {
        LiferayGradleProject eeProject = Util.fullImportGradleProject( "projects/liferay-workspace-ee" );

        assertNotNull( eeProject );

        Util.waitForBuildAndValidation();

        assertNotLiferayProject("liferay-workspace-ee");
        assertNotLiferayProject("aws");
        assertNotLiferayProject("docker");
        assertNotLiferayProject("jenkins");
    }

    @Test
    public void testImportLiferayWorkspaceDontOverrideGradleProperties() throws Exception
    {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = ws.getRoot();

        File src = new File( "projects/testWorkspace" );
        File dst = new File( root.getLocation().toFile(), src.getName() );

        TestUtil.copyDir( src, dst );

        ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

        op.setWorkspaceLocation( dst.getAbsolutePath() );
        op.setProvisionLiferayBundle( true );

        op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        IProject eeProject = CoreUtil.getProject( "testWorkspace" );

        assertNotNull( eeProject );

        Util.waitForBuildAndValidation();

        assertNotLiferayProject("testWorkspace");
        assertLiferayProject( "sample-portlet" );

        File originalProperities = new File( "projects/testWorkspace/gradle.properties" );
        File importedProperties = eeProject.getFile( "gradle.properties" ).getLocation().toFile();

        String originalContent = CoreUtil.readStreamToString( Files.newInputStream( originalProperities.toPath() ) );
        String importedContent = CoreUtil.readStreamToString( Files.newInputStream( importedProperties.toPath() ) );

        originalContent = originalContent.replaceAll( "\r", "" );
        importedContent = importedContent.replaceAll( "\r", "" );

        assertEquals( importedContent, originalContent, importedContent );
    }

    @Test
    public void testImportLiferayWorkspaceInitBundle() throws Exception
    {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = ws.getRoot();

        File src = new File( "projects/testWorkspace" );
        File dst = new File( root.getLocation().toFile(), src.getName() );

        TestUtil.copyDir( src, dst );

        ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

        op.setWorkspaceLocation( dst.getAbsolutePath() );
        op.setProvisionLiferayBundle( true );

        String bundleUrl = op.getBundleUrl().content( true );

        assertEquals(
            "https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip",
            bundleUrl );

        final NullProgressMonitor monitor = new NullProgressMonitor();

        op.execute( ProgressMonitorBridge.create( monitor ) );

        IProject eeProject = CoreUtil.getProject( "testWorkspace" );

        assertNotNull( eeProject );

        Util.waitForBuildAndValidation();

        eeProject.refreshLocal( IResource.DEPTH_INFINITE, monitor );

        final IFolder bundlesFolder = eeProject.getFolder( "bundles" );

        assertTrue( bundlesFolder.exists() );
    }

    @Test
    public void testImportLiferayWorkspaceCustomBundleURL() throws Exception
    {
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = ws.getRoot();

        File src = new File( "projects/testWorkspaceCustomBundleUrl" );
        File dst = new File( root.getLocation().toFile(), src.getName() );

        TestUtil.copyDir( src, dst );

        ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

        op.setWorkspaceLocation( dst.getAbsolutePath() );

        String bundleUrl = op.getBundleUrl().content( true );

        assertEquals(
            "https://api.liferay.com/downloads/portal/7.0.10.6/liferay-dxp-digital-enterprise-tomcat-7.0-sp6-20171010144253003.zip",
            bundleUrl );
    }

    private void assertSourceFolders( String projectName, String expectedSourceFolderName )
    {
        IProject project = CoreUtil.getProject( projectName );

        assertTrue( "Project " + projectName + " doesn't exist.", project.exists() );

        ILiferayProject liferayProject = LiferayCore.create( project );
        IFolder[] srcFolders = liferayProject.getSourceFolders();

        assertEquals( expectedSourceFolderName, srcFolders[0].getName() );
    }

    private void assertLiferayProject( String projectName )
    {
        IProject project = CoreUtil.getProject( projectName );

        assertTrue( "Project " + projectName + " doesn't exist.", project.exists() );
        assertTrue( "Project " + projectName + " doesn't haven liferay nature", LiferayNature.hasNature( project ) );
    }

    private void assertNotLiferayProject( String projectName )
    {
        IProject project = CoreUtil.getProject( projectName );

        assertTrue( "Project " + projectName + " doesn't exist.", project.exists() );
        assertFalse( "Project " + projectName + " has a liferay nature", LiferayNature.hasNature( project ) );
    }

}
