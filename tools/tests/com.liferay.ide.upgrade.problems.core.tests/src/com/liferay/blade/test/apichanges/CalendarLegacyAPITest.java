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

package com.liferay.blade.test.apichanges;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.blade.test.Util;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class CalendarLegacyAPITest extends APITestBase {

	@Test
	public void calendarLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 7, problems.size());

		UpgradeProblem problem = problems.get(0);

		Assert.assertEquals("", 38, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1886, problem.getStartOffset());
			Assert.assertEquals("", 1946, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1849, problem.getStartOffset());
			Assert.assertEquals("", 1909, problem.getEndOffset());
		}

		problem = problems.get(1);

		Assert.assertEquals("", 39, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1956, problem.getStartOffset());
			Assert.assertEquals("", 2011, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1918, problem.getStartOffset());
			Assert.assertEquals("", 1973, problem.getEndOffset());
		}

		problem = problems.get(2);

		Assert.assertEquals("", 40, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2021, problem.getStartOffset());
			Assert.assertEquals("", 2074, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1982, problem.getStartOffset());
			Assert.assertEquals("", 2035, problem.getEndOffset());
		}

		problem = problems.get(3);

		Assert.assertEquals("", 159, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 7006, problem.getStartOffset());
			Assert.assertEquals("", 7143, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 6848, problem.getStartOffset());
			Assert.assertEquals("", 6983, problem.getEndOffset());
		}

		problem = problems.get(4);

		Assert.assertEquals("", 43, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2228, problem.getStartOffset());
			Assert.assertEquals("", 2276, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2186, problem.getStartOffset());
			Assert.assertEquals("", 2234, problem.getEndOffset());
		}

		problem = problems.get(5);

		Assert.assertEquals("", 41, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2084, problem.getStartOffset());
			Assert.assertEquals("", 2152, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2044, problem.getStartOffset());
			Assert.assertEquals("", 2112, problem.getEndOffset());
		}

		problem = problems.get(6);

		Assert.assertEquals("", 42, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2162, problem.getStartOffset());
			Assert.assertEquals("", 2218, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2121, problem.getStartOffset());
			Assert.assertEquals("", 2177, problem.getEndOffset());
		}
	}

	@Override
	public int getExpectedNumber() {
		return 7;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay70.CalendarLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/CalendarPortlet.java");
	}

}