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
import com.liferay.blade.test.Util;

import java.io.File;
import java.util.List;

import org.junit.Test;

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

		assertNotNull(problems);
		assertEquals(4, problems.size());

		Problem problem = problems.get(0);

		assertEquals(22, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(893, problem.startOffset);
			assertEquals(940, problem.endOffset);
		} else {
			assertEquals(872, problem.startOffset);
			assertEquals(919, problem.endOffset);
		}

		problem = problems.get(1);

		assertEquals(46, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(1420, problem.startOffset);
			assertEquals(1484, problem.endOffset);
		} else {
			assertEquals(1375, problem.startOffset);
			assertEquals(1438, problem.endOffset);
		}

		problem = problems.get(2);

		assertEquals(62, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(1747, problem.startOffset);
			assertEquals(1806, problem.endOffset);
		} else {
			assertEquals(1686, problem.startOffset);
			assertEquals(1745, problem.endOffset);
		}

		problem = problems.get(3);

		assertEquals(73, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(1971, problem.startOffset);
			assertEquals(2044, problem.endOffset);
		} else {
			assertEquals(1899, problem.startOffset);
			assertEquals(1971, problem.endOffset);
		}
	}
}