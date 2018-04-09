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

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.LiferayGradleProject;
import com.liferay.ide.gradle.core.parser.GradleDependency;
import com.liferay.ide.gradle.core.parser.GradleDependencyUpdater;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;

import java.io.File;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class GradleProjectTests {

	@BeforeClass
	public static void deleteAllWorkspaceProjects() throws Exception {
		Util.deleteAllWorkspaceProjects();
	}

	@Test
	public void getOutputJar() throws Exception {
		LiferayGradleProject gradleProject = Util.fullImportGradleProject("projects/getOutputJar");

		Assert.assertNotNull(gradleProject);

		NullProgressMonitor monitor = new NullProgressMonitor();

		IPath outputJar = gradleProject.getOutputBundle(false, monitor);

		Assert.assertNotNull(outputJar);

		File file = outputJar.toFile();

		if (file.exists()) {
			file.delete();
		}

		Assert.assertTrue(!file.exists());

		outputJar = gradleProject.getOutputBundle(true, monitor);

		Assert.assertTrue(FileUtil.exists(outputJar));
	}

	@Test
	public void getSymbolicName() throws Exception {
		LiferayGradleProject gradleProject = Util.fullImportGradleProject("projects/getSymbolicName");

		Assert.assertNotNull(gradleProject);

		NullProgressMonitor monitor = new NullProgressMonitor();

		IPath outputJar = gradleProject.getOutputBundle(false, monitor);

		if (FileUtil.exists(outputJar)) {
			outputJar = gradleProject.getOutputBundle(true, monitor);
		}

		Assert.assertTrue(FileUtil.exists(outputJar));

		Assert.assertEquals("com.liferay.test.bsn", gradleProject.getSymbolicName());
	}

	@Test
	public void hasGradleBundlePluginDetection() throws Exception {
		LiferayGradleProject gradleProject = Util.fullImportGradleProject("projects/biz.aQute.bundle");

		Assert.assertNotNull(gradleProject);

		IBundleProject[] bundleProject = new IBundleProject[1];

		WorkspaceJob job = new WorkspaceJob("") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				bundleProject[0] = LiferayCore.create(IBundleProject.class, gradleProject.getProject());

				return Status.OK_STATUS;
			}

		};

		job.schedule(5000);
		job.join();

		Assert.assertNotNull(bundleProject[0]);

		Assert.assertEquals(LiferayGradleProject.class, bundleProject[0].getClass());
	}

	@Test
	public void testAddGradleDependency() throws Exception {
		LiferayGradleProject gradleProject = Util.fullImportGradleProject("projects/GradleDependencyTestProject");
		String[][] gradleDependencies = {{"com.liferay.portal", "com.liferay.portal.kernel", "2.6.0"}};

		GradleDependency gd = new GradleDependency(
			gradleDependencies[0][0], gradleDependencies[0][1], gradleDependencies[0][2]);

		Assert.assertNotNull(gradleProject);

		IProject project = gradleProject.getProject();

		IFile gradileFile = project.getFile("build.gradle");

		GradleDependencyUpdater updater = new GradleDependencyUpdater(FileUtil.getFile(gradileFile));

		List<GradleDependency> existDependencies = updater.getAllDependencies();

		Assert.assertFalse(existDependencies.contains(gd));

		IProjectBuilder gradleProjectBuilder = gradleProject.adapt(IProjectBuilder.class);

		gradleProjectBuilder.updateProjectDependency(project, Arrays.asList(gradleDependencies));

		GradleDependencyUpdater dependencyUpdater = new GradleDependencyUpdater(FileUtil.getFile(gradileFile));

		List<GradleDependency> updatedDependencies = dependencyUpdater.getAllDependencies();

		Assert.assertTrue(updatedDependencies.contains(gd));
	}

	@Test
	public void testThemeProjectPluginDetection() throws Exception {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("gradle-theme-test");
		op.setProjectProvider("gradle-module");
		op.setProjectTemplateName("theme");

		op.execute(ProgressMonitorBridge.create(new NullProgressMonitor()));

		IProject project = CoreUtil.getProject("gradle-theme-test");

		Assert.assertNotNull(project);

		Util.waitForBuildAndValidation();

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

		Assert.assertNotNull(bundleProject);
	}

	@Test
	public void toolingApiCustomModel() throws Exception {
		LiferayGradleProject gradleProject = Util.fullImportGradleProject("projects/customModel");

		Assert.assertNotNull(gradleProject);

		CustomModel customModel = GradleCore.getToolingModel(CustomModel.class, gradleProject.getProject());

		Assert.assertNotNull(customModel);

		Assert.assertFalse(customModel.hasPlugin("not.a.plugin"));

		Assert.assertTrue(customModel.hasPlugin("org.dm.gradle.plugins.bundle.BundlePlugin"));
	}

}