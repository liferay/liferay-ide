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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.workspace.BaseLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;

import java.io.File;

import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andy Wu
 * @author Joye Luo
 */
@SuppressWarnings("restriction")
public class NewMavenLiferayWorkspaceOpTests {

	@Before
	public void clearWorkspace() throws Exception {
		for (IProject project : CoreUtil.getAllProjects()) {
			project.close(new NullProgressMonitor());
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testNewLiferayWorkspaceOpWithInvalidBundleUrl() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceName("NewGradleWorkspaceWithInvalidBundleUrl");
		op.setProjectProvider("maven-liferay-workspace");
		op.setUseDefaultLocation(false);
		op.setProvisionLiferayBundle(true);
		op.setBundleUrl("https://issues.liferay.com/browse/IDE-3605");

		String serverName = "NewServerNameWithInvalidBundleUrl";

		op.setServerName(serverName);

		op.execute(new ProgressMonitor());

		IProject workspaceProject = CoreUtil.getProject("NewGradleWorkspaceWithInvalidBundleUrl");

		Assert.assertNotNull(workspaceProject);

		Assert.assertTrue(workspaceProject.exists());

		Stream<IServer> serverStream = Stream.of(ServerCore.getServers());

		boolean serverCreated = serverStream.filter(
			server -> server.getName().equals(serverName)
		).findAny(
		).isPresent();

		Assert.assertFalse(serverCreated);
	}

	@Test
	public void testNewMavenLiferayWorkspaceInitBundle() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		String projectName = "test-liferay-workspace-init";

		IPath rootLocation = CoreUtil.getWorkspaceRootLocation();

		op.setWorkspaceName(projectName);
		op.setUseDefaultLocation(false);
		op.setLocation(rootLocation.toPortableString());
		op.setProjectProvider("maven-liferay-workspace");
		op.setProvisionLiferayBundle(true);

		Value<String> bundleUrl = op.getBundleUrl();

		Assert.assertEquals(
			"https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5" +
				"/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip",
				bundleUrl);

		op.execute(new ProgressMonitor());

		JobHelpers.waitForJobs(
			job -> {
				String jobName = job.getName();

				if (jobName.equals("Init Liferay Bundle")) {
					return true;
				}

				return false;
			},

			30 * 60 * 1000);

		IPath fullLocation = rootLocation.append(projectName);

		IPath pomPath = fullLocation.append("pom.xml");

		Assert.assertTrue(FileUtil.exists(pomPath));

		IPath bundlesPath = fullLocation.append("bundles");

		Assert.assertTrue(FileUtil.exists(bundlesPath));

		String content = FileUtil.readContents(pomPath.toFile());

		Assert.assertTrue(content.contains("com.liferay.portal.tools.bundle.support"));
	}

	@Test
	public void testNewMavenLiferayWorkspaceOp() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		String projectName = "test-liferay-workspace";

		IPath rootLocation = CoreUtil.getWorkspaceRootLocation();

		op.setWorkspaceName(projectName);
		op.setUseDefaultLocation(false);
		op.setLocation(rootLocation.toPortableString());
		op.setProjectProvider("maven-liferay-workspace");

		op.execute(new ProgressMonitor());

		IPath wslocation = rootLocation.append(projectName);

		String projectLocation = wslocation.toPortableString();

		File pomFile = new File(projectLocation, "pom.xml");

		Assert.assertTrue(pomFile.exists());

		String content = FileUtil.readContents(pomFile);

		Assert.assertTrue(content.contains("com.liferay.portal.tools.bundle.support"));
	}

	@Test
	public void testNewMavenLiferayWorkspaceOpWithBundle71() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		String projectName = "test-liferay-workspace-71";

		IWorkspaceRoot wsRoot = CoreUtil.getWorkspaceRoot();

		IPath workspaceLocation = wsRoot.getLocation();

		op.setWorkspaceName(projectName);
		op.setLiferayVersion("7.1");
		op.setProjectProvider("maven-liferay-workspace");
		op.setLocation(workspaceLocation.toPortableString());

		Status status = op.execute(new ProgressMonitor());

		Assert.assertNotNull(status);
		Assert.assertEquals("OK", status.message());

		IPath wsPath = workspaceLocation.append(projectName);

		String wsLocation = wsPath.toPortableString();

		File wsFile = new File(wsLocation);

		File pomFile = new File(wsFile, "pom.xml");

		Assert.assertTrue(pomFile.exists());

		String xml = FileUtil.readContents(pomFile);

		Assert.assertTrue(xml.contains(BaseLiferayWorkspaceOp.LIFERAY_71_BUNDLE_URL));
	}

	@Test
	public void testNewMavenLiferayWorkspaceSetUrl() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		String projectName = "test-liferay-workspace-url";

		String bundleUrl =
			"https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5" +
				"/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		IWorkspaceRoot wsRoot = CoreUtil.getWorkspaceRoot();

		IPath workspaceLocation = wsRoot.getLocation();

		op.setWorkspaceName(projectName);
		op.setUseDefaultLocation(false);
		op.setLocation(workspaceLocation.toPortableString());
		op.setProjectProvider("maven-liferay-workspace");
		op.setProvisionLiferayBundle(true);
		op.setBundleUrl(bundleUrl);

		op.execute(new ProgressMonitor());

		JobHelpers.waitForJobs(
			job -> {
				String jobName = job.getName();

				if (jobName.equals("Init Liferay Bundle")) {
					return true;
				}

				return false;
			},

			30 * 60 * 1000);

		IPath wslocation = workspaceLocation.append(projectName);

		String projectLocation = wslocation.toPortableString();

		File pomFile = new File(projectLocation, "pom.xml");

		Assert.assertTrue(pomFile.exists());

		File bundleDir = new File(projectLocation, "bundles");

		Assert.assertTrue(bundleDir.exists());

		String content = FileUtil.readContents(pomFile);

		Assert.assertEquals(content.contains(bundleUrl), true);
	}

}