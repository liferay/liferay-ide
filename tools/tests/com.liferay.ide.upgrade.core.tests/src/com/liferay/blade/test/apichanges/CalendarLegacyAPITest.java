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

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.test.Util;

import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class CalendarLegacyAPITest extends APITestBase {

	@Test
	public void calendarLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 7, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals("", 38, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1886, problem.startOffset);
			Assert.assertEquals("", 1946, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1849, problem.startOffset);
			Assert.assertEquals("", 1909, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals("", 39, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1956, problem.startOffset);
			Assert.assertEquals("", 2011, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1918, problem.startOffset);
			Assert.assertEquals("", 1973, problem.endOffset);
		}

		problem = problems.get(2);

		Assert.assertEquals("", 40, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2021, problem.startOffset);
			Assert.assertEquals("", 2074, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1982, problem.startOffset);
			Assert.assertEquals("", 2035, problem.endOffset);
		}

		problem = problems.get(3);

		Assert.assertEquals("", 159, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 7006, problem.startOffset);
			Assert.assertEquals("", 7143, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 6848, problem.startOffset);
			Assert.assertEquals("", 6983, problem.endOffset);
		}

		problem = problems.get(4);

		Assert.assertEquals("", 43, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2228, problem.startOffset);
			Assert.assertEquals("", 2276, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2186, problem.startOffset);
			Assert.assertEquals("", 2234, problem.endOffset);
		}

		problem = problems.get(5);

		Assert.assertEquals("", 41, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2084, problem.startOffset);
			Assert.assertEquals("", 2152, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2044, problem.startOffset);
			Assert.assertEquals("", 2112, problem.endOffset);
		}

		problem = problems.get(6);

		Assert.assertEquals("", 42, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2162, problem.startOffset);
			Assert.assertEquals("", 2218, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2121, problem.startOffset);
			Assert.assertEquals("", 2177, problem.endOffset);
		}
	}

	@Override
	public int getExpectedNumber() {
		return 7;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.blade.upgrade.liferay70.apichanges.CalendarLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/CalendarPortlet.java");
	}

}