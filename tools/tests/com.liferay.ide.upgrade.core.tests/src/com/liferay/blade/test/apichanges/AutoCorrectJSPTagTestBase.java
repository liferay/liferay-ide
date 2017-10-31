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
import static org.junit.Assert.assertTrue;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public abstract class AutoCorrectJSPTagTestBase {

	protected final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	private String[] _readLines(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}

		List<String> lines = new ArrayList<>();

		try (BufferedReader bufferedReader =
				new BufferedReader(new InputStreamReader(inputStream))) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringBuffer contents = new StringBuffer(line);

				lines.add(contents.toString());
			}
		}
		catch (Exception e) {
		}

		return lines.toArray(new String[lines.size()]);
	}

	@Test
	public void autoCorrectProblems() throws Exception {
		File tempFolder = Files.createTempDirectory("autocorrect").toFile();
		File testFile = new File(tempFolder, "test.jsp");

		tempFolder.deleteOnExit();

		Files.copy(getOriginalTestFile().toPath(), testFile.toPath());

		List<Problem> problems = null;
		FileMigrator migrator = null;

		Collection<ServiceReference<FileMigrator>> mrefs = context.getServiceReferences(FileMigrator.class, null);

		for (ServiceReference<FileMigrator> mref : mrefs) {
			migrator = context.getService(mref);

			if (migrator.getClass().getName().contains(getImplClassName())) {
				problems = migrator.analyze(testFile);

				break;
			}
		}

		assertEquals(getExpectedNumber(), problems.size());

		int problemsFixed = ((AutoMigrator) migrator).correctProblems(testFile, problems);

		assertEquals(getExpectedFixedNumber(), problemsFixed);

		File dest = new File(tempFolder, "Updated.jsp");

		assertTrue(testFile.renameTo(dest));

		problems = migrator.analyze(dest);

		assertEquals(0, problems.size());

		for (String checkPoint : getCheckPoints()) {
			int lineNumber = Integer.parseInt(checkPoint.split(",")[0]);
			String lineContent = checkPoint.split(",")[1];

			try {
				String[] lines = _readLines(Files.newInputStream(dest.toPath()));

				assertTrue(lines[lineNumber - 1].trim().equals(lineContent));
			}
			catch (Exception e) {
			}
		}
	}

	public abstract List<String> getCheckPoints();

	public int getExpectedFixedNumber() {
		return 1;
	}

	public int getExpectedNumber() {
		return 1;
	}

	public abstract String getImplClassName();

	public abstract File getOriginalTestFile();
}
