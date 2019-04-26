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
public class DDMStructureLocalServiceInvocationTest extends APITestBase {

	@Test
	public void ddmTemplateAnalyzeTest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 1, problems.size());

		UpgradeProblem problem = problems.get(0);

		Assert.assertEquals("", 7, problem.getLineNumber());

		if (Util.isWindows()) {
			Assert.assertEquals("", 144, problem.getStartOffset());
			Assert.assertEquals("", 270, problem.getEndOffset());
		}
		else {
			Assert.assertEquals("", 138, problem.getStartOffset());
			Assert.assertEquals("", 264, problem.getEndOffset());
		}
	}

	@Override
	public String getComponentName() {
		return "com.liferay.ide.upgrade.problems.core.internal.liferay70.DDMStructureLocalServiceAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/filetests/DDMStructureLocalServiceAPITest.java");
	}

}