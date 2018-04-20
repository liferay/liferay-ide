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
import com.liferay.ide.gradle.core.GradleUtil;
import com.liferay.ide.test.core.base.support.ImportProjectSupport;
import com.liferay.ide.test.project.core.base.ProjectBase;

import org.eclipse.buildship.core.CorePlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;

import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class GradleUtilTests extends ProjectBase {

	@Test
	public void importLiferayWorkspace() throws CoreException {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace");

		ips.before();

		GradleUtil.importGradleProject(ips.getProjectFile(), npm);

		waitForBuildAndValidation();

		assertNotLiferayProject(ips.getName());

		assertLiferayProject("jstl.test");
		assertLiferayProject("roster-api");
		assertLiferayProject("roster-service");
		assertLiferayProject("roster-web");
		assertLiferayProject("sample-portlet");
		assertLiferayProject("sample-model-listener");
		assertLiferayProject("sample-theme");

		assertSourceFolders("sample-theme", "src");

		deleteProject(ips.getName());
	}

	@Test
	public void importLiferayWorkspaceEE() {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace-ee");

		ips.before();

		try {
			GradleUtil.importGradleProject(ips.getProjectFile(), npm);
		}
		catch (CoreException e) {
			failTest(e);
		}

		waitForBuildAndValidation();

		assertNotLiferayProject(ips.getName());

		assertNotLiferayProject("aws");
		assertNotLiferayProject("docker");
		assertNotLiferayProject("jenkins");

		deleteProject(ips.getName());
	}

	@Test
	public void isBuildFile() {
		// TODO test for GradleUtil.isBuildFile()
	}

	@Test
	public void refreshGradleProject() {
		// TODO test for GradleUtil.refreshGradleProject()
	}

	@Test
	public void runGradleTask() {
		// TODO test for GradleUtil.runGradleTask()
	}

	@Override
	protected void needJobsToBuild(IJobManager manager) throws InterruptedException, OperationCanceledException {
		manager.join(CorePlugin.GRADLE_JOB_FAMILY, new NullProgressMonitor());
		manager.join(GradleCore.JobFamilyId, new NullProgressMonitor());
	}

}