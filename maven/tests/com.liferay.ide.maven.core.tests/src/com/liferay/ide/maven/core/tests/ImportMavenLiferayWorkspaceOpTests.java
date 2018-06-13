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
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.m2e.tests.common.JobHelpers;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.wst.server.core.IServer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.osgi.framework.Bundle;

/**
 * @author Andy Wu
 * @author Joye Luo
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class ImportMavenLiferayWorkspaceOpTests {

	@Before
	public void clearWorkspace() throws Exception {
		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testImportMavenLiferayWorkspaceInitBundle() throws Exception {
		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		Bundle bundle = Platform.getBundle("com.liferay.ide.maven.core.tests");

		URL zipUrl = FileLocator.toFileURL(bundle.getEntry("projects/maven-liferay-workspace.zip"));

		File zipFile = new File(zipUrl.getFile());

		File rootFile = CoreUtil.getWorkspaceRootFile();

		ZipUtil.unzip(zipFile, rootFile);

		File workspaceFolder = new File(rootFile, "maven-liferay-workspace");

		op.setWorkspaceLocation(workspaceFolder.getAbsolutePath());

		op.setProvisionLiferayBundle(true);

		Value<String> url = op.getBundleUrl();

		String bundleUrl = url.content(true);

		Assert.assertEquals(
			"https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5" +
				"/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip",
			bundleUrl);

		op.setServerName("test-bundle");

		Status validationStatus = op.validation();

		Assert.assertTrue(validationStatus.ok());

		op.execute(new ProgressMonitor());

		Util.waitForJobsToComplete();

		JobHelpers.waitForJobs(
			job -> {
				String jobName = job.getName();

				if (jobName.equals("Init Liferay Bundle")) {
					return true;
				}

				return false;
			},

			30 * 60 * 1000);

		File bundleDir = new File(workspaceFolder, "bundles");

		Assert.assertTrue(bundleDir.exists());

		Value<String> serverName = op.getServerName();

		IServer server = ServerUtil.getServer(serverName.content());

		Assert.assertTrue(ServerUtil.isLiferayRuntime(server));

		IProject project = CoreUtil.getProject("maven-liferay-workspace");

		Assert.assertTrue(project.exists());

		op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		Status status = op.validation();

		Assert.assertEquals(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg, status.message());

		project = CoreUtil.getProject("maven-liferay-workspace");

		project.delete(true, true, new NullProgressMonitor());
	}

	@Test
	public void testImportMavenLiferayWorkspaceInitBundleSetUrl() throws Exception {
		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		Bundle bundle = Platform.getBundle("com.liferay.ide.maven.core.tests");

		URL zipUrl = FileLocator.toFileURL(bundle.getEntry("projects/maven-liferay-workspace-bundleUrl.zip"));

		File zipFile = new File(zipUrl.getFile());

		IPath rootLocation = CoreUtil.getWorkspaceRootLocation();

		File rootFile = CoreUtil.getWorkspaceRootFile();

		String projectName = "maven-liferay-workspace";

		IPath fullLocation = rootLocation.append(projectName);

		String projectLocation = fullLocation.toPortableString();

		ZipUtil.unzip(zipFile, rootFile);

		File pomFile = new File(projectLocation, "pom.xml");

		Assert.assertTrue(pomFile.exists());

		String content = FileUtil.readContents(pomFile);

		String existUrl = "http://www.example.com/";

		Assert.assertTrue(content.contains(existUrl));

		String emailConfig = "<emailAddress>example@liferay.com</emailAddress>";

		Assert.assertTrue(content.contains(emailConfig));

		File wsFolder = new File(rootFile, projectName);

		op.setWorkspaceLocation(wsFolder.getAbsolutePath());

		op.setProvisionLiferayBundle(true);

		Value<String> url = op.getBundleUrl();

		String defaultUrl = url.content(true);

		Assert.assertEquals(existUrl, defaultUrl);

		String bundleUrl =
			"https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5" +
				"/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";

		op.setBundleUrl(bundleUrl);

		op.execute(new ProgressMonitor());

		Util.waitForJobsToComplete();

		JobHelpers.waitForJobs(
			job -> {
				String jobName = job.getName();

				if (jobName.equals("Init Liferay Bundle")) {
					return true;
				}

				return false;
			},

			30 * 60 * 1000);

		content = FileUtil.readContents(pomFile);

		File bundleDir = new File(projectLocation, "bundles");

		Assert.assertTrue(bundleDir.exists());

		Assert.assertFalse(content.contains(existUrl));
		Assert.assertTrue(content.contains(bundleUrl));
		Assert.assertTrue(content.contains(emailConfig));

		IProject project = CoreUtil.getProject("maven-liferay-workspace");

		project.delete(true, true, new NullProgressMonitor());
	}

	@Test
	public void testImportMavenLiferayWorkspaceOp() throws Exception {
		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		Bundle bundle = Platform.getBundle("com.liferay.ide.maven.core.tests");

		URL zipUrl = FileLocator.toFileURL(bundle.getEntry("projects/maven-liferay-workspace.zip"));

		File zipFile = new File(zipUrl.getFile());

		File rootFile = CoreUtil.getWorkspaceRootFile();

		ZipUtil.unzip(zipFile, rootFile);

		File workspaceFolder = new File(rootFile, "maven-liferay-workspace");

		op.setWorkspaceLocation(workspaceFolder.getAbsolutePath());

		Status validationStatus = op.validation();

		Assert.assertTrue(validationStatus.ok());

		op.execute(new ProgressMonitor());

		JobHelpers.waitForJobsToComplete(new NullProgressMonitor());

		IProject project = CoreUtil.getProject("maven-liferay-workspace");

		Assert.assertTrue(project.exists());

		project = CoreUtil.getProject("maven-liferay-workspace-modules");

		Assert.assertTrue(project.exists());

		project = CoreUtil.getProject("maven-liferay-workspace-themes");

		Assert.assertTrue(project.exists());

		project = CoreUtil.getProject("maven-liferay-workspace-wars");

		Assert.assertTrue(project.exists());

		op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		Status status = op.validation();

		Assert.assertEquals(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg, status.message());

		project = CoreUtil.getProject("maven-liferay-workspace");

		project.delete(true, true, new NullProgressMonitor());
	}

}