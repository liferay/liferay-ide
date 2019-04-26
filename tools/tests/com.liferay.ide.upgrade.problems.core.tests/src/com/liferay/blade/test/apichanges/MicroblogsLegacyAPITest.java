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
public class MicroblogsLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 4;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay70.MicroblogsLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/MicroblogsPortlet.java");
	}

	@Test
	public void microblogsLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 4, problems.size());

		UpgradeProblem problem = problems.get(0);

		Assert.assertEquals("", 22, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 972, problem.getStartOffset());
			Assert.assertEquals("", 1029, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 951, problem.getStartOffset());
			Assert.assertEquals("", 1008, problem.getEndOffset());
		}

		problem = problems.get(1);

		Assert.assertEquals("", 47, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1809, problem.getStartOffset());
			Assert.assertEquals("", 1876, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1763, problem.getStartOffset());
			Assert.assertEquals("", 1830, problem.getEndOffset());
		}

		problem = problems.get(2);

		Assert.assertEquals("", 77, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 2879, problem.getStartOffset());
			Assert.assertEquals("", 2997, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2803, problem.getStartOffset());
			Assert.assertEquals("", 2920, problem.getEndOffset());
		}

		problem = problems.get(3);

		Assert.assertEquals("", 81, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 3018, problem.getStartOffset());
			Assert.assertEquals("", 3194, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 2938, problem.getStartOffset());
			Assert.assertEquals("", 3112, problem.getEndOffset());
		}
	}

}