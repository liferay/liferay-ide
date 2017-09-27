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

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.util.NullProgressMonitor;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class AllProblemsTest {

	@Test
	@Ignore
	public void allProblems() throws Exception {
		ServiceReference<Migration> sr = context
			.getServiceReference(Migration.class);
		Migration m = context.getService(sr);
		List<Problem> problems = m
				.findProblems(new File(
						"../com.liferay.blade.upgrade.liferay70/projects/"), new NullProgressMonitor());

		final int expectedSize = 304;
		final int size = problems.size();

		if (size != expectedSize) {
			System.err.println("All problems size is " + size + ", expected size is " + expectedSize);
		}

		assertEquals(expectedSize, size);

//		List<String> lines = new ArrayList<>();
//		String contents = new String(IO.read(this.getClass().getResourceAsStream("BREAKING_CHANGES.tags")));
//		BufferedReader reader = new BufferedReader(IO.reader(contents));
//
//		String read = reader.readLine();
//		while(read != null) {
//			lines.add(read);
//			read = reader.readLine();
//		}

//		Collection<ServiceReference<FileMigrator>> refs = context.getServiceReferences(FileMigrator.class, null);

		for (Problem problem : problems) {
			if (problem.html != null && problem.html.equals("#legacy")) continue;

//			if (problem.html == null || problem.html.equals("")) {
//				for (ServiceReference<FileMigrator> ref : refs) {
//					String section = (String) ref.getProperty("problem.section");
//					String title = (String) ref.getProperty("problem.title");
//					if (title.equals(problem.title)) {
//						for (String line : lines) {
//							if (section.startsWith(line)) {
//								fail("line=" + line + ", section=" + section);
//							}
//						}
//					}
//				}
//			}


			assertNotNull("problem.title=" + problem.title + ", problem.file=" + problem.file, problem.html);
			assertTrue("problem.title=" + problem.title + ", problem.file=" + problem.file, problem.html.length() > 0);
		}
	}

	private final BundleContext context = FrameworkUtil.getBundle(
		this.getClass()).getBundleContext();

}
