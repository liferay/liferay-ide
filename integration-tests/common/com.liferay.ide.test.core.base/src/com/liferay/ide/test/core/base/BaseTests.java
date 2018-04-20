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

package com.liferay.ide.test.core.base;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.test.core.base.util.FileUtil;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.junit.Assert;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class BaseTests {

	protected static void failTest(Exception e) {
		StringWriter s = new StringWriter();

		e.printStackTrace(new PrintWriter(s));

		Assert.fail(s.toString());
	}

	protected static IProject project(String name) {
		return workspaceRoot().getProject(name);
	}

	protected static IWorkspace workspace() {
		return ResourcesPlugin.getWorkspace();
	}

	protected static IWorkspaceRoot workspaceRoot() {
		return workspace().getRoot();
	}

	protected void assertBundleProject(String projectName) {
		IProject project = project(projectName);

		assertProjectExists(project);

		Assert.assertNotNull(LiferayCore.create(IBundleProject.class, project));
	}

	protected void assertFileExists(File file) {
		Assert.assertTrue(FileUtil.exists(file));
	}

	protected void assertFileExists(IFile file) {
		try {
			file.refreshLocal(0, npm);
		}
		catch (CoreException ce) {
		}

		Assert.assertTrue(FileUtil.exists(file));
	}

	protected void assertFileExists(IPath path) {
		Assert.assertTrue(FileUtil.exists(path));
	}

	protected void assertFileNotExists(IFile file) {
		Assert.assertTrue(FileUtil.notExists(file));
	}

	protected void assertFileSuffix(IFile file, String expectedSuffix) {
		Assert.assertTrue(file.getName().endsWith(expectedSuffix));
	}

	protected void assertFileSuffix(IPath path, String expectedSuffix) {
		File file = path.toFile();

		String fileName = file.getName();

		Assert.assertTrue(fileName.endsWith(expectedSuffix));
	}

	protected void assertLiferayProject(IProject project) {
		Assert.assertTrue(FileUtil.exists(project));

		Assert.assertTrue(LiferayNature.hasNature(project));
	}

	protected void assertLiferayProject(String projectName) {
		IProject project = project(projectName);

		Assert.assertTrue(FileUtil.exists(project));

		Assert.assertTrue(LiferayNature.hasNature(project));
	}

	protected void assertNotLiferayProject(String projectName) {
		IProject project = project(projectName);

		assertProjectExists(project);

		Assert.assertFalse("Project " + projectName + " has a liferay nature", LiferayNature.hasNature(project));
	}

	protected void assertProjectExists(IProject project) {
		Assert.assertTrue(FileUtil.exists(project));
	}

	protected void assertProjectExists(String projectName) {
		Assert.assertTrue(FileUtil.exists(project(projectName)));
	}

	protected void assertProjectFileContains(String projectName, String filePath, String expectedContent) {
		IProject project = project(projectName);

		assertProjectExists(project);

		IFile file = project.getFile(filePath);

		assertFileExists(file);

		String content = FileUtil.readContents(file.getLocation().toFile());

		Assert.assertTrue(content.contains(expectedContent));
	}

	protected void assertProjectFileExists(String projectName, String filePath) {
		IProject project = project(projectName);

		assertProjectExists(project);

		assertFileExists(project.getFile(filePath));
	}

	protected void assertProjectFileNotExists(String projectName, String filePath) {
		IProject project = project(projectName);

		assertProjectExists(project);

		assertFileNotExists(project.getFile(filePath));
	}

	protected void assertSourceFolders(String projectName, String expectedSourceFolderName) {
		IProject project = project(projectName);

		assertProjectExists(project);

		ILiferayProject liferayProject = LiferayCore.create(project);

		IFolder[] srcFolders = liferayProject.getSourceFolders();

		Assert.assertEquals(expectedSourceFolderName, srcFolders[0].getName());
	}

	protected final void deleteProject(String projectName) {
		IProject project = project(projectName);

		assertProjectExists(project);

		try {
			project.close(npm);

			project.delete(true, true, npm);
		}
		catch (CoreException ce) {
			failTest(ce);
		}
	}

	protected IProgressMonitor npm = new NullProgressMonitor();

}