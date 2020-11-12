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

import com.liferay.ide.maven.core.tests.base.NewModuleMavenBase;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class NewModuleWarMavenTests extends NewModuleMavenBase {

	@Test
	public void createLayoutTemplate() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("layout-template");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Ignore("ignore to wait new Spring MVC Portlet Wizard")
	@Test
	public void createSpringMvcPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("spring-mvc-portlet");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createWarHook() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("war-hook");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createWarMvcPortlet() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("war-mvc-portlet");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Override
	protected String shape() {
		return "war";
	}

}