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

import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.test.core.base.support.ProjectSupport;
import com.liferay.ide.test.project.core.base.NewModuleOpBase;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Joye Luo
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class NewModuleGradleTests extends NewModuleOpBase<NewLiferayModuleProjectOp> {

	@Test
	public void createActivator() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("activator");

		createAndBuild(op, project.getName());

		deleteProject(project.getName());
	}

	@Rule
	public ProjectSupport project = new ProjectSupport();

	@Override
	protected void needJobsToBuild(IJobManager manager) throws InterruptedException, OperationCanceledException {
		manager.join(CorePlugin.GRADLE_JOB_FAMILY, new NullProgressMonitor());
		manager.join(GradleCore.JobFamilyId, new NullProgressMonitor());
	}

	protected void verifyProjectFiles(IProject project) {
		assertFileExists(project.getFile("build.gradle"));
		assertFileNotExists(project.getFile("pom.xml"));
	}

}