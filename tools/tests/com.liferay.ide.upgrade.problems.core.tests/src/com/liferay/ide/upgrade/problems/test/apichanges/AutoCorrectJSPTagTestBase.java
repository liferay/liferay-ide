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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
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
 * @author Gregory Amerson
 * @author Terry Jia
 */
public abstract class AutoCorrectJSPTagTestBase {

	@Test
	public void autoCorrectProblems() throws Exception {
		File tempFolder = Files.createTempDirectory("autocorrect").toFile();

		File testFile = new File(tempFolder, "test.jsp");

		tempFolder.deleteOnExit();

		Files.copy(getOriginalTestFile().toPath(), testFile.toPath());

		List<UpgradeProblem> problems = null;
		FileMigrator migrator = null;

		Collection<ServiceReference<FileMigrator>> mrefs = context.getServiceReferences(FileMigrator.class, null);

		for (ServiceReference<FileMigrator> mref : mrefs) {
			migrator = context.getService(mref);

			Class<?> clazz = migrator.getClass();

			if (clazz.getName().contains(getImplClassName())) {
				problems = migrator.analyze(testFile);

				break;
			}
		}

		Assert.assertEquals("", getExpectedNumber(), problems.size());

		int problemsFixed = ((AutoFileMigrator)migrator).correctProblems(testFile, problems);

		Assert.assertEquals("", getExpectedFixedNumber(), problemsFixed);

		File dest = new File(tempFolder, "Updated.jsp");

		Assert.assertTrue(testFile.renameTo(dest));

		problems = migrator.analyze(dest);

		Assert.assertEquals("", 0, problems.size());

		for (String checkPoint : getCheckPoints()) {
			int lineNumber = Integer.parseInt(checkPoint.split(",")[0]);
			String lineContent = checkPoint.split(",")[1];

			try {
				String[] lines = _readLines(Files.newInputStream(dest.toPath()));

				Assert.assertTrue(lines[lineNumber - 1].trim().equals(lineContent));
			}
			catch (Exception e) {
			}
		}
	}

	public abstract List<String> getCheckPoints();

	public int getExpectedFixedNumber() {
		return 1;
	}

	public int getExpectedNumber() {
		return 1;
	}

	public abstract String getImplClassName();

	public abstract File getOriginalTestFile();

	protected BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

	private String[] _readLines(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}

		List<String> lines = new ArrayList<>();

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringBuffer contents = new StringBuffer(line);

				lines.add(contents.toString());
			}
		}
		catch (Exception e) {
		}

		return lines.toArray(new String[lines.size()]);
	}

}