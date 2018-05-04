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
public class DDLRecordLegacyAPITest extends APITestBase {

	@Test
	public void dDLRecordLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals(3, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals(30, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals(1361, problem.startOffset);
			Assert.assertEquals(1426, problem.endOffset);
		}
		else {
			Assert.assertEquals(1332, problem.startOffset);
			Assert.assertEquals(1397, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals(132, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals(4220, problem.startOffset);
			Assert.assertEquals(4263, problem.endOffset);
		}
		else {
			Assert.assertEquals(4089, problem.startOffset);
			Assert.assertEquals(4132, problem.endOffset);
		}

		problem = problems.get(2);

		Assert.assertEquals(145, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals(4619, problem.startOffset);
			Assert.assertEquals(4699, problem.endOffset);
		}
		else {
			Assert.assertEquals(4475, problem.startOffset);
			Assert.assertEquals(4554, problem.endOffset);
		}
	}

	@Override
	public int getExpectedNumber() {
		return 3;
	}

	@Override
	public String getImplClassName() {
		return "DDLRecordLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/EditRecordAction.java");
	}

}