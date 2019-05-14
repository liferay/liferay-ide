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
public class DDMLegacyAPITest extends APITestBase {

	@Test
	public void dDMLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 5, problems.size());

		UpgradeProblem problem = problems.get(0);

		Assert.assertEquals("", 36, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1704, problem.getStartOffset());
			Assert.assertEquals("", 1779, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1669, problem.getStartOffset());
			Assert.assertEquals("", 1744, problem.getEndOffset());
		}

		problem = problems.get(1);

		Assert.assertEquals("", 134, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertTrue(problem.getStartOffset() >= 4829 && problem.getStartOffset() <= 4832);
			Assert.assertTrue(problem.getEndOffset() >= 4886 && problem.getEndOffset() <= 4889);
		}
		else {
			Assert.assertTrue(problem.getStartOffset() >= 4696 && problem.getStartOffset() <= 4699);
			Assert.assertTrue(problem.getEndOffset() >= 4753 && problem.getEndOffset() <= 4756);
		}

		problem = problems.get(2);

		Assert.assertEquals("", 147, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertTrue(problem.getStartOffset() >= 5177 && problem.getStartOffset() <= 5180);
			Assert.assertTrue(problem.getEndOffset() >= 5234 && problem.getEndOffset() <= 5237);
		}
		else {
			Assert.assertTrue(problem.getStartOffset() >= 5031 && problem.getStartOffset() <= 5034);
			Assert.assertTrue(problem.getEndOffset() >= 5088 && problem.getEndOffset() <= 5091);
		}

		problem = problems.get(3);

		Assert.assertEquals("", 37, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertTrue(problem.getStartOffset() >= 1789 && problem.getStartOffset() <= 1792);
			Assert.assertTrue(problem.getEndOffset() >= 1859 && problem.getEndOffset() <= 1862);
		}
		else {
			Assert.assertEquals("", 1753, problem.getStartOffset());
			Assert.assertEquals("", 1823, problem.getEndOffset());
		}

		problem = problems.get(4);

		Assert.assertEquals("", 162, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertTrue(problem.getStartOffset() >= 5573 && problem.getStartOffset() <= 5576);
			Assert.assertTrue(problem.getEndOffset() >= 5690 && problem.getEndOffset() <= 5693);
		}
		else {
			Assert.assertTrue(
				String.valueOf(problem.getStartOffset()), problem.getStartOffset() >= 5412 && problem.getStartOffset() <= 5415);
			Assert.assertTrue(
				String.valueOf(problem.getEndOffset()), problem.getEndOffset() >= 5527 && problem.getEndOffset() <= 5530);
		}
	}

	@Override
	public int getExpectedNumber() {
		return 5;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay70.DDMLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/JournalArticleAssetRendererFactory.java");
	}

}