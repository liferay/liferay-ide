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
public class MobileDeviceRulesLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 8;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay70.MobileDeviceRulesLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/EditLayoutsAction.java");
	}

	@Test
	public void mobileDeviceRulesLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 8, problems.size());

		UpgradeProblem problem = problems.get(0);

		Assert.assertEquals("", 36, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1554, problem.getStartOffset());
			Assert.assertEquals("", 1625, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1519, problem.getStartOffset());
			Assert.assertEquals("", 1590, problem.getEndOffset());
		}

		problem = problems.get(1);

		Assert.assertEquals("", 64, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2804, problem.getStartOffset());
			Assert.assertEquals("", 2900, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2741, problem.getStartOffset());
			Assert.assertEquals("", 2836, problem.getEndOffset());
		}

		problem = problems.get(2);

		Assert.assertEquals("", 37, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1635, problem.getStartOffset());
			Assert.assertEquals("", 1701, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1599, problem.getStartOffset());
			Assert.assertEquals("", 1665, problem.getEndOffset());
		}

		problem = problems.get(3);

		Assert.assertEquals("", 68, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2960, problem.getStartOffset());
			Assert.assertEquals("", 3183, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2893, problem.getStartOffset());
			Assert.assertEquals("", 3112, problem.getEndOffset());
		}

		problem = problems.get(4);

		Assert.assertEquals("", 38, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1711, problem.getStartOffset());
			Assert.assertEquals("", 1793, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1674, problem.getStartOffset());
			Assert.assertEquals("", 1756, problem.getEndOffset());
		}

		problem = problems.get(5);

		Assert.assertEquals("", 50, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2248, problem.getStartOffset());
			Assert.assertEquals("", 2360, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2199, problem.getStartOffset());
			Assert.assertEquals("", 2310, problem.getEndOffset());
		}

		problem = problems.get(6);

		Assert.assertEquals("", 39, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1803, problem.getStartOffset());
			Assert.assertEquals("", 1880, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1765, problem.getStartOffset());
			Assert.assertEquals("", 1842, problem.getEndOffset());
		}

		problem = problems.get(7);

		Assert.assertEquals("", 57, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2513, problem.getStartOffset());
			Assert.assertEquals("", 2756, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2457, problem.getStartOffset());
			Assert.assertEquals("", 2696, problem.getEndOffset());
		}
	}

}