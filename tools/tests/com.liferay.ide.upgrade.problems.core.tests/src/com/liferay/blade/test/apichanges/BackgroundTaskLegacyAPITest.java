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
public class BackgroundTaskLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 2;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay70.BackgroundTaskLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay" +
				"/DLFileNameBackgroundTaskServiceImpl.java");
	}

	@Test
	public void testFull() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 2, problems.size());

		UpgradeProblem problem = problems.get(0);

		Assert.assertEquals("", 18, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 688, problem.getStartOffset());
			Assert.assertEquals("", 736, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 671, problem.getStartOffset());
			Assert.assertEquals("", 719, problem.getEndOffset());
		}

		problem = problems.get(1);

		Assert.assertEquals("", 19, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 746, problem.getStartOffset());
			Assert.assertEquals("", 801, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 728, problem.getStartOffset());
			Assert.assertEquals("", 783, problem.getEndOffset());
		}
	}

}