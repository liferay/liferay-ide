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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.test.Util;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class WebContentLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 5;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay70.WebContentLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/LegacyAPIsAntPortlet.java");
	}

	@Test
	public void webContentLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 5, problems.size());

		UpgradeProblem problem = problems.get(0);

		Assert.assertEquals(20, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 961, problem.getStartOffset());
			Assert.assertEquals("", 1023, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 942, problem.getStartOffset());
			Assert.assertEquals("", 1004, problem.getEndOffset());
		}

		problem = problems.get(1);

		Assert.assertEquals(47, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1917, problem.getStartOffset());
			Assert.assertEquals("", 1950, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1871, problem.getStartOffset());
			Assert.assertEquals("", 1904, problem.getEndOffset());
		}

		problem = problems.get(2);

		Assert.assertEquals(21, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1033, problem.getStartOffset());
			Assert.assertEquals("", 1099, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1013, problem.getStartOffset());
			Assert.assertEquals("", 1079, problem.getEndOffset());
		}

		problem = problems.get(3);

		Assert.assertEquals("", 41, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1637, problem.getStartOffset());
			Assert.assertEquals("", 1695, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1597, problem.getStartOffset());
			Assert.assertEquals("", 1655, problem.getEndOffset());
		}

		problem = problems.get(4);

		Assert.assertEquals(45, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1830, problem.getStartOffset());
			Assert.assertEquals("", 1873, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1786, problem.getStartOffset());
			Assert.assertEquals("", 1829, problem.getEndOffset());
		}
	}

}