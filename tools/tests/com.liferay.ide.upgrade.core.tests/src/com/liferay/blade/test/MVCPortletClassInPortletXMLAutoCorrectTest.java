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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Gregory Amerson
 */
public class MVCPortletClassInPortletXMLAutoCorrectTest {

	final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
	private AutoMigrator autoMigrator = null;

	@Before
	public void beforeTest() throws Exception {
		Filter filter = context.createFilter("(implName=MVCPortletClassInPortletXML)");

		ServiceTracker<AutoMigrator, AutoMigrator> tracker = new ServiceTracker<AutoMigrator, AutoMigrator>(context, filter, null);

		tracker.open();

		ServiceReference<AutoMigrator>[] refs = tracker.getServiceReferences();

		assertNotNull(refs);

		assertEquals(1, refs.length);

		autoMigrator = context.getService(refs[0]);
	}

	@Test
	public void testAutoCorrectPortletXmlBoth() throws Exception {
		assertNotNull(autoMigrator);

		FileMigrator fileMigrator = (FileMigrator) autoMigrator;

		File testfile = new File("target/test/MVCPortletClassInPortletXMLAutoCorrectTest/portlet.xml");

		if (testfile.exists()) {
			assertTrue(testfile.delete());
		}

		testfile.getParentFile().mkdirs();

		Files.copy(new File("projects/test-portlet/docroot/WEB-INF/portlet.xml").toPath(), new FileOutputStream(testfile));


		List<Problem> problems = fileMigrator.analyze(testfile);

		assertEquals(2, problems.size());

		int corrected = autoMigrator.correctProblems(testfile, problems);

		assertEquals(2, corrected);

		problems = fileMigrator.analyze(testfile);

		assertEquals(0, problems.size());
	}

	@Test
	public void testAutoCorrectPortletXmlSingle() throws Exception {
		assertNotNull(autoMigrator);

		FileMigrator fileMigrator = (FileMigrator) autoMigrator;

		File testfile = new File("generated/test/MVCPortletClassInPortletXMLAutoCorrectTest/portlet.xml");

		if (testfile.exists()) {
			assertTrue(testfile.delete());
		}

		testfile.getParentFile().mkdirs();

		Files.copy(new File("projects/test-portlet/docroot/WEB-INF/portlet.xml").toPath(), new FileOutputStream(testfile));

		List<Problem> problems = fileMigrator.analyze(testfile);

		assertEquals(2, problems.size());

		int corrected = autoMigrator.correctProblems(testfile, Collections.singletonList(problems.get(0)));

		assertEquals(1, corrected);

		problems = fileMigrator.analyze(testfile);

		assertEquals(1, problems.size());

		corrected = autoMigrator.correctProblems(testfile, Collections.singletonList(problems.get(0)));

		assertEquals(1, corrected);

		problems = fileMigrator.analyze(testfile);

		assertEquals(0, problems.size());
	}
}
