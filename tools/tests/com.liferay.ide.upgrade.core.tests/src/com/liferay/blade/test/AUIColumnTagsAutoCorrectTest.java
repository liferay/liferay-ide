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

package com.liferay.blade.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class AUIColumnTagsAutoCorrectTest {

	@Test
	public void autoCorrectProblems() throws Exception {
		File tempFolder = Files.createTempDirectory("autocorrect").toFile();
		File testFile = new File(tempFolder, "AUIColumnTagTest.jsp");

		tempFolder.deleteOnExit();

		File originalTestfile = new File("jsptests/aui-column/AUIColumnTagTest.jsp");

		Files.copy(originalTestfile.toPath(), testFile.toPath());

		List<Problem> problems = null;
		FileMigrator migrator = null;

		Collection<ServiceReference<FileMigrator>> mrefs = context.getServiceReferences(FileMigrator.class, null);

		for (ServiceReference<FileMigrator> mref : mrefs) {
			migrator = context.getService(mref);

			if (migrator.getClass().getName().contains("AUIColumnTags")) {
				problems = migrator.analyze(testFile);
				break;
			}
		}

		assertEquals(1, problems.size());

		int problemsFixed = ((AutoMigrator) migrator).correctProblems(testFile, problems);

		assertEquals(1, problemsFixed);

		File dest = new File(tempFolder, "Updated.jsp");

		assertTrue(testFile.renameTo(dest));

		problems = migrator.analyze(dest);

		assertEquals(0, problems.size());
	}

	private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
}
