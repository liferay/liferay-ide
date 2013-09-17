/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.adt.core.ADTUtil;
import com.liferay.ide.adt.core.model.LiferayApi;
import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;
import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ADTCoreTests extends BaseTests
{

    private IProject a;

    @After
    public void cleanup() throws Exception
    {
        deleteProject( "a" );
    }

    protected IProject createLiferayAndroidProject( NewLiferayAndroidProjectOp op )
    {
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );

        assertEquals( true, status.ok() );

        final IProject newLiferayAndroidProject = project( op.getProjectName().content() );

        assertNotNull( newLiferayAndroidProject );

        assertEquals( true, newLiferayAndroidProject.exists() );

        return newLiferayAndroidProject;
    }

    protected void createLiferayAndroidProjectAPI( String[] requiredApis )
    {
        final StringBuffer sb = new StringBuffer();

        for( String api : requiredApis )
        {
            sb.append( api + "-" );
        }

        final String projectName = "test-liferay-android-project-api-" + sb.toString();

        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        for( String api : requiredApis )
        {
            LiferayApi liferayApi = LiferayApi.TYPE.instantiate();
            liferayApi.setName( api );

            op.getLiferayApis().add( liferayApi );
        }

        final IProject newLiferayAndroidProject = createLiferayAndroidProject( op );

        for( final String api : requiredApis )
        {
            final IFile apiFile = newLiferayAndroidProject.getFile( "libs/" + api + "-service.jar" );

            assertEquals( "Checking for api file: " + api, true, apiFile.exists() );
        }

        //TODO need to ensure no other jars exist that user didn't specify
    }

    protected void createLiferayAndroidProjectExample( boolean createExample )
    {
        final String projectName = "test-liferay-android-project-example-" + createExample;

        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setIncludeExample( createExample );

        final IProject newProject = createLiferayAndroidProject( op );

        final IFile exampleFile = newProject.getFile( "src/com/liferay/mobile/sample/task/UserSitesAsyncTask.java" );

        assertEquals( createExample, exampleFile.exists() );
    }

    protected void createLiferayAndroidProjectName( final String projectName ) throws Exception
    {
        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        createLiferayAndroidProject( op );
    }

    protected void createLiferayAndroidProjectSDK( final String targetSdk ) throws Exception
    {
        final int sdkLevel = ADTUtil.extractSdkLevel( targetSdk );
        final String projectName = "test-liferay-android-project-sdk-" + sdkLevel;

        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setTargetSDK( targetSdk );

        final IProject newProject = createLiferayAndroidProject( op );

        final IFile androidManifest = newProject.getFile( "AndroidManifest.xml" );

        final String androidManifestContent = CoreUtil.readStreamToString( androidManifest.getContents() );

        final String expectedAndroidManifestContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/AndroidManifest-targetSDK-" + sdkLevel + ".xml" ) );

        assertEquals( androidManifestContent, expectedAndroidManifestContent );
    }

    @Before
    public void createTestProject() throws Exception
    {
        this.a = createProject( "a" );
    }

    protected IProject getCurrentProject()
    {
        return this.a;
    }

    @Test
    public void testCreateLiferayAndroidProjectAPIs() throws Exception
    {
        final String[] noapis = {};
        createLiferayAndroidProjectAPI( noapis );

        final String[] blogs = { "blogs" };
        createLiferayAndroidProjectAPI( blogs );

        final String[] blogsAndUsers = { "blogs", "users" };
        createLiferayAndroidProjectAPI( blogsAndUsers );
    }

    @Test
    public void testCreateLiferayAndroidProjectCustomLocation() throws Exception
    {
        final String suffix = System.currentTimeMillis() + "";
        final String projectName = "test-liferay-android-project-custom-location" + suffix;

        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setUseDefaultLocation( false );

        final File tmpLocation = new File( File.createTempFile( suffix, null ).getParentFile(), projectName );

        final IPath projectPath = new Path( tmpLocation.getCanonicalPath() );

        op.setLocation( PathBridge.create( projectPath ) );

        final IProject newLiferyAndroidProject = createLiferayAndroidProject( op );

        assertEquals( projectPath.toFile(), new File( newLiferyAndroidProject.getLocationURI() ) );
    }

    @Test
    public void testCreateLiferayAndroidProjectDefaultLocation() throws Exception
    {
        final String projectName = "test-liferay-android-project-default-location";

        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        final IProject newProject = createLiferayAndroidProject( op );

        final IPath workspaceLocation = workspaceRoot().getLocation();

        final IPath expectedProjectLocation = workspaceLocation.append( projectName );

        assertEquals( expectedProjectLocation, newProject.getLocation() );
    }

    @Test
    public void testCreateLiferayAndroidProjectExample() throws Exception
    {
        createLiferayAndroidProjectExample( true );
        createLiferayAndroidProjectExample( false );
    }

    @Test
    public void testCreateLiferayAndroidProjectNames() throws Exception
    {
        createLiferayAndroidProjectName( "test-name-1" );
        createLiferayAndroidProjectName( "Test With Spaces" );
        createLiferayAndroidProjectName( "test_name_1" );
    }

    @Test
    public void testCreateLiferayAndroidProjectTargetSDKs() throws Exception
    {
        createLiferayAndroidProjectSDK( "API 17: Android 4.2.2"/*17*/ );
        createLiferayAndroidProjectSDK( "API 18: Android 4.3"/*18*/ );
    }

}
