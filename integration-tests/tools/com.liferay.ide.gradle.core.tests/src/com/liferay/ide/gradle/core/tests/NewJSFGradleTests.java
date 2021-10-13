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

import com.liferay.ide.gradle.core.tests.base.NewJSFGradleBase;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.modules.BaseModuleOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class NewJSFGradleTests extends NewJSFGradleBase {

	@Test
	public void createAlloy() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("alloy");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createBootsfaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("bootsfaces");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createButterfaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("butterfaces");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createIcefaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("icefaces");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPrimefaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("primefaces");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createRichfaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("richfaces");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createStandard() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("jsf");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void testDefaultBuildType() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		Value<NewLiferayProjectProvider<BaseModuleOp>> projectProvider = op.getProjectProvider();

		DefaultValueService buildTypeDefaultService = projectProvider.service(DefaultValueService.class);

		Assert.assertEquals(buildTypeDefaultService.value(), "gradle-jsf");
	}

	@Test
	public void testProjectExisted() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("richfaces");
		op.setProjectName(project.getName());

		IProject existedProject = createOrImportAndBuild(op, project.getName());

		Assert.assertNotNull(existedProject);

		NewLiferayJSFModuleProjectOp newProjectNameop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		newProjectNameop.setProjectProvider("gradle-jsf");
		newProjectNameop.setProjectName(project.getName());

		Value<String> projectName = newProjectNameop.getProjectName();

		Status projectNameExistedValidationStatus = projectName.validation();

		Assert.assertFalse(projectNameExistedValidationStatus.ok());
	}

	@Override
	protected String shape() {
		return "war";
	}

}