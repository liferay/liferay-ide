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
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOp;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andy Wu
 */
public class ImportLiferayModuleProjectOpTest {

	@Before
	public void clearWorkspace() throws Exception {
		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testImportGradleModuleProject() throws Exception {
		String projectName = "testGradleProject";

		String projectLocation = _unzipFile(projectName);

		ImportLiferayModuleProjectOp importOp = ImportLiferayModuleProjectOp.TYPE.instantiate();

		importOp.setLocation(projectLocation);

		Assert.assertEquals("gradle", importOp.getBuildType().text(false));
		Assert.assertTrue(importOp.validation().ok());
		Assert.assertTrue(importOp.execute(ProgressMonitorBridge.create(new NullProgressMonitor())).ok());

		MavenTestUtil.waitForJobsToComplete();

		IProject project = ProjectUtil.getProject(projectName);

		Assert.assertTrue(project.exists());

		importOp = ImportLiferayModuleProjectOp.TYPE.instantiate();

		importOp.setLocation(projectLocation);

		Assert.assertEquals("A project with that name already exists.", importOp.validation().message());

		projectLocation = _unzipFile("gradle-liferay-workspace");

		importOp.setLocation(projectLocation);

		Assert.assertEquals(
			"Can't import Liferay Workspace, please use Import Liferay Workspace Project wizard.",
			importOp.validation().message());
	}

	@Test
	public void testImportMavenModuleProject() throws Exception {
		String projectName = "testMavenProjects";

		String projectLocation = _unzipFile(projectName);

		ImportLiferayModuleProjectOp importOp = ImportLiferayModuleProjectOp.TYPE.instantiate();

		importOp.setLocation(projectLocation);

		Assert.assertEquals("maven", importOp.getBuildType().text(false));
		Assert.assertTrue(importOp.validation().ok());
		Assert.assertTrue(importOp.execute(ProgressMonitorBridge.create(new NullProgressMonitor())).ok());

		IProject project = ProjectUtil.getProject(projectName);

		Assert.assertTrue(project.exists());

		importOp = ImportLiferayModuleProjectOp.TYPE.instantiate();

		importOp.setLocation(projectLocation);

		Assert.assertEquals("A project with that name already exists.", importOp.validation().message());

		projectLocation = _unzipFile("maven-liferay-workspace");

		importOp.setLocation(projectLocation);

		Assert.assertEquals(
			"Can't import Liferay Workspace, please use Import Liferay Workspace Project wizard.",
			importOp.validation().message());
	}

	private String _unzipFile(String fileName) throws Exception {
		URL projectZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/" + fileName + ".zip");

		File projectZipFile = new File(FileLocator.toFileURL(projectZipUrl).getFile());

		IPath stateLocation = LiferayMavenCore.getDefault().getStateLocation();

		File targetFolder = new File(stateLocation.toFile(), fileName);

		if (targetFolder.exists()) {
			targetFolder.delete();
		}

		ZipUtil.unzip(projectZipFile, stateLocation.toFile());

		Assert.assertTrue(targetFolder.exists());

		return targetFolder.getAbsolutePath();
	}

}