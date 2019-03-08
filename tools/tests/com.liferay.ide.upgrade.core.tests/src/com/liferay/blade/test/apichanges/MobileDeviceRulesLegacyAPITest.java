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
public class MobileDeviceRulesLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 8;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.blade.upgrade.liferay70.apichanges.MobileDeviceRulesLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/EditLayoutsAction.java");
	}

	@Test
	public void mobileDeviceRulesLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 8, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals("", 36, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1554, problem.startOffset);
			Assert.assertEquals("", 1625, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1519, problem.startOffset);
			Assert.assertEquals("", 1590, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals("", 64, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2804, problem.startOffset);
			Assert.assertEquals("", 2900, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2741, problem.startOffset);
			Assert.assertEquals("", 2836, problem.endOffset);
		}

		problem = problems.get(2);

		Assert.assertEquals("", 37, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1635, problem.startOffset);
			Assert.assertEquals("", 1701, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1599, problem.startOffset);
			Assert.assertEquals("", 1665, problem.endOffset);
		}

		problem = problems.get(3);

		Assert.assertEquals("", 68, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2960, problem.startOffset);
			Assert.assertEquals("", 3183, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2893, problem.startOffset);
			Assert.assertEquals("", 3112, problem.endOffset);
		}

		problem = problems.get(4);

		Assert.assertEquals("", 38, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1711, problem.startOffset);
			Assert.assertEquals("", 1793, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1674, problem.startOffset);
			Assert.assertEquals("", 1756, problem.endOffset);
		}

		problem = problems.get(5);

		Assert.assertEquals("", 50, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2248, problem.startOffset);
			Assert.assertEquals("", 2360, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2199, problem.startOffset);
			Assert.assertEquals("", 2310, problem.endOffset);
		}

		problem = problems.get(6);

		Assert.assertEquals("", 39, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1803, problem.startOffset);
			Assert.assertEquals("", 1880, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1765, problem.startOffset);
			Assert.assertEquals("", 1842, problem.endOffset);
		}

		problem = problems.get(7);

		Assert.assertEquals("", 57, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2513, problem.startOffset);
			Assert.assertEquals("", 2756, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2457, problem.startOffset);
			Assert.assertEquals("", 2696, problem.endOffset);
		}
	}

}