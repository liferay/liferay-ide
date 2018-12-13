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

import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewModuleNpm71MavenTests extends NewModuleMavenBase {

	@Test
	public void createNpmAngularPortlet71() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("npm-angular-portlet");
		op.setLiferayVersion("7.1");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createNpmReactPortlet71() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("npm-react-portlet");
		op.setLiferayVersion("7.1");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Test
	public void createNpmVuejsPortlet71() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setProjectTemplateName("npm-vuejs-portlet");
		op.setLiferayVersion("7.1");

		createOrImportAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Override
	protected String shape() {
		return "jar";
	}

	protected void verifyProjectFiles(String projectName) {
		super.verifyProjectFiles(projectName);

		assertProjectFileContains(
			projectName, "src/main/resources/META-INF/resources/view.jsp",
			"<aui:script require=\"<%= mainRequire %>\">");
	}

}