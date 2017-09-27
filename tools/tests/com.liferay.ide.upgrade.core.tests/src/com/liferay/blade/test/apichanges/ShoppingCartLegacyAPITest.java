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

public class ShoppingCartLegacyAPITest  extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 4;
	}

	@Override
	public String getImplClassName() {
		return "ShoppingCartLegacyAPI";
	}

	@Override
	public File getTestFile() {
		return new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/CartAction.java");
	}

	@Test
	public void shoppingCartLegacyAPITest() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		assertNotNull(problems);
		assertEquals(4, problems.size());

		Problem problem = problems.get(0);

		assertEquals(32, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(1475, problem.startOffset);
//			assertEquals(1540, problem.endOffset);
//		} else {
			assertEquals(1444, problem.startOffset);
			assertEquals(1509, problem.endOffset);
//		}

		problem = problems.get(1);

		assertEquals(143, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(4691, problem.startOffset);
//			assertEquals(4858, problem.endOffset);
//		} else {
			assertEquals(4549, problem.startOffset);
			assertEquals(4714, problem.endOffset);
//		}

		problem = problems.get(2);

		assertEquals(33, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(1550, problem.startOffset);
//			assertEquals(1615, problem.endOffset);
//		} else {
			assertEquals(1518, problem.startOffset);
			assertEquals(1583, problem.endOffset);
//		}

		problem = problems.get(3);

		assertEquals(118, problem.lineNumber);

//		if (Util.isWindows()) {
//			assertEquals(3987, problem.startOffset);
//			assertEquals(4031, problem.endOffset);
//		} else {
			assertEquals(3870, problem.startOffset);
			assertEquals(3914, problem.endOffset);
//		}
	}

}
