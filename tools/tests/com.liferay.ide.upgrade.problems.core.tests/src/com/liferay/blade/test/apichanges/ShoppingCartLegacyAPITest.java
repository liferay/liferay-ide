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
public class ShoppingCartLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 4;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay70.ShoppingCartLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/CartAction.java");
	}

	@Test
	public void shoppingCartLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 4, problems.size());

		UpgradeProblem problem = problems.get(0);

		Assert.assertEquals("", 32, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1475, problem.getStartOffset());
			Assert.assertEquals("", 1540, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1444, problem.getStartOffset());
			Assert.assertEquals("", 1509, problem.getEndOffset());
		}

		problem = problems.get(1);

		Assert.assertEquals("", 143, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 4691, problem.getStartOffset());
			Assert.assertEquals("", 4858, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 4549, problem.getStartOffset());
			Assert.assertEquals("", 4714, problem.getEndOffset());
		}

		problem = problems.get(2);

		Assert.assertEquals("", 33, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 1550, problem.getStartOffset());
			Assert.assertEquals("", 1615, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 1518, problem.getStartOffset());
			Assert.assertEquals("", 1583, problem.getEndOffset());
		}

		problem = problems.get(3);

		Assert.assertEquals("", 118, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 3987, problem.getStartOffset());
			Assert.assertEquals("", 4031, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 3870, problem.getStartOffset());
			Assert.assertEquals("", 3914, problem.getEndOffset());
		}
	}

}