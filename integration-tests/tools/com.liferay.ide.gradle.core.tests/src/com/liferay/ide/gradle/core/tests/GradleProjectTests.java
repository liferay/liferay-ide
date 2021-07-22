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

import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.core.LiferayGradleProject;
import com.liferay.ide.gradle.core.model.GradleBuildScript;
import com.liferay.ide.gradle.core.model.GradleDependency;
import com.liferay.ide.gradle.core.tests.util.GradleTestUtil;
import com.liferay.ide.test.core.base.support.ImportProjectSupport;
import com.liferay.ide.test.project.core.base.ProjectBase;

import java.io.File;

import java.nio.file.Files;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class GradleProjectTests extends ProjectBase {

	@Test
	public void getOutputJar() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("get-output-jar");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		IPath outputJar = gradleProject.getOutputBundle(false, npm);

		assertFileExists(outputJar);

		File file = outputJar.toFile();

		Files.deleteIfExists(file.toPath());

		assertFileNotExists(outputJar);

		outputJar = gradleProject.getOutputBundle(true, npm);

		assertFileExists(outputJar);

		deleteProject(ips);
	}

	@Test
	public void getSymbolicName() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("get-symbolic-name");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		Assert.assertEquals("com.liferay.test.bsn", gradleProject.getSymbolicName());

		deleteProject(ips);
	}

	@Test
	public void hasGradleBundlePluginDetection() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("test-gradle");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, gradleProject.getProject());

		Assert.assertNotNull(bundleProject);

		Assert.assertEquals(LiferayGradleProject.class, bundleProject.getClass());

		deleteProject(ips);
	}

	@Test
	public void testAddGradleDependency() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("test-gradle-dependency");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		GradleDependency gradleDependency = new GradleDependency(
			"compile", "com.liferay.portal", "com.liferay.portal.kernel", "2.6.0", -1, -1);

		IProject project = gradleProject.getProject();

		IFile gradileFile = project.getFile("build.gradle");

		GradleBuildScript gradleBuildScript = new GradleBuildScript(FileUtil.getFile(gradileFile));

		List<GradleDependency> existingDependencies = gradleBuildScript.getDependencies();

		Assert.assertFalse(existingDependencies.contains(gradleDependency));

		IProjectBuilder gradleProjectBuilder = gradleProject.adapt(IProjectBuilder.class);

		Artifact artifact = new Artifact("com.liferay.portal", "com.liferay.portal.kernel", "2.6.0", "compile", null);

		gradleProjectBuilder.updateDependencies(project, Arrays.asList(artifact));

		gradleBuildScript = new GradleBuildScript(FileUtil.getFile(gradileFile));

		List<GradleDependency> updatedDependencies = gradleBuildScript.getDependencies();

		Assert.assertTrue(updatedDependencies.contains(gradleDependency));

		deleteProject(ips);
	}

	@Test
	public void toolingApiCustomModel() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("custom-model");

		ips.before();

		LiferayGradleProject gradleProject = GradleTestUtil.fullImportGradleProject(ips);

		assertProjectExists(ips);

		ProjectInfo customModel = LiferayGradleCore.getToolingModel(ProjectInfo.class, gradleProject.getProject());

		Set<String> pluginClassNames = customModel.getPluginClassNames();

		Assert.assertNotNull(customModel);

		Assert.assertFalse(pluginClassNames.contains("not.a.plugin"));

		Assert.assertTrue(pluginClassNames.contains("aQute.bnd.gradle.BndWorkspacePlugin"));

		deleteProject(ips);
	}

}