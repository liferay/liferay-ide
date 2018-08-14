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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.modules.BaseModuleOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.m2e.tests.common.WorkspaceHelpers;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.junit.Assert;

/**
 * @author Charles Wu
 */
@SuppressWarnings("restriction")
public class MavenTestUtil {

	public static IProject create(BaseModuleOp op) throws InterruptedException, CoreException {
		Status status = op.execute(ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertNotNull(status);

		Assert.assertTrue(status.message(), status.ok());

		waitForJobsToComplete();

		Value<String> projectName = op.getProjectName();

		return CoreUtil.getProject(projectName.content());
	}

	public static IProject createAndBuild(BaseModuleOp op) throws Exception {
		Status validation = op.validation();

		Assert.assertTrue(validation.message(), validation.ok());

		IProject project = MavenTestUtil.create(op);

		verifyProject(project);

		return project;
	}

	public static void verifyProject(IProject project) throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();

		Assert.assertNotNull(project);
		Assert.assertTrue(project.exists());

		Assert.assertFalse(FileUtil.exists(project.getFile("build.gradle")));

		project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);

		JobHelpers.waitForJobsToComplete(monitor);

		project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		JobHelpers.waitForJobsToComplete(monitor);

		WorkspaceHelpers.assertNoErrors(project);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		Assert.assertNotNull(bundleProject);

		IPath outputBundle = bundleProject.getOutputBundle(true, monitor);

		Assert.assertNotNull(outputBundle);

		Assert.assertTrue(FileUtil.exists(outputBundle.toFile()));
	}

	public static void waitForJobsToComplete() throws InterruptedException, CoreException {
		JobHelpers.waitForJobs(job -> {
			Object property = job.getProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB);

			if (property != null || job.belongsTo(LiferayCore.LIFERAY_JOB_FAMILY)) {
				return true;
			}

			return false;
		},

		30 * 60 * 1000);

		JobHelpers.waitForJobsToComplete();
	}

}