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
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.NewLiferayComponentOpMethods;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOpMethods;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class NewLiferayComponentOpTests extends BaseTests
{
	@AfterClass
	public static void removeWorkspaceProjects() throws Exception {
		IProject workspaceProject = CoreUtil.getProject( "test-liferay-workspace" );

		workspaceProject.delete(true, null);
	}

    @Test
    public void testNewLiferayComponentDefaultValueServiceDashes() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my-test-project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );
    }

    @Test
    public void testNewLiferayComponentDefaultValueServiceUnderscores() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my_test_project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );
    }

    @Test
    public void testNewLiferayComponentDefaultValueServiceDots() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );
    }

    @Test
    public void testNewLiferayComponentDefaultValueServiceIsListeningToProjectName() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );

        op.setProjectName( "my_abc-test" );

        assertEquals( "MyAbcTestPortletActionCommand", op.getComponentClassName().content( true ) );
    }

    @Test
    public void testNewLiferayComponentDefaultValueServiceIsListeningToComponentClassTemplateName() throws Exception
    {
        NewLiferayComponentOp op = NewLiferayComponentOp.TYPE.instantiate();

        op.setProjectName( "my.test.project" );

        op.setComponentClassTemplateName( "PortletActionCommand" );

        assertEquals( "MyTestProjectPortletActionCommand", op.getComponentClassName().content( true ) );

        op.setComponentClassTemplateName( "FriendlyUrlMapper" );

        assertEquals( "MyTestProjectFriendlyUrlMapper", op.getComponentClassName().content( true ) );
    }


    @BeforeClass
    public static void setupBladeCLIRepoUrl() throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, "https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/lastSuccessfulBuild/artifact/build/generated/p2/" );

        prefs.flush();

        NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

        op.setWorkspaceName( "test-liferay-workspace" );
        op.setUseDefaultLocation( true );

        if( op.validation().ok() )
        {
            NewLiferayWorkspaceOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        }

        TestUtil.waitForBuildAndValidation();
    }

    @AfterClass
    public static void deleteLiferayWorkspace() throws Exception {
        deleteAllWorkspaceProjects();
    }

    @Test
    public void testNewLiferayComponentBndAndGradleForPortleActionCommandAndRest() throws Exception
    {
        NewLiferayModuleProjectOp pop = NewLiferayModuleProjectOp.TYPE.instantiate();

        pop.setProjectName( "testGradleModuleComponentBnd" );
        pop.setProjectTemplateName( "mvc-portlet" );
        pop.setProjectProvider( "gradle-module" );

        Status modulePorjectStatus = NewLiferayModuleProjectOpMethods.execute( pop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );
        assertTrue( modulePorjectStatus.ok() );

        TestUtil.waitForBuildAndValidation();

        IProject modPorject = CoreUtil.getProject( pop.getProjectName().content() );
        modPorject.open( new NullProgressMonitor() );

        NewLiferayComponentOp cop = NewLiferayComponentOp.TYPE.instantiate();
        cop.setProjectName( pop.getProjectName().content() );
        cop.setComponentClassTemplateName( "PortletActionCommand" );

        NewLiferayComponentOpMethods.execute( cop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        IFile bgd = modPorject.getFile( "bnd.bnd" );
        String bndcontent = FileUtil.readContents( bgd.getLocation().toFile(), true );

        String bndConfig = "-includeresource: \\" + System.getProperty( "line.separator" ) +
                        "\t" + "@com.liferay.util.bridges-2.0.0.jar!/com/liferay/util/bridges/freemarker/FreeMarkerPortlet.class,\\" + System.getProperty( "line.separator" ) +
                        "\t" + "@com.liferay.util.taglib-2.0.0.jar!/META-INF/*.tld" + System.getProperty( "line.separator" );

        assertTrue( bndcontent.contains( bndConfig ) );

        IFile buildgrade = modPorject.getFile( "build.gradle" );
        String buildgradeContent = FileUtil.readContents( buildgrade.getLocation().toFile(),true );
        assertTrue( buildgradeContent.contains( "compileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.bridges\", version: \"2.0.0\"" ) );
        assertTrue( buildgradeContent.contains( "compileOnly group: \"org.osgi\", name: \"org.osgi.service.component.annotations\", version: \"1.3.0\"" ) );

        NewLiferayComponentOp copRest = NewLiferayComponentOp.TYPE.instantiate();
        copRest.setProjectName( pop.getProjectName().content() );
        copRest.setComponentClassTemplateName( "RestService" );

        NewLiferayComponentOpMethods.execute( copRest, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        bgd = modPorject.getFile( "bnd.bnd" );
        bndcontent = FileUtil.readContents( bgd.getLocation().toFile(), true );
        assertTrue( bndcontent.contains( bndConfig ) );

        final String restConfig = "Require-Capability: osgi.contract; filter:=\"(&(osgi.contract=JavaJAXRS)(version=2))\"";
        assertTrue( bndcontent.contains( restConfig ) );

        buildgrade = modPorject.getFile( "build.gradle" );
        buildgradeContent = FileUtil.readContents( buildgrade.getLocation().toFile(),true );
        assertTrue( buildgradeContent.contains( "compileOnly group: \"javax.ws.rs\", name: \"javax.ws.rs-api\", version: \"2.0.1\"" ) );

        NewLiferayComponentOp copAuth = NewLiferayComponentOp.TYPE.instantiate();
        copAuth.setProjectName( pop.getProjectName().content() );
        copAuth.setComponentClassTemplateName( "Authenticator" );

        NewLiferayComponentOpMethods.execute( copAuth, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        bgd = modPorject.getFile( "bnd.bnd" );
        bndcontent = FileUtil.readContents( bgd.getLocation().toFile(), true );

        bndConfig = "-includeresource: \\" + System.getProperty( "line.separator" ) +
                        "\t" + "@com.liferay.util.bridges-2.0.0.jar!/com/liferay/util/bridges/freemarker/FreeMarkerPortlet.class,\\" + System.getProperty( "line.separator" ) +
                        "\t" + "@com.liferay.util.taglib-2.0.0.jar!/META-INF/*.tld,\\" + System.getProperty( "line.separator" ) +
                        "\t" + "@shiro-core-1.1.0.jar";

        assertTrue( bndcontent.contains( bndConfig ) );

        buildgrade = modPorject.getFile( "build.gradle" );
        buildgradeContent = FileUtil.readContents( buildgrade.getLocation().toFile() ,true);
        assertTrue( buildgradeContent.contains( "compileOnly group: \"org.apache.shiro\", name: \"shiro-core\", version: \"1.1.0\"" ) );

        NewLiferayComponentOp copStruts = NewLiferayComponentOp.TYPE.instantiate();
        copStruts.setProjectName( pop.getProjectName().content() );
        copStruts.setComponentClassTemplateName( "StrutsAction" );

        NewLiferayComponentOpMethods.execute( copStruts, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        bgd = modPorject.getFile( "bnd.bnd" );
        bndcontent = FileUtil.readContents( bgd.getLocation().toFile(), true );

        bndConfig = "Web-ContextPath: /TestgradlemodulecomponentbndStrutsAction";

        assertTrue( bndcontent.contains( bndConfig ) );
    }

    @Test
    public void testNewLiferayComponentActionCommandPortlet()  throws Exception{
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "action-command-test" );
        op.setProjectTemplateName( "api" );
        op.setProjectProvider( "gradle-module" );

        Status modulePorjectStatus = NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertTrue( modulePorjectStatus.ok() );

        TestUtil.waitForBuildAndValidation();

        IProject modProject = CoreUtil.getProject( op.getProjectName().content() );

        modProject.open( new NullProgressMonitor() );

        NewLiferayComponentOp cop = NewLiferayComponentOp.TYPE.instantiate();

        cop.setProjectName( op.getProjectName().content() );
        cop.setComponentClassTemplateName( "PortletActionCommand" );

        Status status = NewLiferayComponentOpMethods.execute( cop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertEquals( Status.createOkStatus(),status );

        IFile javaFile = modProject.getFile( "/src/main/java/action/command/test/api/ActionCommandTestActionCommand.java" );

        assertTrue(javaFile.exists());

        String javaFileContent = FileUtil.readContents( javaFile.getLocation().toFile(), true );

        assertTrue( javaFileContent.contains( "javax.portlet.name=action_command_test_api_ActionCommandTestPortlet" ) );
        assertTrue( javaFileContent.contains( "mvc.command.name=greet" ) );

        IFile initFile = modProject.getFile( "/src/main/resources/META-INF/resources/actioncommandtestportletactioncommand/init.ftl" );

        assertTrue( initFile.exists() );

        IFile viewFile = modProject.getFile( "/src/main/resources/META-INF/resources/actioncommandtestportletactioncommand/view.ftl" );

        assertTrue( viewFile.exists() );
    }

    @Test
    public void testNewLiferayComponentStrutsAction() throws Exception{
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "testGradleStrutsActionComponent" );
        op.setProjectTemplateName( "mvc-portlet" );
        op.setProjectProvider( "gradle-module" );

        Status modulePorjectStatus = NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertTrue( modulePorjectStatus.ok() );

        TestUtil.waitForBuildAndValidation();

        IProject modProject = CoreUtil.getProject( op.getProjectName().content() );

        modProject.open( new NullProgressMonitor() );

        NewLiferayComponentOp cop = NewLiferayComponentOp.TYPE.instantiate();

        cop.setProjectName( op.getProjectName().content() );
        cop.setComponentClassTemplateName( "StrutsAction" );

        Status status = NewLiferayComponentOpMethods.execute( cop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertEquals( Status.createOkStatus(),status );

        IFile javaFile = modProject.getFile( "/src/main/java/" +
            "/content/TestgradlestrutsactioncomponentStrutsAction.java" );

        assertTrue(javaFile.exists());

        String javaFileContent = FileUtil.readContents( javaFile.getLocation().toFile(), true );

        assertTrue(javaFileContent.contains( "osgi.web.symbolicname=testGradleStrutsActionComponent" ) );

        IFile jspFile = modProject.getFile( "/src/main/resources/META-INF/resources" +
            "/testgradlestrutsactioncomponentstrutsaction/html/portal/testgradlestrutsactioncomponent.jsp" );

        assertTrue( jspFile.exists() );

        String jspFileContent = FileUtil.readContents( jspFile.getLocation().toFile(), true );

        assertTrue( jspFileContent.contains("/testgradlestrutsactioncomponentstrutsaction/html/init.jsp" ) );
    }

    @Test
    public void testNewLiferayComponentPortletFilter() throws Exception {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "testGradlePortletFilterComponent" );
        op.setProjectTemplateName( "api" );
        op.setProjectProvider( "gradle-module" );

        Status modulePorjectStatus = NewLiferayModuleProjectOpMethods.execute( op, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertTrue( modulePorjectStatus.ok() );

        TestUtil.waitForBuildAndValidation();

        IProject modProject = CoreUtil.getProject( op.getProjectName().content() );

        modProject.open( new NullProgressMonitor() );

        NewLiferayComponentOp cop = NewLiferayComponentOp.TYPE.instantiate();

        cop.setProjectName( op.getProjectName().content() );
        cop.setComponentClassTemplateName( "PortletFilter" );

        Status status = NewLiferayComponentOpMethods.execute( cop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertEquals( Status.createOkStatus(),status );

        IFile gradleFile = modProject.getFile( "build.gradle" );

        String gradleFileContent = FileUtil.readContents( gradleFile.getLocation().toFile() ,true );

        assertTrue( gradleFileContent.contains( "compileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"" ) );
    }

    @Test
    public void testNewLiferayComponentProjectValidation() throws Exception
    {
        NewLiferayComponentOp cop = NewLiferayComponentOp.TYPE.instantiate();

        Status projectValidationStatus = cop.getProjectName().validation();

        assertEquals( "No suitable Liferay module project", projectValidationStatus.message() );

        NewLiferayModuleProjectOp pop = NewLiferayModuleProjectOp.TYPE.instantiate();

        pop.setProjectName( "testProjectValidation" );
        pop.setProjectTemplateName( "mvc-portlet" );
        pop.setProjectProvider( "gradle-module" );

        Status moduleProjectStatus = NewLiferayModuleProjectOpMethods.execute( pop, ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertTrue( moduleProjectStatus.ok() );

        TestUtil.waitForBuildAndValidation();

        IProject modProject = CoreUtil.getProject( pop.getProjectName().content() );
        modProject.open( new NullProgressMonitor() );

        IFile bndFile = modProject.getFile( "bnd.bnd" );

        bndFile.delete( true, true, new NullProgressMonitor() );

        cop.setProjectName( pop.getProjectName().content() );

        projectValidationStatus = cop.getProjectName().validation();

        assertEquals( "Can not find bnd.bnd file in the project", projectValidationStatus.message() );
    }
}
