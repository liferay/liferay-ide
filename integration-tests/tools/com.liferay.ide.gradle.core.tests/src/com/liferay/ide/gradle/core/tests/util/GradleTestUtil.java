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

package com.liferay.ide.gradle.core.tests.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.JobUtil;
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.core.LiferayGradleProject;
import com.liferay.ide.test.core.base.support.ImportProjectSupport;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

import org.junit.Assert;

/**
 * @author Gregory Amerson
 */
public class GradleTestUtil {

	public static void failTest(Exception exception) {
		StringWriter s = new StringWriter();

		exception.printStackTrace(new PrintWriter(s));

		Assert.fail(s.toString());
	}

	public static LiferayGradleProject fullImportGradleProject(ImportProjectSupport ips) throws Exception {
		IStatus status = GradleUtil.synchronizeProject(new Path(ips.getPath()), new NullProgressMonitor());

		waitForBuildAndValidation();

		if (status.isOK()) {
			IProject project = CoreUtil.getProject(ips.getName());

			return new LiferayGradleProject(project);
		}

		throw new Exception(status.getException());
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
			manager.join(LiferayGradleCore.FAMILY_BUILDSHIP_CORE_JOBS, new NullProgressMonitor());
			JobUtil.waitForLiferayProjectJob();

			Thread.sleep(200);

			manager.beginRule(root = workspace.getRoot(), null);
		}
		catch (InterruptedException interruptedException) {
			failTest(interruptedException);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			failTest(illegalArgumentException);
		}
		catch (OperationCanceledException operationCanceledException) {
			failTest(operationCanceledException);
		}
		finally {
			if (root != null) {
				manager.endRule(root);
			}
		}
	}

}