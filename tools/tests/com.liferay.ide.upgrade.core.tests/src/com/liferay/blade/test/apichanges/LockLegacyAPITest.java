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
public class LockLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 4;
	}

	@Override
	public String getImplClassName() {
		return "LockLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/LockProtectedAction.java");
	}

	@Test
	public void lockLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 4, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals("", 22, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 893, problem.startOffset);
			Assert.assertEquals("", 940, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 872, problem.startOffset);
			Assert.assertEquals("", 919, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals("", 46, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1420, problem.startOffset);
			Assert.assertEquals("", 1484, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1375, problem.startOffset);
			Assert.assertEquals("", 1438, problem.endOffset);
		}

		problem = problems.get(2);

		Assert.assertEquals("", 62, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1747, problem.startOffset);
			Assert.assertEquals("", 1806, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1686, problem.startOffset);
			Assert.assertEquals("", 1745, problem.endOffset);
		}

		problem = problems.get(3);

		Assert.assertEquals("", 73, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 1971, problem.startOffset);
			Assert.assertEquals("", 2044, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 1899, problem.startOffset);
			Assert.assertEquals("", 1971, problem.endOffset);
		}
	}

}