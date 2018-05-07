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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.maven.core.LiferayMavenLegacyProjectUpdater;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andy Wu
 */
public class LiferayMavenLegacyProjectUpdaterTests {

	@Test
	public void testUpgrade() throws Exception {
		IProject project = CoreUtil.getProject("testMavenProjects");

		if (FileUtil.exists(project)) {
			project.delete(true, true, new NullProgressMonitor());
		}

		URL projectZipUrl = Platform.getBundle(
			"com.liferay.ide.maven.core.tests").getEntry("projects/testMavenProjects.zip");

		File projectZipFile = new File(FileLocator.toFileURL(projectZipUrl).getFile());

		IPath stateLocation = LiferayMavenCore.getDefault().getStateLocation();

		File targetFolder = new File(stateLocation.toFile(), "testMavenProjects");

		if (targetFolder.exists()) {
			targetFolder.delete();
		}

		ZipUtil.unzip(projectZipFile, stateLocation.toFile());

		Assert.assertTrue(targetFolder.exists());

		ILiferayProjectImporter importer = LiferayCore.getImporter("maven");

		importer.importProjects(targetFolder.getAbsolutePath(), new NullProgressMonitor());

		LiferayMavenLegacyProjectUpdater updater = new LiferayMavenLegacyProjectUpdater();

		// portlet project

		IProject ppProject = CoreUtil.getProject("testpp");

		Assert.assertTrue(updater.isNeedUpgrade(ppProject));
		updater.upgradePomFile(ppProject, null);
		Assert.assertFalse(updater.isNeedUpgrade(ppProject));
		Assert.assertTrue(_containString(ppProject, "com.liferay.css.builder"));
		_makeSureNoLegacyElememnts(ppProject);

		// service builder parent project

		IProject testsbProject = CoreUtil.getProject("testsb");

		Assert.assertTrue(updater.isNeedUpgrade(testsbProject));
		updater.upgradePomFile(testsbProject, null);
		Assert.assertFalse(updater.isNeedUpgrade(testsbProject));
		_makeSureNoLegacyElememnts(testsbProject);

		// service builder -portlet subproject

		IProject testsbPortletProject = CoreUtil.getProject("testsb-portlet");

		Assert.assertTrue(updater.isNeedUpgrade(testsbPortletProject));
		updater.upgradePomFile(testsbPortletProject, null);
		Assert.assertFalse(updater.isNeedUpgrade(testsbPortletProject));
		Assert.assertTrue(_containString(testsbPortletProject, "com.liferay.css.builder"));
		Assert.assertTrue(_containString(testsbPortletProject, "com.liferay.portal.tools.service.builder"));
		Assert.assertTrue(_containString(testsbPortletProject, "biz.aQute.bnd.annotation"));
		_makeSureNoLegacyElememnts(testsbPortletProject);

		//// service builder -service subproject
		IProject testsbPortletServiceProject = CoreUtil.getProject("testsb-portlet-service");

		Assert.assertTrue(updater.isNeedUpgrade(testsbPortletServiceProject));
		updater.upgradePomFile(testsbPortletServiceProject, null);
		Assert.assertFalse(updater.isNeedUpgrade(testsbPortletServiceProject));
		Assert.assertTrue(_containString(testsbPortletServiceProject, "biz.aQute.bnd.annotation"));
		_makeSureNoLegacyElememnts(testsbPortletServiceProject);

		// theme project

		IProject testthemeProject = CoreUtil.getProject("testtheme");

		Assert.assertTrue(updater.isNeedUpgrade(testthemeProject));

		updater.upgradePomFile(testthemeProject, null);

		Assert.assertFalse(updater.isNeedUpgrade(testthemeProject));
		Assert.assertTrue(_containString(testthemeProject, "com.liferay.portal.tools.theme.builder.outputDir"));
		Assert.assertTrue(_containString(testthemeProject, "project.build.sourceEncoding"));
		Assert.assertTrue(_containString(testthemeProject, "maven-war-plugin"));
		Assert.assertTrue(_containString(testthemeProject, "maven-dependency-plugin"));
		Assert.assertTrue(_containString(testthemeProject, "com.liferay.css.builder"));
		Assert.assertTrue(_containString(testthemeProject, "com.liferay.portal.tools.theme.builder"));

		_makeSureNoLegacyElememnts(testthemeProject);
	}

	private boolean _containString(IProject project, String containStr) {
		IFile pomFile = project.getFile("pom.xml");

		try (InputStream ins = pomFile.getContents()) {
			String content = FileUtil.readContents(ins);

			if (!CoreUtil.empty(content)) {
				return content.contains(containStr);
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	private void _makeSureNoLegacyElememnts(IProject project) {
		Assert.assertFalse(_containString(project, "liferay-maven-plugin"));
		Assert.assertFalse(_containString(project, "portal-service"));
		Assert.assertFalse(_containString(project, "util-java"));
		Assert.assertFalse(_containString(project, "util-bridges"));
		Assert.assertFalse(_containString(project, "util-taglib"));
		Assert.assertFalse(_containString(project, "util-slf4j"));
	}

}