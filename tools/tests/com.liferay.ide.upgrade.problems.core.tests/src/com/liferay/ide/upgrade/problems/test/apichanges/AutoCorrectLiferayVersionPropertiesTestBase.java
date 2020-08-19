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

package com.liferay.ide.upgrade.problems.test.apichanges;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

/**
 * @author Seiphon Wang
 */
public abstract class AutoCorrectLiferayVersionPropertiesTestBase {

	@Test
	public void autoCorrectProblems() throws Exception {
		File tempFolder = Files.createTempDirectory("autocorrect").toFile();

		File testFile = new File(tempFolder, "liferay-plugin-package.properties");

		tempFolder.deleteOnExit();

		Files.copy(getOriginalTestFile().toPath(), testFile.toPath());

		FileMigrator liferayVersionFileMigrator = null;

		Collection<ServiceReference<FileMigrator>> mrefs =
			context.getServiceReferences(FileMigrator.class, "(version=" + getVersion() + ")");

		for (ServiceReference<FileMigrator> mref : mrefs) {
			FileMigrator fileMigrator = context.getService(mref);

			Class<?> clazz = fileMigrator.getClass();

			if (clazz.getName().contains(getImplClassName())) {
				liferayVersionFileMigrator = fileMigrator;

				break;
			}
		}

		Assert.assertNotNull("Expected that a valid descriptorFileMigrator would be found", liferayVersionFileMigrator);

		List<UpgradeProblem> upgradeProblems = liferayVersionFileMigrator.analyze(testFile);

		Assert.assertEquals("Expected to have found exactly one problem.", 1, upgradeProblems.size());

		int problemsFixed = ((AutoFileMigrator)liferayVersionFileMigrator).correctProblems(testFile, upgradeProblems);

		Assert.assertEquals("Expected to have fixed exactly one problem.", 1, problemsFixed);

		upgradeProblems = liferayVersionFileMigrator.analyze(testFile);

		Assert.assertEquals("Expected to not find any problems.", 0, upgradeProblems.size());
	}

	public abstract String getImplClassName();

	public abstract File getOriginalTestFile();

	public abstract String getVersion();

	protected BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
}
