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

public class BackgroundTaskLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 2;
	}

	@Test
	public void testFull() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		assertNotNull(problems);
		assertEquals(2, problems.size());

		Problem problem = problems.get(0);

		assertEquals(18, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(688, problem.startOffset);
			assertEquals(736, problem.endOffset);
		}
		else {
			assertEquals(671, problem.startOffset);
			assertEquals(719, problem.endOffset);
		}

		problem = problems.get(1);

		assertEquals(19, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(746, problem.startOffset);
			assertEquals(801, problem.endOffset);
		} else {
			assertEquals(728, problem.startOffset);
			assertEquals(783, problem.endOffset);
		}
	}


	@Override
	public String getImplClassName() {
		return "BackgroundTaskLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/DLFileNameBackgroundTaskServiceImpl.java");
	}

}
