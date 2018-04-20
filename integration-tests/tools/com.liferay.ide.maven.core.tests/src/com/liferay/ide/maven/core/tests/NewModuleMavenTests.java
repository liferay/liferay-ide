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

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.test.core.base.support.ProjectSupport;
import com.liferay.ide.test.project.core.base.NewModuleOpBase;

import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.tests.common.JobHelpers;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class NewModuleMavenTests extends NewModuleOpBase<NewLiferayModuleProjectOp> {

	@Test
	public void createActivator() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider("maven-module");
		op.setProjectTemplateName("activator");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport();

	protected void verifyProjectFiles(IProject project) {
		assertFileNotExists(project.getFile("build.gradle"));
		assertFileExists(project.getFile("pom.xml"));
	}

	protected void waitForBuildAndValidation() {
		JobHelpers.waitForJobsToComplete();
	}

}