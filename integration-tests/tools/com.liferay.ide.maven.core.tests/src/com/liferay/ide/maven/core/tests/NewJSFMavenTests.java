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

import com.liferay.ide.maven.core.tests.base.NewJSFMavenBase;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class NewJSFMavenTests extends NewJSFMavenBase {

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
	public void createAlloyFaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("alloy");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createBootsFaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("bootsfaces");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createButterFaces() throws Exception {
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
	public void createJSF() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("jsf");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createPrimeFaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("primefaces");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createRichFaces() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider(provider());
		op.setTemplateName("richfaces");
		op.setProjectName(project.getName());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void validateProjectNames() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("maven-jsf");
		op.setProjectName("Test1");

		Value<String> projectName = op.getProjectName();

		Status status = projectName.validation();

		Assert.assertEquals(status.message(), "ok");

		op.setProjectProvider("maven-jsf");
		op.setProjectName("#Test1");

		status = projectName.validation();

		Assert.assertEquals(status.message(), "The project name is invalid.");

		op.setProjectProvider("maven-jsf");
		op.setProjectName("Test1_Abc");

		status = projectName.validation();

		Assert.assertEquals(status.message(), "ok");
	}

	@Override
	protected String shape() {
		return "war";
	}

}