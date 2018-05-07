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
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.wst.server.core.IServer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andy Wu
 * @author Joye Luo
 */
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

		URL wsZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/maven-liferay-workspace.zip");

		File wsZipFile = new File(FileLocator.toFileURL(wsZipUrl).getFile());

		File eclipseWorkspaceLocation = CoreUtil.getWorkspaceRootLocation().toFile();

		ZipUtil.unzip(wsZipFile, eclipseWorkspaceLocation);

		File wsFolder = new File(eclipseWorkspaceLocation, "maven-liferay-workspace");

		op.setWorkspaceLocation(wsFolder.getAbsolutePath());

		op.setProvisionLiferayBundle(true);

		String bundleUrl = op.getBundleUrl().content(true);

		Assert.assertEquals(
			"https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5" +
				"/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip",
			bundleUrl);

		op.setServerName("test-bundle");

		Status validationStatus = op.validation();

		Assert.assertTrue(validationStatus.ok());

		op.execute(new ProgressMonitor());

		File bundleDir = new File(wsFolder, "bundles");

		Assert.assertTrue(bundleDir.exists());

		IServer server = ServerUtil.getServer(op.getServerName().content());

		Assert.assertTrue(ServerUtil.isLiferayRuntime(server));

		IProject project = CoreUtil.getProject("maven-liferay-workspace");

		Assert.assertTrue(project.exists());

		op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		Assert.assertEquals(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg, op.validation().message());

		project = CoreUtil.getProject("maven-liferay-workspace");

		project.delete(true, true, new NullProgressMonitor());
	}

	@Test
	public void testImportMavenLiferayWorkspaceInitBundleSetUrl() throws Exception {
		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		URL wsZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/maven-liferay-workspace-bundleUrl.zip");

		String bundleUrl =
			"https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5" +
				"/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip";
		String existUrl = "http://www.example.com/";
		String emailConfig = "<emailAddress>example@liferay.com</emailAddress>";
		String projectName = "maven-liferay-workspace";

		File wsZipFile = new File(FileLocator.toFileURL(wsZipUrl).getFile());

		IPath workspaceLocation = CoreUtil.getWorkspaceRoot().getLocation();

		String projectLocation = workspaceLocation.append(projectName).toPortableString();

		ZipUtil.unzip(wsZipFile, workspaceLocation.toFile());

		File wsFolder = new File(workspaceLocation.toFile(), projectName);

		File pomFile = new File(projectLocation, "pom.xml");

		Assert.assertTrue(pomFile.exists());

		String content = FileUtil.readContents(pomFile);

		Assert.assertTrue(content.contains(existUrl));
		Assert.assertTrue(content.contains(emailConfig));

		op.setWorkspaceLocation(wsFolder.getAbsolutePath());
		op.setProvisionLiferayBundle(true);

		String defaultUrl = op.getBundleUrl().content(true);

		Assert.assertEquals(existUrl, defaultUrl);

		op.setBundleUrl(bundleUrl);

		op.execute(new ProgressMonitor());

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

		URL wsZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/maven-liferay-workspace.zip");

		File wsZipFile = new File(FileLocator.toFileURL(wsZipUrl).getFile());

		File eclipseWorkspaceLocation = CoreUtil.getWorkspaceRootLocation().toFile();

		ZipUtil.unzip(wsZipFile, eclipseWorkspaceLocation);

		File wsFolder = new File(eclipseWorkspaceLocation, "maven-liferay-workspace");

		op.setWorkspaceLocation(wsFolder.getAbsolutePath());

		Status validationStatus = op.validation();

		Assert.assertTrue(validationStatus.ok());

		op.execute(new ProgressMonitor());

		IProject project = CoreUtil.getProject("maven-liferay-workspace");

		Assert.assertTrue(project.exists());

		project = CoreUtil.getProject("maven-liferay-workspace-modules");

		Assert.assertTrue(project.exists());

		project = CoreUtil.getProject("maven-liferay-workspace-themes");

		Assert.assertTrue(project.exists());

		project = CoreUtil.getProject("maven-liferay-workspace-wars");

		Assert.assertTrue(project.exists());

		op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		Assert.assertEquals(LiferayWorkspaceUtil.hasLiferayWorkspaceMsg, op.validation().message());

		project = CoreUtil.getProject("maven-liferay-workspace");

		project.delete(true, true, new NullProgressMonitor());
	}

}