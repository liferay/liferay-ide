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

package com.liferay.blade.test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigration;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class TrashEmptyTagsTest {

	@Test
	public void findUpgradeProblems() throws Exception {
		ServiceReference<FileMigration> sr = _context.getServiceReference(FileMigration.class);

		FileMigration m = _context.getService(sr);

		List<String> versions = Arrays.asList("7.0", "7.1", "7.2");

		List<UpgradeProblem> problems = m.findUpgradeProblems(
			new File("jsptests/liferay-ui-trash-empty/"), versions, new NullProgressMonitor());

		Assert.assertEquals("", 3, problems.size());
	}

	private final BundleContext _context = FrameworkUtil.getBundle(getClass()).getBundleContext();

}