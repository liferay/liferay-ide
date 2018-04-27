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

import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.LiferayGradleProject;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

import org.junit.Assert;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class Util {

	public static void deleteAllWorkspaceProjects() throws Exception {
		for (IProject project : CoreUtil.getAllProjects()) {
			project.close(new NullProgressMonitor());
			project.delete(true, new NullProgressMonitor());
		}
	}

	public static void failTest(Exception e) {
		StringWriter s = new StringWriter();

		e.printStackTrace(new PrintWriter(s));

		Assert.fail(s.toString());
	}

	public static LiferayGradleProject fullImportGradleProject(String projectPath) throws Exception {
		File src = new File(projectPath);

		IPath location = CoreUtil.getWorkspaceRootLocation();

		File dst = new File(location.toFile(), src.getName());

		TestUtil.copyDir(src, dst);

		IProgressMonitor monitor = new NullProgressMonitor();

		IStatus status = GradleUtil.importGradleProject(dst, monitor);

		waitForBuildAndValidation();

		if (status.isOK()) {
			IProject project = CoreUtil.getProject(dst.getName());

			return new LiferayGradleProject(project);
		}
		else {
			throw new Exception(status.getException());
		}
	}

	public static void waitForBuildAndValidation() throws Exception {
		IWorkspaceRoot root = null;

		IJobManager manager = Job.getJobManager();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		try {
			workspace.checkpoint(true);

			manager.join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			manager.join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
			manager.join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			manager.join(CorePlugin.GRADLE_JOB_FAMILY, new NullProgressMonitor());
			manager.join(GradleCore.JOB_FAMILY_ID, new NullProgressMonitor());

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