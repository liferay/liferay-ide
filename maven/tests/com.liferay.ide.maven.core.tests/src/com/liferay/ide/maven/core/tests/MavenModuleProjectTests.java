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

package com.liferay.ide.maven.core.tests;

import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.PropertyKey;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class MavenModuleProjectTests extends AbstractMavenProjectTestCase
{

    @Test
    public void testProjectTemplateActivator() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "activator-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "activator" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateApi() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "api-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "api" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateContentTargetingReport() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "content-targeting-report-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "content-targeting-report" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateContentTargetingRule() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "content-targeting-rule-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "content-targeting-rule" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateContentTargetingTrackingAction() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "content-targeting-tracking-action-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "content-targeting-tracking-action" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateContrlMenuEntry() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "control-menu-entry-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "control-menu-entry" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateFragment() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "fragment-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "fragment" );

        PropertyKey hostBundleSymbolicName = op.getPropertyKeys().insert();
        hostBundleSymbolicName.setName( "hostBundleSymbolicName" );
        hostBundleSymbolicName.setValue( "com.liferay.login.web" );

        PropertyKey hostBundleVersion = op.getPropertyKeys().insert();
        hostBundleVersion.setName( "hostBundleVersion" );
        hostBundleVersion.setValue( "1.0.0" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateMvcPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "mvc-portlet-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "mvc-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePanelApp() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "panel-app-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "panel-app" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "portlet-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePortletConfigurationIcon() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "portlet-configuration-icon-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "portlet-configuration-icon" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePortletProvider() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "portlet-provider-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "portlet-provider" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePortletToolbarContributor() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "portlet-toolbar-contributor-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "portlet-toolbar-contributor" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateService() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "service-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "service" );
        op.setServiceName( "com.liferay.portal.kernel.events.LifecycleAction" );
        op.setComponentName( "MyService" );

        IProject project = create( op );

        IFile serviceFile = project.getFile( "src/main/java/service/test/MyService.java" );

        String contents =
                        "package service.test;\n" +
                        "import com.liferay.portal.kernel.events.ActionException;\n" +
                        "import com.liferay.portal.kernel.events.LifecycleAction;\n" +
                        "import com.liferay.portal.kernel.events.LifecycleEvent;\n" +
                        "import org.osgi.service.component.annotations.Component;\n" +
                        "@Component(\n" +
                            "immediate = true, property = {\"key=login.events.pre\"},\n" +
                            "service = LifecycleAction.class\n" +
                        ")\n" +
                        "public class MyService implements LifecycleAction {\n" +
                            "@Override public void processLifecycleEvent(LifecycleEvent lifecycleEvent) throws ActionException { }\n" +
                        "}" ;
        serviceFile.setContents( new ByteArrayInputStream( contents.getBytes() ), IResource.FORCE, monitor );

        verifyProject( project );
    }

    @Test
    public void testProjectTemplateServiceBuilder() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "service-builder-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "service-builder" );
        op.setPackageName( "com.liferay.test" );

        PropertyKey apiPath = op.getPropertyKeys().insert();

        apiPath.setName( "apiPath" );
        apiPath.setValue( "foo" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateServiceWrapper() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "service-wrapper-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "service-wrapper" );
        op.setServiceName( "com.liferay.portal.kernel.service.UserLocalServiceWrapper" );
        op.setComponentName( "MyServiceWrapper" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateSimulationPanelEntry() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "simulation-panel-entry-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "simulation-panel-entry" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateTemplateContextContributor() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "template-context-contributor-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "template-context-contributor" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateTheme() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "theme-test" );
        op.setProjectProvider( "maven-module" );
        op.setProjectTemplateName( "theme" );

        createAndBuild(op);
    }

    private void createAndBuild( NewLiferayModuleProjectOp op ) throws Exception
    {
        IProject project = create( op );

        verifyProject(project);
    }

    private IProject create( NewLiferayModuleProjectOp op ) throws InterruptedException, CoreException
    {
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );
        assertTrue( status.message(), status.ok() );

        waitForJobsToComplete();

        return CoreUtil.getProject( op.getProjectName().content() );
    }

    @Test
    public void testNewLiferayMavenModuleMVCPortletProject() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "foo" );
        op.setProjectProvider( "maven-module" );

        IProject project = create( op );

        verifyProject(project);

        assertTrue(project.getFile( "src/main/java/foo/portlet/FooPortlet.java" ).exists());
    }

    @Test
    public void testNewLiferayMavenModuleMVCPortletProjectWithDashes() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "foo-bar" );
        op.setProjectProvider( "maven-module" );

        IProject project = create( op );

        verifyProject(project);

        assertTrue(project.getFile( "src/main/java/foo/bar/portlet/FooBarPortlet.java" ).exists());
    }

    @Test
    public void testNewLiferayMavenModuleMVCPortletProjectWithDots() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "foo.bar" );
        op.setProjectProvider( "maven-module" );

        IProject project = create( op );

        verifyProject(project);
    }

    @Test
    public void testThemeProjectPluginDetection() throws Exception
    {
       NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

       op.setProjectName( "maven-theme-test" );
       op.setProjectProvider( "maven-module" );
       op.setProjectTemplateName( "theme" );

       op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

       IProject project = CoreUtil.getProject( "maven-theme-test" );

       assertNotNull( project );

       IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, project );

       assertNotNull( bundleProject );
    }

    private void verifyProject(IProject project ) throws Exception
    {
        assertNotNull( project );
        assertTrue( project.exists() );

        assertFalse(project.getFile( "build.gradle" ).exists());

        project.build( IncrementalProjectBuilder.CLEAN_BUILD, monitor );

        waitForJobsToComplete();

        project.build( IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor );

        waitForJobsToComplete();

        assertNoErrors( project );
    }

}
