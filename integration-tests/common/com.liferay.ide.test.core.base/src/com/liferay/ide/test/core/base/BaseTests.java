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
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.test.core.base.support.FileSupport;
import com.liferay.ide.test.core.base.support.ImportProjectSupport;
import com.liferay.ide.test.core.base.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Properties;

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
import org.eclipse.sapphire.Value;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;

import org.junit.Assert;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class BaseTests {

	protected static void failTest(Exception exception) {
		StringWriter s = new StringWriter();

		exception.printStackTrace(new PrintWriter(s));

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

	protected IBundleProject assertBundleProject(String projectName) {
		IProject project = project(projectName);

		assertProjectExists(project);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		Assert.assertNotNull("Expected project " + project + " is IBundleProject", bundleProject);

		return bundleProject;
	}

	protected void assertComponentValue(Value<?> component, String expectedValue) {
		Assert.assertEquals(component.content(), expectedValue);
	}

	protected void assertFileContains(FileSupport fs, String expectedContent) {
		File file = fs.getFile();

		String content = FileUtil.readContents(file);

		Assert.assertTrue(
			"Expected file " + file + " contains " + expectedContent + " but now content is " + content,
			content.contains(expectedContent));
	}

	protected void assertFileExists(File file) {
		Assert.assertTrue("Expected file " + file + " exists", FileUtil.exists(file));
	}

	protected void assertFileExists(IFile file) {
		try {
			file.refreshLocal(0, npm);
		}
		catch (CoreException coreException) {
		}

		Assert.assertTrue("Expected file " + file.getLocation() + " exists", FileUtil.exists(file.getLocation()));
	}

	protected void assertFileExists(IPath path) {
		Assert.assertNotNull("Expected path not be null", path);

		Assert.assertTrue("Expected path " + path + " exists", FileUtil.exists(path));
	}

	protected void assertFileNotContains(FileSupport fs, String expectedContent) {
		String content = FileUtil.readContents(fs.getFile());

		Assert.assertFalse(content.contains(expectedContent));
	}

	protected void assertFileNotExists(File file) {
		Assert.assertTrue("Expected file " + file + " not exists", FileUtil.notExists(file));
	}

	protected void assertFileNotExists(IFile file) {
		Assert.assertTrue(FileUtil.notExists(file));
	}

	protected void assertFileNotExists(IPath path) {
		Assert.assertTrue(FileUtil.notExists(path));
	}

	protected void assertFileSuffix(IFile file, String expectedSuffix) {
		String name = file.getName();

		Assert.assertTrue(name.endsWith(expectedSuffix));
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

	protected void assertLiferayServerExists(String serverName) {
		Assert.assertTrue(ServerUtil.isLiferayRuntime(ServerUtil.getServer(serverName)));
	}

	protected void assertLiferayServerNotExists(String serverName) {
		Assert.assertNull(ServerUtil.getServer(serverName));
	}

	protected void assertNotLiferayProject(String projectName) {
		IProject project = project(projectName);

		assertProjectExists(project);

		Assert.assertFalse("Project " + projectName + " has a liferay nature", LiferayNature.hasNature(project));
	}

	protected void assertProjectExists(ImportProjectSupport ips) {
		Assert.assertTrue(FileUtil.exists(project(ips.getName())));
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

		IPath location = file.getLocation();

		String content = FileUtil.readContents(location.toFile());

		Assert.assertTrue(
			"Expected " + filePath + " in " + projectName + " contains content " + expectedContent,
			content.contains(expectedContent));
	}

	protected void assertProjectFileEquals(String projectName, String filePath, String expectedContent) {
		IProject project = project(projectName);

		assertProjectExists(project);

		IFile file = project.getFile(filePath);

		assertFileExists(file);

		IPath location = file.getLocation();

		String content = FileUtil.readContents(location.toFile());

		Assert.assertEquals(expectedContent, content);
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

	protected void assertProjectFolderExists(String projectName, String folderPath) {
		IProject project = project(projectName);

		assertProjectExists(project);

		try {
			project.refreshLocal(0, npm);
		}
		catch (CoreException coreException) {
		}

		IFolder folder = project.getFolder(folderPath);

		Assert.assertTrue("Expected folder " + folder.getLocation() + " exists", FileUtil.exists(folder.getLocation()));
	}

	protected void assertPropertyValue(String projectName, String filePath, String key, String expectedValue) {
		IProject project = project(projectName);

		assertProjectExists(project);

		IFile propertiesFile = project.getFile(filePath);

		IPath location = propertiesFile.getLocation();

		assertFileExists(propertiesFile);

		Properties properties = new Properties();

		try (InputStream in = new FileInputStream(location.toOSString())) {
			properties.load(in);

			String value = properties.getProperty(key);

			Assert.assertEquals(expectedValue, value);
		}
		catch (Exception exception) {
			failTest(exception);
		}
	}

	protected void assertRuntimeExists(String serverName) {
		Assert.assertNotNull(ServerUtil.getRuntime(serverName));
	}

	protected void assertSourceFolders(String projectName, String expectedSourceFolderName) {
		IProject project = project(projectName);

		assertProjectExists(project);

		ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

		IFolder[] srcFolders = liferayProject.getSourceFolders();

		Assert.assertEquals(expectedSourceFolderName, srcFolders[0].getName());
	}

	protected final void deleteProject(ImportProjectSupport ips) {
		deleteProject(ips.getName());
	}

	protected final void deleteProject(IProject project) {
		assertProjectExists(project);

		try {
			project.close(npm);

			project.delete(true, true, npm);
		}
		catch (CoreException coreException) {

			// failTest(coreException);

			coreException.printStackTrace();
		}
	}

	protected final void deleteProject(String projectName) {
		IProject project = project(projectName);

		deleteProject(project);
	}

	protected void deleteRuntime(String runtimeName) {
		IRuntime runtime = ServerUtil.getRuntime(runtimeName);

		try {
			runtime.delete();
		}
		catch (CoreException coreException) {
		}
	}

	protected void deleteServer(String serverName) {
		IServer server = ServerUtil.getServer(serverName);

		try {
			server.delete();
		}
		catch (CoreException coreException) {
		}
	}

	protected String getProjectFileContents(String projectName, String fileName) {
		IProject project = project(projectName);

		assertProjectExists(project);

		return FileUtil.readContents(project.getFile(fileName));
	}

	protected void writeFile(FileSupport fs, Iterable<? extends CharSequence> lines) throws IOException {
		File file = fs.getFile();

		Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
	}

	protected static IProgressMonitor npm = new NullProgressMonitor();

}