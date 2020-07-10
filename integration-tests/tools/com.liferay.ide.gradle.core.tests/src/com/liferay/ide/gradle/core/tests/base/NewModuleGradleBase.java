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

package com.liferay.ide.gradle.core.tests.base;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.JobUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOpMethods;
import com.liferay.ide.test.project.core.base.NewModuleOpBase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public abstract class NewModuleGradleBase extends NewModuleOpBase<NewLiferayModuleProjectOp> {

	@BeforeClass
	public static void createLiferayWorkspace() throws CoreException {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceName("liferay-gradle-workspace");

		op.setProjectProvider("gradle-liferay-workspace");
		op.setProductVersion("portal-7.3-ga4");

		JobUtil.waitForLiferayProjectJob();

		NewLiferayWorkspaceOpMethods.execute(op, ProgressMonitorBridge.create(new NullProgressMonitor()));
	}

	@AfterClass
	public static void deleteWorksapceProject() throws CoreException {
		IProgressMonitor monitor = new NullProgressMonitor();

		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, monitor);
		}
	}

	@Override
	protected void needJobsToBuild(IJobManager manager) throws InterruptedException, OperationCanceledException {
		manager.join(LiferayGradleCore.FAMILY_BUILDSHIP_CORE_JOBS, new NullProgressMonitor());
	}

	@Override
	protected String provider() {
		return "gradle-module";
	}

	protected void verifyProjectFiles(String projectName) {
		assertProjectFileExists(projectName, "build.gradle");
		assertProjectFileNotExists(projectName, "pom.xml");
	}

}