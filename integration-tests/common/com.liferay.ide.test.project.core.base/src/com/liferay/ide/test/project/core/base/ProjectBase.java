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

package com.liferay.ide.test.project.core.base;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.test.core.base.BaseTests;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.junit.AfterClass;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class ProjectBase extends BaseTests {

	@AfterClass
	public static void cleanUp() {
		for (IProject project : CoreUtil.getAllProjects()) {
			try {
				project.close(npm);

				project.delete(true, npm);
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	protected void needJobsToBuild(IJobManager manager) throws InterruptedException, OperationCanceledException {
	}

	protected void verifyProject(IProject project) {
		assertProjectExists(project);

		verifyProjectFiles(project.getName());

		try {
			project.build(IncrementalProjectBuilder.CLEAN_BUILD, npm);
		}
		catch (CoreException ce) {
			failTest(ce);
		}

		waitForBuildAndValidation();

		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, npm);
		}
		catch (CoreException ce) {
			failTest(ce);
		}

		waitForBuildAndValidation();
	}

	protected void verifyProject(String projectName) {
		verifyProject(project(projectName));
	}

	protected void verifyProjectFiles(String projectName) {
	}

	protected void waitForBuildAndValidation() {
		IWorkspaceRoot root = null;

		IJobManager manager = Job.getJobManager();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		try {
			workspace.checkpoint(true);

			manager.join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			manager.join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
			manager.join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			manager.join(ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor());

			needJobsToBuild(manager);

			Thread.sleep(200);

			manager.beginRule(root = workspace.getRoot(), null);
		}
		catch (InterruptedException ie) {
			failTest(ie);
		}
		catch (IllegalArgumentException iae) {
			failTest(iae);
		}
		catch (OperationCanceledException oce) {
			failTest(oce);
		}
		finally {
			if (root != null) {
				manager.endRule(root);
			}
		}
	}

}