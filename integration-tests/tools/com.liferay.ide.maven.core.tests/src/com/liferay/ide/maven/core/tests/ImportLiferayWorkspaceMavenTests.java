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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;
import com.liferay.ide.test.core.base.support.ImportProjectSupport;
import com.liferay.ide.test.project.core.base.ProjectOpBase;

import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.sapphire.modeling.Status;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class ImportLiferayWorkspaceMavenTests extends ProjectOpBase<ImportLiferayWorkspaceOp> {

	@Test
	public void importLiferayWorkspace() {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace-maven");

		ips.before();

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(ips.getPath());
		op.setProvisionLiferayBundle(false);

		createOrImportAndBuild(op, ips.getName());

		assertNotLiferayProject(ips.getName());

		assertProjectExists("test-liferay-workspace-maven-modules");
		assertProjectExists("test-liferay-workspace-maven-themes");
		assertProjectExists("test-liferay-workspace-maven-wars");

		deleteProject(ips.getName());
	}

	@Test
	public void importLiferayWorkspaceInitBundle() {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace-maven");

		ips.before();

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(ips.getPath());
		op.setProvisionLiferayBundle(true);

		String bundleUrl = SapphireUtil.getContent(op.getBundleUrl());

		Assert.assertEquals(WorkspaceConstants.BUNDLE_URL_CE_7_0, bundleUrl);

		op.setServerName("test-bundle");

		createOrImportAndBuild(op, ips.getName());

		assertProjectFolderExists(ips.getName(), "bundles");

		assertLiferayServerExists("test-bundle");
		assertRuntimeExists("test-bundle");

		deleteServer("test-bundle");
		deleteRuntime("test-bundle");
		deleteProject(ips.getName());
	}

	@Test
	public void importLiferayWorkspaceInitBundleCustomBundleUrl() {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace-maven");

		ips.before();

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(ips.getPath());
		op.setProvisionLiferayBundle(true);
		op.setBundleUrl(WorkspaceConstants.BUNDLE_URL_CE_7_0);
		op.setServerName("test-bundle");

		createOrImportAndBuild(op, ips.getName());

		assertProjectFolderExists(ips.getName(), "bundles");

		assertLiferayServerExists("test-bundle");
		assertRuntimeExists("test-bundle");

		deleteServer("test-bundle");
		deleteRuntime("test-bundle");
		deleteProject(ips.getName());
	}

	@Test
	public void validationExistingLiferayWorkpace() {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace-maven");

		ips.before();

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(ips.getPath());
		op.setProvisionLiferayBundle(false);

		createOrImportAndBuild(op, ips.getName());

		op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		Status status = op.validation();

		Assert.assertEquals(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg, status.message());

		deleteProject(ips.getName());
	}

	@Override
	protected String provider() {
		return null;
	}

	@Override
	protected void waitForBuildAndValidation() {
		JobHelpers.waitForJobs(
			job -> {
				Object property = job.getProperty(ILiferayProjectProvider.LIFERAY_PROJECT_JOB);

				if ((property != null) || job.belongsTo(LiferayCore.LIFERAY_JOB_FAMILY)) {
					return true;
				}

				return false;
			},
			30 * 60 * 1000);

		JobHelpers.waitForJobsToComplete();
	}

}