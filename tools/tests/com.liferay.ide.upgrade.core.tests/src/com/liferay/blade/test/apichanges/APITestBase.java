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

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public abstract class APITestBase {

    protected final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

    protected ServiceReference<FileMigrator>[] fileMigrators;

	@Before
	public void beforeTest() throws Exception {
		Filter filter = context.createFilter("(implName=" + getImplClassName() + ")");

		ServiceTracker<FileMigrator, FileMigrator> fileMigratorTracker = new ServiceTracker<FileMigrator, FileMigrator>(context, filter, null);

		fileMigratorTracker.open();

		fileMigrators = fileMigratorTracker.getServiceReferences();

		assertNotNull(fileMigrators);

		assertEquals(1, fileMigrators.length);
	}

	public abstract String getImplClassName();

	public abstract File getTestFile();

	public int getExpectedNumber() {
		return 1;
	}

	@Test
	public void test() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		assertNotNull(problems);

		assertEquals(getExpectedNumber(), problems.size());
	}

	@Test
	public void test2() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<Problem> problems = fmigrator.analyze(getTestFile());

		problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		assertNotNull(problems);
		assertEquals(getExpectedNumber(), problems.size());
	}

}
