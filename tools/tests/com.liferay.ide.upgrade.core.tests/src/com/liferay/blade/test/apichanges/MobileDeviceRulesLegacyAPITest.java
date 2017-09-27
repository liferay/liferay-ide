/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.test.apichanges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class MobileDeviceRulesLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 8;
	}

	@Override
	public String getImplClassName() {
		return "MobileDeviceRulesLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/EditLayoutsAction.java");
	}

	@Test
	public void mobileDeviceRulesLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		assertNotNull(problems);
		assertEquals(8, problems.size());

		Problem problem = problems.get(0);

		assertEquals(36, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(1554, problem.startOffset);
//			assertEquals(1625, problem.endOffset);
//		} else {
			assertEquals(1519, problem.startOffset);
			assertEquals(1590, problem.endOffset);
//		}

		problem = problems.get(1);

		assertEquals(64, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(2804, problem.startOffset);
//			assertEquals(2900, problem.endOffset);
//		} else {
			assertEquals(2741, problem.startOffset);
			assertEquals(2836, problem.endOffset);
//		}

		problem = problems.get(2);

		assertEquals(37, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(1635, problem.startOffset);
//			assertEquals(1701, problem.endOffset);
//		} else {
			assertEquals(1599, problem.startOffset);
			assertEquals(1665, problem.endOffset);
//		}

		problem = problems.get(3);

		assertEquals(68, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(2960, problem.startOffset);
//			assertEquals(3183, problem.endOffset);
//		} else {
			assertEquals(2893, problem.startOffset);
			assertEquals(3112, problem.endOffset);
//		}

		problem = problems.get(4);

		assertEquals(38, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(1711, problem.startOffset);
//			assertEquals(1793, problem.endOffset);
//		} else {
			assertEquals(1674, problem.startOffset);
			assertEquals(1756, problem.endOffset);
//		}

		problem = problems.get(5);

		assertEquals(50, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(2248, problem.startOffset);
//			assertEquals(2360, problem.endOffset);
//		} else {
			assertEquals(2199, problem.startOffset);
			assertEquals(2310, problem.endOffset);
//		}

		problem = problems.get(6);

		assertEquals(39, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(1803, problem.startOffset);
//			assertEquals(1880, problem.endOffset);
//		} else {
			assertEquals(1765, problem.startOffset);
			assertEquals(1842, problem.endOffset);
//		}

		problem = problems.get(7);

		assertEquals(57, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(2513, problem.startOffset);
//			assertEquals(2756, problem.endOffset);
//		} else {
			assertEquals(2457, problem.startOffset);
			assertEquals(2696, problem.endOffset);
//		}
	}
}