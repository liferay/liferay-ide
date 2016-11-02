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
package com.liferay.ide.project.core.tests.modules;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class NewLiferayModuleProjectOpTests
{

    @Test
    public void testNewLiferayModuleProjectDefaultLocation() throws Exception
    {
        final URL wsZipUrl =
            Platform.getBundle( "com.liferay.ide.project.core.tests" ).getEntry( "projects/emptyLiferayWorkspace.zip" );

        final File wsZipFile = new File( FileLocator.toFileURL( wsZipUrl ).getFile() );

        File eclipseWorkspaceLocation = CoreUtil.getWorkspaceRoot().getLocation().toFile();

        ZipUtil.unzip( wsZipFile, eclipseWorkspaceLocation );

        File wsFolder = new File( eclipseWorkspaceLocation, "emptyLiferayWorkspace" );

        ProjectImportTestUtil.importExistingProject( wsFolder, new NullProgressMonitor() );

        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "mvc-portlet" );

        op.setProjectProvider( "maven-module" );

        // don't put maven type project inside liferay-workspace
        assertTrue( op.getLocation().content().toFile().equals( eclipseWorkspaceLocation ) );

        op.setProjectProvider( "gradle-module" );

        op.setProjectTemplateName( "theme" );

        // put gradle type theme project inside liferay-workspace/wars
        assertTrue( op.getLocation().content().toPortableString().contains( "emptyLiferayWorkspace/wars" ) );

        op.setProjectTemplateName( "mvc-portlet" );

        // put gradle type project inside liferay-workspace/modules
        assertTrue( op.getLocation().content().toPortableString().contains( "emptyLiferayWorkspace/modules" ) );

        IProject project = CoreUtil.getProject( "emptyLiferayWorkspace" );

        if( project != null && project.isAccessible() && project.exists() )
        {
            project.delete( true, true, new NullProgressMonitor() );
        }

        op.setProjectTemplateName( "service-builder" );

        // no liferay-workspace
        assertTrue( op.getLocation().content().toFile().equals( eclipseWorkspaceLocation ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceDashes() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceDots() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectName() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectName( "my_abc-test" );

        assertEquals( "MyAbcTest", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectTemplateName() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "activator" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "mvc-portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "service" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "service-wrapper" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );

        op.setProjectTemplateName( "service-builder" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceUnderscores() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my_test_project" );

        op.setProjectTemplateName( "portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectPackageDefaultValueService() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "my.test.project", op.getPackageName().content( true ) );

        op.setProjectName( "my.test.foo" );

        assertEquals( "my.test.foo", op.getPackageName().content( true ) );

        op.setProjectName( "my_test_foo1" );

        op.setProjectTemplateName( "ServiceWrapper" );

        assertEquals( "my.test.foo1", op.getPackageName().content( true ) );
    }


    @Test
    public void testNewLiferayModuleProjectNewProperties() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "test-properties-in-portlet" );

        op.setProjectTemplateName( "portlet" );
        op.setComponentName( "Test" );

        PropertyKey pk = op.getPropertyKeys().insert();

        pk.setName( "property-test-key" );
        pk.setValue( "property-test-value" );

        Status exStatus =
            NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertEquals( "OK", exStatus.message() );

        IProject modPorject = CoreUtil.getProject( op.getProjectName().content() );
        modPorject.open( new NullProgressMonitor() );

        SearchFilesVisitor sv = new SearchFilesVisitor();
        List<IFile> searchFiles = sv.searchFiles( modPorject, "TestPortlet.java" );
        IFile componentClassFile = searchFiles.get( 0 );

        assertEquals( componentClassFile.exists(), true );

        String actual = CoreUtil.readStreamToString( componentClassFile.getContents() );

        String expected =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream( "files/TestPortlet.java.txt" ) );

        assertEquals( expected, actual );
    }

    @Test
    public void testNewLiferayPortletProviderNewProperties() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "test-properties-in-portlet-provider" );
        op.setProjectTemplateName( "portlet-provider" );

        PropertyKey pk = op.getPropertyKeys().insert();

        pk.setName( "property-test-key" );
        pk.setValue( "property-test-value" );

        Status exStatus =
            NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertEquals( "OK", exStatus.message() );

        IProject modPorject = CoreUtil.getProject( op.getProjectName().content() );
        modPorject.open( new NullProgressMonitor() );

        SearchFilesVisitor sv = new SearchFilesVisitor();
        List<IFile> searchFiles = sv.searchFiles( modPorject, "TestPropertiesInPortletProviderAddPortletProvider.java" );
        IFile componentClassFile = searchFiles.get( 0 );

        assertEquals( componentClassFile.exists(), true );

        String actual = CoreUtil.readStreamToString( componentClassFile.getContents() );

        String expected =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream( "files/TestPortletProvider.txt" ) );

        assertEquals( expected, actual );
    }
}
