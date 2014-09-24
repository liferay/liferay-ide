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

package com.liferay.ide.adt.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.adt.core.ADTCore;
import com.liferay.ide.adt.core.ADTUtil;
import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;
import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.mobile.sdk.core.MobileSDKCore;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
public class ADTCoreTests extends BaseTests
{
    public static File getProjectFile( Bundle bundle, String file ) throws IOException
    {
        return new File( FileLocator.toFileURL( bundle.getEntry( "projects/" + file ) ).getFile() );
    }

    public static IProject importAndroidProject( String projectName, String fileName ) throws Exception
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

    final String projectName = "SampleAndroidApp";

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

    protected IProject createLiferayAndroidProject( NewLiferayAndroidProjectOp op )
    {
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );

        assertEquals( Status.createOkStatus().message(), status.message() );

        final IProject newLiferayAndroidProject = project( op.getProjectName().content() );

        assertNotNull( newLiferayAndroidProject );

        assertEquals( true, newLiferayAndroidProject.exists() );

        return newLiferayAndroidProject;
    }

    protected IProject createLiferayAndroidProjectName( final String projectName ) throws Exception
    {
        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        return createLiferayAndroidProject( op );
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

        assertTrue( androidManifestContent.contains( "android:targetSdkVersion=\"" + sdkLevel + "\"" ) );

        final IFile projectProperties = newProject.getFile( "project.properties" );

        final String projectPropertiesContent = CoreUtil.readStreamToString( projectProperties.getContents() );

        final String expectedProjectPropertiesContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/project-" + sdkLevel + ".properties" ) );

        assertEquals(
            stripCarriageReturns( expectedProjectPropertiesContent ),
            stripCarriageReturns( projectPropertiesContent ) );
    }

    @Before
    public void deleteSampleAndroidProject() throws Exception
    {
        deleteSampleProject( projectName );
    }

    public static void deleteSampleProject( String name ) throws CoreException
    {
        final IProject sampleProject = project( name );

        if( sampleProject.exists() )
        {
            sampleProject.delete( true, new NullProgressMonitor() );
        }
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
                    "projects/liferay-mobile-sdk-sample-android-custom.zip" ) ).getFile() );
    }

    private IEclipsePreferences getDefaultADTCorePrefs()
    {
        return DefaultScope.INSTANCE.getNode( ADTCore.PLUGIN_ID );
    }


    private void restoreDefaults( final IEclipsePreferences prefs )
    {
        prefs.remove( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION );
        prefs.remove( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION );
    }

    @Test
    public void testAddLiferayAndroidSDKLibs() throws Exception
    {

        final String propertiesFileName = "libs/liferay-android-sdk-6.2.0.1.jar.properties";

        final IProject sampleProject = ADTCoreTests.importAndroidProject( projectName, projectName + ".zip" );

        final Map<String, File[]> libmap = MobileSDKCore.getLibraryMap();

        final File[] libs = libmap.get( "liferay-android-sdk-6.2.0.1" );

        ADTUtil.addLibsToAndroidProject( sampleProject, Collections.singletonList( libs ), new NullProgressMonitor() );

        assertTrue( sampleProject.getFile( "libs/liferay-android-sdk-6.2.0.1.jar" ).exists() );

        assertTrue( sampleProject.getFile( propertiesFileName ).exists() );

        assertTrue( sampleProject.getFile( "libs/src/liferay-android-sdk-6.2.0.1-sources.jar" ).exists() );

        final String propertiesContent =
            CoreUtil.readStreamToString( sampleProject.getFile( propertiesFileName ).getContents(
                true ) );

        assertEquals(
            stripCarriageReturns( "src=src/liferay-android-sdk-6.2.0.1-sources.jar" ),
            stripCarriageReturns( propertiesContent ) );
    }

    @Test
    public void testAddLiferayAndroidSDKLibsRunTwice() throws Exception
    {
        final String projectName = "SampleAndroidApp";

        final IProject sampleProject = ADTCoreTests.importAndroidProject( projectName, projectName + ".zip" );

        final Map<String, File[]> libmap = MobileSDKCore.getLibraryMap();

        final File[] libs = libmap.get( "liferay-android-sdk-6.2.0.1" );

        ADTUtil.addLibsToAndroidProject( sampleProject, Collections.singletonList( libs ), new NullProgressMonitor() );

        ADTUtil.addLibsToAndroidProject( sampleProject, Collections.singletonList( libs ), new NullProgressMonitor() );
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
    public void testCreateLiferayAndroidProjectCustomTemplate() throws Exception
    {
        final IEclipsePreferences prefs = getADTCorePrefs();

        prefs.put( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION, ADTCore.VALUE_USE_CUSTOM_TEMPLATE );
        prefs.put( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, getCustomTemplateFile().getCanonicalPath() );

        final String projectName = "test-liferay-android-project-sdk-custom-template";

        final NewLiferayAndroidProjectOp op = NewLiferayAndroidProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        final IProject newLiferayAndroidProject = createLiferayAndroidProject( op );

        assertNotNull( newLiferayAndroidProject );

        final IFile customFile = newLiferayAndroidProject.getFile( "custom-file.txt" );

        assertEquals( true, customFile.exists() );
    }

//    @Test
//    public void testCreateLiferayAndroidProjectExample() throws Exception
//    {
//        createLiferayAndroidProjectExample( true );
//        createLiferayAndroidProjectExample( false );
//    }

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
    public void testCreateLiferayAndroidProjectMetadata() throws Exception
    {
        final String n = "foo";
        final IFile projectFile = createLiferayAndroidProjectName( n ).getFile( ".project" );

        assertTrue( CoreUtil.readStreamToString( projectFile.getContents() ).contains( n ) );
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
        createLiferayAndroidProjectSDK( "API 16: Android 4.1 (Jelly Bean)" /*16*/ );
        createLiferayAndroidProjectSDK( "API 17: Android 4.2 (Jelly Bean)" /*17*/ );
        createLiferayAndroidProjectSDK( "API 18: Android 4.3 (Jelly Bean)" /*18*/ );
        createLiferayAndroidProjectSDK( "API 19: Android 4.4 (KitKat)" /*19*/ );
    }

    @Test
    public void testGetDefaultProjectTemplateLocationPrefs() throws Exception
    {
        final IEclipsePreferences defaults = getDefaultADTCorePrefs();
        final IEclipsePreferences prefs = getADTCorePrefs();

        restoreDefaults( prefs );

        assertEquals( ADTCore.VALUE_USE_EMBEDDED_TEMPLATE, prefs.get( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION, defaults.get( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION, null ) ) );
        assertEquals( null, prefs.get( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, defaults.get( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, null ) ) );

        assertEquals( new File( ADTCore.getEmbeddedProjectTemplateLocation() ), ADTCore.getProjectTemplateFile() );

        prefs.put( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, getCustomTemplateFile().getAbsolutePath() );

        assertEquals( new File( ADTCore.getEmbeddedProjectTemplateLocation() ), ADTCore.getProjectTemplateFile() );

        prefs.put( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION, ADTCore.VALUE_USE_CUSTOM_TEMPLATE );

        assertEquals( getCustomTemplateFile(), ADTCore.getProjectTemplateFile() );

        restoreDefaults( prefs );

        assertEquals( new File( ADTCore.getEmbeddedProjectTemplateLocation() ), ADTCore.getProjectTemplateFile() );
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

        adtCorePrefs.put( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION_OPTION, ADTCore.VALUE_USE_CUSTOM_TEMPLATE );
        adtCorePrefs.put( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION, customTemplateFile.getCanonicalPath() );

        final File projectTemplateFile = ADTCore.getProjectTemplateFile();

        assertEquals( customTemplateFile, projectTemplateFile );

        adtCorePrefs.remove( ADTCore.PREF_PROJECT_TEMPLATE_LOCATION );

        final File newDefaultTemplateFile = ADTCore.getProjectTemplateFile();

        assertEquals( defaultTemplateFile, newDefaultTemplateFile );

        restoreDefaults( adtCorePrefs );
    }

}
