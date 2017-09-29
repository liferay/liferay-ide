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

public class DDLRecordLegacyAPITest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 3;
	}

	@Test
	public void dDLRecordLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		assertNotNull(problems);
		assertEquals(3, problems.size());

		Problem problem = problems.get(0);

		assertEquals(30, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(1361, problem.startOffset);
			assertEquals(1426, problem.endOffset);
		}
		else {
			assertEquals(1332, problem.startOffset);
			assertEquals(1397, problem.endOffset);
		}

		problem = problems.get(1);

		assertEquals(132, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(4220, problem.startOffset);
			assertEquals(4263, problem.endOffset);
		}
		else {
			assertEquals(4089, problem.startOffset);
			assertEquals(4132, problem.endOffset);
		}

		problem = problems.get(2);

		assertEquals(145, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(4619, problem.startOffset);
			assertEquals(4699, problem.endOffset);
		}
		else {
			assertEquals(4475, problem.startOffset);
			assertEquals(4554, problem.endOffset);
		}
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
