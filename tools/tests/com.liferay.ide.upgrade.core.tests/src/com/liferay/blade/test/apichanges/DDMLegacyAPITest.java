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
public class DDMLegacyAPITest extends APITestBase {

	@Test
	public void dDMLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 5, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals("", 36, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1704, problem.startOffset);
			Assert.assertEquals("", 1779, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1669, problem.startOffset);
			Assert.assertEquals("", 1744, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals("", 134, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertTrue(problem.startOffset >= 4829 && problem.startOffset <= 4832);
			Assert.assertTrue(problem.endOffset >= 4886 && problem.endOffset <= 4889);
		}
		else {
			Assert.assertTrue(problem.startOffset >= 4696 && problem.startOffset <= 4699);
			Assert.assertTrue(problem.endOffset >= 4753 && problem.endOffset <= 4756);
		}

		problem = problems.get(2);

		Assert.assertEquals("", 147, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertTrue(problem.startOffset >= 5177 && problem.startOffset <= 5180);
			Assert.assertTrue(problem.endOffset >= 5234 && problem.endOffset <= 5237);
		}
		else {
			Assert.assertTrue(problem.startOffset >= 5031 && problem.startOffset <= 5034);
			Assert.assertTrue(problem.endOffset >= 5088 && problem.endOffset <= 5091);
		}

		problem = problems.get(3);

		Assert.assertEquals("", 37, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertTrue(problem.startOffset >= 1789 && problem.startOffset <= 1792);
			Assert.assertTrue(problem.endOffset >= 1859 && problem.endOffset <= 1862);
		}
		else {
			Assert.assertEquals("", 1753, problem.startOffset);
			Assert.assertEquals("", 1823, problem.endOffset);
		}

		problem = problems.get(4);

		Assert.assertEquals("", 162, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertTrue(problem.startOffset >= 5573 && problem.startOffset <= 5576);
			Assert.assertTrue(problem.endOffset >= 5690 && problem.endOffset <= 5693);
		}
		else {
			Assert.assertTrue(
				String.valueOf(problem.startOffset), problem.startOffset >= 5412 && problem.startOffset <= 5415);
			Assert.assertTrue(
				String.valueOf(problem.endOffset), problem.endOffset >= 5527 && problem.endOffset <= 5530);
		}
	}

	@Override
	public int getExpectedNumber() {
		return 5;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.blade.upgrade.liferay70.apichanges.DDMLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/JournalArticleAssetRendererFactory.java");
	}

}