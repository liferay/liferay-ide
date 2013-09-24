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

import com.liferay.ide.adt.core.ADTCore;
import com.liferay.ide.adt.core.ADTUtil;
import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;
import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ADTCoreTests extends BaseTests
{

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

//    protected void createLiferayAndroidProjectExample( boolean createExample )
//    {
//        final String projectName = "test-liferay-android-project-example-" + createExample;
//
//        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
//        op.setProjectName( projectName );
//        op.setIncludeExample( createExample );
//
//        final IProject newProject = createLiferayAndroidProject( op );
//
//        final IFile exampleFile = newProject.getFile( "src/com/liferay/mobile/sample/task/UserSitesAsyncTask.java" );
//
//        assertEquals( createExample, exampleFile.exists() );
//    }

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

        assertEquals( stripCarriageReturns( androidManifestContent ), stripCarriageReturns( expectedAndroidManifestContent ) );
    }

    private String stripCarriageReturns( String value )
    {
        return value.replaceAll( "\r", "" );
    }

    @Test
    public void testGetProjectTemplateLocation() throws Exception
    {
        final File projectTemplateFile = ADTCore.getProjectTemplateFile();

        assertNotNull( projectTemplateFile );

        assertEquals( true, projectTemplateFile.exists() );

        final String topLevelDir = ZipUtil.getFirstZipEntryName( projectTemplateFile );

        assertNotNull( topLevelDir );
    }

    @Test
    public void testSetCustomProjectTemplateLocation() throws Exception
    {
        final File defaultTemplateFile = ADTCore.getProjectTemplateFile();

        final IEclipsePreferences adtCorePrefs = getADTCorePrefs();

        final File customTemplateFile = getCustomTemplateFile();

        adtCorePrefs.put( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, customTemplateFile.getCanonicalPath() );

        final File projectTemplateFile = ADTCore.getProjectTemplateFile();

        assertEquals( customTemplateFile, projectTemplateFile );

        adtCorePrefs.remove( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION );

        final File newDefaultTemplateFile = ADTCore.getProjectTemplateFile();

        assertEquals( defaultTemplateFile, newDefaultTemplateFile );
    }

    private IEclipsePreferences getADTCorePrefs()
    {
        return InstanceScope.INSTANCE.getNode( ADTCore.PLUGIN_ID );
    }

    private File getCustomTemplateFile() throws IOException
    {
        return new File(
            FileLocator.toFileURL(
                Activator.getDefault().getBundle().getEntry(
                    "templates/liferay-mobile-sdk-sample-android-custom.zip" ) ).getFile() );
    }

    @Test
    public void testCreateLiferayAndroidProjectCustomTemplate() throws Exception
    {
        getADTCorePrefs().put( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, getCustomTemplateFile().getCanonicalPath() );

        final String projectName = "test-liferay-android-project-sdk-custom-template";

        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        final IProject newLiferayAndroidProject = createLiferayAndroidProject( op );

        assertNotNull( newLiferayAndroidProject );

        final IFile customFile = newLiferayAndroidProject.getFile( "custom-file.txt" );

        assertEquals( true, customFile.exists() );
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

//    @Test
//    public void testCreateLiferayAndroidProjectExample() throws Exception
//    {
//        createLiferayAndroidProjectExample( true );
//        createLiferayAndroidProjectExample( false );
//    }

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
        createLiferayAndroidProjectSDK( "API 16: Android 4.1 (Jelly Bean)"/*16*/ );
        createLiferayAndroidProjectSDK( "API 17: Android 4.2 (Jelly Bean)"/*17*/ );
        createLiferayAndroidProjectSDK( "API 18: Android 4.3"/*18*/ );
    }

}
