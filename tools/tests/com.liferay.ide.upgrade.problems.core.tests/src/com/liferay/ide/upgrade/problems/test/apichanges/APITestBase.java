/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.upgrade.problems.test.apichanges;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public abstract class APITestBase {

	@Before
	public void beforeTest() throws Exception {
		Filter filter = getFilter();

		ServiceTracker<FileMigrator, FileMigrator> fileMigratorTracker = new ServiceTracker<>(context, filter, null);

		fileMigratorTracker.open();

		fileMigrators = fileMigratorTracker.getServiceReferences();

		Assert.assertNotNull(fileMigrators);

		Assert.assertEquals("", 1, fileMigrators.length);
	}

	public int getExpectedNumber() {
		return 1;
	}

	protected Filter getFilter() throws Exception {
		return context.createFilter("(component.name=" + getComponentName() + ")");
	}

	public abstract String getComponentName();

	public abstract File getTestFile();

	@Test
	public void test() throws Exception {
		FileMigrator fmigrator = context.getService(fileMigrators[0]);

		List<UpgradeProblem> problems = fmigrator.analyze(getTestFile());

		context.ungetService(fileMigrators[0]);

		Assert.assertNotNull(problems);

		Assert.assertEquals("", getExpectedNumber(), problems.size());
	}

	protected BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
	protected ServiceReference<FileMigrator>[] fileMigrators;

}