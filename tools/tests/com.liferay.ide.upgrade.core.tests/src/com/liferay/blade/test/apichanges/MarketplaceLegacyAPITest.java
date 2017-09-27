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

public class MarketplaceLegacyAPITest  extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 6;
	}

	@Override
	public String getImplClassName() {
		return "MarketplaceLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/MarketplaceMessageListener.java");
	}

	@Test
	public void marketplaceLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		assertNotNull(problems);
		assertEquals(6, problems.size());

		Problem problem = problems.get(0);

		assertEquals(18, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(681, problem.startOffset);
//			assertEquals(732, problem.endOffset);
//		} else {
			assertEquals(664, problem.startOffset);
			assertEquals(715, problem.endOffset);
//		}

		problem = problems.get(1);

		assertEquals(60, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(2068, problem.startOffset);
//			assertEquals(2176, problem.endOffset);
//		} else {
			assertEquals(2009, problem.startOffset);
			assertEquals(2115, problem.endOffset);
//		}

		problem = problems.get(2);

		assertEquals(87, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(2887, problem.startOffset);
//			assertEquals(2947, problem.endOffset);
//		} else {
			assertEquals(2801, problem.startOffset);
			assertEquals(2861, problem.endOffset);
//		}

		problem = problems.get(3);

		assertEquals(19, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(742, problem.startOffset);
//			assertEquals(796, problem.endOffset);
//		} else {
			assertEquals(724, problem.startOffset);
			assertEquals(778, problem.endOffset);
//		}

		problem = problems.get(4);

		assertEquals(73, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(2503, problem.startOffset);
//			assertEquals(2613, problem.endOffset);
//		}
//		else {
			assertEquals(2431, problem.startOffset);
			assertEquals(2539, problem.endOffset);
//		}

		problem = problems.get(5);

		assertEquals(82, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(2764, problem.startOffset);
//			assertEquals(2875, problem.endOffset);
//		}
//		else {
			assertEquals(2683, problem.startOffset);
			assertEquals(2792, problem.endOffset);
//		}
	}
}