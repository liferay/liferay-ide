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

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOpMethods;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import java.io.File;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewLiferayJSFModuleProjectOpTests extends ProjectCoreBase {

	@Before
	public void clearWorkspace() throws Exception {
		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, new NullProgressMonitor());
		}
	}

	@Test
	public void testAlloyGradleJSFProject() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("gradle-jsf");
		op.setProjectName("Test14");
		op.setTemplateName("alloy");

		Status status = NewLiferayJSFModuleProjectOpMethods.execute(
			op, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(status.ok());

		_waitForBuildAndValidation2();

		IProject project = CoreUtil.getProject("Test14");

		Assert.assertNotNull(project);
	}

	@Test
	public void testIcefacesGradleJSFProject() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("gradle-jsf");
		op.setProjectName("Test15");
		op.setTemplateName("icefaces");

		Status status = NewLiferayJSFModuleProjectOpMethods.execute(
			op, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(status.ok());

		_waitForBuildAndValidation2();

		IProject project = CoreUtil.getProject("Test15");

		Assert.assertNotNull(project);
	}

	@Test
	public void testMavenICEFacesNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Testq10");
		existedProjectop.setTemplateName("alloy");

		Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			existedProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(exStatus.ok());

		IProject existedProject = CoreUtil.getProject(existedProjectop.getProjectName().content());

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testMavenJSFStandardNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Test11");
		existedProjectop.setTemplateName("jsf");

		Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			existedProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(exStatus.ok());

		IProject existedProject = CoreUtil.getProject(existedProjectop.getProjectName().content());

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testMavenLiferayFacesNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Test9");
		existedProjectop.setTemplateName("icefaces");

		Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			existedProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(exStatus.ok());

		IProject existedProject = CoreUtil.getProject(existedProjectop.getProjectName().content());

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testMavenPrimeFacesNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Test8");
		existedProjectop.setTemplateName("primefaces");

		Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			existedProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(exStatus.ok());

		IProject existedProject = CoreUtil.getProject(existedProjectop.getProjectName().content());

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testMavenRichFacesNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Test7");
		existedProjectop.setTemplateName("richfaces");

		Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			existedProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(exStatus.ok());

		IProject existedProject = CoreUtil.getProject(existedProjectop.getProjectName().content());

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testNewLiferayJSFModuleProjectOpDefaultBuildType() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectName("Test3");
		existedProjectop.setProjectProvider("gradle-jsf");
		existedProjectop.setTemplateName("primefaces");

		Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			existedProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(exStatus.ok());

		IProject existedProject = CoreUtil.getProject(existedProjectop.getProjectName().content());

		Assert.assertNotNull(existedProject);

		NewLiferayJSFModuleProjectOp newBuildTypeop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		DefaultValueService buildTypeDefaultService =
			newBuildTypeop.getProjectProvider().service(DefaultValueService.class);

		Assert.assertEquals(buildTypeDefaultService.value(), "gradle-jsf");
	}

	@Test
	public void testNewLiferayJSFModuleProjectOpProjectExisted() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("gradle-jsf");
		existedProjectop.setProjectName("Test2");
		existedProjectop.setTemplateName("richfaces");

		Status exStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			existedProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(exStatus.ok());

		IProject existedProject = CoreUtil.getProject(existedProjectop.getProjectName().content());

		Assert.assertNotNull(existedProject);

		NewLiferayJSFModuleProjectOp newProjectNameop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		newProjectNameop.setProjectProvider("gradle-jsf");
		newProjectNameop.setProjectName("Test2");

		Status projectNameExistedValidationStatus = newProjectNameop.getProjectName().validation();

		Assert.assertEquals(false, projectNameExistedValidationStatus.ok());
	}

	@Test
	public void testNewLiferayJSFModuleProjectOpProjectName() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("maven-jsf");
		op.setProjectName("Test1");

		Status projectNameOkValidationStatus1 = op.getProjectName().validation();

		Assert.assertEquals(projectNameOkValidationStatus1.message(), "ok");

		op.setProjectProvider("maven-jsf");
		op.setProjectName("#Test1");

		Status projectNameErrorValidationStatus = op.getProjectName().validation();

		Assert.assertEquals(projectNameErrorValidationStatus.message(), "The project name is invalid.");

		op.setProjectProvider("maven-jsf");
		op.setProjectName("Test1_Abc");

		Status projectNameOkValidationStatus2 = op.getProjectName().validation();

		Assert.assertEquals(projectNameOkValidationStatus2.message(), "ok");
	}

	@Test
	public void testNewLiferayJSFModuleStandardAloneProject() throws Exception {
		NewLiferayJSFModuleProjectOp mavenProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		mavenProjectop.setProjectProvider("gradle-jsf");
		mavenProjectop.setProjectName("Test4");
		mavenProjectop.setTemplateName("icefaces");

		Status mavenProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			mavenProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(mavenProjectStatus.ok());

		IProject mavenProject = CoreUtil.getProject("Test4");

		Assert.assertNotNull(mavenProject);

		NewLiferayJSFModuleProjectOp graldeProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		graldeProjectop.setProjectProvider("gradle-jsf");
		graldeProjectop.setProjectName("Test5");

		mavenProjectop.setTemplateName("alloy");

		Status gradleProjectStatus = NewLiferayJSFModuleProjectOpMethods.execute(
			graldeProjectop, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(gradleProjectStatus.ok());

		IProject gradleProject = CoreUtil.getProject("Test5");

		Assert.assertNotNull(gradleProject);
	}

	@Test
	public void testNewLiferayJSFWarWorkspaceMavenJsfProject() throws Exception {
		_importWorkspaceProject("testWorkspace");

		IProject workspaceProject = CoreUtil.getProject("testWorkspace");

		Assert.assertNotNull(workspaceProject);

		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("maven-jsf");
		op.setProjectName("Test13");
		op.setTemplateName("jsf");

		IPath location = workspaceProject.getLocation();

		Assert.assertNotNull(location);

		op.setLocation(location.append("wars").toOSString());

		Status status = NewLiferayJSFModuleProjectOpMethods.execute(
			op, ProgressMonitorBridge.create(new NullProgressMonitor()));

		_waitForBuildAndValidation2();

		Assert.assertTrue(status.ok());

		IProject project = CoreUtil.getProject("Test13");

		Assert.assertNotNull(project);
	}

	@Test
	public void testPrimefacesGradleJSFProject() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("gradle-jsf");
		op.setProjectName("Test16");
		op.setTemplateName("primefaces");

		Status status = NewLiferayJSFModuleProjectOpMethods.execute(
			op, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(status.ok());

		_waitForBuildAndValidation2();

		IProject project = CoreUtil.getProject("Test16");

		Assert.assertNotNull(project);
	}

	@Test
	public void testRichfacesGradleProject() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("gradle-jsf");
		op.setProjectName("Test17");
		op.setTemplateName("richfaces");

		Status status = NewLiferayJSFModuleProjectOpMethods.execute(
			op, ProgressMonitorBridge.create(new NullProgressMonitor()));

		Assert.assertTrue(status.ok());

		_waitForBuildAndValidation2();

		IProject project = CoreUtil.getProject("Test17");

		Assert.assertNotNull(project);
	}

	@Override
	protected void waitForBuildAndValidation(IProject project) throws Exception {
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());

		_waitForBuildAndValidation2();

		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());

		_waitForBuildAndValidation2();
	}

	private static void _waitForBuildAndValidation2() throws Exception {
		IWorkspaceRoot root = null;

		try {
			ResourcesPlugin.getWorkspace().checkpoint(true);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			Job.getJobManager().join(ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			Thread.sleep(800);
			Job.getJobManager().beginRule(root = ResourcesPlugin.getWorkspace().getRoot(), null);
		}
		catch (InterruptedException ie) {
			failTest(ie);
		}
		catch (IllegalArgumentException iae) {
			failTest(iae);
		}
		catch (OperationCanceledException oce) {
			failTest(oce);
		}
		finally {
			if (root != null) {
				Job.getJobManager().endRule(root);
			}
		}
	}

	private void _importWorkspaceProject(String name) throws Exception {
		URL projectZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/" + name + ".zip");

		File projectZipFile = new File(FileLocator.toFileURL(projectZipUrl).getFile());

		IPath wordkspaceProjectLocation = CoreUtil.getWorkspaceRoot().getLocation();

		ZipUtil.unzip(projectZipFile, wordkspaceProjectLocation.toFile());

		IPath projectFolder = wordkspaceProjectLocation.append(name);

		Assert.assertEquals(true, projectFolder.toFile().exists());

		ILiferayProjectImporter gradleImporter = LiferayCore.getImporter("gradle");

		gradleImporter.importProjects(projectFolder.toOSString(), new NullProgressMonitor());

		IProject workspaceProject = CoreUtil.getProject("testWorkspace");

		Assert.assertNotNull(workspaceProject);
	}

}