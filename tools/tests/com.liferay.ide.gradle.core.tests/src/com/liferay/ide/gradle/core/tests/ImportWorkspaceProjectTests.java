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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.tests.TestUtil;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.LiferayGradleProject;
import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;

import java.io.File;

import java.nio.file.Files;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ImportWorkspaceProjectTests {

	@Before
	public void clearWorkspace() throws Exception {
		Util.deleteAllWorkspaceProjects();
	}

	@Test
	public void testImportLiferayWorkspace() throws Exception {
		LiferayGradleProject rootProject = Util.fullImportGradleProject("projects/testWorkspace");

		Assert.assertNotNull(rootProject);

		Util.waitForBuildAndValidation();

		_assertLiferayProject("jstl.test");
		_assertLiferayProject("roster-api");
		_assertLiferayProject("roster-service");
		_assertLiferayProject("roster-web");
		_assertLiferayProject("sample-portlet");
		_assertLiferayProject("sample-model-listener");
		_assertLiferayProject("sample-theme");
		_assertSourceFolders("sample-theme", "src");
	}

	@Test
	public void testImportLiferayWorkspaceCustomBundleURL() throws Exception {
		File src = new File("projects/testWorkspaceCustomBundleUrl");

		IPath location = CoreUtil.getWorkspaceRootLocation();

		File dst = new File(location.toFile(), src.getName());

		TestUtil.copyDir(src, dst);

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(dst.getAbsolutePath());

		Value<String> bundleUrlValue = op.getBundleUrl();

		String bundleUrl = bundleUrlValue.content(true);

		Assert.assertEquals(
			"https://api.liferay.com/downloads/portal/7.0.10.6/liferay-dxp-digital-enterprise-tomcat-7.0-sp6-20171010144253003.zip",
			bundleUrl);
	}

	@Test
	public void testImportLiferayWorkspaceDontOverrideGradleProperties() throws Exception {
		File src = new File("projects/testWorkspace");

		IPath location = CoreUtil.getWorkspaceRootLocation();

		File dst = new File(location.toFile(), src.getName());

		TestUtil.copyDir(src, dst);

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(dst.getAbsolutePath());
		op.setProvisionLiferayBundle(true);

		op.execute(ProgressMonitorBridge.create(new NullProgressMonitor()));

		IProject eeProject = CoreUtil.getProject("testWorkspace");

		Assert.assertNotNull(eeProject);

		Util.waitForBuildAndValidation();

		_assertNotLiferayProject("testWorkspace");
		_assertLiferayProject("sample-portlet");

		File originalProperities = new File("projects/testWorkspace/gradle.properties");

		IFile file = eeProject.getFile("gradle.properties");

		IPath gradleLocation = file.getLocation();

		File importedProperties = gradleLocation.toFile();

		String originalContent = CoreUtil.readStreamToString(Files.newInputStream(originalProperities.toPath()));
		String importedContent = CoreUtil.readStreamToString(Files.newInputStream(importedProperties.toPath()));

		originalContent = originalContent.replaceAll("\r", "");
		importedContent = importedContent.replaceAll("\r", "");

		Assert.assertEquals(importedContent, originalContent);
	}

	@Test
	public void testImportLiferayWorkspaceEE() throws Exception {
		LiferayGradleProject eeProject = Util.fullImportGradleProject("projects/liferay-workspace-ee");

		Assert.assertNotNull(eeProject);

		Util.waitForBuildAndValidation();

		_assertNotLiferayProject("liferay-workspace-ee");
		_assertNotLiferayProject("aws");
		_assertNotLiferayProject("docker");
		_assertNotLiferayProject("jenkins");
	}

	@Test
	public void testImportLiferayWorkspaceInitBundle() throws Exception {
		File src = new File("projects/testWorkspace");

		IPath location = CoreUtil.getWorkspaceRootLocation();

		File dst = new File(location.toFile(), src.getName());

		TestUtil.copyDir(src, dst);

		ImportLiferayWorkspaceOp op = ImportLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceLocation(dst.getAbsolutePath());
		op.setProvisionLiferayBundle(true);

		Value<String> bundleUrlValue = op.getBundleUrl();

		String bundleUrl = bundleUrlValue.content(true);

		Assert.assertEquals(
			"https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.4-ga5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip",
			bundleUrl);

		NullProgressMonitor monitor = new NullProgressMonitor();

		op.execute(ProgressMonitorBridge.create(monitor));

		Util.waitForBuildAndValidation();

		IProject eeProject = CoreUtil.getProject("testWorkspace");

		Assert.assertNotNull(eeProject);

		eeProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);

		IFolder bundlesFolder = eeProject.getFolder("bundles");

		Assert.assertTrue(bundlesFolder.exists());
	}

	private void _assertLiferayProject(String projectName) {
		IProject project = CoreUtil.getProject(projectName);

		Assert.assertTrue("Project " + projectName + " doesn't exist.", project.exists());
		Assert.assertTrue("Project " + projectName + " doesn't haven liferay nature", LiferayNature.hasNature(project));
	}

	private void _assertNotLiferayProject(String projectName) {
		IProject project = CoreUtil.getProject(projectName);

		Assert.assertTrue("Project " + projectName + " doesn't exist.", project.exists());
		Assert.assertFalse("Project " + projectName + " has a liferay nature", LiferayNature.hasNature(project));
	}

	private void _assertSourceFolders(String projectName, String expectedSourceFolderName) {
		IProject project = CoreUtil.getProject(projectName);

		Assert.assertTrue("Project " + projectName + " doesn't exist.", project.exists());

		ILiferayProject liferayProject = LiferayCore.create(project);

		IFolder[] srcFolders = liferayProject.getSourceFolders();

		Assert.assertEquals(expectedSourceFolderName, srcFolders[0].getName());
	}

}