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

import java.io.File;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.modeling.Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class NewLiferayJSFModuleProjectOpTests {

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

		IProject project = MavenTestUtil.create(op);

		Assert.assertNotNull(project);
	}

	@Test
	public void testIcefacesGradleJSFProject() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("gradle-jsf");
		op.setProjectName("Test15");
		op.setTemplateName("icefaces");

		IProject project = MavenTestUtil.create(op);

		Assert.assertNotNull(project);
	}

	@Test
	public void testMavenICEFacesNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Testq10");
		existedProjectop.setTemplateName("alloy");

		IProject existedProject = MavenTestUtil.create(existedProjectop);

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testMavenJSFStandardNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Test11");
		existedProjectop.setTemplateName("jsf");

		IProject existedProject = MavenTestUtil.create(existedProjectop);

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testMavenLiferayFacesNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Test9");
		existedProjectop.setTemplateName("icefaces");

		IProject existedProject = MavenTestUtil.create(existedProjectop);

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testMavenPrimeFacesNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Test8");
		existedProjectop.setTemplateName("primefaces");

		IProject existedProject = MavenTestUtil.create(existedProjectop);

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testMavenRichFacesNewLiferayJSFModuleProjectOp() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectProvider("maven-jsf");
		existedProjectop.setProjectName("Test7");
		existedProjectop.setTemplateName("richfaces");

		IProject existedProject = MavenTestUtil.create(existedProjectop);

		Assert.assertNotNull(existedProject);
	}

	@Test
	public void testNewLiferayJSFModuleProjectOpDefaultBuildType() throws Exception {
		NewLiferayJSFModuleProjectOp existedProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		existedProjectop.setProjectName("Test3");
		existedProjectop.setProjectProvider("gradle-jsf");
		existedProjectop.setTemplateName("primefaces");

		IProject existedProject = MavenTestUtil.create(existedProjectop);

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

		IProject existedProject = MavenTestUtil.create(existedProjectop);

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

		IProject mavenProject = MavenTestUtil.create(mavenProjectop);

		Assert.assertNotNull(mavenProject);

		NewLiferayJSFModuleProjectOp graldeProjectop = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		graldeProjectop.setProjectProvider("gradle-jsf");
		graldeProjectop.setProjectName("Test5");

		mavenProjectop.setTemplateName("alloy");

		IProject gradleProject = MavenTestUtil.create(graldeProjectop);

		Assert.assertNotNull(gradleProject);
	}

	@Test
	public void testNewLiferayJSFWarWorkspaceMavenJsfProject() throws Exception {
		_importWorkspaceProject("testWorkspace");

		MavenTestUtil.waitForJobsToComplete();

		IProject workspaceProject = CoreUtil.getProject("testWorkspace");

		Assert.assertNotNull(workspaceProject);

		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("maven-jsf");
		op.setProjectName("Test13");
		op.setTemplateName("jsf");

		IPath location = workspaceProject.getLocation();

		Assert.assertNotNull(location);

		op.setLocation(location.append("wars").toOSString());

		IProject project = MavenTestUtil.create(op);

		Assert.assertNotNull(project);
	}

	@Test
	public void testPrimefacesGradleJSFProject() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("gradle-jsf");
		op.setProjectName("Test16");
		op.setTemplateName("primefaces");

		IProject project = MavenTestUtil.create(op);

		Assert.assertNotNull(project);
	}

	@Test
	public void testRichfacesGradleProject() throws Exception {
		NewLiferayJSFModuleProjectOp op = NewLiferayJSFModuleProjectOp.TYPE.instantiate();

		op.setProjectProvider("gradle-jsf");
		op.setProjectName("Test17");
		op.setTemplateName("richfaces");

		IProject project = MavenTestUtil.create(op);

		Assert.assertNotNull(project);
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