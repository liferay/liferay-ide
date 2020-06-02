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

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.tests.base.NewModuleMavenBase;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOpMethods;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class NewModuleMavenTests extends NewModuleMavenBase {

	@BeforeClass
	public static void createLiferayWorkspace() {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceName("new-maven-workspace");
		op.setProjectProvider("maven-liferay-workspace");

		NewLiferayWorkspaceOpMethods.execute(op, ProgressMonitorBridge.create(new NullProgressMonitor()));
	}

	@AfterClass
	public static void deleteWorksapceProject() throws CoreException {
		IProgressMonitor monitor = new NullProgressMonitor();

		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, monitor);
		}
	}

	@Ignore("Template has been removed on latest blade 3.10.0")
	@Test
	public void createActivator() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("activator");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createApi() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("api");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Ignore("Template has been removed on latest blade 3.10.0")
	@Test
	public void createContentTargetingReport() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		Value<String> version = op.getLiferayVersion();

		if ("7.2".equals(version.getDefaultContent())) {
			return;
		}

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-report");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Ignore("Template has been removed on latest blade 3.10.0")
	@Test
	public void createContentTargetingRule() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		Value<String> version = op.getLiferayVersion();

		if ("7.2".equals(version.getDefaultContent())) {
			return;
		}

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-rule");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Ignore("Template has been removed on latest blade 3.10.0")
	@Test
	public void createContentTargetingTrackingAction() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		Value<String> version = op.getLiferayVersion();

		if ("7.2".equals(version.getDefaultContent())) {
			return;
		}

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-tracking-action");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createControlMenuEntry() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("control-menu-entry");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createFormField() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		Value<String> version = op.getLiferayVersion();

		if ("7.2".equals(version.getDefaultContent())) {
			return;
		}

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("form-field");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createMvcPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("mvc-portlet");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPanelApp() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("panel-app");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Ignore("no portlet template since blade 3.7.0")
	@Test
	public void createPortlet() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPortletConfigurationIcon() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-configuration-icon");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPortletProvider() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-provider");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPortletToolbarContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-toolbar-contributor");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createRest() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("rest");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createSimulationPanelEntry() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("simulation-panel-entry");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Ignore("ignore as endless building, no this template since blade 3.7.0")
	@Test
	public void createSoyPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("soy-portlet");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createTemplateContextContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("template-context-contributor");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createThemeContributor() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("theme-contributor");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Override
	protected String shape() {
		return "jar";
	}

}