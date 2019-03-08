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
public class SocialNetworkingLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 2;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.blade.upgrade.liferay70.apichanges.SocialNetworkingLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/GroupModelListener.java");
	}

	@Test
	public void testFull() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 2, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals("", 20, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 781, problem.startOffset);
			Assert.assertEquals("", 843, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 762, problem.startOffset);
			Assert.assertEquals("", 824, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals("", 31, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1086, problem.startOffset);
			Assert.assertEquals("", 1149, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1056, problem.startOffset);
			Assert.assertEquals("", 1119, problem.endOffset);
		}
	}

	@Test
	public void testFull2() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(testFile2);

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 6, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals("", 24, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1005, problem.startOffset);
			Assert.assertEquals("", 1070, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 982, problem.startOffset);
			Assert.assertEquals("", 1047, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals("", 57, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1892, problem.startOffset);
			Assert.assertEquals("", 1955, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1836, problem.startOffset);
			Assert.assertEquals("", 1899, problem.endOffset);
		}

		problem = problems.get(2);

		Assert.assertEquals("", 128, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 4271, problem.startOffset);
			Assert.assertEquals("", 4570, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 4144, problem.startOffset);
			Assert.assertEquals("", 4439, problem.endOffset);
		}

		problem = problems.get(3);

		Assert.assertEquals("", 135, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 4591, problem.startOffset);
			Assert.assertEquals("", 4914, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 4457, problem.startOffset);
			Assert.assertEquals("", 4775, problem.endOffset);
		}

		problem = problems.get(4);

		Assert.assertEquals("", 25, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1080, problem.startOffset);
			Assert.assertEquals("", 1152, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1056, problem.startOffset);
			Assert.assertEquals("", 1128, problem.endOffset);
		}

		problem = problems.get(5);

		Assert.assertEquals("", 156, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 5378, problem.startOffset);
			Assert.assertEquals("", 5504, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 5223, problem.startOffset);
			Assert.assertEquals("", 5348, problem.endOffset);
		}
	}

	public File testFile2 = new File(
		"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/MeetupsPortlet.java");

}