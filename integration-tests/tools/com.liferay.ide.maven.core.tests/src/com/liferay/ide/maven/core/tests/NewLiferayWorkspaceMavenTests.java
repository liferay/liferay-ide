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

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.workspace.WorkspaceConstants;
import com.liferay.ide.maven.core.tests.util.MavenTestUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.test.core.base.support.LiferayWorkspaceSupport;
import com.liferay.ide.test.project.core.base.ProjectOpBase;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Andy Wu
 * @author Joye Luo
 * @author Simon Jiang
 * @author Ashley Yuan
 */
public class NewLiferayWorkspaceMavenTests extends ProjectOpBase<NewLiferayWorkspaceOp> {

	@Test
	public void createLiferayWorkspace() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setWorkspaceName(workspace.getName());
		op.setProductVersion("portal-7.3-ga4");

		waitForBuildAndValidation();

		op.setProjectProvider(provider());
		op.setLiferayVersion("7.3");

		createOrImportAndBuild(op, workspace.getName());

		waitForBuildAndValidation();

		deleteProject(workspace.getName());
	}

	@After
	public void deleteWorksapceProject() throws CoreException {
		IProgressMonitor monitor = new NullProgressMonitor();

		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, monitor);
		}
	}

	@Test
	public void testNewLiferayMavenWorkspaceOpBundleUrlDependendOnTaregetPlatform() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		String projectName = "test-liferay-maven-workspace-new";

		op.setProjectProvider("maven-liferay-workspace");
		op.setWorkspaceName(projectName);
		op.setUseDefaultLocation(true);
		op.setLiferayVersion("7.1");
		op.setTargetPlatform("7.1.2");

		createOrImportAndBuild(op, workspace.getName());

		waitForBuildAndValidation();

		IProject workspaceProject = CoreUtil.getProject(workspace.getName());

		MavenTestUtil.waitForJobsToComplete();

		IPath wsLocation = workspaceProject.getLocation();

		IPath pomFilePath = wsLocation.append("pom.xml");

		File pomFile = pomFilePath.toFile();

		Assert.assertTrue(pomFile.exists());

		String content = FileUtil.readContents(pomFile);

		Assert.assertTrue(
			content.contains(
				"https://releases-cdn.liferay.com/portal/7.1.2-ga3" +
					"/liferay-ce-portal-tomcat-7.1.2-ga3-20190107144105508.tar.gz"));
	}

	@Test
	public void testNewLiferayWorkspaceOpWithInvalidBundleUrl() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setProductVersion("portal-7.3-ga4");

		waitForBuildAndValidation();

		op.setWorkspaceName(workspace.getName());

		op.setProjectProvider(provider());
		op.setUseDefaultLocation(true);
		op.setProvisionLiferayBundle(true);
		op.setBundleUrl("https://issues.liferay.com/browse/IDE-3605");

		op.setServerName(workspace.getName());

		createOrImportAndBuild(op, workspace.getName());

		waitForBuildAndValidation();

		IProject workspaceProject = CoreUtil.getProject(workspace.getName());

		assertProjectExists(workspaceProject);

		assertProjectFileNotExists(workspaceProject.getName(), "bundles");

		assertLiferayServerNotExists(workspace.getName());

		deleteProject(workspace.getName());
	}

	@Test
	public void testNewMavenLiferayWorkspaceInitBundle() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setProductVersion("portal-7.3-ga4");

		waitForBuildAndValidation();

		String defaultBundleUrl = WorkspaceConstants.BUNDLE_URL_CE_7_2;

		IPath rootLocation = CoreUtil.getWorkspaceRootLocation();

		op.setWorkspaceName(workspace.getName());
		op.setUseDefaultLocation(false);
		op.setLocation(rootLocation.toPortableString());
		op.setProjectProvider(provider());
		op.setProvisionLiferayBundle(true);
		op.setLiferayVersion("7.2");

		String bundleUrl = _getter.get(op.getBundleUrl());

		Assert.assertEquals(defaultBundleUrl, bundleUrl);

		createOrImportAndBuild(op, workspace.getName());

		MavenTestUtil.waitForJobsToComplete();

		waitForBuildAndValidation();

		IPath fullLocation = rootLocation.append(workspace.getName());

		assertProjectFileExists(workspace.getName(), "bundles");

		File pomFile = new File(fullLocation.toFile(), "pom.xml");

		String content = FileUtil.readContents(pomFile);

		Assert.assertTrue(content.contains("com.liferay.portal.tools.bundle.support"));

		assertLiferayServerExists(workspace.getName());

		deleteServer(workspace.getName());

		deleteProject(workspace.getName());
	}

	@Test
	public void testNewMavenLiferayWorkspaceOpWithBundle70() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setProductVersion("portal-7.3-ga4");

		waitForBuildAndValidation();

		IWorkspaceRoot wsRoot = CoreUtil.getWorkspaceRoot();

		IPath workspaceLocation = wsRoot.getLocation();

		op.setWorkspaceName(workspace.getName());
		op.setLiferayVersion("7.0");
		op.setProjectProvider(provider());
		op.setLocation(workspaceLocation.toPortableString());

		createOrImportAndBuild(op, workspace.getName());

		waitForBuildAndValidation();

		IProject workspaceProject = CoreUtil.getProject(workspace.getName());

		MavenTestUtil.waitForJobsToComplete();

		IPath wsLocation = workspaceProject.getLocation();

		IPath pomFilePath = wsLocation.append("pom.xml");

		File pomFile = pomFilePath.toFile();

		Assert.assertTrue(pomFile.exists());

		String xml = FileUtil.readContents(pomFile);

		Assert.assertTrue(xml.contains(WorkspaceConstants.BUNDLE_URL_CE_7_0));

		assertLiferayServerNotExists(workspace.getName());

		deleteProject(workspace.getName());
	}

	@Test
	public void testNewMavenLiferayWorkspaceOpWithBundle71() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setProductVersion("portal-7.3-ga4");

		waitForBuildAndValidation();

		IWorkspaceRoot wsRoot = CoreUtil.getWorkspaceRoot();

		IPath workspaceLocation = wsRoot.getLocation();

		op.setWorkspaceName(workspace.getName());
		op.setLiferayVersion("7.1");
		op.setProjectProvider(provider());
		op.setLocation(workspaceLocation.toPortableString());

		createOrImportAndBuild(op, workspace.getName());

		IProject workspaceProject = CoreUtil.getProject(workspace.getName());

		MavenTestUtil.waitForJobsToComplete();

		IPath wsLocation = workspaceProject.getLocation();

		IPath pomFilePath = wsLocation.append("pom.xml");

		File pomFile = pomFilePath.toFile();

		Assert.assertTrue(pomFile.exists());

		String xml = FileUtil.readContents(pomFile);

		Assert.assertTrue(xml.contains(WorkspaceConstants.BUNDLE_URL_CE_7_1));

		assertLiferayServerNotExists(workspace.getName());

		waitForBuildAndValidation();

		deleteProject(workspace.getName());
	}

	@Test
	public void testNewMavenLiferayWorkspaceSetUrl() throws Exception {
		NewLiferayWorkspaceOp op = NewLiferayWorkspaceOp.TYPE.instantiate();

		op.setProductVersion("portal-7.3-ga4");

		waitForBuildAndValidation();

		String bundleUrl = WorkspaceConstants.BUNDLE_URL_CE_7_0;

		IWorkspaceRoot wsRoot = CoreUtil.getWorkspaceRoot();

		IPath workspaceLocation = wsRoot.getLocation();

		op.setWorkspaceName(workspace.getName());
		op.setUseDefaultLocation(false);
		op.setLocation(workspaceLocation.toPortableString());
		op.setProjectProvider(provider());
		op.setProvisionLiferayBundle(true);
		op.setBundleUrl(bundleUrl);

		createOrImportAndBuild(op, workspace.getName());

		MavenTestUtil.waitForJobsToComplete();

		IProject workspaceProject = CoreUtil.getProject(workspace.getName());

		IPath wsLocation = workspaceProject.getLocation();

		IPath pomFilePath = wsLocation.append("pom.xml");

		File pomFile = pomFilePath.toFile();

		Assert.assertTrue(pomFile.exists());

		IPath bundlePath = wsLocation.append("bundles");

		File bundleFile = bundlePath.toFile();

		Assert.assertTrue(bundleFile.exists());

		String content = FileUtil.readContents(pomFile);

		Assert.assertEquals(content.contains(bundleUrl), true);

		assertLiferayServerExists(workspace.getName());

		waitForBuildAndValidation();

		deleteServer(workspace.getName());

		deleteProject(workspace.getName());
	}

	@Rule
	public LiferayWorkspaceSupport workspace = new LiferayWorkspaceSupport();

	@Override
	protected void needJobsToBuild(IJobManager manager) throws InterruptedException, OperationCanceledException {
		manager.join(LiferayCore.LIFERAY_JOB_FAMILY, new NullProgressMonitor());
	}

	@Override
	protected String provider() {
		return "maven-liferay-workspace";
	}

	protected void verifyProjectFiles(String projectName) {
		assertProjectFileNotExists(projectName, "build.gradle");
		assertProjectFileExists(projectName, "pom.xml");
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

}