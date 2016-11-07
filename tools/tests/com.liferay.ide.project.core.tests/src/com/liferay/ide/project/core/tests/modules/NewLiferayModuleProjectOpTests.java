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
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.modules.PropertyKey;
import com.liferay.ide.project.core.util.SearchFilesVisitor;

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class NewLiferayModuleProjectOpTests
{

    @BeforeClass
    public static void setupBladeCLIPrefs() throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, "https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/lastSuccessfulBuild/artifact/build/generated/p2/" );
    }

    @AfterClass
    public static void restoreBladeCLIPrefsToDefault() throws Exception
    {
        IEclipsePreferences defaults = DefaultScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        final String defaultValue = defaults.get( BladeCLI.BLADE_CLI_REPO_URL, "" );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, defaultValue );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceDashes() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceDots() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "Portlet" );

        assertEquals( "MyTestProject", op.getComponentName().content( true ) );
    }

    @Test
    public void testNewLiferayModuleProjectDefaultValueServiceIsListeningToProjectName() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setProjectTemplateName( "Portlet" );

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

        op.setProjectTemplateName( "Portlet" );

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

}