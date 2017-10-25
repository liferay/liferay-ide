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

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.util.NullProgressMonitor;

public class JSPTagImportsTest {

	@Test
	public void allProblems() throws Exception {
		ServiceReference<Migration> sr = context.getServiceReference(Migration.class);

		Migration migration = context.getService(sr);

		File tempFolder = Files.createTempDirectory("autocorrect").toFile();
		File testFile = new File(tempFolder, "jsptaglist.jsp");

		tempFolder.deleteOnExit();

		File originalTestfile = new File("jsptests/imports/view.jsp");

		Files.copy( originalTestfile.toPath(), testFile.toPath());

		List<Problem> problems = migration.findProblems(testFile, new NullProgressMonitor());

		assertEquals(6, problems.size());

		Collection<ServiceReference<AutoMigrator>> refs =
			context.getServiceReferences(AutoMigrator.class, "(auto.correct=import)");

		for (ServiceReference<AutoMigrator> ref : refs) {
			AutoMigrator autoMigrator = context.getService(ref);

			autoMigrator.correctProblems(testFile, problems);
		}

		File dest = new File(tempFolder, "Updated.jsp");

		assertTrue(testFile.renameTo(dest));

		List<Problem> problems2 = migration.findProblems(dest, new NullProgressMonitor());

		assertEquals(0, problems2.size());

	}

	private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

}
