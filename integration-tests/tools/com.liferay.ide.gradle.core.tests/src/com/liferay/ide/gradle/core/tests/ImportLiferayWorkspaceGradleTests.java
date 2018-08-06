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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;
import com.liferay.ide.test.core.base.support.ImportProjectSupport;
import com.liferay.ide.test.project.core.base.ProjectOpBase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Value;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
@Ignore("ignore and will fix later")
public class ImportLiferayWorkspaceGradleTests extends ProjectOpBase<ImportLiferayWorkspaceOp> {

	@Test
	public void importLiferayWorkspaceDontOverrideGradleProperties() throws CoreException {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace");

		ips.before();

		IPath gradlePath = ips.getFile("gradle.properties");

		String content = FileUtil.readContents(gradlePath.toFile());

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(ips.getPath());

		createOrImportAndBuild(op, ips.getName());

		assertNotLiferayProject(ips.getName());
		assertLiferayProject("sample-portlet");

		assertProjectFileEquals(ips.getName(), "gradle.properties", content);

		deleteProject("roster-api");
		deleteProject("roster-service");
		deleteProject("roster-web");
		deleteProject("sample-portlet");
		deleteProject("sample-model-listener");
		deleteProject("jstl.test");
		deleteProject("sample-theme");
		deleteProject("apps");
		deleteProject("extensions");
		deleteProject("modules");
		deleteProject("themes");
		deleteProject(ips.getName());
	}

	@Test
	public void importLiferayWorkspaceInitBundle() throws CoreException {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace");

		ips.before();

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(ips.getPath());
		op.setProvisionLiferayBundle(true);

		createOrImportAndBuild(op, ips.getName());

		assertProjectFileExists(ips.getName(), "bundles");

		deleteProject("roster-api");
		deleteProject("roster-service");
		deleteProject("roster-web");
		deleteProject("sample-portlet");
		deleteProject("sample-model-listener");
		deleteProject("jstl.test");
		deleteProject("sample-theme");
		deleteProject("apps");
		deleteProject("extensions");
		deleteProject("modules");
		deleteProject("themes");
		deleteProject(ips.getName());
	}

	@Test
	public void validationCustomBundleUrl() throws CoreException {
		ImportProjectSupport ips = new ImportProjectSupport("test-liferay-workspace-custom-bundle-url");

		ips.before();

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(ips.getPath());
		op.setProvisionLiferayBundle(true);

		Value<String> bundleUrlValue = op.getBundleUrl();

		String bundleUrl = bundleUrlValue.content(true);

		StringBuffer sb = new StringBuffer();

		sb.append("https://api.liferay.com/downloads/portal/");
		sb.append("7.0.10.6");
		sb.append("/");
		sb.append("liferay-dxp-digital-enterprise-tomcat-7.0-sp6-20171010144253003.zip");

		Assert.assertEquals(sb.toString(), bundleUrl);

		ips.after();
	}

	@Override
	protected String provider() {
		return null;
	}

}