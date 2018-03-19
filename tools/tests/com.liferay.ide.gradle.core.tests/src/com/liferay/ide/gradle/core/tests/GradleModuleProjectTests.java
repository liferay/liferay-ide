/**
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
 */

package com.liferay.ide.gradle.core.tests;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Test;

/**
 * @author Joye Luo
 */
public class GradleModuleProjectTests {

    private IProject create( NewLiferayModuleProjectOp op ) throws Exception
    {
        Status status = op.execute( ProgressMonitorBridge.create( _monitor ) );

        assertNotNull( status );
        assertTrue( status.message(), status.ok() );

        Util.waitForBuildAndValidation();

        return CoreUtil.getProject( op.getProjectName().content() );
    }

    private IProject createAndBuild( NewLiferayModuleProjectOp op ) throws Exception
    {
        assertTrue( op.validation().message(), op.validation().ok() );

        IProject project = create( op );

        verifyProject( project );

        return project;
    }

    @Test
    public void testProjectTemplateActivator() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "activator-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "activator" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateApi() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "api-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "api" );

        createAndBuild( op );
    }

    @Test
    public void testProjectTemplateContentTargetingReport() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "content-targeting-report-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "content-targeting-report" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateContentTargetingRule() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "content-targeting-rule-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "content-targeting-rule" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateContentTargetingTrackingAction() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "content-targeting-tracking-action-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "content-targeting-tracking-action" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateControlMenuEntry() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "control-menu-entry-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "control-menu-entry" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateFormField() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "form-field-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "form-field" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateLayoutTemplate() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "layout-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "layout-template" );

        IProject project = createAndBuild(op);

        project.refreshLocal( IResource.DEPTH_INFINITE, _monitor );

        assertTrue( project.getFile( "build/libs/layout-test.war" ).exists() );
    }

    @Test
    public void testProjectTemplateMvcPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "mvc-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "mvc-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmAngularPortlet () throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-angular-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-angular-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmBillboardjsPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-billboardjs-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-billboardjs-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmIsomorphicPortlet () throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-isomorphic-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-isomorphic-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmJqueryPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-jquery-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-jquery-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmMetaljsPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-metaljs-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-metaljs-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmReactPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-react-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-react-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmVuejsPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-vuejs-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-vuejs-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateNpmAngularPortlet71() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-angular-portlet-test-v71" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-angular-portlet" );
        op.setLiferayVersion( "7.1" );

        createAndBuild( op );
        verifyNpmPortletV71( op );
    }

    @Test
    public void testProjectTemplateNpmBillboardjsPortlet71() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-billboardjs-portlet-test-v71" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-billboardjs-portlet" );
        op.setLiferayVersion( "7.1" );

        createAndBuild( op );
        verifyNpmPortletV71( op );
    }

    @Test
    public void testProjectTemplateNpmIsomorphicPortlet71() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-isomorphic-portlet-test-v71" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-isomorphic-portlet" );
        op.setLiferayVersion( "7.1" );

        createAndBuild( op );
        verifyNpmPortletV71( op );
    }

    @Test
    public void testProjectTemplateNpmJqueryPortlet71() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-jquery-portlet-test-v71" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-jquery-portlet" );
        op.setLiferayVersion( "7.1" );

        createAndBuild( op );
        verifyNpmPortletV71( op );
    }

    @Test
    public void testProjectTemplateNpmMetaljsPortlet71() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-metaljs-portlet-test-v71" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-metaljs-portlet" );
        op.setLiferayVersion( "7.1" );

        createAndBuild( op );
        verifyNpmPortletV71( op );
    }

    @Test
    public void testProjectTemplateNpmPortlet71() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-portlet-test-v71" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-portlet" );
        op.setLiferayVersion( "7.1" );

        createAndBuild( op );
        verifyNpmPortletV71( op );
    }

    @Test
    public void testProjectTemplateNpmReactPortlet71() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-react-portlet-test-v71" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-react-portlet" );
        op.setLiferayVersion( "7.1" );

        createAndBuild( op );
        verifyNpmPortletV71( op );
    }

    @Test
    public void testProjectTemplateNpmVuejsPortlet71() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "npm-vuejs-portlet-test-v71" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "npm-vuejs-portlet" );
        op.setLiferayVersion( "7.1" );

        createAndBuild( op );
        verifyNpmPortletV71( op );
    }

    @Test
    public void testProjectTemplatePanelApp() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "panel-app-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "panel-app" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePortletConfigurationIcon() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "portlet-configuration-icon-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "portlet-configuration-icon" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePortletProvider() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "portlet-provider-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "portlet-provider" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplatePortletToolbarContributor() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "portlet-toolbar-contributor-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "portlet-toolbar-contributor" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateRest() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "rest-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "rest" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateService() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "service-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "service" );
        op.setServiceName( "com.liferay.portal.kernel.events.LifecycleAction" );

        IProject project = create( op );

        IFile serviceFile = project.getFile( "src/main/java/service/test/ServiceTest.java" );

        assertTrue( serviceFile.exists() );

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
            "public class ServiceTest implements LifecycleAction {\n" +
                "@Override public void processLifecycleEvent(LifecycleEvent lifecycleEvent) throws ActionException { }\n" +
            "}";

        serviceFile.setContents( new ByteArrayInputStream( contents.getBytes() ), IResource.FORCE, _monitor );

        verifyProject( project );
    }

    @Test
    public void testProjectTemplateServiceBuilder() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "service-builder-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "service-builder" );
        op.setPackageName( "com.liferay.test" );

        IProject parent = create( op );

        assertTrue( parent != null && parent.exists() );

        IProject api = CoreUtil.getProject( "service-builder-test-api" );

        assertTrue( api != null && api.exists() );

        IProject service = CoreUtil.getProject( "service-builder-test-service" );

        assertTrue( service != null && service.exists() );

        api.build( IncrementalProjectBuilder.FULL_BUILD, _monitor );

        service.build( IncrementalProjectBuilder.FULL_BUILD, _monitor );

        IBundleProject apiBundle = LiferayCore.create( IBundleProject.class, api );

        assertNotNull( apiBundle );

        IPath apiOutput = apiBundle.getOutputBundle( true, _monitor );

        assertNotNull( apiOutput );

        assertTrue( apiOutput.toFile().exists() );

        assertEquals( "com.liferay.test.api-1.0.0.jar", apiOutput.lastSegment() );

        IBundleProject serviceBundle = LiferayCore.create( IBundleProject.class, service );

        IPath serviceOutput = serviceBundle.getOutputBundle( true, _monitor );

        assertNotNull( serviceOutput );

        assertTrue( serviceOutput.toFile().exists() );

        assertEquals( "com.liferay.test.service-1.0.0.jar", serviceOutput.lastSegment() );
    }

    @Test
    public void testProjectTemplateServiceWrapper() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "service-wrapper-test" );
        op.setProjectProvider( "gradle-module" );
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
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "simulation-panel-entry" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateSoyPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "soy-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "soy-portlet" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateSpringMvcPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "spring-mvc-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "spring-mvc-portlet" );

        IProject project = createAndBuild(op);

        project.refreshLocal( IResource.DEPTH_INFINITE, _monitor );

        assertTrue( project.getFile( "build/libs/spring-mvc-portlet-test.war" ).exists() );
    }

    @Test
    public void testProjectTemplateTemplateContextContributor() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "template-context-contributor-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "template-context-contributor" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateTheme() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "theme-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "theme" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateThemeContributor() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "theme-contributor-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "theme-contributor" );

        createAndBuild(op);
    }

    @Test
    public void testProjectTemplateWarHook() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "war-hook-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "war-hook" );

        IProject project = createAndBuild(op);

        project.refreshLocal( IResource.DEPTH_INFINITE, _monitor );

        assertTrue( project.getFile( "build/libs/war-hook-test.war" ).exists() );
    }

    @Test
    public void testProjectTemplateWarMvcPortlet() throws Exception
    {
        NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

        op.setProjectName( "war-mvc-portlet-test" );
        op.setProjectProvider( "gradle-module" );
        op.setProjectTemplateName( "war-mvc-portlet" );

        IProject project = createAndBuild(op);

        project.refreshLocal( IResource.DEPTH_INFINITE, _monitor );

        assertTrue( project.getFile( "build/libs/war-mvc-portlet-test.war" ).exists() );
    }

    private void verifyNpmPortletV71( NewLiferayModuleProjectOp op ) {

        IProject project = CoreUtil.getProject( op.getProjectName().content() );

        assertNotNull( project );

        IFile viewFile = project.getFile( "src/main/resources/META-INF/resources/view.jsp" );

        assertTrue( viewFile.exists() );

        String viewFileContent = FileUtil.readContents( viewFile.getLocation().toFile() );

        assertTrue( viewFileContent.contains( "<aui:script require=\"<%= bootstrapRequire %>\">" ) );
    }

    private void verifyProject( IProject project ) throws Exception
    {
        assertNotNull( project );
        assertTrue( project.exists() );

        assertTrue(project.getFile( "build.gradle" ).exists());

        project.build( IncrementalProjectBuilder.CLEAN_BUILD, _monitor );

        Util.waitForBuildAndValidation();

        project.build( IncrementalProjectBuilder.FULL_BUILD, _monitor );

        Util.waitForBuildAndValidation();

        IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, project );

        assertNotNull( bundleProject );

        IPath outputBundle = bundleProject.getOutputBundle( true,_monitor );

        assertNotNull( outputBundle );

        assertTrue( outputBundle.toFile().exists() );
    }

    private IProgressMonitor _monitor = new NullProgressMonitor();

}
