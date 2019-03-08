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
public class MarketplaceLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 6;
	}

	@Override
	public String getComponentName() {
		return "com.liferay.blade.upgrade.liferay70.apichanges.MarketplaceLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/MarketplaceMessageListener.java");
	}

	@Test
	public void marketplaceLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);
		Assert.assertEquals("", 6, problems.size());

		Problem problem = problems.get(0);

		Assert.assertEquals("", 18, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 681, problem.startOffset);
			Assert.assertEquals("", 732, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 664, problem.startOffset);
			Assert.assertEquals("", 715, problem.endOffset);
		}

		problem = problems.get(1);

		Assert.assertEquals("", 60, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2068, problem.startOffset);
			Assert.assertEquals("", 2176, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2009, problem.startOffset);
			Assert.assertEquals("", 2115, problem.endOffset);
		}

		problem = problems.get(2);

		Assert.assertEquals("", 87, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2887, problem.startOffset);
			Assert.assertEquals("", 2947, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2801, problem.startOffset);
			Assert.assertEquals("", 2861, problem.endOffset);
		}

		problem = problems.get(3);

		Assert.assertEquals("", 19, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 742, problem.startOffset);
			Assert.assertEquals("", 796, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 724, problem.startOffset);
			Assert.assertEquals("", 778, problem.endOffset);
		}

		problem = problems.get(4);

		Assert.assertEquals("", 73, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2503, problem.startOffset);
			Assert.assertEquals("", 2613, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2431, problem.startOffset);
			Assert.assertEquals("", 2539, problem.endOffset);
		}

		problem = problems.get(5);

		Assert.assertEquals("", 82, problem.lineNumber);

		if (Util.isWindows()) {
			Assert.assertEquals("", 2764, problem.startOffset);
			Assert.assertEquals("", 2875, problem.endOffset);
		}
		else {
			Assert.assertEquals("", 2683, problem.startOffset);
			Assert.assertEquals("", 2792, problem.endOffset);
		}
	}

}