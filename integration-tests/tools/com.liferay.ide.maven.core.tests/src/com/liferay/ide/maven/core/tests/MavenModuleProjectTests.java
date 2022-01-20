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

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.maven.core.MavenUtil;
import com.liferay.ide.core.IProjectBuilder;

import java.io.File;

import java.net.URL;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class MavenModuleProjectTests extends AbstractMavenProjectTestCase {

	@Test
	public void testMavenDependencyUpdate() throws Exception {
		String[][] dependency = {{"com.liferay.portal", "com.liferay.portal.kernel", "2.6.0"}};

		Artifact artifact = new Artifact("com.liferay.portal", "com.liferay.portal.kernel", "2.6.0", "compile", null);

		Dependency mavenDependency = new Dependency();

		mavenDependency.setGroupId(dependency[0][0]);
		mavenDependency.setArtifactId(dependency[0][1]);
		mavenDependency.setVersion(dependency[0][2]);

		URL wsZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/MavenDependencyTestProject.zip");

		File wsZipFile = new File(FileLocator.toFileURL(wsZipUrl).getFile());

		File eclipseWorkspaceLocation = CoreUtil.getWorkspaceRootLocation().toFile();

		ZipUtil.unzip(wsZipFile, eclipseWorkspaceLocation);

		File mavenDependencyTestProjectFolder = new File(eclipseWorkspaceLocation, "MavenDependencyTestProject");

		MavenUtil.importProject(mavenDependencyTestProjectFolder.getAbsolutePath(), monitor);

		waitForJobsToComplete(monitor);

		IProject mavenDependencyTestProject = CoreUtil.getProject("MavenDependencyTestProject");

		Assert.assertNotNull(mavenDependencyTestProject);

		Assert.assertTrue(mavenDependencyTestProject.exists());

		IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(
			mavenDependencyTestProject, new NullProgressMonitor());

		Assert.assertNotNull(projectFacade);

		MavenProject mavenProject = projectFacade.getMavenProject(new NullProgressMonitor());

		List<Dependency> existedDependencies = mavenProject.getDependencies();

		Assert.assertFalse(_checkDependency(existedDependencies, mavenDependency));

		ILiferayProject liferayMavenDependencyProject =
			LiferayCore.create(ILiferayProject.class, mavenDependencyTestProject);

		IProjectBuilder projectBuilder = liferayMavenDependencyProject.adapt(IProjectBuilder.class);

		projectBuilder.updateDependencies(mavenDependencyTestProject, Arrays.asList(artifact));

		waitForJobsToComplete(monitor);

		MavenProject updateMavenProject = projectFacade.getMavenProject(new NullProgressMonitor());

		List<Dependency> updateDependencies = updateMavenProject.getDependencies();

		Assert.assertTrue(_checkDependency(updateDependencies, mavenDependency));
	}

	private boolean _checkDependency(List<Dependency> existedDependencies, Dependency mavenDependency) {
		for (Dependency existedDependency : existedDependencies) {
			String existedKey = existedDependency.getManagementKey();

			if (existedKey.equals(mavenDependency.getManagementKey())) {
				return true;
			}
		}

		return false;
	}

}