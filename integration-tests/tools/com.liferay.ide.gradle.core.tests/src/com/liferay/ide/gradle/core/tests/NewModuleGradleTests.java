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

import com.liferay.ide.gradle.core.tests.base.NewModuleGradleBase;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.junit.Test;

/**
 * @author Joye Luo
 * @author Terry Jia
 */
public class NewModuleGradleTests extends NewModuleGradleBase {

	@Test
	public void createActivator() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("activator");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createApi() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("api");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createContentTargetingReport() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-report");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createContentTargetingRule() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-rule");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createContentTargetingTrackingAction() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("content-targeting-tracking-action");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createControlMenuEntry() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("control-menu-entry");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createFormField() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("form-field");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createMvcPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("mvc-portlet");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPanelApp() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("panel-app");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPortletConfigurationIcon() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-configuration-icon");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPortletProvider() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-provider");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPortletToolbarContributor() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("portlet-toolbar-contributor");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createRest() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("rest");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createSimulationPanelEntry() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("simulation-panel-entry");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createSoyPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("soy-portlet");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createTemplateContextContributor() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("template-context-contributor");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createThemeContributor() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("theme-contributor");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void testServiceBuilder() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("service-builder");
		op.setPackageName("com.liferay.test");

		create(op, project.getName());

		assertProjectExists(project.getName());

		assertBundleProject(project.getName() + "-api");

		verifyProject(project.getName() + "-api");

		assertProjectFileExists(project.getName() + "-api", "build/libs/com.liferay.test.api-1.0.0.jar");

		assertBundleProject(project.getName() + "-service");

		verifyProject(project.getName() + "-service");

		assertProjectFileExists(project.getName() + "-service", "build/libs/com.liferay.test.service-1.0.0.jar");

		deleteProject(project.getName() + "-api");

		deleteProject(project.getName() + "-service");

		deleteProject(project.getName());
	}

	@Override
	protected String shape() {
		return "jar";
	}

}