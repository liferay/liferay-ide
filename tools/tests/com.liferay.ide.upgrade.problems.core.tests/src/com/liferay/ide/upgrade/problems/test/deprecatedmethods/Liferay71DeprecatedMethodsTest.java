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

package com.liferay.ide.upgrade.problems.test.deprecatedmethods;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.test.apichanges.APITestBase;

/**
 * @author Ethan Sun
 */
public class Liferay71DeprecatedMethodsTest extends APITestBase {

	@Test
	public void deprecatedMethods71TestFile() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(deprecatedMethods71TestFile);

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 8, problems.size());
	}

	@Override
	public int getExpectedNumber() {
		return 20;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay71.Liferay71DeprecatedMethodsMigrator";
	}

	@Override
	public File getTestFile() {
		return new File("projects/deprecated-methods-test/liferay71-deprecated-methods-test/Liferay71DeprecatedMethodsTestCase.java");
	}

	public File deprecatedMethods71TestFile = new File(
		"projects/deprecated-methods-test/liferay71-deprecated-methods-test/JournalArticleLocalServiceUtil.java");

}